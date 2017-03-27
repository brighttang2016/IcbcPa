/**
 * 指定机构下属网点查询 2015-11-24
 * 最初手动录入机构下属所有网点总包时使用，后改为批量导入后，此方法废除，后期备用
 */
package com.icbc.nt.bus.receiver;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.bus.MediumBus;
import com.icbc.nt.util.TransactionMapData;

@Transactional
@Service
public class WdQueryRcv extends BusParent implements BusReceiver {
	@Autowired
	private MediumBus mediumBus;
	/**
	 * 当前机构下属网点查询
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 * @return
	 */
	public Object wdQuery(JSONArray ja,
			LinkedHashMap<String, Object> condition, Map retMap,
			TransactionMapData tmd){
		
		HashMap<String,String> colNameMap = new HashMap<String, String>();
		iu.rmCondition(condition);
		JSONArray visibleJa = new JSONArray();
		
		String userId = tmd.get("userId").toString();
		String orgId = mediumBus.getUserOrg(userId);
		String orgIn = mediumBus.getOrgIn(orgId);
		sqlStr = "select orgid,orgname from t_ntmisc_org where orgid in "
				+ orgIn;
		this.queryAuto(visibleJa, condition, sqlStr, daoParent, 1);
		ja.clear();
		for (int i = 0; i < visibleJa.size(); i++) {
			JSONObject json = visibleJa.getJSONObject(i);
			JSONArray childJa = new JSONArray();
			logger.info("当前循环json："+json);
			String orgIdTemp = json.getString("orgid");
			logger.info("orgIdTemp:"+orgIdTemp);
			sqlStr = "select * from t_ntmisc_org where porgid = "+orgIdTemp;
			this.queryAuto(childJa, condition, sqlStr, daoParent, 1);
			logger.info("childJa.size():"+childJa.size());
			if(childJa.size() == 0){
				ja.add(json);
				logger.info("找到网点："+json);
			}
		}
		return ja;
	}
	
	@Override
	public void doWork(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub
		this.wdQuery(ja, condition, retMap, tmd);
	}
}
