package com.ruoyi.quartz.util;

import com.ruoyi.common.enums.BwSupplierId;
import com.ruoyi.quartz.domain.BwConfigQuartz;
import com.ruoyi.quartz.service.IWarnSettingQuartzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 定时任务-余额预警
 *
 * @author aofajia
 * @create 2018/12/14
 * @since 1.0.0
 */
@Component
public class WarningQuartzUtil {


    @Autowired
    private IWarnSettingQuartzService iWarnSettingQuartzService;


    public List<Map<String,Object>> assemble() {
        List<BwConfigQuartz> list=iWarnSettingQuartzService.findAllSupplierInfo();
        List<Map<String,Object>> resultList=new ArrayList<Map<String,Object>>();
        BigDecimal balancemoney=new BigDecimal(0.0);
        for (BwConfigQuartz entity : list) {
            String supplierid = entity.getSupplierid();
            //预警额度
            BigDecimal warningMoney = entity.getWarningmoney();
            //供应商预存款额度
            balancemoney=iWarnSettingQuartzService.getMoneyByBwRecord(supplierid);

            Map resultMap=new HashMap<String,Object>();
            resultMap.put("supplierid",supplierid);
            resultMap.put("supplier",entity.getConfigname());
            resultMap.put("balancemoney",balancemoney);
            resultMap.put("warningMoney",warningMoney);

            resultList.add(resultMap);
        }
        return resultList;

    }

    public List<Map<String,Object>> isTriggerWarn(){
        List<Map<String,Object>> triggerList=new ArrayList<Map<String,Object>>();
        boolean isTrigger=false;
        List<Map<String,Object>> list=assemble();

        for(int i=0;i<list.size();i++){
            Map<String,Object> map=list.get(i);
            String supplierid=(String)map.get("supplierid");
            Map<String,Object> triggerMap=new HashMap<String,Object>();
            //京东超市
            if(BwSupplierId.JD.getSupplierid().equals(supplierid)){
                isTrigger=getIsTrigger(map);
            }
            //欧飞油卡充值
            else if(BwSupplierId.OUFEI_OILCARD.getSupplierid().equals(supplierid)){
                isTrigger=getIsTrigger(map);
            }
            //欧飞话费充值
            else if(BwSupplierId.OUFEI_PHONE.getSupplierid().equals(supplierid)){
                isTrigger=getIsTrigger(map);
            }
            //饿了么
            else if(BwSupplierId.ELM.getSupplierid().equals(supplierid)){
                isTrigger=getIsTrigger(map);
            }
            //携程
            else if(BwSupplierId.CTRIP.getSupplierid().equals(supplierid)){
                isTrigger=getIsTrigger(map);
            }
            triggerMap.put("supplierid",supplierid);
            triggerMap.put("supplier",map.get("supplier"));
            triggerMap.put("balancemoney",(BigDecimal)map.get("balancemoney"));
            triggerMap.put("warningMoney",(BigDecimal)map.get("warningMoney"));
            triggerMap.put("isTrigger",isTrigger);
            triggerList.add(triggerMap);

        }
        return triggerList;
    }

    /**
     * 判断预存款与预警额度值的大小
     * @param map
     * @return
     */
    public boolean getIsTrigger(Map<String,Object> map){
        boolean isTrigger=false;
        BigDecimal advanceDeposit =(BigDecimal) map.get("balancemoney");
        BigDecimal warningMoney =(BigDecimal) map.get("warningMoney");
        if(advanceDeposit.compareTo(warningMoney)==-1){
            isTrigger=true;
        }
        return  isTrigger;
    }

}

