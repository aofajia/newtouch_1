package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.*;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.IOrderCheckingService;
import com.ruoyi.system.tool.HRFIExctption;
import com.ruoyi.system.tool.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class OrderCheckingServiceImpl implements IOrderCheckingService
{
    @Autowired
    private StoremangerMapper storemangerMapper;
    @Autowired
    private BWConfigMapper bwConfigMapper;
    @Autowired
    private MemberAdvanceMapper memberAdvanceMapper;
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private RechargeLogMapper rechargeLogMapper;
    @Autowired
    private BalanceRecordMapper balanceRecordMapper;
    @Autowired
    private CheckingLogMapper checkingLogMapper;
    @Autowired
    private CheckingOperationMapper checkingOperationMapper;
    @Autowired
    private CheckingMallMapper checkingMallMapper;
    @Autowired
    private CheckingStoreMapper checkingStoreMapper;
    @Autowired
    private OrderCheckStatusMapper orderCheckStatusMapper;

    @Override
    public List<Storemanger> selectStoreAll()
    {
        List<Storemanger> storemangers = storemangerMapper.selectAllStore();

        return storemangers;
    }

    @Override
    public List<StoreConfig> selectStoreConfigList()
    {
        List<StoreConfig> storeConfigs = bwConfigMapper.selectStoreConfigAll();
        return storeConfigs;
    }

    @Override
    public Map chinkingMemberAdvance(String startTime, int intflag)
    {
        //创建对账操作批次 主ID 操作批次ID
        String mainid = UUID.randomUUID().toString().toUpperCase();
        //插入对账操作记录表
        CheckingOperation checkingOperation = new CheckingOperation();
        checkingOperation.setMainid(mainid);
        checkingOperation.setCheckingdate(startTime);
        if(intflag == 0)
        {
            //来自定时对账程序
            checkingOperation.setCheckingtype("0");
        }
        else
        {
            //手动对账
            checkingOperation.setCheckingtype("1");
        }
        checkingOperationMapper.insertSelective(checkingOperation);

        //开始对账
        Map<String, String> paramMap = new HashMap();
        paramMap.put("startTime",startTime);

        //员工消费总金额
        Map employeePayMoney = ordersMapper.getEmployeePayMoney(paramMap);
        String employeepaymoney = employeePayMoney.get("employeepaymoney").toString();
        BigDecimal bpaymoney = new BigDecimal(employeepaymoney);

        //员工剩余账户总金额
        paramMap.put("supplierid","Mall");
        List<BalanceRecord> balanceRecords = balanceRecordMapper.selectBySupplierId(paramMap);
        BalanceRecord balanceRecord = new BalanceRecord();
        if(balanceRecords.size() != 0)
        {
            balanceRecord = balanceRecords.get(0);
        }
        else
        {
            balanceRecord.setBalancemoney(new BigDecimal("0.00"));
        }
        BigDecimal employeeresiduemoney = balanceRecord.getBalancemoney();
        paramMap.remove("supplierid");

        //申请提现额度
        String url = "http://59.80.30.153:4090/HR/getForwardInfo";
        HttpClientUtil httpUtil=new HttpClientUtil();
        paramMap.put("startDate","2018-06-01");
        paramMap.put("endDate",startTime);
        String str=httpUtil.doPost(url,paramMap,"UTF-8");
        BigDecimal bleavemoney = new BigDecimal(str);

        //每月充值
        paramMap.put("memo","每月充值");
        Map monthMoney = memberAdvanceMapper.getMonthMoney(paramMap);
        String monthmoney = monthMoney.get("import_money").toString();
        BigDecimal bmonthmoney = new BigDecimal(monthmoney);

        //判断商城恒等式是否相等
        String flag = "";
        BigDecimal formerSum = bpaymoney.add(employeeresiduemoney).add(bleavemoney);
        int i = formerSum.compareTo(bmonthmoney);
        if (i < 0)
        {
            flag = "<";
        }
        if (i == 0)
        {
            flag = "=";
        }
        if (i > 0)
        {
            flag = ">";
        }

        Map<String, Object> responseMap = new HashMap();
        //员工消费总金额
        responseMap.put("employee_paymoney",bpaymoney);
        //员工剩余账户总金额
        responseMap.put("employee_residuemoney",employeeresiduemoney);
        //申请提现额度
        responseMap.put("employee_leavemoney",bleavemoney);
        //商城充值总金额
        responseMap.put("mall_rechargemoney",bmonthmoney);
        //恒等式平衡标志位
        responseMap.put("flag",flag);
        responseMap.put("intflag",i);
        responseMap.put("mainid",mainid);

        //插入商城恒等式记录表
        CheckingMall checkingMall = new CheckingMall();
        checkingMall.setDetailid(UUID.randomUUID().toString().toUpperCase());
        checkingMall.setMianid(mainid);
        checkingMall.setCheckingdate(startTime);
        checkingMall.setCheckingstatus(i);
        checkingMall.setPaymoney(bpaymoney);
        checkingMall.setResiduemoney(employeeresiduemoney);
        checkingMall.setLeavemoney(bleavemoney);
        checkingMall.setWelfaremoney(bmonthmoney);
        checkingMallMapper.insertSelective(checkingMall);

        return responseMap;
    }

    @Override
    public List<Map> storeChecking(String startTime, String mainid)
    {
        List<Map> responseList = new ArrayList<>();

        List<StoreConfig> storeConfigs = selectStoreConfigList();
        for (int i = 0; i < storeConfigs.size() ; i++)
        {

            //获取配置供应商信息
            StoreConfig storeConfig = storeConfigs.get(i);
            String store_id = storeConfig.getStore_id();
            String shop_name = storeConfig.getShop_name();
            String paymethod = storeConfig.getPaymethod();
            Map<String, Object> paramMap = new HashMap();
            paramMap.put("store_id",store_id);
            paramMap.put("startTime",startTime);
            if("104".equals(store_id))
            {
                continue;
            }

            //插入供应商恒等式记录表
            CheckingStore checkingStore = new CheckingStore();
            checkingStore.setDetailid(UUID.randomUUID().toString().toUpperCase());
            checkingStore.setMianid(mainid);
            checkingStore.setCheckingdate(startTime);
            checkingStore.setSupplierid(store_id);
            checkingStore.setSuppliername(shop_name);

            Map<String, Object> responseMap = new HashMap();
            responseMap.put("store_id",store_id);
            responseMap.put("shop_name",shop_name);
            responseMap.put("paymethod",paymethod);
            if("precharge".equals(paymethod))
            {
                //预充值供应商
                //获取有效订单总金额
                Map validOrderSum = ordersMapper.getValidOrderSum(paramMap);
                String validmoney = "0.00";
                if(validOrderSum != null)
                {
                    validmoney = validOrderSum.get("validmoney").toString();
                }
                else
                {
                }
                BigDecimal valid = new BigDecimal(validmoney);
                responseMap.put("validmoney",validmoney);

                //获取当前时间供应商余额记录表中的余额(定时任务 从供应商接口获取)
                paramMap.put("supplierid",store_id);
                List<BalanceRecord> balanceRecords = balanceRecordMapper.selectBySupplierId(paramMap);
                BalanceRecord balanceRecord = new BalanceRecord();
                if(balanceRecords.size() != 0)
                {
                    balanceRecord = balanceRecords.get(0);
                }
                else
                {
                    balanceRecord.setBalancemoney(new BigDecimal("0.00"));
                }
                BigDecimal balance = balanceRecord.getBalancemoney();
                responseMap.put("storebalance",balance);

                //从供应商充值记录表中获取 对应供应商累计充值总金额值总金额
                Map rechargeSum = rechargeLogMapper.selectRechargeSumByStore(paramMap);
                String rechargemoney = "0.00";
                if(rechargeSum != null)
                {
                    rechargemoney = rechargeSum.get("rechargemoney").toString();
                }
                BigDecimal recharge = new BigDecimal(rechargemoney);
                responseMap.put("rechargemoney",rechargemoney);

                //判断供应商恒等式是否相等
                String flag = "";
                BigDecimal storeSum = valid.add(balance);
                int j = storeSum.compareTo(recharge);
                responseMap.put("intflag",j);
                if (j == 0)
                {
                    flag = "=";
                    CheckingLog checkingLog = new CheckingLog();
                    checkingLog.setId(UUID.randomUUID().toString().toUpperCase());
                    checkingLog.setMainid(mainid);
                    checkingLog.setCheckingdate(startTime);
                    checkingLog.setCheckingstatus(0);
                    checkingLog.setSupplierid(store_id);
                    checkingLog.setSuppliername(shop_name);
                    Date nowdate = new Date();
                    checkingLog.setCreatetime(nowdate);
                    checkingLogMapper.insertSelective(checkingLog);
                }

                //补全供应商恒等式记录表
                checkingStore.setCheckingstatus(j);
                checkingStore.setPaymethod(paymethod);
                checkingStore.setValidmoney(valid);
                checkingStore.setStorebalance(balance);
                checkingStore.setRechargemoney(recharge);
                checkingStoreMapper.insertSelective(checkingStore);
            }
            else if ("monthly".equals(paymethod))
            {
                //月结
                //获取有效订单总金额
                Map validOrderSum = ordersMapper.getValidOrderSum(paramMap);
                String validmoney = "0.00";
                if(validOrderSum != null)
                {
                    validmoney = validOrderSum.get("validmoney").toString();
                }
                BigDecimal valid = new BigDecimal(validmoney);
                responseMap.put("validmoney",validmoney);

                //获取当前时间 供应商订单总额(定时任务 从供应商接口处获取所有订单 并记录表(供应商ID 订单号 订单时间 金额))
                String storeordersmoney = "0.00";
                BigDecimal rdersmoney = new BigDecimal(storeordersmoney);
                responseMap.put("storeordersmoney",storeordersmoney);

                //判断供应商恒等式是否相等
                String flag = "";
                int j = valid.compareTo(rdersmoney);
                responseMap.put("intflag",j);
                if (j == 0)
                {
                    flag = "=";
                    CheckingLog checkingLog = new CheckingLog();
                    checkingLog.setId(UUID.randomUUID().toString().toUpperCase());
                    checkingLog.setMainid(mainid);
                    checkingLog.setCheckingdate(startTime);
                    checkingLog.setCheckingstatus(0);
                    checkingLog.setSupplierid(store_id);
                    checkingLog.setSuppliername(shop_name);
                    Date nowdate = new Date();
                    checkingLog.setCreatetime(nowdate);
                    checkingLogMapper.insertSelective(checkingLog);
                }

                //补全供应商恒等式记录表
                checkingStore.setCheckingstatus(j);
                checkingStore.setPaymethod(paymethod);
                checkingStore.setValidmoney(valid);
                checkingStore.setStoreordersmoney(rdersmoney);
                checkingStoreMapper.insertSelective(checkingStore);
            }
            responseList.add(responseMap);
        }
        return responseList;
    }

    @Override
    public List gatDifferenceOrderList(String id, String startTime, String mainid)
    {
        List<StoreConfig> storeConfigs = bwConfigMapper.selectStoreConfigAll();

        Map<String, Object> paramMap = new HashMap();
        paramMap.put("id",id);
        paramMap.put("startTime",startTime);

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        try
        {
            date = sdf.parse(startTime);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -6);
        date = calendar.getTime();
        String before6Time = sdf.format(date);
        paramMap.put("before6Time",before6Time);

        //订单数据交集 插入到hrfi_openticket订单对账状态记录表 checkingstatus状态 0对账正确
        paramMap.put("orderid","orderid");
        paramMap.put("ordermoney","ordermoney");
        paramMap.put("intcount",2);
        paramMap.put("samelist","samelist");
        List<DifferenceOrderList> sameList = ordersMapper.gatDifferenceOrderList(paramMap);
        for (int i = 0; i < sameList.size(); i++)
        {
            //插入订单对账状态记录表 开票使用
            OrderCheckStatus orderCheckStatus = new OrderCheckStatus();
            orderCheckStatus.setOrderId(Long.parseLong(sameList.get(i).getOrder_id()));
            orderCheckStatus.setOrderLegalman(Integer.parseInt(sameList.get(i).getOrder_legalman()));
            orderCheckStatus.setInvoiceLegalman(Integer.parseInt(sameList.get(i).getInvoice_legalman()));
            orderCheckStatus.setFinalAmount(new BigDecimal(sameList.get(i).getPayed()));
            orderCheckStatus.setStatus(sameList.get(i).getStatus());
            orderCheckStatus.setStoreId(Long.parseLong(sameList.get(i).getStore_id()));
            orderCheckStatus.setCreatetime(Integer.parseInt(sameList.get(i).getCreatetime()));
            orderCheckStatus.setCheckingstatus(0);
            orderCheckStatus.setType(sameList.get(i).getType());
            OrderCheckStatus ifnull = orderCheckStatusMapper.selectByPrimaryKey(Long.parseLong(sameList.get(i).getOrder_id()));
            if(ifnull == null)
            {
                orderCheckStatusMapper.insertSelective(orderCheckStatus);
            }
            else
            {
                orderCheckStatusMapper.updateByPrimaryKeySelective(orderCheckStatus);
            }
        }


        //完全不相同订单(包含订单相同 金额不同)
        paramMap.remove("samelist");
        paramMap.replace("intcount",1);
        paramMap.put("differ","differ");
        List<DifferenceOrderList> differList = ordersMapper.gatDifferenceOrderList(paramMap);

        //订单数据差集(订单不相同) checkingstatus状态 1商城订单丢失 2供应商订单丢失
        paramMap.remove("ordermoney");
        List<DifferenceOrderList> outList = ordersMapper.gatDifferenceOrderList(paramMap);

        List<Integer> indexList = new ArrayList<>();
        //删除所有订单不相同 求订单相同 金额不同订单
        for (int i = 0; i < outList.size(); i++)
        {
            DifferenceOrderList differenceOrderList = outList.get(i);
            if("t1".equals(outList.get(i).getTn()))
            {
                String order_id = differenceOrderList.getOrder_id();
                for (int j = 0; j < differList.size(); j++)
                {
                    if(differList.get(j).getOrder_id().equals(order_id))
                    {
                        indexList.add(j);
                    }
                }
            }
            else if("t2".equals(outList.get(i).getTn()))
            {
                String other_order_id = differenceOrderList.getOther_order_id();
                for (int j = 0; j < differList.size(); j++)
                {
                    if(differList.get(j).getOther_order_id().equals(other_order_id))
                    {
                        indexList.add(j);
                    }
                }
            }
        }
        for (int i = indexList.size()-1; i >= 0; i--)
        {
            int index = indexList.get(i);
            differList.remove(index);
        }


        //订单数据差集 checkingstatus状态 1商城订单丢失 2供应商订单丢失
        /*paramMap.remove("ordermoney");
        paramMap.remove("samelist");
        paramMap.replace("intcount",1);
        List<DifferenceOrderList> outList = ordersMapper.gatDifferenceOrderList(paramMap);
*/
        //订单数据 订单号相同 金额不同数据 checkingstatus状3
//        paramMap.remove("orderid");
//        paramMap.put("ordermoney","ordermoney");
//        paramMap.put("differ","differ");
//        List<DifferenceOrderList> differList = ordersMapper.gatDifferenceOrderList(paramMap);
        indexList.clear();
        Map<String, Object> removeMapInfo = new LinkedHashMap<String, Object>();
        for (int i = 0; i < differList.size(); i++)
        {
            if("t2".equals(differList.get(i).getTn()))
            {
                indexList.add(i);
                removeMapInfo.put(differList.get(i).getOther_order_id(),differList.get(i).getOther_payed());
                removeMapInfo.put(differList.get(i).getOther_order_id()+"_status",differList.get(i).getOther_status());
            }
            else
            {
                String order_id = differList.get(i).getOrder_id();
                differList.get(i).setOther_order_id(order_id);
                differList.get(i).setOther_payed(removeMapInfo.get(order_id).toString());
                differList.get(i).setOther_status(removeMapInfo.get(order_id+"_status").toString());
                if("103".equals(differList.get(i).getStore_id()) || "104".equals(differList.get(i).getStore_id()))
                {
                    if ("dead".equals(differList.get(i).getStatus()) && "9".equals(differList.get(i).getOther_status()))
                    {
                        indexList.add(i);
                    }
                }
            }
        }
        for (int i = indexList.size()-1; i >= 0; i--)
        {
            int index = indexList.get(i);
            differList.remove(index);
        }

        Date nowdate = new Date();
        List<DifferenceOrderList> exceptionList = differList;
        Collections.reverse(outList);
        exceptionList.addAll(outList);
        for (int i = 0; i < exceptionList.size(); i++)
        {
            CheckingLog checkingLog = new CheckingLog();
            checkingLog.setId(UUID.randomUUID().toString().toUpperCase());
            checkingLog.setMainid(mainid);
            checkingLog.setCheckingdate(startTime);
            checkingLog.setSupplierid(id);
            checkingLog.setSuppliername(exceptionList.get(i).getStorename());
            checkingLog.setOrderId(exceptionList.get(i).getOrder_id());
            checkingLog.setPayed(exceptionList.get(i).getPayed());
            checkingLog.setOtherOrderId(exceptionList.get(i).getOther_order_id());
            checkingLog.setOtherPayed(exceptionList.get(i).getOther_payed());
            checkingLog.setCreatetime(nowdate);


            //插入订单对账状态记录表 开票使用
            OrderCheckStatus orderCheckStatus = new OrderCheckStatus();
            OrderCheckStatus ifnull = new OrderCheckStatus();
            DifferenceOrderList differenceOrderList = exceptionList.get(i);
            String order_id = differenceOrderList.getOrder_id();
            String other_order_id = differenceOrderList.getOther_order_id();
            String payed = differenceOrderList.getPayed();
            String other_payed = differenceOrderList.getOther_payed();
            if("".equals(other_order_id) && !"".equals(order_id))
            {
                //商城有 第三方缺失
                orderCheckStatus.setOrderId(Long.parseLong(order_id));
                orderCheckStatus.setFinalAmount(new BigDecimal(payed));
                orderCheckStatus.setStatus(differenceOrderList.getStatus());
                orderCheckStatus.setCreatetime(Integer.parseInt(exceptionList.get(i).getCreatetime()));
                orderCheckStatus.setCheckingstatus(2);
                orderCheckStatus.setType(differenceOrderList.getType());

                ifnull = orderCheckStatusMapper.selectByPrimaryKey(Long.parseLong(order_id));

                checkingLog.setCheckingstatus(2);
                checkingLogMapper.insertSelective(checkingLog);
            }
            else if("".equals(order_id) && !"".equals(other_order_id))
            {
                //第三方有 商城缺失
                orderCheckStatus.setOrderId(Long.parseLong(other_order_id));
                orderCheckStatus.setFinalAmount(new BigDecimal(other_payed));
                orderCheckStatus.setCheckingstatus(1);

                ifnull = orderCheckStatusMapper.selectByPrimaryKey(Long.parseLong(other_order_id));

                checkingLog.setCheckingstatus(1);
                checkingLogMapper.insertSelective(checkingLog);
            }
            else if(!"".equals(order_id) && !"".equals(other_order_id))
            {
                //订单数据 订单号相同 金额不同数据
                orderCheckStatus.setOrderId(Long.parseLong(order_id));
                orderCheckStatus.setFinalAmount(new BigDecimal(payed));
                orderCheckStatus.setStatus(differenceOrderList.getStatus());
                orderCheckStatus.setCreatetime(Integer.parseInt(exceptionList.get(i).getCreatetime()));
                orderCheckStatus.setCheckingstatus(3);
                orderCheckStatus.setType(differenceOrderList.getType());

                checkingLog.setCheckingstatus(3);
                ifnull = orderCheckStatusMapper.selectByPrimaryKey(Long.parseLong(order_id));

                checkingLogMapper.insertSelective(checkingLog);
            }
            orderCheckStatus.setStoreId(Long.parseLong(exceptionList.get(i).getStore_id()));


            if(ifnull == null)
            {
                orderCheckStatusMapper.insertSelective(orderCheckStatus);
            }
            else
            {
                orderCheckStatusMapper.updateByPrimaryKeySelective(orderCheckStatus);
            }
        }
        return exceptionList;
    }

    /*MAP 商城恒等式(mall:恒等式) 供应商恒等式(多个供应商为store:List(恒等式对象))*/
    @Override
    public Map getchinkinginfo(String startTime) throws HRFIExctption
    {
        Map<String, Object> responseMap = new HashMap();
        //现获取对账时间最后一次对账记录 获取mianID
        List<CheckingOperation> checkingOperations = checkingOperationMapper.selectByStartTime(startTime);
        String mainid = "";
        if(checkingOperations.size() == 0)
        {
            throw new HRFIExctption("本日暂无对账信息");
        }
        else
        {
            mainid = checkingOperations.get(0).getMainid();
        }
        //通过mianID去  商城恒等式记录表(对象)  供应商恒等式记录表(List(对象))  获取数据
        List<CheckingMall> checkingMalls = checkingMallMapper.selectByMainID(mainid);
        if(checkingMalls.size() == 0)
        {
            throw new HRFIExctption("本日暂无对账信息");
        }
        else
        {
            CheckingMall checkingMall = checkingMalls.get(0);
            responseMap.put("mall",checkingMall);
        }

        List<CheckingStore> checkingStores = checkingStoreMapper.selectByMainID(mainid);
        if(checkingStores.size() == 0)
        {
            throw new HRFIExctption("本日暂无对账信息");
        }
        else
        {
            responseMap.put("store",checkingStores);
        }

        return responseMap;
    }

    @Override
    public List getchinkingList(String mainid)
    {
        List<CheckingLog> checkingLogs = checkingLogMapper.selectByMainID(mainid);
        return checkingLogs;
    }
}
