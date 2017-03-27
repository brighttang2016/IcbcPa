/**
 * 支行产品绩效计算业务逻辑2015-11-20
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
import com.icbc.nt.util.TransactionMapData;

public class JxZhtsCalcRcv extends BusParent implements BusReceiver{
	@Autowired
	private MediumBus mediumBus;
	/**
	 * 最终标准产品绩效计算
	 * @param userYwlJa
	 */
	public void jxCalc(JSONArray userYwlJa){
		System.out.println("绩效录入开始");
		HashMap retMap = new HashMap();
		for (int i = 0; i < userYwlJa.size(); i++) {
			JSONObject ywlJson = userYwlJa.getJSONObject(i);
			float fw = ywlJson.getFloatValue("fw");//1000分标准化后得分
			float fwSum = ywlJson.getFloatValue("fwSum");
			float zb = ywlJson.getFloatValue("zb");//总包
			float userJx = iu.getFloatDecimal(zb*(fw/fwSum)+"", 2);//网点考核绩效
			iu.rmCondition(condition);
			iu.putCondition(condition, "JX_TSCP_WD", userJx+"");
			iu.putCondition(condition, "ZSF_TSCP_WD", fw+"");
			iu.putCondition(condition, "userId", ywlJson.getString("user_id"));
			iu.putCondition(condition, "zq", ywlJson.getString("zq"));
			sqlStr  = "update t_ntmisc_userjx t set t.JX_TSCP_WD=?,t.ZSF_TSCP_WD=? where t.user_id = ? and t.zq=?";
			this.update(condition, daoParent, sqlStr, retMap);
			
			//待考核数据使用标识置1(表明该数据已参与考核)
			/*iu.rmCondition(condition);
			iu.putCondition(condition, "used", "1");
			iu.putCondition(condition, "userId", ywlJson.getString("user_id"));
			iu.putCondition(condition, "zq", ywlJson.getString("zq"));
			sqlStr = "update t_ntmisc_ywlmx t set t.used = ? where t.user_id=? and t.zq=?";
			this.update(condition, daoParent, sqlStr, retMap);*/
		}
		System.out.println("支行特色产品绩效计算（网点考核），写入绩效、折算分，userYwlJa："+userYwlJa.toJSONString());
		logger.info("支行特色产品绩效计算（网点考核），写入绩效、折算分，userYwlJa："+userYwlJa.toJSONString());
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
		System.out.println("支行特色产品绩效计算（网点考核），计算fwSum后的业务量数据："+userYwlJa.toJSONString());
		logger.info("支行特色产品绩效计算（网点考核），计算fwSum后的业务量数据："+userYwlJa.toJSONString());
	}
	
	/**
	 * wSum计算
	 * @param userYwlJa
	 */
	public void wSumCalc(JSONArray userYwlJa){
		//计算wSum
		JSONArray orgWsumJa = new JSONArray();//保存机构下对应岗位类别的wSum
		for (int i = 0; i < userYwlJa.size(); i++) {
			float zsYwl = 0;//柜员个人折算标准产品
//			float avgYwl = 0;//网点所有柜员人均标准产品
//			float zbYwl = 0;//网点标准产品总包
//			float gyNum = 0;//网点柜员数目
//			float zbGyYwl = 0;//网点柜员标准产品总包
			float w = 0;//个人标准产品/网点人均标准产品
//			float wSum = 0;//w求和
			JSONObject ywlJson = userYwlJa.getJSONObject(i);
			String orgid = ywlJson.getString("orgid");
//			String jbId = ywlJson.getString("jb_id");//岗位层级id
			String jbjx_pid = ywlJson.getString("jbjx_pid");
			String jbjx_id = ywlJson.getString("jbjx_id");
			String zq = ywlJson.getString("zq");
//			zsYwl = ywlJson.getFloatValue("zs");
//			avgYwl = ywlJson.getFloatValue("avg_ywl");
			w = ywlJson.getFloatValue("w");
			System.out.println("w:"+w);
			
			JSONObject wSumJson = new JSONObject();
			JSONObject wSumJsonExist = new JSONObject();
			wSumJson.put("orgid", orgid);
			wSumJson.put("jbjx_pid", jbjx_pid);
			wSumJson.put("jbjx_id", jbjx_id);
			wSumJson.put("zq", zq);
			wSumJson.put("wSum", w);
			
			boolean existFlag = false;
			for (int j = 0; j < orgWsumJa.size(); j++) {
				JSONObject wSumJsonTemp = orgWsumJa.getJSONObject(j);
				if(orgid.equals(wSumJsonTemp.getString("orgid")) 
						&& jbjx_pid.equals(wSumJsonTemp.getString("jbjx_pid"))
						&& jbjx_id.equals(wSumJsonTemp.getString("jbjx_id"))
						&& zq.equals(wSumJsonTemp.getString("zq"))){//同一机构同意职务类别已存在
					existFlag = true;
					wSumJsonExist = wSumJsonTemp;
				}
			}
			System.out.println("existFlag:"+existFlag);
			if(existFlag){
				wSumJsonExist.put("wSum", wSumJsonExist.getFloatValue("wSum")+w);
			}else{
				orgWsumJa.add(wSumJson);
			}
			System.out.println("orgWsumJa:"+orgWsumJa);
			
		}
		System.out.println("orgWsumJa:"+orgWsumJa);
		for (int j = 0; j < userYwlJa.size(); j++) {
			JSONObject ywlJsonTemp = userYwlJa.getJSONObject(j);
			for (int i = 0; i < orgWsumJa.size(); i++) {
				JSONObject wSumJsonTemp = orgWsumJa.getJSONObject(i);
//				System.out.println(wSumJsonTemp.toJSONString());
				if(ywlJsonTemp.getString("orgid").equals(wSumJsonTemp.getString("orgid")) 
						&&ywlJsonTemp.getString("jbjx_pid").equals(wSumJsonTemp.getString("jbjx_pid"))
						&&ywlJsonTemp.getString("jbjx_id").equals(wSumJsonTemp.getString("jbjx_id"))
						&&ywlJsonTemp.getString("zq").equals(wSumJsonTemp.getString("zq"))){//同意机构下同意职务类别
					ywlJsonTemp.put("wSum", wSumJsonTemp.getString("wSum"));
				}
			}
		}
		System.out.println("支行特色产品绩效计算（网点考核）,计算wSum后的标准产品数据："+userYwlJa.toJSONString());
		logger.info("支行特色绩效计算（网点考核）,计算wSum后的标准产品数据："+userYwlJa.toJSONString());
	}
	
	/**
	 * 当前用户同一层级柜员数目、标准产品总包设置
	 * @param zbblJa
	 * @param ywlJa
	 */
	public void levelNum(JSONArray ywlJa,TransactionMapData tmd){
		for (int i = 0; i < ywlJa.size(); i++) {
			JSONObject ywlJson = ywlJa.getJSONObject(i);
			String orgid = ywlJson.getString("orgid");
			String zq = ywlJson.getString("zq");
			String jbjx_id = ywlJson.getString("jbjx_id");//绩效考核岗位id
			String jbjx_pid = ywlJson.getString("jbjx_pid");
			JSONArray rsKhZbJa = new JSONArray();//考核转换人数、参与考核人总包（对应绩效考核岗位的总包）
			sqlStr = "select t.rs_kh,t.zb_zhts from ("
					+ " select a.orgid,a.zq,a.jbjx_pid,a.jbjx_id,b.khfw, a.rs_kh,b.zb_ywl,b.zb_bzcp,b.zb_zhts,b.zb_dx"
					+ " from t_ntmisc_orgzbrs a,t_ntmisc_orgzbfp b"
					+ " where a.orgid = b.orgid and a.zq = b.zq and a.jbjx_pid = b.jbjx_pid and a.jbjx_id = b.jbjx_id "
					+ " ) t where t.orgid=? and t.zq=? and t.jbjx_pid=? and t.jbjx_id=? and  t.khfw=?";
			iu.rmCondition(condition);
			iu.putCondition(condition, "orgid", orgid);
			iu.putCondition(condition, "zq", zq);
			iu.putCondition(condition, "jbjx_pid", jbjx_pid);
			iu.putCondition(condition, "jbjx_id", jbjx_id);
			iu.putCondition(condition, "khfw", "2");//网点考核
			logger.info("1111111111111:sqlStr:"+sqlStr+",condition:"+condition);
			this.queryManu(rsKhZbJa, condition, sqlStr, daoParent, 1);
			JSONObject rsKhZbJson = new JSONObject();//考核人数总包json
			
			try {
				rsKhZbJson = rsKhZbJa.getJSONObject(0);
				ywlJson.put("zrs", rsKhZbJson.getString("rs_kh"));//所在考核分组总人数（网点考核）
				ywlJson.put("zb", rsKhZbJson.getString("zb_zhts"));//所在考核分组标准产品总包(网点考核)
			} catch (Exception e) {
				logger.error("orgid:"+orgid+"|zq"+zq+"jbjx_pid|"+jbjx_pid+"|jbjx_id:"+jbjx_id+",获取用户所在考核分组总人数、总包失败");
				tmd.put("errorCode", "11");
				tmd.put("errorMsg", "获取用户所在考核分组总人数、总包失败");
				tmd.put("finish", "1");
			}
		}
		logger.info("支行特色产品绩效计算（网点考核），所在考核分组总人数、业务量总包计算结果"+ywlJa.toJSONString());
	}
	
	/**
	 * 获取机构总包比例
	 * @param orgId
	 */
	public void getZbbl(JSONArray ja){
		iu.rmCondition(condition);
		sqlStr = "select * from t_ntmisc_zbbl t";
		this.queryAuto(ja, condition, sqlStr, daoParent, 1);
	}
	
	/**
	 * 设置当前用户所在分支行所有员工的特色产品考核w值
	 * @param ja 当前所有可见用户
	 */
	public void wCalc(JSONArray userJa,TransactionMapData tmd){
		String orgIn = tmd.get("orgIn").toString();
		iu.rmCondition(condition);
		iu.putCondition(condition, "zq", tmd.get("zqCurr"));
		sqlStr =  " select t.user_id,t.orgid,t.depid,t.jbjx_pid,t.jbjx_pname,t.jbjx_id,t.jbjx_name,t.zq,sum(cj) w from( " 
				+  " select a.user_id,a.seq_num,a.rs,a.used,a.point,a.zq,b.orgid,b.depid,a.rs*a.point cj ,c.jbjx_pid,c.jbjx_pname,c.jbjx_id,c.jbjx_name " 
				+  " from t_ntmisc_zhtsmx a,t_ntmisc_user b,t_ntmisc_jobjx c " 
				+  " where a.user_id = b.userid and b.jbjx_id = c.jbjx_id and a.used='0' and a.zq=? " 
				+  " and a.seq_num in (select seq_num from t_ntmisc_orgzhts where orgid in "
				+  orgIn
				+ ")"
				+ ") t " 
				+  " where orgid in "
				+ 	orgIn 
				+  " group by  t.user_id,t.orgid,t.depid,t.jbjx_pid,t.jbjx_pname,t.jbjx_id,t.jbjx_name,t.zq " ;
		logger.info("执行sql,设置当前用户所在分支行所有员工的特色产品考核w值，sqlStr:"+sqlStr+"-----condition:"+condition);
		this.queryManu(userJa, condition, sqlStr, daoParent, 1);
		System.out.println("支行特色绩效计算，userJa设置w值后："+userJa.toJSONString());
		logger.info("支行特色绩效计算，userJa设置w值后："+userJa.toJSONString());
	}
	
	@Override
	public void doWork(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		this.setProgress(tmd);
		System.out.println("支行特色产品绩效计算");
		logger.info("支行特色产品绩效计算");
		ja.clear();
		/*tmd.put("errorCode", "12");
		tmd.put("errorMsg","支行特色产品绩效计算中...");
		tmd.put("progress", "0.7");
		tmd.put("finish", "0");*/
		
//		JSONArray zbblJa = new JSONArray();//机构总包比例
//		this.getZbbl(zbblJa);//查询总包比例
		
		//查询所有可见支行特色产品明细数据
//		JSONArray userYwlJa = new JSONArray();// 当前用户可见支行特色产品原始数据
//		String userId = tmd.get("userId").toString();
//		String orgId = mediumBus.getUserOrg(userId);
//		String orgIn = mediumBus.getOrgIn(orgId);
		
		//查询所有可见的存在于支行特色明细表中的用户
		JSONArray userJa = new JSONArray();
		this.wCalc(userJa,tmd);//设置特色产品考核w值
		this.wSumCalc(userJa);//计算wSum
		this.levelNum(userJa,tmd);//当前用户同一层级柜员数目、支行特色产品总包设置
		this.fwCalc(userJa);//计算fw、fwsum
		this.jxCalc(userJa);//最终支行特色产品绩效计算
		
	}
}
