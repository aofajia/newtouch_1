package com.ruoyi.system.domain;

/**
 * 开票信息汇总
 */
public class OpenTicketInfoCollect {

    //订单编号
    Long ordersId;

    //法人体ID
    String company_id;

    //开票抬头
    String open_ticket_rise;

    //订单数量
    Integer order_num;

    //金额数量
    Double money_num;

    //比列
    String account;

    //供应商
    String supplier;

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public Long getOrdersId() {
        return ordersId;
    }

    public void setOrdersId(Long ordersId) {
        this.ordersId = ordersId;
    }


    public Double getMoney_num() {
        return money_num;
    }

    public void setMoney_num(Double money_num) {
        this.money_num = money_num;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getOpen_ticket_rise() {
        return open_ticket_rise;
    }

    public void setOpen_ticket_rise(String open_ticket_rise) {
        this.open_ticket_rise = open_ticket_rise;
    }

    public Integer getOrder_num() {
        return order_num;
    }

    public void setOrder_num(Integer order_num) {
        this.order_num = order_num;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
