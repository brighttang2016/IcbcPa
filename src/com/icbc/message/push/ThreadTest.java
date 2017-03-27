package com.icbc.message.push;

import org.apache.log4j.Logger;

import com.icbc.nt.util.IcbcUtil;

public class ThreadTest extends Thread{
	Logger logger = IcbcUtil.getLogger();
	String receiver;
//	MessagePushHelper mph = new MessagePushHelper();
	MessagePushHelper mph;
	public ThreadTest(String receiver,MessagePushHelper mph){
		this.receiver = receiver;
		this.mph = mph;
	}
	public void run(){
		/*int i = 0;
		while(i < 10){
			i++;
			try {
				Thread.currentThread().sleep(1000);
				System.out.println("i:"+i);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
		
		int j = 0;
		while(j < 10){
			try {
				System.out.println(j+"PushThread 线程启动");
				Thread.currentThread().sleep(1000);
				mph.pushMessage(receiver, j+"服务器推送消息" + receiver);
			} catch (Exception e) {
				e.printStackTrace();
			}
			j++;
			if(j == 10){
				mph.pushMessage(receiver, j+"服务器推送消息完成" + receiver);
			}
			logger.info(j+"服务器推送消息" + receiver);
		}
		
	}
}
