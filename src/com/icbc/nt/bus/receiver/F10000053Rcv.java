/**
 * 机构分配数据导入结果查询 2015-12-10
 */
package com.icbc.nt.bus.receiver;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.bus.MediumBus;
import com.icbc.nt.util.TransactionMapData;

public class F10000053Rcv extends BusParent implements BusReceiver{
	@Autowired
	private MediumBus mediumBus;
	@Override
	public void doWork(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		
		String orgIn = tmd.get("orgIn").toString();//("机构号1","机构号2","机构号3","机构号4")
//		iu.rmCondition(condition);
		sqlStr =  " select a.zb_init,a.bl_mova ,b.*,c.orgname from t_ntmisc_orgzbrj a, " 
				+  " (select orgid,zq, " 
				+  " to_char(sum(case when t.jbjx_id='1' then t.xs_ry else '0' end),'FM990.90') as xs_gy, " 
				+  " to_char(sum(case when t.jbjx_id='3' then t.xs_ry else '0' end),'FM990.90') as xs_khjl, " 
				+  " to_char(sum(case when t.jbjx_id='1' then t.xs_bl else '0' end),'FM990.90') as xsbl_dg, " 
				+  " to_char(sum(case when t.jbjx_id='2' then t.xs_bl else '0' end),'FM990.90') as xsbl_gg, " 
				+  " to_char(sum(case when t.jbjx_id='3' then t.xs_bl else '0' end),'FM990.90') as xsbl_dgkhjl, " 
				+  " to_char(sum(case when t.jbjx_id='4' then t.xs_bl else '0' end),'FM990.90') as xsbl_xskhjl, " 
				+  " to_char(sum(case when t.jbjx_id='5' then t.xs_bl else '0' end),'FM990.90') as xsbl_dtkhjl " 
				+  " from t_ntmisc_orgzbxs t " 
				+  " group by orgid,zq)b,t_ntmisc_org c " 
				+  " where a.orgid = b.orgid and a.zq = b.zq and a.orgid = c.orgid and a.orgid in "
				+ orgIn;
		
		sqlStr = mediumBus.putOrgCond(sqlStr, tmd,condition);//设置机构部门查询条件
		//查询总条数
		iu.putCondition(condition, "userid", tmd.get("userIdQuery"));//设置用户编号查询条件
		tmd.put("count",this.count(condition, daoParent, sqlStr, 1));//总记录数入变量池
		//查询表格
		iu.putCondition(condition, "start", tmd.get("start"));
		iu.putCondition(condition, "end", tmd.get("end"));
		logger.info("机构分配数据导入结果查询");
		iu.infoDbOper("机构分配数据导入结果查询", sqlStr, condition);
		this.queryAuto(ja, condition, sqlStr, daoParent, 2);//分页查询
	}
}
