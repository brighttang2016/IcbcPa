package com.icbc.nt.bus.command;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.bus.receiver.BusReceiver;
import com.icbc.nt.bus.receiver.ZbQueryRec;
import com.icbc.nt.util.TransactionMapData;

public class JxCalcCm extends BusParent implements BusCommand{
	@Autowired
	private BusReceiver jxCalcRcv;
	private JSONArray ja;
	private LinkedHashMap<String, Object> condition;
	private Map retMap;
	private TransactionMapData tmd;

	@Override
	public void execute(JSONArray ja,
			LinkedHashMap<String, Object> condition, Map retMap,
			TransactionMapData tmd) {
		jxCalcRcv.doWork(ja, condition, retMap, tmd);
	}
}
