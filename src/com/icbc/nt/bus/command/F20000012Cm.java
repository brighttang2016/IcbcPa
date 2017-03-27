package com.icbc.nt.bus.command;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bus.receiver.F20000012Rcv;
import com.icbc.nt.util.TransactionMapData;

public class F20000012Cm extends CmParemt implements BusCommand {
	@Autowired
	private F20000012Rcv f20000012Rcv;
	@Override
	public void execute(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub
	}
	public void execute(TransactionMapData tmd) {
		f20000012Rcv.doWork(tmd);
	}

}
