package com.icbc.nt.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bus.FileUploadBus;
import com.icbc.nt.util.IcbcUtil;
import com.icbc.nt.util.TransactionMapData;

@Controller
@Scope("session")
public class FileUploadTest extends ControlerParent{
	Logger logger = IcbcUtil.getLogger();
	@Autowired
	private FileUploadBus fileUploadBus;
	/*@Autowired
	TransactionMapData tmd;*/
	TransactionMapData tmd = new TransactionMapData();
	@PostConstruct
	public void init(){
		logger.info("@PostConstruct  init");
//		tmd = new TransactionMapData();
	}
	
	
	@ResponseBody
	@RequestMapping(value="/fileUploadTest.html",produces="text/html")//,method=RequestMethod.POST,produces="text/html"
	public Map fileUpload(HttpServletResponse response,HttpServletRequest request,@RequestParam("file") MultipartFile file){
		logger.info("fileUpload");
		System.out.println("fileUpload");
		HttpSession session = request.getSession();
		Map<String, Object> retMap = new HashMap<String, Object>();
		tmd.put("userId", session.getAttribute("userId"));
//		fileUploadBus.fileUpload(session, file, retMap,tmd);
		return retMap;
	}
	
	@ResponseBody
	@RequestMapping(value="/getPercentTest.html",produces="text/html;charset=UTF-8")
	public Map getPercent(HttpServletRequest request){
//		logger.info("getPercent");
		System.out.println("getPercent");
		Map<String, Object> retMap = new HashMap<String, Object>();
		HttpSession session = request.getSession();
		fileUploadBus.getPercent(session, retMap);
		return retMap;
	}
}
