/**
 * 手工调整批量
 */
package com.icbc.nt.excel;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;

import com.icbc.nt.bus.BusParent;
import com.icbc.nt.bus.FileUploadEvent;

public class Excelf30068 extends BusParent implements FileUploadListener {
	private static String txCode = "30068";
	@Autowired
	ExcelBus excelBusImpl;
	
	public void actionToFileUpload(FileUploadEvent e) {
		// TODO Auto-generated method stub
		tmd = e.getTmd();
//		mpc.pushMessage(tmd.get("userId").toString(), "准备写入excel");
		if(txCode.equals(tmd.get("txCode"))){
			ArrayList<String> typeList = new ArrayList<String>();
//			手工调整：400  
			typeList.add("400");
			tmd.put("typeList", typeList);
			tmd.put("title", "手工调整学分");
			excelBusImpl.excelParseToDb(tmd);
		}
	}
}
