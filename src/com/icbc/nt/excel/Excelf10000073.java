/**
 * 权重区间设置导入2015-12-21
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

public class Excelf10000073 extends BusParent implements FileUploadListener {
	private static String txCode = "10000073";
	/**
	 * 对应机构周期记录是否存在
	 * @param orgId
	 * @param zq
	 * @return
	 */
	public boolean isExist(String sqlStr,String orgId,String jbJxPid,String khfw){
		boolean isExist = false;
		iu.rmCondition(condition);
		iu.putCondition(condition, "orgId", orgId);
		iu.putCondition(condition, "jbJxPid", jbJxPid);
		iu.putCondition(condition, "khfw", khfw);
		JSONArray ja = new JSONArray();
//		sqlStr = "select * from t_ntmisc_orgzb t where orgid=? and zq=?";
		this.queryManu(ja, condition, sqlStr, daoParent, 1);
		logger.info("考核区间比例表，对应机构、绩效考核岗位父类别、考核范围的记录是否存在，ja:"+ja.toJSONString());
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
			String jbJxPid = json.getString("jbJxPid");
			String khfw = json.getString("khfw");
			String ywlXx = json.getString("ywlXx");
			String ywlSx = json.getString("ywlSx");
			String bzcpXx = json.getString("bzcpXx");
			String bzcpSx= json.getString("bzcpSx");
			String zhtsXx= json.getString("zhtsXx");
			String zhtsSx= json.getString("zhtsSx");
			String dxXx= json.getString("dxXx");
			String dxSx= json.getString("dxSx");
			
			
			String operTime = iu.getTime();
			sqlStr = "select * from t_ntmisc_khqj t where orgid=? and jbjx_pid=? and khfw=?";
			if(this.isExist(sqlStr,orgId,jbJxPid,khfw)){
				condUpdMap.put("ywlXx", ywlXx);
				condUpdMap.put("ywlSx", ywlSx);
				condUpdMap.put("bzcpXx", bzcpXx);
				condUpdMap.put("bzcpSx", bzcpSx);
				condUpdMap.put("zhtsXx", zhtsXx);
				condUpdMap.put("zhtsSx", zhtsSx);
				condUpdMap.put("dxXx", dxXx);
				condUpdMap.put("dxSx", dxSx);
				
				condUpdMap.put("orgId", orgId);
				condUpdMap.put("jbJxPid", jbJxPid);
				condUpdMap.put("khfw", khfw);
				condUpdList.add(condUpdMap);
			}else{
				condMap.put("orgId", orgId);
				condMap.put("jbJxPid", jbJxPid);
				condMap.put("khfw", khfw);
				condMap.put("ywlXx", ywlXx);
				condMap.put("ywlSx", ywlSx);
				condMap.put("bzcpXx", bzcpXx);
				condMap.put("bzcpSx", bzcpSx);
				condMap.put("zhtsXx", zhtsXx);
				condMap.put("zhtsSx", zhtsSx);
				condMap.put("dxXx", dxXx);
				condMap.put("dxSx", dxSx);
//				condMap.put("operTime", operTime);
				condList.add(condMap);
			}
		}
		//插入人员分配比例表当期数据
		sqlStr =  " insert into t_ntmisc_khqj(orgid,jbjx_pid,khfw,ywl_xx,ywl_sx,bzcp_xx,bzcp_sx,zhts_xx,zhts_sx,dx_xx,dx_sx) " 
				+  " values(?,?,?,?,?,?,?,?,?,?,?) " ;
		logger.info("执行sql,考核区间表数据批量插入，sqlStr:"+sqlStr+"*****condList:"+condList.toString());
		this.updateBat(condList, daoParent, retMap, sqlStr, 1);
//		更新人员分配比例表当期数据
		sqlStr = "update t_ntmisc_khqj set ywl_xx=?,ywl_sx=?,bzcp_xx=?,bzcp_sx=?,zhts_xx=?,zhts_sx=?,dx_xx=?,dx_sx=?"
				+ "where orgid=? and  jbjx_pid=? and khfw=?";
		logger.info("执行sql,考核区间表数据批量更新，sqlStr:"+sqlStr+"*****condUpdList:"+condUpdList.toString());
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
//			String zq = sheet.getCell(2, i).getContents();//考核周期
			//柜员考核比例
			//支行
			String ywlGyZhXx = sheet.getCell(2, i).getContents();//
			String ywlGyZhSx = sheet.getCell(3, i).getContents();//
			String bzcpGyZhXx = sheet.getCell(4, i).getContents();//
			String bzcpGyZhSx = sheet.getCell(5, i).getContents();//
			String zhtsGyZhXx = sheet.getCell(6, i).getContents();//
			String zhtsGyZhSx = sheet.getCell(7, i).getContents();//
			String dxGyZhXx = sheet.getCell(8, i).getContents();//
			String dxGyZhSx = sheet.getCell(9, i).getContents();//
			//网点
			String ywlGyWdXx = sheet.getCell(10, i).getContents();//
			String ywlGyWdSx = sheet.getCell(11, i).getContents();//
			String bzcpGyWdXx = sheet.getCell(12, i).getContents();//
			String bzcpGyWdSx = sheet.getCell(13, i).getContents();//
			String zhtsGyWdXx = sheet.getCell(14, i).getContents();//
			String zhtsGyWdSx = sheet.getCell(15, i).getContents();//
			String dxGyWdXx = sheet.getCell(16, i).getContents();//
			String dxGyWdSx = sheet.getCell(17, i).getContents();//
			//客户经理考核比例
			//支行
			String bzcpKhjlZhXx = sheet.getCell(18, i).getContents();//柜员-支行-支行特色比例
			String bzcpKhjlZhSx = sheet.getCell(19, i).getContents();//柜员-支行-定性比例
			String zhtsKhjlZhXx = sheet.getCell(20, i).getContents();//柜员-支行-业务量比例
			String zhtsKhjlZhSx = sheet.getCell(21, i).getContents();//柜员-支行-标准产品比例
			String dxKhjlZhXx = sheet.getCell(22, i).getContents();//柜员-支行-支行特色比例
			String dxKhjlZhSx = sheet.getCell(23, i).getContents();//柜员-支行-定性比例
			//网点
			String bzcpKhjlWdXx = sheet.getCell(24, i).getContents();//柜员-支行-支行特色比例
			String bzcpKhjlWdSx = sheet.getCell(25, i).getContents();//柜员-支行-定性比例
			String zhtsKhjlWdXx = sheet.getCell(26, i).getContents();//柜员-支行-业务量比例
			String zhtsKhjlWdSx = sheet.getCell(27, i).getContents();//柜员-支行-标准产品比例
			String dxKhjlWdXx = sheet.getCell(28, i).getContents();//柜员-支行-支行特色比例
			String dxKhjlWdSx = sheet.getCell(29, i).getContents();//柜员-支行-定性比例
			
//			 空行判断
			logger.info("".equals(orgId.trim()));
			if("".equals(orgId.trim())){
				continue;
			}
//			String operTime = iu.getTime();
//			zqList.add(zq);
			if("".equals(orgId.trim())){
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
			rowJson.put("jbJxPid", "1");//绩效考核岗位父类别（1：柜员、2、客户经理）
			rowJson.put("khfw", "1");//考核范围（1、支行；2、网点）
			rowJson.put("ywlXx", ywlGyZhXx);
			rowJson.put("ywlSx", ywlGyZhSx);
			rowJson.put("bzcpXx", bzcpGyZhXx);
			rowJson.put("bzcpSx", bzcpGyZhSx);
			rowJson.put("zhtsXx", zhtsGyZhXx);
			rowJson.put("zhtsSx", zhtsGyZhSx);
			rowJson.put("dxXx", dxGyZhXx);
			rowJson.put("dxSx", dxGyZhSx);
			tableJa.add(rowJson);
			//柜员-网点考核比例
			rowJson = new JSONObject();
			rowJson.put("orgId", orgId);
			rowJson.put("jbJxPid", "1");//绩效考核岗位父类别（1：柜员、2、客户经理）
			rowJson.put("khfw", "2");//考核范围（1、支行；2、网点）
			rowJson.put("ywlXx", ywlGyWdXx);
			rowJson.put("ywlSx", ywlGyWdSx);
			rowJson.put("bzcpXx", bzcpGyWdXx);
			rowJson.put("bzcpSx", bzcpGyWdSx);
			rowJson.put("zhtsXx", zhtsGyWdXx);
			rowJson.put("zhtsSx", zhtsGyWdSx);
			rowJson.put("dxXx", dxGyWdXx);
			rowJson.put("dxSx", dxGyWdSx);
			tableJa.add(rowJson);//
			//客户经理-支行考核比例
			rowJson = new JSONObject();
			rowJson.put("orgId", orgId);
			rowJson.put("jbJxPid", "2");//绩效考核岗位父类别（1：柜员、2、客户经理）
			rowJson.put("khfw", "1");//考核范围（1、支行；2、网点）
			rowJson.put("ywlXx", "0");
			rowJson.put("ywlSx", "0");
			rowJson.put("bzcpXx", bzcpKhjlZhXx);
			rowJson.put("bzcpSx", bzcpKhjlZhSx);
			rowJson.put("zhtsXx", zhtsKhjlZhXx);
			rowJson.put("zhtsSx", zhtsKhjlZhSx);
			rowJson.put("dxXx", dxKhjlZhXx);
			rowJson.put("dxSx", dxKhjlZhSx);
			tableJa.add(rowJson);
			//客户经理-网点考核比例
			rowJson = new JSONObject();
			rowJson.put("orgId", orgId);
			rowJson.put("jbJxPid", "2");//绩效考核岗位父类别（1：柜员、2、客户经理）
			rowJson.put("khfw", "2");//考核范围（1、支行；2、网点）
			rowJson.put("ywlXx", "0");
			rowJson.put("ywlSx", "0");
			rowJson.put("bzcpXx", bzcpKhjlWdXx);
			rowJson.put("bzcpSx", bzcpKhjlWdSx);
			rowJson.put("zhtsXx", zhtsKhjlWdXx);
			rowJson.put("zhtsSx", zhtsKhjlWdSx);
			rowJson.put("dxXx", dxKhjlWdXx);
			rowJson.put("dxSx", dxKhjlWdSx);
			tableJa.add(rowJson);
		}
	}
	
	public void actionToFileUpload(FileUploadEvent e) {
		System.out.println("权重区间设置");
		HashMap retMap = new HashMap();
		tmd = e.getTmd();
		if(txCode.equals(tmd.get("txCode"))){
			ArrayList<String> typeList = new ArrayList<String>();
			tmd.put("title", "批量导入权重区间比例数据");
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
				logger.info("批量导入权重区间数据excel数据 tableJa:"+tableJa);
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
