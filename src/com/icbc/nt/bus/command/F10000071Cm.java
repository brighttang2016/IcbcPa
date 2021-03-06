/**
 * 当期绩效查询 2015-12-24
 */
package com.icbc.nt.bus.command;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bus.receiver.F10000071Rcv;
import com.icbc.nt.util.TransactionMapData;

public class F10000071Cm extends CmParemt implements BusCommand {
	@Autowired
	private F10000071Rcv f10000071Rcv;
	@Override
	public void execute(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub
		f10000071Rcv.doWork(ja, condition, retMap, tmd);
	}

}
