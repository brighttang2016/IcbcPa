/**
 * 标注产品绩效计算（支行）命令类 2015-12-18
 */
package com.icbc.nt.bus.command;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bus.receiver.JxBzcpCalcRcv;
import com.icbc.nt.bus.receiver.JxBzcpCalcZhRcv;
import com.icbc.nt.util.TransactionMapData;

public class JxBzcpCalcZhCm extends CmParemt implements BusCommand{
	@Autowired
	private JxBzcpCalcZhRcv jxBzcpCalcZhRcv;
	@Override
	public void execute(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub
		jxBzcpCalcZhRcv.doWork(ja, condition, retMap, tmd);
	}
	
}
