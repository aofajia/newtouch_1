package com.ruoyi.system.service;

import com.ruoyi.common.base.AjaxResult;
import com.ruoyi.system.domain.Employee;
import com.ruoyi.system.domain.EmployeeExample;
import com.ruoyi.system.domain.OpenTicketInfoCollect;

import java.util.List;

public interface OpenTicketService {

    /**
     *  开票信息汇总
     */
    public List<OpenTicketInfoCollect> list(String supplier,String startDate,String endDate);


    /**
     *  订单信息汇总
     */
    public List<OpenTicketInfoCollect> orderList(String id,String supplier,String startDate,String endDate);


    /**
     *  查询开票抬头
     */
    public List<OpenTicketInfoCollect> selectInvoice();


    /**
     * 数据同步
     */
    public Integer tableWith();


    /**
     *  调整开票抬头
     */
    public AjaxResult changeTicketRise(List<OpenTicketInfoCollect> list,String rise);


    /**
     *  供应商开票同步数据
     */
    public AjaxResult getOpenCardInfo();


    /**
     *  供应商分页数据显示
     */
    public List<EmployeeExample> openCardDataList();


    /**
     *  饿了么开卡
     */
    public AjaxResult openCard(List<EmployeeExample> list);


    /**
     *  开卡查询
     */
    public List<EmployeeExample> openCardDataListById(String id);


    /**
     *  饿了么删除
     */
    public AjaxResult deleteByELM(List<EmployeeExample> list);


    /**
     *  获取新员工定时任务
     */
    public AjaxResult getNewEmployeeTask();


    /**
     *  携程开卡
     */
    public AjaxResult openCardByXC(List<EmployeeExample> examples);


    /**
     *  导入数据
     */
    public AjaxResult exportData(String address);
}
