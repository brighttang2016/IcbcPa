/**
 * 邮件类接口，具体邮件类功能定义
 */
package com.icbc.nt.mail;
import com.alibaba.fastjson.JSONArray;

public interface  MailInterface {
	public void mailRecQuery(String applyId,JSONArray ja);//邮件接收方查询
	public void actionToMail(MailEvent mailEvent);//插入邮件发送表
}
