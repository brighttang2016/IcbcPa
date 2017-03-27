/**
 * brighttang 2015-06-10
 * 权限、session超时判断拦截器 
 */
package com.icbc.nt.interceptor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.aopalliance.intercept.Interceptor;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.RequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.nt.bus.AuthInteceptorBus;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.bus.RoleBus;
import com.icbc.nt.bus.UserBus;
import com.icbc.nt.bus.dispatcher.BusDispatcherImpl;
import com.icbc.nt.util.IcbcUtil;
import com.icbc.nt.util.TransactionMapData;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;

@Scope("request")
@Controller
public class AuthInteceptor extends BusParent implements HandlerInterceptor,Serializable{
//	private static UserBus ub;
//	private static IcbcUtil iu;
//	private static Logger logger;
	private UserBus userBus;
	
	@Autowired
	private TransactionMapData tmd;
	private TransactionMapData tmd2 = new TransactionMapData();
	@Autowired
	private AuthInteceptorBus authInteceptorBus;
	@Autowired
	private BusDispatcherImpl busDispatcherImpl;
	static{
//		ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
//		ub = (UserBus) ac.getBean("userBus");
//		iu = (IcbcUtil) ac.getBean("icbcUtil");
//		logger = new IcbcUtil().getLogger();
	}
	public UserBus getUserBus() {
		return userBus;
	}
	public void setUserBus(UserBus userBus) {
		this.userBus = userBus;
	}
	/**
	 * 查询用户当前所有权限
	 * @param userId 用户号
	 * @param ja json对象数组
	 * @param txCode 当前交易码
	 * @return
	 */
	public int userAuthQuery(String userId,JSONArray ja,String txCode,HttpSession session){
		logger.debug("用户权限查询："+userId+",txCode:"+txCode);
		int authFlag = 0;
		logger.debug("session userAuthJa:"+session.getAttribute("userAuthJa"));
		if(session.getAttribute("userAuthJa") == null){
			Map retMap = new HashMap<String, Object>();
			LinkedHashMap<String,Object> condition = new LinkedHashMap<String,Object>();
			iu.putCondition(condition, "user_id", userId);
			userBus.userAuthQuery(ja,condition);
			session.setAttribute("userAuthJa", ja);
		}else{
			ja = (JSONArray) session.getAttribute("userAuthJa");
		}
		logger.debug("当前用户所有交易权限ja："+ja.toJSONString());
		
		for(int i = 0;i < ja.size();i++){
			JSONObject json = (JSONObject) ja.get(i);
			if(txCode.equals(json.getString("authority_id"))){
				authFlag = 1;
			}
		}
		return authFlag;
	}

//	@Override
	public void destroy() {
	}
//	@Override
	public void init() {
	}

	@Override
	public void afterCompletion(HttpServletRequest arg0,
			HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1,
			Object arg2, ModelAndView arg3) throws Exception {
		// TODO Auto-generated method stub
	}

	/**
	 * 终端上送报文解包
	 * @param request
	 */
	/*public void unpack(HttpServletRequest request){
		String strRcv = request.getParameter("sendJson");
		logger.info("接收终端上送报文strRcv:"+strRcv);
		try {
			JSONArray rcvArr = JSONArray.parseArray(strRcv);
			for (int i = 0; i < rcvArr.size(); i++) {
				JSON json = (JSON) rcvArr.get(i);
				
			}
		} catch (Exception e) {
			logger.info("解包失败，终端上送报文strRcv可能为null");
		}
	}*/
	
	/**
	 * 解包，入变量池
	 * @param tranData
	 */
	public void unPack(HttpServletRequest request){
		String tranData = "";//定制的交易数据
		Map<String,Object> paramMap = request.getParameterMap();
//		logger.info("AuthIntercepter接收到客户端数据-------->>>>>>>>paramMap解析前:"+paramMap);
		Iterator it = paramMap.keySet().iterator();
		HashMap<String,String> paraMapUpk = new HashMap<String, String>();//request解析后的hashMap
		while(it.hasNext()){
			String paramKey = (String) it.next();
			String paramValue = "";
			try {
				paramValue = request.getParameter(paramKey);
			} catch (Exception e) {
				paramValue = "无法转换为字符串";
				logger.error(paramMap.get(paramKey)+"无法转换为字符串");
			}
			if(!"tranData".equals(paramKey)){
				tmd.put(paramKey, paramValue);
			}else{
				tranData = paramValue;
			}
			paraMapUpk.put(paramKey, paramValue);
		}
		
		try {
			tmd.put("start", Integer.parseInt(tmd.get("start").toString())+1);
			tmd.put("end", Integer.parseInt(tmd.get("start").toString()) + Integer.parseInt(tmd.get("limit").toString()));
		} catch (Exception e) {}
		
		logger.info("AuthIntercepter接收到客户端数据-------->>>>>>>>request解析为paraMapUpk:"+paraMapUpk);
//		logger.info("AuthIntercepter接收到客户端数据-------->>>>>>>>:"+tranData);
		JSONObject tranDataJson = null;
		try {
			if(!"".equals(tranData)){
				tranDataJson = JSONObject.parseObject(tranData);
				Iterator<String> keyIt = tranDataJson.keySet().iterator();
				while(keyIt.hasNext()){
					String key = keyIt.next();
					try {
						tmd.put(key, JSONArray.parseArray(tranDataJson.getString(key)));//若能解析为json，则转化为json对象后入变量池2016-02-25
					} catch (Exception e) {
						tmd.put(key, tranDataJson.getString(key));
					}
				}
			}
		} catch (Exception e) {
			logger.error("解包失败，tranData无法转换成json对象，tranData："+tranData);
		}
		logger.info("解包，入变量池完成");
		tmd.tmdLogger();
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object obj) throws Exception {
		tmd.cleanTmd();//清空变量池
		JSONArray ja = new JSONArray();
		HttpSession session = request.getSession();
		String userId = (String) session.getAttribute("userIdLogin");
		tmd.put("userIdLogin", userId);
		tmd.put("userId", userId);
		tmd.put("session", session);
		//当前考核周期查询
		if(session.getAttribute("zqCurr") == null){
			String zqCurr = busDispatcherImpl.zqCurr();
			session.setAttribute("zqCurr", zqCurr);//当前考核周期入变量池和session
			tmd.put("zqCurr", zqCurr);
		}else{
			tmd.put("zqCurr", session.getAttribute("zqCurr"));
		}
		logger.info("*******************前端请求拦截*************************");
		this.unPack(request);
//		authInteceptorBus.visibleUser(vbUserJa);
//		String txCode = request.getParameter("tx_code");//获取request中元素
		String txCode = "";//交易码
		try {
			txCode = tmd.get("tx_code").toString();
		} catch (Exception e) {
			// TODO: handle exception
		}
		tmd.put("txCode", txCode);//excel上传处理逻辑使用txCode,与tranData中解包的tx_code
		if(userId == null || "".equals(userId.trim())){//session超时
			response.addHeader("sessionStatus", "true");
			request.getRequestDispatcher("/sessionInvalid.html").forward(request, response);  
			return false;
		}else{
			if((!"".equals(txCode) &&  !"00000000".equals(txCode)) && !"0".equals(tmd.get("auth_check"))){//交易码：00000000，设为不作任何权限判断-------authChcek:0时，即使存在交易码，也不做权限校验
				int authFlag = 0;
				try {
					authFlag = this.userAuthQuery(userId, ja,txCode,session);
				} catch (Exception e) {
					logger.error("用户："+userId+",权限："+txCode+"查询异常");
				}
				if(authFlag == 0){//权限不足
					logger.error("当前用户交易权限不足，交易码："+txCode);
					request.getRequestDispatcher("/authInvalid.html").forward(request, response);
					return false;
				}
			}
		}
		try {
			authInteceptorBus.visibleUser();
			authInteceptorBus.visibleOrg();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return true;//执行拦截器后，继续后续交易
	}
}
