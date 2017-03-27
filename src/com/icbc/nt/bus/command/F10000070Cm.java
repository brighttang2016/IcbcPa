package com.icbc.nt.bus.command;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bus.receiver.F10000070Rcv;
import com.icbc.nt.util.TransactionMapData;

public class F10000070Cm extends CmParemt implements BusCommand {
	@Autowired
	private F10000070Rcv f10000070Rcv;
	@Override
	public void execute(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub
		f10000070Rcv.doWork(ja, condition, retMap, tmd);
	}
}
