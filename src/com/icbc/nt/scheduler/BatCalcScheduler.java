/**
 * 批量定时任务2015-10-29
 */
package com.icbc.nt.scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.directwebremoting.json.types.JsonArray;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.nt.bus.MediumBus;

public class BatCalcScheduler extends SchedulerParent{
	@Autowired
	private MediumBus mediumBus;
	/**
	 * 批处理扫描（测试中.......2015-10-30）
	 */
	public void batScan(){
		System.out.println("***************批量任务扫描***************");
		Scheduler scheduler = null;
		SimpleScheduleBuilder ssdb = null;
		CronScheduleBuilder csb = null;
		JSONArray ja = new JSONArray();
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
			JobBuilder jobBuilder = JobBuilder.newJob(PointBat.class);
			JobDetail jobDetail = jobBuilder.build();
			System.out.println("************扫描定时任务表**************");
			iu.rmCondition(condition);
			iu.putCondition(condition, "cron_tag", "jfpljs");
			sqlStr = "select cron_name,cron_exp from t_ntmisc_cron where cron_tag=?";
			this.queryManu(ja, condition, sqlStr, daoParent, 1);
			String cronExp = ja.getJSONObject(0).getString("cron_exp");
			System.out.println("cronExp:"+cronExp);
//			CronExpression ce = new CronExpression("0/1 * 0-23 * * ?");//每天17点，每隔5分钟触发一次
			CronExpression ce = new CronExpression(cronExp);//每天17点，每隔5分钟触发一次
			csb = CronScheduleBuilder.cronSchedule(ce);
			TriggerBuilder<CronTrigger> tb = TriggerBuilder.newTrigger().withSchedule(csb);
			Trigger trigger = tb.build();
			scheduler.scheduleJob(jobDetail, trigger);
			scheduler.start();
//			logger.info("started............");
		} catch (Exception e) {
			System.out.println("抛出异常");
			//e.printStackTrace();
		}
	}
	
	
	/**
	 * 职务层级自动晋升批量计算
	 */
	public void jobBat(){
		System.out.println("***********职务层级自动晋升批量计算*************");
		boolean upFlag = false;//晋升条件满足标识 true：满足晋升条件 false：不满足晋升条件
		String nextJbId = "0";//下一职务层级
		float sGrade = 0;//职务层级起始工资等级
		float sLevel = 0;//职务层级起始工资档次
		String ctId = "";//岗位层级所需资格
		JSONArray nextJobArray = new JSONArray();//下一职务层级
		JSONArray userJa = new JSONArray();
		HashMap<String,Object> retMap = new HashMap<String, Object>();
		iu.rmCondition(condition);
		sqlStr = "select a.*,b.seq_num,c.chg_time from t_ntmisc_user a,t_ntmisc_job b,t_ntmisc_jobhis c where a.jb_id = b.jb_id and a.userid = c.user_id and a.jb_id = c.jb_id";
		this.queryAuto(userJa, condition, sqlStr, daoParent, 1);
		LinkedList<LinkedHashMap<String, Object>> condList = new LinkedList<LinkedHashMap<String,Object>>();
		for (int i = 0; i < userJa.size(); i++) {
			LinkedHashMap<String, Object> condMap = new LinkedHashMap<String, Object>();
			JSONObject userJson = userJa.getJSONObject(i);
			String jbId = userJson.getString("jb_id");
			String userId = userJson.getString("userid");
			int seqNum = userJson.getIntValue("seq_num");//当前岗位层级序号
			String fbFlag = userJson.getString("fb_flag");//禁止晋升标识
			String jbChgTime = userJson.getString("chg_time");//当前岗位层级获取时间
			float wgValue = userJson.getFloat("wg_value");//当前工资等级
			float wlValue = userJson.getFloat("wl_value");//当前工资档次
			JSONArray jobArray = new JSONArray();
			mediumBus.getJobCat(jbId, jobArray);
			logger.info("userid:"+userId+"|jbId:"+jbId+"对应的岗位类别，jobArray="+jobArray);
			JSONObject jobCatJson = jobArray.getJSONObject(0);
			String jbCatName = jobCatJson.getString("jb_name").trim();//岗位类别
			
			
			if(fbFlag.equals("1")){
				logger.info("jbCatName:"+jbCatName);
				logger.info(!jbCatName.equals("管理类岗位"));
				if(!jbCatName.equals("管理类岗位")){
					//获取下一岗位层级工资等级档次要求
					mediumBus.getNextJob(jbId, nextJobArray);
					if(nextJobArray.size() > 0){
						JSONObject nextJobJson = nextJobArray.getJSONObject(0);
						nextJbId = nextJobJson.getString("jb_id");
						sGrade = nextJobJson.getFloat("s_grade");
						sLevel = nextJobJson.getFloat("s_level");
						ctId = nextJobJson.getString("ct_id");
					}
					if(iu.dateCompare(jbChgTime, 2)){//任职时间大于两年(和当前日期比较)
						if(wgValue >= sGrade && wlValue >= sLevel){
							JSONArray userCtArray = new JSONArray();
							mediumBus.userCtQuery(userId, userCtArray);
							boolean ctExistFlat = false;
							for (int j = 0; j < userCtArray.size(); j++) {
								JSONObject userCtJson = userCtArray.getJSONObject(j);
								if(userCtJson.getString("ct_id").equals(ctId)){
									ctExistFlat = true;
								}
							}
							if(ctExistFlat){
								if(jbCatName.equals("销售类岗位") || jbCatName.equals("专业类岗位")){
									if(seqNum <= 2){
										upFlag = true;//满足所有晋升条件，准许晋升
									}else{
										logger.info("岗位层级无法晋升，岗位类别为销售类和专业类岗位，则当前职务层级顺序号需<=2，用户号userId："+userId+"|当前职位jbId："+jbId+"|置为顺序号seqNum:"+seqNum);
									}
								}else{//非销售岗位和专业岗位
									upFlag = true;//满足所有晋升条件，准许晋升
								}
							}else{
								logger.info("岗位层级无法晋升，不具备升级后岗位的认证资格要求");
							}
						}else{
							logger.info("岗位层级无法晋升，下一岗位层级所需的工资等级或工资档次不足，员工号："+userId);
						}
					}else{
						logger.info("岗位层级无法晋升，任职时间不足两年");
					}
				}else{
					logger.info("管理岗位层级无法自动晋升，userId:"+userId+"当前岗位jbId："+jbId+"岗位类别jbCatName："+jbCatName);
				}
			}else{
				logger.info("禁止晋升,用户userId:"+userId);
			}
			if(upFlag){
				//开始晋升
				iu.rmCondition(condition);
				iu.putCondition(condition, "jbId", nextJbId);
				iu.putCondition(condition, "userId", userId);
				sqlStr = "update t_ntmisc_user t set t.jb_id = ? where userid = ?";
				this.update(condition, daoParent, sqlStr, retMap);
				//插入岗位调整历史表
				iu.rmCondition(condition);
				iu.putCondition(condition, "userId", userId);
				iu.putCondition(condition, "jbId", nextJbId);
				iu.putCondition(condition, "chgTime", iu.getDate());
				sqlStr = "insert into t_ntmisc_jobhis(user_id,jb_id,chg_time) values(?,?,?)";
				this.update(condition, daoParent, sqlStr, retMap);
				logger.info("晋升成功，用户号:"+userId);
			}else{
				logger.info("无法晋升，晋升条件不满足");
			}
		}
	}
	
	/**
	 * 工资等级档次批量计算
	 */
	public void wglBat(){
		System.out.println("***********工资等级档次批量计算*************");
		JSONArray userJa = new JSONArray();
		HashMap<String,Object> retMap = new HashMap<String, Object>();
		iu.rmCondition(condition);
		sqlStr = "select a.*,b.m_grade  from t_ntmisc_user a,t_ntmisc_job b where a.jb_id = b.jb_id";
		this.queryAuto(userJa, condition, sqlStr, daoParent, 1);
		LinkedList<LinkedHashMap<String, Object>> condList = new LinkedList<LinkedHashMap<String,Object>>();
		for (int i = 0; i < userJa.size(); i++) {
			LinkedHashMap<String, Object> condMap = new LinkedHashMap<String, Object>();
			JSONObject userJson = userJa.getJSONObject(i);
			String userId = userJson.getString("userid");
			float point = 0;
			int wgValue = 1;
			int wLValue = 1;
			String fbFlag  = "0";//初始化为禁止晋升
			try {
				point = userJson.getFloatValue("point");
			} catch (Exception e) {}
			try {
				wgValue = userJson.getIntValue("wg_value");
			} catch (Exception e) {}
			try {
				wLValue = userJson.getIntValue("wl_value");
			} catch (Exception e) {}
			try {
				fbFlag = userJson.getString("fb_flag");
			} catch (Exception e) {}
			
			int mGrade = userJson.getIntValue("m_grade");
			logger.info("**********mGrade:"+mGrade);
			int wgNew = wgValue;
			int wLNew = wLValue;
			if("1".equals(fbFlag)){//禁止晋升标识 1：允许晋升 0：禁止晋升
				if(point >= 3){
					if(wLValue != 7){
						if(wLValue < 3){
							wgNew = wgValue;
							wLNew = wLValue+1;
						}else if(wLValue >= 3 && wLValue < 7 && wgValue >= mGrade){
							wgNew = wgValue;
							wLNew = wLValue+1;
						}else if(wLValue >= 3  && wgValue < mGrade){
							wgNew = wgValue+1;
							wLNew = wLValue-2;
						}
						
						logger.info("可以晋升工资等级及档次:"+userId+",新工资等级及档次:wgNew|wLNew="+wgNew+"|"+wLNew+"**************晋升前积分："+point+"晋升后积分："+(point-3));
					}else if(wLValue == 7){
						wgNew = wgValue+1;
						wLNew = 1;
						logger.info("工资档次已达当前工资等级的最高工资档次，用户号："+userId);
//						logger.info("工资档次已达当前职位层级最高工资等级的最高工资档次，用户号："+userId);
					}
					condMap.put("wgValue", wgNew);
					condMap.put("wLValue", wLNew);
					condMap.put("point", (point-3)+"");
					condMap.put("userId", userId);
					condList.add(condMap);
				}else{
					logger.info("积分不足3分，无法晋升工资等级及工资档次，员工号："+userId);
				}
			}else{
				logger.info("禁止晋升员工号："+userId);
			}
		}
		sqlStr = "update t_ntmisc_user set wg_value = ?,wl_value = ?,point = ? where userid= ?";
		this.updateBat(condList, daoParent, retMap, sqlStr, 1);
		logger.info("员工工资等级及档次批量修改条数 retMap.get(\"recUpdCount\")："+retMap.get("recUpdCount"));
	}
	
	/**
	 * 积分批量计算
	 */
	public void pointBat(){
		System.out.println("***********积分批量计算*************");
		logger.info("nzkhCalc 根据学习积分计算年终考核");
		JSONArray ja = new JSONArray();
		Map retMap = new HashMap();
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		iu.rmCondition(condition);
		//1、查询所有用户学习积分
		sqlStr = "select t.user_id,t.orgid,sum(point) sump,t.ass_flag "
				+ " from (select a.*,b.orgid,b.ass_flag from t_ntmisc_credit a,t_ntmisc_user b where a.user_id = b.userid) t"
				+ " group by t.user_id,t.orgid,t.ass_flag";
//		String userOrgId = mediumBus.getUserOrg(tmd.get("userId").toString());
//		sqlStr = mediumBus.getSqlWithIn(sqlStr, userOrgId);
		this.queryManu(ja, condition, sqlStr, daoParent, 1);
		
		//2、查询考核标准
		JSONArray assptJa = new JSONArray();
		sqlStr = "select * from t_ntmisc_asspt t where t.point != '0' order by cond asc";
		iu.rmCondition(condition);
		this.queryAuto(assptJa, condition, sqlStr, daoParent, 1);
		for (int i = 0; i < assptJa.size(); i++) {
			JSONObject json = assptJa.getJSONObject(i);
			logger.info("考核标准"+i+":"+json.getString("asspt_name")+json.getString("cond"));
		}
		for (int i = 0; i < ja.size(); i++) {//循环用户学习积分
			String point = "";//考核积分
			String assptId = "";//考核结果
			JSONObject rowJson = ja.getJSONObject(i);//用户积分行
			String assFlag = rowJson.getString("ass_flag");//参与考核标识 1:参与考核 0：不参与考核
			String userIdTemp = rowJson.getString("user_id");
			logger.info("行"+i+":"+rowJson.getString("user_id")+"|"+rowJson.getString("orgid")+"|"+rowJson.getString("sump"));
			int creditPoint = Integer.parseInt(rowJson.getString("sump"));//学习积分
//			计算有学习积分的用户当年的考核积分
			if("0".equals(assFlag)){
				point = "0";
				assptId = "6";
			}else{
				for (int j = 0; j < assptJa.size(); j++) {
					JSONObject jsonNow = assptJa.getJSONObject(j);
					int startPoint = jsonNow.getIntValue("cond");//考核条件区间下限;
					int endPoint = 0;
					if(j < assptJa.size()-1){
						JSONObject jsonNext = assptJa.getJSONObject(j+1);
						endPoint = jsonNext.getIntValue("cond");
					}else if(j == assptJa.size()-1){
						endPoint = 1000;//满分100分,尽量设置一个较大的上限1000
					}
					if(creditPoint >= startPoint && creditPoint < endPoint){
						point = jsonNow.getString("point");
						assptId = jsonNow.getString("asspt_id");
						logger.info("考核积分："+point);
					}
				}
			}
			if("0".equals(assFlag)){
				iu.rmCondition(condition);
				iu.putCondition(condition, "userId", userIdTemp);
				iu.putCondition(condition, "assTime", iu.getYear());
				iu.putCondition(condition, "assptId", "6");
				iu.putCondition(condition, "opperTime", iu.getTime());
				iu.putCondition(condition, "assFlag", "0");
				sqlStr = "insert into t_ntmisc_asshis(user_id,ass_time,asspt_id,oper_time,ass_flag) values(?,?,?,?,?)";
				this.update(condition, daoParent, sqlStr, retMap);
			}else{
//				3、写入考核结果表
				iu.rmCondition(condition);
				iu.putCondition(condition, "userId", userIdTemp);
				iu.putCondition(condition, "assTime", iu.getYear());
				iu.putCondition(condition, "assptId", assptId);
				iu.putCondition(condition, "opperTime", iu.getTime());
				iu.putCondition(condition, "assFlag", assFlag);
				sqlStr = "insert into t_ntmisc_asshis(user_id,ass_time,asspt_id,oper_time,ass_flag) values(?,?,?,?,?)";
				this.update(condition, daoParent, sqlStr, retMap);
				//4、写入用户考核积分历史表
				iu.rmCondition(condition);
				iu.putCondition(condition, "userId", userIdTemp);
				iu.putCondition(condition, "chg_time", iu.getDate());
				iu.putCondition(condition, "point", point+"");
				iu.putCondition(condition, "type", "0");
				iu.putCondition(condition, "note", "基础积分");
				iu.putCondition(condition, "used", "0");
				sqlStr = "insert into t_ntmisc_pointhis(user_id,chg_time,point,type,note,used) values(?,?,?,?,?,?)";
				this.update(condition, daoParent, sqlStr, retMap);
			}
		}
		
//		5、计算用户最终积分
		iu.rmCondition(condition);
		JSONArray pointhisJa = new JSONArray();//积分历史表未参与计算积分数据
		JSONArray userJa = new JSONArray();//用户表原始积分数据
		sqlStr = "select a.user_id,sum(a.point) point_sum from t_ntmisc_pointhis a where a.used = '0' group by a.user_id";
		this.queryAuto(pointhisJa, condition, sqlStr, daoParent, 1);
		sqlStr = "select t.userid,t.point,t.ass_flag from t_ntmisc_user t";
		this.queryAuto(userJa, condition, sqlStr, daoParent, 1);
		
		LinkedList<LinkedHashMap<String, Object>> condList = new LinkedList<LinkedHashMap<String,Object>>();
		for (int k = 0; k < pointhisJa.size(); k++) {
			JSONObject rowJson = pointhisJa.getJSONObject(k);
			float pointAdd = rowJson.getFloatValue("point_sum");
			String userIdLoop = rowJson.getString("user_id");//积分历史表当前循环用户id
			logger.info(rowJson.getString("user_id")+"新增积分："+pointAdd);
			logger.info("未使用积分："+rowJson.toJSONString());
			for (int i = 0; i < userJa.size(); i++) {
				LinkedHashMap<String, Object> rowMap = new LinkedHashMap<String, Object>();
				JSONObject userJson = userJa.getJSONObject(i);
				String userIdTarget = userJson.getString("userid");//用户表用户id
				if("1".equals(userJson.getString("ass_flag")) && userIdLoop.equals(userIdTarget)){//参与年终考核
					float pointNow = 0;
					try {
						pointNow = userJson.getFloatValue("point");
					} catch (Exception e) {
						logger.info(userIdTarget+"原始积分为空");
					}
					rowMap.put("point", pointNow+pointAdd);
					rowMap.put("userId", userIdTarget);
					condList.add(rowMap);
//					6、积分历史对应记录使用标识置为 1
					iu.rmCondition(condition);
					iu.putCondition(condition, "used", "1");
					iu.putCondition(condition, "userId", userIdTarget);
					sqlStr = "update t_ntmisc_pointhis t set t.used = ? where t.user_id = ? and t.used = '0'";
					this.update(condition, daoParent, sqlStr, retMap);
				}
			}
		}
//		7、更改用户积分
		sqlStr = "update t_ntmisc_user t set t.point = ? where t.userid = ?";
		this.updateBat(condList, daoParent, retMap, sqlStr, 1);
	}
}
