/**
 * 当期绩效查询 2015-12-24
 */
package com.icbc.nt.bus.receiver;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.bus.MediumBus;
import com.icbc.nt.util.TransactionMapData;

public class F10000071Rcv extends BusParent implements BusReceiver {
	@Autowired
	private MediumBus mediumBus;
	/**
	 * 绩效考核计算结果查询
	 * @param tmd
	 * @param ja
	 */
	public void jxCalcQuery(TransactionMapData tmd,JSONArray ja){
		ArrayList zqArray = (ArrayList) tmd.get("zqArray");
//		String zq = tmd.get("zqCurr").toString();
		String zq = tmd.get("zqQuery").toString();
		String orgIn = tmd.get("orgIn").toString();
//		LinkedList<String> table = new LinkedList<String>();//文件数据源
		iu.rmCondition(condition);
		iu.putCondition(condition, "zq", zq);
		sqlStr =  " select a.userid,a.name,a.orgid,a.depid,b.orgname,c.depname,d.jbjx_pid,d.jbjx_pname,d.jbjx_id,d.jbjx_name, " 
				+  " e.zq,e.jx_ywl_zh,e.jx_bzcp_zh,e.jx_tscp_zh,e.jx_dx_zh,e.jx_ywl_wd,e.jx_bzcp_wd,e.jx_tscp_wd,e.jx_dx_wd, " 
				+  " e.zsf_ywl_zh,e.zsf_bzcp_zh,e.zsf_tscp_zh,e.zsf_dx_zh,e.zsf_ywl_wd,e.zsf_bzcp_wd,e.zsf_tscp_wd,e.zsf_dx_wd " 
				+  " from t_ntmisc_user a ,t_ntmisc_org b,t_ntmisc_dept c,t_ntmisc_jobjx d,t_ntmisc_userjx e " 
				+  " where a.orgid=b.orgid and a.depid=c.depid and a.jbjx_id=d.jbjx_id and a.userid=e.user_id " 
				+  " and a.orgid in "
				+  orgIn ;
		sqlStr = mediumBus.putOrgCond(sqlStr, tmd, condition);
		tmd.put("count",this.count(condition, daoParent, sqlStr, 1));//总记录数入变量池
		iu.putCondition(condition, "start", tmd.get("start"));
		iu.putCondition(condition, "end", tmd.get("end"));
		logger.info("执行sql，绩效查询,sqlStr:"+sqlStr+"-----condition:"+condition);
		this.queryAuto(ja, condition, sqlStr, daoParent, 1);
		
	}
	@Override
	public void doWork(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub
		this.jxCalcQuery(tmd, ja);
	}
}
