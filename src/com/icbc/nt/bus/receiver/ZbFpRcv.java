/**
 * 总包分配 2015-12-17
 */
package com.icbc.nt.bus.receiver;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.directwebremoting.json.types.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.bus.dispatcher.BusDispatcherImpl;
import com.icbc.nt.util.TransactionMapData;

public class ZbFpRcv extends BusParent implements BusReceiver {
	@Autowired
	private BusDispatcherImpl busDispatcherImpl;
	@Override
	public void doWork(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * 对应机构周期记录是否存在
	 * @param orgId
	 * @param zq
	 * @param jbjx_pid
	 * @param jbjx_id
	 * @param khfw
	 * @return
	 */
	public boolean isExist(String orgId,String zq,String jbjx_pid,String jbjx_id,String khfw){
		boolean isExist = false;
		iu.rmCondition(condition);
		iu.putCondition(condition, "orgId", orgId);
		iu.putCondition(condition, "zq", zq);
		iu.putCondition(condition, "jbjx_pid", jbjx_pid);
		iu.putCondition(condition, "jbjx_id", jbjx_id);
		iu.putCondition(condition, "khfw", khfw);
		JSONArray ja = new JSONArray();
		sqlStr = "select * from t_ntmisc_orgzbfp t where orgid=? and zq=? and jbjx_pid=? and jbjx_id=? and khfw=?";
		this.queryManu(ja, condition, sqlStr, daoParent, 1);
		logger.info("机构总包分配表，对应机构、周期、绩效考核父岗位id、绩效考核岗位id、考核范围 记录是否存在，ja:"+ja.toJSONString());
		if(ja.size() > 0)
			isExist = true;
		return isExist;
	}
	public void doWork(TransactionMapData tmd) {
		 Map retMap = new HashMap();
		// TODO Auto-generated method stub
		//当前登录用户
		String currUser = tmd.get("userId").toString();
		tmd.put("currUser", currUser);
//		当前登录用户所在机构
		String orgCurr = busDispatcherImpl.userOrgQuery(tmd);
		//当前登录用户所在分支行号
//		String brOrgId = this.getBrOrgId(orgCurr);
		String brOrgId = busDispatcherImpl.brOrgQuery(orgCurr);
		if("".equals(brOrgId)){
//							brOrgId = "0310000000";//重庆分行
			tmd.put("errorCode", "11");
			tmd.put("finish", "1");
			tmd.put("errorMsg", "非分支行用户,获取分支行号失败!");
			return;
		}
		//分支行号下属所有网点,组成in形式后的结果：('网点1'、'网点2'、'网点3')
		tmd.put("orgIdCurr", brOrgId);
		String orgIn = busDispatcherImpl.userOrgIn(tmd);
		//当前考核周期
		String zqCurr = busDispatcherImpl.zqCurr();
		
		
		iu.rmCondition(condition);
		iu.putCondition(condition, "zq", zqCurr);
		JSONArray zbFpJa = new JSONArray();//机构总包分配表：业务量总包、标准产品总包、支行特色总包、定性总包 求和结果
		sqlStr =  " select a.orgid,a.zq,a.jbjx_pid,a.jbjx_id,a.zb_fp, " 
				+  " b.khfw,b.bl_ywl,b.bl_bzcp,b.bl_zhts,b.bl_dx, " 
				+  " to_char(a.zb_fp*b.bl_ywl,'999999999990.99') zb_ywl, " 
				+  " to_char(a.zb_fp*b.bl_bzcp,'999999999990.99') zb_bzcp, " 
				+  " to_char(a.zb_fp*b.bl_zhts,'999999999990.99') zb_zhts, " 
				+  " to_char(a.zb_fp*b.bl_dx,'999999999990.99') zb_dx " 
				+  " from t_ntmisc_orgzbrs a,t_ntmisc_orgzbbl b " 
				+  " where a.orgid=b.orgid and a.zq=b.zq and a.jbjx_pid=b.jbjx_pid and a.zq=? and a.orgid in "
				+  orgIn ;
		this.queryManu(zbFpJa, condition, sqlStr, daoParent, 1);
		for (int i = 0; i < zbFpJa.size(); i++) {
			JSONObject zbFpJson = zbFpJa.getJSONObject(i);
			String orgId = zbFpJson.getString("orgid").trim();
			String jbjx_pid = zbFpJson.getString("jbjx_pid").trim();
			String jbjx_id = zbFpJson.getString("jbjx_id").trim();
			String khfw = zbFpJson.getString("khfw").trim();
			
			String zb_ywl = zbFpJson.getString("zb_ywl").trim();
			String zb_bzcp = zbFpJson.getString("zb_bzcp").trim();
			String zb_zhts = zbFpJson.getString("zb_zhts").trim();
			String zb_dx = zbFpJson.getString("zb_dx").trim();
			if(this.isExist(orgId, zqCurr, jbjx_pid, jbjx_id, khfw)){
				iu.rmCondition(condition);
				iu.putCondition(condition, "zb_ywl",zb_ywl);
				iu.putCondition(condition, "zb_bzcp",zb_bzcp);
				iu.putCondition(condition, "zb_zhts",zb_zhts);
				iu.putCondition(condition, "zb_dx",zb_dx);
				iu.putCondition(condition, "orgId",orgId);
				iu.putCondition(condition, "zq",zqCurr);
				iu.putCondition(condition, "jbjx_pid",jbjx_pid);
				iu.putCondition(condition, "jbjx_id",jbjx_id);
				iu.putCondition(condition, "khfw",khfw);
				sqlStr = "update t_ntmisc_orgzbfp t set t.zb_ywl=?,t.zb_bzcp=?,t.zb_zhts=?,t.zb_dx=? where t.orgid=? and t.zq=? and t.jbjx_pid=? and t.jbjx_id=? and t.khfw=?";
				logger.info("执行sql，修改机构总包分配表，sqlStr:"+sqlStr+"condition:"+condition);
				this.update(condition, daoParent, sqlStr, retMap);
			}else{
				iu.rmCondition(condition);
				iu.putCondition(condition, "orgId",orgId);
				iu.putCondition(condition, "zq",zqCurr);
				iu.putCondition(condition, "jbjx_pid",jbjx_pid);
				iu.putCondition(condition, "jbjx_id",jbjx_id);
				iu.putCondition(condition, "khfw",khfw);
				iu.putCondition(condition, "zb_ywl",zb_ywl);
				iu.putCondition(condition, "zb_bzcp",zb_bzcp);
				iu.putCondition(condition, "zb_zhts",zb_zhts);
				iu.putCondition(condition, "zb_dx",zb_dx);
				iu.putCondition(condition, "oper_time",iu.getTime());
				sqlStr = "insert into t_ntmisc_orgzbfp(orgid,zq,jbjx_pid,jbjx_id,khfw,zb_ywl,zb_bzcp,zb_zhts,zb_dx,oper_time) values(?,?,?,?,?,?,?,?,?,?)";
				logger.info("执行sql，插入机构总包分配表，sqlStr:"+sqlStr+"condition:"+condition);
				this.update(condition, daoParent, sqlStr, retMap);
			}
			
		}
	}
}
