package com.icbc.nt.bus;

import java.util.HashMap;
import java.util.Map;

import com.icbc.nt.util.TransactionMapData;

public class ThreadTest extends Thread{
//	HashMap map;
	TransactionMapData tmd;
	String receiver;
	public ThreadTest(String receiver,TransactionMapData tmd){
		this.tmd = tmd;
		this.receiver = receiver;
	}
	public void run(){
		int i = 0;
		while(i < 10){
			i++;
			try {
				Thread.currentThread().sleep(1000);
				
				System.out.println("tmd name:"+"i:"+i+"|"+tmd.get("finish"));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
}
