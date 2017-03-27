/**
 * 初始化总包人均表t_ntmisc_orgzbrj与总包人数表t_ntmisc_orgzbrs中与总包相关部分
 * 2015-12-16
*/
package com.icbc.nt.bus.command;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bus.receiver.ZbInitRcv;
import com.icbc.nt.util.TransactionMapData;

public class ZbInitCm extends CmParemt implements BusCommand {
	@Autowired
	private ZbInitRcv zbInit;
	@Override
	public void execute(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub

	}
	public void zbInitZbrj(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd){
		zbInit.zbInitZbrj(ja, condition, retMap, tmd);
	}
	public void zbInitZbrs(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd){
		zbInit.zbInitZbrs(ja, condition, retMap, tmd);
	}

}
