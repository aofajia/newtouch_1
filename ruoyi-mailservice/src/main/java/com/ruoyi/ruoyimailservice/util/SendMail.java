package com.ruoyi.ruoyimailservice.util;

import org.springframework.boot.web.servlet.server.Session;
import org.springframework.stereotype.Component;


import java.math.BigDecimal;
import java.net.PasswordAuthentication;
import java.util.Map;
import java.util.Properties;

import javax.mail.*;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * 余额预警-发送邮件
 *
 * @author aofajia
 * @create 2018/12/18
 * @since 1.0.0
 */
@Component
public class SendMail {

    public static  void main(String[] args){
        //send("欧飞");
        System.out.println("邮件发送成功了！");
    }

    /*public  void send(Map<String, Object> map){

        String supplier=(String)map.get("supplier");
        BigDecimal balancemoney=(BigDecimal)map.get("balancemoney");
        BigDecimal warningMoney=(BigDecimal)map.get("warningMoney");
        String content="商城负责人你好,供应商"+supplier+"预存款额度"+balancemoney+"元," +
                "已经低于预警额度"+warningMoney+",请立即处理！";

        Properties props= null;
        try {
            props = new Properties();
            props.put("username","aofajia@163.com");
            props.put("password","fgtb1991118");
            props.put("mail.transport.protocol","smtp");
            props.put("mail.smtp.host","smtp.163.com");
            props.put("mail.smtp.port","25");
            props.setProperty("mail.smtp.auth", "true");// 指定验证为true
            Authenticator auth = new Authenticator(){
                @Override
                public PasswordAuthentication getPasswordAuthentication() {
                    //设置发送人的帐号和密码
                    return new PasswordAuthentication("aofajia", "fgtb1991118");
                }
            };


            Session session = Session.getInstance(props, auth);

           // Session session=Session.getDefaultInstance(props);

            Message msg=new MimeMessage(session);
            msg.setFrom(new InternetAddress(props.getProperty("username")));
           // msg.addRecipients(Message.RecipientType.TO,InternetAddress.parse(to));
            msg.addRecipients(Message.RecipientType.CC,InternetAddress.parse(props.getProperty("username")));
            msg.setSubject("预警邮件");
            msg.setContent(content, "text/html;charset=utf-8");
            msg.saveChanges();

            *//*Transport transport=session.getTransport("smtp");
            transport.connect( props.getProperty("mail.smtp.host"),
                    props.getProperty("username"),props.getProperty("password") );*//*

            Transport.send(msg);

        }catch(MessagingException e) {
            e.printStackTrace();
        }catch(AddressException e){
            e.printStackTrace();
        }



    }*/

}

