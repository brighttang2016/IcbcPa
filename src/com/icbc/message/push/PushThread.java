package com.icbc.message.push;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.util.IcbcUtil;
import com.icbc.nt.util.TransactionMapData;
//@Scope("session")
public class PushThread extends Thread{
	Logger logger = IcbcUtil.getLogger();
//	String[] receiver;
	String receiver;
	MessagePushInterface mph;
	HttpSession session;

	TransactionMapData tmd;
	TransactionMapData tmdCurr;
	
	
	public TransactionMapData getTmdCurr() {
		return tmdCurr;
	}

	public void setTmdCurr(TransactionMapData tmdCurr) {
		this.tmdCurr = tmdCurr;
	}

	HashMap hashMap;
	
	/**
	 * 清空session自定义属性
	 * @param session
	 */
	public void attrClean(HttpSession session){
		session.setAttribute("percent", "0");
		session.setAttribute("errorCode", "0");
		session.setAttribute("errorMsg", "0");
		session.setAttribute("progress", "0");
		session.setAttribute("finish", "0");
	}
	
	/**
	 * 
	 * @param receiver 接受者
	 * @param mph 消息推送器	
	 * @param session 
	 * @param tmd 变量池
	 * @param hashMap
	 */
	public PushThread(String receiver,MessagePushInterface mph,TransactionMapData tmd){
//	public PushThread(String receiver,MessagePushInterface mph){
//	public PushThread(String receiver,MessagePushHelper mph,HttpSession session,TransactionMapData tmd,HashMap hashMap){
		this.receiver = receiver;
		this.mph = mph;
		this.session = session;
		tmdCurr = tmd;
		this.hashMap = hashMap;
	}
	
	public PushThread(){

	}
	
	public MessagePushInterface getMph() {
		return mph;
	}

	public TransactionMapData getTmd() {
		return tmd;
	}

	public void setTmd(TransactionMapData tmd) {
		this.tmdCurr = tmd;
	}

	public void setMph(MessagePushInterface mph) {
		this.mph = mph;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	@Override
	public void run() {
//		tmdCurr.tmdLogger();
		String errorCode = "0";
		String percent = "0";
		String progress = "0";
		String errorMsg = "";
		String finish = "0";
		String data = "";//其他需要返回的参数
		Map retMap = new HashMap();
		//清空session数据
		while(!"1".equals(finish)){
			try {
				errorCode = tmdCurr.get("errorCode").toString();
				percent = tmdCurr.get("percent").toString();
				progress = tmdCurr.get("progress").toString();
				errorMsg = tmdCurr.get("errorMsg").toString();
				finish = tmdCurr.get("finish").toString();
//				logger.info("推送中的progress："+tmdCurr.get("progress").toString());
				try {
					data = tmdCurr.get("data").toString();
				} catch (Exception e) {
					// TODO: handle exception
				}
				
			/*try{
				errorCode = session.getAttribute("errorCode").toString();
			}catch(Exception e){}
			try{
				errorMsg = session.getAttribute("errorMsg").toString();
			}catch(Exception e){}
			try{
				percent = session.getAttribute("percent").toString();
			}catch(Exception e){}
			try{
				progress = session.getAttribute("progress").toString();
			}catch(Exception e){}
			try{
				finish = session.getAttribute("finish").toString();
			}catch(Exception e){}*/
				
				retMap.put("success", true);
				retMap.put("errorCode",errorCode);//错误码
				retMap.put("errorMsg",errorMsg);//错误信息
//				retMap.put("percent", percent);//2015-01-08后失效
				retMap.put("progress", progress);//进度条进度 0-1
				retMap.put("finish", finish);//0：继续推送 1：停止推送
				retMap.put("data", data);//其他需要推送的数据
				Thread.currentThread().sleep(1000);
//				logger.info("推送："+JSON.toJSONString(retMap));
				logger.debug("消息推送---->接受者："+receiver+"|推送数据:"+JSON.toJSONString(retMap));
//				logger.info("线程中的 hashMap:"+hashMap);
				System.out.println("消息推送---->接受者："+receiver+"|推送数据:"+JSON.toJSONString(retMap));
				mph.pushMessage(receiver, JSON.toJSONString(retMap));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if("1".equals(finish)){//交易完成，清空数据
			mph.pushMessage(receiver, JSON.toJSONString(retMap));
//			this.attrClean(session);
		}
	}
}
