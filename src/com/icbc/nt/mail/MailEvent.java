/**
 * 邮件事件类，初始化各个角色邮件类参数applyId
 */
package com.icbc.nt.mail;

import java.util.HashMap;
import java.util.LinkedHashMap;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.dom.DaoParent;
import com.icbc.nt.util.IcbcUtil;

public class MailEvent {
	String applyId;
	String mailSub;
	String mailCont;
	HashMap<String,Object> retMap;
	
	/**
	 * 
	 * @param applyId 申请单号
	 * @param mailSub 邮件标题
	 * @param mailCont 邮件内容
	 * @param retMap 
	 */
	public MailEvent(String applyId,HashMap<String,Object> retMap){
		this.applyId = applyId;
		this.retMap = retMap;
	}
	
	/**
	 * 
	 * @param applyId 申请单号
	 * @param mailSub 邮件标题
	 * @param mailCont 邮件内容
	 * @param retMap 
	 */
	public MailEvent(String applyId,String mailSub,String mailCont,HashMap<String,Object> retMap){
		this.applyId = applyId;
		this.retMap = retMap;
		this.mailSub = mailSub;
		this.mailCont = mailCont;
	}
}
