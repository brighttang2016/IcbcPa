/**
 * 参数设置控制器 20150723
 *brighttang 
 */
package com.icbc.nt.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.nt.bean.AssptBean;
import com.icbc.nt.bean.JobBean;
import com.icbc.nt.bean.NaviBean;
import com.icbc.nt.bean.RpBean;
import com.icbc.nt.bean.SsBean;
import com.icbc.nt.bean.UserBean;
import com.icbc.nt.bean.UserCtBean;
import com.icbc.nt.bus.FileUploadBus;
import com.icbc.nt.bus.ParamBus;
import com.icbc.nt.bus.dispatcher.BusDispatcherImpl;
import com.icbc.nt.util.IcbcUtil;
import com.icbc.nt.util.TransactionMapData;

@Controller
//@Scope("session")
@Scope("request")
public class ParamControler extends ControlerParent{
	Logger logger = IcbcUtil.getLogger();
	@Autowired
	private ParamBus paramBus;
	@Autowired
	private BusDispatcherImpl busDispatcherImpl;
//	TransactionMapData tmd = new TransactionMapData();
	@Autowired
	private TransactionMapData tmd;
	@PostConstruct
	public void init(){
		logger.info("@PostConstruct  init");
	}
	
	//系统初始化
	@ResponseBody
	@RequestMapping(value="/orgInit.html")
	public Map orgInit(HttpServletRequest request,UserBean userBean){
		JSONObject paramJson = new JSONObject();
		Map map = new HashMap<String, String>();
		String path = request.getSession().getServletContext().getRealPath("/");//web上下文绝对根路径
		tmd.put("txCode", "10000001");
		tmd.put("path", path);
		tmd.put("currUser", request.getSession().getAttribute("userId"));//当前登录账户
		Map<String, Object> retMap = new HashMap<String, Object>();
		JSONArray ja = new JSONArray();
		LinkedHashMap<String,Object> condition = new LinkedHashMap<String,Object>();
		paramBus.sysInit(tmd);
		retMap.put("count", tmd.get("count"));
		retMap.put("success", true);
		retMap.put("rows", ja);
		return retMap;
	}
	
	//系统初始化
	@ResponseBody
	@RequestMapping(value="/sysInit.html")
	public Map userInit(HttpServletRequest request,UserBean userBean){
		JSONObject paramJson = new JSONObject();
		Map map = new HashMap<String, String>();
		String path = request.getSession().getServletContext().getRealPath("/");//web上下文绝对根路径
//		tmd.put("txCode", userBean.getTx_code());
//		tmd.put("txCode", "10000002");
		tmd.put("path", path);
		tmd.put("currUser", request.getSession().getAttribute("userId"));//当前登录账户
		Map<String, Object> retMap = new HashMap<String, Object>();
		JSONArray ja = new JSONArray();
		LinkedHashMap<String,Object> condition = new LinkedHashMap<String,Object>();
		paramBus.sysInit(tmd);
		retMap.put("count", tmd.get("count"));
		retMap.put("success", true);
		retMap.put("rows", ja);
		return retMap;
	}
	
	//总包占比管理
	@ResponseBody
	@RequestMapping(value="/zbzbManage.html")
	public Map zbzbManage(HttpServletRequest request,UserBean userBean,String wd_id,String bl_id,String min,String max,String data){
		JSONObject paramJson = new JSONObject();
		Map map = new HashMap<String, String>();
//		tmd.put("userId", userBean.getUserId());//查询区userId
//				tmd.put("ass_flag", userBean.getAss_flag());
//		tmd.put("depId", userBean.getDeptId());
//		tmd.put("orgId", userBean.getOrgId());
//		tmd.put("currUser", request.getSession().getAttribute("userId"));//当前登录账户
//		tmd.put("txCode", userBean.getTx_code());
//		tmd.put("wdId", wd_id);
//		tmd.put("min", min);
//		tmd.put("max", max);
//		tmd.put("blId", bl_id);
//		tmd.put("data", data);
//		try {
//			paramJson = JSONObject.parseObject(data);
//			this.tmd.put("txCode", paramJson.getString("txCode"));
//		} catch (Exception e) {}
		
//		try {
//			tmd.put("start", Integer.parseInt(userBean.getStart())+1);
//			tmd.put("end", Integer.parseInt(userBean.getStart()) + Integer.parseInt(userBean.getLimit())+1);
//		} catch (Exception e) {}
		Map<String, Object> retMap = new HashMap<String, Object>();
		JSONArray ja = new JSONArray();
		LinkedHashMap<String,Object> condition = new LinkedHashMap<String,Object>();
//				paramBus.assManage(ja, condition, retMap,tmd);
		paramBus.zbzbManage(ja, condition, retMap, tmd);
		retMap.put("count", tmd.get("count"));
		retMap.put("success", true);
		retMap.put("rows", ja);
		return retMap;
	}
	
	//考核任务管理
	@ResponseBody
	@RequestMapping(value="/param_khrw.html")
	public Map khrwManage(HttpServletRequest request,UserBean userBean,String zq,String timeStart,String timeEnd,String data){
		JSONObject paramJson = new JSONObject();
		Map map = new HashMap<String, String>();
//		tmd.put("userId", userBean.getUserId());//查询区userId
//				tmd.put("ass_flag", userBean.getAss_flag());
//		tmd.put("depId", userBean.getDeptId());
//		tmd.put("orgId", userBean.getOrgId());
//		tmd.put("currUser", request.getSession().getAttribute("userId"));//当前登录账户
//		tmd.put("txCode", userBean.getTx_code());
//		tmd.put("zq", zq);
//		tmd.put("timeStart", timeStart);
//		tmd.put("timeEnd", timeEnd);
//		tmd.put("data", data);
//		try {
//			paramJson = JSONObject.parseObject(data);
//			this.tmd.put("txCode", paramJson.getString("txCode"));
//		} catch (Exception e) {}
		
//		try {
//			tmd.put("start", Integer.parseInt(userBean.getStart())+1);
//			tmd.put("end", Integer.parseInt(userBean.getStart()) + Integer.parseInt(userBean.getLimit())+1);
//		} catch (Exception e) {}
		Map<String, Object> retMap = new HashMap<String, Object>();
		JSONArray ja = new JSONArray();
		LinkedHashMap<String,Object> condition = new LinkedHashMap<String,Object>();
//				paramBus.assManage(ja, condition, retMap,tmd);
		busDispatcherImpl.khrwManage(ja, condition, retMap);
		retMap.put("count", tmd.get("count"));
		retMap.put("success", true);
		retMap.put("rows", ja);
		return retMap;
	}
	
	//权重区间管理
	@ResponseBody
	@RequestMapping(value="/param_qzqj.html")
	public Map qzqjManage(HttpServletRequest request,UserBean userBean,String data){
		JSONObject paramJson = new JSONObject();
		Map map = new HashMap<String, String>();
		tmd.put("userId", userBean.getUserId());//查询区userId
//			tmd.put("ass_flag", userBean.getAss_flag());
		tmd.put("depId", userBean.getDeptId());
		tmd.put("orgId", userBean.getOrgId());
		tmd.put("currUser", request.getSession().getAttribute("userId"));//当前登录账户
		tmd.put("txCode", userBean.getTx_code());
		tmd.put("data", data);
		try {
			paramJson = JSONObject.parseObject(data);
			this.tmd.put("txCode", paramJson.getString("txCode"));
		} catch (Exception e) {}
		
		try {
			tmd.put("start", Integer.parseInt(userBean.getStart())+1);
			tmd.put("end", Integer.parseInt(userBean.getStart()) + Integer.parseInt(userBean.getLimit())+1);
		} catch (Exception e) {}
		Map<String, Object> retMap = new HashMap<String, Object>();
		JSONArray ja = new JSONArray();
		LinkedHashMap<String,Object> condition = new LinkedHashMap<String,Object>();
//			paramBus.assManage(ja, condition, retMap,tmd);
		busDispatcherImpl.qzqjManage(ja, condition, retMap, tmd);
		retMap.put("count", tmd.get("count"));
		retMap.put("success", true);
		retMap.put("rows", ja);
		return retMap;
	}
	
	//用户认证资格管理
	@ResponseBody
	@RequestMapping(value="/uctManage.html")
	public Map uctManage(HttpServletRequest request,UserCtBean userCtBean,String data){
		Map map = new HashMap<String, String>();
		tmd.put("userId", userCtBean.getUserId());//查询区userId
		tmd.put("ctId", userCtBean.getCtId());
		tmd.put("depId", userCtBean.getDeptId());
		tmd.put("orgId", userCtBean.getOrgId());
		tmd.put("currUser", request.getSession().getAttribute("userId"));//当前登录账户
		tmd.put("txCode", userCtBean.getTx_code());
		tmd.put("data", data);
		try {
			tmd.put("start", Integer.parseInt(userCtBean.getStart())+1);
			tmd.put("end", Integer.parseInt(userCtBean.getStart()) + Integer.parseInt(userCtBean.getLimit())+1);
		} catch (Exception e) {}
		Map<String, Object> retMap = new HashMap<String, Object>();
		JSONArray ja = new JSONArray();
		LinkedHashMap<String,Object> condition = new LinkedHashMap<String,Object>();
		paramBus.uctManage(ja, condition, retMap,tmd);
		retMap.put("count", tmd.get("count"));
		retMap.put("success", true);
		retMap.put("rows", ja);
		return retMap;
	}
	
	
	
	//不考核人员管理
	@ResponseBody
	@RequestMapping(value="/assManage.html")
	public Map assManage(HttpServletRequest request,UserBean userBean,String data){
		JSONObject paramJson = new JSONObject();
		Map map = new HashMap<String, String>();
		tmd.put("userId", userBean.getUserId());//查询区userId
		tmd.put("ass_flag", userBean.getAss_flag());
		tmd.put("depId", userBean.getDeptId());
		tmd.put("orgId", userBean.getOrgId());
		tmd.put("currUser", request.getSession().getAttribute("userId"));//当前登录账户
		tmd.put("txCode", userBean.getTx_code());
		tmd.put("data", data);
		try {
			paramJson = JSONObject.parseObject(data);
			this.tmd.put("txCode", paramJson.getString("txCode"));
		} catch (Exception e) {}
		
		try {
			tmd.put("start", Integer.parseInt(userBean.getStart())+1);
			tmd.put("end", Integer.parseInt(userBean.getStart()) + Integer.parseInt(userBean.getLimit())+1);
		} catch (Exception e) {}
		Map<String, Object> retMap = new HashMap<String, Object>();
		JSONArray ja = new JSONArray();
		LinkedHashMap<String,Object> condition = new LinkedHashMap<String,Object>();
		paramBus.assManage(ja, condition, retMap,tmd);
		retMap.put("count", tmd.get("count"));
		retMap.put("success", true);
		retMap.put("rows", ja);
		return retMap;
	}
	
	//职务层级自动晋升批量计算
	@ResponseBody
	@RequestMapping(value="/jobCalc.html")
	public Map jobCalc(HttpServletRequest request){
		Map map = new HashMap<String, String>();
		Map<String, Object> retMap = new HashMap<String, Object>();
		JSONArray ja = new JSONArray();
		HttpSession session  = request.getSession();
		tmd.put("userId", session.getAttribute("userId"));
		LinkedHashMap<String,Object> condition = new LinkedHashMap<String,Object>();
		paramBus.jobCalc(ja, condition, retMap,tmd);
	//	retMap.put("count", tmd.get("count"));
		retMap.put("success", true);
		retMap.put("rows", ja);
		return retMap;
	}
	
	//工资等级及工资档次计算
	@ResponseBody
	@RequestMapping(value="/wglCalc.html")
	public Map wglCalc(HttpServletRequest request){
		Map map = new HashMap<String, String>();
		Map<String, Object> retMap = new HashMap<String, Object>();
		JSONArray ja = new JSONArray();
		HttpSession session  = request.getSession();
		tmd.put("userId", session.getAttribute("userId"));
		LinkedHashMap<String,Object> condition = new LinkedHashMap<String,Object>();
		paramBus.wglCalc(ja, condition, retMap,tmd);
	//	retMap.put("count", tmd.get("count"));
		retMap.put("success", true);
		retMap.put("rows", ja);
		return retMap;
	}
	
	//积分批量计算
	@ResponseBody
	@RequestMapping(value="/pointCalc.html")
	public Map pointCalc(HttpServletRequest request){
		Map map = new HashMap<String, String>();
		Map<String, Object> retMap = new HashMap<String, Object>();
		JSONArray ja = new JSONArray();
		HttpSession session  = request.getSession();
		tmd.put("userId", session.getAttribute("userId"));
		LinkedHashMap<String,Object> condition = new LinkedHashMap<String,Object>();
		paramBus.pointCalc(ja, condition, retMap,tmd);
	//	retMap.put("count", tmd.get("count"));
		retMap.put("success", true);
		retMap.put("rows", ja);
		return retMap;
	}
	
	//积分类型查询
	@ResponseBody
	@RequestMapping(value="/cTypeQuery.html")
	public Map cTypeQuery(HttpServletRequest request){
		Map map = new HashMap<String, String>();
		Map<String, Object> retMap = new HashMap<String, Object>();
		JSONArray ja = new JSONArray();
		String cTypeId = request.getParameter("cTypeId");
		String cTypePid = request.getParameter("cTypePid");
		tmd.put("cTypeId", cTypeId);
		tmd.put("cTypePid", cTypePid);
		LinkedHashMap<String,Object> condition = new LinkedHashMap<String,Object>();
		paramBus.cTypeQuery(ja, condition, retMap,tmd);
	//	retMap.put("count", tmd.get("count"));
		retMap.put("success", true);
		retMap.put("rows", ja);
		return retMap;
	}
	
	@ResponseBody
	@RequestMapping(value="/creditManage.html")
	public Map creditManage(HttpServletRequest request,UserBean userBean,String data){
		Map map = new HashMap<String, String>();
//		iu.putCondition(condition, "userid", userBean.getUserId());
//		tmd.put("attStat", userBean.getAttStat());
	tmd.put("txCode", userBean.getTx_code());
	tmd.put("userId", userBean.getUserId());
	tmd.put("name", userBean.getName());
//	tmd.put("sex", userBean.getSex());
	tmd.put("depId", userBean.getDeptId());
	tmd.put("orgId", userBean.getOrgId());
//	tmd.put("mail", userBean.getMail());
//	tmd.put("beginDate", userBean.getBeginDate());//开始工作日期
//	tmd.put("propertyId", userBean.getPropertyId());//人员类别
//	tmd.put("jbId", userBean.getJbId());//获取岗位
//	tmd.put("roleSelect",userBean.getRoleSelect());
	tmd.put("currUser", request.getSession().getAttribute("userId"));//当前登录账户
	tmd.put("data", data);
	try {
		tmd.put("start", Integer.parseInt(userBean.getStart())+1);
		tmd.put("end", Integer.parseInt(userBean.getStart()) + Integer.parseInt(userBean.getLimit())+1);
	} catch (Exception e) {}
	Map<String, Object> retMap = new HashMap<String, Object>();
	JSONArray ja = new JSONArray();
//	String tx_code = userBean.getTx_code();
	LinkedHashMap<String,Object> condition = new LinkedHashMap<String,Object>();
	paramBus.creditManage(ja, condition, retMap,tmd);
	retMap.put("count", tmd.get("count"));
	retMap.put("success", true);
	retMap.put("rows", ja);
	return retMap;
	}
	
/*	//当前用户可见用户查询
	@ResponseBody
	@RequestMapping(value="/userQuery.html")
	public Map userQuery(HttpServletRequest request,String userIdQuery){
		Map map = new HashMap<String, String>();
		HttpSession session = request.getSession();
		String userId = session.getAttribute("userId").toString();
		tmd.put("userId", userId);
		tmd.put("userIdQuery", userIdQuery);
		Map<String, Object> retMap = new HashMap<String, Object>();
		JSONArray ja = new JSONArray();
		
		LinkedHashMap<String,Object> condition = new LinkedHashMap<String,Object>();
		paramBus.userQuery(ja, condition, retMap, tmd);
//		retMap.put("count", tmd.get("count"));
		retMap.put("success", true);
		retMap.put("rows", ja);
		return retMap;
	}*/
	
	//员工考核奖励信息管理(reward punnish)
	@ResponseBody
	@RequestMapping(value="/rpManage.html")
	public Map rpManage(RpBean rpBean,String data,String rpTimeQuery,String userIdQuery){
		Map map = new HashMap<String, String>();
		tmd.put("userId", rpBean.getUserId());
		tmd.put("userName", rpBean.getUserName());
		tmd.put("rpTime", rpBean.getRpTime());
//		tmd.put("assptId", rpBean.getAssptId());
		tmd.put("rpPoint", rpBean.getRpPoint());
//		tmd.put("rpPoint", rpBean.getRpPoint());
		tmd.put("rpMsg", rpBean.getRpMsg());
		
		tmd.put("txCode", rpBean.getTx_code());
		tmd.put("data", data);
		logger.info("data:"+data);
//		JSONArray jsonArray = JSONArray.parseArray(data);
		try {
			tmd.put("start", Integer.parseInt(rpBean.getStart())+1);
			tmd.put("end", Integer.parseInt(rpBean.getStart()) + Integer.parseInt(rpBean.getLimit())+1);
		} catch (Exception e) {}
		
		tmd.put("rpTimeQuery", rpTimeQuery);
		tmd.put("userIdQuery", userIdQuery);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		JSONArray ja = new JSONArray();
		
		LinkedHashMap<String,Object> condition = new LinkedHashMap<String,Object>();
		paramBus.rpManage(ja, condition, retMap, tmd);
		retMap.put("count", tmd.get("count"));
		retMap.put("success", true);
		retMap.put("rows", ja);
		return retMap;
	}
	
	//考核积分对照表(assess point)
	@ResponseBody
	@RequestMapping(value="/assptManage.html")
	public Map assptManage(AssptBean assptBean){
		Map map = new HashMap<String, String>();
		tmd.put("assptId", assptBean.getAssptId());
		tmd.put("assptName", assptBean.getAssptName());
		tmd.put("point", assptBean.getPoint());
		tmd.put("note", assptBean.getNote());
		tmd.put("cond", assptBean.getCond());
		tmd.put("txCode", assptBean.getTx_code());
		
		try {
			tmd.put("start", Integer.parseInt(assptBean.getStart())+1);
			tmd.put("end", Integer.parseInt(assptBean.getStart()) + Integer.parseInt(assptBean.getLimit())+1);
		} catch (Exception e) {}
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		JSONArray ja = new JSONArray();
		
		LinkedHashMap<String,Object> condition = new LinkedHashMap<String,Object>();
		paramBus.assptManage(ja, condition, retMap, tmd);
		retMap.put("count", tmd.get("count"));
		retMap.put("success", true);
		retMap.put("rows", ja);
		return retMap;
	}
	
	//行龄补贴管理(subsity manage)
	@ResponseBody
	@RequestMapping(value="/ssManage.html")
	public Map ssManage(SsBean ssBean){
		Map map = new HashMap<String, String>();
		tmd.put("ssId", ssBean.getSsId());
		tmd.put("yearStart", ssBean.getYearStart());//查询区userId
		tmd.put("yearEnd", ssBean.getYearEnd());
		tmd.put("subsity", ssBean.getSubsity());
		tmd.put("note", ssBean.getNote());
		tmd.put("txCode", ssBean.getTx_code());
		
		try {
			tmd.put("start", Integer.parseInt(ssBean.getStart())+1);
			tmd.put("end", Integer.parseInt(ssBean.getStart()) + Integer.parseInt(ssBean.getLimit())+1);
		} catch (Exception e) {}
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		JSONArray ja = new JSONArray();
		
		LinkedHashMap<String,Object> condition = new LinkedHashMap<String,Object>();
		paramBus.ssManage(ja, condition, retMap, tmd);
		retMap.put("count", tmd.get("count"));
		retMap.put("success", true);
		retMap.put("rows", ja);
		return retMap;
	}
	
	//工资档次查询
	@ResponseBody
	@RequestMapping("wlQuery.html")
	public Map wlQuery(String wgId){
		HashMap retMap = new HashMap();
		JSONArray ja = new JSONArray();
		LinkedHashMap<String , Object> condition = new LinkedHashMap<String, Object>();
		tmd.put("wgId", wgId);
		paramBus.wlQuery(ja, condition, retMap, tmd);
		retMap.put("success", true);
		retMap.put("rows", ja);
		return retMap;
	}
	
	//工资等级查询
	@ResponseBody
	@RequestMapping("/wgQuery.html")
	public Map wgQuery(HttpServletRequest request){
		HashMap retMap = new HashMap();
		JSONArray ja = new JSONArray();
		LinkedHashMap<String , Object> condition = new LinkedHashMap<String, Object>();
		paramBus.wgQuery(ja, condition, retMap, tmd);
		retMap.put("success", true);
		retMap.put("rows", ja);
		return retMap;
	}
	//岗位层级查询
	@ResponseBody
	@RequestMapping(value="/dtQuery.html")
	public Map dtQuery(HttpServletRequest request,NaviBean naviBean){
		HttpSession session = request.getSession();
		JSONArray ja = new JSONArray();
		HashMap retMap = new HashMap();
		LinkedHashMap<String , Object> condition = new LinkedHashMap<String, Object>();
		paramBus.dtQuery(ja, condition, retMap, tmd);
		retMap.put("success", true);
		retMap.put("rows", ja);
		return retMap;
	}
	
	//认证资格表查询
	@ResponseBody
	@RequestMapping(value="/ctQuery.html")
	public Map ctQuery(HttpServletRequest request,NaviBean naviBean){
		HttpSession session = request.getSession();
		JSONArray ja = new JSONArray();
		HashMap retMap = new HashMap();
		LinkedHashMap<String , Object> condition = new LinkedHashMap<String, Object>();
		paramBus.ctQuery(ja, condition, retMap, tmd);
		retMap.put("success", true);
		retMap.put("rows", ja);
		return retMap;
	}
	
	//职务层级管理
	@ResponseBody
	@RequestMapping(value="/jobManage.html")
	public Map jobManage(HttpServletRequest request,JobBean jobBean){
		HttpSession session = request.getSession();
		JSONArray ja = new JSONArray();
		HashMap retMap = new HashMap();
		LinkedHashMap<String , Object> condition = new LinkedHashMap<String, Object>();
		String tx_code = jobBean.getTx_code();
		logger.debug("tx_code:"+tx_code);
//		tmd.put("txCode", tx_code);//在用户管理页面，如果未该用户未赋值岗位层级查询权限，页面显示异常，故：前端岗位层级查询不再传入tx_code交易码，在如下直接赋值10044，在业务逻辑执行10044交易
		if("00000000".equals(tx_code))
			tmd.put("txCode", "10044");
		else
			tmd.put("txCode", tx_code);
//		tmd.put("currUser", session.getAttribute("userId"));
		tmd.put("pId", jobBean.getpId());
		tmd.put("pName", jobBean.getpName());
		tmd.put("menuTreeId", jobBean.getMenuTreeId());
		tmd.put("menuName", jobBean.getMenuName());
		tmd.put("ctId", jobBean.getCtId());
		tmd.put("dtId", jobBean.getDtId());
		tmd.put("mGrade", jobBean.getmGrade());
		tmd.put("sGrade", jobBean.getsGrade());
		tmd.put("sLevel", jobBean.getsLevel());
		tmd.put("menuId", jobBean.getMenuId());
		tmd.put("seqNum", jobBean.getSeqNum());
		tmd.tmdLogger();
		
//		paramBus.jobManage(ja, condition, retMap,tmd);//2015-11-18命令抽取
		busDispatcherImpl.zwcj(ja, condition, retMap, tmd);
		
		retMap.put("success", true);
		retMap.put("rows", ja);
		return retMap;
	}
}
