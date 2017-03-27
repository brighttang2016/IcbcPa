/**
 * 人员总包分配比例导入2015-12-13
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
import com.icbc.nt.util.TransactionMapData;

public class Excelf10000068 extends BusParent implements FileUploadListener {
	private static String txCode = "10000068";
	/**
	 * 对应机构周期记录是否存在
	 * @param orgId
	 * @param zq
	 * @return
	 */
	public boolean isExist(String sqlStr,String orgId,String zq,String jbJxPid,String khfw){
		boolean isExist = false;
		iu.rmCondition(condition);
		iu.putCondition(condition, "orgId", orgId);
		iu.putCondition(condition, "zq", zq);
		iu.putCondition(condition, "jbJxPid", jbJxPid);
		iu.putCondition(condition, "khfw", khfw);
		JSONArray ja = new JSONArray();
//		sqlStr = "select * from t_ntmisc_orgzb t where orgid=? and zq=?";
		this.queryManu(ja, condition, sqlStr, daoParent, 1);
		logger.info("人员分配比例表，对应机构、周期、绩效考核岗位父类别、考核范围的记录是否存在，ja:"+ja.toJSONString());
		if(ja.size() > 0)
			isExist = true;
		return isExist;
	}
	
	/**
	 * 批量更新人员总包比例表
	 * @param retMap
	 * @param data
	 */
	public void doBat(Map retMap,String data){
		JSONArray jsonArray = JSONArray.parseArray(data);
		logger.info("jsonArray.toJSONString():"+jsonArray.toJSONString());
		LinkedList<LinkedHashMap<String,Object>> condList = new LinkedList<LinkedHashMap<String,Object>>();
		LinkedList<LinkedHashMap<String,Object>> condUpdList = new LinkedList<LinkedHashMap<String,Object>>();
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject json = jsonArray.getJSONObject(i);
			LinkedHashMap<String, Object> condMap = new LinkedHashMap<String, Object>();
			LinkedHashMap<String, Object> condUpdMap = new LinkedHashMap<String, Object>();
			String orgId = json.getString("orgId");
			String zq = json.getString("zq");
			String jbJxPid = json.getString("jbJxPid");
			String khfw = json.getString("khfw");
			String blYwl = json.getString("blYwl");
			String blBzcp = json.getString("blBzcp");
			String blZhts= json.getString("blZhts");
			String blDx= json.getString("blDx");
			
			String operTime = iu.getTime();
			sqlStr = "select * from t_ntmisc_orgzbbl t where orgid=? and zq=? and jbjx_pid=? and khfw=?";
			if(this.isExist(sqlStr,orgId, zq,jbJxPid,khfw)){
				condUpdMap.put("blYwl", blYwl);
				condUpdMap.put("blBzcp", blBzcp);
				condUpdMap.put("blZhts", blZhts);
				condUpdMap.put("blDx", blDx);
				condUpdMap.put("orgId", orgId);
				condUpdMap.put("zq", zq);
				condUpdMap.put("jbJxPid", jbJxPid);
				condUpdMap.put("khfw", khfw);
				condUpdList.add(condUpdMap);
			}else{
				condMap.put("orgId", orgId);
				condMap.put("zq", zq);
				condMap.put("jbJxPid", jbJxPid);
				condMap.put("khfw", khfw);
				condMap.put("blYwl", blYwl);
				condMap.put("blBzcp", blBzcp);
				condMap.put("blZhts", blZhts);
				condMap.put("blDx", blDx);
				condMap.put("operTime", operTime);
				condList.add(condMap);
			}
		}
		//插入人员分配比例表当期数据
		sqlStr = "insert into t_ntmisc_orgzbbl(orgid,zq,jbjx_pid,khfw,bl_ywl,bl_bzcp,bl_zhts,bl_dx,oper_time)"
				+ " values(?,?,?,?,?,?,?,?,?)";
		logger.info("执行sql,人员分配比例表数据批量插入，sqlStr:"+sqlStr+"*****condList:"+condList.toString());
		this.updateBat(condList, daoParent, retMap, sqlStr, 1);
//		更新人员分配比例表当期数据
		sqlStr = "update t_ntmisc_orgzbbl set bl_ywl=?,bl_bzcp=?,bl_zhts=?,bl_dx=?"
				+ " where orgid=? and zq=? and jbjx_pid=? and khfw=?";
		logger.info("执行sql,人员分配比例表数据批量更新，sqlStr:"+sqlStr+"*****condUpdList:"+condUpdList.toString());
		this.updateBat(condUpdList, daoParent, retMap, sqlStr, 1);
	}
	
	/**
	 * 解析excel数据
	 * @param tableJa
	 * @param sheet
	 * @param zqList
	 */
	public void tableJaAdd(JSONArray tableJa,Sheet  sheet,ArrayList<String> zqList){
		for (int i = 5; i < sheet.getRows(); i++) {
			JSONObject rowJson = new JSONObject();
			String orgId = sheet.getCell(0, i).getContents();//机构编号
			String zq = sheet.getCell(2, i).getContents();//考核周期
			//柜员考核比例
			//支行
			String gyZhYwl = sheet.getCell(3, i).getContents();//柜员-支行-业务量比例
			String gyZhBzcp = sheet.getCell(4, i).getContents();//柜员-支行-标准产品比例
			String gyZhZhts = sheet.getCell(5, i).getContents();//柜员-支行-支行特色比例
			String gyZhDx = sheet.getCell(6, i).getContents();//柜员-支行-定性比例
			//网点
			String gyWdYwl = sheet.getCell(7, i).getContents();//柜员-网点-业务量比例
			String gyWdBzcp = sheet.getCell(8, i).getContents();//柜员-网点-标准产品比例
			String gyWdZhts = sheet.getCell(9, i).getContents();//柜员-网点-支行特色比例
			String gyWdDx = sheet.getCell(10, i).getContents();//柜员-网点-定性比例
			//客户经理考核比例
			//支行
			String khjlZhBzcp = sheet.getCell(11, i).getContents();//客户经理-支行-标准产品比例
			String khjlZhZhts = sheet.getCell(12, i).getContents();//客户经理-支行-支行特色比例
			String khjlZhDx = sheet.getCell(13, i).getContents();//客户经理-支行-定性比例
			//网点
			String khjlWdBzcp = sheet.getCell(14, i).getContents();//客户经理-网点-标准产品比例
			String khjlWdZhts = sheet.getCell(15, i).getContents();//客户经理-网点-支行特色比例
			String khjlWdDx = sheet.getCell(16, i).getContents();//客户经理-网点-定性比例
			
//			 空行判断
			logger.info("表格空行判断："+orgId+"|"+zq+"|");
			logger.info("".equals(orgId.trim()) && "".equals(zq.trim()));
			if("".equals(orgId.trim()) && "".equals(zq.trim())){
				continue;
			}
//			String operTime = iu.getTime();
			zqList.add(zq);
			if("".equals(orgId.trim()) || "".equals(zq.trim())){
				tmd.put("errorCode", "11");
				tmd.put("errorMsg", "表格存在空数据,导入失败!");
				tmd.put("finish", "1");
				logger.info("表格存在空数据,导入失败！");
				return;
			}
			/*if(!iu.isNumber(zbInit)){
				tmd.put("errorCode", "11");
				tmd.put("errorMsg", "初次分配机构总包必须为数字,导入失败!");
				tmd.put("finish", "1");
				logger.info("初次分配机构总包为数字,导入失败！");
				return;
			}*/
			//柜员-支行考核比例
			rowJson.put("orgId", orgId);
			rowJson.put("zq", zq);
			rowJson.put("jbJxPid", "1");//绩效考核岗位父类别（1：柜员、2、客户经理）
			rowJson.put("khfw", "1");//考核范围（1、支行；2、网点）
			rowJson.put("blYwl", gyZhYwl);
			rowJson.put("blBzcp", gyZhBzcp);
			rowJson.put("blZhts", gyZhZhts);
			rowJson.put("blDx", gyZhDx);
			tableJa.add(rowJson);
			//柜员-网点考核比例
			rowJson = new JSONObject();
			rowJson.put("orgId", orgId);
			rowJson.put("zq", zq);
			rowJson.put("jbJxPid", "1");//绩效考核岗位父类别（1：柜员、2、客户经理）
			rowJson.put("khfw", "2");//考核范围（1、支行；2、网点）
			rowJson.put("blYwl", gyWdYwl);
			rowJson.put("blBzcp", gyWdBzcp);
			rowJson.put("blZhts", gyWdZhts);
			rowJson.put("blDx", gyWdDx);
			tableJa.add(rowJson);//
			//客户经理-支行考核比例
			rowJson = new JSONObject();
			rowJson.put("orgId", orgId);
			rowJson.put("zq", zq);
			rowJson.put("jbJxPid", "2");//绩效考核岗位父类别（1：柜员、2、客户经理）
			rowJson.put("khfw", "1");//考核范围（1、支行；2、网点）
			rowJson.put("blYwl", "0");//客户经理无业务量数据
			rowJson.put("blBzcp", khjlZhBzcp);
			rowJson.put("blZhts", khjlZhZhts);
			rowJson.put("blDx", khjlZhDx);
			tableJa.add(rowJson);
			//客户经理-网点考核比例
			rowJson = new JSONObject();
			rowJson.put("orgId", orgId);
			rowJson.put("zq", zq);
			rowJson.put("jbJxPid", "2");//绩效考核岗位父类别（1：柜员、2、客户经理）
			rowJson.put("khfw", "2");//考核范围（1、支行；2、网点）
			rowJson.put("blYwl", "0");//客户经理无业务量数据
			rowJson.put("blBzcp", khjlWdBzcp);
			rowJson.put("blZhts", khjlWdZhts);
			rowJson.put("blDx", khjlWdDx);
			tableJa.add(rowJson);
		}
	}
	
	public void actionToFileUpload(FileUploadEvent e) {
		HashMap retMap = new HashMap();
		tmd = e.getTmd();
		if(txCode.equals(tmd.get("txCode"))){
			ArrayList<String> typeList = new ArrayList<String>();
			tmd.put("title", "批量导入机构总包分配比例数据");
			tmd.put("errorMsg", "上传成功,正在解析文件");
			//解析excel
			String title = tmd.get("title").toString();
			try {
				FileInputStream fis = new FileInputStream(new File(tmd.get("path")+"\\"+tmd.get("fileName")));
				Workbook workBook = Workbook.getWorkbook(fis);
				Sheet  sheet = workBook.getSheet(0);
				tmd.put("errorMsg", "解析成功,准备批量导入");
				boolean excFlag = false;//异常标识，false:无异常 true:存在异常
				LinkedList<LinkedHashMap<String, Object>> condList = new LinkedList<LinkedHashMap<String,Object>>();
				JSONArray tableJa = new JSONArray();//表格数据
				ArrayList<String> zqList = new ArrayList<String>();//周期列表
				this.tableJaAdd(tableJa, sheet, zqList);
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
				logger.info("批量导入机构分配数据excel数据 tableJa:"+tableJa);
				if(!excFlag){
					this.doBat(retMap, tableJa+"");
//					this.doBatOrgZbbl(retMap, tableJa+"");
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
