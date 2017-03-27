/**
 * 机构所在分支行号查询
 */
package com.icbc.nt.bus.command;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bus.receiver.BrOrgQueryRcv;
import com.icbc.nt.util.TransactionMapData;

public class BrOrgQueryCm  extends CmParemt implements BusCommand{
	@Autowired
	private BrOrgQueryRcv brOrgQueryRcv;
	@Override
	public void execute(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub

	}
	public String execute(String orgId) {
		return brOrgQueryRcv.doWork(orgId);
	}

}
