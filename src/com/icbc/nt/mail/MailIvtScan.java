package com.icbc.nt.mail;

import java.util.HashMap;
import java.util.LinkedHashMap;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.dom.DaoParent;
import com.icbc.nt.util.IcbcUtil;

public class MailIvtScan extends BusParent implements MailInterface {

	public void mailRecQuery(String applyId, JSONArray ja) {
		// TODO Auto-generated method stub
		logger.info("mailRecQuery");
		LinkedHashMap<String,Object> condition = new LinkedHashMap<String, Object>();
		condition.put("role_id", "06");//暂定库管接管采购工作
		String sqlStr = "select * from t_consb_user a where a.user_id in(select user_id from t_consb_userrole where role_id =?)";
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
			condition.put("mail_sub", "库存不足通知");
			condition.put("mail_cont", "库存中部分物品库存不足，可在耗材管理系统中查询，谢谢！");
			condition.put("mail_time", new IcbcUtil().getTime());
			String sqlStr = "insert into t_consb_mail(mail_id,mail_rcver,mail_sub,mail_cont,mail_time) values(SEQ_CONSB_MAILID.NEXTVAL,?,?,?,?)";
//			this.updateExt(ja, condition, daoObj,retMap, sqlStr, 1);
//			this.update(condition, daoParent, sqlStr);
			this.update(condition, daoParent, sqlStr, retMap);
		}
	}
}
