/**
 * 员工业绩导入结果查询2016-01-13
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

public class F20000002Rcv extends BusParent implements BusReceiver {
	@Autowired
	private MediumBus mediumBus;
	/**
	 * 绩效查询（新版）
	 * @param tmd
	 * @param ja
	 */
	public void fhzbDrQuery(JSONArray ja){
		String orgIn = tmd.get("orgIdIn").toString();
		iu.rmCondition(condition);
		iu.putCondition(condition, "zq", tmd.get("zqQuery"));
		sqlStr = " select orgid,orgname,depid,depname,userid,name,zq, " 
				+  " to_char(sum(case when nr_id='1' or nr_id='2' then point else '0' end),'99999990.90') as pt_gwlz, " 
				+  " to_char(sum(case when nr_id='3' then point else '0' end),'99999990.90') as pt_wdgg, " 
				+  " to_char(sum(case when nr_id='5' then point else '0' end),'99999990.90') as pt_qyyx " 
				+  " from( " 
				+  " select a.*,b.orgid,b.orgname,b.depid,b.depname,b.name from t_ntmisc_userjxn a,(select a.userid,a.name,a.orgid,a.depid,a.jbjx_id,b.orgname,c.depname from t_ntmisc_user a,t_ntmisc_org b,t_ntmisc_dept c " 
				+  " where a.orgid = b.orgid and a.depid = c.depid) b " 
				+  " where a.userid = b.userid "  
				+  " ) " 
				+  " where orgid in"
				+  orgIn 
				+  " group by orgid,orgname,depid,depname,userid,name,zq " 
				+  " order by orgid,depid,zq desc,userid " ;
		sqlStr = mediumBus.putOrgCond(sqlStr, tmd, condition);
		tmd.put("count",this.count(condition, daoParent, sqlStr, 1));//总记录数入变量池
		iu.putCondition(condition, "start", tmd.get("start"));
		iu.putCondition(condition, "end", tmd.get("end"));
		iu.infoDbOper("员工业绩导入结果查询", sqlStr, condition);
		this.queryAuto(ja, condition, sqlStr, daoParent, 1);
		logger.info("员工业绩导入结果查询,查询结果,ja："+ja.toJSONString());
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
