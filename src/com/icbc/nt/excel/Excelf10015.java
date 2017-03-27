/**
 * 员工批量导入2016-02-29
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

import org.apache.commons.collections.map.LinkedMap;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.bus.FileUploadEvent;
import com.icbc.nt.bus.MediumBus;
import com.icbc.nt.bus.dispatcher.BusDispatcherImpl;
import com.icbc.nt.util.TransactionMapData;

public class Excelf10015 extends BusParent implements FileUploadListener {
	private static String txCode = "10015";
	@Autowired
	BusDispatcherImpl busDispatcherImpl;
	@Autowired
	MediumBus mediumBus;

	public void jbQuery(JSONArray jbJa){
		sqlStr = " select a.jb_id,a.jb_name,a.jb_idp,b.jb_name jb_pname from t_ntmisc_job a,t_ntmisc_job b "
				+ " where a.jb_idp = b.jb_id ";
		iu.rmCondition(condition);
		iu.infoDbOper("职务层级及岗位序列查询", sqlStr, condition);
		this.queryAuto(jbJa, condition, sqlStr, daoParent, 1);
	}
	
	/**
	 * 获取岗位层级对应的岗位类别
	 * @param jbId
	 * @return
	 */
	public String getJbCat(String jbId){
		JSONArray jbJa = new JSONArray();
		String jbCat = "";
		iu.rmCondition(condition);
		iu.putCondition(condition, "jbId", jbId);
		sqlStr = "select jb_idp from t_ntmisc_job where jb_id=?";
		this.queryManu(jbJa, condition, sqlStr, daoParent, 1);
		if(jbJa.size() > 0){
			JSONObject jbJson = jbJa.getJSONObject(0);
			String jbIdp = jbJson.getString("jb_idp");
			if(jbIdp.equals("0")){
				jbCat = jbId;
			}else{
				jbCat = getJbCat(jbIdp);
			}	
		}
		return jbCat;
	}
	
	/**
	 * 绩效考核岗位查询
	 * @param jobJxJa
	 * @param jbjx_name
	 * @param jbjx_pname
	 */
	public void jobJxQuery(JSONArray jobJxJa,String jbjx_name,String jbjx_pname){
		iu.rmCondition(condition);
		iu.putCondition(condition, "jbjx_name", jbjx_name);
		iu.putCondition(condition, "jbjx_pname", jbjx_pname);
		sqlStr = "select * from t_ntmisc_jobjx t where jbjx_name = ? and jbjx_pname = ?";
		this.queryManu(jobJxJa, condition, sqlStr, daoParent, 1);
	}
	
	/**
	 * 已存在用户查询
	 * @param existUserArr 保存已存在用户
	 * @param sheet 解析excel数据
	 * @return
	 */
	public boolean existUserQuery(ArrayList<String> existUserArr,Sheet sheet){
		boolean hasExistUser = false;
		JSONArray ja = new JSONArray();
		iu.rmCondition(condition);
		sqlStr = "select userid from t_ntmisc_user";
		this.queryAuto(ja, condition, sqlStr, daoParent, 1);
		for (int i = 0; i < ja.size(); i++) {
			JSONObject json = ja.getJSONObject(i);
			String userId = json.getString("userid");
			for (int j = 3; j < sheet.getRows(); j++) {
				String userIdExcel = sheet.getCell(0, j).getContents();
				if(userId.equals(userIdExcel)){
					hasExistUser = true;
					existUserArr.add(userId+"|"+sheet.getCell(2, j).getContents()+"【已经存在】");
					break;
				}
			}
		}
		return hasExistUser;
	}
	
	/**
	 * 导入表格中重复人力资源编码判断
	 * @return true：重复，false：不重复
	 */
	public boolean isDup(Sheet sheet){
		boolean dupFlag = false;//导入表格中人力资源编码重复标识
		int rowNum = sheet.getRows();
		for (int i = 3; i < rowNum; i++){
			for (int j = i+1; j < rowNum; j++) {
				try {
					if("".equals(sheet.getCell(0, i).getContents()))
						continue;
//					logger.info("重复人力资源编码判断:"+sheet.getCell(0, i).getContents()+"|"+sheet.getCell(0, j).getContents());
					if(sheet.getCell(0, i).getContents().equals(sheet.getCell(0, j).getContents())){//存在重复内容
						logger.info("员工初始化，导入excel存在重复人力资源编码："+sheet.getCell(0, i));
						dupFlag = true;
						break;
					}
				} catch (Exception e) {}
			}
		}
		return dupFlag;
	}
	
	/**
	 * 用户数据初始化
	 * @param path
	 * @param tmd
	 */
	public void dataInit(String path,TransactionMapData tmd){
		logger.info("数据初始化");
		int rowIngore = 0;//excel表格中，人力资源编码为空，忽略该行
		tmd.tmdLogger();
		LinkedList<LinkedHashMap<String, Object>> condList = new LinkedList<LinkedHashMap<String,Object>>();
		Map retMap = new HashMap();
		LinkedHashMap<String,Object> rowMap = null;
		Sheet sheet = null;
		int rowNum = 0;
		ArrayList<String> noVisibleArr = new ArrayList<String>();//用户不可见
		ArrayList<String> noZwceArr = new ArrayList<String>();//数据库中不存在的职位
		ArrayList<String> noBmArr = new ArrayList<String>();//数据库中不存在的机构部门
		ArrayList<String> errorGwArr = new ArrayList<String>();//绩效考核岗位录入错误
		ArrayList<String> existUserArr = new ArrayList<String>();//已存在用户
//		int colNum = 5;
		//清空数据，实际录入时打开如下：
//		iu.putCondition(condition, "userid", "admin");
//		sqlStr = "delete t_ntmisc_user where userid != ?";
//		this.update(condition, daoParent, sqlStr, retMap);
		
		File file = new File(path);
		try {
			Workbook workBook = Workbook.getWorkbook(file);
			sheet = workBook.getSheet(0);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		rowNum = sheet.getRows();
		System.out.println("**************rowNum:"+rowNum);
//		colNum = sheet.getColumns();
		//绩效考核岗位查询
		//1、查询目前已有的职务层级
		JSONArray jbJa = new JSONArray();//所有职务层级
		/*
		iu.rmCondition(condition);
		sqlStr = "select jb_id,jb_name from t_ntmisc_job t ";
		this.queryAuto(jbJa, condition, sqlStr, daoParent, 1);
		*/
		this.jbQuery(jbJa);
		logger.info("jbJa:"+jbJa.toJSONString());
		//2、查询所有部门
		JSONArray depJa = new JSONArray();//所有部门
		iu.rmCondition(condition);
		sqlStr = "select depid,depname,orgid from t_ntmisc_dept";
		this.queryAuto(depJa, condition, sqlStr, daoParent, 1);
		JSONArray vbUserJa = new JSONArray();//当前登录用户可见用户
//		this.visibleUser(vbUserJa, tmd);
		//初始化员工
		JSONArray userJa = new JSONArray();//excel合法用户
		JSONArray userInvalidJa = new JSONArray();//excel不合法用户
		System.out.println("正在验证excel用户数据....");
		StringBuffer errorBf = new StringBuffer();//用户导入异常信息（前端显示）
//		JSONArray noVisibleJa = new JSONArray();//不可见用户
		JSONArray vbOrgJa = (JSONArray) tmd.get("vbOrgJa");//所有可见机构
//		this.visibleOrg(vbOrgJa);
		boolean validFlag = true;//true:合法  false：非法
		
		if(this.isDup(sheet)){
			validFlag = false;
			iu.infoPrgsIntrpt(tmd, "处理失败，存在重复人力资源编码");
			return;
		}
		
		if(this.existUserQuery(existUserArr, sheet)){
			validFlag = false;
			iu.infoPrgsIntrpt(tmd, existUserArr.get(0));
			return;
		}
		logger.info("existUserArr:"+existUserArr);
		for (int i = 3; i < rowNum; i++) {
			System.out.println("**********:"+i+"**********rowNum:"+rowNum);
//			float iTemp = i;
//			float rowNumTemp = rowNum;
			//设置当前进度
			iu.setProgress(tmd, "11", "正在验证第【"+(i-2)+"】行(共"+(rowNum-3)+"行)", "0.3");
			if("".equals(sheet.getCell(0, i).getContents())){//员工id为空
				iu.setProgress(tmd, "11", "忽略第【"+(i-2)+"】行,该行人力资源编号为空", "0.3");
				rowIngore++;
				continue;
			}
			JSONObject rowJson = new JSONObject();
//					String type = sheet.getCell(3, i).getContents();
			String userId = sheet.getCell(0, i).getContents().trim();//人力资源编码
			String depName = sheet.getCell(1, i).getContents().trim();//部门名称
			String name = sheet.getCell(2, i).getContents().trim();//姓名
//			String jbName = sheet.getCell(4, i).getContents().trim();//岗位
//			String xlName = sheet.getCell(5, i).getContents().trim()+"序列";//岗位序列
			String jbjx_pname =  sheet.getCell(4, i).getContents().trim();//绩效考核大类
			String jbjx_name =  sheet.getCell(5, i).getContents().trim();//绩效考核小类
			JSONArray jobJxJa = new JSONArray();
			this.jobJxQuery(jobJxJa, jbjx_name, jbjx_pname);
			if(jobJxJa.size() > 0){
				JSONObject jobJxJson = jobJxJa.getJSONObject(0);
				rowJson.put("jbjx_id", jobJxJson.getString("jbjx_id"));
			}else{
				validFlag = false;//非法
				errorGwArr.add(userId+"|"+depName+"|"+name);
				iu.infoPrgsIntrpt(tmd, userId+"|"+depName+"|"+name+"【岗位填写错误】");
//				return;
				//iu.infoPrgsIntrpt(tmd, "绩效考核岗位录入错误");
			}
			rowJson.put("userid", userId);
			rowJson.put("depname", depName);
			rowJson.put("name", name);
//			rowJson.put("jbname", jbName);
//			rowJson.put("xlname", xlName);//所属岗位序列
			
//			机构可见性查询
			boolean isVisible = false;//false:当前用户不可见,true:可见
			for (int j = 0; j < vbOrgJa.size(); j++) {
				JSONObject tempOrgJson = vbOrgJa.getJSONObject(j);
				String tempOrgName = tempOrgJson.getString("menuname");
				if(depName.equals(tempOrgName)){
					isVisible = true;
				}
			}
			logger.info("isVisible:"+isVisible);
			if(!isVisible){
				validFlag = false;//非法
				JSONObject jsonTmp = new JSONObject();
				jsonTmp.put("userid", userId);
				jsonTmp.put("depname", depName);
				jsonTmp.put("name", name);
				noVisibleArr.add(userId+"|"+depName+"|"+name);
				iu.infoPrgsIntrpt(tmd, userId+"|"+depName+"|"+name+"【机构不可达】");
				return;
			}
//		        验证部门名称是否存在
			boolean bmExist = false;
			JSONObject depJson = null;
			for (int j = 0; j < depJa.size(); j++) {
				depJson = depJa.getJSONObject(j);
				if(depName.equals(depJson.getString("depname"))){
					bmExist = true;
					break;
				}
			}
			if(bmExist){//部门存在
				rowJson.put("orgid",depJson.getString("orgid"));
				rowJson.put("depid",depJson.getString("depid"));
			}else{
				iu.infoPrgsIntrpt(tmd, userId+"|"+depName+"|"+name+"【部门不存在】");
			}
			
			if(validFlag){
				userJa.add(rowJson);
			}else{
				userInvalidJa.add(rowJson);
			}
		}	
		//批量插入用户
		if(!validFlag)
			return;
		for (int i = 0; i < userJa.size(); i++) {
			JSONObject userJson = userJa.getJSONObject(i);
			rowMap = new LinkedHashMap<String, Object>();
			rowMap.put("orgid", userJson.getString("orgid"));
			rowMap.put("depid", userJson.getString("depid"));
			rowMap.put("userid", userJson.getString("userid"));
			rowMap.put("name", userJson.getString("name"));
//			rowMap.put("jbid", userJson.getString("jbid"));
//			rowMap.put("jbcat", userJson.getString("jbcat"));
			rowMap.put("jbjx_id", userJson.getString("jbjx_id"));
			rowMap.put("property", "0");
			condList.add(rowMap);
		}
		System.out.println("插入总用户数："+userJa.size());
//				logger.info("准备插入用户："+condList.toString());
//				实际录入是打开如下
		sqlStr = "insert into t_ntmisc_user(orgid,depid,userid,name,jbjx_id,property) values(?,?,?,?,?,?)";
		this.updateBat(condList, daoParent, retMap, sqlStr, 1);
		int totalNum = rowNum-3-rowIngore;
		if(retMap.get("errorMsg").equals("操作成功")){
			iu.setProgress(tmd, "10", "用户数据导入完成,共导入:"+totalNum+"条记录", "1");
			tmd.put("finish", "1");
		}
		
		/*
		//修改admin用户机构部门为：重庆分行
		iu.rmCondition(condition);
		sqlStr = " insert into t_ntmisc_user(userid,name,orgid,depid) values('admin','admin','0310000000','0310000000') ";
		iu.infoDbOper("添加admin用户机构部门为：重庆分行", sqlStr, condition);
		this.update(condition, daoParent, sqlStr, retMap);
		
		//设置admin角色01
		sqlStr = " insert into t_ntmisc_userrole(user_id,role_id) values('admin','01')";
		iu.infoDbOper("设置admin角色01", sqlStr, condition);
		this.update(condition, daoParent, sqlStr, retMap);

		int totalNum = rowNum-2;
		int successNUm = userJa.size();
		iu.setProgress(tmd, "11", "用户数据导入失败信息:<br/>"+errorBf.toString(), "0.9");
		iu.setProgress(tmd, "10", "用户数据导入完成,共:"+totalNum+",成功:"+successNUm, "1");

		*/
	}
	
	public void actionToFileUpload(FileUploadEvent e) {
		HashMap retMap = new HashMap();
		tmd = e.getTmd();
		if(txCode.equals(tmd.get("tx_code"))){
			ArrayList<String> typeList = new ArrayList<String>();
			tmd.put("errorMsg", "上传成功,正在解析文件");
			try {
				String path = tmd.get("path")+"\\"+tmd.get("fileName");
				tmd.put("errorMsg", "解析成功,准备批量导入");
				this.dataInit(path, tmd);
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
