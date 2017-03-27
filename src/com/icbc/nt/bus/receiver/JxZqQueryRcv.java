/**
 * 绩效周期查询 2015-12-07
 */
package com.icbc.nt.bus.receiver;

import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.util.TransactionMapData;

public class JxZqQueryRcv extends BusParent implements BusReceiver {

	@Override
	public void doWork(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		logger.info("周期查询处理逻辑");
		// TODO Auto-generated method stub
//		String userId = tmd.get("userId").toString();
//		String orgId = mediumBus.getUserOrg(userId);
//		String orgIn = mediumBus.getOrgIn(orgId);
//		tmd.put("currUser", userId);
//		String orgId = busDispatcherImpl.userOrgQuery(ja, condition, retMap, tmd);
//		tmd.put("orgIdCurr", orgId);
//		String orgIn = busDispatcherImpl.userOrgIn(ja, condition, retMap, tmd);
		this.iu.rmCondition(condition);
		sqlStr = "select distinct t.zq from t_ntmisc_zq t";
//		this.queryAuto(ja, condition, sqlStr, daoParent, 1);
		this.queryAuto(ja, condition, sqlStr, daoParent, 2);//分页查询
//		sqlStr = mediumBus.putOrgCond(sqlStr, tmd, condition);
//		tmd.put("count",this.count(condition, daoParent, sqlStr, 1));//总记录数入变量池
//		iu.putCondition(condition, "start", tmd.get("start"));
//		iu.putCondition(condition, "end", tmd.get("end"));
//		this.queryAuto(ja, condition, sqlStr, daoParent, 2);//分页查询
	}
}
