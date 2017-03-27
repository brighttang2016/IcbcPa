/**
 * 业务逻辑转发器接口2015-11-16
 */
package com.icbc.nt.bus.dispatcher;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.util.TransactionMapData;

public interface BusDispatcher{
	public void exlUserValidate(JSONArray ja,LinkedHashMap<String, Object> condition, Map retMap,TransactionMapData tmd);
	public void zbQuery(JSONArray ja,LinkedHashMap<String, Object> condition, Map retMap,TransactionMapData tmd);
}
