package com.ruoyi.quartz.task;


import com.ruoyi.common.enums.BwSupplierId;
import com.ruoyi.quartz.util.SpringContextUtil;
import com.ruoyi.quartz.util.WarningQuartzUtil;


import com.ruoyi.ruoyimailservice.util.SendMailText;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 *
 * 供应商预存款额度预警任务
 *
 * @author aofajia
 * @create 2018/12/12
 * @since 1.0.0
 */
@Component("WarningTask")
public class WarningTask{

    @Autowired
    private WarningQuartzUtil warningQuartzUtil;

    @Autowired
    private SendMailText sendMailText;

    public void moneyMatch(){

        System.out.println("供应商预存款额度预警!");
        /*WarningQuartzUtil util=(WarningQuartzUtil)SpringContextUtil.getBean("warningQuartzUtil");*/

        List<Map<String, Object>> triggerWarn =warningQuartzUtil.isTriggerWarn();
        for(Map<String, Object>  map:triggerWarn){
            Object supplierid = map.get("supplierid");
            //京东
            if(supplierid.equals(BwSupplierId.JD.getSupplierid())){
                isResult(map);
                continue;
            }
            //携程
            if (supplierid.equals(BwSupplierId.CTRIP.getSupplierid())){
                isResult(map);
                continue;
            }
            //饿了么
            if (supplierid.equals(BwSupplierId.ELM.getSupplierid())){
                isResult(map);
                continue;
            }
            //欧飞油卡充值
            if (supplierid.equals(BwSupplierId.OUFEI_OILCARD.getSupplierid())){
                isResult(map);
                continue;
            }
            //欧飞话费充值
            if (supplierid.equals(BwSupplierId.OUFEI_PHONE.getSupplierid())){
                isResult(map);
                continue;
            }
        }

    }

    public void isResult(Map<String, Object> map){
        if((boolean)map.get("isTrigger")==true){
            sendMailText.scheduleTaskSendMail(map);
        }
    }
}

