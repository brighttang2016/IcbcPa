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
@Service
@Transactional
public class RoleBus extends BusParent{
	ResultSet rs = null;
	ResultSetMetaData rsmd = null;
	
	public void roleAllQuery(JSONArray ja,HashMap<String, Object> condition){
		String sqlStr = "select role_id,role_name from t_ntmisc_role";
		Object[] objArr = new Object[condition.size()];
//		roleDao.extQueryArr(ja, sqlStr, objArr);
		this.queryManu(ja, condition, sqlStr, daoParent, 1);
	}
	
	/**
	 * 批量增加记录
	 * @param condList 记录
	 * @param retMap 
	 * @param roleIdEtFlag 角色表role_id存在标志（区分add与update）
	 */
	public void roleAddBatch(final LinkedList<LinkedHashMap<String, Object>> condList,Map retMap,boolean roleIdEtFlag){
		logger.info("roleUpdateBatch  roleIdEtFlag："+roleIdEtFlag);
		int updFlag = 1;
		String sqlStr = "";
		if(roleIdEtFlag)//增加角色权限（修改角色）
			sqlStr = "insert into t_ntmisc_roleauth values(?,?)";
		else//增加角色权限（新增角色）
			sqlStr = "insert into t_ntmisc_roleauth values(SEQ_ntmisc_ROLEID.Currval,?)";//取SEQ_ntmisc_ROLEID.Currval
//		this.extUpdateBatch(condList, roleDao, retMap, sqlStr, updFlag);
		this.updateBat(condList, daoParent, retMap, sqlStr, updFlag);
		
	}

	/**
	 * @param retMap
	 * @param role_id 修改角色权限的角色id（新增为空，修改为当前所修改角色的的角色id）
	 * @param role_auth 角色权限
	 */
	public void roleAuthAdd(Map retMap,String role_id,String role_auth){
		boolean roleIdEtFlag = true;//role_id存在标识
		iu.rmCondition(condition);
		String[] roleAuthArr =  role_auth.split("\\|");
		LinkedList<LinkedHashMap<String, Object>> condList = new LinkedList<LinkedHashMap<String,Object>>();
		for(int i = 0;i < roleAuthArr.length;i++){
			LinkedHashMap<String, Object> rowMap = new LinkedHashMap<String, Object>();
			if(!roleAuthArr[i].trim().equals("")){
				if(role_id.equals(""))
					roleIdEtFlag = false;
				else
					rowMap.put("role_id", role_id);
				rowMap.put("authority_id", roleAuthArr[i]);
				System.out.println("i:"+i+"    "+roleAuthArr[i]);
				logger.info("角色权限：authority_id："+roleAuthArr[i]);
				condList.add(rowMap);
			}
		}
		this.roleAddBatch(condList, retMap,roleIdEtFlag);
		
	}
	
	/**
	 * 业务处理路由
	 * @param ja 明细数据
	 * @param condition sql条件
	 * @param tx_code 交易码
	 * @param retMap 返回map
	 */
	public void roleManage(JSONArray ja,LinkedHashMap<String, Object> condition,String tx_code,Map retMap){
		logger.info("业务处理路由->当前交易码  tx_code:"+tx_code);
		String sqlStr = "";
		int updFlag = 0;
		switch(Integer.parseInt(tx_code)){
		case 10021:
			updFlag = 1;
			retMap.put("operName", "新增角色");
			iu.rmCondition(condition);
			condition.put("role_name", tmd.get("role_name"));
			condition.put("role_desc", tmd.get("role_desc"));
			logger.info("roleManage condition:"+condition+"|"+tmd.get("role_auth").toString());
			sqlStr = "insert into t_ntmisc_role values(SEQ_ntmisc_ROLEID.NEXTVAL,?,?)";
//			this.updateExt(ja, condition, roleDao, retMap, sqlStr, updFlag);
			this.update(condition, daoParent, sqlStr,retMap);
			this.roleAuthAdd(retMap,"", tmd.get("role_auth").toString());
			break;
		case 10022:
			iu.rmCondition(condition);
			condition.put("role_name", tmd.get("role_name"));
			condition.put("role_desc", tmd.get("role_desc"));
			condition.put("role_id", tmd.get("role_id"));
			sqlStr = "update t_ntmisc_role t set t.role_name=?,t.role_desc = ? where t.role_id=?";
			this.update(condition, daoParent, sqlStr,retMap);//更新基本信息
			this.roleManage(ja, condition, "10025", retMap);//删除旧角色权限
			this.roleAuthAdd(retMap,tmd.get("role_id").toString(), tmd.get("role_auth").toString());//批量增加新角色权限
			break;
		case 10023:
			iu.rmCondition(condition);
			condition.put("role_id", tmd.get("role_id"));
			sqlStr = "select * from t_ntmisc_userrole t where t.role_id=?";
			this.queryManu(ja, condition, sqlStr, daoParent, 1);
			if(ja.size() > 0){
				retMap.put("errorCode", "01");
				retMap.put("errorMsg", "操作失败，角色用户不为空");
			}else{
				sqlStr = "delete t_ntmisc_role t where t.role_id=?";
				this.update(condition, daoParent, sqlStr,retMap);
				retMap.put("errorCode", "01");
				retMap.put("errorMsg", "操作成功！");
			}
			
			break;
		case 10024://角色查询（查询角色编码大于或等于当前用户所拥有的最小角色编码的角色）
			retMap.put("operName", "角色查询");
//			sqlStr = "select * from t_ntmisc_role order by role_id";
			sqlStr =  " select a.role_id,a.role_name,a.role_desc from t_ntmisc_role a, " 
					+  " (select min(role_id) role_id from t_ntmisc_userrole t where user_id = '"+tmd.get("userIdLogin")+"') b " 
					+  " where a.role_id >= b.role_id "  
					+  " order by role_id " ;
			iu.rmCondition(condition);
//			iu.putCondition(condition, "userid", tmd.get("userIdLogin"));
			iu.putCondition(condition, "role_name", tmd.get("role_name"));
			iu.putCondition(condition, "role_desc", tmd.get("role_desc"));
			tmd.put("count", this.count(condition, daoParent, sqlStr, 2));
			iu.putCondition(condition, "start", tmd.get("start"));
			iu.putCondition(condition, "end", tmd.get("end"));
			
			this.queryAuto(ja, condition, sqlStr, daoParent, 2);
			break;
		case 10025://角色权限删除
			iu.rmCondition(condition);
			iu.putCondition(condition, "role_id",tmd.get("role_id"));
			sqlStr = "delete from t_ntmisc_roleauth t where t.role_id = ?";
			this.update(condition, daoParent, sqlStr,retMap);
			break;
		}
		
	}

	@Override
	public int getCount(HashMap<String,Object> condition,String tranCode) {
		System.out.println("RoleBus getCount");
		iu.rmCondition(condition);
		String sqlStr = "select * from t_ntmisc_role";
//		return this.countExt(condition, roleDao,sqlStr);
		return this.count(condition, daoParent, sqlStr, 1);
	}
}
