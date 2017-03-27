/**
 * 总包(网点柜员手动分配)查询命令接收者
 */
package com.icbc.nt.bus.receiver;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.bus.MediumBus;
import com.icbc.nt.bus.dispatcher.BusDispatcherImpl;
import com.icbc.nt.util.TransactionMapData;

public class ZbSdfpQueryRec extends BusParent implements BusReceiver{
	@Autowired
	MediumBus mediumBus;
	@Autowired
	BusDispatcherImpl busDispatcherImpl;

	public void doWork(JSONArray ja,
			LinkedHashMap<String, Object> condition, Map retMap,
			TransactionMapData tmd){
//		String userId = tmd.get("userId").toString();
//		String orgId = mediumBus.getUserOrg(userId);
//		String orgIn = mediumBus.getOrgIn(orgId);
//		String zqCurr = busDispatcherImpl.zqCurr();
		String zqCurr = tmd.get("zqCurr").toString();
		String orgIdIn = tmd.get("orgIdIn").toString();
		this.iu.rmCondition(condition);
		iu.putCondition(condition, "zq", zqCurr);
		//sqlStr = " select a.nr_id,a.orgid,a.zq,to_char(a.zb,'999,999,999') zb,b.orgname  "
		sqlStr = " select a.nr_id,a.orgid,a.zq,a.zb,b.orgname  "
				+ " from t_ntmisc_khzb a,t_ntmisc_org b "
				+ " where a.orgid = b.orgid and  a.nr_id='4' and a.orgid in"
				+ orgIdIn;
		logger.info("总包(网点柜员手动分配)查询");
		iu.infoDbOper("总包(网点柜员手动分配)查询----1", sqlStr, condition);
		sqlStr = mediumBus.putOrgCond(sqlStr, tmd, condition);
		tmd.put("count",this.count(condition, daoParent, sqlStr, 1));//总记录数入变量池
		iu.putCondition(condition, "start", tmd.get("start"));
		iu.putCondition(condition, "end", tmd.get("end"));
		iu.infoDbOper("总包(网点柜员手动分配)查询-----2(分页查询)", sqlStr, condition);
		this.queryAuto(ja, condition, sqlStr, daoParent, 2);//分页查询
	}
}
