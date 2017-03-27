/**
 * 机构树查询接收者 2015-12-04
 */

package com.icbc.nt.bus.receiver;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.bus.dispatcher.BusDispatcherImpl;
import com.icbc.nt.util.TransactionMapData;
@Service
public class OrgTreeQueryRcv extends BusParent implements BusReceiver {
	@Autowired
	private BusDispatcherImpl busDispatcherImpl;
	
	/**
	 * 子部门查询
	 * @param ja
	 * @param parentDepId
	 */
	public void subDeptQuery(JSONArray ja,String pDepId,String pDeptname){
		logger.debug("subDeptQuery pDepId:"+pDepId+"|pDeptname:"+pDeptname);
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
	 * @param json 当前节点数据
	 */
	public boolean hasSubItem(JSONObject json){
		logger.debug("hasSubItem");
		boolean boolRet = false;
		JSONArray jaTemp = new JSONArray();
		JSONArray jaOrgTemp = new JSONArray();
		JSONArray jaDeptTemp = new JSONArray();
//		this.subDeptQuery(jaDeptTemp, json.getString("menuid"),json.getString("menuname"));
//		this.subOrgQuery(jaOrgTemp, json.getString("menuid"),json.getString("menuname"));
		String menuId = json.getString("menuid");
		logger.debug("查询子部门开始 pDepId："+menuId);
		iu.rmCondition(condition);
		iu.putCondition(condition, "pDepId", menuId);
		sql = "select depid menuid,depname menuname from t_ntmisc_dept t where t.parentdepid = ? order by menuname";
		logger.debug("查询子部门结束");
		this.queryManu(jaTemp, condition, sql, daoParent, 1);
		
		logger.debug("查询子机构开始 porgid："+menuId);
		iu.rmCondition(condition);
		iu.putCondition(condition, "porgid", menuId);
		sql = "select orgid menuid,orgname menuname from t_ntmisc_org t where t.porgid=? order by menuname";
		this.queryManu(jaTemp, condition, sql, daoParent, 1);
		logger.debug("查询子机构结束");
		
		logger.debug("jaDeptTemp.size():"+jaDeptTemp.size()+"|jaOrgTemp.size():"+jaOrgTemp.size());
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
//		logger.debug("子机构子部门判断完成 json:"+json);
		return boolRet;
	}
	
	/**
	 * 子机构查询
	 * @param ja
	 * @param pOrgId
	 */
	public void subOrgQuery(JSONArray ja,String pOrgId,String pOrgName){
		logger.debug("subOrgQuery pOrgId:"+pOrgId+"|pOrgName:"+pOrgName);
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
	 * 当前用户所见最顶层机构
	 * 重庆分行管理员、系统初始化：重庆分行及下属所有机构部门
	 * 分支行管理员：用户所在分支行及下属所有机构部门
	 * 网点管理员：所在网点机构部门
	 * 其他：所在网点机构部门
	 * @return 最顶层机构
	 */
	public String getTopOrg(){
		logger.info("getTopOrg:"+tmd.get("brOrgId").toString());
		String topOrg = "";//当前用户所有角色中，所见最高一级机构
		//查询用户已有角色
		JSONArray userRoleJa = new JSONArray();//用户角色
		HttpSession session = (HttpSession) tmd.get("session");
		/*if(session.getAttribute("userRoleJa") == null){
			
			session.setAttribute("userRoleJa", userRoleJa);
		}else{
			userRoleJa = (JSONArray) session.getAttribute("userRoleJa");
		}*/
		
		iu.rmCondition(condition);
		iu.putCondition(condition, "userId", tmd.get("userIdLogin"));
		sqlStr = "select t.user_id,t.role_id from t_ntmisc_userrole t where t.user_id=?";
		logger.info("当前用户所见最顶层机构----》用户角色查询");
		iu.infoDbOper("当前用户所见最顶层机构----》用户角色查询：", sqlStr, condition);
		
		this.queryManu(userRoleJa, condition, sqlStr,daoParent, 1);
		
		boolean role_xtcsh = false;
		boolean role_fh = false;
		boolean role_fzh = false;
		boolean role_wd = false;
		for (int i = 0; i < userRoleJa.size(); i++) {
			JSONObject json = userRoleJa.getJSONObject(i);
			switch(Integer.parseInt(json.getString("role_id"))){
			case 1://系统初始化
				role_xtcsh = true;
				break;
			case 2://重庆分行管理员
				role_fh = true;
				break;
			case 3://分支行管理员
				role_fzh = true;
				break;
			case 4://网点管理员
				role_wd = true;
				break;
			default:
				role_wd = true;
				break;
			}
		}
		tmd.tmdLogger();
		if(role_xtcsh || role_fh){
			topOrg = "0310000000";
		}else if(role_fzh){
//			topOrg = busDispatcherImpl.brOrgQuery(tmd.get("orgIdLogin").toString());
			topOrg = tmd.get("brOrgId").toString();
		}else if(role_wd){
			topOrg = tmd.get("orgIdLogin").toString();
		}/*else{
			topOrg = "0310000000";//默认到分行根目录
		}*/
		logger.info("222222222222222:"+tmd.get("brOrgId").toString());
		logger.info(role_xtcsh+"|"+role_fh+"|"+role_fzh+"|"+role_wd);
		logger.info("当前用户所见最顶层机构topOrg："+topOrg);
		return topOrg;
	}
	
	@Override
	public void doWork(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		logger.info("333333333333doWork:"+tmd.get("brOrgId").toString());
		if("root".equals(tmd.get("menuTreeId"))){//初始查询
//			String orgId = busDispatcherImpl.userOrgQuery(tmd);
			String orgId = this.getTopOrg();
			ja.clear();
			iu.rmCondition(condition);
			iu.putCondition(condition, "orgId", orgId);
			sqlStr = "select orgid menuid,orgname menuname from t_ntmisc_org t where t.orgid = ? ";
			iu.infoDbOper("机构树查询", sqlStr, condition);
			this.queryManu(ja, condition, sqlStr, daoParent, 1);
			for (int i = 0; i < ja.size(); i++) {
				JSONObject json = ja.getJSONObject(i);
				json.put("menutype", "1");//menutype 1：机构 2：部门
				json.put("pid", tmd.get("menuTreeId"));
				json.put("pname", tmd.get("menuName"));
				this.hasSubItem(json);
			}
		}else{
			ja.clear();
			String menuId ="";
			String menuName = "";
			menuId = tmd.get("menuTreeId").toString();
			menuName = tmd.get("menuName").toString();
			this.subOrgQuery(ja,menuId,menuName);
			this.subDeptQuery(ja, menuId,menuName);
		}
	}
}
