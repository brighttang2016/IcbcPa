/**
 * 给定机构号下所有客户经理计数 2015-12-11
 */
package com.icbc.nt.bus.command;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bus.receiver.CountKhjlRcv;
import com.icbc.nt.util.TransactionMapData;

public class CountKhjlCm extends CmParemt implements BusCommand{
	@Autowired
	private CountKhjlRcv countKhjlRcv;
	@Override
	public void execute(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub

	}
	public int execute(TransactionMapData tmd){
		return countKhjlRcv.doWork(tmd);
	}
}
