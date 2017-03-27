/**
 * 附加学分批量
 */
package com.icbc.nt.excel;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;

import com.icbc.nt.bus.BusParent;
import com.icbc.nt.bus.FileUploadEvent;

public class Excelf30066 extends BusParent implements FileUploadListener {
	private static String txCode = "30066";
	@Autowired
	ExcelBus excelBusImpl;
	
	public void actionToFileUpload(FileUploadEvent e) {
		// TODO Auto-generated method stub
		tmd = e.getTmd();
//		mpc.pushMessage(tmd.get("userId").toString(), "准备写入excel");
		if(txCode.equals(tmd.get("txCode"))){
			ArrayList<String> typeList = new ArrayList<String>();
//			附加学分1 附加学分2 附加学分3 附加学分4 附加学分5 附加学分6 附加学分7 附加学分8:3001 3002 3003 3004 3005 3006 3007 3008  
			for (int i = 3001; i < 3009; i++) {
				typeList.add(i+"");
			}
			tmd.put("typeList", typeList);
			tmd.put("title", "附加学分");
			
			excelBusImpl.excelParseToDb(tmd);
		}
	}
}
