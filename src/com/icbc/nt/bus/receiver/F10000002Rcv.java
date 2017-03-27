/**
 * 用户数据初始化 2016-01-26
 */
package com.icbc.nt.bus.receiver;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.message.push.MessagePushHelper;
import com.icbc.message.push.PushThread;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.util.TransactionMapData;

public class F10000002Rcv extends BusParent implements BusReceiver {

	@Override
	public void doWork(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub
	}
	
	public void jbQuery(JSONArray jbJa){
		sqlStr = " select a.jb_id,a.jb_name,a.jb_idp,b.jb_name jb_pname from t_ntmisc_job a,t_ntmisc_job b "
				+ " where a.jb_idp = b.jb_id ";
		iu.rmCondition(condition);
		logger.info("职务层级及岗位序列查询");
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
	
	public void dataInit(String path,TransactionMapData tmd){
		LinkedList<LinkedHashMap<String, Object>> condList = new LinkedList<LinkedHashMap<String,Object>>();
		Map retMap = new HashMap();
		LinkedHashMap<String,Object> rowMap = null;
		Sheet sheet = null;
		int rowNum = 0;
		ArrayList<String> noZwceArr = new ArrayList<String>();//数据库中不存在的职位
		ArrayList<String> noBmArr = new ArrayList<String>();//数据库中不存在的机构部门
//		int colNum = 5;
		//清空数据，实际录入时打开如下：
		iu.putCondition(condition, "userid", "admin");
		sqlStr = "delete t_ntmisc_user where userid != ?";
		this.update(condition, daoParent, sqlStr, retMap);
		
		File file = new File(path);
		try {
			Workbook workBook = Workbook.getWorkbook(file);
			sheet = workBook.getSheet(0);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		rowNum = sheet.getRows();
//		colNum = sheet.getColumns();
		
		//1、查询目前已有的职务层级
		JSONArray jbJa = new JSONArray();//所有职务层级
		/*
		iu.rmCondition(condition);
		sqlStr = "select jb_id,jb_name from t_ntmisc_job t ";
		this.queryAuto(jbJa, condition, sqlStr, daoParent, 1);
		*/
		this.jbQuery(jbJa);
		logger.debug("jbJa:"+jbJa.toJSONString());
		//2、查询所有部门
		JSONArray depJa = new JSONArray();//所有部门
		iu.rmCondition(condition);
		sqlStr = "select depid,depname,orgid from t_ntmisc_dept";
		this.queryAuto(depJa, condition, sqlStr, daoParent, 1);
		
		
		//初始化员工
		JSONArray userJa = new JSONArray();
		JSONArray userInvalidJa = new JSONArray();
		System.out.println("正在验证excel用户数据....");
		
		for (int i = 2; i < rowNum; i++) {
			float iTemp = i;
			float rowNumTemp = rowNum;
//			System.out.println("正在验证第【"+i+"】行(共"+rowNum+"行)");
			//设置当前进度
			tmd.put("errorCode", "11");
			tmd.put("errorMsg", "正在验证第【"+i+"】行(共"+rowNum+"行)");
			tmd.put("progress", iTemp/rowNumTemp);
			
			boolean validFlag = true;//true:非法  false：非法
			JSONObject rowJson = new JSONObject();
			String type = sheet.getCell(3, i).getContents();
			String userId = sheet.getCell(0, i).getContents().trim();
			String depName = sheet.getCell(1, i).getContents().trim();
			String name = sheet.getCell(2, i).getContents().trim();
			String jbName = sheet.getCell(4, i).getContents().trim();
			String xlName = sheet.getCell(5, i).getContents().trim()+"序列";
			rowJson.put("userid", userId);
			rowJson.put("depname", depName);
			rowJson.put("name", name);
			rowJson.put("jbname", jbName);
			rowJson.put("xlname", xlName);//所属岗位序列
//			验证职务层级是否存在
			boolean zwcjExist = false;
			JSONObject jbJson = null;
			for (int j = 0; j < jbJa.size(); j++) {
				jbJson = jbJa.getJSONObject(j);
				if(jbName.equals(jbJson.getString("jb_name")) && xlName.equals(jbJson.getString("jb_pname"))){
					zwcjExist = true;
					break;
				}
			}
//			logger.info("userId:"+userId+"|jbName:"+jbName+"|xlName:"+xlName+"|zwcjExist:"+zwcjExist);
			if(zwcjExist){//岗位序列、职务层级存在
				rowJson.put("jbid", jbJson.getString("jb_id"));
				String jbCat = getJbCat(jbJson.getString("jb_id"));
//				System.out.println("找到岗位类别:"+jbCat);
//				logger.info("用户职务层级："+jbJson.getString("jb_id")+"|找到职务类别："+jbCat);
				rowJson.put("jbcat",jbCat);
			}else{
				validFlag = false;
//				System.out.println("当前循环用户"+rowJson.getString("userid")+"职务层级："+rowJson.getString("jbname")+"在数据库表t_ntmisc_job中不存在");
//				logger.info("当前循环用户职务层级："+rowJson.getString("jbname")+"在数据库表t_ntmisc_job中不存在");
				boolean b1 = false;
				for (int j = 0; j < noZwceArr.size(); j++) {
					if(noZwceArr.get(j).equals(jbName+"|"+xlName)){
						b1 = true;
					}
				}
				if(!b1)
					noZwceArr.add(jbName+"|"+xlName);
			}
			
//			验证部门名称是否存在
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
				validFlag = false;
//				System.out.println("当前循环用户部门名称："+rowJson.getString("depname")+" 在数据库表t_ntmisc_dept中不存在");
				
				boolean b1 = false;
				for (int j = 0; j < noBmArr.size(); j++) {
					if(noBmArr.get(j).equals(rowJson.getString("depname"))){
						b1 = true;
					}
				}
				if(!b1)
					noBmArr.add(rowJson.getString("depname"));
			}
			
			if(validFlag){
				userJa.add(rowJson);
			}else{
				userInvalidJa.add(rowJson);
			}
		}
//		logger.info("验证未通过用户："+userInvalidJa.toJSONString());
		StringBuffer errorBf = new StringBuffer();//用户导入异常信息（前端显示）
		//打印不存在内容
		for (int j = 0; j < noZwceArr.size(); j++) {
			logger.error("职务层级："+noZwceArr.get(j)+"【用户导入失败】");
			errorBf.append("职务层级："+noZwceArr.get(j)+"【用户导入失败】<br/>");
		}
		for (int j = 0; j < noBmArr.size(); j++) {
			logger.error("机构部门："+noBmArr.get(j)+"【用户导入失败】");
			errorBf.append("机构部门："+noBmArr.get(j)+"【用户导入失败】<br/>");
		}
		//批量插入用户
		for (int i = 0; i < userJa.size(); i++) {
			JSONObject userJson = userJa.getJSONObject(i);
			rowMap = new LinkedHashMap<String, Object>();
			rowMap.put("orgid", userJson.getString("orgid"));
			rowMap.put("depid", userJson.getString("depid"));
			rowMap.put("userid", userJson.getString("userid"));
			rowMap.put("name", userJson.getString("name"));
			rowMap.put("jbid", userJson.getString("jbid"));
			rowMap.put("jbcat", userJson.getString("jbcat"));
			rowMap.put("property", "0");
			condList.add(rowMap);
		}
		
//		System.out.println(condList.toString());
		System.out.println("插入总用户数："+userJa.size());
//		logger.info("准备插入用户："+condList.toString());

//		实际录入是打开如下
		sqlStr = "insert into t_ntmisc_user(orgid,depid,userid,name,jb_id,jb_cat,property) values(?,?,?,?,?,?,?)";
		logger.info("新增用户");
		iu.infoDbOper("新增用户", sqlStr, condList);
		this.updateBat(condList, daoParent, retMap, sqlStr, 1);
		

		//修改admin用户机构部门为：重庆分行
		iu.rmCondition(condition);
//				sqlStr = "update t_ntmisc_user set orgid='0310000000' ,depid='0310000000' where userid='admin'";
		sqlStr = " insert into t_ntmisc_user(userid,name,orgid,depid) values('admin','admin','0310000000','0310000000') ";
		logger.info("添加admin用户机构部门为：重庆分行");
		iu.infoDbOper("添加admin用户机构部门为：重庆分行", sqlStr, condition);
		this.update(condition, daoParent, sqlStr, retMap);
		
		//设置admin角色01
		sqlStr = " insert into t_ntmisc_userrole(user_id,role_id) values('admin','01')";
		logger.info("设置admin角色01");
		iu.infoDbOper("设置admin角色01", sqlStr, condition);
		this.update(condition, daoParent, sqlStr, retMap);

		int totalNum = rowNum-2;
		int successNUm = userJa.size();
		iu.setProgress(tmd, "11", "用户数据导入失败信息:<br/>"+errorBf.toString(), "0.9");
		iu.setProgress(tmd, "10", "用户数据导入完成,共:"+totalNum+",成功:"+successNUm, "1");

	}
	
	public void doWork(TransactionMapData tmd) {
		iu.setProgress(tmd, "11", "准备导入用户数据", "0.1");
		String path = tmd.get("path")+"\\"+tmd.get("fileName");
		logger.debug("用户初始化文件绝对路径path:"+path);
		this.dataInit(path,tmd);
	}
}
