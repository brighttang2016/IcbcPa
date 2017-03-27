/**
 * 用户所属机构查询命令类2015-12-04
 */
package com.icbc.nt.bus.command;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bus.receiver.UserOrgQueryRcv;
import com.icbc.nt.util.TransactionMapData;
@Service
public class UserOrgQueryCm extends CmParemt implements BusCommand {
	@Autowired
	private UserOrgQueryRcv userOrgQueryRcv;
	@Override
	public void execute(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub
	}
	public void execute(TransactionMapData tmd) {
		userOrgQueryRcv.doWork(tmd);
	}
}
