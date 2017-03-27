package com.icbc.nt.processor;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class InitStartProcessor implements ApplicationListener<ContextRefreshedEvent>{
	//ApplicationEvent
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		// TODO Auto-generated method stub
		System.out.println("***********启动系统*********:"+event.getApplicationContext().getParent());
		if(event.getApplicationContext().getParent() == null){
			System.out.println("66666666666666666666666");
		}
	}
}
