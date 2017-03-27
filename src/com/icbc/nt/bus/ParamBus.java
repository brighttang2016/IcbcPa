package com.icbc.nt.bus;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import oracle.jdbc.oracore.OracleType;

import org.apache.log4j.Logger;
import org.directwebremoting.json.types.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.nt.bus.dispatcher.BusDispatcherImpl;
import com.icbc.nt.dom.ParamDao;
import com.icbc.nt.util.TransactionMapData;
@Service
@Transactional
public class ParamBus extends BusParent{
	ResultSet rs = null;
	ResultSetMetaData rsmd = null;
	@Autowired
	private OrgBus orgBus;
	@Autowired
	private ParamDao paramDao;
	@Autowired
	private MediumBus mediumBus;
	@Autowired
	private BusDispatcherImpl busDispatcherImpl;
	
	/**
	 * 机构用户初始化
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void sysInit(TransactionMapData tmd){
		//初始化机构部门
		busDispatcherImpl.f10000001(tmd);
		//初始化用户
		busDispatcherImpl.f10000002(tmd);
		/*switch(Integer.parseInt(tmd.get("txCode").toString())){
		case 10000001:
			busDispatcherImpl.f10000001(tmd);
			break;
		case 10000002:
			busDispatcherImpl.f10000002(tmd);
			break;
		}*/
	}
	
	/**
	 * 总包占比管理
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void zbzbManage(JSONArray ja,LinkedHashMap<String, Object> condition,Map retMap,TransactionMapData tmd){
		busDispatcherImpl.zbzbManage(ja, condition, retMap, tmd);
	}
	
	/**
	 * 员工资格认证设置
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 * @return
	 */
	public Object uctManage(JSONArray ja,LinkedHashMap<String, Object> condition,Map retMap,TransactionMapData tmd){
		String txCode = tmd.get("txCode").toString();
		logger.info("ParamBus->uctManage txCode:" + txCode);
		iu.rmCondition(condition);
		switch (Integer.parseInt(txCode)) {
		case 30091://添加员工资格认证信息
			mediumBus.uctBat(retMap, tmd.get("data").toString());
			break;
		case 30092://删除员工资格认证信息
			iu.rmCondition(condition);
			iu.putCondition(condition, "userId", tmd.get("userId"));
			iu.putCondition(condition, "ctId", tmd.get("ctId"));
			sqlStr = "delete t_ntmisc_userct t where t.userid=? and t.ct_id=?";
			this.update(condition, daoParent, sqlStr, retMap);
			break;
		case 30093://查询员工资格认证信息
			String orgCurr = "";//登录用户所在orgid
			String orgId = null;//查询区orgid
			String userId = null;
			String  currUser = tmd.get("currUser").toString();
			if(tmd.get("orgId") != null)
				orgId = tmd.get("orgId").toString();
			if(tmd.get("userId") != null)
				userId = tmd.get("userId").toString();
			logger.info("查询所有用户，机构根 orgId:"+orgId);
			try{
				if(tmd.get("currUser") != null){
					orgCurr = mediumBus.getUserOrg(tmd.get("currUser").toString());
				}
			}catch(Exception e){
				logger.error("用户管理：获取当前查询用户所在机构号失败");
			}
			ja.clear();
			iu.rmCondition(condition);
			//初始sql
			sqlStr = "select a.*,b.ct_id,b.ct_name,b.oper_time from (select a.userid,a.name,a.orgid,a.depid,b.orgname,c.depname from t_ntmisc_user a,t_ntmisc_org b,t_ntmisc_dept c where a.orgid = b.orgid and a.depid = c.depid) a, (select a.userid,a.ct_id,a.oper_time,b.ct_name from t_ntmisc_userct a,t_ntmisc_ct b where a.ct_id = b.ct_id ) b where a.userid = b.userid order by a.userid,b.ct_id";
			iu.putCondition(condition, "userid", tmd.get("userId"));
			try{
				if(orgId == null || "".equals(orgId)){//点击"用户管理"，初始查询
//					orgCurr = mediumBus.getUserOrg(currUser);
					sqlStr = mediumBus.getSqlWithIn(sqlStr,orgCurr);
				}else{
					if(orgId.equals(tmd.get("depId"))){//机构号与部门号相同，查询当前机构所有员工
//						sqlStr = this.getSqlWithIn(orgId);
						sqlStr = mediumBus.getSqlWithIn(sqlStr,orgId);
					}else{//机构号与部门号不同，查询当前部门所有员工
						iu.putCondition(condition, "orgid", orgId);//机构号
						iu.putCondition(condition, "depid", tmd.get("depId"));//部门编号
					}
				}
			}catch(Exception e){}
			tmd.put("count",this.count(condition, daoParent, sqlStr, 1));//总记录数入变量池
			iu.putCondition(condition, "start", tmd.get("start"));
			iu.putCondition(condition, "end", tmd.get("end"));
			logger.info(tmd.get("start")+"|"+tmd.get("end"));
			this.queryAuto(ja, condition, sqlStr, daoParent, 2);
			break;
		}
		return ja;
	}
	
	/**
	 * 不参与年终考核员工设置
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 * @return
	 */
	public Object assManage(JSONArray ja,LinkedHashMap<String, Object> condition,Map retMap,TransactionMapData tmd){
		String txCode = tmd.get("txCode").toString();
		logger.info("ParamBus->assManage txCode:" + txCode);
		iu.rmCondition(condition);
		String data = "";
		switch (Integer.parseInt(txCode)) {
		case 30081://添加不参与年终考核员工
			JSONArray jsonArray = JSONArray.parseArray(tmd.get("data").toString());
			logger.info("jsonArray.toJSONString():"+jsonArray.toJSONString());
			LinkedList<LinkedHashMap<String,Object>> condList = new LinkedList<LinkedHashMap<String,Object>>();
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject json = jsonArray.getJSONObject(i);
				LinkedHashMap<String, Object> condMap = new LinkedHashMap<String, Object>();
				condMap.put("ass_flag", "0");
				condMap.put("userId", json.getString("userId"));
				logger.info("assManage condMap:"+condMap.toString());
				condList.add(condMap);
			}
			sqlStr = "update t_ntmisc_user t set t.ass_flag = ? where t.userid = ?";
			this.updateBat(condList, daoParent, retMap, sqlStr, 1);
			break;
		case 30082://删除不参与年终考核员工
			data = tmd.get("data").toString();
			condList = new LinkedList<LinkedHashMap<String,Object>>();
			jsonArray = JSONArray.parseArray(data);
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject json = jsonArray.getJSONObject(i);
				LinkedHashMap<String, Object> hashMap = new LinkedHashMap<String, Object>();
				hashMap.put("ass_flag", "1");
				hashMap.put("userId", json.getString("userId"));
				condList.add(hashMap);
			}
			sqlStr = "update t_ntmisc_user t set t.ass_flag = ? where t.userid = ?";
			logger.info("执行sql,删除不参与年终考核员工,sqlStr:"+sqlStr+"condList:"+condList.toString());
			this.updateBat(condList, daoParent, retMap, sqlStr, 1);
			break;
		case 30083://查询不参与年终考核员工
			String orgCurr = "";//登录用户所在orgid
			String orgId = null;//查询区orgid
			String userId = null;
			String  currUser = tmd.get("currUser").toString();
			if(tmd.get("orgId") != null)
				orgId = tmd.get("orgId").toString();
			if(tmd.get("userId") != null)
				userId = tmd.get("userId").toString();
			logger.info("查询所有用户，机构根 orgId:"+orgId);
			try{
				if(tmd.get("currUser") != null){
					orgCurr = mediumBus.getUserOrg(tmd.get("currUser").toString());
				}
			}catch(Exception e){
				logger.error("用户管理：获取当前查询用户所在机构号失败");
			}
			ja.clear();
			iu.rmCondition(condition);
			//初始sql
			sqlStr = "select a.userid,a.name,a.orgid,a.depid,b.orgname,c.depname from t_ntmisc_user a,t_ntmisc_org b,t_ntmisc_dept c  "
					+ " where a.orgid = b.orgid and a.depid = c.depid and a.ass_flag = '0'";
			iu.putCondition(condition, "userid", tmd.get("userId"));
			sqlStr = mediumBus.putOrgCond(sqlStr, tmd, condition);
			/*try{
				if(orgId == null || "".equals(orgId)){//点击"用户管理"，初始查询
//					orgCurr = mediumBus.getUserOrg(currUser);
					sqlStr = mediumBus.getSqlWithIn(sqlStr,orgCurr);
				}else{
					if(orgId.equals(tmd.get("depId"))){//机构号与部门号相同，查询当前机构所有员工
//						sqlStr = this.getSqlWithIn(orgId);
						sqlStr = mediumBus.getSqlWithIn(sqlStr,orgId);
					}else{//机构号与部门号不同，查询当前部门所有员工
						iu.putCondition(condition, "orgid", orgId);//机构号
						iu.putCondition(condition, "depid", tmd.get("depId"));//部门编号
					}
				}
			}catch(Exception e){}*/
			
			tmd.put("count",this.count(condition, daoParent, sqlStr, 1));//总记录数入变量池
			iu.putCondition(condition, "start", tmd.get("start"));
			iu.putCondition(condition, "end", tmd.get("end"));
			logger.info(tmd.get("start")+"|"+tmd.get("end"));
			this.queryAuto(ja, condition, sqlStr, daoParent, 2);
			break;
		}
		return ja;
	}
	
	
	/**
	 * 工资等级档次计算，根据用户积分计算工资等级档次
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void wglCalc(JSONArray ja,LinkedHashMap<String, Object> condition,Map retMap,TransactionMapData tmd){
		mediumBus.wglBat();
	}
	
	/**
	 * 职务层级自动晋升批量计算
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void jobCalc(JSONArray ja,LinkedHashMap<String, Object> condition,Map retMap,TransactionMapData tmd){
		mediumBus.jobBat();
	}
	
	/**
	 * 年终考核，根据学习积分计算年终考核
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void pointCalc(JSONArray ja,LinkedHashMap<String, Object> condition,Map retMap,TransactionMapData tmd){
		mediumBus.pointBat();
	}
	/**
	 * 积分维护类型查询
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void cTypeQuery(JSONArray ja,LinkedHashMap<String, Object> condition,Map retMap,TransactionMapData tmd){
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		iu.rmCondition(condition);
		if("100".equals(tmd.get("cTypeId")) || "400".equals(tmd.get("cTypeId"))){//总行积分与手工调整无子节点
			iu.putCondition(condition, "ctype_pid", tmd.get("cTypeId"));
			sqlStr = "select * from t_ntmisc_credittype t where t.leaf_tag ='1' and ctype_id=?";
		}else{
			iu.putCondition(condition, "ctype_pid", tmd.get("cTypePid"));
			sqlStr = "select * from t_ntmisc_credittype t where t.leaf_tag ='1' and ctype_pid=?";
		}
			
		this.queryManu(ja, condition, sqlStr, daoParent, 1);
	}
	
	/**
	 * 纵表变横表(说明：若在数据库层直接用sql进行行列转换，当学分类型表(t_ntmisc_credittype)中的col_name列改变后，涉及到sql语句的改变，故而转为在代码中动态实现行列转换)
	 * @param ja 纵表
	 */
	public JSONArray colToRow(JSONArray ja){
		JSONArray jaRow = new JSONArray();
		for (int i = 0; i < ja.size(); i++) {
			JSONObject jsonScan = ja.getJSONObject(i);
			String userIdScan = jsonScan.getString("userid");
			String existTag = "0";
			for (int j = 0; j < jaRow.size(); j++) {
				JSONObject jsonExist = jaRow.getJSONObject(j);
				String userIdExist = jsonExist.getString("userid");
				if(userIdScan.equals(userIdExist)){
					jsonExist.put(jsonScan.getString("col_name"), jsonScan.getString("point_sum"));
					jsonExist.put("all_sum", Integer.parseInt(jsonExist.getString("all_sum"))+Integer.parseInt(jsonScan.getString("point_sum")));
					logger.info("行加入列"+jsonScan.getString("col_name")+"|"+jsonScan.getString("point_sum"));
					
					existTag = "1";
				}
				logger.info("添加列后行数据："+jsonExist.toJSONString());
			}
			if("0".equals(existTag)){//行不存在
//				HashMap<String,String> rowMap = new HashMap<String, String>();
				JSONObject jsonNew = new JSONObject();
				jsonNew.put("userid", jsonScan.getString("userid"));
				jsonNew.put("name", jsonScan.getString("name"));
				jsonNew.put("sex", jsonScan.getString("sex"));
				jsonNew.put("orgid", jsonScan.getString("orgid"));
				jsonNew.put("orgname", jsonScan.getString("orgname"));
				jsonNew.put("depid", jsonScan.getString("depid"));
				jsonNew.put("depname", jsonScan.getString("depname"));
				jsonNew.put(jsonScan.getString("col_name"), jsonScan.getString("point_sum"));
				jsonNew.put("all_sum", jsonScan.getString("point_sum"));
				jaRow.add(jsonNew);
				logger.info("新增行："+jsonNew.toJSONString());
			}
		}
//		ja = jaRow;
//		logger.info("**************:"+ja.size());
		ja.clear();
		List<String> list = new ArrayList();
		for(Object json:jaRow){
			ja.add(json);
		}
		return jaRow;
	}
	
	/**
	 * 获取集成in条件后的sql
	 * @return
	 */
	public String getSqlWithIn(String sqlStr,String orgId){
		logger.info("getSqlWithIn orgId:"+orgId);
//		String sqlStr = "";
		JSONArray jaOrgInfo = new JSONArray();
		StringBuffer orgIdSb = new StringBuffer();//当前机构及下属所有机构号，用","分割
		orgIdSb.append(orgId);
//		orgBus.getSubOrg(jaOrgInfo, orgId);
		mediumBus.getSubOrg(jaOrgInfo, orgId);
		logger.info("jaOrgInfo.size():"+jaOrgInfo.size());
		for (int i = 0; i < jaOrgInfo.size(); i++) {
			JSONObject orgJson = jaOrgInfo.getJSONObject(i);
//			logger.info("orgid："+orgJson.get("menuid"));
			orgIdSb.append(","+orgJson.get("menuid"));
		}
		/*sqlStr = "select a.*,b.orgname,c.depname from t_ntmisc_user a,t_ntmisc_org b,t_ntmisc_dept c" 
				+" where a.orgid = b.orgid and a.depid = c.depid and a.orgid in("+orgIdSb.toString()+")";*/
		sqlStr = "select * from ( "+sqlStr+" ) where 1=1" +" and orgid in("+orgIdSb.toString()+")";
		logger.info("getSqlWithIn sqlStr:"+sqlStr);
		return sqlStr;
	}
	
	public void batchInsert(Map retMap,TransactionMapData tmd){
		JSONArray jsonArray = JSONArray.parseArray(tmd.get("data").toString());
		LinkedList<LinkedHashMap<String,Object>> condList = new LinkedList<LinkedHashMap<String,Object>>();
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject json = jsonArray.getJSONObject(i);
			LinkedHashMap<String, Object> condMap = new LinkedHashMap<String, Object>();
			condMap.put("userId", json.getString("userId"));
			condMap.put("point", json.getString("point"));
			condMap.put("pointYear", iu.timeFormate(Calendar.getInstance().getTime(), "yyyy"));
			condMap.put("cTypeId",json.getString("cTypeId"));
			condMap.put("operDate", iu.timeFormate(Calendar.getInstance().getTime(), "yyyyMMdd"));
			logger.info("creditManage condMap:"+condMap.toString());
			condList.add(condMap);
		}
		sqlStr = "insert into t_ntmisc_credit(user_id,point,point_year,ctype_id,oper_date) values(?,?,?,?,?)";
		this.updateBat(condList, daoParent, retMap, sqlStr, 1);
	}
	
	/**
	 * 学习积分管理
	 * @param ja
	 * @param condition
	 * @param txCode 交易码
	 * @param retMap
	 * @return
	 */
	public Object creditManage(JSONArray ja,LinkedHashMap<String, Object> condition,Map retMap,TransactionMapData tmd){
		String txCode = tmd.get("txCode").toString();
		logger.info("ParamBus->creditManage txCode:" + txCode);
		List<String> parmList = new ArrayList<String>();
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		iu.rmCondition(condition);
		switch (Integer.parseInt(txCode)) {
		case 30061://总行基础学分单笔
			batchInsert(retMap,tmd);
			break;
		case 30062://总行基础学分批量
			break;
		case 30063://市行基础学分单笔
			batchInsert(retMap,tmd);
			break;
		case 30064://市行基础学分批量
			break;
		case 30065://附加学分单笔
			batchInsert(retMap,tmd);
			break;
		case 30066://附加学分批量
			break;
		case 30067://手工调整单笔
			batchInsert(retMap,tmd);
			break;
		case 30068://手工调整批量
			break;
		/*case 1://人员性质查询
			sqlStr = "select property_id propertyid ,property_name propertyname from t_ntmisc_property t";
			this.queryAuto(ja, condition, sqlStr,daoParent, 1);
			break;*/
		/*case 2://用户角色查询
			iu.putCondition(condition, "userId", tmd.get("userId"));
			sqlStr = "select t.user_id,t.role_id from t_ntmisc_userrole t where t.user_id=?";
			this.queryManu(ja, condition, sqlStr,daoParent, 1);
			break;*/
		/*case 3://用户机构查询
			iu.putCondition(condition, "userId", tmd.get("currUser"));
			sqlStr = "select t.orgid from t_ntmisc_user t where t.userid = ?";
			this.queryManu(ja, condition, sqlStr,daoParent, 1);
			break;*/
		/*case 10011://新增
			//插入用户今本信息
			condition.put("orgid", tmd.get("orgId"));
			condition.put("depid", tmd.get("depId"));
			condition.put("userid", tmd.get("userId"));
			condition.put("name", tmd.get("name"));
			condition.put("sex", tmd.get("sex"));
			condition.put("property", tmd.get("propertyId"));
			condition.put("mail", tmd.get("mail"));
			condition.put("begindate", iu.timeFormate(tmd.get("beginDate").toString(), "yyyy-MM-dd", "yyyyMMdd"));
			condition.put("jbId", tmd.get("jbId"));
			sqlStr = "insert into t_ntmisc_user(orgid,depid,userid,name,sex,property,mail,begindate,jb_id)  values(?,?,?,?,?,?,?,?,?)";
			this.update(condition, daoParent, sqlStr,retMap);
//			this.roleInsertBat(retMap);
			break;*/
		/*case 10012://修改
			//修改用户
			condition.put("orgid", tmd.get("orgId"));
			condition.put("depid", tmd.get("depId"));
			condition.put("name", tmd.get("name"));
			condition.put("sex", tmd.get("sex"));
			condition.put("property", tmd.get("propertyId"));
			condition.put("mail", tmd.get("mail"));
			condition.put("begindate", iu.timeFormate(tmd.get("beginDate").toString(), "yyyy-MM-dd", "yyyyMMdd"));
			condition.put("jbId", tmd.get("jbId"));
			condition.put("userid", tmd.get("userId"));
			sqlStr = "update t_ntmisc_user t set t.orgid=?,t.depid=?,t.name=?,t.sex=?,t.property=?,t.mail=?,t.begindate=?,t.jb_id=? where t.userid=?";
			this.update(condition, daoParent, sqlStr,retMap);
//			this.roleInsertBat(retMap);
			break;*/
		/*case 10013://删除
			iu.putCondition(condition, "userId", tmd.get("userId"));
			sqlStr = "delete t_ntmisc_user t where t.userid = ?";//删用户
			this.update(condition, daoParent, sqlStr, retMap);
			sqlStr = "delete t_ntmisc_userrole t where t.user_id = ?";
			this.update(condition, daoParent, sqlStr, retMap);//删用户角色
			break;*/
		case 30069:// 用户学习积分查询
			String orgCurr = "";//登录用户所在orgid
			String orgId = null;//查询区orgid
			String depId = null;
			String userId = null;
			String  currUser = tmd.get("currUser").toString();
			if(tmd.get("orgId") != null)
				orgId = tmd.get("orgId").toString();
			if(tmd.get("depId") != null)
				depId = tmd.get("depId").toString();
			if(tmd.get("userId") != null)
				userId = tmd.get("userId").toString();
			logger.info("查询所有用户，机构根 orgId:"+orgId);
			System.out.println("查询所有用户，机构根 orgId:"+orgId);
			try{
				if(tmd.get("currUser") != null){
//					orgCurr = this.getUserOrg(tmd.get("currUser").toString());
					orgCurr = mediumBus.getUserOrg(tmd.get("currUser").toString());
				}
			}catch(Exception e){
				logger.error("用户管理：获取当前查询用户所在机构号失败");
			}
			//先分页查询，再在查询结果中统计各种学分数据，存在未在分页范围内的同组数据无法统计的问题，解决方法，不通过sql查询，改用存储过程
			/*ja.clear();
			iu.rmCondition(condition);
			//初始sql
			sqlStr = " select t.userid,t.name, case when t.sex = 'y' then '男' else '女'end sex, t.orgid,t.orgname,t.depid,t.depname,t.ctype_id,t.ctype_name,t.col_name,sum(point) point_sum   from (select a.*, b.ctype_name, b.col_name from (select b.*,a.point,a.point_year,a.ctype_id,a.note, a.oper_date from t_ntmisc_credit a left join (select a.userid,a.name,a.orgid,a.sex,b.orgname,a.depid,c.depname from t_ntmisc_user a,t_ntmisc_org b,t_ntmisc_dept c where a.orgid = b.orgid and a.depid = c.depid) b on a.user_id = b.userid) a left join t_ntmisc_credittype b   on a.ctype_id = b.ctype_id) t  group by t.userid,t.name,t.sex,t.orgid, t.orgname,t.depid,t.depname,t.ctype_id,t.ctype_name,t.col_name ";
			iu.putCondition(condition, "userid", tmd.get("userId"));
			try{
				logger.info("66666666666 orgId:"+orgId);
				if(orgId == null || "".equals(orgId)){//点击"用户管理"，初始查询
					orgCurr = this.getUserOrg(currUser);
					sqlStr = this.getSqlWithIn(sqlStr,orgCurr);
				}else{
					if(orgId.equals(tmd.get("depId"))){//机构号与部门号相同，查询当前机构所有员工
						sqlStr = this.getSqlWithIn(sqlStr,orgId);
					}else{//机构号与部门号不同，查询当前部门所有员工
						iu.putCondition(condition, "orgid", orgId);//机构号
						iu.putCondition(condition, "depid", tmd.get("depId"));//部门编号
					}
				}
			}catch(Exception e){}
//			logger.info("员工信息查询sqlStr："+sqlStr);
			tmd.put("count",this.count(condition, daoParent, sqlStr, 1));//总记录数入变量池
			iu.putCondition(condition, "start", tmd.get("start"));
			iu.putCondition(condition, "end", tmd.get("end"));
			this.queryAuto(ja, condition, sqlStr, daoParent, 2);
			for (int i = 0; i < ja.size(); i++) {
				JSONObject json = ja.getJSONObject(i);
				logger.info("学习积分查询:"+json.toJSONString());
//				json.put("begindate", iu.timeFormate(json.get("begindate").toString(), "yyyyMMdd", "yyyy-MM-dd"));
			}
			this.colToRow(ja);
			logger.info("************:"+ja.size());*/
			
			//调用存储过程查询学分信息
//			String[] inArray = new String[]{orgId,depId,userId};
			/*
			String[] inArray = new String[]{"a"};
			String[] outArray = new String[]{"cursor"}; 
			HashMap outMap = new HashMap();
			sqlStr = "{call procpkg.procrefcursor(?,?)}";
			paramDao.creditQuery(sqlStr, inArray, outArray, outMap);*/
			sqlStr =" select * from (" 
					+ " select t.userid,t.name,t.sex,t.orgid,t.orgname,t.depid,t.depname,"
					+ " sum(case when t.ctype_id = '100' then point_sum end) zhjf,"
					+ " sum(case when t.ctype_id = '2001' then point_sum end) cxck,"
					+ " sum(case when t.ctype_id = '2002' then point_sum end) zgrz,"
					+ " sum(case when t.ctype_id = '2003' then point_sum end) zzgl,"
					+ " sum(case when t.ctype_id = '400' then point_sum end) sgtz,"
					+ " sum(case when t.ctype_id = '3001' then point_sum end) fjxf1,"
					+ " sum(case when t.ctype_id = '3002' then point_sum end) fjxf2,"
					+ " sum(case when t.ctype_id = '3003' then point_sum end) fjxf3,"
					+ " sum(case when t.ctype_id = '3004' then point_sum end) fjxf4,"
					+ " sum(case when t.ctype_id = '3005' then point_sum end) fjxf5,"
					+ " sum(case when t.ctype_id = '3006' then point_sum end) fjxf6,"
					+ " sum(case when t.ctype_id = '3007' then point_sum end) fjxf7,"
					+ " sum(case when t.ctype_id = '3008' then point_sum end) fjxf8"
					+ " from ("
					+ " select t.userid,t.name,"
					+ " case when t.sex = 'y' then '男' else '女'end sex,"
					+ " t.orgid,t.orgname,t.depid,t.depname,t.ctype_id,t.ctype_name,t.col_name,sum(point) point_sum"
					+ " from (select a.*, b.ctype_name, b.col_name"
					+ " from (select b.*,a.point,a.point_year,a.ctype_id,a.note, a.oper_date"
					+ " from t_ntmisc_credit a"
					+ " left join (select a.userid,a.name,a.orgid,a.sex,b.orgname,a.depid,c.depname"
					+ " from t_ntmisc_user a,t_ntmisc_org b,t_ntmisc_dept c"
					+ " where a.orgid = b.orgid and a.depid = c.depid) b"
					+ " on a.user_id = b.userid) a"
					+ " left join t_ntmisc_credittype b"
					+ " on a.ctype_id = b.ctype_id) t"
					+ " group by t.userid,t.name,t.sex,t.orgid, t.orgname,t.depid,t.depname,t.ctype_id,t.ctype_name,t.col_name"
					+ " ) t"
					+ " group by t.userid,t.name,t.sex,t.orgid,t.orgname,t.depid,t.depname) where 1 = 1";
			iu.putCondition(condition, "userid", tmd.get("userId"));
			try{
				logger.info("66666666666 orgId:"+orgId);
				if(orgId == null || "".equals(orgId)){//点击"用户管理"，初始查询
//					orgCurr = this.getUserOrg(currUser);
					orgCurr = mediumBus.getUserOrg(currUser);
					sqlStr = this.getSqlWithIn(sqlStr,orgCurr);
				}else{
					if(orgId.equals(tmd.get("depId"))){//机构号与部门号相同，查询当前机构所有员工
						sqlStr = this.getSqlWithIn(sqlStr,orgId);
					}else{//机构号与部门号不同，查询当前部门所有员工
						iu.putCondition(condition, "orgid", orgId);//机构号
						iu.putCondition(condition, "depid", tmd.get("depId"));//部门编号
					}
				}
			}catch(Exception e){}
//			logger.info("员工信息查询sqlStr："+sqlStr);
			tmd.put("count",this.count(condition, daoParent, sqlStr, 1));//总记录数入变量池
			iu.putCondition(condition, "start", tmd.get("start"));
			iu.putCondition(condition, "end", tmd.get("end"));
			this.queryAuto(ja, condition, sqlStr, daoParent, 2);
		
			
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
	
	/**
	 * 员工考核奖励信息管理
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void rpManage(JSONArray ja,LinkedHashMap<String, Object> condition,Map retMap,TransactionMapData tmd){
		String txCode = tmd.get("txCode")+"";
		logger.info("ParamBus->userManage txCode:" + txCode);
//		List<String> parmList = new ArrayList<String>();
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		iu.rmCondition(condition);
		String data = "";
		LinkedList<LinkedHashMap<String, Object>> condList = null;
		LinkedList<LinkedHashMap<String, Object>> condList2 = null;
		JSONArray jsonArray = null;
		switch(Integer.parseInt(txCode)){
		case 30051://新增
			data = tmd.get("data").toString();
			mediumBus.rpBat(retMap, data);
			break;
		case 30052://删除
			data = tmd.get("data").toString();
			condList = new LinkedList<LinkedHashMap<String,Object>>();
			jsonArray = JSONArray.parseArray(data);
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject json = jsonArray.getJSONObject(i);
				LinkedHashMap<String, Object> hashMap = new LinkedHashMap<String, Object>();
				hashMap.put("userId", json.getString("userId"));
				condList.add(hashMap);
			}
//			iu.putCondition(condition, "ssId", tmd.get("ssId"));
			sqlStr = "delete t_ntmisc_rphis t where t.user_id = ?";
			this.updateBat(condList, daoParent, retMap, sqlStr, 1);
			break;
		case 30053://查询
			iu.putCondition(condition, "rp_time", tmd.get("rpTimeQuery"));
			iu.putCondition(condition, "user_id", tmd.get("userIdQuery"));
//			sqlStr = "select a.name,b.*,c.asspt_name from t_ntmisc_user a,t_ntmisc_rphis b,t_ntmisc_asspt c "
//					+ " where a.userid = b.user_id and b.asspt_id = c.asspt_id";
			sqlStr = "select a.name,b.* from t_ntmisc_user a,t_ntmisc_rphis b where a.userid = b.user_id";
			tmd.put("count",this.count(condition, daoParent, sqlStr, 1));//总记录数入变量池
			iu.putCondition(condition, "start", tmd.get("start"));
			iu.putCondition(condition, "end", tmd.get("end"));
			this.queryAuto(ja, condition, sqlStr, daoParent, 2);
			break;
		}
	}
	
	/**
	 * 考核积分对照表
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void assptManage(JSONArray ja,LinkedHashMap<String, Object> condition,Map retMap,TransactionMapData tmd){
		String txCode = tmd.get("txCode")+"";
		logger.info("ParamBus->assptManage txCode:" + txCode);
//		List<String> parmList = new ArrayList<String>();
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		iu.rmCondition(condition);
		switch(Integer.parseInt(txCode)){
		case 30041://修改
			iu.putConditionAll(condition, "assptName", tmd.get("assptName"));
			iu.putConditionAll(condition, "point", tmd.get("point"));
			iu.putConditionAll(condition, "note", tmd.get("note"));
			iu.putConditionAll(condition, "cond", tmd.get("cond"));
			iu.putConditionAll(condition, "assptId", tmd.get("assptId"));
			sqlStr = "update t_ntmisc_asspt t set t.asspt_name=?,t.point=?,t.note=?,t.cond=? where t.asspt_id=?";		
			this.update(condition, daoParent, sqlStr, retMap);
			break;
		case 30042://查询
			sqlStr = "select * from t_ntmisc_asspt t";
			tmd.put("count",this.count(condition, daoParent, sqlStr, 1));//总记录数入变量池
			iu.putCondition(condition, "start", tmd.get("start"));
			iu.putCondition(condition, "end", tmd.get("end"));
			this.queryAuto(ja, condition, sqlStr, daoParent, 1);
			break;
		}
	}
	
	/**
	 * 行龄补贴管理
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void ssManage(JSONArray ja,LinkedHashMap<String, Object> condition,Map retMap,TransactionMapData tmd){
		String txCode = tmd.get("txCode")+"";
		logger.info("Parambus->userManage txCode:" + txCode);
//		List<String> parmList = new ArrayList<String>();
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		iu.rmCondition(condition);
		switch(Integer.parseInt(txCode)){
		case 30021://新增
			iu.putConditionAll(condition, "yearStart", tmd.get("yearStart"));
			iu.putConditionAll(condition, "yearEnd", tmd.get("yearEnd"));
			iu.putConditionAll(condition, "subsity", tmd.get("subsity"));
			iu.putConditionAll(condition, "note", tmd.get("note"));
			sqlStr = "insert into t_ntmisc_ss(ss_id,year_start,year_end,subsity,note) values(seq_ntmisc_ss.nextval,?,?,?,?)";
			this.update(condition, daoParent, sqlStr, retMap);
			break;
		case 30022://修改
			iu.putConditionAll(condition, "yearStart", tmd.get("yearStart"));
			iu.putConditionAll(condition, "yearEnd", tmd.get("yearEnd"));
			iu.putConditionAll(condition, "subsity", tmd.get("subsity"));
			iu.putConditionAll(condition, "note", tmd.get("note"));
			iu.putConditionAll(condition, "ssId", tmd.get("ssId"));
			sqlStr = "update t_ntmisc_ss t set t.year_start=?,t.year_end=?,t.subsity = ?,t.note=? where t.ss_id = ?";		
			this.update(condition, daoParent, sqlStr, retMap);
			break;
		case 30023://删除
			iu.putCondition(condition, "ssId", tmd.get("ssId"));
			sqlStr = "delete t_ntmisc_ss t where t.ss_id = ?";
			this.update(condition, daoParent, sqlStr, retMap);
			break;
		case 30024://查询
			sqlStr = "select * from t_ntmisc_ss t order by to_number(t.year_start)";
			tmd.put("count",this.count(condition, daoParent, sqlStr, 1));//总记录数入变量池
			iu.putCondition(condition, "start", tmd.get("start"));
			iu.putCondition(condition, "end", tmd.get("end"));
			this.queryAuto(ja, condition, sqlStr, daoParent, 1);
			break;
		}
	}
	
	/**
	 * 工资档次查询
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void wlQuery(JSONArray ja,LinkedHashMap<String, Object> condition,Map retMap,TransactionMapData tmd){
		sqlStr = "select * from t_ntmisc_wl t";
		iu.rmCondition(condition);
		iu.putCondition(condition, "wg_id", tmd.get("wgId"));
		tmd.tmdLogger();
		this.queryAuto(ja, condition, sqlStr, daoParent, 1);
	} 
	
	/**
	 * 工资等级查询（all）
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void wgQuery(JSONArray ja,LinkedHashMap<String, Object> condition,Map retMap,TransactionMapData tmd){
		sqlStr = "select distinct t.wg_value,t.wg_name from t_ntmisc_wl t  order by to_number(t.wg_value)";
		this.queryAuto(ja, condition, sqlStr, daoParent, 1);
	}
	
	/**
	 * 职务层级查询
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd 变量池
	 */
	public void dtQuery(JSONArray ja,LinkedHashMap<String, Object> condition,Map retMap,TransactionMapData tmd){
		String sqlStr = "select * from t_ntmisc_dt t";
		iu.rmCondition(condition);
		this.queryAuto(ja, condition, sqlStr,daoParent, 1);
	}
	
	/**
	 * 所有认证资格查询
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd 变量池
	 */
	public void ctQuery(JSONArray ja,LinkedHashMap<String, Object> condition,Map retMap,TransactionMapData tmd){
		String sqlStr = "select * from t_ntmisc_ct";
		iu.rmCondition(condition);
		this.queryAuto(ja, condition, sqlStr,daoParent, 1);
	}


	@Override
	public int getCount(HashMap<String,Object> condition,String tranCode) {
//		System.out.println("RoleBus getCount");
		String sqlStr = "select * from t_ntmisc_role";
//		return this.countExt(condition, roleDao,sqlStr);
		return this.count(condition, daoParent, sqlStr, 1);
	}
}
