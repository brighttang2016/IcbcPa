/**
 * 命令接受者接口
 */
package com.icbc.nt.bus.receiver;

import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.util.TransactionMapData;

public interface BusReceiver{
	public void doWork(JSONArray ja,LinkedHashMap<String, Object> condition, Map retMap,TransactionMapData tmd);
}
