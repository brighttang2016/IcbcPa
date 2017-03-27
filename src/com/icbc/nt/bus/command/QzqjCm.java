package com.icbc.nt.bus.command;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bus.receiver.QzqjRcv;
import com.icbc.nt.util.TransactionMapData;

public class QzqjCm extends CmParemt implements BusCommand {
	@Autowired
	private QzqjRcv qzqjRcv;
	
	@Override
	public void execute(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub
		qzqjRcv.doWork(ja, condition, retMap, tmd);
	}
}
