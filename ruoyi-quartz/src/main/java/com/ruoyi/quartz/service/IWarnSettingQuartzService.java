package com.ruoyi.quartz.service;

import com.ruoyi.quartz.domain.BwConfigQuartz;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 定时任务-余额设置业务层
 *
 * @author aofajia
 * @create 2018/12/13
 * @since 1.0.0
 */

public interface IWarnSettingQuartzService {


    /**
     * 1.判断是否初始化供应商配置
     *
     * @return
     */
    public Boolean isSettingSupplier();

    /**
     * 2.拿到sdb_business_storemanger中所有供应商的信息，包括supplierid和预警额度
     *
     * @return
     */
    public List<BwConfigQuartz> findAllSupplierInfo();


    /**
     * 3.通过供应商supplierid可以去表hrfi_balancerecord查出所有供应商最新的预存款额度
     *
     * @return
     */
    public List<Map<String,String>> findBalancemoneyBySupplierid();


    /**
     * 4.判断供应商预存款是否到达用户设置预警额度,达到就触发预警
     *
     * @return
     */
    public boolean isTriggerWarn();

    /**
     * 获取各个供应商实时预存款额度
     */
    public BigDecimal getMoneyByBwRecord(String supplierid);
}

