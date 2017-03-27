package com.icbc.nt.bus.command;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bus.receiver.F20000014Rcv;
import com.icbc.nt.bus.receiver.F20000008Rcv;
import com.icbc.nt.util.TransactionMapData;

public class F20000014Cm extends CmParemt implements BusCommand {
	@Autowired
	private F20000014Rcv f20000014Rcv;
	@Override
	public void execute(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub
		f20000014Rcv.doWork(ja, condition, retMap, tmd);
	}

}
