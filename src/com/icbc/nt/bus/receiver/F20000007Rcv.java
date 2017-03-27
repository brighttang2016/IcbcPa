/**
 * 绩效计算（新版）业务逻辑
 */
package com.icbc.nt.bus.receiver;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.bus.dispatcher.BusDispatcherImpl;
import com.icbc.nt.util.TransactionMapData;

public class F20000007Rcv extends BusParent implements BusReceiver {

	@Autowired
	private BusDispatcherImpl busDispatcherImpl;
	/**
	 * 网点手动分配总包初始化
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void zbWdsdfpInit(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd){
		if("1".equals(tmd.get("breakFlag"))){//流程中断标识
			return;
		}
		ja.clear();
		String orgIdIn = tmd.get("orgIdIn").toString();
		String zqCurr = tmd.get("zqCurr").toString();
		String brOrgId = tmd.get("brOrgId").toString();
		JSONArray zbWdJa = new JSONArray();
		LinkedList<LinkedHashMap<String, Object>> condList = new LinkedList<LinkedHashMap<String,Object>>();
		LinkedList<LinkedHashMap<String, Object>> condUpdList = new LinkedList<LinkedHashMap<String,Object>>();
		//查询总包基础数据表
		iu.rmCondition(condition);
		iu.putCondition(condition, "zq", zqCurr);
		sqlStr =  "  select a.nr_id,a.zq,b.orgid ,sum(jx) jx_wdsdfp from t_ntmisc_userjxn a, " 
				+  " (select a.userid,a.name,a.orgid,a.depid,a.jbjx_id,b.orgname,c.depname,d.jbjx_pid,d.jbjx_name,d.jbjx_pname  from t_ntmisc_user a,t_ntmisc_org b,t_ntmisc_dept c,t_ntmisc_jobjx d " 
				+  " where a.orgid = b.orgid and a.depid = c.depid and a.jbjx_id = d.jbjx_id) b " 
				+  " where a.userid = b.userid and a.nr_id ='5' and b.jbjx_pid='1' and a.zq=? and b.orgid in"//a.nr_id ='5' and b.jbjx_pid='1':柜员的全员营销奖励绩效
				+   orgIdIn
				+  " group by a.nr_id,a.zq,b.orgid " ;
		logger.info("网点手动分配总包初始化查询");
		iu.infoDbOper("网点手动分配总包初始化查询", sqlStr, condition);
		this.queryManu(ja, condition, sqlStr, daoParent, 1);
		for (int i = 0; i < ja.size(); i++) {
			LinkedHashMap<String, Object> condMap = new LinkedHashMap<String, Object>();
			LinkedHashMap<String, Object> condUpdMap = new LinkedHashMap<String, Object>();
			JSONObject json = ja.getJSONObject(i);
			condMap.put("orgid", json.getString("orgid"));
			condMap.put("nr_id", "4");//网点手动分配绩效
			condMap.put("zq", zqCurr);
			condMap.put("zb",json.getString("jx_wdsdfp"));
			
			condUpdMap.put("orgid", json.getString("orgid"));
			condUpdMap.put("nr_id", "4");//网点手动分配绩效
			condUpdMap.put("zq", zqCurr);
			
			condList.add(condMap);
			condUpdList.add(condUpdMap);
		}
		sqlStr = "delete t_ntmisc_khzb where orgid=? and nr_id=? and zq=?";
		logger.info("删除当前手动分配总包");
		iu.infoDbOper("删除当前手动分配总包", sqlStr, condUpdList);
		this.updateBat(condUpdList, daoParent, retMap, sqlStr, 1);
		sqlStr = "insert into t_ntmisc_khzb(orgid,nr_id,zq,zb) values(?,?,?,?)";
		logger.info("新增当前手动分配总包");
		iu.infoDbOper("新增当前手动分配总包", sqlStr, condList);
		this.updateBat(condList, daoParent, retMap, sqlStr, 1);
	}
	
	public boolean zbblIsValid(TransactionMapData tmd){
		boolean isValid = true;
		
		/*float jxGyYwlSum = Float.parseFloat(tmd.get("jxGyYwlSum").toString());
		float jxKhjlYwlSum = Float.parseFloat(tmd.get("jxKhjlYwlSum").toString());
		float wdggSum = Float.parseFloat(tmd.get("wdggSum").toString());
		float qyyxSum = Float.parseFloat(tmd.get("qyyxSum").toString());
		float jxZhSum = Float.parseFloat(tmd.get("jxZhSum").toString());*/
		float jxGyYwlSum = 0;//支行所有柜员业务量及风险质量绩效汇总
		float jxKhjlYwlSum = 0;//客户经理岗位履职总包
		float wdggSum = 0;//网点挂钩总包
		float qyyxSum = 0;//全员营销总包
		float jxZhSum = 0;//支行所有绩效汇总
		
		try {
			logger.info("支行所有柜员业务量及风险质量绩效汇总"+tmd.get("jxGyYwlSum"));
			jxGyYwlSum = Float.parseFloat(tmd.get("jxGyYwlSum").toString());
		} catch (Exception e) {
			iu.infoPrgsIntrpt(tmd, "数据转换失败:支行所有柜员业务量及风险质量绩效汇总");
		}
		try {
			logger.info("客户经理岗位履职总包"+tmd.get("jxKhjlYwlSum"));
			jxKhjlYwlSum = Float.parseFloat(tmd.get("jxKhjlYwlSum").toString());
		} catch (Exception e) {
			iu.infoPrgsIntrpt(tmd, "数据转换失败:客户经理岗位履职总包");
		}
		try {
			logger.info("网点挂钩总包"+tmd.get("wdggSum"));
			wdggSum = Float.parseFloat(tmd.get("wdggSum").toString());
		} catch (Exception e) {
			iu.infoPrgsIntrpt(tmd, "数据转换失败:网点挂钩总包");
		}
		try {
			logger.info("全员营销总包"+tmd.get("qyyxSum"));
			qyyxSum = Float.parseFloat(tmd.get("qyyxSum").toString());
		} catch (Exception e) {
			iu.infoPrgsIntrpt(tmd, "全员营销总包");
		}
		try {
			logger.info("支行所有绩效汇总"+tmd.get("jxZhSum"));
			jxZhSum = Float.parseFloat(tmd.get("jxZhSum").toString());
		} catch (Exception e) {
			iu.infoPrgsIntrpt(tmd, "支行所有绩效汇总");
		}
		
		float bl1 = iu.getFloatDecimal(jxGyYwlSum/jxZhSum+"", 2);
		float bl2 = iu.getFloatDecimal(jxKhjlYwlSum/jxZhSum+"", 2);
		float bl3 = iu.getFloatDecimal(wdggSum/jxZhSum+"", 2);
		float bl4 = iu.getFloatDecimal(qyyxSum/jxZhSum+"", 2);
		logger.info("jxGyYwlSum:"+jxGyYwlSum+"|"+"jxKhjlYwlSum:"+jxKhjlYwlSum+"|"+"wdggSum:"+wdggSum+"|"+"qyyxSum:"+qyyxSum+"|"+"jxZhSum:"+jxZhSum);
		logger.info("bl1:"+bl1+"|"+"bl2:"+bl2+"|"+"bl3:"+bl3+"|"+"bl4:"+bl4);
		//查询考核比例设置
		iu.rmCondition(condition);
		JSONArray khblJa = new JSONArray();
		sqlStr = "select a.*,b.nr_name from t_ntmisc_khbl a,t_ntmisc_khnr b  where a.nr_id = b.nr_id";
		this.queryManu(khblJa, condition, sqlStr, daoParent, 1);
		for (int i = 0; i < khblJa.size(); i++) {
			JSONObject khblJson = khblJa.getJSONObject(i);
			float blCalc = 0;//计算得出的总包占比
			int nrId = khblJson.getIntValue("nr_id");
			float min = khblJson.getFloatValue("min");
			float max = khblJson.getFloatValue("max");
			String nrName = khblJson.getString("nr_name");//考核内容名
			logger.info("nrName"+nrName+"nrId:"+nrId+"|"+"min:"+min+"|"+"max:"+max);
			switch(nrId){
			case 1:
				blCalc = bl1;
				break;
			case 2:
				blCalc = bl2;
				break;
			case 3:
				blCalc = bl3;
				break;
			case 5:
				blCalc = bl4;
				break;
			}
			if(blCalc < min || blCalc > max){
//				iu.setProgress(tmd, "11", nrName+"总包占比:"+blCalc+",设定区间:["+min+","+max+"],计算失败", "0");
				iu.infoPrgsIntrpt(tmd, nrName+"总包占比:"+blCalc+",设定区间:["+min+","+max+"],计算失败");
				isValid = false;
				break;
			}
		}
		return isValid;
	}
	
	/**
	 * 绩效计算
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void jxInit(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd){
		if("1".equals(tmd.get("breakFlag"))){
			return;
		}
		LinkedList<LinkedHashMap<String, Object>> condList = new LinkedList<LinkedHashMap<String,Object>>();
		float jxGyYwlSum = 0;//支行所有柜员业务量及风险质量绩效汇总
		float jxKhjlYwlSum = 0;//客户经理岗位履职总包
		float wdggSum = 0;//网点挂钩总包
		float qyyxSum = 0;//全员营销总包
		float jxZhSum = 0;//支行所有绩效汇总
		boolean dataValid = true;//基础数据合法性标识
		ja.clear();
		String zqCurr = tmd.get("zqCurr").toString();
		sqlStr = " select a.* ,b.orgid from t_ntmisc_userjxn a ,t_ntmisc_user b "
				+ " where a.userid = b.userid and  a.nr_id <>'4' and a.zq=? and b.orgid in "
				+ tmd.get("orgIdIn");
		logger.info("员工绩效计算，用户初始考核绩效查询");
		iu.infoDbOper("员工绩效计算，用户初始考核绩效查询", sqlStr, condition);
		this.queryManu(ja, condition, sqlStr, daoParent, 1);
		for (int i = 0; i < ja.size(); i++) {
			LinkedHashMap<String, Object> condMap = new LinkedHashMap<String, Object>();
			JSONObject json = ja.getJSONObject(i);
			String userId = json.getString("userid");
			String nr_id = json.getString("nr_id");
			try {
				float point = json.getFloatValue("point");
				float pointSum = json.getFloatValue("point_sum");
				float zb = json.getFloatValue("zb");
				float jx = iu.getFloatDecimal(point/pointSum*zb+"", 2);//根据个人得分计算各个板块绩效
				condMap.put("jx", jx+"");
				condMap.put("userId", userId);
				condMap.put("nrId", nr_id);
				condMap.put("zq",zqCurr);
				condList.add(condMap);
//				logger.info("condMap:"+condMap);
				switch(Integer.parseInt(nr_id)){
				case 1:
					jxGyYwlSum += jx;
					tmd.put("jxGyYwlSum", jxGyYwlSum);
					break;
				case 2:
					jxKhjlYwlSum += jx;
					tmd.put("jxKhjlYwlSum", jxKhjlYwlSum);
					break;
				case 3:
					wdggSum += jx;
					tmd.put("wdggSum", wdggSum);
					break;
				case 5:
					qyyxSum += jx;
					tmd.put("qyyxSum", qyyxSum);
					break;
				}
				jxZhSum += jx;
				tmd.put("jxZhSum", jxZhSum);
//				logger.info("jxGyYwlSum:"+jxGyYwlSum+"|"+"jxKhjlYwlSum:"+jxKhjlYwlSum+"|"+"wdggSum:"+wdggSum+"|"+"qyyxSum:"+qyyxSum+"|"+"jxZhSum:"+jxZhSum);
			} catch (Exception e) {
				dataValid = false;//数据不合法
				logger.error("数据不合法");
			}
		}
		if(dataValid){
			if(this.zbblIsValid(tmd)){
				iu.setProgress(tmd, "11", "各板块绩效总包占比校验成功", "1");
				sqlStr = "update t_ntmisc_userjxn t set t.jx=? where t.userid=? and t.nr_id=? and t.zq=?";
				logger.info("用户各板块最终绩效初始化,sqlStr:"+sqlStr+"|condition:"+condition.toString());
				iu.infoDbOper("用户各板块最终绩效初始化", sqlStr, condList);
				this.updateBat(condList, daoParent, retMap, sqlStr, 1);
				iu.setProgress(tmd, "10", "绩效计算完成", "1");
				logger.info("绩效计算完成");
			}
		}else{
			iu.infoPrgsIntrpt(tmd, "基础数据异常,计算失败");
		}
	}
	
	/**
	 * 网点自由考核总包初始化（网点自由考核）
	 * @param zfZhJa
	 * @param pointJa
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void zbInitWd(JSONArray pointJa , LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd){
		iu.setProgress(tmd, "11", "网点考核总包初始化", "0.4");
		String orgIdIn = tmd.get("orgIdIn").toString();
		String zqCurr = tmd.get("zqCurr").toString();
		String brOrgId = tmd.get("brOrgId").toString();
		JSONArray zbWdJa = new JSONArray();
		LinkedList<LinkedHashMap<String, Object>> condList = new LinkedList<LinkedHashMap<String,Object>>();
		//查询总包基础数据表
		iu.rmCondition(condition);
		iu.putCondition(condition, "zq", zqCurr);
		sqlStr =  " select a.* from t_ntmisc_khzb a,t_ntmisc_khnr b where zq = ? and a.nr_id = b.nr_id and b.khcc='网点'" ;
//		logger.info("执行sql,sqlStr:"+sqlStr+"|condition:"+condition.toString());
		logger.info("查询总包基础数据表");
		iu.infoDbOper("查询总包基础数据表", sqlStr, condition);
		this.queryManu(zbWdJa, condition, sqlStr, daoParent, 1);//zbZhJa:主要输出项：机构号（此处为：网点号或者是分支行号、考核内容id、考核周期、总包）
		
		//遍历当前用户绩效表
		for (int i = 0; i < pointJa.size(); i++) {
			JSONObject jsonTemp = pointJa.getJSONObject(i);
//			logger.info("jsonTemp:"+jsonTemp.toJSONString());
			String userId = jsonTemp.getString("userid");
			String nrIdUser = jsonTemp.getString("nr_id");//用户考核内容编号
			String point = jsonTemp.getString("point");//对应考核内容积分
			String orgId = jsonTemp.getString("orgid");
//			String brOrgIdTemp = busDispatcherImpl.brOrgQuery(orgId);//用户所在分支行号
			for (int j = 0; j < zbWdJa.size(); j++) {
				JSONObject jsonZb = zbWdJa.getJSONObject(j);
				String nrIdZb= jsonZb.getString("nr_id");//支行考核内容编号
				String orgIdZb = jsonZb.getString("orgid");//机构编号
				String zb = jsonZb.getString("zb");//分支行考核内容编号对应总分
				if(nrIdUser.equals(nrIdZb) && orgId.equals(orgIdZb)){
					LinkedHashMap<String, Object> condMap = new LinkedHashMap<String, Object>();
					condMap.put("zb", zb);
					condMap.put("userId", userId);
					condMap.put("nrId", nrIdZb);
					condMap.put("zq", zqCurr);
					condList.add(condMap);
				}
			}
		}
		sqlStr = "update t_ntmisc_userjxn t set t.zb = ? where t.userid=? and t.nr_id=? and t.zq=?";
		logger.info("更新用户绩效表-网点自由考核总包");
		iu.infoDbOper("更新用户绩效表-网点自由考核总包", sqlStr, condList);
		this.updateBat(condList, daoParent, retMap, sqlStr, 1);
		
		//未初始化考核总包查询(存在未初始化的考核总包，拒绝计算绩效)
		JSONArray unInitJa = new JSONArray();
		iu.rmCondition(condition);
		iu.putCondition(condition, "zq", zqCurr);
		sqlStr = " select a.userid,a.nr_id,c.nr_name,b.orgid,b.orgname from t_ntmisc_userjxn a,(select a.userid,a.name,a.orgid,a.depid,a.jbjx_id,b.orgname,c.depname from t_ntmisc_user a,t_ntmisc_org b,t_ntmisc_dept c "
				+ " where a.orgid = b.orgid and a.depid = c.depid) b,t_ntmisc_khnr c "
				+ " where a.userid = b.userid and a.nr_id = c.nr_id and a.nr_id <>'4' and a.zq = ? and a.zb is null and b.orgid in "
				+ orgIdIn;
		this.queryManu(unInitJa, condition, sqlStr, daoParent, 1);
		StringBuffer bufMsg = new StringBuffer();
		for (int i = 0; i < unInitJa.size(); i++) {
			JSONObject unInitJson = unInitJa.getJSONObject(i);
			String userId = unInitJson.getString("userid");
			String orgId = unInitJson.getString("orgid");
			String orgName = unInitJson.getString("orgname");
			String nrName = unInitJson.getString("nr_name");
			bufMsg.append("用户("+userId+"|"+orgName+"("+orgId+")"+nrName+"总包缺失<br/>");
		}
		if(unInitJa.size() > 0){
			iu.infoPrgsIntrpt(tmd, bufMsg.toString());
		}
		
	}
	
	/**
	 * 支行考核总包初始化（业务量及风险质量、基础任务及管理营销考核、全员营销奖励考核）
	 * @param zfZhJa
	 * @param pointJa
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void zbInitZh(JSONArray pointJa , LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd){
//		iu.setProgress(tmd, "11", "支行考核总包初始化", "0.3");
		String orgIdIn = tmd.get("orgIdIn").toString();
		String zqCurr = tmd.get("zqCurr").toString();
		String brOrgId = tmd.get("brOrgId").toString();
		JSONArray zbZhJa = new JSONArray();
		LinkedList<LinkedHashMap<String, Object>> condList = new LinkedList<LinkedHashMap<String,Object>>();
		//查询当前支行考核总包基础数据表
		iu.rmCondition(condition);
		iu.putCondition(condition, "zq", zqCurr);
		iu.putCondition(condition, "orgid", brOrgId);
		sqlStr =  " select a.* from t_ntmisc_khzb a,t_ntmisc_khnr b where zq = ? and a.nr_id = b.nr_id and b.khcc='支行' and orgid =  ? " ;
		logger.info("支行考核总包初始化");
		iu.infoDbOper("支行考核总包初始化", sqlStr, condition);
//		logger.info("执行sql,sqlStr:"+sqlStr+"|condition:"+condition.toString());
		this.queryManu(zbZhJa, condition, sqlStr, daoParent, 1);//zbZhJa:主要输出项：机构号（此处为：网点号或者是分支行号、考核内容id、考核周期、总包）
		
		//遍历当期所有考核用户
		for (int i = 0; i < pointJa.size(); i++) {
			JSONObject jsonTemp = pointJa.getJSONObject(i);
//			logger.info("jsonTemp:"+jsonTemp.toJSONString());
			String userId = jsonTemp.getString("userid");
			String nrIdUser = jsonTemp.getString("nr_id");//当前遍历用户考核内容编号
			String point = jsonTemp.getString("point");//对应考核内容积分
			String orgId = jsonTemp.getString("orgid");
			String brOrgIdTemp = busDispatcherImpl.brOrgQuery(orgId);//用户所在分支行号
			if(brOrgIdTemp.equals(brOrgId)){//遍历用户为当前分支行用户
//				logger.info("分支行用户jsonTemp："+jsonTemp);
				for (int j = 0; j < zbZhJa.size(); j++) {
					LinkedHashMap<String, Object> condMap = new LinkedHashMap<String, Object>();
					JSONObject jsonZb = zbZhJa.getJSONObject(j);
					String nrIdZb= jsonZb.getString("nr_id");//支行考核内容编号
					if(nrIdZb.equals(nrIdUser)){//当前遍历用户考核内容id=当前分支行总包表中对应考核内容id
						String zb = jsonZb.getString("zb");//分支行考核内容编号对应总分
						condMap.put("zb", zb);
						condMap.put("userId", userId);
						condMap.put("nrId", nrIdZb);
						condMap.put("zq", zqCurr);
						condList.add(condMap);
					}
				}
			}
		}
		sqlStr = "update t_ntmisc_userjxn t set t.zb = ? where t.userid=? and t.nr_id=? and t.zq=?";
		logger.info("执行sql,支行考核总包初始化,sqlStr:"+sqlStr+"|condition:"+condition.toString());
//		this.update(condition, daoParent, sqlStr, retMap);
		this.updateBat(condList, daoParent, retMap, sqlStr, 1);
	}
	
	/**
	 * 网点考核总分初始化(各个网点所有员工在当前周期下“网点自由考核”总分)
	 * @param zfZhJa
	 * @param pointJa
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void pointSumInitWd(JSONArray zfWdJa,JSONArray pointJa , LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd){
//		iu.setProgress(tmd, "11", "网点考核总分初始化", "0.2");
		String orgIdIn = tmd.get("orgIdIn").toString();
		String zqCurr = tmd.get("zqCurr").toString();
		String brOrgId = tmd.get("brOrgId").toString();
		LinkedList<LinkedHashMap<String, Object>>  condList = new LinkedList<LinkedHashMap<String,Object>>();
		//初始化支行考核总分
		iu.rmCondition(condition);
		iu.putCondition(condition, "zq", zqCurr);
		sqlStr =  "  select b.orgid, a.nr_id,a.zq,sum(point) point_sum " 
				+  " from (select a.*,b.khcc from t_ntmisc_userjxn a,t_ntmisc_khnr b " 
				+  " where a.nr_id = b.nr_id  and b.nr_id <> '4')a, " 
				+  " (select a.userid,a.name,a.orgid,a.depid,a.jbjx_id,b.orgname,c.depname from t_ntmisc_user a,t_ntmisc_org b,t_ntmisc_dept c " 
				+  " where a.orgid = b.orgid and a.depid = c.depid) b " 
				+  " where a.userid = b.userid and a.khcc='网点' and a.zq=? " 
				+  " group by b.orgid, a.nr_id,a.zq ";
		logger.info("网点考核总分初始化");
		iu.infoDbOper("网点考核总分初始化", sqlStr, condition);
		this.queryManu(zfWdJa, condition, sqlStr, daoParent, 1);//zfZhJa:主要输出项：机构号、内容编号、考核周期、网点当期该项考核内容总分
		
		for (int i = 0; i < pointJa.size(); i++) {
			JSONObject jsonTemp = pointJa.getJSONObject(i);
//			logger.info("jsonTemp:"+jsonTemp.toJSONString());
			String userId = jsonTemp.getString("userid");
			String nrIdUser = jsonTemp.getString("nr_id");//用户考核内容编号
//					String zq = jsonTemp.getString("zq");
			String point = jsonTemp.getString("point");//对应考核内容积分
			String orgId = jsonTemp.getString("orgid");
			
			for (int j = 0; j < zfWdJa.size(); j++) {
				JSONObject jsonZf = zfWdJa.getJSONObject(j);
				String orgIdWd = jsonZf.getString("orgid");//网点编号
				String nrIdWd = jsonZf.getString("nr_id");//网点考核内容编号
				String pointSum = jsonZf.getString("point_sum");//分支行考核内容编号对应总分
				if(orgIdWd.equals(orgId)){
					LinkedHashMap<String, Object> condMap = new LinkedHashMap<String, Object>();
					condMap.put("pointSum", pointSum);
					condMap.put("userId", userId);
					condMap.put("nrId", nrIdWd);
					condMap.put("zq", zqCurr);
					condList.add(condMap);
				}
			}
		}
		sqlStr = "update t_ntmisc_userjxn t set t.point_sum = ? where t.userid=? and t.nr_id=? and t.zq=?";
		logger.info("执行sql,网点考核总分初始化,sqlStr:"+sqlStr+"|condList:"+condList.toString());
		this.updateBat(condList, daoParent, retMap, sqlStr, 1);
	}
	
	/**
	 * 支行考核总分初始化（业务量及风险质量、基础任务及管理营销考核、全员营销奖励考核）
	 * @param zfZhJa
	 * @param pointJa
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void pointSumInitZh(JSONArray zfZhJa,JSONArray pointJa , LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd){
//		iu.setProgress(tmd, "11", "支行考核总分初始化", "0.1");
		String orgIdIn = tmd.get("orgIdIn").toString();
		String zqCurr = tmd.get("zqCurr").toString();
		String brOrgId = tmd.get("brOrgId").toString();
		LinkedList<LinkedHashMap<String, Object>>  condList = new LinkedList<LinkedHashMap<String,Object>>();
		//初始化支行考核总分
		iu.rmCondition(condition);
		iu.putCondition(condition, "zq", zqCurr);
		sqlStr =  " select a.nr_id,a.zq,sum(a.point) point_sum " 
				+  " from (select a.*,b.khcc from t_ntmisc_userjxn a,t_ntmisc_khnr b " 
				+  " where a.nr_id = b.nr_id) a, " 
				+  " (select a.userid,a.name,a.orgid,a.depid,a.jbjx_id,b.orgname,c.depname from t_ntmisc_user a,t_ntmisc_org b,t_ntmisc_dept c " 
				+  " where a.orgid = b.orgid and a.depid = c.depid) b " 
				+  " where a.userid = b.userid and a.khcc='支行'  and a.zq=? and b.orgid in "
				+  orgIdIn 
				+  " group by a.nr_id,a.zq " ;
		logger.info("执行sql,sqlStr:"+sqlStr+"|condition:"+condition.toString());
		this.queryManu(zfZhJa, condition, sqlStr, daoParent, 1);//zfZhJa:主要输出项：考核内容编号、考核周期、分支行当期该项考核内容总分
		
		for (int i = 0; i < pointJa.size(); i++) {
			JSONObject jsonTemp = pointJa.getJSONObject(i);
			logger.info("jsonTemp:"+jsonTemp.toJSONString());
			String userId = jsonTemp.getString("userid");
			String nrIdUser = jsonTemp.getString("nr_id");//用户考核内容编号
//					String zq = jsonTemp.getString("zq");
			String point = jsonTemp.getString("point");//对应考核内容积分
			String orgId = jsonTemp.getString("orgid");
			String brOrgIdTemp = busDispatcherImpl.brOrgQuery(orgId);//遍历用户所在分支行
			if(brOrgIdTemp.equals(brOrgId)){//遍历用户为当前分支行用户
//				logger.info("分支行用户jsonTemp："+jsonTemp);
				for (int j = 0; j < zfZhJa.size(); j++) {
					LinkedHashMap<String, Object> condMap = new LinkedHashMap<String, Object>();
					JSONObject jsonZf = zfZhJa.getJSONObject(j);
					String nrIdZh = jsonZf.getString("nr_id");//支行考核内容编号
					String pointSum = jsonZf.getString("point_sum");//分支行考核内容编号对应总分
					condMap.put("pointSum", pointSum);
					condMap.put("userId", userId);
					condMap.put("nrId", nrIdZh);
					condMap.put("zq", zqCurr);
					condList.add(condMap);
				}
			}
		}
		sqlStr = "update t_ntmisc_userjxn t set t.point_sum = ? where t.userid=? and t.nr_id=? and t.zq=?";
		
		logger.info("执行sql,更新用户绩效表支行考核总分,sqlStr:"+sqlStr+"|condition:"+condition.toString());
		logger.info("更新用户绩效表支行考核总分");
		iu.infoDbOper("更新用户绩效表支行考核总分", sqlStr, condList);
		this.updateBat(condList, daoParent, retMap, sqlStr, 1);
	}
	
	/**
	 * 当期积分查询（主要输出项:用户id、机构id、内容id、积分）
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void pointQuery(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd){
		String zqCurr = tmd.get("zqCurr").toString();
		if("".equals(zqCurr)){
			iu.infoPrgsIntrpt(tmd, "获取当前考核周期失败,请管理员提前设置当前考核周期");
			return;
		}else{
			iu.rmCondition(condition);
			iu.putCondition(condition, "zq", zqCurr);
			sqlStr =  " select a.*,b.orgid from t_ntmisc_userjxn a, " 
					+  " (select a.userid,a.name,a.orgid,a.depid,a.jbjx_id,b.orgname,c.depname from t_ntmisc_user a,t_ntmisc_org b,t_ntmisc_dept c " 
					+  " where a.orgid = b.orgid and a.depid = c.depid) b " 
					+  " where a.userid = b.userid and a.zq = ? and b.orgid in "
					+ tmd.get("orgIdIn") ;
			logger.info("当期:"+zqCurr+",积分查询");
			iu.infoDbOper("当期:"+zqCurr+",积分查询", sqlStr, condition);
			this.queryManu(ja, condition, sqlStr, daoParent, 1);//主要输出项:用户id、机构id、内容id、积分。。。。
		}
	}
	
	/**
	 * 各项考核内容所在考核层级总分初始化
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void pointSumInit(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd){
		JSONArray zfZhJa = new JSONArray();//支行考核总分
		JSONArray zfWdJa = new JSONArray();//网点考核总分
		JSONArray pointJa = new JSONArray();//当期积分基础数据
		
		//初始化分支行考核部分
//		String orgIdIn = tmd.get("orgIdIn").toString();
//		String zqCurr = tmd.get("zqCurr").toString();
//		String brOrgId = tmd.get("brOrgId").toString();
		//当期积分查询
		iu.setProgress(tmd, "11", "个人业绩初始化查询", "0.1");
		this.pointQuery(pointJa, condition, retMap, tmd);
		if(pointJa.size() ==0){
			iu.infoPrgsIntrpt(tmd, "个人业绩未初始化,计算失败");
			tmd.put("breakFlag", "1");//中断后续流程
			return;
		}
//		初始化支行考核总分
		iu.setProgress(tmd, "11", "支行考核总分初始化", "0.1");
		this.pointSumInitZh(zfZhJa, pointJa, condition, retMap, tmd);
//		初始化网点考核总分
		iu.setProgress(tmd, "11", "网点考核总分初始化", "0.2");
		this.pointSumInitWd(zfWdJa, pointJa, condition, retMap, tmd);
//		初始化支行考核（考核层级）总包
		iu.setProgress(tmd, "11", "支行考核总包初始化", "0.3");
		this.zbInitZh(pointJa, condition, retMap, tmd);
//		初始化网点考核（考核层级）总包
		iu.setProgress(tmd, "11", "网点考核总包初始化", "0.4");
		this.zbInitWd(pointJa, condition, retMap, tmd);
	}
	
	
	@Override
	public void doWork(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		tmd.put("breakFlag", "0");
		logger.info("准备开始绩效计算");
		// TODO Auto-generated method stub
		if("0310000000".equals(tmd.get("brOrgId"))){
			tmd.put("finish", "1");
			tmd.put("errorCode", "11");
			tmd.put("errorMsg", "非分支行用户,无法计算绩效");
			return;
		}
		/**********************************数据准备阶段开始**********************************/
//		初始化绩效计算基础数据（t_ntmisc_userjxn）
		this.pointSumInit(ja, condition, retMap, tmd);
		/**********************************数据准备阶段结束**********************************/
		
		/**********************************绩效计算阶段开始**********************************/
//		绩效计算
		this.jxInit(ja, condition, retMap, tmd);
		this.zbWdsdfpInit(ja, condition, retMap, tmd);
		/**********************************绩效计算阶段结束**********************************/
	}

}
