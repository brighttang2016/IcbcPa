package com.icbc.nt.bus.command;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bus.receiver.JxZhtsCalcRcv;
import com.icbc.nt.bus.receiver.JxZhtsCalcZhRcv;
import com.icbc.nt.util.TransactionMapData;

public class JxZhtsCalcZhCm extends CmParemt implements BusCommand{
	@Autowired
	private JxZhtsCalcZhRcv jxZhtsCalcZhRcv;

	@Override
	public void execute(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub
		jxZhtsCalcZhRcv.doWork(ja, condition, retMap, tmd);
	}

}
