/**
 * 2015-06-04 brighttang
 * 更新操作后置增强类（更新操作后，操作结果写入session，待前端展示）
 */
package com.icbc.nt.advice;

import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.aop.AfterReturningAdvice;
import org.springframework.beans.factory.annotation.Autowired;

import com.icbc.nt.bus.BusParent;
import com.icbc.nt.util.TransactionMapData;

public class UpdAfterAdvice extends BusParent implements AfterReturningAdvice{
	@Override
	public void afterReturning(Object retObj, Method method, Object[] args,Object obj) throws Throwable {
//		System.out.println("method.getName():"+method.getName()+" retObj:"+retObj);
		HttpSession session = null;
		for (int i = 0; i < args.length; i++) {
			System.out.println("参数"+i+":"+args[i]);
		}
		try {
			session = (HttpSession)args[0];
		} catch (Exception e) {
			logger.info("后置增强获取session失败");
		}
//			if(Integer.parseInt(retObj+"") > 0){
		
		try {
			logger.info("retObj:"+retObj);
			if(Integer.parseInt(retObj+"") > 0){
//				if(Integer.parseInt(tmd.get("recUpdCount").toString()) > 0){
				logger.info("操作成功");
				session.setAttribute("updateMsg", "操作成功");
				session.setAttribute("errorMsg", "操作成功");
				session.setAttribute("errorCode", "00");
			}else{
				logger.info("操作失败");
				session.setAttribute("updateMsg", "操作失败");
				session.setAttribute("errorMsg", "操作失败");
				session.setAttribute("errorCode", "01");
			}
		} catch (Exception e) {
			logger.info("操作失败，可能原因：1、获取session失败；2获取更新操作执行失败");
		}
		
	}
}
