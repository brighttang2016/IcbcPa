package com.icbc.nt.web;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bean.RoleBean;
import com.icbc.nt.bus.RoleBus;
import com.icbc.nt.util.TransactionMapData;

@Controller
public class RoleControler extends ControlerParent{
	@Autowired
	private RoleBus roleBus;
	@Autowired
	private TransactionMapData tmd;
	//角色管理
	@ResponseBody
	@RequestMapping(value="/roleMangage.html")
	public Map roleMangage(HttpServletRequest request,Object obj){
		Map map = new HashMap();
		return map;
	}
	
	@ResponseBody
	@RequestMapping(value="/roleRouter.html")
	public Map roleRouter(HttpServletRequest request,RoleBean roleBean){
		logger.info("roleRouter");
//		String tx_code = roleBean.getTx_code();
		
//		String role_auth = roleBean.getRole_auth();
//		String role_name = roleBean.getRole_name();
//		String role_desc = roleBean.getRole_desc();
//		String role_id = roleBean.getRole_id();
//		logger.info("角色Router");
//		logger.info("^^^^^^^^^^^^^^^^tx_code:"+tmd.get("tx_code"));
		tmd.tmdLogger();
//		System.out.println(tmd.get("tx_code").toString());
		String tx_code = "";
		try {
			tx_code = tmd.get("tx_code").toString();
		} catch (Exception e) {
		}
//		tmd.put("role_name", role_name);
//		tmd.put("role_desc", role_desc);
//		tmd.put("role_auth", role_auth);
//		tmd.put("role_id", role_id);
		
//		String[] proctedRoelId = new String[]{"01","147","181","182","183","381","382","383","387"};//系统预设角色，不能删除
		Map<String, Object> retMap = new HashMap<String, Object>();
		JSONArray ja = new JSONArray();
		LinkedHashMap<String,Object> condition = new LinkedHashMap<String,Object>();
		logger.info("交易路由->当前交易码：tx_code："+tx_code);
		if("10024".equals(tx_code)){
//			tmd.put("start", Integer.parseInt(roleBean.getStart())+1);
//			tmd.put("end", Integer.parseInt(roleBean.getStart()) + Integer.parseInt(roleBean.getLimit())+1);
			roleBus.roleManage(ja, condition, tx_code, retMap);
			retMap.put("count", tmd.get("count"));
		}else{
			roleBus.roleManage(ja, condition, tx_code, retMap);
		}
		retMap.put("success", true);
		retMap.put("rows", ja);
		return retMap;
	}
}
