/**
 * MOVA机构得分导入2015-12-11
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
import com.icbc.nt.util.TransactionMapData;

public class Excelf10000055 extends BusParent implements FileUploadListener {
	private static String txCode = "10000055";
	/*@Autowired
	MediumBus mediumBus;*/
	
	/**
	 * 对应机构周期记录是否存在
	 * @param orgId
	 * @param zq
	 * @return
	 */
	public boolean isExist(String orgId,String zq){
		boolean isExist = false;
		iu.rmCondition(condition);
		iu.putCondition(condition, "orgId", orgId);
		iu.putCondition(condition, "zq", zq);
		JSONArray ja = new JSONArray();
		sqlStr = "select * from t_ntmisc_orgpt t where orgid=? and zq=?";
		this.queryManu(ja, condition, sqlStr, daoParent, 1);
		logger.info("机构得分表，对应机构周期记录是否存在，ja:"+ja.toJSONString());
		if(ja.size() > 0)
			isExist = true;
		return isExist;
	}
	
	public void doBat(Map retMap,String data){
		JSONArray jsonArray = JSONArray.parseArray(data);
		logger.info("jsonArray.toJSONString():"+jsonArray.toJSONString());
		LinkedList<LinkedHashMap<String,Object>> condList = new LinkedList<LinkedHashMap<String,Object>>();
		LinkedList<LinkedHashMap<String,Object>> condUpdList = new LinkedList<LinkedHashMap<String,Object>>();
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject json = jsonArray.getJSONObject(i);
			/**
			 * t_ntmisc_orgptb表中，三个模块可对表做插入、修改操作，分别为：机构mova得分导入模块、综合经营排位得分模块、重点产品挂钩模块，
			 * 这三个模块初始数据导入表t_ntmisc_orgptb中的顺序不一致，因此，对表t_ntmisc_orgptb的操作可能存在两种结果：1、新增（无对应机构、周期的记录）、2、修改（存在对饮周期机构的记录）
			 */
			LinkedHashMap<String, Object> condMap = new LinkedHashMap<String, Object>();//新插入记录
			LinkedHashMap<String, Object> condUpdMap = new LinkedHashMap<String, Object>();//更新记录
			String orgId = json.getString("orgId");
			String ptMova = json.getString("ptMova");
			String zq = json.getString("zq");
			if(this.isExist(orgId, zq)){
				condUpdMap.put("ptMova", ptMova);
				condUpdMap.put("orgId", orgId);
				condUpdMap.put("zq", zq);
				condUpdList.add(condUpdMap);
			}else{
				condMap.put("orgId", json.getString("orgId"));
				condMap.put("zq", json.getString("zq"));
				condMap.put("ptMova", json.getString("ptMova"));
				condMap.put("operTime", json.getString("operTime"));
				condList.add(condMap);
			}
		}
		//MOVA机构得分新增
		sqlStr = "insert into t_ntmisc_orgpt(orgid,zq,pt_mova,oper_time) values(?,?,?,?)";
		logger.info("执行sql,MOVA机构得分新增，sqlStr:"+sqlStr+"*****condList:"+condList.toString());
		this.updateBat(condList, daoParent, retMap, sqlStr, 1);
		//MOVA机构得分修改
		sqlStr = "update t_ntmisc_orgpt t set t.pt_mova=? where t.orgid=? and t.zq=?";
		logger.info("执行sql,MOVA机构得分修改，sqlStr:"+sqlStr+"*****condUpdList:"+condUpdList.toString());
		this.updateBat(condUpdList, daoParent, retMap, sqlStr, 1);
		
	}
	
	public void actionToFileUpload(FileUploadEvent e) {
		HashMap retMap = new HashMap();
		tmd = e.getTmd();
		if(txCode.equals(tmd.get("txCode"))){
			ArrayList<String> typeList = new ArrayList<String>();
			tmd.put("title", "批量导入MOVA机构得分");
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
				ArrayList<String> zqList = new ArrayList<String>();//周期列表
				for (int i = 3; i < rowLenth; i++) {
					JSONObject rowJson = new JSONObject();
//					LinkedHashMap<String, Object> rowMap = new LinkedHashMap<String, Object>();
					String orgId = sheet.getCell(0, i).getContents();//机构编号
					String ptMova = sheet.getCell(2, i).getContents();//机构mova得分
					String zq = sheet.getCell(3, i).getContents();//考核周期
					
//					 空行判断
//					logger.info("表格空行判断："+orgId+"|"+zbInit+"|"+movaBl+"|"+xsGy+"|"+xsKhjl+"|"+zq+"|");
//					logger.info("".equals(orgId.trim()) && "".equals(zbInit.trim())&& "".equals(movaBl.trim())
//							&& "".equals(xsGy.trim())&& "".equals(xsKhjl.trim())&& "".equals(zq.trim()));
					if("".equals(orgId.trim()) && "".equals(ptMova.trim())&& "".equals(zq.trim())){
						continue;
					}
					String operTime = iu.getTime();
					zqList.add(zq);
					if("".equals(orgId.trim()) || "".equals(ptMova.trim()) || "".equals(zq.trim())){
						tmd.put("errorCode", "11");
						tmd.put("errorMsg", "表格存在空数据,导入失败!");
						tmd.put("finish", "1");
						logger.info("表格存在空数据,导入失败！");
						return;
					}
					if(!iu.isNumber(ptMova)){
						tmd.put("errorCode", "11");
						tmd.put("errorMsg", "机构mova得分必须为数字,导入失败!");
						tmd.put("finish", "1");
						logger.info("机构mova得分必须为数字,导入失败！");
						return;
					}
					rowJson.put("orgId", orgId);
					rowJson.put("ptMova", ptMova);
					rowJson.put("zq", zq);
					rowJson.put("operTime", operTime);
					tableJa.add(rowJson);
				}
				
				for (int i = 0; i < zqList.size(); i++) {
					String zqTemp = zqList.get(i);
					if(i <= zqList.size()-2){
						String zqNext = zqList.get(i+1);
						if(!zqTemp.equals(zqNext)){
							excFlag = true;
							tmd.put("errorCode", "11");
							tmd.put("finish", "1");
							tmd.put("errorMsg", "存在不一致考核周期,导入失败！");
							logger.info("存在不一致考核周期,导入失败！");
						}
					}
				}
				
				logger.info("批量导入MOVA机构得分excel数据 tableJa:"+tableJa);
				if(!excFlag){
					this.doBat(retMap, tableJa+"");
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
