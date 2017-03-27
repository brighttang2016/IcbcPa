/**
 * 2015-02-13
 * 库存扫描job
 * inventory scan
 */
package com.icbc.nt.scheduler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.dom.DaoParent;
import com.icbc.nt.mail.MailEvent;
import com.icbc.nt.mail.MailIvtScan;

public class IvtScanSchduler extends SchedulerParent{
	private MailIvtScan mailIvtScan;
	
	public MailIvtScan getMailIvtScan() {
		return mailIvtScan;
	}

	public void setMailIvtScan(MailIvtScan mailIvtScan) {
		this.mailIvtScan = mailIvtScan;
	}

	/**
	 * 扫描库存
	 */
	public void ivtScan(){
		System.out.println("****************库存定时扫描，当前线程:"+Thread.currentThread().getId()+"**************");
		logger.info("****************库存定时扫描，当前线程:"+Thread.currentThread().getId()+"**************");
		HashMap<String,Object> retMap = new HashMap<String,Object>();
		JSONArray  ja = new JSONArray();
		LinkedHashMap<String,Object> condition = new LinkedHashMap<String,Object>();
		String sqlStr = "select * from t_consb_kc where to_number(consb_unum) <= to_number(consb_minnum)";
//		this.extQuery(ja, condition, sqlStr, daoObj, 1);
		this.queryManu(ja, condition, sqlStr, daoParent, 1);
		System.out.println("扫描结果,库存不足商品数目:"+ja.size());
		if(ja.size() > 0){//存在库存不足物品，向主办发送邮件
			mailIvtScan.actionToMail(new MailEvent("","","",retMap));
			/*iu.rmCondition(condition);
			ja.clear();
			condition.put("role_id", "382");//主办负责人采购
			sqlStr = "select * from t_consb_user a where a.user_id in(select user_id from t_consb_userrole where role_id =?)";
			this.extQueryManu(ja, condition, sqlStr, daoObj, 1);
			System.out.println("主办人数:"+ja.size());
			for (int i = 0; i < ja.size(); i++) {
				JSONObject json = ja.getJSONObject(i);
				iu.rmCondition(condition);
				condition.put("mail_rcver",json.get("user_mail"));
				condition.put("mail_sub", "库存不足通知");
				condition.put("mail_cont", "库存中部分物品库存不足，可在耗材管理系统中查询，谢谢！");
				sqlStr = "insert into t_consb_mail(mail_id,mail_rcver,mail_sub,mail_cont) values(SEQ_CONSB_MAILID.NEXTVAL,?,?,?)";
				this.updateExt(ja, condition, daoObj,retMap, sqlStr, 1);
			}*/
			
		}
	}
}
