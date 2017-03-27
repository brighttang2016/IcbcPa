/**
 * 总包占比管理 2016-01-21
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
public class ZbzbRcv extends BusParent implements BusReceiver {

	@Autowired
	private MediumBus mediumBus;
	@Autowired
	private BusDispatcherImpl busDispatcherImpl;
	
	public void doWork(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap) {
		// TODO Auto-generated method stub
		//int txCode = Integer.parseInt(tmd.get("txCode").toString());
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
		case 20000015://update
			ja.clear();
			iu.rmCondition(condition);
			iu.putCondition(condition, "min", tmd.get("min"));
			iu.putCondition(condition, "max", tmd.get("max"));
			iu.putCondition(condition, "blId", tmd.get("bl_id"));
			//初始sql
			sqlStr = " update t_ntmisc_khbl t set min=? ,max=? where bl_id=?";
			iu.infoDbOper("总包占比修改", sqlStr, condition);
			this.update(condition, daoParent, sqlStr, retMap);
			break;
		case 20000016://query
			ja.clear();
			iu.rmCondition(condition);
			//初始sql
			sqlStr = "select a.*,b.nr_name from t_ntmisc_khbl a ,t_ntmisc_khnr b  where a.nr_id = b.nr_id";
			tmd.put("count",this.count(condition, daoParent, sqlStr, 1));//总记录数入变量池
			iu.putCondition(condition, "start", tmd.get("start"));
			iu.putCondition(condition, "end", tmd.get("end"));
//			logger.info(tmd.get("start")+"|"+tmd.get("end"));
			iu.infoDbOper("总包占比查询", sqlStr, condition);
			this.queryAuto(ja, condition, sqlStr, daoParent, 2);
			break;
		}
	}

	@Override
	public void doWork(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub
		
	}
}
