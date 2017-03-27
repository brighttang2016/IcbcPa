/**
 * 市行基础学分批量
 */
package com.icbc.nt.excel;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;

import com.icbc.nt.bus.BusParent;
import com.icbc.nt.bus.FileUploadEvent;

public class Excelf30064 extends BusParent implements FileUploadListener {
	private static String txCode = "30064";
	@Autowired
	ExcelBus excelBusImpl;
	
	public void actionToFileUpload(FileUploadEvent e) {
		// TODO Auto-generated method stub
		tmd = e.getTmd();
//		mpc.pushMessage(tmd.get("userId").toString(), "准备写入excel");
		if(txCode.equals(tmd.get("txCode"))){
			ArrayList<String> typeList = new ArrayList<String>();
			//参考参训：2001；资格认证：2002；组织管理：2003
			for (int i = 2001; i < 2004; i++) {
				typeList.add(i+"");
			}
			tmd.put("typeList", typeList);
			tmd.put("title", "市行基础学分");
			
			excelBusImpl.excelParseToDb(tmd);
		}
	}
}
