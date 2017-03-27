
package com.icbc.nt.bus.command;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.bus.receiver.BusReceiver;
import com.icbc.nt.bus.receiver.ZbQueryRec;
import com.icbc.nt.util.TransactionMapData;

public class ExlUserValidateCm extends BusParent implements BusCommand{
	@Autowired
	private BusReceiver exlUserValidateRcv;
	private JSONArray ja;
	private LinkedHashMap<String, Object> condition;
	private Map retMap;
	private TransactionMapData tmd;

	@Override
	public void execute(JSONArray ja,
			LinkedHashMap<String, Object> condition, Map retMap,
			TransactionMapData tmd) {
		logger.info("执行命令ExlUserValidateCm");
		exlUserValidateRcv.doWork(ja, condition, retMap, tmd);
	}
}
