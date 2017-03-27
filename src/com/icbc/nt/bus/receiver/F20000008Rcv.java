/**
 * 绩效查询（新版）2016-01-08
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
import com.icbc.nt.bus.dispatcher.BusDispatcherImpl;
import com.icbc.nt.util.TransactionMapData;

public class F20000008Rcv extends BusParent implements BusReceiver {
	@Autowired
	private MediumBus mediumBus;
	@Autowired
	private BusDispatcherImpl busDispatcherImpl;
	/**
	 * 绩效查询结果数据处理
	 * @param ja 绩效查询结果
	 */
	public void jxDataHandle(JSONArray ja){
		for (int i = 0; i < ja.size(); i++) {
			JSONObject json = ja.getJSONObject(i);
			String jbjx_pid = json.getString("jbjx_pid");
//			jbjx_pid：1：柜员 2：客户经理
			if("1".equals(jbjx_pid)){
				String jx_qyyxjl = json.getString("jx_qyyxjl");//全员营销奖励（柜员全员营销奖励回归到网点汇总后，再在网点内部柜员中手动分配产生手动分配绩效）
				json.put("jx_qyyxjl", "0");
			}
			float jx_sum = json.getFloat("jx_gwlz")+json.getFloat("jx_wdzykh")+json.getFloat("jx_qyyxjl")+json.getFloat("jx_wdsdfp");
			json.put("jx_sum",jx_sum+"" );//设置用户总绩效
		}
		
	}
	
	public boolean isFzhRoleExist(TransactionMapData tmd){
		JSONArray ja = new JSONArray();
		boolean isExist = false;
		String currUser = tmd.get("currUser").toString();//当前登录用户
		iu.rmCondition(condition);
		sqlStr = " select * from t_ntmisc_userrole a where a.role_id='21' ";
		logger.info("用户权限查询");
		iu.infoDbOper("用户权限查询", sqlStr, condition);
		this.queryAuto(ja, condition, sqlStr, daoParent, 1);
		logger.debug("是否具有分支行管理员权限查询结果："+ja.toJSONString());
		for (int i = 0; i < ja.size(); i++) {
			JSONObject json = ja.getJSONObject(i);
			if(json.getString("user_id").equals(currUser)){
				isExist = true;
			}
		}
		logger.debug("currUser|isExist:"+currUser+"|"+isExist);
		return isExist;
	}
	
	/**
	 * 绩效查询（新版）
	 * @param tmd
	 * @param ja
	 */
	public void jxCalcQuery(TransactionMapData tmd,JSONArray ja){
//		String orgIn = tmd.get("orgIn").toString();
		String orgIdQuery = "";//查询机构
		String userId = tmd.get("userId").toString();
		tmd.put("currUser", userId);
		String orgIdTemp = busDispatcherImpl.userOrgQuery(tmd);//当前用户所在机构
//		orgIdCurr = orgIdTemp;
		if(this.isFzhRoleExist(tmd)){
			orgIdQuery = busDispatcherImpl.brOrgQuery(orgIdTemp);//分支行号
		}else{
			orgIdQuery = orgIdTemp;
		}
		
		tmd.put("orgIdCurr", orgIdQuery);
//		String orgIn = busDispatcherImpl.userOrgIn(tmd);
		String orgIn = tmd.get("orgIdIn").toString();
//		String zqCurr = tmd.get("zqCurr").toString();
//		String zqQuery = tmd.get("zqQuery").toString();
		iu.rmCondition(condition);
		iu.putCondition(condition, "zq", tmd.get("zqQuery"));
		sqlStr = "  select a.*,b.zq,b.jx_gwlz,b.jx_wdzykh,b.jx_wdsdfp,b.jx_qyyxjl   "
				+ " from (select a.userid,a.name,a.orgid,a.depid,a.jbjx_id,b.orgname,c.depname,d.jbjx_pid,d.jbjx_name,d.jbjx_pname  from t_ntmisc_user a,t_ntmisc_org b,t_ntmisc_dept c,t_ntmisc_jobjx d "
				+ " where a.orgid = b.orgid and a.depid = c.depid and a.jbjx_id = d.jbjx_id) a, "
				+ " (select t.* from ( "
				+ " select t.userid,t.zq, "
				+ " to_char(sum(case when t.nr_id='1' or t.nr_id='2'  then t.jx else '0' end),'9999999990.90') as jx_gwlz, "
				+ " to_char(sum(case when t.nr_id='3' then t.jx else '0' end),'9999999990.90') as jx_wdzykh, "
				+ " to_char(sum(case when t.nr_id='4' then t.jx else '0' end),'9999999990.90') as jx_wdsdfp, "
				+ " to_char(sum(case when t.nr_id='5' then t.jx else '0' end),'9999999990.90') as jx_qyyxjl "
				+ " from t_ntmisc_userjxn t "
				+ " group by t.userid,t.zq "
				+ " ) t ) b "
				+ " where a.userid = b.userid and a.orgid in "
				+ orgIn
				+ " order by b.zq desc,a.orgid asc,a.userid asc  ";
		tmd.tmdLogger();
		sqlStr = mediumBus.putOrgCond(sqlStr, tmd, condition);
		tmd.put("count",this.count(condition, daoParent, sqlStr, 1));//总记录数入变量池
		iu.putCondition(condition, "start", tmd.get("start"));
		iu.putCondition(condition, "end", tmd.get("end"));
//		logger.info("执行sql，绩效查询,sqlStr:"+sqlStr+"-----condition:"+condition);
		logger.info("绩效查询（新版）");
		iu.infoDbOper("绩效查询（新版）", sqlStr, condition);
		this.queryAuto(ja, condition, sqlStr, daoParent, 1);
		logger.debug("绩效查询结果,ja："+ja.toJSONString());
	}
	@Override
	public void doWork(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub
		this.jxCalcQuery(tmd, ja);
		this.jxDataHandle(ja);
	}
}
