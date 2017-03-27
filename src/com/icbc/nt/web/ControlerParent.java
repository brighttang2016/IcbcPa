package com.icbc.nt.web;

import java.io.Serializable;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;
import org.jboss.interceptor.model.SerializationProxyFactory;

import com.icbc.nt.util.IcbcUtil;

/**
 * 公用类
 * @author Administrator
 *
 */
public class ControlerParent implements Serializable{
	
	protected LinkedHashMap<String, Object> condition = new LinkedHashMap<String, Object>();
	protected IcbcUtil iu = new IcbcUtil();
	protected Logger logger = iu.getLogger();
}
