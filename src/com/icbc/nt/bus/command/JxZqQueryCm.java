/**
 * 绩效周期查询 2015-12-07
 */
package com.icbc.nt.bus.command;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bus.receiver.JxZqQueryRcv;
import com.icbc.nt.util.TransactionMapData;

public class JxZqQueryCm extends CmParemt implements BusCommand {
	@Autowired
	private JxZqQueryRcv jxZqQueryRcv;
	@Override
	public void execute(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub
		jxZqQueryRcv.doWork(ja, condition, retMap, tmd);
	}
}
