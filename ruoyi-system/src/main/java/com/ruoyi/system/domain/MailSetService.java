package com.ruoyi.system.domain;

import java.util.Date;

public class MailSetService {
    private Integer serviceId;

    private String serviceProtocl;

    private String serviceHost;

    private String servicePort;

    private String fromAccount;

    private String fromMail;

    private String authPassword;

    private Date creatTime;

    private String bz;

    private String toMail;

    public String getToMail(){
        return toMail;
    }

    public void setToMail(String toMail){
        this.toMail=toMail;
    }
    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceProtocl() {
        return serviceProtocl;
    }

    public void setServiceProtocl(String serviceProtocl) {
        this.serviceProtocl = serviceProtocl == null ? null : serviceProtocl.trim();
    }

    public String getServiceHost() {
        return serviceHost;
    }

    public void setServiceHost(String serviceHost) {
        this.serviceHost = serviceHost == null ? null : serviceHost.trim();
    }

    public String getServicePort() {
        return servicePort;
    }

    public void setServicePort(String servicePort) {
        this.servicePort = servicePort == null ? null : servicePort.trim();
    }

    public String getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(String fromAccount) {
        this.fromAccount = fromAccount == null ? null : fromAccount.trim();
    }

    public String getFromMail() {
        return fromMail;
    }

    public void setFromMail(String fromMail) {
        this.fromMail = fromMail == null ? null : fromMail.trim();
    }

    public String getAuthPassword() {
        return authPassword;
    }

    public void setAuthPassword(String authPassword) {
        this.authPassword = authPassword == null ? null : authPassword.trim();
    }

    public Date getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(Date creatTime) {
        this.creatTime = creatTime;
    }

    public String getBz() {
        return bz;
    }

    public void setBz(String bz) {
        this.bz = bz == null ? null : bz.trim();
    }
}