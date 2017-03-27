package com.icbc.nt.web;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.nt.bean.UserBean;
import com.icbc.nt.bus.JxBus;
import com.icbc.nt.bus.MediumBus;
import com.icbc.nt.bus.dispatcher.BusDispatcherImpl;
import com.icbc.nt.util.IcbcUtil;
import com.icbc.nt.util.TransactionMapData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Scope("request")
public class JxControler extends ControlerParent {
	Logger logger = IcbcUtil.getLogger();
	@Autowired
	BusDispatcherImpl busDispatcherImpl;
	@Autowired MediumBus mediumBus;
	@Autowired
	private JxBus jxBus;
	//TransactionMapData tmd = new TransactionMapData();
	@Autowired
	private TransactionMapData tmd;

	
	//网点手动分配总包查询
	@ResponseBody
	@RequestMapping({ "/zbSdfpQuery.html" })
	public Map zbSdfpQuery(HttpServletRequest request,UserBean userBean) {
		Map retMap = new HashMap();
		JSONArray ja = new JSONArray();
		LinkedHashMap condition = new LinkedHashMap();
		HttpSession session = request.getSession();
		tmd.put("userId", session.getAttribute("userId"));
		try {
			tmd.put("start", Integer.parseInt(userBean.getStart())+1);
			tmd.put("end", Integer.parseInt(userBean.getStart()) + Integer.parseInt(userBean.getLimit())+1);
		} catch (Exception e) {}
		busDispatcherImpl.zbSdfpQuery(ja, condition, retMap, tmd);
		retMap.put("count", tmd.get("count"));
		retMap.put("success", Boolean.valueOf(true));
		retMap.put("rows", ja);
		return retMap;
	}
	
	//下载绩效计算结果
	@RequestMapping(value="/jxDownload.html")
	public void jxDownload(HttpServletRequest request,HttpServletResponse response,UserBean userBean,String zqQuery,String data) throws IOException{
		logger.info("绩效文件下载");
		String txCode = request.getParameter("tx_code");
		String path = request.getSession().getServletContext().getRealPath("/");//web上下文绝对根路径
		tmd.put("txCode",userBean.getTx_code());
		tmd.put("path", path);
		tmd.put("response", response);
		tmd.put("userId", request.getSession().getAttribute("userId"));
		/*tmd.put("zqQuery", zqQuery);
		tmd.put("orgId", userBean.getOrgId());
		tmd.put("depId", userBean.getDeptId());*/
		//历史绩效导出(需要表单数据)
		try {
			JSONObject dataJson = JSONObject.parseObject(data);
			tmd.put("zqQuery", dataJson.getString("zqQuery"));
			tmd.put("orgId", dataJson.getString("orgId"));
			tmd.put("depId", dataJson.getString("depId"));
		} catch (Exception e) {
			// TODO: handle exception
		}
		mediumBus.fileDownLoad(tmd);
	}
	
	//总包插入
	@ResponseBody
	@RequestMapping({ "/zbControler.html" })
	public Map zbControler(HttpServletRequest request,String data) {
		Map retMap = new HashMap();
		JSONArray ja = new JSONArray();
		LinkedHashMap condition = new LinkedHashMap();
		HttpSession session = request.getSession();
		String txCode = request.getParameter("tx_code");
		tmd.put("userId", session.getAttribute("userId"));
		tmd.put("data", data);
		tmd.put("txCode", txCode);
		jxBus.zbInsert(ja, condition, retMap, tmd);
		retMap.put("success", Boolean.valueOf(true));
		retMap.put("rows", ja);
		return retMap;
	}
	
	//当前机构号下属所有网点查询
	@ResponseBody
	@RequestMapping({ "/zbOrgControler.html" })
	public Map zbOrgControler(HttpServletRequest request) {
		Map retMap = new HashMap();
		JSONArray ja = new JSONArray();
		LinkedHashMap condition = new LinkedHashMap();
		HttpSession session = request.getSession();
		tmd.put("userId", session.getAttribute("userId"));
//		jxBus.zbOrgManage(ja, condition, retMap, tmd);
		busDispatcherImpl.wdQuery(ja, condition, retMap, tmd);
//			retMap.put("count", tmd.get("count"));
		retMap.put("success", Boolean.valueOf(true));
		retMap.put("rows", ja);
		return retMap;
	}
	
	//指标（产品）下拉框控制器
	@ResponseBody
	@RequestMapping({ "/zbComboControler.html" })
	public Map zbComboControler(HttpServletRequest request) {
		Map retMap = new HashMap();
		JSONArray ja = new JSONArray();
		LinkedHashMap condition = new LinkedHashMap();
		HttpSession session = request.getSession();
		String index = request.getParameter("index");
		String seqNumQuery = request.getParameter("seqNumQuery");
		tmd.put("index", index);
		if(!"all".equals(seqNumQuery))
			tmd.put("seqNumQuery", seqNumQuery);
		jxBus.zbManage(ja, condition, retMap, tmd);
//		retMap.put("count", tmd.get("count"));
		retMap.put("success", Boolean.valueOf(true));
		retMap.put("rows", ja);
		return retMap;
	}
	
	//指标范围编辑控制器
	@ResponseBody
	@RequestMapping({ "/zbfwEdit.html" })
	public Map zbfwEdit(HttpServletRequest request,String data) {
		Map map = new HashMap();
		Map retMap = new HashMap();
		JSONArray ja = new JSONArray();
		LinkedHashMap condition = new LinkedHashMap();
		HttpSession session = request.getSession();
		JSONObject paramJson = new JSONObject();//接收前端发送json报文：data（机构标准产品指标编辑，发送的是json报文）
		try {
			paramJson = JSONObject.parseObject(data);
		} catch (Exception e) {}
		tmd.put("txCode", paramJson.getString("txCode"));
		tmd.put("orgid", paramJson.getString("orgid"));
		tmd.put("seq_num", paramJson.getString("seq_num"));
		tmd.put("org_point", paramJson.getString("org_point"));
		jxBus.zbfwManage(ja, condition, retMap, tmd);
		retMap.put("count", tmd.get("count"));
		retMap.put("success", Boolean.valueOf(true));
		retMap.put("rows", ja);
		return retMap;
	}
	
	//指标范围控制器
	@ResponseBody
	@RequestMapping({ "/zbfwControler.html" })
	public Map zbfwControler(HttpServletRequest request,UserBean userBean,String data) {
		Map map = new HashMap();
		Map retMap = new HashMap();
		JSONArray ja = new JSONArray();
		LinkedHashMap condition = new LinkedHashMap();
		HttpSession session = request.getSession();
		this.tmd.put("userId", session.getAttribute("userId"));
		this.tmd.put("txCode", userBean.getTx_code());
		this.tmd.put("depId", userBean.getDeptId());
		this.tmd.put("orgId", userBean.getOrgId());
		JSONObject paramJson = new JSONObject();//接收前端发送json报文：data（机构标准产品指标编辑，发送的是json报文）
		try {
			paramJson = JSONObject.parseObject(data);
			this.tmd.put("txCode", paramJson.getString("txCode"));
		} catch (Exception e) {}
		
		tmd.put("data", data);
		tmd.put("scope", request.getParameter("scope"));//作用域 1：当前机构  2：当前机构及下属所有机构
		iu.putCondition(condition, "userid", userBean.getUserId());
//			iu.putCondition(condition, "orgid", userBean.getOrgId());
		try {
			tmd.put("start", Integer.parseInt(userBean.getStart())+1);
			tmd.put("end", Integer.parseInt(userBean.getStart()) + Integer.parseInt(userBean.getLimit())+1);
		} catch (Exception e) {}
		jxBus.zbfwManage(ja, condition, retMap, tmd);
		retMap.put("count", tmd.get("count"));
		retMap.put("success", Boolean.valueOf(true));
		retMap.put("rows", ja);
		return retMap;
	}
	
	//数据初始化控制器
	@ResponseBody
	@RequestMapping({ "/initDataControler.html" })
	public Map initDataControler(HttpServletRequest request,UserBean userBean,String zqQuery) {
		logger.info("initDataControler");
		Map map = new HashMap();
		Map retMap = new HashMap();
		JSONArray ja = new JSONArray();
		LinkedHashMap condition = new LinkedHashMap();
		HttpSession session = request.getSession();
		this.tmd.put("userId", session.getAttribute("userId"));
		this.tmd.put("txCode", userBean.getTx_code());
		this.tmd.put("depId", userBean.getDeptId());
		this.tmd.put("orgId", userBean.getOrgId());
		tmd.put("userIdQuery", userBean.getUserId());
		tmd.put("zqQuery", zqQuery);//人员分配导入查询
//		iu.putCondition(condition, "userid", userBean.getUserId());
//		iu.putCondition(condition, "orgid", userBean.getOrgId());
		tmd.put("start", Integer.parseInt(userBean.getStart())+1);
		try {
			tmd.put("end", Integer.parseInt(userBean.getStart()) + Integer.parseInt(userBean.getLimit())+1);
		} catch (Exception e) {}
		jxBus.initDataManage(ja, condition, retMap, tmd);
		retMap.put("count", tmd.get("count"));
		retMap.put("success", Boolean.valueOf(true));
		retMap.put("rows", ja);
		return retMap;
	}
	
	//绩效周期查询
	@ResponseBody
	@RequestMapping({ "/jxZqControler.html" })
	public Map jxZqControler(HttpServletRequest request,HttpServletResponse response,String txCode,UserBean userBean) {
		Map map = new HashMap();
		Map retMap = new HashMap();
		JSONArray ja = new JSONArray();
		LinkedHashMap condition = new LinkedHashMap();
		jxBus.jxZqQuery(ja, condition, retMap, tmd);
//		retMap.put("count",tmd.get("count"));
		retMap.put("success", Boolean.valueOf(true));
		retMap.put("rows", ja);
		return retMap;
	}
	
	//绩效计算控制器
	@ResponseBody
	@RequestMapping({ "/jxCalcControler.html" })
//		public Map calcControler(HttpServletRequest request,HttpServletResponse response,String txCode,UserBean userBean,String queryZq) {
	public Map jxJsControler(HttpServletRequest request,HttpServletResponse response,UserBean userBean,String queryZq) {
		Map map = new HashMap();
		Map retMap = new HashMap();
		JSONArray ja = new JSONArray();
		HttpSession session = request.getSession();
		String path = request.getSession().getServletContext().getRealPath("/");
//		this.tmd.put("userId", session.getAttribute("userId"));
//		this.tmd.put("txCode", userBean.getTx_code());
		tmd.put("response", response);
		tmd.put("request", request);
		tmd.put("path", path);
		ArrayList<String> zqArray = new ArrayList<String>();
		tmd.put("zqArray", zqArray);//检查四个考核周期是否一致
		LinkedHashMap condition = new LinkedHashMap();
		jxBus.calcBus(ja, condition, retMap);
		retMap.put("count",tmd.get("count"));
		retMap.put("success", Boolean.valueOf(true));
		retMap.put("rows", ja);
		return retMap;
	}
	
	//绩效查询控制器(查询用)
	@ResponseBody
	@RequestMapping({ "/jxQueryControler.html" })
//	public Map calcControler(HttpServletRequest request,HttpServletResponse response,String txCode,UserBean userBean,String queryZq) {
	public Map calcControler(HttpServletRequest request,HttpServletResponse response,UserBean userBean,String zqQuery) {
//		Map map = new HashMap();
		Map retMap = new HashMap();
		JSONArray ja = new JSONArray();
		HttpSession session = request.getSession();
		String path = request.getSession().getServletContext().getRealPath("/");
//		this.tmd.put("userId", session.getAttribute("userId"));
//		this.tmd.put("depId", userBean.getDeptId());
//		this.tmd.put("orgId", userBean.getOrgId());
//		this.tmd.put("txCode", userBean.getTx_code());
//		this.tmd.put("zqQuery", zqQuery==null?"":zqQuery);
		tmd.put("response", response);
		tmd.put("request", request);
		tmd.put("path", path);
		ArrayList<String> zqArray = new ArrayList<String>();
		tmd.put("zqArray", zqArray);//检查四个考核周期是否一致
		LinkedHashMap condition = new LinkedHashMap();
		/*tmd.put("start", Integer.parseInt(userBean.getStart())+1);
		try {
			tmd.put("end", Integer.parseInt(userBean.getStart()) + Integer.parseInt(userBean.getLimit())+1);
		} catch (Exception e) {}*/
		jxBus.calcBus(ja, condition, retMap);
		retMap.put("count",tmd.get("count"));
		retMap.put("success", Boolean.valueOf(true));
		retMap.put("rows", ja);
		return retMap;
	}
}