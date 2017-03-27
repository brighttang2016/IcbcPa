/**
 * 机构下属网点查询 2015-11-24
 */
package com.icbc.nt.bus.command;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bus.receiver.WdQueryRcv;
import com.icbc.nt.util.TransactionMapData;

public class WdQueryCm extends CmParemt implements BusCommand {
	@Autowired
	private WdQueryRcv wdQueryRcv;
	@Override
	public void execute(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub
		wdQueryRcv.doWork(ja, condition, retMap, tmd);
	}
}
