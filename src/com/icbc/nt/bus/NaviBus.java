/**
 * 导航初始化业务逻辑（和NaviInit功能相同,目前使用）
 * brighttang 2015-11-09
 * 左导航初始化程序
 */
package com.icbc.nt.bus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import com.icbc.nt.dom.DaoParent;
import com.icbc.nt.util.IcbcUtil;

@Transactional
public class NaviBus extends BusParent{
	@Value("${sysMode}")
	private String sysMode;
	String menu_text = "";
	String menu_id = "";
	String menu_type = "";
	String url_text = "";
	String auth_id = "";
	String auth_name = "";
	String node_type = "";
	
/*	static ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
	static BusParent busParent = (BusParent) ac.getBean("busParent");
	static DaoParent objDao = (DaoParent) ac.getBean("daoParent");
	static NaviInit naviInit = (NaviInit) ac.getBean("naviInit");
	static IcbcUtil iu = busParent.getIu();
	static LinkedHashMap<String, Object> condition = new LinkedHashMap<String, Object>();
	String sqlStr = "";*/
	LinkedHashMap<String, Object> condition = new LinkedHashMap<String, Object>();
	public void naviClean(){
		System.out.println("清理开始。。。");
		Map retMap = new HashMap();
		iu.rmCondition(condition);
		String sqlStr = "delete T_ntmisc_AUTHINFO";
		this.update(condition, daoParent, sqlStr,retMap);
		sqlStr = "delete T_ntmisc_MENU";
		this.update(condition, daoParent, sqlStr,retMap);
		System.out.println("清理开始结束");
	}
	
	public boolean nodeIsExist(LinkedHashMap<String, Object> condition,String sqlStr){
		boolean ret = false;
		List<Map<String,String>> tableList = new ArrayList<Map<String,String>>();	
		this.queryManu(tableList, condition, sqlStr, daoParent, 1);
		if(tableList.size() > 0){
			System.out.println("*******"+menu_id+"节点已存在**********");
			ret = true;
		}
		return ret;
	}
	
	public  void initAuth(List<Element> list,BusParent busParent,DaoParent daoParent){
		
	}
	
	public void initMenu(List<Element> list){
		Map retMap = new HashMap();
		for(Element element:list){
			menu_text = element.attributeValue("menu_text");
			menu_id = element.attributeValue("menu_id");
			menu_type = element.attributeValue("menu_type");
			url_text = element.attributeValue("url_text");
			auth_id = element.attributeValue("auth_id");
			auth_name = element.attributeValue("auth_name");
			if(!"3".equals(menu_type)){//非权限节点
				iu.rmCondition(condition);
				iu.putConditionAll(condition, "menu_id", menu_id);
				sqlStr = "select * from t_ntmisc_menu t where t.menu_id = ?";
				if(!this.nodeIsExist(condition,sqlStr)){
					sqlStr = "insert into t_ntmisc_menu(menu_id,parent_id,menu_text,node_type,url_text) values(?,?,?,?,?)";
					iu.rmCondition(condition);
					iu.putConditionAll(condition, "menu_id", menu_id);
					iu.putConditionAll(condition, "parent_id", element.getParent().attributeValue("menu_id"));
					iu.putConditionAll(condition, "menu_text", menu_text);
					iu.putConditionAll(condition, "menu_type", menu_type);
					iu.putConditionAll(condition, "url_text", url_text);
					System.out.println("t_ntmisc_menu表插入节点condition:"+condition);
					this.update(condition, daoParent, sqlStr,retMap);
				}
				this.initMenu(element.elements());
			}else{//设置权限
				iu.rmCondition(condition);
				iu.putCondition(condition, "authority_id", menu_id);
				sqlStr = "select * from t_ntmisc_authinfo t where t.authority_id = ?";
				if(!this.nodeIsExist(condition,sqlStr)){
					sqlStr = "insert into t_ntmisc_authinfo(authority_id,authority_class,authority_cn,node_type) values(?,?,?,?)";
					iu.rmCondition(condition);
					iu.putConditionAll(condition, "authority_id", menu_id);
					iu.putConditionAll(condition, "authority_class", element.getParent().attributeValue("menu_text"));
					iu.putConditionAll(condition, "authority_cn", menu_text);
					iu.putConditionAll(condition, "node_type", menu_type);
					System.out.println("t_ntmisc_authinfo表插入节点condition:"+condition);
					this.update(condition, daoParent, sqlStr,retMap);
				}
				
				iu.rmCondition(condition);
				iu.putConditionAll(condition, "menu_id", element.getParent().attributeValue("menu_id"));
				iu.putConditionAll(condition, "authority_id", menu_id);
				sqlStr = "select * from t_ntmisc_menuauth t where t.menu_id = ? and t.authority_id=?";
				if(!this.nodeIsExist(condition,sqlStr)){
					sqlStr = "insert into t_ntmisc_menuauth(menu_id,authority_id) values(?,?)";
					iu.rmCondition(condition);
					iu.putConditionAll(condition, "menu_id", element.getParent().attributeValue("menu_id"));
					iu.putConditionAll(condition, "authority_id", menu_id);
					System.out.println("t_ntmisc_menuauth表插入节点condition:"+condition);
					this.update(condition, daoParent, sqlStr,retMap);
				}
//				this.initMenu(element.elements(), busParent,daoParent);
//				this.initAuth(element.elements(), busParent);
//				System.out.println("auth_id:"+auth_id+"menu_type:"+menu_type);
			}
		}
//		throw new RuntimeException("抛出运行错误事物回滚测试");
	}
	
	public void  initStart(){
		logger.info("系统模式sysMode:"+sysMode);
		if("1".equals(sysMode)){
			logger.info("系统当前为【运行模式】，请在【开发模式】下设置导航条");
			return;
		}
		System.out.println("***************************导航初始化开始***************************");
		List<Element> list = iu.getNodeList("navi_cfg.xml", "nt", "http://www.nantian.com.cn/rmc/schema/navi", "//nt:root");
		this.naviClean();
		this.initMenu(list);
		System.out.println("***************************导航初始化结束***************************");
	}
}
