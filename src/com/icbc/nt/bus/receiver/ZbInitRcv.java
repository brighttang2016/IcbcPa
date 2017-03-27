/**
 * 初始化总包人均表t_ntmisc_orgzbrj与总包人数表t_ntmisc_orgzbrs中与总包相关部分
 * 2015-12-16
*/
package com.icbc.nt.bus.receiver;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.bus.MediumBus;
import com.icbc.nt.bus.dispatcher.BusDispatcherImpl;
import com.icbc.nt.util.TransactionMapData;

public class ZbInitRcv extends BusParent implements BusReceiver {
	@Autowired
	MediumBus mediumBus;
	@Autowired
	BusDispatcherImpl busDispatcherImpl;
	@Override
	public void doWork(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * 获取当前用户所在分支行号
	 * @param orgId
	 * @return
	 */
/*	public String getBrOrgId(String orgId){
		String orgRet = "";
		iu.rmCondition(condition);
		iu.putCondition(condition, "orgId", orgId);
		JSONArray ja = new JSONArray();
		sqlStr = "select porgid from t_ntmisc_org where orgid=?";
		this.queryManu(ja, condition, sqlStr, daoParent, 1);
		logger.info("porgid ja:"+ja.toJSONString());
		if(ja.size() > 0){
			String pOrgId = ja.getJSONObject(0).getString("porgid");
			if("0310000000".equals(pOrgId.trim())){
				orgRet = orgId;
			}else if(!"".equals(pOrgId.trim())){
				orgRet = this.getBrOrgId(pOrgId);
			}
		}
		return orgRet;
	}*/
	/**
	 * 分支行下属所有网点机构mova总分之和
	 * @param orgId
	 * @return
	 */
	public float ptMovaSum(String orgIn,String zq){
		float ptMova = 0;
		JSONArray ja = new JSONArray();
		iu.rmCondition(condition);
		iu.putCondition(condition, "zq", zq);
		sqlStr =  " select a.zq,sum(a.pt_mova) pt_mova from( " 
				+  " select * from t_ntmisc_orgpt where orgid in "
				+  orgIn 
				+  " ) a "  
				+  " where a.zq = ? "  
				+  " group by a.zq " ;
		this.queryManu(ja, condition, sqlStr, daoParent, 1);
		try {
			if(ja.size() > 0){
				ptMova = ja.getJSONObject(0).getFloatValue("pt_mova");
			}else
				logger.error("失败：获取【分支行下属所有网点机构mova总分之和】");
		} catch (Exception e) {
			logger.error("异常：获取【分支行下属所有网点机构mova总分之和】出现异常");
		}
		return ptMova;
	}
	/**
	 * 分支行下属所有网点mova总包之和
	 * @param ja
	 */
	public float zbMovaSum(String orgIn,String zq){
		float zbMova = 0;
		JSONArray ja = new JSONArray();
		iu.rmCondition(condition);
		iu.putCondition(condition, "zq", zq);
		sqlStr =  " select a.zq,sum(a.zb_mova) zb_mova from( " 
				+  " select * from t_ntmisc_orgzbrj where orgid in "
				+ orgIn 
				+  " )a "  
				+  " where a.zq=? "  
				+  " group by a.zq " ;
		this.queryManu(ja, condition, sqlStr, daoParent, 1);
		try {
			if(ja.size() > 0){
				zbMova = ja.getJSONObject(0).getFloatValue("zb_mova");
			}else
				logger.error("失败：获取【分支行下属所有网点mova总包之和】");
		} catch (Exception e) {
			logger.error("异常：获取【分支行下属所有网点mova总包之和】出现异常");
		}
		return zbMova;
	}
	
	/**
	 * 初始化机构总包人均表t_ntmisc_orgzbrj中未初始化的总包相关部分:不考虑MOVA挂钩的网点人均总包、不参与考核人员总包、参与考核人员总包、纳入MOVA考核部分抢回总包、考虑mova后新网点总包、参与考核人员新总包、参与考核人均总包
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void zbInitZbrj(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd){
		logger.info("zbInitZbrj 初始化机构总包人均表t_ntmisc_orgzbrj中未初始化的总包相关部分");
		float zbMovaSum = 0;//分支行下属所有网点mova总包之和
		float ptMovaSum = 0;//分支行下属所有网点机构mova总分之和
		JSONArray subOrgJa = new JSONArray();
		//当前登录用户
		String currUser = tmd.get("userId").toString();
		tmd.put("currUser", currUser);
//		当前登录用户所在机构
		String orgCurr = busDispatcherImpl.userOrgQuery(tmd);
		//当前登录用户所在分支行号
//		String brOrgId = this.getBrOrgId(orgCurr);
		String brOrgId = busDispatcherImpl.brOrgQuery(orgCurr);
		if("".equals(brOrgId)){
//			brOrgId = "0310000000";//重庆分行
			tmd.put("errorCode", "11");
			tmd.put("finish", "1");
			tmd.put("errorMsg", "非分支行用户,获取分支行号失败!");
			return;
		}
		//分支行号下属所有网点,组成in形式后的结果：('网点1'、'网点2'、'网点3')
		tmd.put("orgIdCurr", brOrgId);
		String orgIn = busDispatcherImpl.userOrgIn(tmd);
		//当前考核周期
		String zqCurr = busDispatcherImpl.zqCurr();
		zbMovaSum = this.zbMovaSum(orgIn, zqCurr);
		ptMovaSum = this.ptMovaSum(orgIn, zqCurr);
		logger.info("当前考核周期：zqCurr："+zqCurr);
		logger.info("分支行下属所有网点mova总包之和,zbMovaSum:"+zbMovaSum);
		logger.info("分支行下属所有网点机构mova总分之和,ptMovaSum:"+ptMovaSum);
//		分支行下属所有网点
		mediumBus.getSubOrg(subOrgJa, brOrgId);
//		logger.info("分支行："+brOrgId+"子机构subOrgJa："+subOrgJa.toJSONString());
		
		//计算网点抢回总包
		for (int i = 0; i < subOrgJa.size(); i++) {
			JSONObject subOrgJson = subOrgJa.getJSONObject(i);
			JSONArray orgPtJa = new JSONArray();//机构mova得分
			
			String subOrgId = subOrgJson.getString("menuid");
			iu.rmCondition(condition);
			iu.putCondition(condition, "orgid", subOrgId);
			iu.putCondition(condition, "zq", zqCurr);
			//1、计算抢回mova总包
			sqlStr = "select pt_mova from t_ntmisc_orgpt t where orgid = ? and zq = ?";
			this.queryManu(orgPtJa, condition, sqlStr, daoParent, 1);
			float ptMovaOrg = 0;//机构mova得分
			float zbQh = 0;//抢回总包
			try {
				ptMovaOrg = orgPtJa.getJSONObject(0).getFloatValue("pt_mova");
				zbQh = iu.getFloatDecimal((ptMovaOrg / ptMovaSum) * zbMovaSum + "", 2);
				logger.info("subOrgId+"+subOrgId+"|ptMovaOrg:"+ptMovaOrg+"|zbQh"+zbQh);
			} catch (Exception e) {
				logger.error("异常：机构号："+subOrgId+"----周期："+zqCurr+"机构mova得分信息缺失");
				//由于测试只有部分网点有数据，生产打开
				/*
				tmd.put("errorCode", "11");
				tmd.put("errorMsg", subOrgId+"|"+zqCurr+"机构mova得分缺失");
				tmd.put("finish", "1");
				*/
			}
			//2、查询原有总包
			JSONArray orgZbRjJa = new JSONArray();//机构总包人均表
			iu.rmCondition(condition);
			iu.putCondition(condition, "orgid", subOrgId);
			iu.putCondition(condition, "zq", zqCurr);
			sqlStr = "select * from t_ntmisc_orgzbrj t where orgid = ? and zq = ? ";
			this.queryManu(orgZbRjJa, condition, sqlStr, daoParent, 1);
			try {
				JSONObject zbRjJson = orgZbRjJa.getJSONObject(0);
				float zb_init = zbRjJson.getFloatValue("zb_init");//不考虑MOVA挂钩的初次分配绩效总包
				float zb_mova = zbRjJson.getFloatValue("zb_mova");//参与考核人员纳入MOVA考核部分总包
				float zrs_zh = zbRjJson.getFloatValue("zrs_zh");//总人数转换
				float zrs_cykh_zh =  zbRjJson.getFloatValue("zrs_cykh_zh");//参与考核总人数转换
				float zb_init_avg = iu.getFloatDecimal(zb_init/zrs_zh+"", 2);//不考虑MOVA挂钩的网点人均总包
				float zb_cy_sum = iu.getFloatDecimal(zrs_cykh_zh * zb_init_avg +"", 2);// 参与考核人员总包
				float zb_bcy_sum = zb_init - zb_cy_sum;//不参与考核人员总包
				float zb_new = zb_init - zb_mova + zbQh;//考虑MOVA后新网点总包
				float zb_new_cy = zb_new - zb_bcy_sum;//参与考核人员新总包
				float zb_new_avg = iu.getFloatDecimal(zb_new_cy/zrs_cykh_zh+"", 2);//参与考核人员新人均总包
				logger.info("subOrgId"+subOrgId+"zq+"+zqCurr+"---->总比计算结果：zb_init："+zb_init+"|"+"zb_mova："+zb_mova+"|"+"zrs_zh:"+zrs_zh+"|"+"zrs_cykh_zh:"+zrs_cykh_zh+"|"+"zb_init_avg:"+zb_init_avg+"|"+"zb_cy_sum:"+zb_cy_sum+"|"+"zb_bcy_sum:"+zb_bcy_sum+"|"+"zb_new:"+zb_new+"|"+"zb_new_cy:"+zb_new_cy+"|"+"zb_new_avg:"+zb_new_avg);
				//更新总包人均表总包相关信息
				iu.rmCondition(condition);
				iu.putCondition(condition, "zb_init_avg", zb_init_avg+"");
				iu.putCondition(condition, "zb_bcy_sum", zb_bcy_sum+"");
				iu.putCondition(condition, "zb_cy_sum", zb_cy_sum+"");
				iu.putCondition(condition, "zb_qh", zbQh+"");
				iu.putCondition(condition, "zb_new", zb_new+"");
				iu.putCondition(condition, "zb_new_cy", zb_new_cy+"");
				iu.putCondition(condition, "zb_new_avg", zb_new_avg+"");
				iu.putCondition(condition, "orgid", subOrgId+"");
				iu.putCondition(condition, "zq", zqCurr+"");
				sqlStr = "update t_ntmisc_orgzbrj set zb_init_avg=?,zb_bcy_sum=?,zb_cy_sum=?,zb_qh=?,zb_new=?,zb_new_cy=?,zb_new_avg=? where orgid=? and zq=?";
				this.update(condition, daoParent, sqlStr, retMap);
			} catch (Exception e) {
				logger.error("异常：机构号："+subOrgId+"----周期："+zqCurr+"初始总包数据缺失");
				//由于测试只有部分网点有数据，生产打开
				/*
				tmd.put("errorCode", "11");
				tmd.put("errorMsg", subOrgId+"|"+zqCurr+"初始总包数据缺失");
				tmd.put("finish", "1");
				*/
			}
		}
	}
	/**
	 * 初始化机构总包人数表t_ntmisc_orgzbrs中总包相关部分：网点对应绩效考核岗位的绩效总包、网点对应绩效考核岗位的保留总包、网点对应的绩效考核岗位的当期可分配总包
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void zbInitZbrs(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd){
		logger.info("zbInitZbrs 初始化机构总包人数表t_ntmisc_orgzbrs中总包相关部分");
		JSONArray orgZbRjJa = new JSONArray();//总包人均
		//当前登录用户
		String currUser = tmd.get("userId").toString();
		tmd.put("currUser", currUser);
//				当前登录用户所在机构
		String orgCurr = busDispatcherImpl.userOrgQuery(tmd);
		//当前登录用户所在分支行号
//		String brOrgId = this.getBrOrgId(orgCurr);
		String brOrgId = busDispatcherImpl.brOrgQuery(orgCurr);
		if("".equals(brOrgId)){
//					brOrgId = "0310000000";//重庆分行
			tmd.put("errorCode", "11");
			tmd.put("finish", "1");
			tmd.put("errorMsg", "非分支行用户,获取分支行号失败!");
			return;
		}
		//分支行号下属所有网点,组成in形式后的结果：('网点1'、'网点2'、'网点3')
		tmd.put("orgIdCurr", brOrgId);
		String orgIn = busDispatcherImpl.userOrgIn(tmd);
		//当前考核周期
		String zqCurr = busDispatcherImpl.zqCurr();
		iu.rmCondition(condition);
		iu.putCondition(condition, "zq", zqCurr);
//		计算岗位人数表：考核总包、保留总包、分配总包
		/*
//		 方法一：java实现计算逻辑
		//查询当前用户可见总包人均表
		sqlStr = "select orgid,zq,zb_new_avg from t_ntmisc_orgzbrj where zq = ? and orgid in"
				+ orgIn;
		this.queryManu(orgZbRjJa, condition, sqlStr, daoParent, 1);
		for (int i = 0; i < orgZbRjJa.size(); i++) {
			JSONObject zbRjJson = orgZbRjJa.getJSONObject(i);
			String orgId = zbRjJson.getString("orgid");
			float zbNewAvg = zbRjJson.getFloat("zb_new_avg"); 
			//遍历总包人数表
			iu.rmCondition(condition);
			iu.putCondition(condition, "orgId", orgId);
			iu.putCondition(condition, "zq", zqCurr);
			JSONArray zbRsJa = new JSONArray();
			sqlStr = "select * from t_ntmisc_orgzbrs t where  t.orgid =?  and t.zq = ? ";
			this.queryManu(zbRsJa, condition, sqlStr, daoParent, 1);
			for (int j = 0; j < zbRsJa.size(); j++) {
				JSONObject zbRsJson = zbRsJa.getJSONObject(j);
				logger.info("zbRsJson:"+zbRsJson);
				String jbjx_pid = zbRsJson.getString("jbjx_pid");//绩效考核岗位父类（1、柜员；2、客户经理）
				String jbjx_id = zbRsJson.getString("jbjx_id");//绩效考核岗位类别（1低柜2高柜3对公4销售5大堂）
				float rs_kh_zh = zbRsJson.getFloatValue("rs_kh_zh");//参与考核人数转换

				float zb_sy = 0;//当期保留总包
				float zb_fp = 0;//当期分配总包（岗位绩效总包-当期保留总包）
				float zb_kh = 0;
				//保留系数查询
				iu.rmCondition(condition);
				iu.putCondition(condition, "orgId", orgId);
				iu.putCondition(condition, "zq", zqCurr);
				iu.putCondition(condition, "jbJxPid", jbjx_pid);
				iu.putCondition(condition, "jbJxId", jbjx_id);
				JSONArray zbXsJa = new JSONArray();
				sqlStr = "select xs_bl from t_ntmisc_orgzbxs t where orgid = ? and zq = ? and jbjx_pid=? and jbjx_id = ?";
				this.queryManu(zbXsJa, condition, sqlStr, daoParent, 1);
				try {
					zb_kh = rs_kh_zh * zbNewAvg;//绩效总包（岗位总包）
					float xsBl = zbXsJa.getJSONObject(0).getFloat("xs_bl");
					zb_sy = iu.getFloatDecimal(zb_kh * xsBl+"", 2);
					zb_fp = zb_kh - zb_sy;
				} catch (Exception e) {
					logger.error("orgId:"+orgId+"zq:"+zqCurr+"jbJxPid:"+jbjx_pid+"jbJxId:"+jbjx_id+"------->获取保留系数失败");
				}
				logger.info("orgId:"+orgId+"zq:"+zqCurr+"jbJxPid:"+jbjx_pid+"jbJxId:"+jbjx_id+"---->岗位人数表有关总包计算结果：zb_sy："+zb_sy+"zb_fp:"+zb_fp);
				
				//更新机构总包人数表t_ntmisc_orgzbrs中总包相关部分
				iu.rmCondition(condition);
				iu.putCondition(condition, "zb_kh", zb_kh);
				iu.putCondition(condition, "zb_sy", zb_sy);
				iu.putCondition(condition, "zb_fp", zb_fp);
				iu.putCondition(condition, "orgId", orgId);
				iu.putCondition(condition, "zq", zqCurr);
				iu.putCondition(condition, "jbJxPid", jbjx_pid);
				iu.putCondition(condition, "jbJxId", jbjx_id);
				sqlStr = "update t_ntmisc_orgzbrs set zb_kh=?,zb_sy=?,zb_fp=? where orgid=? and zq=?  and jbjx_pid=? and jbjx_id =?";
				this.update(condition, daoParent, sqlStr, retMap);
			}
		}
		*/
//		方法二：数据库直接查询计算最终结果(优选方案)
		JSONArray zbRsJsJa = new JSONArray();//总包人数总包计算结果。总包人数表中，岗位参与考核绩效总包、当期保留总包、当期分配总包（绩效总包-当期保留总包)
		sqlStr =  "  select a.orgid,a.zq,a.jbjx_pid,a.jbjx_id,a.xs_bl, " 
				+  " nvl(b.zb_kh,'0') zb_kh, " 
				+  " to_char(nvl(a.xs_bl*b.zb_kh,'0'),'99999999999.99') zb_sy, " 
				+  " a.xs_bl*b.zb_kh, " 
				+  " to_char(nvl(b.zb_kh-a.xs_bl*b.zb_kh,'0'),'999999999999.99')  zb_fp " 
				+  " from t_ntmisc_orgzbxs a , " 
				+  " ( " 
				+  " select a.orgid,a.zq,a.jbjx_pid,a.jbjx_id,a.rs_gw,a.rs_kh,a.rs_kh_zh,b.zb_new_avg ,a.rs_kh_zh*b.zb_new_avg zb_kh " 
				+  " from t_ntmisc_orgzbrs a ,t_ntmisc_orgzbrj b " 
				+  " where a.orgid = b.orgid and a.zq = b.zq " 
				+  " )b " 
				+  " where a.orgid=b.orgid and a.zq=b.zq and a.jbjx_pid=b.jbjx_pid and a.jbjx_id=b.jbjx_id " 
				+  " and a.zq = ? and a.orgid in " 
				+  orgIn;
		this.queryManu(zbRsJsJa, condition, sqlStr, daoParent, 1);
		for (int i = 0; i < zbRsJsJa.size(); i++) {
			JSONObject zbRsJsJson = zbRsJsJa.getJSONObject(i);
			iu.rmCondition(condition);
			iu.putCondition(condition, "zb_kh", zbRsJsJson.getString("zb_kh"));
			iu.putCondition(condition, "zb_sy", zbRsJsJson.getString("zb_sy"));
			iu.putCondition(condition, "zb_fp", zbRsJsJson.getString("zb_fp"));
			iu.putCondition(condition, "orgid", zbRsJsJson.getString("orgid"));
			iu.putCondition(condition, "zq", zbRsJsJson.getString("zq"));
			iu.putCondition(condition, "jbjx_pid", zbRsJsJson.getString("jbjx_pid"));
			iu.putCondition(condition, "jbjx_id", zbRsJsJson.getString("jbjx_id"));
			sqlStr = "update t_ntmisc_orgzbrs set zb_kh=?,zb_sy=?,zb_fp=? where orgid=? and zq=?  and jbjx_pid=? and jbjx_id =?";
			this.update(condition, daoParent, sqlStr, retMap);
		}
	}
}
