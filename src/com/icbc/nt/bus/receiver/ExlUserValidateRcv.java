/**
 * 总包查询命令接收者
 */
package com.icbc.nt.bus.receiver;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.bus.MediumBus;
import com.icbc.nt.util.TransactionMapData;

public class ExlUserValidateRcv extends BusParent implements BusReceiver{
	@Autowired
	MediumBus mediumBus;
	public void doWork(JSONArray ja,
			LinkedHashMap<String, Object> condition, Map retMap,
			TransactionMapData tmd){
		ja = new JSONArray();
		boolean excFlag = false;
		tmd.put("excFlag", excFlag);
		String userId = tmd.get("exlUserId").toString();
		String userIdLogin = tmd.get("userId").toString();
		if("".equals(userId.trim())){
			excFlag = true;
			tmd.put("errorCode", "11");
			tmd.put("errorMsg", "人力资源编码存在空数据,导入失败!");
			tmd.put("finish", "1");
			logger.info("人力资源编码存在空数据,导入失败！");
			tmd.put("excFlag", excFlag);
			return;
		}
		
		String orgId = mediumBus.getUserOrg(userIdLogin);
		String orgIn = mediumBus.getOrgIn(orgId);
		iu.rmCondition(condition);
		iu.putCondition(condition, "userId", userId);
		ja.clear();
		sqlStr = " select * from (select * from t_ntmisc_user where orgid not in "
				+ orgIn
				+ " ) t where t.userid=? ";
		this.queryManu(ja, condition, sqlStr, daoParent, 1);
		if(ja.size() > 1){
			excFlag = true;
			tmd.put("errorCode", "11");
			tmd.put("finish", "1");
			tmd.put("errorMsg", "存在非本机构人力资源编码:"+userId+",导入失败！");
			logger.info("存在非本机构人力资源编码:"+userId+",导入失败！");
			tmd.put("excFlag", excFlag);
			return;
		}
		
		ja.clear();
		sqlStr = " select * from t_ntmisc_user t where t.userid=? ";
		this.queryManu(ja, condition, sqlStr, daoParent, 1);
		if(ja.size() < 1){
			excFlag = true;
			tmd.put("errorCode", "11");
			tmd.put("finish", "1");
			tmd.put("errorMsg", "存在未注册的人力资源编码:"+userId+",导入失败！");
			logger.info("存在未注册的人力资源编码:"+userId+",导入失败！");
			tmd.put("excFlag", excFlag);
			return;
		}
	}
}
