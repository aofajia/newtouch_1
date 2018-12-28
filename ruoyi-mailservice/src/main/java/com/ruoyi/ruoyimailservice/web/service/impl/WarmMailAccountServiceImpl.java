package com.ruoyi.ruoyimailservice.web.service.impl;

import com.ruoyi.ruoyimailservice.web.service.IWarmMailAccountService;
import com.ruoyi.system.domain.MailSetService;
import com.ruoyi.system.mapper.MailSetServiceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author aofajia
 * @create 2018/12/19
 * @since 1.0.0
 */
@Service
public class WarmMailAccountServiceImpl implements IWarmMailAccountService {

   @Autowired
   private MailSetServiceMapper mailSetServiceMapper;

    @Override
    public int add(MailSetService mailSetService){
        int i=mailSetServiceMapper.insert(mailSetService);
        return i;
    }

    @Override
    public int selectAll() {
        int i = mailSetServiceMapper.selectAll();
        return i;
    }


    @Override
    public int checkNum(Integer serviceId) {
        int count = mailSetServiceMapper.selectByMailId(serviceId);
        return count;
    }

    @Override
    public int update(MailSetService mailSetService) {
        int i = mailSetServiceMapper.updateByPrimaryKeySelective(mailSetService);
        return i;
    }

    @Override
    public MailSetService find() {
        List<MailSetService> mailSetServices = mailSetServiceMapper.find();
        MailSetService mailSetService=null;
        if(mailSetServices.size()!=0){
             mailSetService = mailSetServices.get(0);
        }
        return  mailSetService;
    }


}

