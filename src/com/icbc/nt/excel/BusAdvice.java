package com.icbc.nt.excel;

import java.lang.reflect.Method;
import java.util.HashSet;

import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.beans.factory.annotation.Autowired;

import com.icbc.nt.bus.FileUploadBus;

public class BusAdvice implements MethodBeforeAdvice{
	@Override
	public void before(Method method, Object[] objs, Object obj)
			throws Throwable {
		// TODO Auto-generated method stub
		System.out.println("方法前置增强"+method.getName()+"|"+objs.length+"|obj:"+obj);
	}
}
