/**
 * 总行基础学分批量
 * @author brighttang 2015-10-14
 */
package com.icbc.nt.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import com.icbc.message.push.MessagePushClient;
import com.icbc.message.push.MessagePushHelper;
import com.icbc.message.push.PushThread;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.bus.FileUploadEvent;

public class Excelf30062 extends BusParent implements FileUploadListener {
//	@Autowired
//	MessagePushClient mpc;
	@Autowired
	ExcelBus excelBusImpl;
	private static String txCode = "30062";
	@Override
	public void actionToFileUpload(FileUploadEvent e) {
		// TODO Auto-generated method stub
		tmd = e.getTmd();
//		mpc.pushMessage(tmd.get("userId").toString(), "准备写入excel");
		if(txCode.equals(tmd.get("txCode"))){
			ArrayList<String> typeList = new ArrayList<String>();
			typeList.add("100");
			tmd.put("typeList", typeList);
			tmd.put("title", "总行基础学分");
			excelBusImpl.excelParseToDb(tmd);
		}
	}
}
