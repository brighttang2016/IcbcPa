/**
 * 批量业务逻辑处理
 */
package com.icbc.nt.bus;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.nt.util.TransactionMapData;

public class BatBus extends BusParent{
	
	/**
	 * 用户资格认证信息批量插入
	 * @param retMap
	 * @param data
	 */
	public void uctBat(Map retMap,String data){
		JSONArray jsonArray = JSONArray.parseArray(data);
		logger.info("jsonArray.toJSONString():"+jsonArray.toJSONString());
		LinkedList<LinkedHashMap<String,Object>> condList = new LinkedList<LinkedHashMap<String,Object>>();
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject json = jsonArray.getJSONObject(i);
			LinkedHashMap<String, Object> condMap = new LinkedHashMap<String, Object>();
//			LinkedHashMap<String, Object> condMapExit = new LinkedHashMap<String, Object>();
			JSONArray jsonArrayExist = new JSONArray();//已存在用户资格信息查询结果
			condMap.put("userId", json.getString("userId"));
			condMap.put("ctId", json.getString("ctId"));
			condMap.put("operTime", iu.getDate());
			iu.rmCondition(condition);
			iu.putCondition(condition, "userId", json.getString("userId"));
			iu.putCondition(condition, "ctId", json.getString("ctId"));
			sqlStr = "select * from t_ntmisc_userct t where t.userid=? and t.ct_id = ?";
			this.queryManu(jsonArrayExist, condition, sqlStr, daoParent, 1);
			logger.info("jsonArrayExist.size():"+jsonArrayExist.size());
			if(jsonArrayExist.size() == 0){//员工资格认证不存在
				logger.info("员工资格认证 condMap："+condMap);
				condList.add(condMap);
			}
		}
		logger.info("condList:"+condList);
		sqlStr = "insert into t_ntmisc_userct(userid,ct_id,oper_time) values(?,?,?)";
		this.updateBat(condList, daoParent, retMap, sqlStr, 1);
	}
	
	/**
	 * 奖励信息批量录入
	 * @param retMap
	 * @param data
	 */
	public void rpBat(Map retMap,String data){
		LinkedList<LinkedHashMap<String, Object>> condList = null;
		LinkedList<LinkedHashMap<String, Object>> condList2 = null;
		JSONArray jsonArray = null;
		condList = new LinkedList<LinkedHashMap<String,Object>>();
		condList2 = new LinkedList<LinkedHashMap<String,Object>>();
		jsonArray = JSONArray.parseArray(data);
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject json = jsonArray.getJSONObject(i);
			LinkedHashMap<String, Object> hashMap = new LinkedHashMap<String, Object>();
			LinkedHashMap<String, Object> hashMap2 = new LinkedHashMap<String, Object>();
			hashMap.put("userId", json.getString("userId"));
			hashMap.put("rpTime", json.getString("rpTime"));
//			hashMap.put("assptId", json.getString("assptId"));
			hashMap.put("rpPoint", json.getString("rpPoint"));
			hashMap.put("rpMsg", json.getString("rpMsg"));
			hashMap.put("oper_time", iu.getTime());
			hashMap2.put("userId", json.getString("userId"));
			hashMap2.put("ch_time", iu.getDate());
			hashMap2.put("point", json.getString("rpPoint"));
			hashMap2.put("type", "1");
			hashMap2.put("note", "奖励积分");
			hashMap2.put("used", "0");
			condList.add(hashMap);
			condList2.add(hashMap2);
		}
		sqlStr = "insert into t_ntmisc_rphis(user_id,rp_time,rp_point,rp_msg,oper_time) values(?,?,?,?,?)";
//		this.update(condition, daoParent, sqlStr, retMap);
		this.updateBat(condList, daoParent, retMap, sqlStr, 1);
		sqlStr = "insert into t_ntmisc_pointhis t(user_id,chg_time,point,type,note,used) values(?,?,?,?,?,?)";
		this.updateBat(condList2, daoParent, retMap, sqlStr, 1);
	}
}
