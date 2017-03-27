package com.icbc.nt.bus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.nt.dom.MenuDao;

@Service
public class MenuBus extends BusParent{
	@Autowired
	private MenuDao menuDao;
	@Autowired
	MediumBus mediumBus;
	
	public void naviRefresh(){
		mediumBus.naviRefresh();
	}
	
	public void setParentState(JSONArray ja,JSONObject json){
		System.out.println("setParentState");
		for (int i = 0; i < ja.size(); i++) {
			JSONObject jsonTemp = (JSONObject) ja.get(i);
			if(jsonTemp.getString("menu_id").equals(json.getString("parent_id"))){
				jsonTemp.put("checked", true);
				System.out.println("找到上级节点:"+jsonTemp.getString("menu_id"));
//				logger.info("找到上级节点:"+jsonTemp.getString("menu_id"));
				setParentState(ja,jsonTemp);
			}
		}
	}
	
	public void setMenuItem(JSONArray ja,HashMap<String, Object> condition){
//		logger.info("setMenuItem daoParent:"+this.daoParent);
		String sqlStr = "";
//		sqlStr = "select t.* from t_ntmisc_roleauth t where t.role_id='01'";
		sqlStr = "select t.* from t_ntmisc_roleauth t where t.role_id=?";
		JSONArray jaAuth = new JSONArray();
//		this.extQuery(jaAuth, condition, sqlStr, menuDao, 1);
		this.queryManu(jaAuth, condition, sqlStr, daoParent, 1);
//		logger.info("当前导航条菜单数据数目："+ja.size());
		for(int i = 0;i < ja.size();i++){
			JSONObject json = (JSONObject) ja.get(i);
			json.put("checked", false);
			System.out.println("jaAuth.size():"+jaAuth.size());
			for(int j = 0; j < jaAuth.size();j++){
				JSONObject jsonAuth = (JSONObject) jaAuth.get(j);
				if(jsonAuth.get("authority_id").equals(json.get("menu_id"))){
					json.put("checked", true);
					this.setParentState(ja, json);
				}
			}
//			logger.info("**********menu_id**********"+json.get("menu_id"));
			json.put("id", json.get("menu_id"));
			json.put("leaf", json.get("node_type").equals("3")?"true":"false");//node_type=3(菜单权限节点)
			json.put("expanded", true);
			json.put("children", new JSONArray());
		}
	}
	/**
	 * 查询所有menu节点
	 * @param ja
	 * @param menuId
	 * @param menuTreeId
	 */
	public void getMenuItemAll(JSONArray ja,HashMap<String, Object> condition){
		System.out.println("MenuBus  getMenuItemAll");
		
		//查询所有menu
		String sqlStr = "select * from t_ntmisc_menu where menu_id !='0'";
//		this.extQuery(ja, condition, sqlStr, menuDao, 1);
		this.queryManu(ja, condition, sqlStr, daoParent, 1);
		//查询所有menu权限
		sqlStr = "  select a.menu_id parent_id,a.authority_id menu_id,b.authority_cn menu_text,b.node_type from t_ntmisc_menuauth a"
				+ " left join"
				+ " t_ntmisc_authinfo b"
				+ " on a.authority_id = b.authority_id";
//		this.extQuery(ja, condition, sqlStr, menuDao, 1);
		this.queryManu(ja, condition, sqlStr, daoParent, 1);
		
		//查询用户具有的menu权限id
//		sqlStr = "select t.authority_id from t_ntmisc_roleauth t where t.role_id in(select role_id from t_ntmisc_userrole where user_id='021301')";
		//查询角色具有的menu权限id
//		menuDao.getMenuItemAll(ja);
		return;
	}
	
	/**
	 * 机构查询
	 * @param condition 条件hashmap
	 * @param ja
	 */
	public void menuQueryExt(JSONArray ja,HashMap<String, Object> condition){
//		logger.info("userQueryExt");
		String sqlStr = "select * from t_ntmisc_menu";
//		this.extQuery(ja, condition, sqlStr, menuDao,1);
		this.queryManu(ja, condition, sqlStr, daoParent, 1);
	}
	/**
	 * 获得menuId所有父辈节点id
	 * @param jaMenuId
	 * @param menuId
	 */
	public void getPrtMenuId(JSONArray jaMenuId,String menuId){
//		logger.info("当前节点menuId："+menuId);
		System.out.println("当前节点menuId："+menuId);
		LinkedHashMap<String, Object> condition = new LinkedHashMap<String, Object>();
		JSONArray jaTemp = new JSONArray();
		condition.put("menu_id", menuId);
		String sqlStr = "select parent_id menu_id from t_ntmisc_menu where menu_id = ?";
//		this.extQueryManu(jaTemp, condition, sqlStr, menuDao, 1);
		this.queryManu(jaTemp, condition, sqlStr, daoParent, 1);
		for(int i = 0;i < jaTemp.size();i++){
			int extFlag = 0;
			JSONObject jsonNow = (JSONObject) jaTemp.get(i);
			String munuIdNow = jsonNow.getString("menu_id");
			if(munuIdNow != null){
				for(int j = 0;j < jaMenuId.size();j++){
					JSONObject jsonPre = (JSONObject) jaMenuId.get(j);
					if(munuIdNow.equals(jsonPre.getString("menu_id")))
						extFlag  =1;//父节点已被其他兄弟节点选择
				}
				if(extFlag == 0){//jaMenuId不存在该id，父节点未被兄弟节点选择
//					logger.info("取得jaMenuId中未添加的上辈节点：jsonNow.getString(\"menu_id\"):"+jsonNow.getString("menu_id"));
					System.out.println("取得jaMenuId中未添加的上辈节点：jsonNow.getString(\"menu_id\"):"+jsonNow.getString("menu_id"));
					if(!"".equals(munuIdNow)){//节点“0”的上一级节点为“”，用户可见节点忽略此节点
						jaMenuId.add(jsonNow);
						if(!"0".equals(munuIdNow)){
							this.getPrtMenuId(jaMenuId, munuIdNow);
						}
					}
				}
			}
		}
	}
	
	/**
	 * 获取当前用户可见所有menuItem  id
	 */
	public String getMenuIdAll(JSONArray jaMenuId,String userId,LinkedHashMap<String, Object> condition){
//		logger.info("getMenuIdAll daoParent:"+this.daoParent);
		condition.put("user_id", userId);
		String sqlStr = " select t.menu_id from t_ntmisc_menuauth t "
				+ " where t.authority_id in"
				+ " ("
				+ " select t.authority_id from t_ntmisc_roleauth t"
				+ " where t.role_id in (select role_id from  t_ntmisc_userrole where user_id = ?)"
				+ " )" 
				+ " group by t.menu_id";
//		this.extQueryManu(jaMenuId, condition, sqlStr, menuDao, 1);
//		logger.info("jaMenuId:"+jaMenuId+"|"+"condition:"+condition+"|"+"daoParent:"+daoParent);
		this.queryManu(jaMenuId, condition, sqlStr, daoParent, 1);
//		this.extQuery(jaMenuId, condition, sqlStr, menuDao, 1);
		System.out.println("jaMenuId.size():"+jaMenuId.size());
		for(int i = 0;i < jaMenuId.size();i++){
			JSONObject  json = (JSONObject) jaMenuId.get(i);
//			logger.info("当前用户可见叶子节点menu_id:"+json.getString("menu_id"));
			System.out.println("当前用户可见叶子节点menu_id:"+json.getString("menu_id"));
			this.getPrtMenuId(jaMenuId, json.getString("menu_id"));
		}
		System.out.println(jaMenuId.size());
		return "";
	}
	/**
	 * 获取导航条menuItem
	 * @param ja
	 * @param menuId
	 * @param menuTreeId
	 */
	public void getMenuItem(JSONArray ja,String menuId,String menuTreeId,String userId,JSONArray jaMenuId){
//		System.out.println("MenuBus  getMenuItem");
//		logger.info("getMenuItem");
		String parentId = "";
		if(!menuTreeId.equals("-1")){//查询展开模块下tree
			parentId = menuTreeId;
		}else{
			parentId = menuId;//查询展开模块
		}
		
//		String strIn = this.getMenuIdAll(userId);
		String strIn = "(";
		for(int i = 0;i < jaMenuId.size();i++){
			JSONObject json = (JSONObject) jaMenuId.get(i);
			if(i < jaMenuId.size()-1)
				strIn += json.get("menu_id")+",";
			else 
				strIn += json.get("menu_id")+")";
		}
//		System.out.println("strIn:"+strIn);
		menuDao.getMenuItem(ja,parentId,userId,strIn);
		for (int i = 0; i < ja.size(); i++) {//设置节点id
			JSONObject json = ja.getJSONObject(i);
			json.put("id", json.get("menuId"));
		}
		return;
	}


	@Override
	public int getCount(HashMap<String, Object> condition,String tranCode) {
		// TODO Auto-generated method stub
		return 0;
	} 
}