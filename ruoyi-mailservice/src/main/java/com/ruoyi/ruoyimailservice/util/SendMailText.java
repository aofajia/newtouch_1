package com.ruoyi.ruoyimailservice.util;


import com.ruoyi.ruoyimailservice.web.service.IWarmMailAccountService;
import com.ruoyi.system.domain.MailSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.rmi.transport.ObjectTable;

import java.io.ObjectInputStream.GetField;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.Map;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.text.html.MinimalHTMLWriter;

@Component
public class SendMailText {

    @Autowired
    private IWarmMailAccountService iWarmMailAccountService;

    public void scheduleTaskSendMail(Map map){
        HashMap<String, Object> sendMap = new HashMap<String, Object>();
        /*sendMap.put("supplier",map.get("supplier"));
        sendMap.put("balancemoney",map.get("balancemoney"));
        sendMap.put("warningMoney",map.get("warningMoney"));
        sendMap.put("isTrigger",map.get("isTrigger"));*/

        MailSetService mailSetService = iWarmMailAccountService.find();
        sendMap.put("protocal",mailSetService.getServiceProtocl());
        sendMap.put("serviceHost",mailSetService.getServiceHost());
        sendMap.put("servicePort",mailSetService.getServicePort());
        sendMap.put("fromMail",mailSetService.getFromMail());
        sendMap.put("fromAccount",mailSetService.getFromAccount());
        sendMap.put("authPassword",mailSetService.getAuthPassword());
        sendMap.put("toMail",mailSetService.getToMail());

        send(sendMap,map);
    }

    public  void send(Map sendMap,Map warnMap) {
        //1、连接邮件服务器的参数配置
        Properties props = new Properties();
        //设置用户的认证方式
       // props.setProperty("mail.smtp.auth", "true");
        props.put("mail.smtp.auth", "true");// 指定验证为true
        //设置传输协议
        props.put("mail.transport.protocol",sendMap.get("protocal"));
        //设置发件人的SMTP服务器地址
        //props.setProperty("mail.smtp.host", "smtp.qq.com");
        props.put("mail.smtp.host",sendMap.get("serviceHost"));

        props.put("mail.smtp.port",sendMap.get("servicePort"));

        try{

            //2、创建定义整个应用程序所需的环境信息的 Session 对象
            Session session = Session.getInstance(props);
            //设置调试信息在控制台打印出来
            session.setDebug(true);
            //3、创建邮件的实例对象
            Message msg = getMimeMessage(session,createContext(warnMap),(String)sendMap.get("fromMail"),(String)sendMap.get("toMail"));
            //4、根据session对象获取邮件传输对象Transport
            Transport transport = session.getTransport();
            //设置发件人的账户名和密码
            transport.connect((String)sendMap.get("fromAccount"),(String)sendMap.get("authPassword"));
            //发送邮件，并发送到所有收件人地址，message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
            transport.sendMessage(msg,msg.getAllRecipients());

            //如果只想发送给指定的人，可以如下写法
            //transport.sendMessage(msg, new Address[]{new InternetAddress("xxx@qq.com")});

            //5、关闭邮件连接
            transport.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *
     */
    public String createContext(Map<String, Object> map){
        String supplier=(String)map.get("supplier");
        BigDecimal balancemoney=(BigDecimal)map.get("balancemoney");
        BigDecimal warningMoney=(BigDecimal)map.get("warningMoney");
        String content="商城负责人你好,供应商"+supplier+"预存款额度"+balancemoney+"元," +
                "已经低于预警额度"+warningMoney+",请立即处理！";
        return content;
    }


    /**
     * 获得创建一封邮件的实例对象
     * @param session 授权session
     * @param content 邮件内容
     * @param senderAddress 发件人邮箱
     * @param recipientAddress 收件人邮箱
     * @return
     * @throws Exception
     */
    public static MimeMessage getMimeMessage(Session session,String content,String senderAddress,String recipientAddress) throws Exception{
        //创建一封邮件的实例对象
        MimeMessage msg = new MimeMessage(session);
        //设置发件人地址
        msg.setFrom(new InternetAddress(senderAddress));
        /**
         * 设置收件人地址（可以增加多个收件人、抄送、密送），即下面这一行代码书写多行
         * MimeMessage.RecipientType.TO:发送
         * MimeMessage.RecipientType.CC：抄送
         * MimeMessage.RecipientType.BCC：密送
         */
        msg.setRecipient(MimeMessage.RecipientType.TO,new InternetAddress(recipientAddress));
        //设置邮件主题
        msg.setSubject("预警邮件","UTF-8");
        //设置邮件正文
        msg.setContent(content, "text/html;charset=UTF-8");
        //设置邮件的发送时间,默认立即发送
        msg.setSentDate(new Date());

        return msg;
    }

}

