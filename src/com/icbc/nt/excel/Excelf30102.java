/**
 * 批量导入业务量基础数据2015-11-06
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

public class Excelf30102 extends BusParent implements FileUploadListener {
	private static String txCode = "30102";
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
			condMap.put("dj", json.getString("dj"));
			condMap.put("sg", json.getString("sg"));
			condMap.put("zs", json.getString("zs"));
			condMap.put("ts", json.getString("ts"));
			condMap.put("rj", json.getString("rj"));
			condMap.put("operTime", iu.getDate());
			condMap.put("used", "0");//初始插入，标识为：未使用
			condMap.put("zq", json.getString("zq"));
			
			condDelMap.put("userId", json.getString("userId"));
			condDelMap.put("zq", json.getString("zq"));
			condDelMap.put("used", "0");
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
		sqlStr = "delete t_ntmisc_ywlmx t where user_id = ? and zq=? and t.used=? ";
		this.updateBat(condDelList, daoParent, retMap, sqlStr, 1);
		//重新插入当期数据
		sqlStr = "insert into t_ntmisc_ywlmx(user_id,dj,sg,zs,ts,rj,oper_time,used,zq) values(?,?,?,?,?,?,?,?,?)";
		this.updateBat(condList, daoParent, retMap, sqlStr, 1);
		//扫描用户绩效表，没有当期用户，则插入
		sqlStr = "insert into t_ntmisc_userjx(user_id,zq) values(?,?)";
		this.updateBat(condAddList, daoParent, retMap, sqlStr, 1);
//		iu.rmCondition(condition);
	}
	
	public void actionToFileUpload(FileUploadEvent e) {
		logger.info("Excelf30094 actionToFileUpload");
		// TODO Auto-generated method stub
		HashMap retMap = new HashMap();
		tmd = e.getTmd();
//		mpc.pushMessage(tmd.get("userId").toString(), "准备写入excel");
		if(txCode.equals(tmd.get("txCode"))){
			ArrayList<String> typeList = new ArrayList<String>();
			tmd.put("title", "批量导入业务量基础数据");
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
					String dj = sheet.getCell(2, i).getContents();//大机业务量
					String sg = sheet.getCell(3,i).getContents();//手工业务量
					String ts = sheet.getCell(4,i).getContents();//当班天数
					String zq = sheet.getCell(5,i).getContents();//考核周期数
					if("".equals(userId.trim())){
						tmd.put("errorCode", "11");
						tmd.put("errorMsg", "人力资源编码存在空数据,导入失败!");
						tmd.put("finish", "1");
						logger.info("人力资源编码存在空数据,导入失败！");
						return;
					}
					if(!"".equals(dj) && !iu.isNumber(dj)){
						tmd.put("errorCode", "11");
						tmd.put("errorMsg", "大机业务量必须为数字,导入失败!");
						tmd.put("finish", "1");
						logger.info("大机业务量必须为数字,导入失败！");
						return;
					}
					if(!"".equals(sg) && !iu.isNumber(sg)){
						tmd.put("errorCode", "11");
						tmd.put("errorMsg", "手工业务量必须为数字,导入失败!");
						tmd.put("finish", "1");
						logger.info("手工业务量必须为数字,导入失败！");
						return;
					}
					if(!"".equals(ts) && !iu.isNumber(ts)){
						tmd.put("errorCode", "11");
						tmd.put("errorMsg", "当班天数必须为数字,导入失败!");
						tmd.put("finish", "1");
						logger.info("当班天数必须为数字,导入失败！");
						return;
					}
					rowJson.put("userId", userId);
					rowJson.put("dj", dj);
					rowJson.put("sg", sg);
					float zs = iu.getFloatDecimal(dj, 2)+iu.getFloatDecimal(sg, 2);
					rowJson.put("zs", zs+"");//折算业务量
					rowJson.put("ts", ts);
					float rj = 0;//日均业务量
					try {
						rj = iu.getFloatDecimal(zs/Float.parseFloat(ts)+"", 2);
					} catch (Exception e2) {
						logger.info("日均业务量计算错误 userId:"+userId+"|zs:"+zs+"|ts:"+ts);
					}
					rowJson.put("rj", rj);
					rowJson.put("operTime", iu.getDate());
					rowJson.put("zq", zq);
					
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
				logger.info("批量导入业务量基础数据excel数据 tableJa:"+tableJa);
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
