/**
 * 网点考核部分总包导入结果查询 2016-01-12
 */
package com.icbc.nt.bus.receiver;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.bus.MediumBus;
import com.icbc.nt.util.TransactionMapData;

public class F20000006Rcv extends BusParent implements BusReceiver {
	@Autowired
	private MediumBus mediumBus;
	/**
	 * 总包导入结果查询(网点考核) 
	 * @param tmd
	 * @param ja
	 */
	public void fhzbDrQuery(JSONArray ja){
		String orgIn = tmd.get("orgIdIn").toString();
		logger.debug("总包导入结果查询(网点考核) ");
		tmd.tmdLogger();
//		StringBuffer orgInBuf = new StringBuffer();//(分支行号1、分支行号2、分支行号3)
//		String userIdLogin  = tmd.get("userIdLogin ").toString();//当前登录用户所在机构
//		String zqCurr = tmd.get("zqCurr").toString();
//		String brOrgId = tmd.get("brOrgId").toString();//当前登录用户所在分支行号
		iu.rmCondition(condition);
		iu.putCondition(condition, "zq", tmd.get("zqQuery"));
		iu.putCondition(condition, "nr_id", "3");//nr_id：3 网点自由考核
		sqlStr =  "  select t.orgid,t.orgid depid,t.orgname,t.zq,t.nr_id,"
				+ "  to_char(t.zb,'999,999,999,999.99') zb_wdzy " 
				+  " from (select a.orgid,a.orgname,b.nr_id,b.zq,b.zb,c.nr_name from t_ntmisc_org a,t_ntmisc_khzb b,t_ntmisc_khnr c " 
				+  " where a.orgid = b.orgid and b.nr_id = c.nr_id) t "
				+  " where t.orgid in "
				+  orgIn
				+  " order by t.zq desc " ;
		sqlStr = mediumBus.putOrgCond(sqlStr, tmd, condition);
		tmd.put("count",this.count(condition, daoParent, sqlStr, 1));//总记录数入变量池
		iu.putCondition(condition, "start", tmd.get("start"));
		iu.putCondition(condition, "end", tmd.get("end"));
		iu.infoDbOper("网点考核部分总包导入结果查询", sqlStr, condition);
		this.queryAuto(ja, condition, sqlStr, daoParent, 1);
		logger.debug("网点考核部分总包导入结果查询,查询结果,ja："+ja.toJSONString());
	}
	
	public void doWork(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap) {
		// TODO Auto-generated method stub
		this.fhzbDrQuery(ja);
	}

	@Override
	public void doWork(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub
		
	}
}
