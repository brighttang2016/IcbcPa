package com.icbc.nt.util;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Priority;

public class LogAppender extends DailyRollingFileAppender{
	public boolean isAsSevereAsThreshold(Priority priority){
		boolean boolRet = false;
		if(priority == null){
			boolRet = true;
		}else if(this.getThreshold().equals(priority)){
			boolRet = true;
		}
		return boolRet;
	}
}
