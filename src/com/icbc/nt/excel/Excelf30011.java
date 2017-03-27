/**
 * 工资薪点初始化
 * @author brighttang 2015-10-20
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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.message.push.MessagePushClient;
import com.icbc.message.push.MessagePushHelper;
import com.icbc.message.push.PushThread;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.bus.FileUploadEvent;

public class Excelf30011 extends BusParent implements FileUploadListener {
	@Autowired
	MessagePushClient mpc;
	
	private static String txCode = "30011";
	@Override
	public void actionToFileUpload(FileUploadEvent e) {
		// TODO Auto-generated method stub
		tmd = e.getTmd();
		ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
		JSONArray ja = new JSONArray();
		JSONObject jsonRet = new JSONObject();
		for (int i = 0; i < 10; i++) {
			JSONObject json = new JSONObject();
			HashMap<String,String> rowMap = new HashMap<String, String>();
			json.put("name", "Lisa");
			json.put("email", "lisa@simpsons.com");
			json.put("phone", "555-111-1224");
			ja.add(json);
		}
		jsonRet.put("items", ja);
		System.out.println("jsonRet.toJSONString():"+jsonRet.toJSONString());
		
		if(txCode.equals(tmd.get("txCode"))){
			mpc.pushMessage(tmd.get("userId").toString(), jsonRet.toJSONString());//消息推送
			tmd.put("errorMsg", "正在批量导入总行基础学分");
			System.out.println("Excelf30062 准备解析excel 当前交易:"+e.getTmd().get("txCode")+"|"+e.getTmd().get("path")+"|"+tmd.get("fileName"));
			logger.info("Excelf30062 准备解析excel 当前交易:"+e.getTmd().get("txCode")+"|"+e.getTmd().get("path")+"|"+tmd.get("fileName"));
			System.out.println(tmd.get("fileName"));
			
			try {
				/*
				FileInputStream fis = new FileInputStream(new File(tmd.get("path")+"\\"+tmd.get("fileName")));
				Workbook workBook = Workbook.getWorkbook(fis);
				Sheet  sheet = workBook.getSheet(0);
				int rowLenth = sheet.getRows();
				int colLenth = sheet.getColumns();
				System.out.println(rowLenth+"|"+colLenth);
				LinkedList<LinkedHashMap<String, Object>> condList = new LinkedList<LinkedHashMap<String,Object>>();
				for (int i = 3; i < rowLenth; i++) {
					LinkedHashMap<String, Object> rowMap = new LinkedHashMap<String, Object>();
					int j = 0;
					
					LinkedList<Integer> list = new LinkedList<Integer>() ;
					list.add(0);
					list.add(2);
					list.add(4);
					Cell cell = sheet.getCell(0, i);
					rowMap.put("column"+0, cell.getContents());
//					rowMap.put("column"+0, iu.getDate());
					cell = sheet.getCell(2, i);
					rowMap.put("column"+2, cell.getContents());
//					rowMap.put("column"+2, iu.getDate());
					cell = sheet.getCell(4, i);
					rowMap.put("column"+4, cell.getContents());
//					rowMap.put("column"+4, iu.getDate());
					
					logger.info("rowMap:"+rowMap);
					if("".equals(rowMap.get("column0").toString().trim()) || "".equals(rowMap.get("column2").toString().trim()) //1、3、5三列数据不能为空
							||"".equals(rowMap.get("column4").toString().trim())){
						tmd.put("finish", "1");
						tmd.put("errorCode", "11");
						tmd.put("errorMsg", "导入失败,上传数据错误!");
						logger.info("导入失败,上传数据错误!");
					}else if(iu.isNumber(rowMap.get("column4").toString().trim())){//第五列积分必须为数字
						tmd.put("finish", "1");
						tmd.put("errorCode", "11");
						tmd.put("errorMsg", "导入失败,积分必须为数字!");
						logger.info("导入失败,积分必须为数字!");
					}else{
						condList.add(rowMap);
						logger.info("导入行rowMap:"+rowMap);
					}
				}
				logger.info("condList.size():"+condList.size());
				Map retMap = new HashMap();
				sqlStr = "insert into t_ntmisc_credit(user_id,ctype_id,point) values(?,?,?)"; 
				this.updateBat(condList, daoParent, retMap, sqlStr, 1);
				tmd.put("finish", "1");
				tmd.put("errorCode", "10");
				tmd.put("errorMsg", "批量导入总行基础学分成功");
				*/
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			
		}
	}
}
