/**
 * 权限拦截器业务逻辑
 */
package com.icbc.nt.bus;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.nt.bus.dispatcher.BusDispatcherImpl;

public class AuthInteceptorBus extends BusParent{
	@Autowired
	BusDispatcherImpl busDispatcherImpl;
	@Autowired
	MediumBus mediumBus;
	/**
	 * 用户拥有角色查询
	 * @return
	 */
	public void userRoleQuery(){
		logger.info("用户拥有角色查询开始");
		boolean isBrUser = false;//true:具有分支行角色（角色编码：），false：不具有分支行角色
		boolean isFhUser = false;//true:具有重庆分行角色，false：不具有重庆分行角色
		tmd.put("isSysInit", false);
		tmd.put("isFhUser", false);
		tmd.put("isFzhUser", false);
		tmd.put("isWdUser", false);
		tmd.tmdLogger();
		String userIdLogin = tmd.get("userIdLogin").toString();
		JSONArray userRoleJa = new JSONArray();//用户角色
		iu.rmCondition(condition);
		condition.put("user_id", userIdLogin);
		sqlStr = "select * from t_ntmisc_userrole where  user_id = ?";
		this.queryManu(userRoleJa, condition, sqlStr, daoParent, 1);
		for (int i = 0; i < userRoleJa.size(); i++) {
			JSONObject userRoleJson = userRoleJa.getJSONObject(i);
			int roleId = 0;
			try {
				roleId = userRoleJson.getIntValue("role_id");
			} catch (Exception e) {
				// TODO: handle exception
				logger.error("抛出异常,获取用户角色id失败");
			}
			switch(roleId){
			case 1://系统初始化
				tmd.put("isSysInit", true);
				break;
			case 2://重庆分行管理员
				tmd.put("isFhUser", true);
				break;
			case 3://分支行管理员
				tmd.put("isFzhUser", true);
				break;
			case 4://网点管理员
				tmd.put("isWdUser", true);
				break;
			}
		}
		logger.debug("用户拥有角色查询结束");
		tmd.tmdLogger();
	}
	
	/**
	 * 可见机构查询
	 */
	public void visibleOrg(){
		logger.debug("可见机构查询");
		JSONArray vbOrgJa = new JSONArray();
		HttpSession session = (HttpSession) tmd.get("session");
		tmd.tmdLogger();
		String brOrgId = tmd.get("brOrgId").toString();//所在分支行号
		String orgIdLogin = tmd.get("orgIdLogin").toString();//当前登录用户所在机构
		String menuid = "";//开始机构编号（可见机构开始节点）
		JSONObject jsonNow = new JSONObject();//当前机构节点,以此机构查询下属所有机构
		if(session.getAttribute("vbOrgJa") == null){
			logger.debug("vbOrgJa不存在session中");
			this.userRoleQuery();
			if((Boolean) tmd.get("isFhUser")){
				menuid = "0310000000";//重庆分行
			}else if((Boolean) tmd.get("isFzhUser")){
				menuid = brOrgId;//分支行
			}else if((Boolean) tmd.get("isWdUser")){
				menuid = orgIdLogin;//网点
			}
			jsonNow.put("menuid", menuid);
			logger.debug("当前机构节点jsonNow："+jsonNow);
			mediumBus.getSubItem(vbOrgJa, jsonNow);
			logger.debug("查询出有子机构:"+vbOrgJa.toJSONString());
			vbOrgJa.add(jsonNow);//当前用户所在机构为可见机构
			logger.debug("可见机构:"+vbOrgJa.toJSONString());
			tmd.put("vbOrgJa", vbOrgJa);
			session.setAttribute("vbOrgJa", vbOrgJa);
		}else{
			logger.debug("vbOrgJa存在session中");
			tmd.put("vbOrgJa", session.getAttribute("vbOrgJa"));
		}
		logger.debug("当前登录用户"+tmd.get("userIdLogin")+"可见机构vbOrgJa:"+((JSONArray)tmd.get("vbOrgJa")).toJSONString());
	} 
	
	/**
	 * 当前用户可见用户查询
	 * 分支行角色用户：所在分支行下属所有用户
	 * 管理员角色用户：当前机构及下属所有机构用户
	 */
	public void visibleUser(){
		logger.debug("可见用户查询");
		JSONArray vbUserJa = new JSONArray();
		String userIdLogin = "";//当前登录用户id
		String orgIdLogin = "";//当前登录用户所在机构
		String brOrgId = "";//当前登录用户所在分支行号
		String fhOrgId = "0310000000";//重庆分行机构号
		String menuid = "";//开始机构编号（可见机构开始节点）
		HttpSession session = (HttpSession) tmd.get("session");
		tmd.tmdLogger();
		userIdLogin = tmd.get("userIdLogin").toString();
		//当前登陆用户所在机构号入变量池
		if(session.getAttribute("orgIdLogin") == null || "".equals(session.getAttribute("orgIdLogin"))){
			logger.debug("orgIdLogin不存在session中");
			orgIdLogin = mediumBus.getUserOrg(userIdLogin);//当前登录用户所在机构
			logger.info("用户所在机构号："+orgIdLogin);
			session.setAttribute("orgIdLogin", orgIdLogin);
			tmd.put("orgIdLogin", orgIdLogin);
			
		}else{
			logger.debug("orgIdLogin存在session中");
			tmd.put("orgIdLogin", session.getAttribute("orgIdLogin"));
		}
		//当前登录用户所在分支行号入变量池
		if(session.getAttribute("brOrgId") == null || "".equals(session.getAttribute("brOrgId"))){
			brOrgId = busDispatcherImpl.brOrgQuery(tmd.get("orgIdLogin").toString());//当前登录用户所在分支行号
			logger.info("用户所在分支行号brOrgId："+brOrgId);
			session.setAttribute("brOrgId", brOrgId);
			tmd.put("brOrgId", brOrgId);
		}else{
			tmd.put("brOrgId", session.getAttribute("brOrgId"));
		}
		
		//当前登录可见机构部门编号入变量池。形式：("编号1","编号2",......)
		String orgIdIn = "";
		if(session.getAttribute("orgIdIn") == null){
			logger.debug("orgIdIn不存在session中");
			this.userRoleQuery();
			if((Boolean) tmd.get("isFhUser")){
				menuid = fhOrgId;//重庆分行
			}else if((Boolean) tmd.get("isFzhUser")){
				menuid = brOrgId;//分支行
			}else if((Boolean) tmd.get("isWdUser")){
				menuid = orgIdLogin;//网点
			}
			tmd.put("orgIdCurr", menuid);
			tmd.tmdLogger();
			orgIdIn = busDispatcherImpl.userOrgIn(tmd);
			session.setAttribute("orgIdIn", orgIdIn);
			tmd.put("orgIdIn", orgIdIn);
		}else{
			logger.debug("orgIdIn存在session中");
			tmd.put("orgIdIn", session.getAttribute("orgIdIn"));
		}
		tmd.tmdLogger();
		//当前登录用户可见 用户入变量池
		sqlStr = " select a.userid,a.name,a.orgid,a.depid,a.jbjx_id,b.orgname,c.depname,d.jbjx_pid,d.jbjx_name,d.jbjx_pname  from t_ntmisc_user a,t_ntmisc_org b,t_ntmisc_dept c,t_ntmisc_jobjx d "
				+  " where a.orgid = b.orgid and a.depid = c.depid and a.jbjx_id = d.jbjx_id and a.orgid in "
				+ tmd.get("orgIdIn");
		iu.rmCondition(condition);
		logger.info("当前登录用户可见用户查询");
		iu.infoDbOper("当前登录用户可见用户查询", sqlStr, condition);
		this.queryManu(vbUserJa, condition, sqlStr, daoParent, 1);
		if(session.getAttribute("vbUserJa") == null){
			logger.debug("vbUserJa不存在session中");
			session.setAttribute("vbUserJa", vbUserJa);
			tmd.put("vbUserJa", vbUserJa);
		}else{
			logger.debug("vbUserJa存在session中");
			tmd.put("vbUserJa", session.getAttribute("vbUserJa"));
		}
		logger.debug("可见用户vbUserJa:"+((JSONArray)tmd.get("vbUserJa")).toJSONString());
	}
}
