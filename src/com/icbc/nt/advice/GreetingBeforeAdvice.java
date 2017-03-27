package com.icbc.nt.advice;

import java.lang.reflect.Method;

import org.springframework.aop.MethodBeforeAdvice;

public class GreetingBeforeAdvice implements MethodBeforeAdvice{

	@Override
	public void before(Method arg0, Object[] arg1, Object arg2)
			throws Throwable {
		// TODO Auto-generated method stub
		System.out.println("类："+arg2.getClass().getName()+"|切点方法:"+arg0);
		System.out.println("GreetingBeforeAdvice 方法前置增强");
	}
}
