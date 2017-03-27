package com.icbc.nt.bus;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jms.Message;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.directwebremoting.json.types.JsonArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.nt.bean.RoleBean;
import com.icbc.nt.bean.UserBean;
import com.icbc.nt.bean.UserRoleBean;
import com.icbc.nt.bean.UserTypeBean;
import com.icbc.nt.bus.dispatcher.BusDispatcherImpl;
import com.icbc.nt.mail.*;
import com.icbc.nt.util.IcbcUtil;
import com.icbc.nt.util.TransactionMapData;

@Transactional
public class UserBus extends BusParent {
	@Autowired
	private BusDispatcherImpl busDispatcherImpl;
	private String dayInterval;
	/*@Autowired
	private OrgBus orgBus;*/
	@Autowired
	private MediumBus mediumBus;//业务处理关联类
	public String getDayInterval() {
		return dayInterval;
	}
	public void setDayInterval(String dayInterval) {
		this.dayInterval = dayInterval;
	}
	
	
	/**
	 * 当前登录用户基本信息查询
	 */
//	public void userInfoQuery(JSONArray ja,TransactionMapData tmd){
	public void userInfoQuery(JSONArray ja){
		logger.info("*********userInfoQuery******");
		tmd.tmdLogger();
		
		iu.rmCondition(condition);
		String userId = tmd.get("userId").toString();
		iu.putCondition(condition, "userId", userId);
		sqlStr = "select a.userid,a.name,a.orgid,b.orgname from t_ntmisc_user a,t_ntmisc_org b where a.orgid=b.orgid and a.userid=?";
		logger.info("用户基本信息查询");
		iu.infoDbOper("用户基本信息查询", sqlStr, condition);
		this.queryManu(ja, condition, sqlStr, daoParent, 1);
		logger.info("用户基本信息查询,结果："+ja.toJSONString());
//		tmd.put("orgIdLogin", ja.getJSONObject(0).getString("orgid"));
//		tmd.put("orgIdName", ja.getJSONObject(0).getString("orgname"));
	}
	
	/**
	 * 绩效考核岗位查询
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void comboJbjx(JSONArray ja,LinkedHashMap<String, Object> condition,Map retMap,TransactionMapData tmd){
		iu.rmCondition(condition);
		iu.putConditionAll(condition, "jbjx_pid", tmd.get("jbjx_pid"));
		sqlStr = "select distinct jbjx_id,jbjx_name from t_ntmisc_jobjx where jbjx_pid=? order by jbjx_id ";
		logger.info("执行sql,绩效考核岗位查询，sqlStr:"+sqlStr+"-----condition:"+condition);
		this.queryManu(ja, condition, sqlStr, daoParent, 1);
	}
	
	/**
	 * 绩效考核所属岗位查询
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void comboJbjxp(JSONArray ja,LinkedHashMap<String, Object> condition,Map retMap,TransactionMapData tmd){
		iu.rmCondition(condition);
		sqlStr = " select distinct jbjx_pid,jbjx_pname from t_ntmisc_jobjx ";
		this.queryManu(ja, condition, sqlStr, daoParent, 1);
	}
	
	/**
	 * 获取协议职务层级
	 * @param jbIdNow
	 */
	public void getNextJob(String jbIdNow,JSONArray ja){
		iu.rmCondition(condition);
		iu.putCondition(condition, "jbId", jbIdNow);
//		JSONArray ja = new JSONArray();
		sqlStr = "select a.* from t_ntmisc_job a ,(select * from t_ntmisc_job where jb_id = ?) b where a.jb_idp = b.jb_idp and a.seq_num = to_number(b.seq_num)+1";
		this.queryManu(ja, condition, sqlStr, daoParent, 1);
	}
	
	/**
	 * 获取岗位所在的岗位类别
	 * @param jbId 岗位id
	 * @param jobArray 查找到的岗位id所在岗位类别数组（长度为1）
	 */
	public void getJobCat(String jbId,JSONArray jobArray){
		iu.rmCondition(condition);
		iu.putCondition(condition, "jb_id", jbId);
		sqlStr = "select * from t_ntmisc_job";
		JSONArray jobJa = new JSONArray();
		this.queryAuto(jobJa, condition, sqlStr, daoParent, 1);
		JSONObject jobJson = jobJa.getJSONObject(0);
		String jbIdp = jobJson.getString("jb_idp");
		String jbName = jobJson.getString("jb_name");
		if("0".equals(jbIdp)){
			jobArray.add(jobJson);
		}else{
			this.getJobCat(jbIdp, jobArray);
		}
	}
	
	
	
	/**
	 * 获取用户所在机构号
	 * @param userId 用户号
	 */
	public String  getUserOrg(String userId){
		String orgId = "";
		iu.putCondition(condition, "userId", userId);
		sqlStr = "select t.orgid,a.orgname from t_ntmisc_user t,t_ntmisc_org a where t.orgid = a.orgid  and t.userid = ?";
		ArrayList<HashMap<String,String>> vistOrgList = new ArrayList<HashMap<String,String>>();
		logger.info("获取用户所在机构号");
		iu.infoDbOper("获取用户所在机构号", sqlStr, condition);
		this.queryManu(vistOrgList, condition, sqlStr, daoParent, 1);
		try {
			HashMap<String,String> row = vistOrgList.get(0);
			orgId = row.get("orgid");
			logger.info("当前用户:"+userId+"所在机构:"+row.get("orgid")+"【"+row.get("orgname")+"】");
		} catch (Exception e) {
			// TODO: handle exception
		}
		return orgId;
	}
	/**
	 * sql拼接（加入 in 机构号集合）
	 * @param sql
	 * @param orgId
	 * @return
	 */
	public String getSqlWithIn(String sql,String orgId){
//		logger.info("getSqlWithIn orgId:"+orgId);
		String sqlStr = "";
		JSONArray jaOrgInfo = new JSONArray();
		StringBuffer orgIdSb = new StringBuffer();//当前机构及下属所有机构号，用","分割
		orgIdSb.append(orgId);
		mediumBus.getSubOrg(jaOrgInfo, orgId);
//		logger.info("jaOrgInfo.size():"+jaOrgInfo.size());
		for (int i = 0; i < jaOrgInfo.size(); i++) {
			JSONObject orgJson = jaOrgInfo.getJSONObject(i);
			orgIdSb.append(","+orgJson.get("menuid"));
		}
		sqlStr = "select * from("+sql+") t where t.orgid in("+orgIdSb.toString()+")";
		logger.info("getSqlWithIn sqlStr:"+sqlStr);
		return sqlStr;
	}
	
	/**
	 * 当前用户可见用户查询（当前用户所在部门所有用户）
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	/*public void visibleUserQuery(JSONArray ja,LinkedHashMap<String, Object> condition,Map retMap,TransactionMapData tmd){
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		String userIdQuery = tmd.get("userIdQuery").toString();
		iu.rmCondition(condition);
		String orgId = this.getUserOrg(tmd.get("userId").toString());
		String sql = "select userid user_id,name user_name,orgid from t_ntmisc_user t";
		if("all".equals(userIdQuery)){
			sqlStr = this.getSqlWithIn(sql, orgId);
		}else{
			sqlStr = this.getSqlWithIn(sql, orgId)+"and t.user_id like '%"+userIdQuery+"%'" +"or t.user_name like '%"+userIdQuery+"%'";
		}
		this.queryManu(ja, condition, sqlStr, daoParent, 1);
	}*/
	
	public String getUserIdOrPassId(JSONArray ja,LinkedHashMap<String, Object> condition,Map retMap,String passIdTag,TransactionMapData tmd){
		logger.info("getUserIdOrPassId passIdTag:"+passIdTag);
		String sqlStr = "";
		String retId = "";
		iu.putCondition(condition, "userIdOrPassid", tmd.get("userId"));
		if(passIdTag.equals("1")){//query userid
			sqlStr = "select a.*,b.passid from t_ntmisc_user a left join t_passid_info b on a.userid = b.userid where b.passid=?";
		}else{
			sqlStr = "select a.*,b.passid from t_ntmisc_user a left join t_passid_info b on a.userid = b.userid where a.user_id=?";
		}
		this.queryManu(ja, condition, sqlStr,daoParent, 1);
		JSONObject json = ja.getJSONObject(0);
		retId = json.getString("userid");
		logger.info("获取retId:"+retId);
		return retId;
	}
	/**
	 * 用户资格查询（供职务层级晋升使用用）
	 * @param userId
	 * @param ja
	 */
	public void userCtQuery(String userId,JSONArray ja){
		iu.rmCondition(condition);
		iu.putCondition(condition, "userId", userId);
		HashMap<String, Object> retMap = new HashMap<String, Object>();
		sqlStr = "select a.userid,a.ct_id,b.ct_name,a.oper_time from t_ntmisc_userct a,t_ntmisc_ct b where a.ct_id = b.ct_id";
		this.queryAuto(ja, condition, sqlStr, daoParent, 1);
	}
	
	/**
	 * 员工历年积分信息、工资登记档次变更、职务层级变更历史查询2015-07-21
	 * @param ja
	 * @param condition
	 * @param txCode
	 * @param retMap
	 * @return
	 */
	public Object userInfoHisQuery(JSONArray ja,LinkedHashMap<String, Object> condition,Map retMap,TransactionMapData tmd){
		String txCode = tmd.get("txCode").toString();
		logger.info("UserBus->userInfoQuery txCode:" + txCode);
		String sqlStr = "";
		List<String> parmList = new ArrayList<String>();
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		String sql = "";
		Object objRet = null;
		iu.rmCondition(condition);
		logger.info("当前交易："+Integer.parseInt(txCode));
		switch (Integer.parseInt(txCode)) {
		case 0://历年积分信息查询
			iu.putCondition(condition, "user_id", tmd.get("userId"));
//			sqlStr = "select t.* from t_ntmisc_pointhis t order by t.chg_time desc";
			sqlStr = " select a.userid user_id, a.name user_name,b.chg_time,b.point,b.type from t_ntmisc_user a"
					+ " left join t_ntmisc_pointhis b"
					+ " on a.userid = b.user_id order by b.chg_time desc";
			tmd.put("count",this.count(condition, daoParent, sqlStr, 1));//总记录数入变量池
			iu.putCondition(condition, "start", tmd.get("start"));
			iu.putCondition(condition, "end", tmd.get("end"));
			this.queryAuto(ja, condition, sqlStr,daoParent, 1);
			break;
		case 1://员工历年工资等级档次变更信息查询
			iu.putCondition(condition, "user_id", tmd.get("userId"));
			sqlStr = " select a.*,b.wg_name,b.wl_name from ("
					+ " select a.user_id,a.wg_value,a.wl_value,a.chg_time,b.name from t_ntmisc_wglhis a left join t_ntmisc_user b on a.user_id = b.userid"
					+ " ) a,t_ntmisc_wl b"
					+ " where a.wg_value = b.wg_value and a.wl_value = b.wl_value"
					+ " order by a.chg_time desc";
			tmd.put("count",this.count(condition, daoParent, sqlStr, 1));//总记录数入变量池
			iu.putCondition(condition, "start", tmd.get("start"));
			iu.putCondition(condition, "end", tmd.get("end"));
			this.queryAuto(ja, condition, sqlStr,daoParent, 1);
			break;
		case 2://员工历年职务层级（岗位）变更信息查询
			iu.putCondition(condition, "userid", tmd.get("userId"));
			sqlStr = " select a.userid,a.name,b.jb_name,c.chg_time from t_ntmisc_user a,t_ntmisc_job b,t_ntmisc_jobhis c"
					+ " where a.userid = c.user_id and b.jb_id = c.jb_id"
					+ " order by c.chg_time desc";
			tmd.put("count",this.count(condition, daoParent, sqlStr, 1));//总记录数入变量池
			iu.putCondition(condition, "start", tmd.get("start"));
			iu.putCondition(condition, "end", tmd.get("end"));
			this.queryAuto(ja, condition, sqlStr,daoParent, 1);
			break;
		case 3://员工历史学习积分信息查询
			iu.putCondition(condition, "userid", tmd.get("userId"));
			sqlStr = "  select b.userid,b.name,b.orgname,b.depname,c.*,a.point_year,a.oper_date,a.point from t_ntmisc_credit a,(select a.userid,a.name,a.orgid,a.depid,b.orgname,c.depname from t_ntmisc_user a,t_ntmisc_org b,t_ntmisc_dept c"
					+ " where a.orgid = b.orgid and a.depid = c.depid)b,(select a.*,"
					+ " case when a.ctype_id in(100,400) then a.ctype_id else a.ctype_pid end group_id,"
					+ " case when a.ctype_id in(100,400) then a.ctype_name else b.ctype_name end group_name"
					+ " from t_ntmisc_credittype a left join t_ntmisc_credittype b on a.ctype_pid = b.ctype_id"
					+ " where a.leaf_tag = 1)c"
					+ " where a.user_id = b.userid and a.ctype_id = c.ctype_id  order by  a.oper_date";
			tmd.put("count",this.count(condition, daoParent, sqlStr, 1));//总记录数入变量池
			iu.putCondition(condition, "start", tmd.get("start"));
			iu.putCondition(condition, "end", tmd.get("end"));
			this.queryAuto(ja, condition, sqlStr,daoParent, 1);
			break;
		case 4:
			iu.putCondition(condition, "userid", tmd.get("userId"));
			sqlStr = "select a.userid,a.ct_id,b.ct_name,a.oper_time from t_ntmisc_userct a,t_ntmisc_ct b where a.ct_id = b.ct_id";
			tmd.put("count",this.count(condition, daoParent, sqlStr, 1));//总记录数入变量池
			iu.putCondition(condition, "start", tmd.get("start"));
			iu.putCondition(condition, "end", tmd.get("end"));
			this.queryAuto(ja, condition, sqlStr,daoParent, 1);
			break;
		}
		return ja;
	}
	
	/**
	 * 用户权限查询\session超时判断
	 * @param condition 条件hashmap
	 * @param ja
	 */
	public void userAuthQuery(JSONArray ja,HashMap<String, Object> condition){
		logger.info("userAuthQuery");
		String sqlStr = " select distinct t.authority_id from t_ntmisc_roleauth t "
				+ " where t.role_id in (select role_id from t_ntmisc_userrole where user_id = ?) "
				+ " group by t.authority_id ";
//		this.extQueryManu(ja, condition, sqlStr, userDao, 1);
		iu.infoDbOper("用户权限查询", sqlStr, condition);
		this.queryManu(ja, condition, sqlStr, daoParent, 1);
		logger.info("用户权限查询结果："+ja);
	}

	/**
	 * 用户信息查询
	 * 
	 * @return 表列：user_id,user_type,org_id,org_name
	 */
	public List<Map<String, String>> userInfoQuery(String accountId) {
		logger.info("UserBus->userInfoQuery accountId:" + accountId);
		List<Map<String, String>> tableList = new ArrayList<Map<String, String>>();
//		List<String> parmList = new ArrayList<String>();
//		parmList.add(accountId);
		HashMap<String, Object> condition = new HashMap<String, Object>();
		condition.put("accountId", accountId);
		this.sql = "select a.user_id,a.user_name,a.org_id,b.org_name from t_ntmisc_user a,t_ntmisc_org b where a.org_id=b.org_id and a.user_id=?";
//		tableList = dh.getTableList(this.sql, parmList);
//		dh.releaseConn();
		this.queryManu(tableList, condition, sql, daoParent, 1);
		return tableList;
	}

	/**
	 * 用户角色修改
	 */
	public String userRoleUpdate(HttpSession session,UserBean userBean,Map retMap) {
		logger.info("userRoleUpdate");
		int updCount = 0;
		String sql = "";
		List<String> parmList1 = new ArrayList<String>();
		List<Map<String, String>> parmList2 = new ArrayList<Map<String, String>>();
//		String roleArr[] = userBean.getUserRoles().split("\\|");// 02|01|
		String roleArr[] = null;
//		parmList1.add(urb.getUserId());
		iu.rmCondition(condition);
		condition.put("userId", userBean.getUserId());
		// 删除
		sql = "delete from t_ntmisc_userrole t where t.user_id=?";
		this.update(condition, daoParent, sql,retMap);
		sql = "insert into t_ntmisc_userrole values(?,?)";
		for (int i = 0; i < roleArr.length; i++) {
			iu.rmCondition(condition);
			condition.put("userId", userBean.getUserId());
			condition.put("roleId", roleArr[i]);
			this.update(condition, daoParent, sql,retMap);
		}
		return tmd.get("recUpdCount").toString();
	}
	/**
	 * 批量插入用户角色
	 * @param retMap
	 */
	public void roleInsertBat(Map retMap,TransactionMapData tmd){
		logger.info("roleInsertBat");
		tmd.tmdLogger();
		String sqlStr = "";
		//删除用户角色
		iu.rmCondition(condition);
		iu.putCondition(condition, "userId", tmd.get("userId"));
		sqlStr = "delete t_ntmisc_userrole t where t.user_id = ?";
		this.update(condition, daoParent, sqlStr, retMap);
		//批量插入用户角色
		iu.rmCondition(condition);
		LinkedList<LinkedHashMap<String, Object>> condList = new LinkedList<LinkedHashMap<String,Object>>();
		/*String[] roleSelectArr = ((String)tmd.get("roleSelect")).split(",");
		for (int i = 0; i < roleSelectArr.length; i++) {
			LinkedHashMap<String, Object> condMap = new LinkedHashMap<String, Object>();
			condMap.put("userId", tmd.get("userId"));
			condMap.put("roleId", roleSelectArr[i]);
			condList.add(condMap);
		}*/
		JSONArray roleSelectJa = (JSONArray) tmd.get("roleSelect");
		for (int i = 0; i < roleSelectJa.size(); i++) {
			LinkedHashMap<String, Object> condMap = new LinkedHashMap<String, Object>();
			condMap.put("userId", tmd.get("userId"));
			condMap.put("roleId", roleSelectJa.getString(i));
			condList.add(condMap);
		}
		sqlStr = "insert into t_ntmisc_userrole(user_id,role_id) values(?,?)";
		this.updateBat(condList, daoParent, retMap, sqlStr, 1);
	}
	
//	public void userManage(LinkedHashMap<String, Object> condition,String tranData,Map retMap,TransactionMapData tmd){
	public void userManage(JSONArray ja,LinkedHashMap<String, Object> condition,Map retMap){
		System.out.println("userManage");
		logger.info("*************userManage************");
		tmd.tmdLogger();
	}
	
	/**
	 * 用户管理
	 * @param ja
	 * @param condition
	 * @param txCode 0:岗位类别查询 1:人员性质查询 10014:用户查询
	 * @param retMap
	 * @return
	 */
	public Object userManage(JSONArray ja,LinkedHashMap<String, Object> condition,String txCode,Map retMap,TransactionMapData tmd){
		logger.info("UserBus->userManage txCode:" + txCode);
//		System.out.println("UserBus用户管理txCode:" + txCode);
		tmd.tmdLogger();
		String sqlStr = "";
		List<String> parmList = new ArrayList<String>();
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		String sql = "";
		Object objRet = null;
		iu.rmCondition(condition);
		switch (Integer.parseInt(txCode)) {
		case 0://岗位层级查询
			sqlStr = "select jb_id jbid,jb_name jbname from t_ntmisc_job t";
			this.queryAuto(ja, condition, sqlStr,daoParent, 1);
			break;
		case 1://人员性质查询
			sqlStr = "select property_id propertyid ,property_name propertyname from t_ntmisc_property t";
			this.queryAuto(ja, condition, sqlStr,daoParent, 1);
			break;
		case 2://用户角色查询
			iu.putCondition(condition, "userId", tmd.get("userId"));
			sqlStr = "select t.user_id,t.role_id from t_ntmisc_userrole t where t.user_id=?";
			this.queryManu(ja, condition, sqlStr,daoParent, 1);
			break;
		case 3://用户机构查询
			iu.putCondition(condition, "userId", tmd.get("currUser"));
			sqlStr = "select t.orgid from t_ntmisc_user t where t.userid = ?";
			this.queryManu(ja, condition, sqlStr,daoParent, 1);
			break;
		case 4://岗位类别查询
			sqlStr = "select jb_id jbcatid,jb_name jbcatname from t_ntmisc_job t where t.jb_idp = '0'";
			this.queryAuto(ja, condition, sqlStr,daoParent, 1);
			break;
		case 10011://新增
			//查询用户是否存在
			iu.rmCondition(condition);
			iu.putCondition(condition, "userid", tmd.get("userId"));
			sqlStr = "select * from t_ntmisc_user where userid=?";
			this.queryManu(ja, condition, sqlStr, daoParent, 1);
			if(ja.size() > 0){
				iu.infoPrgsIntrpt(tmd, "添加失败，用户"+tmd.get("userId")+"已存在！");
				retMap.put("errorMsg", "添加失败，用户"+tmd.get("userId")+"已存在！");
				retMap.put("errorCode", "01");
				break;
			}
			//插入用户今本信息
			iu.rmCondition(condition);
			condition.put("orgid", tmd.get("orgId"));
			condition.put("depid", tmd.get("deptId"));
			condition.put("userid", tmd.get("userId"));
			condition.put("name", tmd.get("name"));
			condition.put("jbjxId", tmd.get("jbjxId")==null?"":tmd.get("jbjxId"));
			sqlStr = "insert into t_ntmisc_user(orgid,depid,userid,name,jbjx_id)  values(?,?,?,?,?)";
			iu.infoDbOper("新增用户", sqlStr, condition);
			this.update(condition, daoParent, sqlStr,retMap);
			this.roleInsertBat(retMap,tmd);
			break;
		case 10012://修改
			//修改用户
			condition.put("orgid", tmd.get("orgId"));
			condition.put("depid", tmd.get("deptId"));
			condition.put("name", tmd.get("name"));
			/*condition.put("sex", tmd.get("sex"));
			condition.put("property", tmd.get("propertyId"));
			condition.put("mail", tmd.get("mail"));
			condition.put("begindate", iu.timeFormate(tmd.get("beginDate").toString(), "yyyy-MM-dd", "yyyyMMdd"));
			condition.put("jbId", tmd.get("jbId"));
			condition.put("jbCat", tmd.get("jbCat"));*/
			condition.put("jbjxId", tmd.get("jbjxId"));
			condition.put("userid", tmd.get("userId"));
//			sqlStr = "update t_ntmisc_user t set t.orgid=?,t.depid=?,t.name=?,t.sex=?,t.property=?,t.mail=?,t.begindate=?,t.jb_id=?,t.jb_cat=?,t.jbjx_id=? where t.userid=?";
			sqlStr = "update t_ntmisc_user t set t.orgid=?,t.depid=?,t.name=?,t.jbjx_id=? where t.userid=?";
			iu.infoDbOper("修改用户", sqlStr, condition);
			this.update(condition, daoParent, sqlStr,retMap);
			this.roleInsertBat(retMap,tmd);
			break;
		case 10013://删除
			iu.putCondition(condition, "userId", tmd.get("userId"));
			sqlStr = "delete t_ntmisc_user t where t.userid = ?";//删用户
			this.update(condition, daoParent, sqlStr, retMap);
			sqlStr = "delete t_ntmisc_userrole t where t.user_id = ?";
			this.update(condition, daoParent, sqlStr, retMap);//删用户角色
			break;
		case 10014:// 查询所有用户
//			String orgCurr = "";//登录用户所在orgid
			String orgId = null;//查询区orgid
			String userId = null;
//			String orgIn = "";//当前用户所有可见用户
//			String currUser = tmd.get("currUser").toString();
			String currUser = tmd.get("userIdLogin").toString();
			tmd.put("currUser", currUser);
//			if(tmd.get("orgId") != null)
//				orgId = tmd.get("orgId").toString();
			if(tmd.get("userId") != null)
				userId = tmd.get("userId").toString();
			logger.debug("查询所有用户，机构根 orgId:"+orgId);
			System.out.println("查询所有用户，机构根 orgId:"+orgId);
			try{
				if(tmd.get("currUser") != null){
//					orgCurr = busDispatcherImpl.userOrgQuery(tmd);
//					orgIn = busDispatcherImpl.userOrgIn(tmd);
				}
			}catch(Exception e){
				logger.error("用户管理：获取当前查询用户所在机构号失败");
			}
			ja.clear();
			iu.rmCondition(condition);
			//初始sql
			sqlStr = " select c.*,d.jbjx_pid,d.jbjx_pname,d.jbjx_name " 
					+  " from  (select a.*,b.orgname,c.depname from t_ntmisc_user a,t_ntmisc_org b,t_ntmisc_dept c where a.orgid = b.orgid and a.depid = c.depid) c " 
					+  " left join t_ntmisc_jobjx d "  
					+  " on c.jbjx_id = d.jbjx_id " 
					+  " where c.orgid in "
					+  tmd.get("orgIdIn") 
					+  " order by c.orgid,c.depid,c.userid";
			String userIdQuery = (String) tmd.get("userIdQuery");
			iu.putCondition(condition, "userid", userIdQuery);
			sqlStr = mediumBus.putOrgCond(sqlStr, tmd, condition);
			/*try{
				if(orgId == null || "".equals(orgId)){//点击"用户管理"，初始查询
					orgCurr = this.getUserOrg(currUser);
					sqlStr = this.getSqlWithIn(sqlStr,orgCurr);
				}else{
					if(orgId.equals(tmd.get("depId"))){//机构号与部门号相同，查询当前机构所有员工
//						sqlStr = this.getSqlWithIn(orgId);
						sqlStr = this.getSqlWithIn(sqlStr,orgId);
					}else{//机构号与部门号不同，查询当前部门所有员工
						iu.putCondition(condition, "orgid", orgId);//机构号
						iu.putCondition(condition, "depid", tmd.get("depId"));//部门编号
					}
				}
			}catch(Exception e){}*/
			tmd.put("count",this.count(condition, daoParent, sqlStr, 2));//总记录数入变量池
			iu.putCondition(condition, "start", tmd.get("start"));
			iu.putCondition(condition, "end", tmd.get("end"));
			System.out.println(tmd.get("start")+"|"+tmd.get("end"));
			logger.info("用户查询");
			iu.infoDbOper("用户查询", sqlStr, condition);
			this.queryAuto(ja, condition, sqlStr, daoParent, 2);
			/*for (int i = 0; i < ja.size(); i++) {
				JSONObject json = ja.getJSONObject(i);
				json.put("begindate", iu.timeFormate(json.get("begindate").toString(), "yyyyMMdd", "yyyy-MM-dd"));
			}*/
			break;
		case 20021:
			iu.rmCondition(condition);
			iu.putCondition(condition, "userid", tmd.get("userId"));
			sqlStr = "select c.*,a.jb_name jbname,b.property_name propertyname from t_ntmisc_job a,t_ntmisc_property b,"
					+ " (select a.*,b.orgname,c.depname from t_ntmisc_user a,t_ntmisc_org b,t_ntmisc_dept c where a.orgid = b.orgid and a.depid = c.depid) c"
					+ " where a.jb_id = c.jb_id and b.property_id = c.property";
			this.queryAuto(ja, condition, sqlStr, daoParent, 1);
			break;
		}
		return ja;
	}

	/*public void naviRefresh(){
		mediumBus.naviRefresh();
	}*/
	/**
	 * 用户登录验证
	 * @return String 0:用户不存在 1:用户存在
	 * @throws SQLException
	 */
	public String userValidate(LinkedHashMap<String,Object> condition) {
		String strRet = "0";
		List<Map<String, String>> tableList = new ArrayList<Map<String, String>>();
		String sqlStr = "select * from t_ntmisc_user t where t.userid=?";
		this.queryManu(tableList, condition, sqlStr, daoParent, 1);
		logger.info("tableList:"+tableList);
		try{
			strRet = tableList.get(0).get("userid");
		}catch(Exception e){
			logger.info("用户登录验证："+condition.get("userid")+"用户不存在");
		}
//		logger.info("UserBus->userValidate strRet（0:用户不存在，其他：返回用户特定账户）:"+strRet);
		return strRet;// 0:用户不存在，返回帐号，用户存在
	}
}
