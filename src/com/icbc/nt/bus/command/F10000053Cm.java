/**
 * 机构分配数据导入结果查询 2015-12-10
 */
package com.icbc.nt.bus.command;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bus.receiver.F10000053Rcv;
import com.icbc.nt.util.TransactionMapData;

public class F10000053Cm extends CmParemt implements BusCommand{
	@Autowired
	private F10000053Rcv f10000053Rcv;
	@Override
	public void execute(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub
		f10000053Rcv.doWork(ja, condition, retMap, tmd);
	}
}
