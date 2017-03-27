/**
 * 机构各种人数查询服务 2015-12-14
 */
package com.icbc.nt.bus.command;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bus.receiver.OrgRsQueryRcv;
import com.icbc.nt.util.TransactionMapData;

public class OrgRsQueryCm extends CmParemt implements BusCommand {
	@Autowired
	private OrgRsQueryRcv orgRsQueryRcv;
	@Override
	public void execute(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub

	}
	public void empNumJbjx(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		orgRsQueryRcv.empNumJbjx(ja, condition, retMap, tmd);
	}
	public void empNumOrg(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		orgRsQueryRcv.empNumOrg(ja, condition, retMap, tmd);
	}

}
