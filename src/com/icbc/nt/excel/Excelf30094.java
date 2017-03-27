/**
 * 批量导入资格认证信息2015-10-29
 */
package com.icbc.nt.excel;

import java.io.File;
import java.io.FileInputStream;
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

public class Excelf30094 extends BusParent implements FileUploadListener {
	private static String txCode = "30094";
	@Autowired
	MediumBus mediumBus;
	
	public void actionToFileUpload(FileUploadEvent e) {
		logger.info("Excelf30094 actionToFileUpload");
		// TODO Auto-generated method stub
		HashMap retMap = new HashMap();
		tmd = e.getTmd();
//		mpc.pushMessage(tmd.get("userId").toString(), "准备写入excel");
		if(txCode.equals(tmd.get("txCode"))){
			ArrayList<String> typeList = new ArrayList<String>();
			tmd.put("title", "批量导入员工资格认证信息");
			tmd.put("errorMsg", "上传成功,正在解析文件");
			//解析excel
			String title = tmd.get("title").toString();
			try {
				FileInputStream fis = new FileInputStream(new File(tmd.get("path")+"\\"+tmd.get("fileName")));
				Workbook workBook = Workbook.getWorkbook(fis);
				Sheet  sheet = workBook.getSheet(0);
				tmd.put("errorMsg", "解析成功,准备批量导入");
				int rowLenth = sheet.getRows();
				boolean excFlag = false;//异常标识，false:无异常 true:存在异常
				LinkedList<LinkedHashMap<String, Object>> condList = new LinkedList<LinkedHashMap<String,Object>>();
				JSONArray tableJa = new JSONArray();//表格数据
				for (int i = 3; i < rowLenth; i++) {
					JSONObject rowJson = new JSONObject();
//					LinkedHashMap<String, Object> rowMap = new LinkedHashMap<String, Object>();
					String userId = sheet.getCell(0, i).getContents();
					String ctId = sheet.getCell(2, i).getContents();
					if("".equals(userId.trim()) || "".equals(ctId.trim())){
						tmd.put("errorCode", "11");
						tmd.put("errorMsg", "存在空数据,导入失败!");
						tmd.put("finish", "1");
						logger.info("存在空数据,导入失败！");
						return;
					}
					rowJson.put("userId", userId);
					rowJson.put("ctId", ctId);
					rowJson.put("operTime", iu.getDate());
					HashMap<String, Object> condition = new HashMap<String, Object>();
					iu.rmCondition(condition);
					condition.put("userId", userId);
					sqlStr = "select * from t_ntmisc_user t where t.userid=?";
					JSONArray ja = new JSONArray();
					this.queryManu(ja, condition, sqlStr, daoParent, 1);
					if(ja.size() < 1){
						excFlag = true;
						tmd.put("errorCode", "11");
						tmd.put("finish", "1");
						tmd.put("errorMsg", "存在错误用户编号,导入失败！");
						logger.info("存在错误用户编号,导入失败！");
						return;
					}
					logger.info("rowJson:"+rowJson.toJSONString()+"rowLenth:"+rowLenth);
					tableJa.add(rowJson);
				}
				logger.info("批量导入资格认证信息excel数据 tableJa:"+tableJa);
				if(!excFlag){
					mediumBus.uctBat(retMap, tableJa+"");
					if(!"0".equals(retMap.get("recUpdCount"))){
						tmd.put("errorCode", "10");
						tmd.put("errorMsg", "批量导入成功!");
						logger.info("批量导入成功！");
					}else{
						tmd.put("errorCode", "11");
						tmd.put("errorMsg", "导入失败,数据录入失败!");
						logger.info("导入失败,数据录入失败！");
					}
				}
				tmd.put("finish", "1");
			} catch (Exception e2) {
				iu.error(logger, e2);
				tmd.put("finish", "1");
				tmd.put("errorCode", "11");
				tmd.put("errorMsg", "系统异常,导入失败！");
				logger.info("系统异常,导入失败！");
			}
		}
	}
}
