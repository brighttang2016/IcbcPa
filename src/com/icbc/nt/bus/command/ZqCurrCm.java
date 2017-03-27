package com.icbc.nt.bus.command;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bus.receiver.ZqCurrRcv;
import com.icbc.nt.util.TransactionMapData;

public class ZqCurrCm extends CmParemt implements BusCommand {
	@Autowired
	private ZqCurrRcv zqCurrRcv;
	@Override
	public void execute(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub

	}
	public String execute(){
		return zqCurrRcv.doWork();
	}

}
