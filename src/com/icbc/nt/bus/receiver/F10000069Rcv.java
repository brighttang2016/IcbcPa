/**
 * 人员分配比例导入结果查询 2015-12-13
 */
package com.icbc.nt.bus.receiver;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.bus.MediumBus;
import com.icbc.nt.util.TransactionMapData;

public class F10000069Rcv extends BusParent implements BusReceiver{
	@Autowired
	private MediumBus mediumBus;
	@Override
	public void doWork(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		String orgIn = tmd.get("orgIn").toString();//("机构号1","机构号2","机构号3","机构号4")
		sqlStr =  " select t.orgid,t.orgid depid,t.zq,b.orgname, " 
				+  " to_char(sum(case when t.khfw='1' and t.jbjx_pid='1' then t.bl_ywl else '0' end),'fm990.99') as bl_zhgy_ywl, " 
				+  " to_char(sum(case when t.khfw='1' and t.jbjx_pid='1' then t.bl_bzcp else '0' end),'fm990.90') as bl_zhgy_bzcp, " 
				+  " to_char(sum(case when t.khfw='1' and t.jbjx_pid='1' then t.bl_zhts else '0' end),'fm990.90') as bl_zhgy_zhts, " 
				+  " to_char(sum(case when t.khfw='1' and t.jbjx_pid='1' then t.bl_dx  else '0' end),'fm990.90') as bl_zhgy_dx, " 
				+  " to_char(sum(case when t.khfw='1' and t.jbjx_pid='2' then t.bl_bzcp else '0' end),'fm990.90') as bl_zhkhjl_bzcp, " 
				+  " to_char(sum(case when t.khfw='1' and t.jbjx_pid='2' then t.bl_zhts  else '0' end),'fm990.90') as bl_zhkhjl_zhts, " 
				+  " to_char(sum(case when t.khfw='1' and t.jbjx_pid='2' then t.bl_dx  else '0' end),'fm990.90') as bl_zhkhjl_dx, " 
				+  " to_char(sum(case when t.khfw='2' and t.jbjx_pid='1' then t.bl_ywl  else '0' end),'fm990.90') as bl_wdgy_ywl, " 
				+  " to_char(sum(case when t.khfw='2' and t.jbjx_pid='1' then t.bl_bzcp  else '0' end),'fm990.90') as bl_wdgy_bzcp, " 
				+  " to_char(sum(case when t.khfw='2' and t.jbjx_pid='1' then t.bl_zhts  else '0' end),'fm990.90') as bl_wdgy_zhts, " 
				+  " to_char(sum(case when t.khfw='2' and t.jbjx_pid='1' then t.bl_dx else '0' end),'fm990.90') as bl_wdgy_dx, " 
				+  " to_char(sum(case when t.khfw='2' and t.jbjx_pid='2' then t.bl_bzcp else '0' end),'fm990.90') as bl_wdkhjl_bzcp, " 
				+  " to_char(sum(case when t.khfw='2' and t.jbjx_pid='2' then t.bl_zhts else '0' end),'fm990.90') as bl_wdkhjl_zhts, " 
				+  " to_char(sum(case when t.khfw='2' and t.jbjx_pid='2' then t.bl_dx else '0' end),'fm990.90') as bl_wdkhjl_dx " 
				+  " from t_ntmisc_orgzbbl t,t_ntmisc_org b " 
				+  " where t.orgid = b.orgid and t.orgid in "  
				+ orgIn
				+  " group by  t.orgid,t.zq,b.orgname ";
		sqlStr = mediumBus.putOrgCond(sqlStr, tmd,condition);//设置机构部门查询条件
		//查询总条数
		iu.putCondition(condition, "userid", tmd.get("userIdQuery"));//设置用户编号查询条件
		tmd.put("count",this.count(condition, daoParent, sqlStr, 1));//总记录数入变量池
		//查询表格
		iu.putCondition(condition, "start", tmd.get("start"));
		iu.putCondition(condition, "end", tmd.get("end"));
		logger.info("执行sql，人员分配比例导入结果 ，sqlStr："+sqlStr+"****condition:"+condition);
		this.queryAuto(ja, condition, sqlStr, daoParent, 2);//分页查询
		logger.info("执行sql，人员分配比例导入结果 ja:"+ja);
	}
}
