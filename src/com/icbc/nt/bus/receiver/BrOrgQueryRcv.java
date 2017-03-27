/**
 * 获取机构所在分支行号
 */
package com.icbc.nt.bus.receiver;

import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.util.TransactionMapData;

public class BrOrgQueryRcv extends BusParent implements BusReceiver {

	/**
	 * 获取当前用户所在分支行号
	 * @param orgId
	 * @return
	 */
	public String getBrOrgId(String orgId){
		String orgRet = "";
		iu.rmCondition(condition);
		iu.putCondition(condition, "orgId", orgId);
		JSONArray ja = new JSONArray();
		sqlStr = "select porgid from t_ntmisc_org where orgid=?";
		logger.info("机构号："+orgId+",对应分支行号查询");
		iu.infoDbOper("机构号："+orgId+",对应分支行号查询", sqlStr, condition);
		this.queryManu(ja, condition, sqlStr, daoParent, 1);
		
//		logger.info("porgid ja:"+ja.toJSONString());
		if(ja.size() > 0){
			String pOrgId = ja.getJSONObject(0).getString("porgid");
			/*if("0310000000".equals(pOrgId.trim())){
				orgRet = orgId;
			}else if(!"".equals(pOrgId.trim())){
				orgRet = this.getBrOrgId(pOrgId);
			}else if("0".equals(pOrgId.trim())){
				orgRet = "0310000000";
			}*/
			if("0310000000".equals(pOrgId.trim())){
				orgRet = orgId;
			}else if("0".equals(pOrgId.trim())){
				orgRet = "0310000000";//重庆分行用户
			}else{
				orgRet = this.getBrOrgId(pOrgId);
			}
		}
		return orgRet;
	}
	@Override
	public void doWork(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub

	}
	
	public String doWork(String orgId) {
		return this.getBrOrgId(orgId);
	}
}
