package com.icbc.nt.web;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class DataRcv {
	
	@Before("execution(* userInfoQuery(..))")
	 public void dataRead(JoinPoint point) throws Throwable{
		System.out.println("dataRead");
		Object[] args = point.getArgs();
		System.out.println("args.length:"+args.length);
		System.out.println("@Before：模拟权限检查...");        
		System.out.println("@Before：目标方法为：" +                
		point.getSignature().getDeclaringTypeName() + "." + point.getSignature().getName());        
		System.out.println("@Before：参数为：" + Arrays.toString(point.getArgs()));        
		System.out.println("@Before：被织入的目标对象为：" + point.getTarget());
		
		return;
	 }
}
