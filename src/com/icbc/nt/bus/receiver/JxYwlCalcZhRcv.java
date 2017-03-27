/**
 * 业务量绩效计算（分支行考核部分）业务逻辑2015-12-17
 */
package com.icbc.nt.bus.receiver;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.bus.MediumBus;
import com.icbc.nt.bus.dispatcher.BusDispatcherImpl;
import com.icbc.nt.util.TransactionMapData;

public class JxYwlCalcZhRcv extends BusParent implements BusReceiver{
	@Autowired
	private MediumBus mediumBus;
	@Autowired
	private BusDispatcherImpl busDispatcherImpl;
	/**
	 * 最终业务量绩效计算
	 * @param userYwlJa
	 * @param zbblJa
	 */
	public void ywlJxCalc(JSONArray userYwlJa){
		System.out.println("绩效录入开始");
		HashMap retMap = new HashMap();
		for (int i = 0; i < userYwlJa.size(); i++) {
			JSONObject ywlJson = userYwlJa.getJSONObject(i);
			float fw = ywlJson.getFloatValue("fw");//1000分标准化后得分
			float fwSum = ywlJson.getFloatValue("fwSum");
			float zb = ywlJson.getFloatValue("zb");//总包
			float userJx = iu.getFloatDecimal(zb*(fw/fwSum)+"", 2);//网点考核绩效
			iu.rmCondition(condition);
			iu.putCondition(condition, "JX_YWL_Zh", userJx+"");
			iu.putCondition(condition, "ZSF_YWL_Zh", fw+"");
			iu.putCondition(condition, "userId", ywlJson.getString("user_id"));
			iu.putCondition(condition, "zq", ywlJson.getString("zq"));
			sqlStr  = "update t_ntmisc_userjx t set t.JX_YWL_Zh=?,t.ZSF_YWL_Zh=? where t.user_id = ? and t.zq=?";
			this.update(condition, daoParent, sqlStr, retMap);
			
			//待考核数据使用标识置1(表明该数据已参与考核)
			/*iu.rmCondition(condition);
			iu.putCondition(condition, "used", "1");
			iu.putCondition(condition, "userId", ywlJson.getString("user_id"));
			iu.putCondition(condition, "zq", ywlJson.getString("zq"));
			sqlStr = "update t_ntmisc_ywlmx t set t.used = ? where t.user_id=? and t.zq=?";
			this.update(condition, daoParent, sqlStr, retMap);*/
		}
		System.out.println("写入绩效、折算分，userYwlJa："+userYwlJa.toJSONString());
		logger.info("写入绩效、折算分，userYwlJa："+userYwlJa.toJSONString());
	}
	
	/**
	 * fw\fwSum计算
	 * @param userYwlJa
	 */
	public void fwCalc(JSONArray userYwlJa){
		JSONArray orgFwSumJa = new JSONArray();//保存机构fwSum
		for (int i = 0; i < userYwlJa.size(); i++) {
			JSONObject userYwlJson = userYwlJa.getJSONObject(i);
			String orgId = userYwlJson.getString("orgid");
			String zq = userYwlJson.getString("zq");
			String jbjx_pid = userYwlJson.getString("jbjx_pid");
			String jbjx_id = userYwlJson.getString("jbjx_id");
			
			float w = userYwlJson.getFloatValue("w");
			float wSum = userYwlJson.getFloatValue("wSum");
			float zrs = userYwlJson.getFloatValue("zrs");
			float fw = 0;
			float fwSum = 0;
			try {
				fw = this.iu.getFloatDecimal(w / wSum * 1000 * zrs + "", 2);
				userYwlJson.put("fw", fw);
			} catch (Exception e) {
			}
			boolean existFlag = false;
			
			JSONObject fwSumJsonExist = new JSONObject();//已存在对象
			JSONObject fwSumJson = new JSONObject();
			fwSumJson.put("orgid", orgId);
			fwSumJson.put("zq", zq);
			fwSumJson.put("jbjx_pid", jbjx_pid);
			fwSumJson.put("jbjx_id", jbjx_id);
			fwSumJson.put("fwSum", fw+"");
			for (int j = 0; j < orgFwSumJa.size(); j++) {
				JSONObject fwSumJsonTemp = orgFwSumJa.getJSONObject(j);
				if(orgId.equals(fwSumJsonTemp.getString("orgid")) 
						&& zq.equals(fwSumJsonTemp.getString("zq"))
						&& jbjx_pid.equals(fwSumJsonTemp.getString("jbjx_pid"))
						&& jbjx_id.equals(fwSumJsonTemp.getString("jbjx_id"))){//已存在
					existFlag = true;
					fwSumJsonExist = fwSumJsonTemp;
				}
			}
			if(existFlag){
				fwSumJsonExist.put("fwSum", fwSumJsonExist.getFloatValue("fwSum")+fw);
			}else{
				orgFwSumJa.add(fwSumJson);
			}
		}
		for (int i = 0; i < userYwlJa.size(); i++) {
			JSONObject userYwlJson = userYwlJa.getJSONObject(i);
			for (int j = 0; j < orgFwSumJa.size(); j++) {
				JSONObject fwSumJsonTemp = orgFwSumJa.getJSONObject(j);
				if(userYwlJson.getString("orgid").equals(fwSumJsonTemp.getString("orgid")) 
						&&userYwlJson.getString("zq").equals(fwSumJsonTemp.getString("zq"))
						&&userYwlJson.getString("jbjx_pid").equals(fwSumJsonTemp.getString("jbjx_pid"))
						&&userYwlJson.getString("jbjx_id").equals(fwSumJsonTemp.getString("jbjx_id"))){//同一机构同一职务类别已
					userYwlJson.put("fwSum", fwSumJsonTemp.getString("fwSum"));
				}
			}
		}
		System.out.println("分支行考核，计算fwSum后的业务量数据："+userYwlJa.toJSONString());
		logger.info("分支行考核，计算fwSum后的业务量数据："+userYwlJa.toJSONString());
	}
	
	/**
	 * wSum计算
	 * @param userYwlJa
	 */
	public void wSumCalc(JSONArray userYwlJa,String zqCurr){
		//计算wSum
		JSONArray orgWsumJa = new JSONArray();//保存机构下对应岗位类别的wSum
		for (int i = 0; i < userYwlJa.size(); i++) {
			float zsYwl = 0;//柜员个人折算业务量
//			float avgYwl = 0;//网点所有柜员人均业务量
//			float zbYwl = 0;//网点业务量总包
//			float gyNum = 0;//网点柜员数目
//			float zbGyYwl = 0;//网点柜员业务量总包
			float w = 0;//个人业务量/网点人均（对应绩效考核岗位）业务量
//			float wSum = 0;//w求和
			JSONObject ywlJson = userYwlJa.getJSONObject(i);
			String ywlOrg = ywlJson.getString("orgid");
			String jbJxId = ywlJson.getString("jbjx_id");//绩效老何岗位类别id
			zsYwl = ywlJson.getFloatValue("zs");
			w = ywlJson.getFloatValue("w");
			System.out.println("w:"+w);
			
			JSONObject wSumJson = new JSONObject();
			JSONObject wSumJsonExist = new JSONObject();//wSum json 数组
			wSumJson.put("orgid", ywlOrg);
			wSumJson.put("zq", zqCurr);
			wSumJson.put("jbJxId", jbJxId);//绩效考核岗位id
			wSumJson.put("wSum", w);
			
			boolean existFlag = false;
			for (int j = 0; j < orgWsumJa.size(); j++) {
				JSONObject wSumJsonTemp = orgWsumJa.getJSONObject(j);
				if(ywlOrg.equals(wSumJsonTemp.getString("orgid")) 
						&& jbJxId.equals(wSumJsonTemp.getString("jbJxId"))
						&& zqCurr.equals(wSumJsonTemp.getString("zq"))){//同一机构绩效考核岗位类别已存在
					existFlag = true;
					wSumJsonExist = wSumJsonTemp;//找到已存在于orgWsumJa的wSumJson
				}
			}
			if(existFlag){
				wSumJsonExist.put("wSum", wSumJsonExist.getFloatValue("wSum")+w);
			}else{
				orgWsumJa.add(wSumJson);
			}
		}
		for (int j = 0; j < userYwlJa.size(); j++) {
			JSONObject ywlJsonTemp = userYwlJa.getJSONObject(j);
			for (int i = 0; i < orgWsumJa.size(); i++) {
				JSONObject wSumJsonTemp = orgWsumJa.getJSONObject(i);
//				System.out.println(wSumJsonTemp.toJSONString());
				if(ywlJsonTemp.getString("orgid").equals(wSumJsonTemp.getString("orgid")) 
						&&ywlJsonTemp.getString("jbjx_id").equals(wSumJsonTemp.getString("jbJxId"))
						&&ywlJsonTemp.getString("zq").equals(wSumJsonTemp.getString("zq"))){//同意机构下同意职务类别
					ywlJsonTemp.put("wSum", wSumJsonTemp.getString("wSum"));
				}
			}
		}
		System.out.println("计算wSum后的业务量数据："+userYwlJa.toJSONString());
		logger.info("计算wSum后的业务量数据："+userYwlJa.toJSONString());
	}
	
	/**
	 * 分支行考核，当前用户同一层级柜员数目、业务量总包设置
	 * @param zbblJa
	 * @param ywlJa
	 */
	public void levelNum(JSONArray ywlJa,TransactionMapData tmd){
		String orgIn = tmd.get("orgIn").toString();
		for (int i = 0; i < ywlJa.size(); i++) {
			JSONObject ywlJson = ywlJa.getJSONObject(i);
			String orgid = ywlJson.getString("orgid");
			String zq = ywlJson.getString("zq");
			String jbjx_id = ywlJson.getString("jbjx_id");//绩效考核岗位id
			String jbjx_pid = ywlJson.getString("jbjx_pid");
			JSONArray rsKhZbJa = new JSONArray();//考核人数、参与考核人总包（对应绩效考核岗位的总包）
			sqlStr =  " select t.jbjx_pid,t.jbjx_id,t.zq,t.khfw,sum(t.rs_kh) rs_kh,sum(t.zb_ywl) zb_ywl from ( " 
					+  " select a.orgid,a.zq,a.jbjx_pid,a.jbjx_id,b.khfw, a.rs_kh,b.zb_ywl,b.zb_bzcp,b.zb_zhts,b.zb_dx " 
					+  " from t_ntmisc_orgzbrs a,t_ntmisc_orgzbfp b " 
					+  " where a.orgid = b.orgid and a.zq = b.zq and a.jbjx_pid = b.jbjx_pid and a.jbjx_id = b.jbjx_id " 
					+  " ) t " 
					+  " where t.zq=? and t.jbjx_pid=? and t.jbjx_id=? and  t.khfw=? and orgid in"
					+  orgIn 
					+  " group by t.jbjx_pid,t.jbjx_id,t.zq,t.khfw " ;
			iu.rmCondition(condition);
//			iu.putCondition(condition, "orgid", orgid);
			iu.putCondition(condition, "zq", zq);
			iu.putCondition(condition, "jbjx_pid", jbjx_pid);
			iu.putCondition(condition, "jbjx_id", jbjx_id);
			iu.putCondition(condition, "khfw", "1");//支行考核
			logger.info("1111111111111:sqlStr:"+sqlStr+",condition:"+condition);
			this.queryManu(rsKhZbJa, condition, sqlStr, daoParent, 1);
			JSONObject rsKhZbJson = new JSONObject();//考核人数总包json
			
			try {
				rsKhZbJson = rsKhZbJa.getJSONObject(0);
				ywlJson.put("zrs", rsKhZbJson.getString("rs_kh"));//所在考核分组总人数（网点考核）
				ywlJson.put("zb", rsKhZbJson.getString("zb_ywl"));//所在考核分组业务量总包(网点考核)
			} catch (Exception e) {
				logger.error("orgid:"+orgid+"|zq"+zq+"jbjx_pid|"+jbjx_pid+"|jbjx_id:"+jbjx_id+",获取用户所在考核分组总人数、总包失败");
				tmd.put("errorCode", "11");
				tmd.put("errorMsg", "获取用户所在考核分组总人数、总包失败");
				tmd.put("finish", "1");
			}
		}
		logger.info("分支行考核，所在考核分组总人数、业务量总包计算结果"+ywlJa.toJSONString());
	}
	
	/**
	 * 业务量计算（分支行考核），计算w值
	 * @param orgId
	 */
	public void wCalc(JSONArray userYwlJa,String zqCurr,TransactionMapData tmd){
		String userId = tmd.get("userId").toString();
		String orgId = mediumBus.getUserOrg(userId);
		String brOrgId = busDispatcherImpl.brOrgQuery(orgId);//当前用户所在分支行号
		String orgIn = mediumBus.getOrgIn(brOrgId);//分支行下属所有机构
		tmd.put("orgIn", orgIn);
//		查询分支行下属所有机构，各个岗位对应的业务量平均值
		JSONArray ywlAvgJa = new JSONArray();//各个岗位对应的业务量平均值
		iu.rmCondition(condition);
		iu.putCondition(condition, "zq", zqCurr);
		sqlStr =  " select jbjx_pid,jbjx_pname,jbjx_id,jbjx_name,avg(zs) zs_avg from( " 
				+  " select b.orgid,b.jbjx_id,c.jbjx_name,c.jbjx_pid,c.jbjx_pname,b.userid,a.zs " 
				+  " from t_ntmisc_ywlmx a, t_ntmisc_user b, t_ntmisc_jobjx c " 
				+  " where a.user_id = b.userid and b.jbjx_id = c.jbjx_id and a.zq=? and a.used='0' and b.orgid in "
				+  orgIn 
				+  " ) "  
				+  " group by jbjx_pid,jbjx_pname,jbjx_id,jbjx_name " ;
		logger.info("各个岗位对应的业务量平均值查询,sqlStr:"+sqlStr+"condition:"+condition);
		this.queryManu(ywlAvgJa, condition, sqlStr, daoParent, 1);//业务量基础数据查询
		logger.info("各个岗位对应的业务量平均值查询结果，ywlAvgJa："+ywlAvgJa);
		
//		分支行下属所有网点员工折算业务量查询
		iu.rmCondition(condition);
		iu.putCondition(condition, "zq", zqCurr);
		sqlStr =  " select a.*, b.orgid,b.jbjx_id,c.jbjx_name,c.jbjx_pid,c.jbjx_pname " 
				+  " from t_ntmisc_ywlmx a, t_ntmisc_user b, t_ntmisc_jobjx c " 
				+  " where a.user_id = b.userid and b.jbjx_id = c.jbjx_id and a.zq=? and a.used='0' and b.orgid in "
				+  orgIn ;
		logger.info("分支行下属所有网点员工折算业务量查询,sqlStr:"+sqlStr+"condition:"+condition);
		this.queryManu(userYwlJa, condition, sqlStr, daoParent, 1);//业务量基础数据查询
		logger.info("分支行下属所有网点员工折算业务量查询结果，userYwlJa："+userYwlJa);
		for (int i = 0; i < userYwlJa.size(); i++) {
			JSONObject ywlJson = userYwlJa.getJSONObject(i);
			for (int j = 0; j < ywlAvgJa.size(); j++) {
				JSONObject ywlAvgJson = ywlAvgJa.getJSONObject(j);
				if(ywlJson.getString("jbjx_pid").equals(ywlAvgJson.getString("jbjx_pid"))
						&& ywlJson.getString("jbjx_id").equals(ywlAvgJson.getString("jbjx_id"))){
					float w = ywlJson.getFloatValue("zs")/ywlAvgJson.getFloatValue("zs_avg");
					w = iu.getFloatDecimal(w+"", 2);
					ywlJson.put("w", w+"");
				}
			}
		}
		logger.info("业务量计算（分支行考核），计算w值,设置w值后，userYwlJa："+userYwlJa);
	}
	
	@Override
	public void doWork(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		this.setProgress(tmd);
		System.out.println("业务量绩效计算");
		String zqCurr = busDispatcherImpl.zqCurr();//当前考核周期
		tmd.put("zqCurr", zqCurr);
		logger.info("业务量绩效计算");
		ja.clear();
		/*tmd.put("errorCode", "12");
		tmd.put("errorMsg","业务量绩效计算中...");
		tmd.put("progress", "0.2");
		tmd.put("finish", "0");*/
		//查询所有可见业务量明细数据
		JSONArray userYwlJa = new JSONArray();// 当前用户可见业务量原始数据
		/******************************业务量计算（支行考核）开始******************************/
		this.wCalc(userYwlJa, zqCurr,tmd);
		this.wSumCalc(userYwlJa,zqCurr);//计算wSum
		this.levelNum(userYwlJa,tmd);//当前用户所处绩效考核岗位员工数目数目 及对应的总包（分支行考核）设置
		this.fwCalc(userYwlJa);//计算fw、fwsum
		this.ywlJxCalc(userYwlJa);//最终业务量绩效计算
		/******************************业务量计算（支行考核）结束******************************/
		
	}
}
