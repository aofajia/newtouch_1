package com.ruoyi.ruoyimailservice.web.controller;

import com.ruoyi.ruoyimailservice.util.SendMailText;
import com.ruoyi.system.domain.MailSetService;
import com.ruoyi.ruoyimailservice.web.service.IWarmMailAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * 邮箱服务设置
 *
 * @author aofajia
 * @create 2018/12/19
 * @since 1.0.0
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequestMapping("/fi/mail")
public class MailServiceSettingController {


    //配置前缀地址
    private static String PREFIX="system/mail";
    //判断值
    private static int NUM=0;

    Logger log=LoggerFactory.getLogger(MailServiceSettingController.class);

    @Autowired
    private IWarmMailAccountService IWarmMailAccountService;

    @Autowired
    private SendMailText sendMailText;

    @RequestMapping("/main")
    public String getMain(){
        return PREFIX+"/mailServiceSetting";
    }

    @RequestMapping("/add")
    @Transactional(rollbackFor = Exception.class)
    @ResponseBody
    public int add(MailSetService mailSetService){
        int i=NUM;
        int count=IWarmMailAccountService.selectAll();
        if(count==NUM){
            i=IWarmMailAccountService.add(mailSetService);
            log.info(i+":insert success!");
        }
        return i;
    }

    @RequestMapping("/update")
    @Transactional(rollbackFor = Exception.class)
    @ResponseBody
    public int update(MailSetService mailSetService){
        int i=NUM;
        Integer serviceId=mailSetService.getServiceId();
        int count= IWarmMailAccountService.checkNum(serviceId);
        if(count!=NUM){
             i=IWarmMailAccountService.update(mailSetService);
             log.info(i+":update success!");
        }
        return i;
    }

    @RequestMapping("/init")
    @Transactional
    @ResponseBody
    public MailSetService findDatas(){
        MailSetService mailSetService = IWarmMailAccountService.find();
        return  mailSetService;
    }

    @RequestMapping("/testToMail")
    @Transactional(rollbackFor = Exception.class)
    @ResponseBody
    public void testToMail(MailSetService mailSetService){
        int i = IWarmMailAccountService.selectAll();
        if(i==NUM){
            int addNum = IWarmMailAccountService.add(mailSetService);
        }else{
            int updateNum = IWarmMailAccountService.update(mailSetService);
        }
        Map<String,Object> map=new HashMap<String,Object>();
        //邮箱协议
        map.put("protocal",mailSetService.getServiceProtocl());
        //服务主机
        map.put("serviceHost",mailSetService.getServiceHost());
        //服务端口
        map.put("servicePort",mailSetService.getServicePort());
        //发件人邮箱
        map.put("fromMail",mailSetService.getFromMail());
        //发件人账户
        map.put("fromAccount",mailSetService.getFromAccount());
        //授权密码
        map.put("authPassword",mailSetService.getAuthPassword());
        //收件箱
        map.put("toMail",mailSetService.getToMail());

        //测试邮件-供应商为欧飞
        HashMap<String, Object> warnMap = new HashMap<String, Object>();
        warnMap.put("supplier","103");
        warnMap.put("balancemoney",0);
        warnMap.put("warningMoney",12000);

        sendMailText.send(map,warnMap);
    }

}

