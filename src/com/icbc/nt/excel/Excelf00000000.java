/**
 * 批量导入机构、部门、用户2016-01-27
 */
package com.icbc.nt.excel;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.bus.FileUploadEvent;
import com.icbc.nt.bus.MediumBus;
import com.icbc.nt.bus.dispatcher.BusDispatcherImpl;
import com.icbc.nt.util.TransactionMapData;

public class Excelf00000000 extends BusParent implements FileUploadListener {
	private static String txCode = "00000000";
	@Autowired
	private BusDispatcherImpl busDispatcherImpl;
	
	public void actionToFileUpload(FileUploadEvent e) {
		HashMap retMap = new HashMap();
		tmd = e.getTmd();
		if(txCode.equals(tmd.get("txCode"))){
			ArrayList<String> typeList = new ArrayList<String>();
			tmd.put("title", "批量导入机构部门用户数据");
			tmd.put("errorMsg", "上传成功,正在解析文件");
			//初始化机构部门
			busDispatcherImpl.f10000001(tmd);
			
			//初始化用户
//			busDispatcherImpl.f10000002(tmd);
		}
	}
}
