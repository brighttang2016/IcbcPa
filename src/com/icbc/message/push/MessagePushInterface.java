package com.icbc.message.push;

import javax.servlet.ServletException;

import org.directwebremoting.extend.DwrConstants;

public interface MessagePushInterface {
	public void init() throws ServletException;
	public void pushMessage(String target,String message);
}
