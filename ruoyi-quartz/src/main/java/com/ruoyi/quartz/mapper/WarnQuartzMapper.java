package com.ruoyi.quartz.mapper;

import com.ruoyi.quartz.domain.BwConfigQuartz;

import java.math.BigDecimal;
import java.util.List;

/**
 * 定时任务-余额预警 数据层
 *
 * @author aofajia
 * @create 2018/12/14
 * @since 1.0.0
 */
public interface WarnQuartzMapper {

    /**
     * 判断是否初始化供应商配置
     * @return
     */
    int findCount();

    /**
     * 查出所有供应商信息
     * @return
     */
    List<BwConfigQuartz> findAllSupplier();

    /**
     * 根据supplierid判断供应商预存款数量
     * @param supplierid
     * @return
     */
    int countMoney(String supplierid);

    /**
     * 跟据supplierid查出供应商预存款
     * @param supplierid
     * @return
     */
    List<BigDecimal> findMoney(String supplierid);

}

