/**
 * 用户绩效初始化数据
 */
package com.icbc.nt.bus.receiver;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.util.TransactionMapData;

public class UserJxInitRcv extends BusParent implements BusReceiver {

	@Override
	public void doWork(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub
		JSONArray userJxJa = (JSONArray) tmd.get("userJxJa");//用户绩效表
		LinkedList<LinkedHashMap<String, Object>> condAddList = (LinkedList<LinkedHashMap<String, Object>>) tmd.get("condAddList");//用户绩效表待添加记录
		boolean existFlag = false;
		JSONObject json = (JSONObject) tmd.get("excelRowJson");//excel表行记录
//		LinkedHashMap<String, Object> condAddMap = (LinkedHashMap<String, Object>) tmd.get("condAddMap");
		LinkedHashMap<String, Object> condAddMap = new LinkedHashMap<String, Object>();
		for (int j = 0; j < userJxJa.size(); j++) {
			JSONObject userJxJson = userJxJa.getJSONObject(j);
			if(json.getString("userId").equals(userJxJson.getString("user_id")) && 
					json.getString("zq").equals(userJxJson.getString("zq"))){
				existFlag = true;
			}
		}
		if(!existFlag){
			boolean isExistFlag = false;
			for (int j = 0; j < condAddList.size(); j++) {
				LinkedHashMap<String, Object> condAddMapExist = condAddList.get(j);
				if(json.getString("userId").equals(condAddMapExist.get("userId")) && json.getString("zq").equals(condAddMapExist.get("zq")) ){
					isExistFlag = true;
					logger.info("condAddMap中userid 和zq对应记录已存在");
				}
			}
			if(!isExistFlag){
				condAddMap.put("userId", json.getString("userId"));
				condAddMap.put("zq", json.getString("zq"));
				condAddList.add(condAddMap);
			}
		}
	}
}
