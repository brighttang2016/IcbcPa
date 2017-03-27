/**
 * 上传后置处理器，推送前端刷新页面消息
 * 2015-10-22
 */
package com.icbc.nt.advice;

import org.apache.log4j.Logger;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;

import com.icbc.nt.util.IcbcUtil;

@Aspect
public class UploadAfterAdvice {
	Logger logger = IcbcUtil.getLogger();
//	@After("execution(* fileUpload(..))")
	@After("execution(* com.icbc.nt.bus.FileUploadBus.fileUpload(..))")
//	@After("execution(* com.icbc.nt.advice.UploadAfterAdvice.*(..))")
//	@After("execution(* com.icbc.nt.bus.FileUploadBus.* (..))")
	public void afterUpload(){
		logger.info("上传后置增强逻辑,当前线程："+Thread.currentThread().getName());
		System.out.println("上传后置增强逻辑");
	}
}
