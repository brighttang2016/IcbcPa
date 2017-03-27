package com.icbc.nt.bus.command;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bus.receiver.JxZhtsCalcRcv;
import com.icbc.nt.util.TransactionMapData;

public class JxZhtsCalcCm extends CmParemt implements BusCommand{
	@Autowired
	private JxZhtsCalcRcv jxZhtsCalcRcv;

	@Override
	public void execute(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub
		jxZhtsCalcRcv.doWork(ja, condition, retMap, tmd);
	}

}
