package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.WarnMail;

public interface WarnMailMapper {
    int deleteByPrimaryKey(Integer mailId);

    int insert(WarnMail record);

    int insertSelective(WarnMail record);

    WarnMail selectByPrimaryKey(Integer mailId);

    int updateByPrimaryKeySelective(WarnMail record);

    int updateByPrimaryKey(WarnMail record);
}