/**
 * 绩效计算业务逻辑2015-11-20
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

public class JxDxCalcRcv extends BusParent implements BusReceiver{
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
			iu.putCondition(condition, "JX_DX_WD", userJx+"");
			iu.putCondition(condition, "ZSF_DX_WD", fw+"");
			iu.putCondition(condition, "userId", ywlJson.getString("user_id"));
			iu.putCondition(condition, "zq", ywlJson.getString("zq"));
			sqlStr  = "update t_ntmisc_userjx t set t.JX_DX_WD=?,t.ZSF_DX_WD=? where t.user_id = ? and t.zq=?";
			this.update(condition, daoParent, sqlStr, retMap);
			
			//待考核数据使用标识置1(表明该数据已参与考核)
			/*iu.rmCondition(condition);
			iu.putCondition(condition, "used", "1");
			iu.putCondition(condition, "userId", ywlJson.getString("user_id"));
			iu.putCondition(condition, "zq", ywlJson.getString("zq"));
			sqlStr = "update t_ntmisc_ywlmx t set t.used = ? where t.user_id=? and t.zq=?";
			this.update(condition, daoParent, sqlStr, retMap);*/
		}
		System.out.println("定性绩效计算（网点考核），写入绩效、折算分，userYwlJa："+userYwlJa.toJSONString());
		logger.info("定性绩效计算（网点考核），写入绩效、折算分，userYwlJa："+userYwlJa.toJSONString());
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
				//fw = this.iu.getFloatDecimal(w / wSum * 1000 * zrs + "", 2);
				fw = 1000+w;
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
		System.out.println("定性绩效计算（网点考核），计算fwSum后的业务量数据："+userYwlJa.toJSONString());
		logger.info("定性绩效计算（网点考核），计算fwSum后的业务量数据："+userYwlJa.toJSONString());
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
			sqlStr = "select t.rs_kh,t.zb_dx from ("
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
				ywlJson.put("zb", rsKhZbJson.getString("zb_dx"));//所在考核分组标准产品总包(网点考核)
			} catch (Exception e) {
				logger.error("orgid:"+orgid+"|zq"+zq+"jbjx_pid|"+jbjx_pid+"|jbjx_id:"+jbjx_id+",获取用户所在考核分组总人数、总包失败");
				tmd.put("errorCode", "11");
				tmd.put("errorMsg", "获取用户所在考核分组总人数、总包失败");
				tmd.put("finish", "1");
			}
		}
		logger.info("定性绩效计算（网点考核），所在考核分组总人数、业务量总包计算结果"+ywlJa.toJSONString());
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
	 * 设置当前用户可见所有员工的定性产品考核w值
	 * @param ja 当前所有可见用户
	 */
	public void setW(JSONArray userJa){
		for (int i = 0; i < userJa.size(); i++) {
			JSONArray userWJa = new JSONArray();//用户支行定性w值数组
			JSONObject userJson = userJa.getJSONObject(i);
			String orgId = userJson.getString("orgid");
			String userId = userJson.getString("user_id");
			iu.rmCondition(condition);
			iu.putCondition(condition, "orgId", orgId);
			sqlStr = " select user_id,orgid,depid,sum(cj) w,zq from(   "
					+ " select a.user_id,a.seq_num,a.rs,a.used,a.point,a.zq,b.orgid,b.depid,a.rs*a.point cj  "
					+ " from t_ntmisc_zhtsmx a,t_ntmisc_user b "
					+ " where a.user_id = b.userid and a.used='0' and a.seq_num in (select seq_num from t_ntmisc_orgzhts where orgid = ?) "
					+ " ) "
					+ " group by  user_id,orgid,depid,zq ";
			this.queryManu(userWJa, condition, sqlStr, daoParent, 1);
			for (int j = 0; j < userWJa.size(); j++) {
				JSONObject userWJson = userWJa.getJSONObject(j);
				if(userId.equals(userWJson.getString("user_id"))){
//					System.out.println(userWJson.getString("user_id"));
					userJson.put("w", userWJson.getString("w"));
					userJson.put("zq", userWJson.getString("zq"));
//					logger.info("############################:"+userJson.toJSONString());
				}
			}
		}
		System.out.println("支行定性绩效计算，userJa设置w值后："+userJa.toJSONString());
		logger.info("支行定性绩效计算，userJa设置w值后："+userJa.toJSONString());
	}
	
	/**
	 * w值计算
	 * @param tmd
	 * @param userYwlJa
	 */
	public void wCalc(JSONArray userYwlJa,TransactionMapData tmd){
		String orgIn = tmd.get("orgIn").toString();//当前用户所在分支行下属所有网点号
		iu.rmCondition(condition);
		iu.putCondition(condition, "zq", tmd.get("zqCurr"));
		sqlStr = " select a.user_id,a.point w,a.oper_time,a.used,a.zq,b.orgid,b.depid,c.jbjx_pid,c.jbjx_pname,c.jbjx_id,c.jbjx_name " 
				+  " from t_ntmisc_dx a,t_ntmisc_user b,t_ntmisc_jobjx c " 
				+  " where a.user_id = b.userid and b.jbjx_id = c.jbjx_id and a.used = '0' and a.zq=? and b.orgid in "
				+  orgIn;
		logger.info("执行sql,定性绩效计算（网点考核），计算w值,设置w值后,sqlStr:"+sqlStr+"------condition:"+condition);
		this.queryManu(userYwlJa, condition, sqlStr, daoParent, 1);
		logger.info("定性绩效计算（网点考核），计算w值,设置w值后，userYwlJa："+userYwlJa);
	}
	
	@Override
	public void doWork(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		this.setProgress(tmd);
		ja.clear();
		System.out.println("定性绩效计算");
		logger.info("定性绩效计算");
		
		/*tmd.put("errorCode", "12");
		tmd.put("errorMsg","定性绩效计算中...");
		tmd.put("progress", "0.9");
		tmd.put("finish", "0");*/
		
		JSONArray zbblJa = new JSONArray();//机构总包比例
		this.getZbbl(zbblJa);//查询总包比例
		
		//查询所有可见定性明细数据
		JSONArray userYwlJa = new JSONArray();// 当前用户可见定性原始数据
//		String userId = tmd.get("userId").toString();
//		String orgId = mediumBus.getUserOrg(userId);
//		String orgIn = mediumBus.getOrgIn(orgId);
		
		//查询所有可见的存在于支行定性明细表中的用户
//		JSONArray userJa = new JSONArray();
//		JSONArray userJa = ja;
		
		this.wCalc(userYwlJa, tmd);
		this.levelNum(userYwlJa,tmd);//当前用户同一层级柜员数目、定性总包设置
		this.fwCalc(userYwlJa);//计算fw、fwsum
		this.jxCalc(userYwlJa);//最终定性绩效计算
		
	}
}
