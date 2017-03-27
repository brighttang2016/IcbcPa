/**
 * 标注产品绩效计算命令类 2015-11-20
 */
package com.icbc.nt.bus.command;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bus.receiver.JxBzcpCalcRcv;
import com.icbc.nt.util.TransactionMapData;

public class JxBzcpCalcCm extends CmParemt implements BusCommand{
	@Autowired
	private JxBzcpCalcRcv jxBzcpCalcRcv;
	@Override
	public void execute(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub
		jxBzcpCalcRcv.doWork(ja, condition, retMap, tmd);
	}
	
}
