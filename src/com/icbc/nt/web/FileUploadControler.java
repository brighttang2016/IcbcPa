package com.icbc.nt.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.nt.bean.UserBean;
import com.icbc.nt.bus.FileUploadBus;
import com.icbc.nt.test.DownLoad;
import com.icbc.nt.util.IcbcUtil;
import com.icbc.nt.util.TransactionMapData;

@Controller
public class FileUploadControler extends ControlerParent{
	Logger logger = IcbcUtil.getLogger();
	@Autowired
	private FileUploadBus fileUploadBus;
	@Autowired
	TransactionMapData tmd;
//	TransactionMapData tmd = new TransactionMapData();
	public void init(){
		logger.info("@PostConstruct  init");
//		tmd = new TransactionMapData();
	}
	
	@ResponseBody
	@RequestMapping(value="/tangUpload.html",produces="text/html")//,method=RequestMethod.POST,produces="text/html"
	public Map fileUpload(HttpServletResponse response,HttpServletRequest request){
		logger.info("tangUpload");
		System.out.println("tangUpload");
		HttpSession session = request.getSession();
		Map<String, Object> retMap = new HashMap<String, Object>();
		String path = request.getSession().getServletContext().getRealPath("/");//web上下文绝对根路径
		tmd.put("userId", session.getAttribute("userId"));
		tmd.put("txCode", request.getParameter("tx_code"));
		tmd.put("path", path);
//		fileUploadBus.fileUpload(session, file, retMap,tmd);
		return retMap;
	}
	
	@ResponseBody
	@RequestMapping(value="/fileUpload.html",produces="text/html")//,method=RequestMethod.POST,produces="text/html"
	public Map fileUpload(HttpServletResponse response,HttpServletRequest request,@RequestParam("file") MultipartFile file){
		logger.info("fileUpload");
		System.out.println("fileUpload");
		HttpSession session = request.getSession();
		Map<String, Object> retMap = new HashMap<String, Object>();
		String path = request.getSession().getServletContext().getRealPath("/");//web上下文绝对根路径
//		tmd.put("userId", session.getAttribute("userId"));
//		tmd.put("txCode", request.getParameter("tx_code"));
		tmd.put("path", path);
		
//		fileUploadBus.fileUpload(session, file, retMap,tmd.clone());//后续处理推送消息处存在新建线程，故此处需传递tmd的一份浅拷贝数据，直接传递tmd对象会抛出异常2016-02-25
		fileUploadBus.fileUpload(session, file, retMap);
		return retMap;
	}
	@ResponseBody
	@RequestMapping(value="/getPercent.html",produces="text/html;charset=UTF-8")
	public Map getPercent(HttpServletRequest request){
//		logger.info("getPercent");
		Map<String, Object> retMap = new HashMap<String, Object>();
		HttpSession session = request.getSession();
		fileUploadBus.getPercent(session, retMap);
		return retMap;
	}
	
	@RequestMapping(value="/downLoad2.html")
	public void downLoad2(HttpServletRequest request,HttpServletResponse response) throws IOException{
		String txCode = request.getParameter("tx_code");
		String path = request.getSession().getServletContext().getRealPath("/");//web上下文绝对根路径
		System.out.println(request.getParameter("name"));
	}
	
	@RequestMapping(value="/downLoad.html")
	public void downLoad(HttpServletRequest request,HttpServletResponse response,String data) throws IOException{
		String txCode = request.getParameter("tx_code");
		String path = request.getSession().getServletContext().getRealPath("/");//web上下文绝对根路径
		try {
			JSONObject dataJson = JSONObject.parseObject(data);
			tmd.put("depId", dataJson.getString("orgId"));
			tmd.put("orgId", dataJson.getString("deptId"));
			tmd.put("zqQuery", dataJson.getString("zqQuery"));
		} catch (Exception e) {
		}
		
		tmd.put("txCode", txCode);
		tmd.put("path", path);
		tmd.put("response", response);
		HttpSession session = request.getSession();
		tmd.put("userId", session.getAttribute("userId"));
		fileUploadBus.fileDownLoad(tmd);
	}
	
	
}
