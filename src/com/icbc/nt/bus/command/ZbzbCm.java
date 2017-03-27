package com.icbc.nt.bus.command;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bus.receiver.KhrwRcv;
import com.icbc.nt.bus.receiver.ZbzbRcv;
import com.icbc.nt.util.TransactionMapData;

public class ZbzbCm extends CmParemt implements BusCommand {
	@Autowired
	private ZbzbRcv zbzbRcv;
	
	public void execute(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap) {
		// TODO Auto-generated method stub
		zbzbRcv.doWork(ja, condition, retMap);
	}

	@Override
	public void execute(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub
		
	}
}
