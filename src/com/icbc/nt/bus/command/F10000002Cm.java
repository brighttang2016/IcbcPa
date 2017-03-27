package com.icbc.nt.bus.command;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bus.receiver.F10000001Rcv;
import com.icbc.nt.bus.receiver.F10000002Rcv;
import com.icbc.nt.bus.receiver.F10000053Rcv;
import com.icbc.nt.util.TransactionMapData;

public class F10000002Cm extends CmParemt implements BusCommand {
	@Autowired
	private F10000002Rcv f10000002Rcv;
	@Override
	public void execute(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub
	}
	public void execute(TransactionMapData tmd) {
		f10000002Rcv.doWork(tmd);
	}
}
