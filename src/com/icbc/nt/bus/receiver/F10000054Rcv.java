/**
 * 机构分配结果查询 2015-12-28
 */
package com.icbc.nt.bus.receiver;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.bus.MediumBus;
import com.icbc.nt.util.TransactionMapData;

public class F10000054Rcv extends BusParent implements BusReceiver{
	@Autowired
	private MediumBus mediumBus;
	@Override
	public void doWork(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		
		String orgIn = tmd.get("orgIn").toString();//("机构号1","机构号2","机构号3","机构号4")
//		iu.rmCondition(condition);
		sqlStr =  " select a.orgid,a.orgid depid,a.zq,b.orgname,a.jbjx_pid,a.jbjx_id,a.rs_gw,rs_kh,rs_kh_zh,a.zb_kh,a.zb_sy,a.zb_fp,c.jbjx_name,c.jbjx_pname " 
				+  " from t_ntmisc_orgzbrs a,t_ntmisc_org b,t_ntmisc_jobjx c where a.orgid = b.orgid and a.jbjx_id = c.jbjx_id and a.orgid in "
				+ orgIn ;

		sqlStr = mediumBus.putOrgCond(sqlStr, tmd,condition);//设置机构部门查询条件
		//查询总条数
//		iu.putCondition(condition, "userid", tmd.get("userIdQuery"));//设置用户编号查询条件
		tmd.put("count",this.count(condition, daoParent, sqlStr, 1));//总记录数入变量池
		//查询表格
		iu.putCondition(condition, "start", tmd.get("start"));
		iu.putCondition(condition, "end", tmd.get("end"));
		logger.info("执行sql，机构分配数据导入结果查询 ，sqlStr："+sqlStr+"****condition:"+condition);
		this.queryAuto(ja, condition, sqlStr, daoParent, 2);//分页查询
	}
}
