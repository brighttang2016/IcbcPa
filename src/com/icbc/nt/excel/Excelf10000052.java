/**
 * 批量导入机构分配数据2015-12-10
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

public class Excelf10000052 extends BusParent implements FileUploadListener {
	private static String txCode = "10000052";
	@Autowired
	private BusDispatcherImpl busDispatcherImpl;
	/**
	 * 对应机构周期记录是否存在
	 * @param orgId
	 * @param zq
	 * @return
	 */
	public boolean isExist(String sqlStr,String orgId,String zq){
		boolean isExist = false;
		iu.rmCondition(condition);
		iu.putCondition(condition, "orgId", orgId);
		iu.putCondition(condition, "zq", zq);
		JSONArray ja = new JSONArray();
//		sqlStr = "select * from t_ntmisc_orgzb t where orgid=? and zq=?";
		this.queryManu(ja, condition, sqlStr, daoParent, 1);
		logger.info("机构总包表，对应机构周期记录是否存在，ja:"+ja.toJSONString());
		if(ja.size() > 0)
			isExist = true;
		return isExist;
	}
	/**
	 * 机构总包系数查询
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void zbxsQuery(JSONArray ja, LinkedHashMap<String, Object> condition, Map retMap, TransactionMapData tmd){
		tmd.put("orgIdcurr", tmd.get("orgId"));
		busDispatcherImpl.f10000053(ja, condition, retMap, tmd);
	}
	
	/**
	 * 机构总包人数表更新
	 * @param retMap
	 * @param data
	 */
	public void orgZbrsUpd(Map retMap,String data){
		tmd.put("errorMsg", "初始化机构总包人数表信息!");
		JSONArray jsonArray = JSONArray.parseArray(data);
		logger.info("jsonArray.toJSONString():"+jsonArray.toJSONString());
		LinkedList<LinkedHashMap<String,Object>> condAddList = new LinkedList<LinkedHashMap<String,Object>>();
		LinkedList<LinkedHashMap<String,Object>> condUpdList = new LinkedList<LinkedHashMap<String,Object>>();
		for (int i = 0; i < jsonArray.size(); i++) {
//			JSONArray empNumJa = new JSONArray();//网点总人数：机构id、机构下属总人数、机构下属总人数转换、机构下属参与考核人数、机构下属不参与考核人数、机构下属参与考核人数转换
			JSONArray empNumJbjxJa = new JSONArray();//网点各绩效考核岗位对应人数：机构号、绩效岗位编号、机构总人数、机构总人数转换、机构参与考核总人数、机构参与考核总人数转换
			JSONObject json = jsonArray.getJSONObject(i);
			LinkedHashMap<String, Object> condMap = new LinkedHashMap<String, Object>();
			
			String orgId = json.getString("orgId");
			String zq = json.getString("zq");
			tmd.put("orgId", orgId);
			//网点各绩效考核岗位对应人数
			busDispatcherImpl.empNumJbjx(empNumJbjxJa, condition, retMap, tmd);
			logger.info(i+"-----网点各绩效考核岗位对应人数empNumJbjxJa:"+empNumJbjxJa.toJSONString());
			for (int j = 0; j < empNumJbjxJa.size(); j++) {
				LinkedHashMap<String, Object> condAddMap = new LinkedHashMap<String, Object>();
				LinkedHashMap<String, Object> condUpdMap = new LinkedHashMap<String, Object>();
//				empNumJson:{"rn":"1","zrs":"11","orgid":"0310000823","zrs_kh":"8","zrs_bcykh_zh":"5","zrs_kh_zh":"16","zrs_zh":"21","zrs_bcykh":"3"}
				JSONObject empNumJbjxJson = empNumJbjxJa.getJSONObject(j);
				String orgIdTemp = empNumJbjxJson.getString("orgid");
//				logger.info("绩效岗位id："+empNumJbjxJson.getFloatValue("jbjx_id"));
				
				String jbjx_id = empNumJbjxJson.getString("jbjx_id");
				String zrs = empNumJbjxJson.getString("zrs");//总人数
//				float zrs_zh = empNumJbjxJson.getFloatValue("zrs_zh");//网点总人数转换
				String zrs_kh = empNumJbjxJson.getString("zrs_kh");//网点参与考核总人数
				String zrs_kh_zh = empNumJbjxJson.getString("zrs_kh_zh");//网点参与考核总人数转换
				String jx_pid = "0";//绩效考核岗位父类（1、柜员；2、客户经理）
				
				if("".equals(jbjx_id)){
					logger.info("岗位绩效id不存在");
					continue;
				}else if("1".equals(jbjx_id) || "2".equals(jbjx_id)){
					jx_pid = "1";
				}else{
					jx_pid = "2";
				}
//				float zrs_bcykh = empNumJbjxJson.getFloatValue("zrs_bcykh");//不参与考核人数
//				float zrs_bcykh_zh = empNumJbjxJson.getFloatValue("zrs_bcykh_zh");//不参与考核人数转换
				sqlStr = "select * from t_ntmisc_orgzbrs t where orgid=? and zq=?";
				if(this.isExist(sqlStr,orgId, zq) && orgIdTemp.equals(orgId)){
					
					condUpdMap.put("zrs", zrs);
					condUpdMap.put("zrs_kh", zrs_kh);
					condUpdMap.put("zrs_kh_zh", zrs_kh_zh);
					condUpdMap.put("orgId", orgId);
					condUpdMap.put("zq", zq);
					condUpdMap.put("jx_pid", jx_pid);
					condUpdMap.put("jbjx_id", jbjx_id);
					condUpdList.add(condUpdMap);
				}else{
					condAddMap.put("orgId", orgId);
					condAddMap.put("zq", zq);
					condAddMap.put("jx_pid", jx_pid);
					condAddMap.put("jbjx_id", jbjx_id);
					condAddMap.put("zrs", zrs);
					condAddMap.put("zrs_kh", zrs_kh);
					condAddMap.put("zrs_kh_zh", zrs_kh_zh);
					condAddList.add(condAddMap);
				}
			}
		}
//		插入机构总包人数表当期人数信息
		sqlStr = " insert into t_ntmisc_orgzbrs(orgid,zq,jbjx_pid,jbjx_id,rs_gw,rs_kh,rs_kh_zh) values(?,?,?,?,?,?,?)";
		logger.info("执行sql,插入机构总包人数表当期人数信息批量更新，sqlStr:"+sqlStr+"*****condAddList:"+condAddList.toString());
		this.updateBat(condAddList, daoParent, retMap, sqlStr, 1);
//		更新机构总包人数表当期人数信息
		sqlStr = " update t_ntmisc_orgzbrs  set rs_gw=?,rs_kh=?,rs_kh_zh=? where orgid=? and zq=? and jbjx_pid=? and jbjx_id=?";
		logger.info("执行sql,更新机构总包人数表当期人数信息批量更新，sqlStr:"+sqlStr+"*****condUpdList:"+condUpdList.toString());
		this.updateBat(condUpdList, daoParent, retMap, sqlStr, 1);
	}
	
	/**
	 * 批量更新总包人均表人数（网点总人数、网点总人数转换、网点参与考核总人数、网点参与考核总人数转换、不参与考核人数、不参与考核人数转换）
	 * @param retMap
	 * @param data
	 */
	public void orgZbrjUpd(Map retMap,String data){
		tmd.put("errorMsg", "初始化机构人均表人数信息!");
		JSONArray jsonArray = JSONArray.parseArray(data);
		logger.info("jsonArray.toJSONString():"+jsonArray.toJSONString());
//		LinkedList<LinkedHashMap<String,Object>> condList = new LinkedList<LinkedHashMap<String,Object>>();
		LinkedList<LinkedHashMap<String,Object>> condUpdList = new LinkedList<LinkedHashMap<String,Object>>();
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONArray empNumJa = new JSONArray();//网点总人数：机构id、机构下属总人数、机构下属总人数转换、机构下属参与考核人数、机构下属不参与考核人数、机构下属参与考核人数转换
			JSONArray empNumJbjxJa = new JSONArray();//网点各绩效考核岗位对应人数：机构号、绩效岗位编号、机构总人数、机构总人数转换、机构参与考核总人数、机构参与考核总人数转换
			JSONObject json = jsonArray.getJSONObject(i);
			LinkedHashMap<String, Object> condMap = new LinkedHashMap<String, Object>();
			
			String orgId = json.getString("orgId");
			String zq = json.getString("zq");
			tmd.put("orgId", orgId);
			//网点总人数
			busDispatcherImpl.empNumOrg(empNumJa, condition, retMap, tmd);
			logger.info(i+"-----网点总人数empNumJa:"+empNumJa.toJSONString());
			for (int j = 0; j < empNumJa.size(); j++) {
				LinkedHashMap<String, Object> condUpdMap = new LinkedHashMap<String, Object>();
//				empNumJson:{"rn":"1","zrs":"11","orgid":"0310000823","zrs_kh":"8","zrs_bcykh_zh":"5","zrs_kh_zh":"16","zrs_zh":"21","zrs_bcykh":"3"}
				JSONObject empNumJson = empNumJa.getJSONObject(j);
				String orgIdTemp = empNumJson.getString("orgid");
				if(orgId.equals(orgIdTemp)){
					float zrs = empNumJson.getFloatValue("zrs");//网点总人数
					float zrs_zh = empNumJson.getFloatValue("zrs_zh");//网点总人数转换
					float zrs_kh = empNumJson.getFloatValue("zrs_kh");//网点参与考核总人数
					float zrs_kh_zh = empNumJson.getFloatValue("zrs_kh_zh");//网点参与考核总人数转换
					float zrs_bcykh = empNumJson.getFloatValue("zrs_bcykh");//不参与考核人数
					float zrs_bcykh_zh = empNumJson.getFloatValue("zrs_bcykh_zh");//不参与考核人数转换
					
					condUpdMap.put("zrs", zrs);
					condUpdMap.put("zrs_zh", zrs_zh);
					condUpdMap.put("zrs_kh", zrs_kh);
					condUpdMap.put("zrs_kh_zh", zrs_kh_zh);
					condUpdMap.put("zrs_bcykh", zrs_bcykh);
					condUpdMap.put("zrs_bcykh_zh", zrs_bcykh_zh);
					condUpdMap.put("orgId", orgId);
					condUpdMap.put("zq", zq);
					condUpdList.add(condUpdMap);
				}
			}
		}
//		更新机构总包人均表当期人数信息
		sqlStr = " update t_ntmisc_orgzbrj t set t.zrs=?,t.zrs_zh=?,t.zrs_cykh=?,t.zrs_cykh_zh=?,t.zrs_bcykh=?,t.zrs_bcykh_zh=?"
				+ " where t.orgid=? and t.zq=?";
		logger.info("执行sql,机构总包人均表当期人数信息批量更新，sqlStr:"+sqlStr+"*****condUpdList:"+condUpdList.toString());
		this.updateBat(condUpdList, daoParent, retMap, sqlStr, 1);
	}
	
	/**
	 * 批量更新总包人均表
	 * @param retMap
	 * @param data
	 */
	public void doBatOrgZbrj(Map retMap,String data){
		tmd.put("errorMsg", "初始化机构总包人均表!");
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
			String zbInit = json.getString("zbInit");
			String blMova = json.getString("blMova");
			String operTime = json.getString("operTime");
			float zbMova = iu.getFloatDecimal(Float.parseFloat(zbInit) * Float.parseFloat(blMova)+"", 2);//纳入mova考核部分总包
			sqlStr = "select * from t_ntmisc_orgzbrj t where orgid=? and zq=?";
			if(this.isExist(sqlStr,orgId, zq)){
				condUpdMap.put("zbInit", zbInit);
				condUpdMap.put("blMova", blMova);
				condUpdMap.put("zbMova", zbMova+"");
				condUpdMap.put("orgId", orgId);
				condUpdMap.put("zq", zq);
				condUpdList.add(condUpdMap);
			}else{
				condMap.put("orgId", orgId);
				condMap.put("zq", zq);
				condMap.put("zbInit", zbInit);
				condMap.put("blMova", blMova);
				condMap.put("zbMova", zbMova+"");
				condMap.put("operTime", operTime);
				condList.add(condMap);
			}
		}
		//插入机构总包人均表当期数据
		sqlStr = "insert into t_ntmisc_orgzbrj(orgid,zq,zb_init,bl_mova,zb_mova,oper_time) values(?,?,?,?,?,?)";
		logger.info("执行sql,机构总包人均表数据批量插入，sqlStr:"+sqlStr+"*****condList:"+condList.toString());
		this.updateBat(condList, daoParent, retMap, sqlStr, 1);
//		更新机构总包人均表当期数据
		sqlStr = "update t_ntmisc_orgzbrj set zb_init=?,bl_mova=?,zb_mova=? where orgid=? and zq=?";
		logger.info("执行sql,机构总包人均表数据批量更新，sqlStr:"+sqlStr+"*****condUpdList:"+condUpdList.toString());
		this.updateBat(condUpdList, daoParent, retMap, sqlStr, 1);
	}
	
	/**
	 * 批量更新总包系数表
	 * @param retMap
	 * @param data
	 */
	public void doBat(Map retMap,String data){
		tmd.put("errorMsg", "更新机构总包系数表!");
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
//			String zbInit = json.getString("zbInit");
//			String blMova = json.getString("blMova");
			String xsGy = json.getString("xsGy");
			String xsKhjl = json.getString("xsKhjl");
			
			String xsblDg = json.getString("xsblDg");
			String xsblGg = json.getString("xsblGg");
			String xsblDgKhjl = json.getString("xsblDgKhjl");
			String xsblXsKhjl = json.getString("xsblXsKhjl");
			String xsblDtKhjl = json.getString("xsblDtKhjl");
			
			String operTime = json.getString("operTime");
			sqlStr = "select * from t_ntmisc_orgzbxs t where orgid=? and zq=?";
			if(this.isExist(sqlStr,orgId, zq)){
				//柜员-低柜
				condUpdMap = new LinkedHashMap<String, Object>();
				condUpdMap.put("xsRy", xsGy);
				condUpdMap.put("xsBl", xsblDg);
				condUpdMap.put("orgId", orgId);
				condUpdMap.put("zq", zq);
				condUpdMap.put("jbJxPid", "1");//绩效考核岗位父类（1、柜员；2、客户经理）
				condUpdMap.put("jbJxId", "1");//绩效考核岗位类别（1低柜2高柜3对公4销售5大堂）
				condUpdList.add(condUpdMap);
				//柜员-高柜
				condUpdMap = new LinkedHashMap<String, Object>();
				condUpdMap.put("xsRy", xsGy);
				condUpdMap.put("xsBl", xsblGg);
				condUpdMap.put("orgId", orgId);
				condUpdMap.put("zq", zq);
				condUpdMap.put("jbJxPid", "1");//绩效考核岗位父类（1、柜员；2、客户经理）
				condUpdMap.put("jbJxId", "2");//绩效考核岗位类别（1低柜2高柜3对公4销售5大堂）
				condUpdList.add(condUpdMap);
				//客户经理-对公
				condUpdMap = new LinkedHashMap<String, Object>();
				condUpdMap.put("xsRy", xsKhjl);
				condUpdMap.put("xsBl", xsblDgKhjl);
				condUpdMap.put("orgId", orgId);
				condUpdMap.put("zq", zq);
				condUpdMap.put("jbJxPid", "2");//绩效考核岗位父类（1、柜员；2、客户经理）
				condUpdMap.put("jbJxId", "3");//绩效考核岗位类别（1低柜2高柜3对公4销售5大堂）
				condUpdList.add(condUpdMap);
				//客户经理-销售
				condUpdMap = new LinkedHashMap<String, Object>();
				condUpdMap.put("xsRy", xsKhjl);
				condUpdMap.put("xsBl", xsblXsKhjl);
				condUpdMap.put("orgId", orgId);
				condUpdMap.put("zq", zq);
				condUpdMap.put("jbJxPid", "2");//绩效考核岗位父类（1、柜员；2、客户经理）
				condUpdMap.put("jbJxId", "4");//绩效考核岗位类别（1低柜2高柜3对公4销售5大堂）
				condUpdList.add(condUpdMap);
				//客户经理-大堂
				condUpdMap = new LinkedHashMap<String, Object>();
				condUpdMap.put("xsRy", xsKhjl);
				condUpdMap.put("xsBl", xsblDtKhjl);
				condUpdMap.put("orgId", orgId);
				condUpdMap.put("zq", zq);
				condUpdMap.put("jbJxPid", "2");//绩效考核岗位父类（1、柜员；2、客户经理）
				condUpdMap.put("jbJxId", "5");//绩效考核岗位类别（1低柜2高柜3对公4销售5大堂）
				condUpdList.add(condMap);
				condUpdList.add(condUpdMap);
			}else{
				//柜员-低柜
				condMap = new LinkedHashMap<String, Object>();
				condMap.put("orgId", orgId);
				condMap.put("zq", zq);
				condMap.put("jbJxPid", "1");//绩效考核岗位父类（1、柜员；2、客户经理）
				condMap.put("jbJxId", "1");//绩效考核岗位类别（1低柜2高柜3对公4销售5大堂）
				condMap.put("xsRy", xsGy);
				condMap.put("xsBl", xsblDg);
				condList.add(condMap);
				//柜员-高柜
				condMap = new LinkedHashMap<String, Object>();
				condMap.put("orgId", orgId);
				condMap.put("zq", zq);
				condMap.put("jbJxPid", "1");//绩效考核岗位父类（1、柜员；2、客户经理）
				condMap.put("jbJxId", "2");//绩效考核岗位类别（1低柜2高柜3对公4销售5大堂）
				condMap.put("xsRy", xsGy);
				condMap.put("xsBl", xsblGg);
				condList.add(condMap);
				//客户经理-对公
				condMap = new LinkedHashMap<String, Object>();
				condMap.put("orgId", orgId);
				condMap.put("zq", zq);
				condMap.put("jbJxPid", "2");//绩效考核岗位父类（1、柜员；2、客户经理）
				condMap.put("jbJxId", "3");//绩效考核岗位类别（1低柜2高柜3对公4销售5大堂）
				condMap.put("xsRy", xsKhjl);
				condMap.put("xsBl", xsblDgKhjl);
				condList.add(condMap);
				//客户经理-销售
				condMap = new LinkedHashMap<String, Object>();
				condMap.put("orgId", orgId);
				condMap.put("zq", zq);
				condMap.put("jbJxPid", "2");//绩效考核岗位父类（1、柜员；2、客户经理）
				condMap.put("jbJxId", "4");//绩效考核岗位类别（1低柜2高柜3对公4销售5大堂）
				condMap.put("xsRy", xsKhjl);
				condMap.put("xsBl", xsblXsKhjl);
				condList.add(condMap);
				//客户经理-大堂
				condMap = new LinkedHashMap<String, Object>();
				condMap.put("orgId", orgId);
				condMap.put("zq", zq);
				condMap.put("jbJxPid", "2");//绩效考核岗位父类（1、柜员；2、客户经理）
				condMap.put("jbJxId", "5");//绩效考核岗位类别（1低柜2高柜3对公4销售5大堂）
				condMap.put("xsRy", xsKhjl);
				condMap.put("xsBl", xsblDtKhjl);
				condList.add(condMap);
			}
		}
//		插入机构总包系数表当期数据
		sqlStr = "insert into t_ntmisc_orgzbxs(orgid,zq,jbjx_pid,jbjx_id,xs_ry,xs_bl) values(?,?,?,?,?,?)";
		logger.info("执行sql,机构总包表数据批量插入，sqlStr:"+sqlStr+"*****condList:"+condList.toString());
		this.updateBat(condList, daoParent, retMap, sqlStr, 1);
//		更新机构总包系数表当期数据
		sqlStr = "update t_ntmisc_orgzbxs set xs_ry=?,xs_bl=? where orgid=? and zq=? and jbjx_pid=? and jbjx_id=?";
		logger.info("执行sql,机构总包表数据批量更新，sqlStr:"+sqlStr+"*****condUpdList:"+condUpdList.toString());
		this.updateBat(condUpdList, daoParent, retMap, sqlStr, 1);
	}
	
	public void actionToFileUpload(FileUploadEvent e) {
		HashMap retMap = new HashMap();
		tmd = e.getTmd();
		if(txCode.equals(tmd.get("txCode"))){
			ArrayList<String> typeList = new ArrayList<String>();
			tmd.put("title", "批量导入机构分配数据");
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
					String zq = sheet.getCell(2, i).getContents();//考核周期
					String zbInit = sheet.getCell(3, i).getContents();//初次分配机构总包
					String blMova = sheet.getCell(4, i).getContents();//MOVA总包挂钩比例
					String xsGy = sheet.getCell(5, i).getContents();//机构柜员系数
					String xsKhjl = sheet.getCell(6, i).getContents();//机构客户经理系数
					String xsblDg = sheet.getCell(7, i).getContents();//低柜总包保留系数
					String xsblGg = sheet.getCell(8, i).getContents();//高柜总包保留系数
					String xsblDgKhjl = sheet.getCell(9, i).getContents();//对公客户经理总包保留系数
					String xsblXsKhjl = sheet.getCell(10, i).getContents();//销售客户经理总包保留系数
					String xsblDtKhjl = sheet.getCell(11, i).getContents();//大堂客户经理总包保留系数
					
//					 空行判断
					logger.info("表格空行判断："+orgId+"|"+zbInit+"|"+blMova+"|"+xsGy+"|"+xsKhjl+"|"+zq+"|");
					logger.info("".equals(orgId.trim()) && "".equals(zbInit.trim())&& "".equals(blMova.trim())
							&& "".equals(xsGy.trim())&& "".equals(xsKhjl.trim())&& "".equals(zq.trim()));
					if("".equals(orgId.trim()) && "".equals(zbInit.trim())&& "".equals(blMova.trim())
							&& "".equals(xsGy.trim())&& "".equals(xsKhjl.trim())&& "".equals(zq.trim())){
						continue;
					}
//					String operTime = iu.getTime();
					zqList.add(zq);
					if("".equals(orgId.trim()) || "".equals(zbInit.trim())|| "".equals(blMova.trim())
							|| "".equals(xsGy.trim())|| "".equals(xsKhjl.trim())|| "".equals(zq.trim())
							|| "".equals(xsblDg.trim())|| "".equals(xsblGg.trim())|| "".equals(xsblDgKhjl.trim())
							|| "".equals(xsblXsKhjl.trim())|| "".equals(xsblDtKhjl.trim())){
						tmd.put("errorCode", "11");
						tmd.put("errorMsg", "表格存在空数据,导入失败!");
						tmd.put("finish", "1");
						logger.info("表格存在空数据,导入失败！");
						return;
					}
					if(!iu.isNumber(zbInit)){
						tmd.put("errorCode", "11");
						tmd.put("errorMsg", "初次分配机构总包必须为数字,导入失败!");
						tmd.put("finish", "1");
						logger.info("初次分配机构总包为数字,导入失败！");
						return;
					}
					rowJson.put("orgId", orgId);
					rowJson.put("zq", zq);
					rowJson.put("zbInit", zbInit);
					rowJson.put("blMova", blMova);
					rowJson.put("xsGy", xsGy);
					rowJson.put("xsKhjl", xsKhjl);
					rowJson.put("xsblDg", xsblDg);
					rowJson.put("xsblGg", xsblGg);
					rowJson.put("xsblDgKhjl", xsblDgKhjl);
					rowJson.put("xsblXsKhjl", xsblXsKhjl);
					rowJson.put("xsblDtKhjl", xsblDtKhjl);
					rowJson.put("operTime", iu.getTime());
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
				
				logger.info("批量导入机构分配数据excel数据 tableJa:"+tableJa);
				if(!excFlag){
//					1、更新机构总包系数表
					this.doBat(retMap, tableJa+"");
//					2、同步初始化机构总包人均表(机构编号、考核周期、操作时间)
					this.doBatOrgZbrj(retMap, tableJa+"");
//					3、初始化机构人均表人数信息（网点总人数、网点总参与人数、参与考核总人数、参与考核总人数转换、不参与考核人数、不参与考核人数转换、初次分配绩效总包）
					this.orgZbrjUpd(retMap, tableJa+"");
//					4、初始化总包人数表人数信息t_ntmisc_orgzbrs
					this.orgZbrsUpd(retMap, tableJa+"");
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
