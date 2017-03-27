/**
 * 机构部门初始化 2016-01-26
 */
package com.icbc.nt.bus.receiver;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.message.push.MessagePushHelper;
import com.icbc.message.push.PushThread;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.util.TransactionMapData;

public class F10000001Rcv extends BusParent implements BusReceiver {

	@Override
	public void doWork(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub

	}
	
	public void dataInit(String path,TransactionMapData tmd){
		System.out.println("dataInit");
		LinkedList<LinkedHashMap<String, Object>> condList = new LinkedList<LinkedHashMap<String,Object>>();
		
		JSONArray orgJa = new JSONArray();//excel原始机构数据
		Map retMap = new HashMap();
		LinkedHashMap<String,Object> rowMap = null;
		Sheet sheet = null;
		int rowNum = 0;
		//清空数据
		
		iu.setProgress(tmd, "11", "清除原始用户角色数据", "0.2");
		
		logger.debug("清除原始用户角色数据:"+tmd.get("progress").toString());
		iu.rmCondition(condition);
		sqlStr = "delete t_ntmisc_userrole ";
		this.update(condition, daoParent, sqlStr, retMap);
		logger.info("清除原始用户角色数据");
		iu.infoDbOper("清除原始用户角色数据", sqlStr, condition);
		logger.debug("清除原始用户数据:"+tmd.get("progress").toString());
		
		iu.rmCondition(condition);
		sqlStr = "delete t_ntmisc_user ";
		this.update(condition, daoParent, sqlStr, retMap);
		iu.setProgress(tmd, "11", "清除原始用户数据", "0.3");
		logger.info("清除原始用户数据");
		iu.infoDbOper("清除原始用户数据", sqlStr, condition);
		
		sqlStr = "delete t_ntmisc_dept ";
		this.update(condition, daoParent, sqlStr, retMap);
		iu.setProgress(tmd, "11", "清除原始部门数据", "0.4");
		logger.info("清除原始部门数据");
		iu.infoDbOper("清除原始部门数据", sqlStr, condition);

		sqlStr = " delete t_ntmisc_org ";
		this.update(condition, daoParent, sqlStr, retMap);
		logger.info("清除原始机构数据");
		iu.infoDbOper("清除原始机构数据", sqlStr, condition);
		
		File file = new File(path);
		try {
			Workbook workBook = Workbook.getWorkbook(file);
			sheet = workBook.getSheet(1);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		rowNum = sheet.getRows();
		
		logger.debug("初始化机构开始");
		iu.setProgress(tmd, "11", "初始化机构数据中...", "0.7");
		
//		1、初始化机构，获取excel原始数据
		for (int i = 3; i < rowNum; i++) {
			JSONObject rowJson = new JSONObject();
			String type = sheet.getCell(3, i).getContents();
			rowJson.put("orgid", sheet.getCell(0, i).getContents().trim());
			rowJson.put("depid", sheet.getCell(0, i).getContents().trim());
			rowJson.put("orgname", sheet.getCell(2, i).getContents().trim());
			rowJson.put("depname", sheet.getCell(2, i).getContents().trim());
			rowJson.put("orgtype", sheet.getCell(3, i).getContents().trim());
			rowJson.put("porgname", sheet.getCell(4, i).getContents().trim());
			orgJa.add(rowJson);
		}
		
		//获取一级分行id
		String fhOrgId = "";
		for (int i = 0; i < orgJa.size(); i++){
			JSONObject jsonNow = orgJa.getJSONObject(i);
			if(jsonNow.getString("orgtype").equals("一级分行")){
				fhOrgId = jsonNow.getString("orgid");
			}
		}
		
		//设置所有网点父机构（初始挂靠点为excel表格网点所在二级分行）
		for (int j = 0; j < orgJa.size(); j++) {
			JSONObject jsonNow = orgJa.getJSONObject(j);
			if(jsonNow.getString("orgtype").equals("网点")){
				for (int i = 0; i < orgJa.size(); i++) {
					JSONObject jsonTemp = orgJa.getJSONObject(i);
					if(jsonNow.getString("porgname").equals(jsonTemp.getString("orgname"))){
						jsonNow.put("porgid", jsonTemp.getString("orgid"));
					}
				}
			}
		}
		
		//设置所属机构号
		for (int i = 0; i < orgJa.size(); i++) {
			JSONObject jsonNow = orgJa.getJSONObject(i);
			if(jsonNow.getString("orgtype").equals("一级分行")){//设置一级分行上级
				jsonNow.put("porgid", "");
			}else if(jsonNow.getString("orgname").equals(jsonNow.getString("porgname"))){//设置二级分行上级
				jsonNow.put("porgid", fhOrgId);
			}else if(jsonNow.getString("orgtype").equals("机构本部")){//设置机构本部上级
				String orgNameTemp = jsonNow.getString("orgname");
				String orgName = orgNameTemp.substring(0, orgNameTemp.length()-2);
				for (int j = 0; j < orgJa.size(); j++) {
					JSONObject jsonCycle = orgJa.getJSONObject(j);
					if(orgName.equals(jsonCycle.getString("orgname"))){
						jsonNow.put("porgid", jsonCycle.getString("orgid"));
					}
				}
			}else if(jsonNow.getString("orgtype").equals("县支行")){//设置县支行上级
				//设置县支行上级
				for (int j = 0; j < orgJa.size(); j++) {
					JSONObject jsonCycle = orgJa.getJSONObject(j);
					if(jsonNow.getString("porgname").equals(jsonCycle.getString("orgname"))){
						jsonNow.put("porgid", jsonCycle.getString("orgid"));
					}
				}
				//设置县支行下属所有网点上级
//				System.out.println("县支行");
				int endIndex = jsonNow.getString("orgname").length()-2;//县支行命名规则：XXXX支行,县支行下属网点命名规则：XXXXYYYY支行、XXXXYYYY分理处、XXXXYYYY营业室
				for (int j = 0; j < orgJa.size(); j++) {//设置县支行下属网点上级
					JSONObject jsonCycle = orgJa.getJSONObject(j);
					if(jsonCycle.getString("orgtype").equals("网点")){//重新将上级是县支行的网点挂靠到对应的县支行
						if(jsonNow.getString("orgname").substring(0,endIndex).equals(jsonCycle.getString("orgname").substring(0,endIndex))){
							jsonCycle.put("porgid", jsonNow.getString("orgid"));
						}
					}
				}
			}
			
			//配置带插入机构数据
			if(!jsonNow.getString("orgtype").equals("内设部室")){
				rowMap = new LinkedHashMap<String, Object>();
				rowMap.put("orgid", jsonNow.getString("orgid"));
				rowMap.put("orgname", jsonNow.getString("orgname"));
				rowMap.put("porgid", jsonNow.getString("porgid")==""?'0':jsonNow.getString("porgid"));
				condList.add(rowMap);
			}
		}
		
		logger.debug("最终导入机构"+condList.toString());
		sqlStr = "insert into t_ntmisc_org(orgid,orgname,porgid) values(?,?,?)";
		logger.info("新增机构数据");
		iu.infoDbOper("新增机构数据", sqlStr, condition);
		this.updateBat(condList, daoParent, retMap, sqlStr, 1);
		logger.debug("初始化机构结束");
		
		logger.debug("初始化部门开始");
//		2、初始化部门
		condList.clear();
		
		for (int i = 0; i < orgJa.size(); i++) {
			
			JSONObject jsonNow = orgJa.getJSONObject(i);
			String orgName = jsonNow.getString("orgname");
			
			if(jsonNow.getString("orgtype").equals("内设部室")){
				jsonNow.put("depid", jsonNow.getString("orgid"));
				jsonNow.put("depname", jsonNow.getString("orgname"));
				boolean pFlag = false;
				//循环经部分挂靠到对应的上级部门
				for (int j = 0; j < orgJa.size(); j++) {
					JSONObject jsonCycle = orgJa.getJSONObject(j);
					if(jsonCycle.getString("orgtype").equals("内设部室")){
//						System.out.println("1111");
						String orgNameCycle = jsonCycle.getString("orgname");
						int length = orgNameCycle.length();
						if(orgName.length() > length){
							if(orgName.substring(0, length).equals(orgNameCycle)){
								pFlag = true;//存在父部门
								jsonNow.put("parentdepid", jsonCycle.getString("depid"));
							}	
						}
					}
				}
				if(!pFlag){//不存在父部门，本部门挂到“机构本部”
					for (int j = 0; j < orgJa.size(); j++) {
						JSONObject jsonCycle = orgJa.getJSONObject(j);
						if(jsonCycle.getString("orgtype").equals("机构本部")){
							String orgTemp = jsonCycle.getString("orgname");
							int endIndex = orgTemp.length()-2;
							try {
								if(orgTemp.substring(0,endIndex).equals(jsonNow.getString("orgname").substring(0, endIndex))){//try 防止jsonNow.getString("orgname")不足endIndex抛出异常
									jsonNow.put("parentdepid", jsonCycle.getString("orgid"));
								}
							} catch (Exception e) {}
						}
					}
				}
			}else{
				jsonNow.put("parentdepid", "0001");
			}
		}
		
//		统一设置部门表中，内设部室所在机构
		for (int i = 0; i < orgJa.size(); i++) {
			JSONObject jsonNow = orgJa.getJSONObject(i);
			if(jsonNow.getString("orgtype").equals("内设部室")){//寻找jsonNow记录的上级部门
				String orgName = jsonNow.getString("orgname");
				int endIndex = orgName.length();
				boolean cDepFlag = false;
				for (int j = 0; j < orgJa.size(); j++) {
					JSONObject jsonCycle = orgJa.getJSONObject(j);
					if(jsonCycle.getString("orgtype").equals("机构本部")){
						String orgTemp = jsonCycle.getString("orgname");
						endIndex = orgTemp.length()-2;
						try {
							if(orgTemp.substring(0,endIndex).equals(jsonNow.getString("orgname").substring(0, endIndex))){//try 防止jsonNow.getString("orgname")不足endIndex抛出异常
								jsonNow.put("orgid", jsonCycle.getString("orgid"));
							}
						} catch (Exception e) {}
					}
				}
			}else{
				jsonNow.put("parentdepid", "0001");//机构本部等于网点，上级部门号为机构号
			}
			
			rowMap = new LinkedHashMap<String, Object>();
			rowMap.put("orgid", jsonNow.getString("orgid"));
			rowMap.put("parentdepid", jsonNow.getString("parentdepid"));
			rowMap.put("depid", jsonNow.getString("depid"));
			rowMap.put("depname", jsonNow.getString("depname"));
			if(rowMap.get("parentdepid") != null)
				condList.add(rowMap);
			else{
				logger.debug("上级机构部门为空，部门导入失败："+rowMap.toString());
				iu.setProgress(tmd, "11", "上级机构部门为空，此机构部门导入失败："+rowMap.toString(), "0.9");
				/**
				 * 说明：未成功导入的部门需要手动导入
				 */
			}	
		}
		iu.setProgress(tmd, "11", "正在导入部门数据...", "0.9");
		sqlStr = "insert into t_ntmisc_dept(orgid,parentdepid,depid,depname) values(?,?,?,?)";
		logger.info("新增部门数据");
		iu.infoDbOper("新增部门数据", sqlStr, condList);
		this.updateBat(condList, daoParent, retMap, sqlStr, 1);
		
		int totalNum = rowNum-4;
		int successNUm = condList.size();
		iu.setProgress(tmd, "11", "机构部门数据导入完成，共:"+totalNum+",成功:"+successNUm, "0.9");
		iu.setProgress(tmd, "11", "", "1");
		tmd.put("finish", "1");
		
		//插入admin用户
		sqlStr = "insert into t_ntmisc_user(userid,name,orgid,depid) values('admin','admin','0310000000','0310000000')";
		iu.rmCondition(condition);
		logger.info("插入admin用户");
		iu.infoDbOper("插入admin用户", sqlStr, condList);
		
		this.update(condition, daoParent, sqlStr, retMap);
		sqlStr  = "insert into t_ntmisc_userrole(user_id,role_id) values('admin','2')";
		logger.info("插入admin用户角色");
		iu.infoDbOper("插入admin用户角色", sqlStr, condList);
		this.update(condition, daoParent, sqlStr, retMap);
	}
	
	
	public void doWork(TransactionMapData tmd) {
//		iu.startPush(tmd);
		iu.setProgress(tmd, "11", "准备导入机构部门数据", "0.1");
		String path = tmd.get("path")+"\\"+tmd.get("fileName");
		logger.debug("xls文件绝对路径path:"+path);
		this.dataInit(path,tmd);
		return;
	}
}
