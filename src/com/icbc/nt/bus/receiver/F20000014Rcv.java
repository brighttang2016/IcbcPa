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

public class F20000014Rcv extends BusParent implements BusReceiver {
	@Autowired
	private MediumBus mediumBus;
	/**
	 * 绩效查询（新版）
	 * @param tmd
	 * @param ja
	 */
	public void fhzbDrQuery(TransactionMapData tmd,JSONArray ja){
		String orgIn = tmd.get("orgIdIn").toString();
//		StringBuffer orgInBuf = new StringBuffer();//(分支行号1、分支行号2、分支行号3)
//		String orgIdCurr = tmd.get("orgIdCurr").toString();//当前登录用户所在机构
//		String zqCurr = tmd.get("zqCurr").toString();
//		String brOrgId = tmd.get("brOrgId").toString();//当前登录用户所在分支行号
		iu.rmCondition(condition);
		iu.putCondition(condition, "zq", tmd.get("zqQuery"));
		iu.putCondition(condition, "nr_id", "4");//nr_id：4 网点手动分配
		sqlStr = "  select b.userid,b.name,b.orgid,b.orgname,b.depid,b.depname,a.nr_id,a.zq,"
				+ "to_char(a.jx,'999,999,999,990.90') jx "
				+ " from t_ntmisc_userjxn a,(select a.userid,a.name,a.orgid,a.depid,a.jbjx_id,b.orgname,c.depname from t_ntmisc_user a,t_ntmisc_org b,t_ntmisc_dept c "
				+  " where a.orgid = b.orgid and a.depid = c.depid) b " 
				+  " where a.userid = b.userid  and b.orgid in"
				+   orgIn
				+  " order by b.orgid asc,b.userid asc,a.zq desc ";
		sqlStr = mediumBus.putOrgCond(sqlStr, tmd, condition);
		tmd.put("count",this.count(condition, daoParent, sqlStr, 1));//总记录数入变量池
		iu.putCondition(condition, "start", tmd.get("start"));
		iu.putCondition(condition, "end", tmd.get("end"));
		logger.info("网点手动分配绩效导入查询");
		iu.infoDbOper("网点手动分配绩效导入查询", sqlStr, condition);
		this.queryAuto(ja, condition, sqlStr, daoParent, 1);
		logger.debug("网点手动分配绩效导入查询,查询结果,ja："+ja.toJSONString());
	}
	@Override
	public void doWork(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub
		this.fhzbDrQuery(tmd, ja);
	}
}
