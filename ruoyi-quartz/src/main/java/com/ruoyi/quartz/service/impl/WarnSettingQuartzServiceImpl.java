package com.ruoyi.quartz.service.impl;


import com.ruoyi.common.constant.InterfaceUrlConstants;
import com.ruoyi.common.enums.BwSupplierId;
import com.ruoyi.common.json.JSONObject;
import com.ruoyi.common.utils.Md5Utils;
import com.ruoyi.quartz.domain.BwConfigQuartz;
import com.ruoyi.quartz.mapper.WarnQuartzMapper;
import com.ruoyi.quartz.service.IWarnSettingQuartzService;
import com.ruoyi.system.tool.XMLUtil;
import com.ruoyi.system.utils.NumberArithmeticUtils;
import com.ruoyi.system.utils.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @author aofajia
 * @create 2018/12/13
 * @since 1.0.0
 */
@Service
public class WarnSettingQuartzServiceImpl implements IWarnSettingQuartzService {

    @Autowired
    private WarnQuartzMapper warnQuartzMapper;


    @Override
    public Boolean isSettingSupplier() {
        boolean isTrue = false;
        int result = warnQuartzMapper.findCount();
        if (result != 0) {
            isTrue = true;
        }
        return isTrue;
    }

    @Override
    public List<BwConfigQuartz> findAllSupplierInfo() {
        List<BwConfigQuartz> list = warnQuartzMapper.findAllSupplier();
        return list;
    }

    @Override
    public List<Map<String, String>> findBalancemoneyBySupplierid() {
        List<BwConfigQuartz> list = findAllSupplierInfo();
        List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
        for (BwConfigQuartz entity : list) {
            String supplierid = entity.getSupplierid();
            BigDecimal money = new BigDecimal(0.0);
            BigDecimal warningMoney = entity.getWarningmoney();
            int count = warnQuartzMapper.countMoney(supplierid);
            if (count != 0) {
                List<BigDecimal> moneyList = warnQuartzMapper.findMoney(supplierid);
                money = moneyList.get(0);
            }

            Map resultMap = new HashMap<String, String>();
            resultMap.put("supplierid", supplierid);
            resultMap.put("money", money);
            resultMap.put("warningMoney", warningMoney);
            resultList.add(resultMap);
        }
        return resultList;
    }

    @Override
    public boolean isTriggerWarn() {

        return false;
    }

    @Override
    public BigDecimal getMoneyByBwRecord(String supplierid) {

        BigDecimal balanceMoney = new BigDecimal(0.0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String startTime = sdf.format(new Date());

        //京东超市
        if (BwSupplierId.JD.getSupplierid().equals(supplierid)) {
            return new BigDecimal(0.0);
        }
        //欧飞(话费、油卡)
        if (BwSupplierId.OUFEI_OILCARD.getSupplierid().equals(supplierid) ||
                BwSupplierId.OUFEI_PHONE.getSupplierid().equals(supplierid)) {
            String url = InterfaceUrlConstants.OUIFI_URL;
            Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
            StringBuffer stringBuffer = new StringBuffer();
            long time = System.currentTimeMillis();
            org.json.JSONObject jo = new org.json.JSONObject();
            jo.put("appId","newtouchmall");
            stringBuffer.append(time);
            stringBuffer.append(jo.toString());
            stringBuffer.append("ac063f15ccff416b9a2278318920926f");
            String md5 = Md5Utils.string2MD5(stringBuffer.toString());
            paramMap.put("appId", "newtouchmall");
            paramMap.put("timestamp", time);
            paramMap.put("sign", md5.toUpperCase());
            try {
                String xml = NumberArithmeticUtils.sendPost(url, paramMap, "utf-8", "application/json", jo.toString());
                Request req = (Request) XMLUtil.convertXmlStrToObject(Request.class, xml);
                if ("1".equals(req.getRetcode())) {
                    String totalBalance = req.getTotalBalance();
                    balanceMoney = new BigDecimal(totalBalance);

                } else {
                    //syn_storedata_logger.info("供应商："+shop_name+"获取余额失败，错误描述："+req.getErr_msg());
                }
            } catch (IOException e) {
                //syn_storedata_logger.info("供应商："+shop_name+"获取余额失败，报错原因："+e.toString());
                e.printStackTrace();
            }

        }

        //饿了么
        if (BwSupplierId.ELM.getSupplierid().equals(supplierid)) {
            return new BigDecimal(0.0);
        }
        //携程
        if (BwSupplierId.CTRIP.getSupplierid().equals(supplierid)) {
            return new BigDecimal(0.0);
        }

        return balanceMoney;
    }
}

