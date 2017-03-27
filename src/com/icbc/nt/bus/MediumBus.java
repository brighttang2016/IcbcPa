/**
 * 各个业务处理类相关联的媒介类 2015-10-09 brighttang
 */
package com.icbc.nt.bus;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.nt.scheduler.BatCalcScheduler;
import com.icbc.nt.util.TransactionMapData;

@Service
public class MediumBus extends BusParent{
	@Autowired
	private NaviBus naviBus;
	@Autowired
	private OrgBus orgBus;
	@Autowired
	private UserBus userBus;
	@Autowired 
	private BatBus batBus;
	@Autowired
	private BatCalcScheduler batCalcScheduler;
	@Autowired
	private FileUploadBus fileUploadBus;
	
	/**
	 * 获取所有机构部门信息
	 * @param ja
	 * @param json
	 */
	public void getSubItem(JSONArray ja,JSONObject json){
		orgBus.getSubItem(ja, json);
	}
	
	/**
	 * 用户基本信息查询
	 * @param ja
	 * @param tmd
	 */
	public void userInfoQuery(JSONArray ja,TransactionMapData tmd){
//		userBus.userInfoQuery(ja, tmd);
		userBus.userInfoQuery(ja);
	}
	/**
	 * 文件下载
	 * @param tmd
	 */
	public void fileDownLoad(TransactionMapData tmd){
		fileUploadBus.fileDownLoad(tmd);
	}
	
	/**
	 * 设置机构查询条件
	 * @param sqlStr
	 * @param tmd
	 * @param condition
	 * @return 拼接结构、部门查询条件后的sql
	 */
	public String putOrgCond(String sqlStr,TransactionMapData tmd,HashMap<String, Object> condition){
		String orgIdQuery = "";
		String depIdQuery = "";
		logger.info("设置机构查询条件");
		logger.info("tmd:"+tmd);
		tmd.tmdLogger();
		try {
			orgIdQuery = tmd.get("orgId").toString();
		} catch (Exception e) {}
		try {
			depIdQuery = tmd.get("deptId").toString();
		} catch (Exception e) {}
		if(!"".equals(orgIdQuery) && !"".equals(orgIdQuery)){
			if(orgIdQuery.equals(depIdQuery)){//机构号和部门编号相同，查询选择机构及下属所有机构部门
				sqlStr = this.getSqlWithIn(sqlStr,orgIdQuery);//机构部门相同，查询机构及下属所有机构、部门员工
			}else{//机构号和部门号不同，查询对应的机构部门
				/*iu.putCondition(condition, "orgid", orgIdQuery);//机构号
				iu.putCondition(condition, "depid", depIdQuery);//部门编号
				 */		
				
				sqlStr = this.getSqlWithIn(sqlStr,depIdQuery);//机构部门不同，查询部门及下属所有部门员工（2016-01-26:按照上两行写法，部门存在子部门的情况，无法查询子部门员工信息）
			}
		}
		return sqlStr;
	}
	
	/**
	 * 获取当前机构及下属所有机构
	 * @param orgId
	 * @return 字符串格式"(机构号1，机构号2，机构号3，机构号4，机构号5)"
	 */
	public String getOrgIn(String orgId) {
		return orgBus.getOrgIn(orgId);
	}

	/**
	 * 用户资格查询（供职务层级晋升使用用）
	 * @param userId
	 * @param ja
	 */
	public void userCtQuery(String userId,JSONArray ja){
		userBus.userCtQuery(userId, ja);
	}
	
	/**
	 * 获取协议职务层级
	 * @param jbIdNow
	 */
	public void getNextJob(String jbIdNow,JSONArray ja){
		userBus.getNextJob(jbIdNow,ja);
	}
	
	/**
	 * 获取岗位所在的岗位类别
	 * @param jbId 岗位id
	 * @param jobArray 查找到的岗位id所在岗位类别数组（长度为1）
	 */
	public void getJobCat(String jbId,JSONArray jobArray){
		userBus.getJobCat(jbId, jobArray);
	}
	
	/**
	 * 职务层级自动晋升批量计算
	 */
	public void jobBat(){
		batCalcScheduler.jobBat();
	}
	
	/**
	 * 工资等级档次批量计算
	 */
	public void wglBat(){
		batCalcScheduler.wglBat();
	}
	/**
	 * 批量计算积分
	 */
	public void pointBat(){
		batCalcScheduler.pointBat();
	}
	
	/**
	 * 用户资格认证信息批量插入
	 * @param retMap
	 * @param data
	 */
	public void uctBat(Map retMap,String data){
		batBus.uctBat(retMap, data);
	}
	
	/**
	 * 奖励信息批量录入
	 * @param retMap
	 * @param data
	 */
	public void rpBat(Map retMap,String data){
		batBus.rpBat(retMap, data);
	}
	/**
	 * sql语句限定，限定当前机构及下属所有机构
	 * @param sql 基础sql
	 * @param orgId 当前层级机构号
	 * @return
	 */
	public String getSqlWithIn(String sql,String orgId){
//		logger.info("getSqlWithIn orgId:"+orgId);
//		String orgId = this.getUserOrg(userId);
		logger.info("getSqlWithIn:"+orgId);
		String sqlStr = "";
		JSONArray jaOrgInfo = new JSONArray();
		StringBuffer orgIdSb = new StringBuffer();//当前机构及下属所有机构号，用","分割
		orgIdSb.append(orgId);
//		this.getSubOrg(jaOrgInfo, orgId);//2016-01-26
		JSONObject json = new JSONObject();
		json.put("menuid", orgId);
		this.getSubItem(jaOrgInfo, json);
//		logger.info("jaOrgInfo.size():"+jaOrgInfo.size());
		for (int i = 0; i < jaOrgInfo.size(); i++) {
			JSONObject orgJson = jaOrgInfo.getJSONObject(i);
			orgIdSb.append(","+orgJson.get("menuid"));
		}
		sqlStr = "select * from("+sql+") t where t.orgid in("+orgIdSb.toString()+")"
				+ " or t.depid in("+orgIdSb.toString()+")";
		logger.info("getSqlWithIn sqlStr:"+sqlStr);
		return sqlStr;
	}
	
	/**
	 * 导航初始化（刷新）
	 */
	public void naviRefresh(){
		naviBus.initStart();
//		naviInit.initStart();
	}
	
	/**
	 * 获取当前机构的所有子机构(通过menuid获取机构号)
	 * @param ja
	 * @param orgId
	 */
	public void getSubOrg(JSONArray ja,String orgId){
		orgBus.getSubOrg(ja, orgId);
	}
	
	public String  getUserOrg(String userId){
		return userBus.getUserOrg(userId);
	}
}
