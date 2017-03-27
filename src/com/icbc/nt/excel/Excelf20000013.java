/**
 * 网点手动分配绩效导入（网点挂钩考核） 2016-01-12
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

public class Excelf20000013 extends BusParent implements FileUploadListener {
	private static String txCode = "20000013";
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
//		tmd.put("errorMsg", "更新用户绩效表!");
		iu.setProgress(tmd, "11", "更新用户手动分配绩效", "0.9");
		JSONArray jsonArray = JSONArray.parseArray(data);
		logger.debug("jsonArray.toJSONString():"+jsonArray.toJSONString());
		LinkedList<LinkedHashMap<String,Object>> condList = new LinkedList<LinkedHashMap<String,Object>>();
		LinkedList<LinkedHashMap<String,Object>> condUpdList = new LinkedList<LinkedHashMap<String,Object>>();
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject json = jsonArray.getJSONObject(i);
			LinkedHashMap<String, Object> condMap = new LinkedHashMap<String, Object>();
			LinkedHashMap<String, Object> condUpdMap = new LinkedHashMap<String, Object>();
			String userId = json.getString("userId");
			String nrId = json.getString("nrId");
			String zq = json.getString("zq");
			String jx = json.getString("jx");
			condMap.put("userId", userId);
			condMap.put("nrId", nrId);
			condMap.put("zq", zq);
			condMap.put("jx", jx);
			condUpdMap.put("userId", userId);
			condUpdMap.put("zq", zq);
			condUpdMap.put("nrId", nrId);
			condList.add(condMap);
			condUpdList.add(condUpdMap);
		}
//		删除
		sqlStr = "delete t_ntmisc_userjxn where userid=? and zq=? and nr_id=?";
		logger.info("网点手动分配绩效导入--删除");
		iu.infoDbOper("网点手动分配绩效导入--删除", sqlStr, condUpdList);
		this.updateBat(condUpdList, daoParent, retMap, sqlStr, 1);
//		插入
		sqlStr = "insert into t_ntmisc_userjxn(userid,nr_id,zq,jx) values(?,?,?,?)";
		logger.info("网点手动分配绩效导入--新增");
		iu.infoDbOper("网点手动分配绩效导入--新增", sqlStr, condList);
		this.updateBat(condList, daoParent, retMap, sqlStr, 1);
	}
	
	/**
	 * 网点用户判断(只有网点管理员有权限手动分配网点柜员绩效)
	 * @param tmd
	 * @return true:网点用户 false：非网点用户
	 */
	public boolean isWdUser(TransactionMapData tmd){
		JSONArray ja = new JSONArray();
		boolean isWdUser = false;
		String orgId = tmd.get("orgIdLogin").toString();//当前登录用户所在机构号
		iu.rmCondition(condition);
		iu.putCondition(condition, "orgid", orgId);
		sqlStr = "select * from t_ntmisc_org where porgid=?";
		this.queryManu(ja, condition, sqlStr, daoParent, 1);
		if(ja.size() == 0){
			isWdUser = true;
		}else{
			isWdUser = false;
		}
		return isWdUser;
	}
	
	/**
	 * 当前登录用户所见所有柜员查询
	 * @param orgGyJa 网点柜员查询结果
	 * @param tmd 变量池
	 */
	public void gyQuery(JSONArray orgGyJa,TransactionMapData tmd){
//		String orgId = tmd.get("orgIdLogin").toString();//当前登录用户所在机构号
		iu.rmCondition(condition);
//		iu.putCondition(condition, "orgid", orgId);
		String orgIdIn = tmd.get("orgIdIn").toString();
		iu.putCondition(condition, "jbjx_pid", "1");//柜员

		sqlStr =  " select a.userid,a.orgid,a.depid,a.name,b.jbjx_pid from t_ntmisc_user a,t_ntmisc_jobjx b " 
				+  " where a.jbjx_id = b.jbjx_id and a.orgid in "
				+ orgIdIn
				+ " and b.jbjx_pid=? ";
		logger.info("用户所见所有柜员查询");
		iu.infoDbOper("用户所见所有柜员查询", sqlStr, condition);
		this.queryManu(orgGyJa, condition, sqlStr, daoParent, 1);
		logger.debug("用户所见所有柜员查询，查询结果："+orgGyJa.toJSONString());
	}
	
	/**
	 * 手动分配绩效总包数据初始化判断（点击“绩效计算”后，初始化该总包）
	 * @param tmd
	 * @return true:已经初始化 false:暂时未初始化
	 */
	public boolean zbIsInit(TransactionMapData tmd,String orgId,String zq){
		JSONArray ja = new JSONArray();
		boolean zbInit = false;
//		String orgId = tmd.get("orgIdLogin").toString();//当前登录用户所在机构号
//		String zqCurr = tmd.get("zqCurr").toString();//当前考核周期
		iu.rmCondition(condition);
		iu.putCondition(condition, "orgid", orgId);
		iu.putCondition(condition, "nrId", "4");//手动分配绩效
		iu.putCondition(condition, "zq", zq);
		sqlStr = "select * from t_ntmisc_khzb t where t.orgid=? and nr_id=? and zq=?";
		logger.info("柜员手动分配绩效总包数据初始化判断");
		iu.infoDbOper("柜员手动分配绩效总包数据初始化判断", sqlStr, condition);
		this.queryManu(ja, condition, sqlStr, daoParent, 1);
		if(ja.size() > 0){
			zbInit = true;
		}else{
			zbInit = false;
		}
		return zbInit;
	}
	
	public void actionToFileUpload(FileUploadEvent e) {
//		JSONArray subOrgJa = new JSONArray();//分支行下属所有子机构
		logger.debug("网点柜员手动分配绩效");
		float jxSum = 0;
		JSONArray orgGyJa = new JSONArray();//网点所有柜员
		HashMap retMap = new HashMap();
		tmd = e.getTmd();
		String brOrgId = tmd.get("brOrgId").toString();//当前登录用户所在支行号
//		String orgId = tmd.get("orgId").toString();//当前登录用户所在机构号
//		mediumBus.getSubOrg(subOrgJa, tmd.get("brOrgId").toString());
		String zqCurr = tmd.get("zqCurr").toString();//当前考核周期
		if("".equals(zqCurr)){
			iu.infoPrgsIntrpt(tmd, "当前考核周期不存在，请管理员提前设置当前考核周期");
		}
		if(txCode.equals(tmd.get("tx_code"))){
			this.gyQuery(orgGyJa, tmd);
			ArrayList<String> typeList = new ArrayList<String>();
			tmd.put("title", "网点手动分配绩效导入");
			iu.setProgress(tmd, "11", "上传成功,正在解析文件", "0.3");
			//解析excel
//			String title = tmd.get("title").toString();
			try {
				FileInputStream fis = new FileInputStream(new File(tmd.get("path")+"\\"+tmd.get("fileName")));
				Workbook workBook = Workbook.getWorkbook(fis);
				Sheet sheet = workBook.getSheet(0);
//				tmd.put("errorMsg", "解析成功,准备批量导入");
				iu.setProgress(tmd, "11", "解析成功,准备批量导入", "0.5");
				int rowLenth = sheet.getRows();
				boolean excFlag = false;//异常标识，false:无异常 true:存在异常
//				LinkedList<LinkedHashMap<String, Object>> condList = new LinkedList<LinkedHashMap<String,Object>>();
				JSONArray tableJa = new JSONArray();//表格数据
				
				for (int i = 3; i < rowLenth; i++) {
					JSONObject rowJson = new JSONObject();
					String orgId = sheet.getCell(0, i).getContents().trim();//机构编号
					String zq = sheet.getCell(2, i).getContents().trim();//考核周期
					String userId = sheet.getCell(3, i).getContents().trim();//人力资源编码
					String jx = sheet.getCell(5, i).getContents().trim();//手动分配绩效
					
					//柜员手动分配绩效总包暂未初始化判断
					if(i == 3){
						if(!this.zbIsInit(tmd,orgId,zq)){
							iu.infoPrgsIntrpt(tmd, "柜员:"+userId+",机构号:"+orgId+",手动分配绩效总包暂未初始化,导入失败!");
							return;
						}
					}
					
//					 空行判断
					if("".equals(userId.trim())){
						continue;
					}else if("".equals(zq.trim()) || "".equals(jx.trim())){
						iu.infoPrgsIntrpt(tmd, "表格存在空数据,导入失败！");
						logger.error("表格存在空数据,导入失败！");
						return;
					}
					/*if(!this.isWdUser(tmd)){
						iu.infoPrgsIntrpt(tmd, "非网点用户,无柜员绩效分配权限,导入失败!");
						return;
					}*/
					//校验用户编号可见性
					boolean userIdValid = false;//excel 机构编号合法性（必须为当前用户所在分支行下属机构，防止当前分支行机构导入其他分支行总包数据）
					for (int j = 0; j < orgGyJa.size(); j++) {//手动分配仅限柜员类员工
						JSONObject orgGyJson = orgGyJa.getJSONObject(j);
						if(userId.equals(orgGyJson.getString("userid"))){
							userIdValid = true;
							break;
						}
					}
					logger.debug("userIdValid:"+userIdValid);
					if(!userIdValid){
//						iu.infoPrgsIntrpt(tmd, "存在当前登录用户不可见的柜员或存在非柜员类用户,导入失败!");
						iu.infoPrgsIntrpt(tmd, "当前登录用户："+tmd.get("userIdLogin")+",对导入的如下用户无权限：人力资源编码|机构号:"+userId+"|"+orgId+",请确保所导入用户为柜员");
						return;
					}
					if(!zq.equals(zqCurr)){
//						iu.infoPrgsIntrpt(tmd, "考核周期错误,导入失败!");
						iu.infoPrgsIntrpt(tmd, "人力资源编码:"+userId+",考核周期:"+zq+"，与当前考核周期:"+zqCurr+"不匹配,导入失败!");
						return;
					}
					if(!iu.isNumber(jx)){
						iu.infoPrgsIntrpt(tmd, "绩效必须为数字,导入失败!");
						return;
					}
					//网点手动分配绩效导入
					rowJson.put("userId", userId);
					rowJson.put("nrId", "4");
					rowJson.put("zq", zqCurr);
					rowJson.put("jx", jx);
					jxSum += Float.parseFloat(jx);
					tableJa.add(rowJson);
				}
				//总包校验
//				tmd.put("jxSum", jxSum);//手动分配总绩效
				JSONArray sdfpZbJa = new JSONArray();
				busDispatcherImpl.zbSdfpQuery(sdfpZbJa, condition, retMap, tmd);
				try {
					JSONObject sdfbJson = sdfpZbJa.getJSONObject(0);
					float sdfpZb = sdfbJson.getFloat("zb");//手动分配总包
					String orgId = sdfbJson.getString("orgid");
					logger.debug("导入所有柜员绩效之和jxSum："+jxSum+"|机构当期柜员可分配总包sdfpZb："+sdfpZb);
					if(sdfpZb == jxSum){
						iu.setProgress(tmd, "11", "绩效总包校验通过", "0.8");
					}else{
						iu.infoPrgsIntrpt(tmd, "导入用户绩效总和:"+jxSum+",机构当期柜员可分配总包:"+sdfpZb+",不一致,导入失败!");
						return;
					}
				} catch (Exception e2) {
					e2.printStackTrace();
					logger.error(e2);
					iu.setProgress(tmd, "11", "用户"+tmd.get("userId")+"所在网点柜员手动分配总包不存在,导入失败", "0.8");
					logger.error("用户"+tmd.get("userId")+"所在网点柜员手动分配总包不存在");
					return;
				}
				logger.debug("批量导入网点手动分配绩效 tableJa:"+tableJa);
				if(!excFlag){
//					1、更新机构总包系数表
					this.doBat(retMap, tableJa+"");
					if(!"0".equals(retMap.get("recUpdCount"))){
						iu.setProgress(tmd, "10", "批量导入成功!", "1");
						logger.info("批量导入成功！");
					}else{
						iu.setProgress(tmd, "11", "导入失败,数据录入失败!", "1");
						logger.error("导入失败,数据录入失败！");
					}
				}
				tmd.put("finish", "1");
			} catch (Exception e2) {
				e2.printStackTrace();
				iu.error(logger, e2);
				iu.infoPrgsIntrpt(tmd, "系统异常,导入失败！");
				logger.error("系统异常,导入失败！");
			}
		}
	}
}
