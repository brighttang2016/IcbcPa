/**
 * 用户机构查询(获取用户所在机构信息)
 */
package com.icbc.nt.bus.receiver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.util.TransactionMapData;

public class UserOrgQueryRcv extends BusParent implements BusReceiver {

	@Override
	public void doWork(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub
		
	}
	public void doWork(TransactionMapData tmd){
		String userId = tmd.get("currUser").toString();
		iu.rmCondition(condition);
		iu.putCondition(condition, "userId", userId);
//		sqlStr = "select t.orgid from t_ntmisc_user t where t.userid = ?";
//		this.queryManu(ja, condition, sqlStr,daoParent, 1);
		String orgId = "";
//		iu.putCondition(condition, "userId", userId);
		sqlStr = "select orgid from t_ntmisc_user t where t.userid = ?";
		ArrayList<HashMap<String,String>> vistOrgList = new ArrayList<HashMap<String,String>>();
		logger.info("执行sql，用户机构查询(获取用户所在机构信息) sqlStr："+sqlStr+",condition:"+condition.toString());
		this.queryManu(vistOrgList, condition, sqlStr, daoParent, 1);
		logger.info("查询结果：vistOrgList："+vistOrgList.toString());
		try {
//			logger.info("长度"+vistOrgList.size());
			HashMap<String,String> row = vistOrgList.get(0);
			orgId = row.get("orgid");
//			logger.info("当前用户:"+userId+"所在机构:"+row.get("orgid"));
		} catch (Exception e) {
			// TODO: handle exception
		}
		tmd.put("orgIdCurr", orgId);
	}
}
