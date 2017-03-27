/**
 * 员工奖励信息批量 2015-10-28
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

public class Excelf30054 extends BusParent implements FileUploadListener {
	private static String txCode = "30054";
	@Autowired
	MediumBus mediumBus;
	
	public void actionToFileUpload(FileUploadEvent e) {
		logger.info("Excelf30084 actionToFileUpload");
		// TODO Auto-generated method stub
		HashMap retMap = new HashMap();
		tmd = e.getTmd();
//		mpc.pushMessage(tmd.get("userId").toString(), "准备写入excel");
		if(txCode.equals(tmd.get("txCode"))){
			ArrayList<String> typeList = new ArrayList<String>();
//			手工调整：400  
//			typeList.add("400");
//			tmd.put("typeList", typeList);
			tmd.put("title", "批量导入员工奖励信息");
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
					String userName = sheet.getCell(1, i).getContents();
					String rpTime = sheet.getCell(2, i).getContents();
					String rpPoint = sheet.getCell(3, i).getContents();
					String rpMsg = sheet.getCell(4, i).getContents();
					if("".equals(userId) || "".equals(rpTime) || "".equals(rpPoint) || "".equals(rpMsg)){
						tmd.put("errorCode", "11");
						tmd.put("errorMsg", "存在空数据,导入失败!");
						tmd.put("finish", "1");
						logger.info("存在空数据,导入失败！");
						return;
					}
					rowJson.put("userId", userId);
					rowJson.put("userName", userName);
					rowJson.put("rpTime", rpTime);
					rowJson.put("rpPoint", rpPoint);
					rowJson.put("rpMsg", rpMsg);
					HashMap<String, Object> condition = new HashMap<String, Object>();
					iu.rmCondition(condition);
					condition.put("userId", userId);
					sqlStr = "select * from t_ntmisc_user t where t.userid=?";
					JSONArray ja = new JSONArray();
					this.queryManu(ja, condition, sqlStr, daoParent, 1);
					if(ja.size() < 1){
						excFlag = true;
						tmd.put("errorCode", "11");
						tmd.put("errorMsg", "存在错误用户编号,导入失败！");
						tmd.put("finish", "1");
						logger.info("存在错误用户编号,导入失败！");
						return;
					}
					/*Iterator it = rowJson.keySet().iterator();
					while(it.hasNext()){
						String key = it.next().toString();
						try {
							if(rowJson.getString(key) != null || "".equals(rowJson.getString(key).trim())){
								excFlag = true;
								tmd.put("errorCode", "11");
								tmd.put("errorMsg", "存在空数据,导入失败!");
								logger.info("存在空数据,导入失败！");
							}else if(!iu.isNumber(rowJson.getString("rpPoint")) || !iu.isNumber(rowJson.getString("rpTime"))){
								excFlag = true;
								tmd.put("errorCode", "11");
								tmd.put("errorMsg", "存在非数字奖励积分,导入失败!");
								logger.info("存在非数字奖励积分,导入失败!");
							}
						} catch (Exception e2) {
							iu.error(logger, e2);
							tmd.put("finish", "1");
							tmd.put("errorCode", "11");
							tmd.put("errorMsg", "数据异常,导入失败!");
							logger.info("数据异常,导入失败!");
						}
					}*/
//					condList.add(rowMap);
					tableJa.add(rowJson);
				}
				logger.info(""+tableJa);
				
				if(!excFlag){
//					sqlStr = "update t_ntmisc_user t set t.ass_flag = ? where t.userid = ?";
//					this.updateBat(condList, daoParent, retMap, sqlStr, 1);
//					logger.info("retMap.get(\"recUpdCount\"):"+retMap.get("recUpdCount"));
					mediumBus.rpBat(retMap, tableJa+"");
					if(!"0".equals(retMap.get("recUpdCount"))){
						tmd.put("errorCode", "10");
						tmd.put("errorMsg", "批量导入成功!");
						logger.info("批量导入成功！");
					}else{
						tmd.put("errorCode", "11");
						tmd.put("errorMsg", "导入失败,数据异常!");
						logger.info("导入失败,数据异常!");
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
