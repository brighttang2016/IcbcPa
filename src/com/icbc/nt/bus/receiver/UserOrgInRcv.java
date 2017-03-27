/**
 * 根据用户机构号获取用户所见所有机构号 2015-12-06
 */
package com.icbc.nt.bus.receiver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.bus.MediumBus;
import com.icbc.nt.bus.dispatcher.BusDispatcherImpl;
import com.icbc.nt.util.TransactionMapData;

public class UserOrgInRcv extends BusParent implements BusReceiver {

	@Autowired
	private BusDispatcherImpl busDispatcherImpl;
	@Autowired
	private MediumBus mediumBus;
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
	 * 获取所有子机构信息
	 * @param ja
	 * @param json
	 */
	public void getSubOrg(JSONArray ja,String orgId){
		JSONArray jaTemp = new JSONArray();
		iu.rmCondition(condition);
		iu.putCondition(condition, "porgid", orgId);
		sql = "select orgid menuid from t_ntmisc_org where porgid=?";
		this.queryManu(jaTemp, condition, sql, daoParent, 1);
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
	 * 获取当前机构及下属所有机构
	 * @param orgId
	 * @return 字符串格式"(机构号1，机构号2，机构号3，机构号4，机构号5)"
	 */
	public String getOrgIn(String orgId) {
		logger.info("获取机构的下属所有机构，机构："+orgId);
		JSONArray subOrgJa = new JSONArray();
		this.getSubOrg(subOrgJa, orgId);
		StringBuffer orgInSb = new StringBuffer();
		orgInSb.append("(");
		orgInSb.append("'"+orgId+"'");
		for (int i = 0; i < subOrgJa.size(); i++) {
			JSONObject orgJson = subOrgJa.getJSONObject(i);
			orgInSb.append(",'" + orgJson.getString("menuid")+"'");
		}
		orgInSb.append(")");
//		this.logger.info("orgInSb:" + orgInSb.toString());
		logger.info("获取当前机构及下属所有机构,获取结果："+orgInSb.toString());
		return orgInSb.toString();
	}
	
	/**
	 * 是否具有分支行管理员角色（角色编号21）
	 * @param tmd
	 * @return
	 */
	public boolean isFzhRoleExist(TransactionMapData tmd){
		JSONArray ja = new JSONArray();
		boolean isExist = false;
		String currUser = tmd.get("currUser").toString();//当前登录用户
		iu.rmCondition(condition);
		sqlStr = " select * from t_ntmisc_userrole a where a.role_id='21' ";
		this.queryAuto(ja, condition, sqlStr, daoParent, 1);
		logger.info("是否具有分支行管理员权限查询结果："+ja.toJSONString());
		for (int i = 0; i < ja.size(); i++) {
			JSONObject json = ja.getJSONObject(i);
			if(json.getString("user_id").equals(currUser)){
				isExist = true;
			}
		}
		logger.info("currUser|isExist:"+currUser+"|"+isExist);
		return isExist;
	}
	
	@Override
	public void doWork(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
//		String orgId = tmd.get("orgIdCurr").toString();
//		tmd.put("orgIn", this.getOrgIn(orgId));
	}
	
	public void doWork(TransactionMapData tmd) {
		String orgId = "";//基础机构节点
		String orgIdCurr = tmd.get("orgIdCurr").toString();
//		String orgIdCurr = tmd.get("orgIdLogin").toString();
		orgId = orgIdCurr;
		/*if(this.isFzhRoleExist(tmd)){
			orgId = busDispatcherImpl.brOrgQuery(orgIdCurr);//分支行号
		}else{
			orgId = orgIdCurr;
		}*/
		tmd.put("orgIn", this.getOrgIn(orgId));
	}
}
