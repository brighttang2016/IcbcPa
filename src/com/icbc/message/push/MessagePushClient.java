package com.icbc.message.push;

import java.util.Collection;

import javax.servlet.ServletException;

import org.directwebremoting.*;
import org.directwebremoting.event.ScriptSessionEvent;
import org.directwebremoting.event.ScriptSessionListener;
import org.directwebremoting.extend.ScriptSessionManager;
import org.directwebremoting.servlet.DwrServlet;

public class MessagePushClient  implements MessagePushInterface{
	/**
	 * session 初始化
	 * @author brighttang 20140604
	 * @return void
	 */
	public void init() throws ServletException{
		System.out.println("MessagePushClient init");
		Container container = ServerContextFactory.get().getContainer();
		ScriptSessionManager ssm = container.getBean(ScriptSessionManager.class);
		ScriptSessionListener ssl = new ScriptSessionListener() {
			@Override
			public void sessionDestroyed(ScriptSessionEvent arg0) {
				// TODO Auto-generated method stub
				System.out.println("MessagePushClient sessionDestroyed");
			}
			@Override
			public void sessionCreated(ScriptSessionEvent arg0) {
				// TODO Auto-generated method stub
				System.out.println("MessagePushClient sessionCreated");
			}
		};
		ssm.addScriptSessionListener(ssl);
	}
	
	/**
	 * 消息推送
	 * @param target 消息接收方注册id
	 * @param message 推送消息
	 * @author brighttang20151020
	 */
	public void pushMessage(String target,String message){
		final String userId = target;
        final String autoMessage = message;
        Browser.withAllSessionsFiltered(new ScriptSessionFilter() {
			@Override
			public boolean match(ScriptSession arg0) {
				if(arg0.getAttribute("userId") == null)
					return false;
				else
					return arg0.getAttribute("userId").equals(userId);
			}
		}, new Runnable() {
			@Override
			public void run() {
				ScriptBuffer sb = new ScriptBuffer();
				sb.appendCall("pageRefresh",autoMessage);
				Collection<ScriptSession> sessions = Browser.getTargetSessions();
				for(ScriptSession ss:sessions){
					ss.addScript(sb);
				}
			}
		});
	}
}
