/**
 * 分支行考核部分总包导入（柜员岗位履职总包、基础任务营销考核总包、全员营销奖励总包） 2016-01-11
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
import com.icbc.nt.bus.dispatcher.BusDispatcherImpl;
import com.icbc.nt.util.TransactionMapData;

public class Excelf20000003 extends BusParent implements FileUploadListener {
	private static String txCode = "20000003";
	@Autowired
	private BusDispatcherImpl busDispatcherImpl;
	@Autowired 
	private MediumBus mediumBus;
	
	/**
	 * 批量更新
	 * @param retMap
	 * @param data
	 */
	public void doBat(Map retMap,String data){
		tmd.put("errorMsg", "更新用户绩效表!");
		JSONArray jsonArray = JSONArray.parseArray(data);
		logger.debug("jsonArray.toJSONString():"+jsonArray.toJSONString());
		LinkedList<LinkedHashMap<String,Object>> condList = new LinkedList<LinkedHashMap<String,Object>>();
		LinkedList<LinkedHashMap<String,Object>> condUpdList = new LinkedList<LinkedHashMap<String,Object>>();
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject json = jsonArray.getJSONObject(i);
			LinkedHashMap<String, Object> condMap = new LinkedHashMap<String, Object>();
			LinkedHashMap<String, Object> condUpdMap = new LinkedHashMap<String, Object>();
			String orgId = json.getString("orgId");
			String zq = json.getString("zq");
			String nrId = json.getString("nrId");
			String zb = json.getString("zb");
			condMap.put("orgId", orgId);
			condMap.put("zq", zq);
			condMap.put("nrId", nrId);
			condMap.put("zb", zb);
			condUpdMap.put("orgId", orgId);
			condUpdMap.put("zq", zq);
			condUpdMap.put("nrId", nrId);
			condList.add(condMap);
			condUpdList.add(condUpdMap);
		}
//		删除
		sqlStr = "delete t_ntmisc_khzb where orgid=? and zq=? and nr_id=?";
		logger.info("总包导入(分支行考核)--删除当期考核总包");
		iu.infoDbOper("总包导入(分支行考核)--删除当期考核总包", sqlStr, condUpdList);
		this.updateBat(condUpdList, daoParent, retMap, sqlStr, 1);
//		插入
		sqlStr = "insert into t_ntmisc_khzb(orgid,zq,nr_id,zb) values(?,?,?,?)";
		logger.info("总包导入(分支行考核)--新增当期考核总包");
		iu.infoDbOper("总包导入(分支行考核)--新增当期考核总包", sqlStr, condList);
		this.updateBat(condList, daoParent, retMap, sqlStr, 1);
	}
	
	public void actionToFileUpload(FileUploadEvent e) {
		JSONArray subOrgJa = new JSONArray();//分支行下属所有子结构
		
		HashMap retMap = new HashMap();
		tmd = e.getTmd();
		String brOrgId = tmd.get("brOrgId").toString();//机构所在分支行号
//		mediumBus.getSubOrg(subOrgJa, tmd.get("brOrgId").toString());
//		logger.info("subOrgJa:"+subOrgJa);
		String zqCurr = tmd.get("zqCurr").toString();//当前考核周期
		if("".equals(zqCurr)){
			iu.infoPrgsIntrpt(tmd, "当前考核周期不存在，请管理员提前设置当前考核周期");
		}
		if(txCode.equals(tmd.get("tx_code"))){
			ArrayList<String> typeList = new ArrayList<String>();
			tmd.put("title", "总包导入(支行考核)");
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
					String orgId = sheet.getCell(0, i).getContents().trim();//机构编号
					String zq = sheet.getCell(2, i).getContents().trim();//考核周期
					String zbGylz = sheet.getCell(3, i).getContents().trim();//柜员岗位履职总包
					String zbKhjlLz = sheet.getCell(4, i).getContents().trim();//客户经理岗位履职总包
					String zbQyyx = sheet.getCell(5, i).getContents().trim();//全员营销奖励总包
					
//					 空行判断
					if("".equals(orgId.trim()) || "".equals(zq.trim())|| "".equals(zbGylz.trim())
							|| "".equals(zbKhjlLz.trim())|| "".equals(zbQyyx.trim())){
						tmd.put("errorCode", "11");
						tmd.put("errorMsg", "表格存在空数据,导入失败!");
						tmd.put("finish", "1");
						logger.error("表格存在空数据,导入失败！");
						return;
					}
					//校验机构编号可见性
					boolean orgIdValid = false;//excel 机构编号合法性（必须为当前用户所在分支行下属机构，防止当前分支行机构导入其他分支行总包数据）
					/*for (int j = 0; j < subOrgJa.size(); j++) {
						JSONObject subOrgJson = subOrgJa.getJSONObject(j);
						if(orgId.equals(subOrgJson.getString("menuid"))){
							orgIdValid = true;
						}
					}*/
					if(orgId.equals(brOrgId)){
						orgIdValid = true;
					}
					logger.info("orgIdValid:"+orgIdValid);
					if(!orgIdValid){
						iu.infoPrgsIntrpt(tmd, "导入excel表格机构编号非法,导入失败!");
						return;
					}
					if(!zq.equals(zqCurr)){
//						iu.infoPrgsIntrpt(tmd, "考核周期错误,导入失败!");
						iu.infoPrgsIntrpt(tmd, "机构编号:"+orgId+",考核周期:"+zq+"，与当前考核周期:"+zqCurr+"不匹配,导入失败!");
						return;
					}
					if(!iu.isNumber(zbGylz)){
						iu.infoPrgsIntrpt(tmd, "柜员履职考核总包必须为数字,导入失败!");
						return;
					}
					if(!iu.isNumber(zbKhjlLz)){
						iu.infoPrgsIntrpt(tmd, "客户经理履职总包必须为数字,导入失败!");
						return;
					}
					if(!iu.isNumber(zbQyyx)){
						iu.infoPrgsIntrpt(tmd, "全员营销奖励总包必须为数字,导入失败!");
						return;
					}
					//柜员岗位履职总包
					rowJson.put("orgId", orgId);
					rowJson.put("zq", zq);
					rowJson.put("nrId", "1");
					rowJson.put("zb", zbGylz);
					tableJa.add(rowJson);
					//柜员岗位履职总包
					rowJson  = new JSONObject();
					rowJson.put("orgId", orgId);
					rowJson.put("zq", zq);
					rowJson.put("nrId", "2");
					rowJson.put("zb", zbKhjlLz);
					tableJa.add(rowJson);
					//柜员岗位履职总包
					rowJson  = new JSONObject();
					rowJson.put("orgId", orgId);
					rowJson.put("zq", zq);
					rowJson.put("nrId", "5");
					rowJson.put("zb", zbQyyx);
					tableJa.add(rowJson);
				}
				
				logger.debug("批量导入总包导入(分支行考核部分) tableJa:"+tableJa);
				if(!excFlag){
//					1、更新机构总包系数表
					this.doBat(retMap, tableJa+"");
					if(!"0".equals(retMap.get("recUpdCount"))){
						tmd.put("errorCode", "10");
						tmd.put("errorMsg", "批量导入成功!");
						logger.info("批量导入成功！");
					}else{
						tmd.put("errorCode", "11");
						tmd.put("errorMsg", "导入失败,数据录入失败!");
						logger.error("导入失败,数据录入失败！");
					}
				}
				tmd.put("finish", "1");
			} catch (Exception e2) {
				iu.error(logger, e2);
				tmd.put("finish", "1");
				tmd.put("errorCode", "11");
				tmd.put("errorMsg", "系统异常,导入失败,请核对所导入excel表格数据格式是否正确！");
				logger.error("系统异常,导入失败,请核对所导入excel表格数据格式是否正确！");
			}
		}
	}
}
