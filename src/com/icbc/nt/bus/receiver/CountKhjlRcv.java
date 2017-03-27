/**
 * 给定机构号下所有客户经理计数 2015-12-11
 */
package com.icbc.nt.bus.receiver;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.bus.dispatcher.BusDispatcherImpl;
import com.icbc.nt.util.TransactionMapData;

public class CountKhjlRcv extends BusParent implements BusReceiver {
	@Autowired
	private BusDispatcherImpl busDispatcherImpl;
	@Override
	public void doWork(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub
	}
	
	public int doWork(TransactionMapData tmd) {
		int emp_num = 0;
		JSONArray ja = new JSONArray();
		Map<String,String> retMap = new HashMap<String,String>();
		String orgId = tmd.get("orgId").toString();
		tmd.put("orgIdCurr", orgId);
//		String orgIn = busDispatcherImpl.userOrgIn(ja, condition, retMap, tmd);
		String orgIn = busDispatcherImpl.userOrgIn(tmd);
		sqlStr = "select count(*) emp_num from("
				+ "select a.orgid,a.depid,a.userid,a.name,a.jb_id,a.jb_cat,b.jb_name"
				+ "from t_ntmisc_user a,t_ntmisc_job b"
				+ "where a.jb_cat = b.jb_id and orgid in "
				+ ""
				+ " and jb_name='销售类岗位'"
				+ ")";
		return emp_num;
	}
}
