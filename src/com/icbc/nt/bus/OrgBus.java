package com.icbc.nt.bus;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.nt.bus.dispatcher.BusDispatcherImpl;
import com.icbc.nt.util.TransactionMapData;
@Service
@Transactional
public class OrgBus extends BusParent{
	ResultSet rs = null;
	ResultSetMetaData rsmd = null;
	@Autowired
	private UserBus userBus;
	@Autowired
	private BusDispatcherImpl busDispatcherImpl;
	
	/**
	 * 获取当前机构及下属所有机构
	 * @param orgId
	 * @return 字符串格式"(机构号1，机构号2，机构号3，机构号4，机构号5)"
	 */
	public String getOrgIn(String orgId) {
		JSONArray subOrgJa = new JSONArray();
		this.getSubOrg(subOrgJa, orgId);
		StringBuffer orgInSb = new StringBuffer();
		orgInSb.append("(");
		orgInSb.append(orgId);
		for (int i = 0; i < subOrgJa.size(); i++) {
			JSONObject orgJson = subOrgJa.getJSONObject(i);
			orgInSb.append("," + orgJson.getString("menuid"));
		}
		orgInSb.append(")");
//		this.logger.info("orgInSb:" + orgInSb.toString());
		return orgInSb.toString();
	}
	
	/**
	 * 获取所有子部门信息
	 * @param ja
	 * @param json
	 */
	public void getSubDept(JSONArray ja,String orgId){
		JSONArray jaTemp = new JSONArray();
		iu.rmCondition(condition);
		iu.putCondition(condition, "porgid", orgId);
		sql = "select * from t_ntmisc_dept t where t.parentdepid = ?";
		this.queryManu(jaTemp, condition, sql, daoParent, 1);
		for (int i = 0; i < jaTemp.size(); i++) {
			JSONObject jsonTemp = jaTemp.getJSONObject(i);
			ja.add(jsonTemp);
			if(this.hasSubItem(jsonTemp)){
				this.getSubDept(ja, jsonTemp.getString("depid"));
			}
		}
	}
	
	/**
	 * 获取所有子机构信息
	 * @param ja
	 * @param json
	 */
	public void getSubOrg(JSONArray ja,String orgId){
		try {
			if(orgId.equals("")){
				logger.info("获取所有子机构信息，当前机构为空,无法获取子机构");
				return;
			}
		} catch (Exception e) {
			logger.info("获取所有子机构信息，机构号错误，抛出异常,无法获取子机构");
			return;
			// TODO: handle exception
		}
		JSONArray jaTemp = new JSONArray();
		iu.rmCondition(condition);
		iu.putCondition(condition, "porgid", orgId);
		sql = "select orgid menuid from t_ntmisc_org where porgid=?";
//		logger.info("执行sql,子机构查询,sql:"+sql+"----condition:"+condition);
//		logger.info("11111jaTemp:"+jaTemp.toJSONString());
//		iu.infoDbOper("子机构查询", sql, condition);
		this.queryManu(jaTemp, condition, sql, daoParent, 1);
//		logger.info("子机构查询结果:"+jaTemp.toJSONString());
		for (int i = 0; i < jaTemp.size(); i++) {
			JSONObject jsonTemp = jaTemp.getJSONObject(i);
			ja.add(jsonTemp);
//			logger.info("jsonTemp:"+jsonTemp);
			if(this.hasSubItem(jsonTemp)){
				this.getSubOrg(ja, jsonTemp.getString("menuid"));
			}
		}
	}
	
	/**
	 * 获取所有机构部门信息
	 * @param ja
	 * @param json
	 */
	public void getSubItem(JSONArray ja,JSONObject json){
//		logger.info("getSubItem");
		JSONArray jaTemp = new JSONArray();
		String menuId = json.getString("menuid");
		iu.rmCondition(condition);
		iu.putCondition(condition, "pDepId", menuId);
		sql = "select depid menuid,depname menuname from t_ntmisc_dept t where t.parentdepid = ? order by menuname";
		this.queryManu(jaTemp, condition, sql, daoParent, 1);
		
		sql = "select orgid menuid,orgname menuname from t_ntmisc_org t where t.porgid=? order by menuname";
		this.queryManu(jaTemp, condition, sql, daoParent, 1);
		for (int i = 0; i < jaTemp.size(); i++) {
			JSONObject jsonTemp = jaTemp.getJSONObject(i);
			ja.add(jsonTemp);
			if(this.hasSubItem(jsonTemp)){
				this.getSubItem(ja, jsonTemp);
			}
		}
	}

	
	/**
	 * @param json 当前节点数据
	 */
	public boolean hasSubItem(JSONObject json){
//		logger.info("hasSubItem");
		boolean boolRet = false;
		JSONArray jaTemp = new JSONArray();
		JSONArray jaOrgTemp = new JSONArray();
		JSONArray jaDeptTemp = new JSONArray();
//		this.subDeptQuery(jaDeptTemp, json.getString("menuid"),json.getString("menuname"));
//		this.subOrgQuery(jaOrgTemp, json.getString("menuid"),json.getString("menuname"));
		
		String menuId = json.getString("menuid");
//		logger.info("查询子部门开始 pDepId："+menuId);
		iu.rmCondition(condition);
		iu.putCondition(condition, "pDepId", menuId);
		sql = "select depid menuid,depname menuname from t_ntmisc_dept t where t.parentdepid = ? order by menuname";
//		logger.info("查询子部门结束");
		this.queryManu(jaTemp, condition, sql, daoParent, 1);
		
//		logger.info("查询子机构开始 porgid："+menuId);
		iu.rmCondition(condition);
		iu.putCondition(condition, "porgid", menuId);
		sql = "select orgid menuid,orgname menuname from t_ntmisc_org t where t.porgid=? order by menuname";
		this.queryManu(jaTemp, condition, sql, daoParent, 1);
//		logger.info("查询子机构结束");
		
//		logger.info("jaDeptTemp.size():"+jaDeptTemp.size()+"|jaOrgTemp.size():"+jaOrgTemp.size());
		if(jaTemp.size() == 0){
			json.put("leaf", "true");
			json.put("leaftag", "1");
			boolRet = false;
		}
		else{
			json.put("leaf", "false");
			json.put("leaftag", "0");
			boolRet = true;
		}
//		logger.info("子机构子部门判断完成 json:"+json);
		return boolRet;
	}
	/**
	 * 子部门查询
	 * @param ja
	 * @param parentDepId
	 */
	public void subDeptQuery(JSONArray ja,String pDepId,String pDeptname){
//		logger.info("subDeptQuery pDepId:"+pDepId+"|pDeptname:"+pDeptname);
		JSONArray jaTemp = new JSONArray();
		iu.rmCondition(condition);
		iu.putCondition(condition, "pDepId", pDepId);
		sql = "select depid menuid,depname menuname from t_ntmisc_dept t where t.parentdepid = ? order by menuname";
		this.queryManu(jaTemp, condition, sql, daoParent, 1);
		for (int i = 0; i < jaTemp.size(); i++) {
			JSONObject json = jaTemp.getJSONObject(i);
			json.put("menutype", "2");//menutype 1：机构 2：部门
			json.put("pid", pDepId);
			json.put("pname", pDeptname);
			this.hasSubItem(json);
			ja.add(json);
		}
	}
	/**
	 * 子机构查询
	 * @param ja
	 * @param pOrgId
	 */
	public void subOrgQuery(JSONArray ja,String pOrgId,String pOrgName){
//		logger.info("subOrgQuery pOrgId:"+pOrgId+"|pOrgName:"+pOrgName);
		JSONArray jaTemp = new JSONArray();
		iu.rmCondition(condition);
		iu.putCondition(condition, "pOrgId", pOrgId);
		sql = "select orgid menuid,orgname menuname from t_ntmisc_org t where t.porgid=? order by menuname";
		this.queryManu(jaTemp, condition, sql, daoParent, 1);
		for (int i = 0; i < jaTemp.size(); i++) {
			JSONObject json = jaTemp.getJSONObject(i);
			json.put("menutype", "1");//menutype 1：机构 2：部门
			json.put("pid", pOrgId);
			json.put("pname", pOrgName);
			this.hasSubItem(json);
			ja.add(json);
		}
	}
	
	/**
	 * 业务处理路由
	 * @param ja 明细数据
	 * @param condition sql条件
	 * @param tx_code 交易码
	 * @param retMap 返回map
	 */
	
	public void orgTreeQuery(JSONArray ja,LinkedHashMap<String, Object> condition,String tx_code,Map retMap,TransactionMapData tmd){
		retMap.put("operName", "机构部门查询");
		busDispatcherImpl.orgTreeQuery(ja, condition, retMap, tmd);
	}
	
	public void orgManage(JSONArray ja,LinkedHashMap<String, Object> condition,String tx_code,Map retMap,TransactionMapData tmd){
//		logger.info("业务处理路由->当前交易码  tx_code:"+tx_code);
		String sqlStr = "";
		int updFlag = 0;
		switch(Integer.parseInt(tx_code)){
		case 10031:
			logger.info("**********机构部门添加");
			if("100".equals(tmd.get("addType"))){//机构添加
				iu.rmCondition(condition);
				iu.putConditionAll(condition, "orgid", tmd.get("menuId"));
				iu.putConditionAll(condition, "orgname", tmd.get("menuName"));
				iu.putConditionAll(condition, "porgid", tmd.get("pId"));
				sqlStr = "insert into t_ntmisc_org(orgid,orgname,porgid) values(?,?,?)";
				this.update(condition, daoParent, sqlStr,retMap);
				iu.rmCondition(condition);
				iu.putConditionAll(condition, "orgid", tmd.get("menuId"));
				iu.putConditionAll(condition, "parentdepid", "0001");
				iu.putConditionAll(condition, "depid", tmd.get("menuId"));
				iu.putConditionAll(condition, "depname", tmd.get("menuName"));
				sqlStr = "insert into t_ntmisc_dept values(?,?,?,?)";
				this.update(condition, daoParent, sqlStr,retMap);
			}else if("200".equals(tmd.get("addType"))){//部门添加
				iu.putConditionAll(condition, "orgid", tmd.get("orgId"));
				iu.putConditionAll(condition, "parentdepid", tmd.get("pId"));
				iu.putConditionAll(condition, "depid", tmd.get("menuId"));
				iu.putConditionAll(condition, "depname", tmd.get("menuName"));
				sqlStr = "insert into t_ntmisc_dept(orgid,parentdepid,depid,depname) values(?,?,?,?)";
				this.update(condition, daoParent, sqlStr,retMap);
			}
//			retMap.put("errorCode", tmd.get("errorCode"));
//			retMap.put("errorMsg", tmd.get("errorMsg"));
			break;
		case 10032:
			logger.info("**********机构部门修改");
			iu.rmCondition(condition);
			iu.putConditionAll(condition, "depname", tmd.get("menuName"));
			iu.putConditionAll(condition, "depid", tmd.get("menuId"));
			if("1".equals(tmd.get("menuType"))){//机构修改
				
//				iu.putConditionAll(condition, "orgname", tmd.get("menuName"));
//				iu.putConditionAll(condition, "orgid", tmd.get("menuId"));
				sqlStr = "update t_ntmisc_org set orgname=? where orgid=?";
				this.update(condition, daoParent, sqlStr,retMap);
			}else if("2".equals(tmd.get("menuType"))){//部门修改
//				iu.rmCondition(condition);
				sqlStr = "update t_ntmisc_dept set depname=? where depid=?";
				this.update(condition, daoParent, sqlStr,retMap);
			}
//			retMap.put("errorCode", tmd.get("errorCode"));
//			retMap.put("errorMsg", tmd.get("errorMsg"));
			break;
		case 10033:
			logger.info("**********机构部门删除");
			iu.rmCondition(condition);
			iu.putConditionAll(condition, "depname", tmd.get("menuName"));
			iu.putConditionAll(condition, "depid", tmd.get("menuId"));
			if("1".equals(tmd.get("menuType"))){//机构删除
//				iu.putConditionAll(condition, "orgname", tmd.get("menuName"));
//				iu.putConditionAll(condition, "orgid", tmd.get("menuId"));
				//首先删除部门表（依赖机构表）
				LinkedHashMap<String, Object> condMap = new LinkedHashMap<String, Object>();
				condMap.put("orgid", tmd.get("menuId"));
				condMap.put("parentdepid", "0001");
				condMap.put("depid", tmd.get("menuId"));
				sqlStr = "delete t_ntmisc_dept where orgid=? and parentdepid=? and depid=?";
				this.update(condMap, daoParent, sqlStr,retMap);
				//删除机构表
				sqlStr = "delete t_ntmisc_org where orgname=? and orgid=?";
				this.update(condition, daoParent, sqlStr,retMap);
			}else if("2".equals(tmd.get("menuType"))){//部门删除
//				iu.rmCondition(condition);
				sqlStr = "delete t_ntmisc_dept where  depname=? and depid=?";
				this.update(condition, daoParent, sqlStr,retMap);
			}
//			retMap.put("errorCode", tmd.get("errorCode"));
//			retMap.put("errorMsg", tmd.get("errorMsg"));
			break;
		
		case 10035:
			retMap.put("operName", "机构部门查询（全部）");
//			if("-1".equals(tmd.get("menuTreeId"))){//初始查询
				iu.rmCondition(condition);
				sqlStr = "select orgid menuid,orgname menuname from t_ntmisc_org t where t.porgid is null order by menuname";
				this.queryAuto(ja, condition, sqlStr, daoParent, 2);
				for (int i = 0; i < ja.size(); i++) {
					JSONObject jsonTemp = ja.getJSONObject(i);
//					this.getSubItem(ja, jsonTemp);
				}
//			}
			break;
		}
	}

	@Override
	public int getCount(HashMap<String,Object> condition,String tranCode) {
//		System.out.println("RoleBus getCount");
		String sqlStr = "select * from t_ntmisc_role";
//		return this.countExt(condition, roleDao,sqlStr);
		return this.count(condition, daoParent, sqlStr, 1);
	}
}
