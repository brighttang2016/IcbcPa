package com.icbc.nt.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;




















import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.nt.bean.UserBean;
import com.icbc.nt.bus.MediumBus;
import com.icbc.nt.bus.UserBus;
import com.icbc.nt.util.IcbcUtil;
import com.icbc.nt.util.TransactionMapData;

//@Scope("request")
@Controller
public class UserControler extends ControlerParent{
	@Autowired
	private UserBus userBus;
	@Autowired
	private TransactionMapData tmd;
	private TransactionMapData tmdSession = new TransactionMapData();//每个session独立生成tmd实例
	private String sessionTimeout;
	public String getSessionTimeout() {
		return sessionTimeout;
	}
	public void setSessionTimeout(String sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}
	
	//绩效考核岗位查询
	@ResponseBody
	@RequestMapping(value="/jbjxControler.html")
	public Map jbjxControler(HttpServletRequest request,String jbjx_pid){
		HttpSession session = request.getSession();
		Map<String, Object> retMap = new HashMap<String, Object>();
		JSONArray ja = new JSONArray();
		LinkedHashMap<String,Object> condition = new LinkedHashMap<String,Object>();
		tmd.put("jbjx_pid", jbjx_pid);
		userBus.comboJbjx(ja, condition, retMap, tmd);
		retMap.put("success", true);
		retMap.put("rows", ja);
		return retMap;
	}
	
	//绩效考核父岗位查询
	@ResponseBody
	@RequestMapping(value="/jbjxpControler.html")
	public Map jbjxpControler(HttpServletRequest request){
		HttpSession session = request.getSession();
		Map<String, Object> retMap = new HashMap<String, Object>();
		JSONArray ja = new JSONArray();
		LinkedHashMap<String,Object> condition = new LinkedHashMap<String,Object>();
		userBus.comboJbjxp(ja, condition, retMap, tmd);
		retMap.put("success", true);
		retMap.put("rows", ja);
		return retMap;
	}
	
	//当前用户可见用户查询(当前机构可见本级机构及下级机构所有用户)
	@ResponseBody
	@RequestMapping(value="/userQuery.html")
	public Map userQuery(HttpServletRequest request,String userIdQuery){
		Map map = new HashMap<String, String>();
		HttpSession session = request.getSession();
		String userId = session.getAttribute("userId").toString();
		tmd.put("userId", userId);
		tmd.put("userIdQuery", userIdQuery);
		Map<String, Object> retMap = new HashMap<String, Object>();
		JSONArray ja = (JSONArray) tmd.get("vbUserJa");
		LinkedHashMap<String,Object> condition = new LinkedHashMap<String,Object>();
//		userBus.visibleUserQuery(ja, condition, retMap, tmd);
//			retMap.put("count", tmd.get("count"));
		retMap.put("success", true);
		retMap.put("rows", ja);
		return retMap;
	}
	
	//用户已拥有资格查询
	@ResponseBody
	@RequestMapping(value="/userCtQuery.html")
	public Map userCtQuery(HttpServletRequest request,UserBean userBean){
		Map<String, Object> retMap = new HashMap<String, Object>();
		JSONArray ja = new JSONArray();
		tmdSession.put("userId", userBean.getUserId());
		tmdSession.put("txCode", "4");
		tmdSession.put("start", Integer.parseInt(userBean.getStart())+1);
		try {
			tmdSession.put("end", Integer.parseInt(userBean.getStart()) + Integer.parseInt(userBean.getLimit())+1);
		} catch (Exception e) {}
		userBus.userInfoHisQuery(ja, condition, retMap,tmdSession);
		retMap.put("count", tmdSession.get("count"));
		retMap.put("success", true);
		retMap.put("rows", ja);
		return retMap;
	}
	
	//员工历史学习积分信息查询2015-07-22
	@ResponseBody
	@RequestMapping(value="/userCreditQuery.html")
	public Map userCreditQuery(HttpServletRequest request,UserBean userBean){
		Map<String, Object> retMap = new HashMap<String, Object>();
		JSONArray ja = new JSONArray();
		tmdSession.put("userId", userBean.getUserId());
		tmdSession.put("txCode", "3");
		tmdSession.put("start", Integer.parseInt(userBean.getStart())+1);
		try {
			tmdSession.put("end", Integer.parseInt(userBean.getStart()) + Integer.parseInt(userBean.getLimit())+1);
		} catch (Exception e) {}
		userBus.userInfoHisQuery(ja, condition, retMap,tmdSession);
		retMap.put("count", tmdSession.get("count"));
		retMap.put("success", true);
		retMap.put("rows", ja);
		return retMap;
	}
	
	//员工个人信息查询2015-08-17
	@ResponseBody
	@RequestMapping(value="/userInfoQuery.html")
	public Map userInfoQuery(HttpServletRequest request,String txCode){
		Map<String, Object> retMap = new HashMap<String, Object>();
		JSONArray ja = new JSONArray();
		/*tmd.put("userId", request.getSession().getAttribute("userId"));
		tmd.put("txCode", txCode);*/
		tmdSession.put("userId", request.getSession().getAttribute("userId"));
		tmdSession.put("txCode", txCode);
		userBus.userManage(ja, condition, txCode, retMap,tmdSession);
		retMap.put("count", tmd.get("count"));
		retMap.put("success", true);
		retMap.put("rows", ja);
		return retMap;
	}
	
	
	//员工历年职务层级变更信息查询2015-07-22
	@ResponseBody
	@RequestMapping(value="/jbHisQuery.html")
	public Map jbHisQuery(HttpServletRequest request,UserBean userBean){
		Map<String, Object> retMap = new HashMap<String, Object>();
		JSONArray ja = new JSONArray();
		tmdSession.put("userId", userBean.getUserId());
		tmdSession.put("txCode", "2");
		tmdSession.put("start", Integer.parseInt(userBean.getStart())+1);
		try {
			tmdSession.put("end", Integer.parseInt(userBean.getStart()) + Integer.parseInt(userBean.getLimit())+1);
		} catch (Exception e) {}
		userBus.userInfoHisQuery(ja, condition, retMap,tmdSession);
		retMap.put("count", tmdSession.get("count"));
		retMap.put("success", true);
		retMap.put("rows", ja);
		return retMap;
	}
	
	//员工历年工资等级档次变更信息查询2015-07-22
	@ResponseBody
	@RequestMapping(value="/wglHisQuery.html")
	public Map wglHisQuery(HttpServletRequest request,UserBean userBean){
		Map<String, Object> retMap = new HashMap<String, Object>();
		JSONArray ja = new JSONArray();
		tmdSession.put("userId", userBean.getUserId());
		tmdSession.put("txCode", "1");
		tmdSession.put("start", Integer.parseInt(userBean.getStart())+1);
		try {
			tmdSession.put("end", Integer.parseInt(userBean.getStart()) + Integer.parseInt(userBean.getLimit())+1);
		} catch (Exception e) {}
		userBus.userInfoHisQuery(ja, condition, retMap,tmdSession);
		retMap.put("count", tmdSession.get("count"));
		retMap.put("success", true);
		retMap.put("rows", ja);
		return retMap;
	}
	
	//员工历年积分信息查询2015-07-21
	@ResponseBody
	@RequestMapping(value="/pointInfoHisQuery.html")
	public Map pointInfoHisQuery(HttpServletRequest request,UserBean userBean){
		Map<String, Object> retMap = new HashMap<String, Object>();
		JSONArray ja = new JSONArray();
		tmdSession.put("userId", userBean.getUserId());
		tmdSession.put("txCode", "0");
		tmdSession.put("start", Integer.parseInt(userBean.getStart())+1);
		try {
			tmdSession.put("end", Integer.parseInt(userBean.getStart()) + Integer.parseInt(userBean.getLimit())+1);
		} catch (Exception e) {}
		userBus.userInfoHisQuery(ja, condition, retMap,tmdSession);
		retMap.put("count", tmdSession.get("count"));
		retMap.put("success", true);
		retMap.put("rows", ja);
		return retMap;
	}
	
	@ResponseBody
	@RequestMapping(value="/getUserRoleInfo.html")
	public Map getUserRoleInfo(HttpServletRequest request,UserBean userBean){
		Map<String, Object> retMap = new HashMap<String, Object>();
		JSONArray ja = new JSONArray();
		
//		tmd.put("userId", userBean.getUserId());
		tmdSession.put("userId", userBean.getUserId());
		userBus.userManage(ja, condition, "2", retMap,tmdSession);
		retMap.put("success", true);
		retMap.put("rows", ja);
//		logger.info("准备返回");
		return retMap;
	}
	
	//岗位类别查询
	@ResponseBody
	@RequestMapping(value="/jbCatQuery.html")
	public Map jbCatQuery(HttpServletRequest request,Object obj){
		Map<String, Object> retMap = new HashMap<String, Object>();
		JSONArray ja = new JSONArray();
		userBus.userManage(ja, condition, "4", retMap,tmdSession);
		retMap.put("success", true);
		retMap.put("rows", ja);
//			logger.info("准备返回");
		return retMap;
	}
	
	//岗位层级查询
	@ResponseBody
	@RequestMapping(value="/jobQuery.html")
	public Map jobQuery(HttpServletRequest request,Object obj){
		Map<String, Object> retMap = new HashMap<String, Object>();
		JSONArray ja = new JSONArray();
		
		userBus.userManage(ja, condition, "0", retMap,tmdSession);
		retMap.put("success", true);
		retMap.put("rows", ja);
//		logger.info("准备返回");
		return retMap;
	}
	//人员性质查询
	@ResponseBody
	@RequestMapping(value="/propertyQuery.html")
	public Map propertyQuery(HttpServletRequest request,Object obj){
		Map<String, Object> retMap = new HashMap<String, Object>();
		JSONArray ja = new JSONArray();
		userBus.userManage(ja, condition, "1", retMap,tmdSession);
		retMap.put("success", true);
		retMap.put("rows", ja);
//		logger.info("准备返回");
		return retMap;
	}
	//用户管理
	@ResponseBody
	@RequestMapping(value="/userManage.html")
	public Map userManage(HttpServletRequest request,UserBean userBean,String tranData){
		logger.info("UserControler接收客户端数据-------->>>>>>>>:"+tranData);
		Map<String, Object> retMap = new HashMap<String, Object>();
//		tmdSession.tmdLogger();
		tmd.tmdLogger();
		
//		logger.info("request中的数据--------》》》》》》》tranData:"+request.getParameter("tranData"));
		/*Map map = new HashMap<String, String>();
		tmdSession.put("userIdQuery", userBean.getUserId());//查询区userId
		tmdSession.put("name", userBean.getName());
		tmdSession.put("sex", userBean.getSex());
		tmdSession.put("depId", userBean.getDeptId());
		tmdSession.put("orgId", userBean.getOrgId());
		tmdSession.put("mail", userBean.getMail());
		tmdSession.put("beginDate", userBean.getBeginDate());//开始工作日期
		tmdSession.put("propertyId", userBean.getPropertyId());//人员类别
		tmdSession.put("jbId", userBean.getJbId());//获取岗位层级
		tmdSession.put("jbCat", userBean.getJbCat());//获取岗位类别
		tmdSession.put("roleSelect",userBean.getRoleSelect());
		tmdSession.put("currUser", request.getSession().getAttribute("userId"));//当前登录账户
		tmdSession.put("userId", userBean.getUserId());
		tmdSession.put("jbjxId", userBean.getJbjxId()==null?"":userBean.getJbjxId());
		tmdSession.put("jbjxPid", userBean.getJbjxPid());
		try {
			tmdSession.put("start", Integer.parseInt(userBean.getStart())+1);
			tmdSession.put("end", Integer.parseInt(userBean.getStart()) + Integer.parseInt(userBean.getLimit())+1);
		} catch (Exception e) {}
		
		JSONArray ja = new JSONArray();
		String tx_code = userBean.getTx_code();
		LinkedHashMap<String,Object> condition = new LinkedHashMap<String,Object>();
		userBus.userManage(ja, condition, tx_code, retMap,tmdSession);
		retMap.put("count", tmdSession.get("count"));
		retMap.put("success", true);
		retMap.put("rows", ja);*/
		
		/*JSONArray ja = new JSONArray();
		String tx_code = userBean.getTx_code();
		LinkedHashMap<String,Object> condition = new LinkedHashMap<String,Object>();
		userBus.userManage(ja, condition, tx_code, retMap,tmdSession);
		retMap.put("count", tmdSession.get("count"));
		retMap.put("success", true);
		retMap.put("rows", ja);
		*/
//		JSONArray ja = new JSONArray();
//		String tx_code = userBean.getTx_code();
		
//		LinkedHashMap<String,Object> condition = new LinkedHashMap<String,Object>();
//		userBus.userManage(ja,condition,tranData, retMap);
//		userBus.userManage(ja, condition, retMap,tmd);
		
		
		
		JSONArray ja = new JSONArray();
//		String tx_code = userBean.getTx_code();
		String tx_code = tmd.get("tx_code").toString();
		LinkedHashMap<String,Object> condition = new LinkedHashMap<String,Object>();
		userBus.userManage(ja, condition, tx_code, retMap,tmd);
		retMap.put("count", tmd.get("count"));
		retMap.put("success", true);
		retMap.put("rows", ja);
		return retMap;
	}
	
	//权限不足返回
	@ResponseBody
	@RequestMapping(value="/authInvalid.html")
	public Map authInvalid(HttpServletRequest request,Object obj){
//		System.out.println("authInvalid");
		HttpSession session = request.getSession();
		Map map = new HashMap<String, String>();
//		map.put("errorCode", "01");
		map.put("errorCode", "3");
		map.put("errorMsg", "您的权限不足,请联系管理员");
		//map.put("msg", "您的权限不足,请联系管理员");
		map.put("success", true);
		return map;
	}
	
	//session超时返回
	@ResponseBody
	@RequestMapping(value="/sessionInvalid.html")
	public Map sessionInvalid(HttpServletRequest request,Object obj){
//		System.out.println("sessionTimeout");
		HttpSession session = request.getSession();
		Map map = new HashMap<String, String>();
//		map.put("errorCode", "01");
		map.put("errorCode", "2");
		map.put("errorMsg", "登录失效或未登录，请重新登录");
		//map.put("msg", "登录失效或未登录，请重新登录");
		map.put("success", true);
		return map;
	}
/*	@ResponseBody
	@RequestMapping("/naviRefresh.html")
	public Map naviRefresh(){
		Map map = new HashMap<String, String>();
		userBus.naviRefresh();
		map.put("success", true);
		return map;
	}*/
	
	
	
	//登录验证
	@ResponseBody
	@RequestMapping("/loginCheck.html")
	public Map userLogin(HttpServletRequest request,UserBean userBean){
		JSONArray userJa = new JSONArray();
		HttpSession session = request.getSession();
		iu.rmCondition(condition);
		Map map = new HashMap<String, String>();
		JSONArray ja = new JSONArray();
		String valiTag = "0";//用户合法性标识
		String passIdTag = "0";
		String userId = userBean.getUserId();
		Pattern patternAdmin = Pattern.compile("admin");
		Pattern pattern = Pattern.compile("31000");
		Matcher matcherAdmin = patternAdmin.matcher(userId);
		Matcher matcher = pattern.matcher(userId);
		if(matcher.find()){//中间业务平台传入的是passid
			session.setAttribute("passIdTag", "1");//passid标识，1：传入passid 0：传入userid
//			userId = userId.substring(1, 10);
			passIdTag = "1";
			tmdSession.put("userId", userId);
			try {
				userId = userBus.getUserIdOrPassId(ja, condition, map, passIdTag, tmdSession);//获取passid对应的userid
			} catch (Exception e) {}
			
		}else{//中间业务平台传入的是userid
			if(!matcherAdmin.find())
				userId = iu.leftZero(userId, 10);
			session.setAttribute("passIdTag", "0");
//			passIdTag = "0";
		}
		
		
		session.setAttribute("userId", userId);
		session.setAttribute("userIdLogin", userId);
		tmdSession.put("userId", userId);
		tmd.put("userIdLogin", userId);
		tmd.put("userId", userId);
		logger.debug("*********UserControler******");
		tmd.tmdLogger();
		iu.rmCondition(condition);
		iu.putCondition(condition, "userid", userId);
		try{
			valiTag = userBus.userValidate(condition);
		}catch(Exception e){
			logger.error(e);
		}
		if("0".equals(valiTag)){
			map.put("errorCode", "01");
			map.put("errorMsg", "用户验证失败");
			map.put("validate","0");
			map.put("success", true);
		}else{
			map.put("errorCode", "00");
			map.put("errorMsg", "验证通过");
			map.put("validate","1");
			map.put("success", true);
			try {
				//查询用户信息
//				userBus.userInfoQuery(userJa, tmdSession);
				userBus.userInfoQuery(userJa);
				JSONObject loginUserJson = userJa.getJSONObject(0);
				session.setAttribute("userName", loginUserJson.getString("name"));
				session.setAttribute("orgName", loginUserJson.getString("orgname"));
			} catch (Exception e) {
				logger.error("登录用户信息放入session出错");
			}
		};
		return map;
	}
	
	@RequestMapping("/toMain.html")
	public String toMain(){
		 return "redirect:/pages/main/main.jsp";
	}
	
	
	//session超时返回
	@ResponseBody
	@RequestMapping(value="/logout.html")
	public Map logout(HttpServletRequest request,Object obj){
		logger.info("系统退出");
		HttpSession session = request.getSession();
		session.invalidate();
		Map map = new HashMap<String, String>();
		map.put("success", true);
		return map;
	}
	
}
