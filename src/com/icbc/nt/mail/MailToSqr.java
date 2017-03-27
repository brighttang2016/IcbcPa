/**
 * 发往申请人
 */
package com.icbc.nt.mail;

import java.util.HashMap;
import java.util.LinkedHashMap;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.util.IcbcUtil;

public class MailToSqr extends BusParent implements MailInterface{

	public void mailRecQuery(String applyId, JSONArray ja) {
		// TODO Auto-generated method stub
		logger.info("mailRecQuery");
		LinkedHashMap<String,Object> condition = new LinkedHashMap<String, Object>();
		condition.put("apply_id", applyId);
		String sqlStr = "select user_mail from t_consb_user a,"
				+ " (select user_id from t_consb_apply t where t.apply_id = ?) b"
				+ " where a.user_id = b.user_id";
//		this.extQueryManu(ja, condition, sqlStr, daoObj, 1);
		this.queryManu(ja, condition, sqlStr, daoParent, 1);
	}

	public void actionToMail(MailEvent mailEvent) {
		// TODO Auto-generated method stub
		logger.info("actionToMail");
		LinkedHashMap<String,Object> condition = new LinkedHashMap<String, Object>();
		HashMap<String,String> retMap = new HashMap<String,String>();
		JSONArray ja = new JSONArray();//邮件接收者json数组
		this.mailRecQuery(mailEvent.applyId,ja);
		for (int i = 0; i < ja.size(); i++) {
			JSONObject json = ja.getJSONObject(i);
			iu.rmCondition(condition);
			condition.put("mail_rcver",json.get("user_mail"));
			condition.put("mail_sub", mailEvent.mailSub);
			condition.put("mail_cont", mailEvent.mailCont);
			condition.put("apply_id", mailEvent.applyId);
			condition.put("mail_time", new IcbcUtil().getTime());
			String sqlStr = "insert into t_consb_mail(mail_id,mail_rcver,mail_sub,mail_cont,apply_id,mail_time) values(SEQ_CONSB_MAILID.NEXTVAL,?,?,?,?,?)";
//			this.updateExt(ja, condition, daoObj,retMap, sqlStr, 1);
//			this.update(condition, daoParent, sqlStr);
			this.update(condition, daoParent, sqlStr, retMap);
		}
	}

}
