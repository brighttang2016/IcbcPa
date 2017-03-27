/**
 * 机构树查询命令类
 */
package com.icbc.nt.bus.command;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bus.receiver.OrgTreeQueryRcv;
import com.icbc.nt.util.TransactionMapData;

public class OrgTreeQueryCm extends CmParemt implements BusCommand {
	@Autowired
	private OrgTreeQueryRcv orgTreeQueryRcv;
	@Override
	public void execute(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub
		orgTreeQueryRcv.doWork(ja, condition, retMap, tmd);
	}
}
