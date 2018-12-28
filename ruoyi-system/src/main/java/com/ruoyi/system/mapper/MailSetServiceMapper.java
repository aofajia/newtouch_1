package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.MailSetService;

import java.util.List;

public interface MailSetServiceMapper {
    int deleteByPrimaryKey(Integer serviceId);

    int insert(MailSetService record);

    int insertSelective(MailSetService record);

    MailSetService selectByPrimaryKey(Integer serviceId);

    int updateByPrimaryKeySelective(MailSetService record);

    int updateByPrimaryKey(MailSetService record);

    int selectByMailId(Integer serviceId);

    List<MailSetService> find();

    int selectAll();
}