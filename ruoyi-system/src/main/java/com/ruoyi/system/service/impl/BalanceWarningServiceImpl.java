package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.*;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.IBalanceWarningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class BalanceWarningServiceImpl implements IBalanceWarningService
{
    @Autowired
    private BWConfigtypeMapper bwConfigtypeMapper;
    @Autowired
    private BWConfigMapper bwConfigMapper;
    @Autowired
    private BWConfigLOGMapper bwConfigLOGMapper;
    @Autowired
    private CompanyMapper companyMapper;
    @Autowired
    private RechargeLogMapper rechargeLogMapper;
    @Autowired
    private BalanceWarnSettingMapper balanceWarnSettingMapper;

    @Override
    public List<BWConfigtype> selectConfigTypeAll()
    {
        List<BWConfigtype> bwConfigtypes = bwConfigtypeMapper.selectConfigTypeAll();
        return bwConfigtypes;
    }

    @Override
    public String checkStoreName(String configname)
    {
        Map map = bwConfigtypeMapper.checkStoreName(configname);
        String count = map.get("COUNT(0)").toString();
        if("0".equals(count))
        {
            //供应商不存在 报错
        }
        return "0";
    }

    @Override
    public int addStoreConfig(BWConfig configMap)
    {
        configMap.setId(UUID.randomUUID().toString().toUpperCase());
        configMap.setConfigtype("0001");
        configMap.setStatus("1");
        String configName = configMap.getConfigname();
        Map map = bwConfigMapper.checkConfigName(configName);
        String count = map.get("COUNT(0)").toString();
        if("0".equals(count))
        {
            //该供应商配置不存在
            int insert = bwConfigMapper.insert(configMap);

            BWConfigLOG bwConfigLOG = new BWConfigLOG(configMap);
            bwConfigLOG.setLogid(UUID.randomUUID().toString().toUpperCase());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date nowdate = new Date();
            String createtime = sdf.format(nowdate);
            bwConfigLOG.setCreatetime(createtime);
            bwConfigLOGMapper.insert(bwConfigLOG);

            return insert;
        }
        else
        {
            return 0;
        }
    }

    @Override
    public int addMangeConfig(BWConfig configMap)
    {
        configMap.setId(UUID.randomUUID().toString().toUpperCase());
        configMap.setConfigtype("0002");
        configMap.setStatus("1");
        String configName = configMap.getConfigname();
        Map map = bwConfigMapper.checkConfigName(configName);
        String count = map.get("COUNT(0)").toString();
        if("0".equals(count))
        {
            //该商城负责人配置不存在
            int insert = bwConfigMapper.insert(configMap);

            BWConfigLOG bwConfigLOG = new BWConfigLOG(configMap);
            bwConfigLOG.setLogid(UUID.randomUUID().toString().toUpperCase());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date nowdate = new Date();
            String createtime = sdf.format(nowdate);
            bwConfigLOG.setCreatetime(createtime);
            bwConfigLOGMapper.insert(bwConfigLOG);

            return insert;
        }
        else
        {
            return 0;
        }
    }

    @Override
    public List<BWConfig> selectConfigAll()
    {
        List<BWConfig> bwConfigs = bwConfigMapper.selectConfigAll();
        return bwConfigs;
    }

    @Override
    public BWConfig selectStoreConfigById(String roleId)
    {
        BWConfig bwConfig = bwConfigMapper.selectByPrimaryKey(roleId);
        return bwConfig;
    }

    @Override
    public BWConfig selectManageConfigById(String roleId)
    {
        BWConfig bwConfig = bwConfigMapper.selectByPrimaryKey(roleId);
        return bwConfig;
    }

    @Override
    public int updateStoreConfig(BWConfig bwConfig)
    {
        Map map = bwConfigMapper.checkConfigNameExceptMe(bwConfig);
        String count = map.get("COUNT(0)").toString();
        if("0".equals(count))
        {
            int i = bwConfigMapper.updateByPrimaryKey(bwConfig);

            BWConfigLOG bwConfigLOG = new BWConfigLOG(bwConfig);
            bwConfigLOG.setLogid(UUID.randomUUID().toString().toUpperCase());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date nowdate = new Date();
            String createtime = sdf.format(nowdate);
            bwConfigLOG.setCreatetime(createtime);
            bwConfigLOGMapper.insert(bwConfigLOG);

            return i;
        }
        else
        {
            return 0;
        }
    }

    @Override
    public int updateManageConfig(BWConfig bwConfig)
    {
        Map map = bwConfigMapper.checkConfigNameExceptMe(bwConfig);
        String count = map.get("COUNT(0)").toString();
        if("0".equals(count))
        {
            int i = bwConfigMapper.updateByPrimaryKey(bwConfig);

            BWConfigLOG bwConfigLOG = new BWConfigLOG(bwConfig);
            bwConfigLOG.setLogid(UUID.randomUUID().toString().toUpperCase());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date nowdate = new Date();
            String createtime = sdf.format(nowdate);
            bwConfigLOG.setCreatetime(createtime);
            bwConfigLOGMapper.insert(bwConfigLOG);

            return i;
        }
        else
        {
            return 0;
        }
    }

    @Override
    public List<StoreConfig> getStoreMonthlyMoney()
    {
        List<StoreConfig> storeConfigs = bwConfigMapper.selectStoreConfigAll();
        return storeConfigs;
    }

    @Override
    public int deleteConfigByIds(String id) throws Exception
    {
        int i = bwConfigMapper.deleteByPrimaryKey(id);

        BWConfigLOG bwConfigLOG = new BWConfigLOG(id);
        bwConfigLOG.setLogid(UUID.randomUUID().toString().toUpperCase());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date nowdate = new Date();
        String createtime = sdf.format(nowdate);
        bwConfigLOG.setCreatetime(createtime);
        bwConfigLOGMapper.insert(bwConfigLOG);

        return i;
    }

    @Override
    public List<Company> companyList()
    {
        List<Company> companies = companyMapper.selectAllCompany();
        return companies;
    }

    @Override
    public int saveRecharge(RechargeLog rechargeLogMap)
    {
        int insert = rechargeLogMapper.insert(rechargeLogMap);
        return insert;
    }

    @Override
    public List<RechargeLog> getRechargeList(RechargeLog rechargeLogMap)
    {
        List<RechargeLog> rechargeLogs = rechargeLogMapper.selectRoleList(rechargeLogMap);
        return rechargeLogs;
    }

    @Override
    public List<SdbBusinessStoremanger> getSupplierInfo() {
        List<SdbBusinessStoremanger> list=balanceWarnSettingMapper.getSupplierInfo();
        return list;
    }

    @Override
    public void insertSupplierInfo(BalanceWarningConfig config) {
        balanceWarnSettingMapper.insertSupplierInfo(config);
    }


    @Override
    public int selectSupplierInfo() {
        int count=balanceWarnSettingMapper.selectSupplierInfo();
        return count;
    }

    @Override
    public int updateBWByStatus(BWConfig bwConfig){

        int i=bwConfigMapper.updateByPrimaryKeySelective(bwConfig);
        /*insert into bwconfiglog*/
        BWConfig bwConfigNew=bwConfigMapper.selectByPrimaryKey(bwConfig.getId());
        BWConfigLOG bwConfigLOG=new BWConfigLOG(bwConfigNew);
        bwConfigLOG.setLogid(UUID.randomUUID().toString().toUpperCase());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date nowdate = new Date();
        String createtime = sdf.format(nowdate);
        bwConfigLOG.setCreatetime(createtime);
        bwConfigLOGMapper.insert(bwConfigLOG);

        return i;
    }
}
