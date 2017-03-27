/**
 * 员工业绩得分导入 2016-01-12
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

public class Excelf20000001 extends BusParent implements FileUploadListener {
	private static String txCode = "20000001";
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
		iu.setProgress(tmd, "11", "导入用户得分", "0.7");
		JSONArray jsonArray = JSONArray.parseArray(data);
//		logger.info("jsonArray.toJSONString():"+jsonArray.toJSONString());
		LinkedList<LinkedHashMap<String,Object>> condList = new LinkedList<LinkedHashMap<String,Object>>();
		LinkedList<LinkedHashMap<String,Object>> condUpdList = new LinkedList<LinkedHashMap<String,Object>>();
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject json = jsonArray.getJSONObject(i);
			LinkedHashMap<String, Object> condMap = new LinkedHashMap<String, Object>();
			LinkedHashMap<String, Object> condUpdMap = new LinkedHashMap<String, Object>();
			String userId = json.getString("userId");
			String nrId = json.getString("nrId");
			String zq = json.getString("zq");
			String point = json.getString("point");
			condMap.put("userId", userId);
			condMap.put("nrId", nrId);
			condMap.put("zq", zq);
			condMap.put("point", point);
			
			condUpdMap.put("userId", userId);
			condUpdMap.put("zq", zq);
			condUpdMap.put("nrId", nrId);
			condList.add(condMap);
			condUpdList.add(condUpdMap);
		}
//		删除
		sqlStr = "delete  t_ntmisc_userjxn where userid=? and zq=? and nr_id=?";
		iu.infoDbOper("员工业绩得分导入--删除", sqlStr, condUpdList);
		this.updateBat(condUpdList, daoParent, retMap, sqlStr, 1);
//		插入
		sqlStr = "insert into  t_ntmisc_userjxn(userid,nr_id,zq,point) values(?,?,?,?)";
		iu.infoDbOper("员工业绩得分导入--新增", sqlStr, condList);
		this.updateBat(condList, daoParent, retMap, sqlStr, 1);
		iu.setProgress(tmd, "10", "导入成功", "1");
	}
	
	/**
	 * 分支行下属所有用户
	 * @param tmd
	 * @param orgUserJa
	 */
	public void zhUserQuery(TransactionMapData tmd,JSONArray zhUserJa){
		iu.rmCondition(condition);
		String  brOrgId = tmd.get("brOrgId").toString();
		if(brOrgId == null || "".equals(brOrgId)){
			logger.info("无法获取所在分支行号");
			iu.infoPrgsIntrpt(tmd, "无法获取所在分支行号");
			return;
		}
		String orgIn = mediumBus.getOrgIn(brOrgId);
		sqlStr =  " select a.userid,a.jbjx_id,b.jbjx_pid  from t_ntmisc_user a " 
				+  " left join t_ntmisc_jobjx b "  
				+  " on a.jbjx_id = b.jbjx_id " 
				+  " where a.orgid in "
				+  orgIn;
		iu.infoDbOper("分支行下属所有用户", sqlStr, condition);
		this.queryAuto(zhUserJa, condition, sqlStr, daoParent, 1);
	}
	
	/**
	 * 用户绩效考核岗位初始化
	 * @param sheet
	 */
	/*public boolean  userJbIdInit(Sheet  sheet,TransactionMapData tmd){
		HashMap<String, Object> retMap = new HashMap<String, Object>();
		boolean allUserValid = true;
		int rowLenth = sheet.getRows();
		for (int i = 3; i < rowLenth; i++) {
			String rylb = sheet.getCell(0, i).getContents().trim();//人员类别
			String userId = sheet.getCell(1, i).getContents().trim();//人力资源编码
//			String zq = tmd.get("zqCurr").toString();//考核周期
//			String ptGwlz = sheet.getCell(2, i).getContents().trim();//岗位履职得分
//			String ptWdgg = sheet.getCell(3, i).getContents().trim();//网点挂钩得分
//			String ptQyyx = sheet.getCell(4, i).getContents().trim();//全员营销奖励得分
			if("".equals(userId))
				continue;
			iu.rmCondition(condition);
			iu.putCondition(condition, "userId", userId);
			sqlStr = "select * from t_ntmisc_user t where userid = ?";
			JSONArray userJa = new JSONArray();
			iu.infoDbOper("单个用户查询", sqlStr, condition);
			this.queryManu(userJa, condition, sqlStr, daoParent, 1);
			if(userJa.size() == 0){
				allUserValid = false;
				iu.setProgress(tmd, "11", "员工"+userId+"未初始化,请提前初始化", "1");
				logger.info( "员工"+userId+"未初始化,请提前初始化");
				tmd.put("finish", "1");
				break;
			}else{
				JSONObject userJson = userJa.getJSONObject(0);
				if(userJson.getString("jbjx_id") == null || "".equals(userJson.getString("jbjx_id"))){
					iu.rmCondition(condition);
					if("柜员".equals(rylb)){
						iu.putCondition(condition, "jbjx_id", "1");//初始导入时，由于没有初始数据，故默认为“低柜”，如需修改，进入用户管理模块修改 （2016-01-15）
						iu.putCondition(condition, "userId", userId);
					}else if("客户经理".equals(rylb)){
						iu.putCondition(condition, "jbjx_id", "3");//初始导入时，由于没有初始数据，故默认为“对公客户经理”，如需修改，进入用户管理模块修改 （2016-01-15）
						iu.putCondition(condition, "userId", userId);
					}else{
						iu.setProgress(tmd, "11", "员工"+userId+"人员类别错误【柜员、客户经理】", "1");
						logger.info("员工"+userId+"人员类别错误【柜员、客户经理】");
						tmd.put("finish", "1");
					}
					sqlStr = "update t_ntmisc_user t set t.jbjx_id=? where userid=?";
					iu.infoDbOper("修改绩效考核岗位", sqlStr, condition);
					this.update(condition, daoParent, sqlStr, retMap);
				}
			}
		}
		return allUserValid;
	}*/
	
	/**
	 * 分支行下属所有用户绩效考核岗位初始化
	 * @param sheet 个人业绩数据导入
	 * @param ja 分支行下属所有用户用户信息
	 */
	public void jbjxIdInit(Sheet sheet,JSONArray ja,TransactionMapData tmd){
		logger.info("分支行下属所有用户绩效考核岗位初始化");
		iu.setProgress(tmd, "11", "分支行下属所有用户绩效考核岗位初始化", "0.5");
		Map retMap = new HashMap();
		LinkedList<LinkedHashMap<String, Object>> condList = new LinkedList<LinkedHashMap<String,Object>>();
		int rowLenth = sheet.getRows();
		logger.info("rowLenth:"+rowLenth+"|ja:"+ja);
		for (int i = 3; i < rowLenth; i++) {//遍历excel
			LinkedHashMap<String, Object> condMap = new LinkedHashMap<String, Object>();
			String gwlzType = "";//岗位履职类型（柜员、客户经理）
			JSONObject rowJson = new JSONObject();
			String gw = sheet.getCell(0, i).getContents().trim();//岗位：柜员、客户经理
			String userId = sheet.getCell(1, i).getContents().trim();//人力资源编码
			String zq = tmd.get("zqCurr").toString();//考核周期
			String ptGwlz = sheet.getCell(3, i).getContents().trim();//岗位履职得分
			String ptWdgg = sheet.getCell(4, i).getContents().trim();//网点挂钩得分
			String ptQyyx = sheet.getCell(5, i).getContents().trim();//全员营销奖励得分
//			logger.info("111111111111gw:"+gw);
			if("".equals(userId)){
				continue;
			}else{
				if(!"柜员".equals(gw) &&  !"客户经理".equals(gw)){
					iu.infoPrgsIntrpt(tmd, "人员类别错误,导入失败");
					logger.error("人员类别错误,导入失败");
					return;
				}
				for (int j = 0; j < ja.size(); j++) {//遍历分支行用户
					JSONObject userJson = ja.getJSONObject(j);
//					logger.info("userJson:"+userJson.toJSONString());
					if(userId.equals(userJson.getString("userid")) && userJson.getString("jbjx_id").equals("")){
						if("柜员".equals(gw)){
							condMap.put("jbjx_id", "1");//绩效考核岗位初始化时，柜员默认为：低柜   客户经理默认为：对公客户经理 
						}else if("客户经理".equals(gw)){
							condMap.put("jbjx_id", "3");//绩效考核岗位初始化时，柜员默认为：低柜   客户经理默认为：对公客户经理
						}
						condMap.put("userid", userId);
						condList.add(condMap);
					}
				}
			}
		}
		sqlStr = " update t_ntmisc_user t set t.jbjx_id=? where t.userid=? ";
		iu.infoDbOper("分支行下属所有用户绩效考核岗位初始化", sqlStr, condList);
		this.updateBat(condList, daoParent, retMap, sqlStr, 1);
	}
	
	/**
	 * 用户合法性校验
	 * @param tmd
	 * @param sheet
	 * @param zhUserJa
	 */
	public void userIsValid(TransactionMapData tmd,Sheet sheet,JSONArray zhUserJa){
		iu.setProgress(tmd, "11", "用户合法性校验", "0.4");
		int rowLenth = sheet.getRows();
		StringBuffer sb = new StringBuffer();
		boolean userIsValid = true;//用户合法性标识
		logger.info("当前登录用户:"+tmd.get("userIdLogin")+"可见用户："+zhUserJa.toJSONString());
		for (int i = 3; i < rowLenth; i++) {
			String gwlzType = "";//岗位履职类型（柜员、客户经理）
			String userId = sheet.getCell(1, i).getContents().trim();//人力资源编码
			String userName = sheet.getCell(2, i).getContents().trim();//姓名
			if("".equals(userId)){
				continue;
			}
			//校验用户可见性
			boolean userIdVisible = false;//excel 机构编号合法性（必须为当前用户所在分支行下属机构，防止当前分支行机构导入其他分支行总包数据）
			for (int j = 0; j < zhUserJa.size(); j++) {
				JSONObject zhUserJson = zhUserJa.getJSONObject(j);
				if(userId.equals(zhUserJson.getString("userid"))){
					userIdVisible = true;
				}
			}
			//校验用户合法性
			logger.debug("i:"+i+"|userId:"+userId+"|userName:"+userName);
			if(!userIdVisible){//用户非法(用户不可见)
				userIsValid = false;
				JSONArray tempUserJa = new JSONArray();
				tmd.put("userId", userId);//查询人力资源编码为：userId的用户
				logger.info("对于当前用户:"+tmd.get("userIdLogin")+"不可见"+userId);
				mediumBus.userInfoQuery(tempUserJa, tmd);
				sb.append("用户合法性未通过,异常信息:");
				if(tempUserJa.size() == 0){//用户不存在
					sb.append("<br/>人力资源编码:"+userId+"|姓名："+userName+",用户不存在");
					logger.error("人力资源编码:"+userId+"|姓名："+userName+",用户不存在");
				}else{//本机机构无法查看兄弟机构及上级机构用户
					String tempOrgName = tempUserJa.getJSONObject(0).getString("orgname");
					sb.append("<br/>人力资源编码:"+userId+"|姓名："+userName+"|所属机构:"+tempOrgName+",当前登录"+tmd.get("userIdLogin")+"用户无操作权限");
					logger.error("\n人力资源编码:"+userId+"|姓名："+userName+"|所属机构:"+tempOrgName+",当前登录"+tmd.get("userIdLogin")+"用户无操作权限");
				}
//				return;
			}
		}
		if(userIsValid){
			iu.setProgress(tmd, "11", "用户合法性校验通过", "0.4");
		}else{
			iu.infoPrgsIntrpt(tmd, sb.toString());
		}
	}
	
	public void actionToFileUpload(FileUploadEvent e) {
//		tmd = e.getTmd();
		logger.info("个人业绩导入");
		tmd.tmdLogger();
		tmd.put("breakFlag", "0");//中断标识
		logger.debug("tttttttttttttt:"+tmd.get("breakFlag"));
		JSONArray zhUserJa = new JSONArray();//分支行下属所有用户
		HashMap retMap = new HashMap();
		String brOrgId = tmd.get("brOrgId").toString();
		logger.info("人员信息批量上传actionToFileUpload");
		tmd.tmdLogger();
		String zqCurr = tmd.get("zqCurr").toString();//当前考核周期
		if("".equals(zqCurr)){
			iu.infoPrgsIntrpt(tmd, "当前考核周期不存在，请管理员提前设置当前考核周期");
		}
		logger.debug("00000000000000:"+tmd.get("breakFlag"));
		if(txCode.equals(tmd.get("tx_code"))){
			ArrayList<String> typeList = new ArrayList<String>();
			tmd.put("title", "员工业绩得分导入");
//			tmd.put("errorMsg", "上传成功,正在解析文件");
			iu.setProgress(tmd, "11", "上传成功,正在解析文件", "0.2");
			logger.debug("000000011111:"+tmd.get("breakFlag"));
			//解析excel
//			String title = tmd.get("title").toString();
			try {
				FileInputStream fis = new FileInputStream(new File(tmd.get("path")+"\\"+tmd.get("fileName")));
				Workbook workBook = Workbook.getWorkbook(fis);
				Sheet  sheet = workBook.getSheet(0);
				int rowLenth = sheet.getRows();
				
				for (int i = 3; i < rowLenth; i++) {
					try {
						String zq = sheet.getCell(6, i).getContents().trim();//考核周期
					} catch (Exception e2) {
						// TODO: handle exception
						iu.infoPrgsIntrpt(tmd, "所导入表格缺少考核周期列,导入失败!");
						logger.info("表格缺少考核周期列,导入失败!");
						return;
					}
				}
				
				iu.setProgress(tmd, "11", "解析成功,准备批量导入", "0.3");
//				查询分支行下属员工
				/*this.zhUserQuery(tmd, zhUserJa);
				if("1".equals(tmd.get("breakFlag"))){
					return;
				}*/
				JSONArray vbUserJa = (JSONArray) tmd.get("vbUserJa");//可见用户
				zhUserJa = vbUserJa;
//				分支行下属所有用户绩效考核岗位初始化
				this.jbjxIdInit(sheet, zhUserJa, tmd);
				if("1".equals(tmd.get("breakFlag"))){
					return;
				}
//				用户合法性校验
				this.userIsValid(tmd, sheet, zhUserJa);
				if("1".equals(tmd.get("breakFlag"))){
					return;
				}
				JSONArray tableJa = new JSONArray();//表格数据
				for (int i = 3; i < rowLenth; i++) {
					String gwlzType = "未指定";//岗位履职类型（柜员、客户经理）
					JSONObject rowJson = new JSONObject();
					String userId = sheet.getCell(1, i).getContents().trim();//人力资源编码
					String userName = sheet.getCell(2, i).getContents().trim();//姓名
					
					String ptGwlz = sheet.getCell(3, i).getContents().trim();//岗位履职得分
					String ptWdgg = sheet.getCell(4, i).getContents().trim();//网点挂钩得分
					String ptQyyx = sheet.getCell(5, i).getContents().trim();//全员营销奖励得分
					String zq = sheet.getCell(6, i).getContents().trim();//考核周期
					
//					校验空数据
					if("".equals(userId.trim()))
						continue;//人力资源编号为空的行不做任何解析处理
					else if(!"".equals(userId)){
						if("".equals(ptGwlz) || "".equals(ptWdgg) || "".equals(ptQyyx) || "".equals(zq)){
							iu.infoPrgsIntrpt(tmd, "表格存在空数据,导入失败!");
							logger.info("表格存在空数据,导入失败！");
							return;
						}
					}
					//校验用户可见性
					boolean userIdValid = false;//excel 机构编号合法性（必须为当前用户所在分支行下属机构，防止当前分支行机构导入其他分支行总包数据）
					for (int j = 0; j < zhUserJa.size(); j++) {
						JSONObject zhUserJson = zhUserJa.getJSONObject(j);
						logger.debug("zhUserJson:"+zhUserJson.toJSONString());
						if(userId.equals(zhUserJson.getString("userid"))){
							userIdValid = true;
							logger.debug("zhUserJson.getString(\"jbjx_pid\"):"+zhUserJson.getString("jbjx_pid"));
							if(zhUserJson.getString("jbjx_pid").equals("1")){//柜员
								gwlzType = "1";//岗位履职类型 1：柜员  2：客户经理
							}else{//客户经理
								gwlzType = "2";
							}
						}
					}
					//校验周期
					if(!zq.equals(zqCurr)){
						iu.infoPrgsIntrpt(tmd, "人力资源编码:"+userId+"|姓名:"+userName+",考核周期:"+zq+"，与当前考核周期:"+zqCurr+"不匹配,导入失败!");
						return;
					}
					//校验岗位履职得分
					if(!iu.isNumber(ptGwlz)){
						iu.infoPrgsIntrpt(tmd, "人力资源编码:"+userId+"|姓名:"+userName+"岗位履职得分:"+ptGwlz+"网点挂钩得分:"+ptWdgg+"全员营销奖励得分:"+ptQyyx+",岗位履职考核得分必须为数字,导入失败!");
						return;
					}
					//校验网点挂钩得分
					if(!iu.isNumber(ptWdgg)){
						iu.infoPrgsIntrpt(tmd, "人力资源编码:"+userId+"|姓名:"+userName+"岗位履职得分:"+ptGwlz+"网点挂钩得分:"+ptWdgg+"全员营销奖励得分:"+ptQyyx+",网点挂钩考核得分必须为数字,导入失败!");
						return;
					}
					//校验全员营销奖励得分
					if(!iu.isNumber(ptQyyx)){
						iu.infoPrgsIntrpt(tmd, "人力资源编码:"+userId+"|姓名:"+userName+"岗位履职得分:"+ptGwlz+"网点挂钩得分:"+ptWdgg+"全员营销奖励得分:"+ptQyyx+",全员营销奖励得分必须为数字,导入失败!");
						iu.infoPrgsIntrpt(tmd, "全员营销奖励得分必须为数字,导入失败!");
						return;
					}
					//岗位履职得分
					rowJson.put("userId", userId);
					rowJson.put("nrId", gwlzType);
					rowJson.put("zq", zq);
					rowJson.put("point", ptGwlz);
					tableJa.add(rowJson);
					//柜员岗位履职总包
					rowJson  = new JSONObject();
					rowJson.put("userId", userId);
					rowJson.put("nrId", "3");
					rowJson.put("zq", zq);
					rowJson.put("point", ptWdgg);
					tableJa.add(rowJson);
					//柜员岗位履职总包
					rowJson  = new JSONObject();
					rowJson.put("userId", userId);
					rowJson.put("nrId", "5");
					rowJson.put("zq", zq);
					rowJson.put("point", ptQyyx);
					tableJa.add(rowJson);
				}
				logger.info("员工业绩得分导入 tableJa:"+tableJa);
				if("0".equals(tmd.get("breakFlag"))){
					this.doBat(retMap, tableJa+"");
					if(!"0".equals(retMap.get("recUpdCount"))){
						iu.setProgress(tmd, "10", "批量导入成功!", "1");
						logger.info("批量导入成功！");
					}else{
						iu.setProgress(tmd, "10", "导入失败,数据录入失败!", "0");
						logger.info("导入失败,数据录入失败！");
					}
				}
				tmd.put("finish", "1");
			} catch (Exception e2) {
				iu.error(logger, e2);
				iu.setProgress(tmd, "11", "系统异常,导入失败！", "0");
				logger.info("系统异常,导入失败！");
			}
		}
	}
}
