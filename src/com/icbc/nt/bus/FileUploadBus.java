package com.icbc.nt.bus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.message.push.MessagePushClient;
import com.icbc.message.push.MessagePushHelper;
import com.icbc.message.push.PushThread;
import com.icbc.nt.bus.dispatcher.BusDispatcherImpl;
import com.icbc.nt.excel.Excelf30062;
import com.icbc.nt.excel.FileUploadListener;
import com.icbc.nt.util.FileTranImpl;
import com.icbc.nt.util.TransactionMapData;

@Service
@Transactional
public class FileUploadBus extends BusParent{
	@Value("${uploadLen}")
	int uploadLen;
	@Value("${uploadPath}")
	String uploadPath;
	@Value("${downloadPath}")
	String downloadPath;
	@Value("${jxExcelPath}")
	private String jxExcelPath;
	@Autowired
	private FileTranImpl fileTranImpl;
	@Autowired
	MessagePushClient mpc;
	@Autowired
	private BusDispatcherImpl busDispatcherImpl;
	@Autowired
	private MediumBus mediumBus;
	private HashSet<FileUploadListener> listenerSet;//监听者list
	public HashSet<FileUploadListener> getListenerSet() {
		return this.listenerSet;
	}
	public void setListenerSet(HashSet<FileUploadListener> listenerSet) {
		this.listenerSet = listenerSet;
	}
	/*public void startPush(String receiver,TransactionMapData tmd){
		MessagePushHelper mph = new MessagePushHelper();
		new PushThread(receiver,mph,tmd).start();
	}*/
	/**
	 * 获取写入文件百分比(滚动条显示数据获取方式：拉模式，使用)
	 * @param session
	 * @param retMap
	 */
	public void getPercent(HttpSession session,Map<String, Object> retMap){
		String errorCode = "0";
		String percent = "0";
		String progress = "0";
		String errorMsg = "";
		String finish = "0";
		errorCode = tmd.get("errorCode").toString();
		percent = tmd.get("percent").toString();
		progress = tmd.get("progress").toString();
		errorMsg = tmd.get("errorMsg").toString();
		finish = tmd.get("finish").toString();
		JSONArray ja = new JSONArray();
		retMap.put("success", true);
		retMap.put("rows", ja);
		retMap.put("errorCode",errorCode);
		retMap.put("errorMsg",errorMsg);
		retMap.put("percent", percent);
		retMap.put("progress", progress);
		retMap.put("finish", finish);
		//清空session数据
		if("1".equals(finish)){//交易完成，清空数据
			tmd.cleanTmd();
		}
	}
	
	/**
	 * 文件上传
	 * @param session
	 * @param file 上传文件流
	 * @param retMap
	 */
//	public void fileUpload(HttpSession session,MultipartFile file,Map<String, Object> retMap,TransactionMapData tmd){
	public void fileUpload(HttpSession session,MultipartFile file,Map<String, Object> retMap){
		String currThread = Thread.currentThread().getName();
		logger.info("*****fileUpload******currThread:"+currThread);
		tmd.tmdLogger();
//		String userId = tmd.get("userId").toString();
//		String orgId = mediumBus.getUserOrg(userId);
//		String brOrgId = busDispatcherImpl.brOrgQuery(orgId);//分支行号
//		String zqCurr = busDispatc
//		tmd.put("brOrgId", brOrgId);
//		tmd.put("zqCurr", zqCurr);
//		tmd.put("orgIdCurr", orgId);//当前登录用户所在机构
		
		fileTranImpl.fileUpload(file, tmd);
		
		//文件写入完成，触发文件上传解析事件。
		for(FileUploadListener ful:listenerSet){
			ful.actionToFileUpload(new FileUploadEvent(this,tmd));
		}
		logger.info("文件上传处理完成，当前线程："+currThread);
		tmd.tmdLogger();
		if(!"1".equals(tmd.get("finish"))){
			tmd.tmdLogger();
			tmd.put("finish", "1");
			tmd.put("errorMsg","文件处理失败!");
		}
		retMap.put("success", true);
		retMap.put("errorCode",tmd.get("errorCode"));
		retMap.put("errorMsg",tmd.get("errorMsg"));
		retMap.put("file", tmd.get("fileName"));
		retMap.put("finish",tmd.get("finish"));
		try {
			Thread.currentThread().sleep(1000);//一秒延时，确保mph推送进程执行完成，处理进度条刷新完成
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mpc.pushMessage(tmd.get("userIdLogin").toString(), JSONObject.toJSONString(retMap));//调用前端fileupload.js中的pageRefresh
		
		
	}
	/**
	 * 文件下载
	 * @param tmd
	 */
	public void fileDownLoad(TransactionMapData tmd){
		String userId = tmd.get("userId").toString();
		String orgId = mediumBus.getUserOrg(userId);
		String txCode = tmd.get("txCode").toString();
		String path = tmd.get("path").toString();
		String zqCurr = busDispatcherImpl.zqCurr();//当前考核周期
		tmd.put("zqCurr", zqCurr);
		tmd.put("orgIdCurr", orgId);//当前登录用户所在机构
		String brOrgId = busDispatcherImpl.brOrgQuery(orgId);//当前用户所在分支行号
		String orgIn = mediumBus.getOrgIn(brOrgId);//分支行下属所有网点号
		tmd.put("orgIn", orgIn);
		tmd.put("zqCurr", zqCurr);
		tmd.put("brOrgId", brOrgId);
		tmd.put("orgIdCurr", orgId);
		
		HttpServletResponse response = (HttpServletResponse) tmd.get("response");
		String fileName = "";//待下载文件名
		String filePath = path+downloadPath;//待下载文件路径
		JSONArray ja = new JSONArray();
		Map retMap = new HashMap();
//		String orgIn = "";
		switch(Integer.parseInt(txCode)){
		case 10015:
			fileName = "人员信息批量导入.xls";
			break;
		case 30062:
			fileName = "批量导入总行基础学分.xls";
			break;
		case 30064:
			fileName = "批量导入市行基础学分.xls";
			break;
		case 30066:
			fileName = "批量导入附加学分.xls";
			break;
		case 30068:
			fileName = "批量导入手工调整学分.xls";
			break;
		case 30084:
			fileName = "批量导入不参与考核人员.xls";
			break;
		case 30054:
			fileName = "批量导入积分奖励信息.xls";
			break;
		case 30094:
			fileName = "批量导入员工资格认证信息.xls";
			break;
		case 30102:
			fileName = "业务量基础数据-支行名称-网点名称.xls";
			break;
		case 30112:
			fileName = "标准产品考核基础数据-支行名称-网点名称.xls";
			break;
		case 30122:
			fileName = "特色业务考核基础数据-支行名称-网点名称.xls";
			break;
		case 30132:
			fileName = "定性考核基础数据-支行名称-网点名称.xls";
			break;
		case 1://绩效计算
			filePath = path+jxExcelPath;//待下载文件路径
//			String zq = tmd.get("zq").toString();
			fileName = "当期绩效-"+tmd.get("userId")+zqCurr+".xls";
			break;
		case 10000052://机构分配数据导入模版下载
			fileName = "机构分配数据导入模版.xls";
			break;
		case 10000055:
			fileName = "MOVA机构得分导入模版.xls";
			break;
		case 10000068:
			fileName = "人员总包分配比例数据模版-支行名称-网点名称.xls";
			break;
		case 10000072://当期绩效导出
			filePath = path+jxExcelPath;//待下载文件路径
			fileName = "当期绩效-"+tmd.get("userId")+zqCurr+".xls";
			//1、查询
//			String brOrgId = busDispatcherImpl.brOrgQuery(orgId);//当前用户所在分支行号
			orgIn = mediumBus.getOrgIn(brOrgId);//分支行下属所有网点号
//			tmd.put("zqQuery", tmd.get("zqCurr"));
//			tmd.put("orgIn", orgIn);
			busDispatcherImpl.f10000071(ja, condition, retMap, tmd);
			tmd.put("ja", ja);
//			2、生成文件
			busDispatcherImpl.f10000072(ja, condition, retMap, tmd);
			break;
		case 10000073:
			fileName = "权重区间模版-支行名称-网点名称.xls";
			break;
		case 10000081://历史绩效导出
			filePath = path+jxExcelPath;//待下载文件路径
			fileName = "历史绩效-"+tmd.get("userId")+".xls";
//			1、查询
			orgIn = mediumBus.getOrgIn(orgId);//当前用户所在机构下属所有网点号
			tmd.put("orgIn", orgIn);
			busDispatcherImpl.f10000080(ja, condition, retMap, tmd);
			logger.info("历史绩效导出,查询结果，ja:"+ja.toJSONString());
			tmd.put("ja", ja);
//			2、生成文件
			busDispatcherImpl.f10000081(ja, condition, retMap, tmd);
			break;
		case 20000001:
			fileName = "个人业绩数据导入.xls";
			break;
		case 20000003:
			fileName = "总包导入(分支行考核部分).xls";
			break;
		case 20000005:
			fileName = "总包导入(网点考核部分).xls";
			break;
		case 20000012://历史绩效导出
			fileName = "历史绩效-"+tmd.get("userId")+zqCurr+".xls";
			filePath =  tmd.get("path")+jxExcelPath;
			//绩效查询
//			orgIn = mediumBus.getOrgIn(orgId);//当前机构及下属所有网点号（当前机构查询当前及以下所有机构）
			orgIn = tmd.get("orgIdIn").toString();
			tmd.put("orgIn", orgIn);
			JSONArray jxHisJa = new JSONArray();
			logger.debug("历史绩效导出");
			tmd.tmdLogger();
			busDispatcherImpl.f20000008(jxHisJa, condition, retMap, tmd);//查询
			tmd.put("ja", jxHisJa);//数据源入变量池
			//生成绩效文件
			tmd.put("fileName", fileName);
			tmd.put("filePath", filePath);
			busDispatcherImpl.f20000012(tmd);//生成文件
			break;
		case 20000013:
			fileName = "网点手动分配绩效导入模版.xls";
			break;
		}
		logger.debug("文件下载，fileName："+fileName+"----filePath:"+filePath);
		fileTranImpl.fileDownLoad(response, fileName, filePath);
	}
}
