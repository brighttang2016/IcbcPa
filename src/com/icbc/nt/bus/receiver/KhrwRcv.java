/**
 * 考核任务管理 2015-12-23
 */
package com.icbc.nt.bus.receiver;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.bus.MediumBus;
import com.icbc.nt.bus.dispatcher.BusDispatcherImpl;
import com.icbc.nt.util.TransactionMapData;
@Transactional
public class KhrwRcv extends BusParent implements BusReceiver {

	@Autowired
	private MediumBus mediumBus;
	@Autowired
	private BusDispatcherImpl busDispatcherImpl;
	
	public void doWork(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap) {
		// TODO Auto-generated method stub
		int txCode = Integer.parseInt(tmd.get("tx_code").toString());
		switch(txCode){
		case 10000077://add
//			1、将往期考核任务使用标识used置：1
			iu.rmCondition(condition);
			sqlStr = "update t_ntmisc_zq set used='1' where used='0'";
			this.update(condition, daoParent, sqlStr, retMap);
//			2、插入新考核周期任务
			iu.rmCondition(condition);
			iu.putCondition(condition, "zq", tmd.get("zq"));
			iu.putCondition(condition, "timeStart", iu.timeFormate(tmd.get("timeStart").toString(), "yyyy-MM-dd", "yyyyMMdd"));
			iu.putCondition(condition, "timeEnd", iu.timeFormate(tmd.get("timeEnd").toString(), "yyyy-MM-dd", "yyyyMMdd"));
			iu.putCondition(condition, "operTime", iu.getTime());
			iu.putCondition(condition, "used", "0");
			sqlStr = "insert into t_ntmisc_zq(zq,time_start,time_end,oper_time,used) values(?,?,?,?,?)";
			this.update(condition, daoParent, sqlStr, retMap);
			
			
			break;
		case 10000075://del
			String data = tmd.get("data").toString();
			LinkedList<LinkedHashMap<String,Object>> condList = new LinkedList<LinkedHashMap<String,Object>>();
			JSONArray jsonArray = JSONArray.parseArray(data);
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject json = jsonArray.getJSONObject(i);
				LinkedHashMap<String, Object> hashMap = new LinkedHashMap<String, Object>();
				hashMap.put("zq", json.getString("zq"));
				hashMap.put("used", '0');
				condList.add(hashMap);
			}
			sqlStr = "delete t_ntmisc_zq where zq=? and used=?";
			logger.info("执行sql,删除考核周期,sqlStr:"+sqlStr+"condList:"+condList.toString());
			this.updateBat(condList, daoParent, retMap, sqlStr, 1);
			if("0".equals(retMap.get("recUpdCount"))){
				tmd.put("errorMsg", "删除失败");
			}
			break;
		case 10000079://query
			ja.clear();
			iu.rmCondition(condition);
			//初始sql
			sqlStr = " select t.zq,t.used, " 
					+  " to_char(to_date(t.time_start,'yyyymmdd'),'yyyy-mm-dd') time_start, " 
					+  " to_char(to_date(t.time_end,'yyyymmdd'),'yyyy-mm-dd') time_end, " 
					+  " to_char(to_date(t.oper_time ,'yyyy-mm-dd hh24:mi:ss'),'yyyy-mm-dd hh24:mi:ss') oper_time " 
					+  " from t_ntmisc_zq t order by to_number(t.oper_time) desc ";
//			iu.putCondition(condition, "userid", tmd.get("userId"));
//			sqlStr = mediumBus.putOrgCond(sqlStr, tmd, condition);
			logger.info("执行sql,任务周期查询，sqlStr："+sqlStr+"---------condition:"+condition);
			tmd.put("count",this.count(condition, daoParent, sqlStr, 1));//总记录数入变量池
			iu.putCondition(condition, "start", tmd.get("start"));
			iu.putCondition(condition, "end", tmd.get("end"));
			logger.info(tmd.get("start")+"|"+tmd.get("end"));
			this.queryAuto(ja, condition, sqlStr, daoParent, 2);
			
			break;
		}
	}

	@Override
	public void doWork(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub
		
	}

	/*@Override
	public void doWork(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub
		
	}*/
}
