/**
 * 职务层级设置命令类
 * 2015-11-18
 * brighttang
 */
package com.icbc.nt.bus.command;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bus.receiver.ZwcjRcv;
import com.icbc.nt.util.TransactionMapData;

public class ZwcjCm extends CmParemt implements BusCommand{
	@Autowired
	private ZwcjRcv zwcjRcv;
	@Override
	public void execute(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub
		System.out.println("ZwcjCm execute");
		zwcjRcv.doWork(ja, condition, retMap, tmd);
	}
}
