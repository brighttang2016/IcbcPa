/**
 * 职务层级设置业务处理逻辑
 * 2015-11-18
 * brighttang
 */
package com.icbc.nt.bus.receiver;

import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.util.TransactionMapData;

public class ZwcjRcv extends BusParent implements BusReceiver{

	/**
	 * 子节点查询
	 * @param ja
	 * @param pid 父节点id
	 * @param pname 父节点名称
	 */
	public void subJobQuery(JSONArray ja,String pid,String pname){
		logger.info("subJobQuery pid:"+pid+"|pname:"+pname);
		JSONArray jaTemp = new JSONArray();
		iu.rmCondition(condition);
		iu.putCondition(condition, "pid", pid);
		sql = "select t.jb_id menuid,t.jb_name menuname,t.jb_idp pid,t.ct_id,t.dt_id,t.m_grade,t.s_grade,t.s_level,t.seq_num from t_ntmisc_job t where t.jb_idp = ? order by to_number(t.jb_id)";
		this.queryManu(jaTemp, condition, sql, daoParent, 1);
		for (int i = 0; i < jaTemp.size(); i++) {
			JSONObject json = jaTemp.getJSONObject(i);
			json.put("pid", pid);
			json.put("pname", pname);
			this.hasSubItem(json);
			ja.add(json);
		}
	}
	
	/**叶子节点判断
	 * @param json 当前节点数据
	 */
	public boolean hasSubItem(JSONObject json){
		boolean boolRet = false;
		JSONArray jaJobTemp = new JSONArray();
		String menuId = json.getString("menuid");
		iu.rmCondition(condition);
		iu.putCondition(condition, "pid", menuId);
		sql = "select t.jb_id menuid,t.jb_name menuname,t.jb_idp pid,t.ct_id,t.dt_id,t.m_grade,t.s_grade,t.s_level,t.seq_num from t_ntmisc_job t where t.jb_idp = ? order by t.seq_num";
		this.queryManu(jaJobTemp, condition, sql, daoParent, 1);
		logger.info("jaJobTemp.size():"+jaJobTemp.size()+"|jaOrgTemp.size():"+jaJobTemp.size());
		if(jaJobTemp.size() == 0){
			json.put("leaf", "true");
			json.put("leaftag", "1");
			boolRet = false;
		}
		else{
			json.put("leaf", "false");
			json.put("leaftag", "0");
			boolRet = true;
		}
		return boolRet;
	}
	
	/**
	 * 岗位管理（岗位增、删、改、查）
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void jobManage(JSONArray ja,LinkedHashMap<String, Object> condition,Map retMap,TransactionMapData tmd){
		String txCode = tmd.get("txCode")+"";
		logger.info("业务处理路由->当前交易码  tx_code:"+txCode);
		String sqlStr = "";
		int updFlag = 0;
		iu.rmCondition(condition);
		switch(Integer.parseInt(txCode)){
		case 10041://新增
			logger.info("**********岗位新增");
			sqlStr = "insert into t_ntmisc_job(jb_id,jb_name,jb_idp,ct_id,dt_id,m_grade,s_grade,s_level,seq_num) values(seq_ntmisc_job.nextval,?,?,?,?,?,?,?,?)";
//			String jbId = this.jbIdCreate(tmd.get("pId").toString());
//			iu.rmCondition(condition);
//			iu.putCondition(condition, "jbId", jbId);
			iu.putConditionAll(condition, "jbName", tmd.get("menuName"));
			iu.putConditionAll(condition, "jdIdp", tmd.get("pId"));
			iu.putConditionAll(condition, "ctId", tmd.get("ctId"));
			iu.putConditionAll(condition, "dtId", tmd.get("dtId"));
			iu.putConditionAll(condition, "mGrade", tmd.get("mGrade"));
			iu.putConditionAll(condition, "sGrade", tmd.get("sGrade"));
			iu.putConditionAll(condition, "sLevel",tmd.get("sLevel"));
			iu.putConditionAll(condition, "seqNum",tmd.get("seqNum"));
//			iu.putCondition(condition, "seqNum",tmd.get("seqNum"));
			this.update(condition, daoParent, sqlStr, retMap);
			break;
		case 10042://修改
			logger.info("**********机构部门修改");
			iu.putConditionAll(condition, "jbName", tmd.get("menuName"));
			iu.putConditionAll(condition, "ctId", tmd.get("ctId"));
			iu.putConditionAll(condition, "dtId", tmd.get("dtId"));
			iu.putConditionAll(condition, "mGrade", tmd.get("mGrade"));
			iu.putConditionAll(condition, "sGrade", tmd.get("sGrade"));
			iu.putConditionAll(condition, "sLevel", tmd.get("sLevel"));
			iu.putConditionAll(condition, "seqNum",tmd.get("seqNum"));
			iu.putCondition(condition, "menuId", tmd.get("menuId"));
			sqlStr = "update t_ntmisc_job t set t.jb_name = ?,t.ct_id = ?,t.dt_id=?,t.m_grade=?,t.s_grade=?,t.s_level=?,t.seq_num=? where t.jb_id=?";
			this.update(condition, daoParent, sqlStr, retMap);
			break;
		case 10043://删除
			logger.info("**********岗位删除");
			iu.putCondition(condition, "jbId", tmd.get("menuId"));
			sqlStr = "delete t_ntmisc_job where jb_id=?";
			this.update(condition, daoParent, sqlStr,retMap);
			break;
		case 10044://查询
			retMap.put("operName", "岗位层级查询");
			if("-1".equals(tmd.get("menuTreeId"))){//初始查询
//				sqlStr = "select orgid menuid,orgname menuname from t_ntmisc_org t where t.orgid = ? ";
				iu.rmCondition(condition);
				if(tmd.get("pId") == null){//岗位层级管理模块初始查询
					iu.putCondition(condition, "jb_idp", "-1");//查询所有岗位层级
				}else{//用户管理中查询岗位类别下属岗位层级
					iu.putCondition(condition, "jb_idp", tmd.get("pId").toString());
				}
				
				sqlStr = "select t.jb_id menuid,t.jb_name menuname,t.jb_idp pid,t.ct_id,t.dt_id,t.m_grade,t.s_grade,t.s_level,t.seq_num from t_ntmisc_job t where t.jb_idp = ? order by to_number(t.jb_id)";
				this.queryManu(ja, condition, sqlStr, daoParent, 1);
				for (int i = 0; i < ja.size(); i++) {
					JSONObject json = ja.getJSONObject(i);
//					json.put("pid", "-1");
//					json.put("pname", "根节点");
					this.hasSubItem(json);
				}
			}else{
				ja.clear();
				String menuId ="";
				String menuName = "";
				menuId = tmd.get("menuTreeId").toString();
				menuName = tmd.get("menuName").toString();
				this.subJobQuery(ja,menuId,menuName);
			}
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
	public void doWork(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub
		System.out.println("ZwcjRcv doWork");
		this.jobManage(ja, condition, retMap, tmd);
	}
}
