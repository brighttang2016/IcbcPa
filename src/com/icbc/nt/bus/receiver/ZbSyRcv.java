/**
 * 剩余总包 2015-12-17
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

public class ZbSyRcv extends BusParent implements BusReceiver {
	@Autowired
	private BusDispatcherImpl busDispatcherImpl;
	@Override
	public void doWork(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub

	}
	/**
	 * 对应机构周期记录是否存在
	 * @param sqlStr
	 * @param orgId
	 * @param zq
	 * @return
	 */
	public boolean isExist(String sqlStr,String orgId,String zq){
		boolean isExist = false;
		iu.rmCondition(condition);
		iu.putCondition(condition, "orgId", orgId);
		iu.putCondition(condition, "zq", zq);
		JSONArray ja = new JSONArray();
//		sqlStr = "select * from t_ntmisc_orgzbsy t where orgid=? and zq=?";
		this.queryManu(ja, condition, sqlStr, daoParent, 1);
		logger.info("机构总包剩余表，对应机构周期记录是否存在，ja:"+ja.toJSONString());
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
		JSONArray zbSySumJa = new JSONArray();//机构当期剩余总包求和结果
		sqlStr =  " select t.orgid,t.zq,sum(zb_sy) zb_sy_sum from t_ntmisc_orgzbrs t " 
				+  " where t.zq=? and orgid in  " 
				+  orgIn
				+  " group by t.orgid,t.zq " ;
		this.queryManu(zbSySumJa, condition, sqlStr, daoParent, 1);
		for (int i = 0; i < zbSySumJa.size(); i++) {
			JSONObject zbSySumJson = zbSySumJa.getJSONObject(i);
			String orgId = zbSySumJson.getString("orgid");
			float zbSySum = zbSySumJson.getFloat("zb_sy_sum");//总包人数表，当期剩余总包总和
			float zbSyBefore = 0;//当期之前剩余总包
			float zbSyAfter = 0;
			//查找前一期机构剩余总包
			JSONArray zbSyJa = new JSONArray();//机构剩余总包
			iu.rmCondition(condition);
			iu.putCondition(condition, "orgid", orgId);
			sqlStr = "select * from t_ntmisc_orgzbsy t where orgid=? order by to_number(oper_time) desc";
			this.queryManu(zbSyJa, condition, sqlStr, daoParent, 1);
			try {
				zbSyBefore = zbSyJa.getJSONObject(0).getFloatValue("zbsy_before");
			} catch (Exception e) {
				logger.info("机构："+orgId+",周期："+zqCurr+"机构剩余总包表无【当期之前剩余总包】");
			}
			zbSyAfter = zbSyBefore + zbSySum;
			logger.info("机构："+orgId+",周期："+zqCurr+",当期之后剩余总包："+zbSyAfter);
			
			sqlStr = "select * from t_ntmisc_orgzbsy t where orgid=? and zq=?";
			if(this.isExist(sqlStr,orgId, zqCurr)){
				iu.rmCondition(condition);
				iu.putCondition(condition, "zbsy_after", zbSyAfter+"");
				iu.putCondition(condition, "oper_time", iu.getTime());
				iu.putCondition(condition, "orgid", orgId);
				iu.putCondition(condition, "zq", zqCurr);
				sqlStr = "update t_ntmisc_orgzbsy set zbsy_after=?,oper_time=? where orgid=? and zq=?";
				logger.info("111111111111111sqlStr:"+sqlStr+"condition:"+condition);
				this.update(condition, daoParent, sqlStr, retMap);
			}else{
				iu.rmCondition(condition);
				iu.putCondition(condition, "orgid", orgId);
				iu.putCondition(condition, "zq", zqCurr);
				iu.putCondition(condition, "zbsy_chg", zbSySum);
				iu.putCondition(condition, "flag", "1");
				iu.putCondition(condition, "zbsy_before", zbSyBefore+"");
				iu.putCondition(condition, "zbsy_after", zbSyAfter+"");
				iu.putCondition(condition, "oper_time", iu.getTime());
				sqlStr = "insert into t_ntmisc_orgzbsy(orgid,zq,zbsy_chg,flag,zbsy_before,zbsy_after,oper_time) values(?,?,?,?,?,?,?)";
				this.update(condition, daoParent, sqlStr, retMap);
			}
		}
	}
}
