package com.ruoyi.ruoyimailservice.web.service;

import com.ruoyi.system.domain.MailSetService;
import org.springframework.stereotype.Repository;


/**
 * @author aofajia
 * @create 2018/12/19
 * @since 1.0.0
 */
@Repository
public interface IWarmMailAccountService {

    public int add(MailSetService mailSetService);

    int selectAll();

    int checkNum(Integer serviceId);

    int update(MailSetService mailSetService);

    MailSetService find();
}

