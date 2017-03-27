/**
 * 当前考核周期2015-12-24
 */
package com.icbc.nt.bus.receiver;

import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.util.TransactionMapData;

public class ZqCurrRcv extends BusParent implements BusReceiver {

	@Override
	public void doWork(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
	}

	public String  doWork() {
		logger.info("查询当前考核周期");
		String zq = "";
		JSONArray ja = new JSONArray();
		sqlStr = "select zq from t_ntmisc_zq where used = '0' order by to_number(oper_time) desc";
		iu.rmCondition(condition);
		this.queryAuto(ja, condition, sqlStr, daoParent, 1);
		if(ja.size() >= 2){
			logger.error("考核周期查询------------>当前考核周期不唯一");
		}
		if(ja.size() > 0){
			zq = ja.getJSONObject(0).getString("zq").trim();
		}
		if("".equals(zq)){
			logger.error("考核周期查询------------>获取的周期为空，获取周期失败");
		}
		tmd.put("zqCurr", zq);//当前考核周期如变量池
		return  zq;
	}
}
