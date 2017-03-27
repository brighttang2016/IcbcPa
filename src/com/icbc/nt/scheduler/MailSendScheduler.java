/**
 * 邮件发送job
 */
package com.icbc.nt.scheduler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class MailSendScheduler extends SchedulerParent{
	/**
	 * 2015-02-13
	 * 逐条扫描邮件发送表，生成notes邮件发送文件
	 */
	public void mailSend(){
		System.out.println("****************邮件发送扫描，当前线程:"+Thread.currentThread().getId()+"**************");
		logger.info("****************邮件发送扫描，当前线程:"+Thread.currentThread().getId()+"**************");
		String filePath = iu.getProperty("consboff_notes_mailpath");
		String fileName = "";
		File file = new File(filePath);
		if(!file.exists()){
			file.mkdirs();
		}
		
		JSONArray  ja = new JSONArray();
		LinkedHashMap<String,Object> condition = new LinkedHashMap<String,Object>();
		String sqlStr = "select * from t_consb_mail";
//		this.extQuery(ja, condition, sqlStr, daoObj, 1);
		iu.rmCondition(condition);
		this.queryManu(ja, condition, sqlStr, daoParent, 1);
		System.out.println("待发送邮件条数:"+ja.size());
		for (int i = 0; i < ja.size(); i++) {
			JSONObject json = ja.getJSONObject(i);
			fileName = "consbsci_notes_"+json.getString("mail_id")+".txt";
			File fileTemp = new File(filePath+"\\\\"+fileName);
			if(fileTemp.exists()){
//				System.out.println("删除文件");
				fileTemp.delete();
			}
			StringBuffer sb = new StringBuffer();
			sb.append("[收件人]\r\n");
			sb.append(json.getString("mail_rcver")+"\r\n");
			sb.append("[主题]"+"\r\n");
			sb.append(json.getString("mail_sub")+"\r\n");
			sb.append("[正文]"+"\r\n");
			sb.append(json.getString("mail_cont")+"\r\n");
			try {
				FileOutputStream fos = new FileOutputStream(fileTemp);
				OutputStreamWriter osw = new OutputStreamWriter(fos, "gbk");
				System.out.println("生成notes邮件发送文件");
				osw.write(sb.toString());
				osw.flush();
				osw.close();
				iu.rmCondition(condition);
				condition.put("mail_id", json.getString("mail_id"));
				sqlStr = "delete t_consb_mail t where t.mail_id=?";
//				this.updateExt(ja, condition, daoObj, new HashMap(), sqlStr, 3);
//				this.update(condition, daoParent, sqlStr);
				HashMap retMap = new HashMap();
				this.update(condition, daoParent, sqlStr, retMap);
			} catch (Exception e) {
				logger.error(e);
				e.printStackTrace();
			}
		}
	}
}
