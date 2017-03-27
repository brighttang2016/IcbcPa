package com.icbc.nt.bus.command;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bus.receiver.JxDxCalcRcv;
import com.icbc.nt.util.TransactionMapData;

public class JxDxCalcCm extends CmParemt implements BusCommand{
	@Autowired
	JxDxCalcRcv jxDxCalcRcv;
	@Override
	public void execute(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub
		jxDxCalcRcv.doWork(ja, condition, retMap, tmd);
	}
}
