package com.icbc.nt.mail;

import java.util.HashMap;
import java.util.LinkedHashMap;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.util.IcbcUtil;

public class MailToLd extends BusParent implements MailInterface{
	/**
	 * 写入邮件发送表
	 */
	public void actionToMail(MailEvent mailEvent) {
		// TODO Auto-generated method stub
		logger.info("actionToMail");
		LinkedHashMap<String,Object> condition = new LinkedHashMap<String, Object>();
		HashMap<String,String> retMap = new HashMap<String,String>();
		JSONArray ja = new JSONArray();
		this.mailRecQuery(mailEvent.applyId,ja);
		for (int i = 0; i < ja.size(); i++) {
			JSONObject json = ja.getJSONObject(i);
			iu.rmCondition(condition);
			condition.put("mail_rcver",json.get("user_mail"));
			condition.put("mail_sub", "新申请待审批通知(部门领导)");
			condition.put("mail_cont", "您部门有新物品申请提交，请登录物品设备管理系统处理，谢谢！");
			condition.put("apply_id", mailEvent.applyId);
			condition.put("mail_time", new IcbcUtil().getTime());
			String sqlStr = "insert into t_consb_mail(mail_id,mail_rcver,mail_sub,mail_cont,apply_id,mail_time) values(SEQ_CONSB_MAILID.NEXTVAL,?,?,?,?,?)";
//			this.updateExt(ja, condition, daoObj,retMap, sqlStr, 1);
//			this.update(condition, daoParent, sqlStr);
			this.update(condition, daoParent, sqlStr, retMap);
		}
	}
/**
 * 邮件接收方查询
 */
	public void mailRecQuery(String applyId,JSONArray ja) {
		// TODO Auto-generated method stub
		logger.info("mailRecQuery");
		LinkedHashMap<String,Object> condition = new LinkedHashMap<String, Object>();
		condition.put("apply_id", applyId);
		String sqlStr = "select user_mail from "
				+ " ("
				+ " select a.*,b.role_id from t_consb_user a"
				+ " left join"
				+ " (select * from t_consb_userrole) b"
				+ " on a.user_id = b.user_id"
				+ " where  b.role_id = '04'"//04:部门领导(机构审批)
				+ " )a,"
				+ " ("
				+ " select a.org_id from t_consb_user a,"
				+ " (select apply_id,user_id from t_consb_apply  where apply_id = ?) b"
				+ " where a.user_id = b.user_id" 
				+ " ) b"
				+ " where a.org_id = b.org_id";
//		this.extQueryManu(ja, condition, sqlStr, daoObj, 1);
		this.queryManu(ja, condition, sqlStr, daoParent, 1);
		
	}
}
