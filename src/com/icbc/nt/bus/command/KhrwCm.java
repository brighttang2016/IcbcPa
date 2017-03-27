package com.icbc.nt.bus.command;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bus.receiver.KhrwRcv;
import com.icbc.nt.util.TransactionMapData;

public class KhrwCm extends CmParemt implements BusCommand {
	@Autowired
	private KhrwRcv khrwRcv;
	
	public void execute(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap) {
		// TODO Auto-generated method stub
		khrwRcv.doWork(ja, condition, retMap);
	}
	@Override
	public void execute(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub
		
	}
}
