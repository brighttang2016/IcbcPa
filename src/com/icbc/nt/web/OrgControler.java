package com.icbc.nt.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import com.icbc.nt.bean.MenuBean;
import com.icbc.nt.bean.NaviBean;
import com.icbc.nt.bean.RoleBean;
import com.icbc.nt.bean.UserBean;
import com.icbc.nt.bus.OrgBus;
import com.icbc.nt.bus.UserBus;
import com.icbc.nt.util.IcbcUtil;
import com.icbc.nt.util.TransactionMapData;

@Scope("request")
@Controller
public class OrgControler extends ControlerParent{
	@Autowired
	private OrgBus orgBus;
//	@Autowired
//	private TransactionMapData tmd;
//	private TransactionMapData tmd = new TransactionMapData();
	@Autowired
	private TransactionMapData tmd;
	private String sessionTimeout;
	
	public String getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(String sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

	
	@ResponseBody
	@RequestMapping(value="/getAllOrg.html")
	public Map getAllOrg(HttpServletRequest request,NaviBean naviBean){
		HttpSession session = request.getSession();
		JSONArray ja = new JSONArray();
		HashMap retMap = new HashMap();
		LinkedHashMap<String , Object> condition = new LinkedHashMap<String, Object>();
		orgBus.orgManage(ja, condition, "10035", retMap,tmd);
		retMap.put("success", true);
		retMap.put("rows", ja);
		return retMap;
	}

	@ResponseBody
	@RequestMapping(value="/orgTreeQuery.html")
	public Map orgTreeQuery(HttpServletRequest request,NaviBean naviBean){
		HttpSession session = request.getSession();
		JSONArray ja = new JSONArray();
		HashMap retMap = new HashMap();
		LinkedHashMap<String , Object> condition = new LinkedHashMap<String, Object>();
		tmd.put("currUser", session.getAttribute("userId"));
		tmd.put("pId", naviBean.getpId());
		tmd.put("pName", naviBean.getpName());
		tmd.put("addType", naviBean.getAddType());
		tmd.put("menuTreeId", naviBean.getMenuTreeId());
		tmd.put("menuId", naviBean.getMenuId());
		tmd.put("orgId", naviBean.getOrgId());
		if("-1".equals(naviBean.getMenuType()))
			tmd.put("menuName", "根节点");
		else
			tmd.put("menuName", naviBean.getMenuName());
		tmd.put("menuType", naviBean.getMenuType());
		logger.debug("naviBean.getMenuTreeId():"+naviBean.getMenuTreeId()
				+"+naviBean.getpId():"+naviBean.getpId()+"|naviBean.getpName():"+naviBean.getpName());
		String tx_code = naviBean.getTx_code();
		orgBus.orgTreeQuery(ja, condition, tx_code, retMap,tmd);
		retMap.put("success", true);
		retMap.put("rows", ja);
		return retMap;
	}
	
	@ResponseBody
	@RequestMapping(value="/orgManage.html")
	public Map orgManage(HttpServletRequest request,NaviBean naviBean){
		HttpSession session = request.getSession();
		JSONArray ja = new JSONArray();
		HashMap retMap = new HashMap();
		LinkedHashMap<String , Object> condition = new LinkedHashMap<String, Object>();
		tmd.put("currUser", session.getAttribute("userId"));
		tmd.put("pId", naviBean.getpId());
		tmd.put("pName", naviBean.getpName());
		tmd.put("addType", naviBean.getAddType());
		tmd.put("menuTreeId", naviBean.getMenuTreeId());
		tmd.put("menuId", naviBean.getMenuId());
		tmd.put("orgId", naviBean.getOrgId());
		if("-1".equals(naviBean.getMenuType()))
			tmd.put("menuName", "根节点");
		else
			tmd.put("menuName", naviBean.getMenuName());
		tmd.put("menuType", naviBean.getMenuType());
		logger.info("naviBean.getMenuTreeId():"+naviBean.getMenuTreeId()
				+"+naviBean.getpId():"+naviBean.getpId()+"|naviBean.getpName():"+naviBean.getpName());
		String tx_code = naviBean.getTx_code();
		orgBus.orgManage(ja, condition, tx_code, retMap,tmd);
		retMap.put("success", true);
		retMap.put("rows", ja);
		return retMap;
	}
}
