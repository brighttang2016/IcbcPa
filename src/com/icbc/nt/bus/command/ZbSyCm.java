/**
 * 总包剩余2015-12-17
 */
package com.icbc.nt.bus.command;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bus.receiver.ZbSyRcv;
import com.icbc.nt.util.TransactionMapData;

public class ZbSyCm extends CmParemt implements BusCommand {
	@Autowired
	private ZbSyRcv zbSyRcv;
	@Override
	public void execute(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub

	}
	public void execute(TransactionMapData tmd) {
		zbSyRcv.doWork(tmd);
	}
}
