/**
 * 权重区间管理 2015-12-21
 */
package com.icbc.nt.bus.receiver;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.bus.MediumBus;
import com.icbc.nt.bus.dispatcher.BusDispatcherImpl;
import com.icbc.nt.util.TransactionMapData;

public class QzqjRcv extends BusParent implements BusReceiver {
	@Autowired
	private MediumBus mediumBus;
	@Autowired
	private BusDispatcherImpl busDispatcherImpl;
	@Override
	public void doWork(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub
		int txCode = Integer.parseInt(tmd.get("txCode").toString());
		switch(txCode){
		/*case 10000073://add
			break;*/
//		case 10000074://del
//			break;
		case 10000075://del
			String data = tmd.get("data").toString();
			LinkedList<LinkedHashMap<String,Object>> condList = new LinkedList<LinkedHashMap<String,Object>>();
//			condList = new LinkedList<LinkedHashMap<String,Object>>();
			JSONArray jsonArray = JSONArray.parseArray(data);
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject json = jsonArray.getJSONObject(i);
				LinkedHashMap<String, Object> hashMap = new LinkedHashMap<String, Object>();
				hashMap.put("orgid", json.getString("orgId"));
				hashMap.put("jbjx_pid", json.getString("jbjx_pid"));
				hashMap.put("khfw", json.getString("khfw"));
				condList.add(hashMap);
			}
			sqlStr = "delete t_ntmisc_khqj t where  t.orgid = ? and t.jbjx_pid=? and t.khfw=?";
			logger.info("执行sql,删除考核区间,sqlStr:"+sqlStr+"condList:"+condList.toString());
			this.updateBat(condList, daoParent, retMap, sqlStr, 1);
			break;
		case 10000076://query
			String orgCurr = "";//登录用户所在orgid
			String orgId = null;//查询区orgid
//			String userId = null;//查询区userid
			String  currUser = tmd.get("currUser").toString();
			String orgIn = "";
			if(tmd.get("orgId") != null)
				orgId = tmd.get("orgId").toString();
//			if(tmd.get("userId") != null)
//				userId = tmd.get("userId").toString();
			logger.info("查询所有用户，机构根 orgId:"+orgId);
			try{
				if(tmd.get("currUser") != null){
					orgCurr = mediumBus.getUserOrg(tmd.get("currUser").toString());
					orgIn = mediumBus.getOrgIn(orgCurr);
				}
			}catch(Exception e){
				logger.error("用户管理：获取当前用户所在机构号失败,currUser:"+currUser+"----orgIn:"+orgIn);
			}
			ja.clear();
			logger.info("1111111111111111");
			iu.rmCondition(condition);
			logger.info("2222222222222");
			//初始sql
			sqlStr =  " select t.*,a.orgname  from t_ntmisc_khqj t,t_ntmisc_org a  where t.orgid = a.orgid and t.orgid in  "
					+  orgIn ;
//			iu.putCondition(condition, "userid", tmd.get("userId"));
			sqlStr = mediumBus.putOrgCond(sqlStr, tmd, condition);
			logger.info("3333333333");
			logger.info("执行sql,权限区间查询，sqlStr："+sqlStr+"---------condition:"+condition);
			tmd.put("count",this.count(condition, daoParent, sqlStr, 1));//总记录数入变量池
			iu.putCondition(condition, "start", tmd.get("start"));
			iu.putCondition(condition, "end", tmd.get("end"));
			logger.info(tmd.get("start")+"|"+tmd.get("end"));
			this.queryAuto(ja, condition, sqlStr, daoParent, 2);
			break;
		}
	}
}
