/**
 * 系统变量池类
 * 2014-11-20
 */
package com.icbc.nt.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;

public class TransactionMapData implements Cloneable{
//public class TransactionMapData{
	Logger logger = IcbcUtil.getLogger();
//	static TransactionMapData tmd = null;
//	static HashMap<String, Object> map = new HashMap<String, Object>();
	HashMap<String, Object> map = new HashMap<String, Object>();
	public TransactionMapData(){}
	/*public static synchronized  TransactionMapData getInstance(){
//	public static TransactionMapData getInstance(){
		if(tmd == null){
			tmd = new TransactionMapData();
		}
		return tmd;
	}*/
	/**
	 * 对象拷贝 2016-02-25
	 */
	public TransactionMapData clone() {
		TransactionMapData tmd = null;
		try {
			tmd = (TransactionMapData) super.clone();
		} catch (CloneNotSupportedException e) {
			logger.error("对象拷贝错误，e:"+e.toString());
		}
		return tmd;
	}
	
	public Object  get(String key){
		return this.map.get(key);
	}
	public void put(String key,Object value){
		this.map.put(key, value==null?"":value);
	}
	public void cleanTmd(){
		this.map.clear();
	}
	/**
	 * 打印变量数据20150724
	 */
	public void tmdLogger(){
		StringBuffer logStrBuf = new StringBuffer();
		Iterator keyIt = this.map.keySet().iterator();
		while(keyIt.hasNext()){
			try {
				String key = keyIt.next()+"";
				logStrBuf.append(key+":"+this.map.get(key)+"|");
			} catch (Exception e) {}
		}
		String currThread = Thread.currentThread().getName();
//		logger.debug("当前线程currThread:"+currThread+"-------tmd变量池中数据："+logStrBuf.toString());
//		logger.info("当前线程currThread:"+currThread+"-------tmd变量池中数据："+logStrBuf.toString());
	}
	
}
