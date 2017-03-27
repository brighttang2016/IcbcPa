package com.icbc.nt.web;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bean.RoleBean;
import com.icbc.nt.bus.MenuBus;
import com.icbc.nt.bus.UserBus;
import com.icbc.nt.util.TransactionMapData;
@Controller
public class MenuControler extends ControlerParent{
	@Autowired
	private MenuBus menuBus;
	@Autowired
	private TransactionMapData tmd;
	
	/**
	 * 获取所有可见menu
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/getMenuItemAll.html")
	public JSONArray getMenuItemAll(HttpServletRequest request,RoleBean roleBean){
		logger.info("getMenuItemAll");
		String role_id = roleBean.getRole_id();
		JSONArray ja = new JSONArray();
		JSONArray jaCheck = new JSONArray();//设置根节点是否选中，临时array
		LinkedHashMap<String,Object> condition = new LinkedHashMap<String,Object>();
//		iu.putCondition(condition, "user_id", session.get("userId").toString());
		menuBus.getMenuItemAll(ja, condition);
		iu.putCondition(condition, "role_id", role_id);
		menuBus.setMenuItem(ja, condition);
		logger.debug("转换前ja："+ja.toJSONString());
//		jaCheck = ja;
		for(int i = 0;i < ja.size();i++){
			jaCheck.add(ja.get(i));
		}
		iu.parJsonArr(ja, jaCheck);
		iu.traversalJsonArr(ja);
		
//		this.setResonseJson(ja);
//		String strRet = "json";
		return ja;
	}
	
	/**
	 * 获取导航条menuItem
	 * @return
	 */
	@ResponseBody
//	@RequestMapping(value="/getMenuItem.html")
	@RequestMapping("/getMenuItem.html")
	public JSONArray getMenuItem(HttpServletRequest request,Object obj){
		logger.debug("getMenuItem");
		String userId = "";
		HttpSession session = request.getSession();
		String menuId = request.getParameter("menuId");
		String menuTreeId = request.getParameter("menuTreeId");
//		String userId = request.getParameter("user_id");
		if(menuTreeId == null){
			menuTreeId = "-1";
		}
		//展开模块
		if(menuId == null){
			menuId = "0";
		}
		try{
			userId = session.getAttribute("userId").toString();
		}catch(Exception e){}
		
		
		JSONArray jaMenuId = new JSONArray();
		LinkedHashMap<String, Object> condition = new LinkedHashMap<String, Object>();
		iu.putCondition(condition, "user_id", userId);
		logger.debug("jaMenuId:"+jaMenuId+"|"+"userId:"+userId+"|"+"condition:"+condition);
		menuBus.getMenuIdAll(jaMenuId, userId, condition);//获取当前用户拥有角色所见menuid
		JSONArray ja = new JSONArray();
		menuBus.getMenuItem(ja,menuId,menuTreeId,userId,jaMenuId);//根据用户id查询该用户可见导航（待实现：2014-12-30）
		/*retMap.put("success", true);
		retMap.put("validate","1");
		retMap.put("msg", "验证通过");
		retMap.put("root", ja);*/
//		this.setResonseJson(ja);
		String strRet = "json";
		return ja;
	}
	
	@ResponseBody
	@RequestMapping("/naviRefresh.html")
	public Map naviRefresh(){
		Map map = new HashMap<String, String>();
		menuBus.naviRefresh();
		map.put("success", true);
		return map;
	}
	/*@ResponseBody
	@RequestMapping("/naviRefresh2.html")
	public Map naviRefresh2(){
		Map map = new HashMap<String, String>();
//		userBus.naviRefresh();
		map.put("success", true);
		return map;
	}*/
}
