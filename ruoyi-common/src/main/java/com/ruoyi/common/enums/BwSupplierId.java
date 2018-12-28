package com.ruoyi.common.enums;

/**
 * 供应商Id
 */
public enum BwSupplierId {
    ELM("饿了么", "105"),
    CTRIP("携程", "106"),
    OUFEI_OILCARD("欧飞油卡充值", "103"),
    OUFEI_PHONE("欧飞话费充值", "104"),
    JD("京东", "102");

    BwSupplierId(String shopname, String supplierid) {
        this.shopname = shopname;
        this.supplierid = supplierid;
    }

    private final String shopname;

    private final String supplierid;


    public String getShopname() {
        return shopname;
    }


    public String getSupplierid() {
        return supplierid;
    }


}
