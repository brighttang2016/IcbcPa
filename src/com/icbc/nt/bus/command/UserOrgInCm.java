/**
 * 根据用户机构号获取用户所见所有机构号 2015-12-06
 */
package com.icbc.nt.bus.command;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bus.receiver.UserOrgInRcv;
import com.icbc.nt.util.TransactionMapData;

public class UserOrgInCm extends CmParemt implements BusCommand {
	@Autowired
	private UserOrgInRcv userOrgInRcv;
	@Override
	public void execute(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub
		userOrgInRcv.doWork(ja, condition, retMap, tmd);
	}
	
	public void execute(TransactionMapData tmd) {
		userOrgInRcv.doWork(tmd);
	}
}
