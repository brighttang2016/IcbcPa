/**
 * 总包查询命令接收者
 */
package com.icbc.nt.bus.receiver;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.bus.MediumBus;
import com.icbc.nt.util.TransactionMapData;

public class ZbQueryRec extends BusParent implements BusReceiver{
	@Autowired
	MediumBus mediumBus;
	/**
	 * 机构总包查询
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 * @return
	 */
	public void doWork(JSONArray ja,
			LinkedHashMap<String, Object> condition, Map retMap,
			TransactionMapData tmd){
		String userId = tmd.get("userId").toString();
		String orgId = mediumBus.getUserOrg(userId);
		String orgIn = mediumBus.getOrgIn(orgId);
		this.iu.rmCondition(condition);
		sqlStr =  "select a.*,b.orgname from t_ntmisc_zbbl a,t_ntmisc_org b where a.orgid = b.orgid and a.orgid in"
				+ orgIn;
		sqlStr = mediumBus.putOrgCond(sqlStr, tmd, condition);
		tmd.put("count",this.count(condition, daoParent, sqlStr, 1));//总记录数入变量池
		iu.putCondition(condition, "start", tmd.get("start"));
		iu.putCondition(condition, "end", tmd.get("end"));
		this.queryAuto(ja, condition, sqlStr, daoParent, 2);//分页查询
	}
}
