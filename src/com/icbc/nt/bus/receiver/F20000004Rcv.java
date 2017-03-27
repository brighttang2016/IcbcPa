/**
 * 分支行考核部分总包导入结果查询 2016-01-11
 */
package com.icbc.nt.bus.receiver;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.bus.MediumBus;
import com.icbc.nt.util.TransactionMapData;

public class F20000004Rcv extends BusParent implements BusReceiver {
	@Autowired
	private MediumBus mediumBus;
	/**
	 * 绩效查询（新版）
	 * @param tmd
	 * @param ja
	 */
	public void fhzbDrQuery(TransactionMapData tmd,JSONArray ja){
		StringBuffer orgInBuf = new StringBuffer();//(分支行号1、分支行号2、分支行号3)
		String orgIdLogin = tmd.get("orgIdLogin").toString();//当前登录用户所在机构
		String zqCurr = tmd.get("zqCurr").toString();
		String brOrgId = tmd.get("brOrgId").toString();//当前登录用户所在分支行号
		if("0310000000".equals(orgIdLogin)){
			iu.rmCondition(condition);
			JSONArray brOrgJa = new JSONArray();
			sqlStr = "select * from t_ntmisc_org t where porgid = '0310000000'";
			this.queryAuto(brOrgJa, condition, sqlStr, daoParent, 1);
			for (int i = 0; i < brOrgJa.size(); i++) {
				JSONObject brOrgJson = brOrgJa.getJSONObject(i);
				String orgId = brOrgJson.getString("orgid");
				if(i == 0){
					orgInBuf.append("('"+orgId+"'");
				}else if(i == brOrgJa.size()-1){
					orgInBuf.append(",'"+orgId+"')");
				}else{
					orgInBuf.append(",'"+orgId+"'");
				}
			}
			logger.debug("当前登录用户所在机构 orgIdLogin："+orgIdLogin+"|orgInBuf："+orgInBuf.toString());
		}else{
			orgInBuf.append("("+brOrgId+")") ;
		}
		iu.rmCondition(condition);
		iu.putCondition(condition, "zq", tmd.get("zqQuery"));
		sqlStr =  " select t.orgid,t.orgid depid,t.orgname,t.zq, " 
				+  " to_char(sum(case when t.nr_id='1' then t.zb else '0' end),'999,999,999,990.90') as zb_gylz, " 
				+  " to_char(sum(case when t.nr_id='2' then t.zb else '0' end),'999,999,999,990.90') as zb_khjllz, " 
				+  " to_char(sum(case when t.nr_id='5' then t.zb else '0' end),'999,999,999,990.90') as zb_qyyx " 
				+  " from (select a.orgid,a.orgname,b.nr_id,b.zq,b.zb,c.nr_name from t_ntmisc_org a,t_ntmisc_khzb b,t_ntmisc_khnr c " 
				+  " where a.orgid = b.orgid and b.nr_id = c.nr_id) t "
				+  " where t.orgid in "
				+  orgInBuf.toString()
				+  " group by t.orgid,t.orgname,t.zq  "
				+  " order by t.zq desc " ;
		sqlStr = mediumBus.putOrgCond(sqlStr, tmd, condition);
		tmd.put("count",this.count(condition, daoParent, sqlStr, 1));//总记录数入变量池
		iu.putCondition(condition, "start", tmd.get("start"));
		iu.putCondition(condition, "end", tmd.get("end"));
		logger.info("分支行考核部分总包导入结果查询");
		iu.infoDbOper("分支行考核部分总包导入结果查询", sqlStr, condition);
		this.queryAuto(ja, condition, sqlStr, daoParent, 1);
		logger.debug("分支行考核部分总包导入结果查询,查询结果,ja："+ja.toJSONString());
	}
	@Override
	public void doWork(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub
		this.fhzbDrQuery(tmd, ja);
	}
}
