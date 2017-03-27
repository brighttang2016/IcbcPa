package com.icbc.message.push;


import java.io.UnsupportedEncodingException;
import java.util.Collection;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.directwebremoting.*;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.springframework.beans.factory.annotation.Autowired;

import com.icbc.nt.util.IcbcUtil;


@ RemoteProxy
public class MessagePush {
	private static Logger logger;
	
	@Autowired
	MessagePushClient mpc;
	@Autowired
	MessagePushHelper mph;
	static{
		logger = IcbcUtil.getLogger();
	}
	public MessagePush(){
	}
	public String testMethod3(String s){
		System.out.println(s+"return");
		logger.info(s+"return");
		return s+"return"+"  服务端返回";
//		return " 服务端返回";
	}
	
	/**
	 * 注册监听客户端
	 * @param userId userId作为客户端id
	 */
	@RemoteMethod
	public void onPageLoad(String userId) {
		System.out.println("onPageLoad，注册监听:"+userId);
		logger.info("onPageLoad，注册监听:"+userId);
        ScriptSession scriptSession = WebContextFactory.get().getScriptSession();
        scriptSession.setAttribute("userId", userId);
//      DwrScriptSessionManagerUtil dwrScriptSessionManagerUtil = new DwrScriptSessionManagerUtil();
//        MessagePushHelper mph = new MessagePushHelper();
//        MessagePushClient mpc = new MessagePushClient();
        try {
			/*
			 * dwrScriptSessionManagerUtil.init();
			 * sendMessageAuto(userId,"服务器推送消息"+userId); pushBegin(userId);
			 */
			
			mph.init();
			mpc.init();
			// mph.pushMessage(userId, "服务器推送消息"+userId);
		} catch (ServletException e) {
			e.printStackTrace();
		}
	}
	public void  pushBegin(String str){
		System.out.println("pushBegin");
		String[] receiver = str.split("\\|");
		MessagePushHelper mph = new MessagePushHelper();
//		new PushThread(str,mph).run();
//		new PushThread(str,mph).start();
		
		/*for (int i = 0; i < receiver.length; i++) {
			mph.pushMessage(receiver[i], "服务器推送消息" + receiver[i]);
			// sendMessageAuto(receiver[i],"服务器推送消息"+receiver[i]);
		}*/
	}
}
