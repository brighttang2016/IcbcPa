/**
 * 批量导入定性考核基础数据2015-11-09
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

public class Excelf30132 extends BusParent implements FileUploadListener {
	private static String txCode = "30132";
	@Autowired
	MediumBus mediumBus;
	
	public void doBat(Map retMap,String data){
		JSONArray jsonArray = JSONArray.parseArray(data);
		logger.info("jsonArray.toJSONString():"+jsonArray.toJSONString());
		LinkedList<LinkedHashMap<String,Object>> condList = new LinkedList<LinkedHashMap<String,Object>>();
		LinkedList<LinkedHashMap<String,Object>> condDelList = new LinkedList<LinkedHashMap<String,Object>>();//先删除原始数据
		LinkedList<LinkedHashMap<String,Object>> condAddList = new LinkedList<LinkedHashMap<String,Object>>();//先删除原始数据
		
		JSONArray userJxJa = new JSONArray();
		iu.rmCondition(condition);
		sqlStr = "select * from t_ntmisc_userjx";
		this.queryAuto(userJxJa, condition, sqlStr, daoParent, 1);
		logger.info("用户绩效表当前信息userJxJa："+userJxJa);
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject json = jsonArray.getJSONObject(i);
			LinkedHashMap<String, Object> condMap = new LinkedHashMap<String, Object>();
			LinkedHashMap<String, Object> condDelMap = new LinkedHashMap<String, Object>();
			LinkedHashMap<String, Object> condAddMap = new LinkedHashMap<String, Object>();
			condMap.put("userId", json.getString("userId"));
			condMap.put("point", json.getString("point"));
			condMap.put("operTime", json.getString("operTime"));
			condMap.put("used", json.getString("used"));//初始插入，标识为：未使用
			condMap.put("zq", json.getString("zq"));
			
			condDelMap.put("userId", json.getString("userId"));
			condDelMap.put("zq", json.getString("zq"));
			condDelMap.put("used", "0");
//			condDelMap.put("seqNum", json.getString("seqNum"));
			condList.add(condMap);
			condDelList.add(condDelMap);
			
			boolean existFlag = false;
			for (int j = 0; j < userJxJa.size(); j++) {
				JSONObject userJxJson = userJxJa.getJSONObject(j);
				if(json.getString("userId").equals(userJxJson.getString("user_id")) && 
						json.getString("zq").equals(userJxJson.getString("zq"))){
					existFlag = true;
				}
			}
			if(!existFlag){
				boolean isExistFlag = false;
				for (int j = 0; j < condAddList.size(); j++) {
					LinkedHashMap<String, Object> condAddMapExist = condAddList.get(j);
					if(json.getString("userId").equals(condAddMapExist.get("userId")) && json.getString("zq").equals(condAddMapExist.get("zq")) ){
						isExistFlag = true;
						logger.info("condAddMap中userid 和zq对应记录已存在");
					}
				}
				if(!isExistFlag){
					condAddMap.put("userId", json.getString("userId"));
					condAddMap.put("zq", json.getString("zq"));
					condAddList.add(condAddMap);
				}
			}
		}
		//清空当期导入的未处理数据
//		sqlStr = "delete t_ntmisc_bzcpmx t where user_id = ? and zq=? and t.used=? and t.seq_num=? ";
		sqlStr = "delete t_ntmisc_dx t where user_id = ? and zq=? and t.used=? ";
		this.updateBat(condDelList, daoParent, retMap, sqlStr, 1);
		//重新插入当期数据
		sqlStr = "insert into t_ntmisc_dx(user_id,point,oper_time,used,zq) values(?,?,?,?,?)";
		this.updateBat(condList, daoParent, retMap, sqlStr, 1);
		//扫描用户绩效表，没有当期用户，则插入
		sqlStr = "insert into t_ntmisc_userjx(user_id,zq) values(?,?)";
		this.updateBat(condAddList, daoParent, retMap, sqlStr, 1);
	}
	
	/**
	 * 获取机构指定的标准产品分值
	 * @param seqNum
	 * @param userId
	 * @return
	 */
/*	public String getPoint(String seqNum,String userId,TransactionMapData tmd){
		String point = "获取失败";
		String orgId = mediumBus.getUserOrg(userId);
		JSONArray  pointJa = new JSONArray();
		JSONObject pointJson = new JSONObject();//当前标准产品在机构中的分值
		iu.rmCondition(condition);
		iu.putCondition(condition, "seq_num", seqNum);
		iu.putCondition(condition, "orgid", orgId);
		sqlStr = "select a.orgid,a.seq_num,a.point org_point from t_ntmisc_orgzhts a";
		this.queryAuto(pointJa, condition, sqlStr, daoParent, 1);
		try {
			pointJson = pointJa.getJSONObject(0);
			point = pointJson.getString("org_point");
		} catch (Exception e) {
			logger.info("存在未指定的机构考核指标,导入失败!（所导入的表格中存在围在t_ntmisc_orgzhts表中指定的产品序号）");
		}
		logger.info("获取分值point:"+point);
		return point;
	}*/
	
	public void actionToFileUpload(FileUploadEvent e) {
//		String point = "";//机构设定的标准产品分值
		HashMap retMap = new HashMap();
		tmd = e.getTmd();
//		mpc.pushMessage(tmd.get("userId").toString(), "准备写入excel");
		if(txCode.equals(tmd.get("txCode"))){
			ArrayList<String> typeList = new ArrayList<String>();
			tmd.put("title", "批量导入定性考核基础数据");
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
				ArrayList<String> zqList = new ArrayList<String>();
				for (int i = 3; i < rowLenth; i++) {
					JSONObject rowJson = new JSONObject();
//					LinkedHashMap<String, Object> rowMap = new LinkedHashMap<String, Object>();
					String userId = sheet.getCell(0, i).getContents();
					String point = sheet.getCell(2, i).getContents();//定性加扣分
					String zq = sheet.getCell(3,i).getContents();//考核周期数
					zqList.add(zq);
					if("".equals(userId.trim())){
						tmd.put("errorCode", "11");
						tmd.put("errorMsg", "人力资源编码存在空数据,导入失败!");
						tmd.put("finish", "1");
						logger.info("人力资源编码存在空数据,导入失败！");
						return;
					}
					if(!iu.isNumber(point)){
						tmd.put("errorCode", "11");
						tmd.put("errorMsg", "定性加扣分必须为数字,导入失败!");
						tmd.put("finish", "1");
						logger.info("定性加扣分必须为数字,导入失败！");
						return;
					}
					rowJson.put("userId", userId);
					rowJson.put("point", point);
					rowJson.put("operTime", iu.getDate());
					rowJson.put("used", "0");
					rowJson.put("zq", zq);
				/*	String orgBzcpPoint = this.getPoint(seqNum,userId,tmd);
					if(orgBzcpPoint.equals("获取失败")){
						excFlag = true;
						tmd.put("excFlag", true);
						tmd.put("errorCode", "11");
						tmd.put("errorMsg", "存在机构考核范围外序号,导入失败!");
						tmd.put("finish", "1");
						return;
					}
					rowJson.put("point", orgBzcpPoint);//机构设定的产品分值
					*/
					/*HashMap<String, Object> condition = new HashMap<String, Object>();
					iu.rmCondition(condition);
					condition.put("userId", userId);
					String userIdLogin = tmd.get("userId").toString();
					String orgId = mediumBus.getUserOrg(userIdLogin);
					String orgIn = mediumBus.getOrgIn(orgId);
					sqlStr = " select * from (select * from t_ntmisc_user where orgid not in "
							+ orgIn
							+ " ) t where t.userid=? ";
					JSONArray ja = new JSONArray();
					this.queryManu(ja, condition, sqlStr, daoParent, 1);
					if(ja.size() > 1){
						excFlag = true;
						tmd.put("errorCode", "11");
						tmd.put("finish", "1");
						tmd.put("errorMsg", "存在非本机构人力资源编码:"+userId+",导入失败！");
						logger.info("存在非本机构人力资源编码:"+userId+",导入失败！");
						return;
					}
					
					ja.clear();
					sqlStr = " select * from t_ntmisc_user t where t.userid=? ";
					this.queryManu(ja, condition, sqlStr, daoParent, 1);
					if(ja.size() < 1){
						excFlag = true;
						tmd.put("errorCode", "11");
						tmd.put("finish", "1");
						tmd.put("errorMsg", "存在未注册的人力资源编码:"+userId+",导入失败！");
						logger.info("存在未注册的人力资源编码:"+userId+",导入失败！");
						return;
					}
					logger.info("rowJson:"+rowJson.toJSONString()+"rowLenth:"+rowLenth);*/
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
				
				logger.info("批量导入标准产品基础数据excel数据 tableJa:"+tableJa);
				/*if(!excFlag){
					sqlStr = "insert into t_ntmisc_ywlmx(user_id,dj,sg,zs,ts,rj,oper_time,used,zq)";
				}*/
				
				if(!excFlag){
//					mediumBus.uctBat(retMap, tableJa+"");
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
