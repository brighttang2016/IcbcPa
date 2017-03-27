/**
 * 机构分配结果查询 2015-12-28
 */
package com.icbc.nt.bus.command;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bus.receiver.F10000053Rcv;
import com.icbc.nt.bus.receiver.F10000054Rcv;
import com.icbc.nt.util.TransactionMapData;

public class F10000054Cm extends CmParemt implements BusCommand{
	@Autowired
	private F10000054Rcv f10000054Rcv;
	@Override
	public void execute(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub
		f10000054Rcv.doWork(ja, condition, retMap, tmd);
	}
}
