package com.icbc.nt.bus.command;

import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.util.TransactionMapData;

public interface BusCommand{
	public void execute(JSONArray ja,
			LinkedHashMap<String, Object> condition, Map retMap,
			TransactionMapData tmd);
}
