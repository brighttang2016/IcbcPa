package com.icbc.nt.bus;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.aspectj.weaver.IUnwovenClassFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.nt.dom.DaoParent;
import com.icbc.nt.util.ConsbContext;
import com.icbc.nt.util.IcbcUtil;
import com.icbc.nt.util.TransactionMapData;

public  class BusParent implements Serializable{
	protected String sqlStr = "";
//	public DaoParent objDao;
	@Autowired
	protected DaoParent daoParent;
	
	protected HashMap<String,String> cfgMap = null;//存储配置文件consb_tran_cfg.xml解析结果
	/**
	 * 系统工具实例
	 */
	protected IcbcUtil iu;
	
	protected Logger logger = new IcbcUtil().getLogger();
	protected  String odName = "";//排序列
	protected String odWay="";//排序方式
	protected ConsbContext consbContext = null;
	
	protected LinkedHashMap<String,Object> condition = new LinkedHashMap<String,Object>();
	/**
	 * 系统变量池
	 */
	protected TransactionMapData tmd;
	protected String sql = "";
	
	public TransactionMapData getTmd() {
		return tmd;
	}
	
	public void setProgress(TransactionMapData tmd){
		try {
			Thread.currentThread().sleep(1000);
			tmd.put("errorCode", "12");
			tmd.put("errorMsg",tmd.get("progressName")+"计算中...");
			tmd.put("progress", tmd.get("progress"));
			tmd.put("finish", "0");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 设置文件生成处理进度条信息
	 * @param tmd
	 */
	/*public void setProgress(TransactionMapData tmd){
		tmd.put("errorCode", "12");//10：上传成功，异常进度条提示信息;11：上传处理异常，不关闭进度条提示信息;12:文件上传处理中
		tmd.put("errorMsg","标准产品绩效计算中...");//进度条文本
		tmd.put("progress", "0.5");//进度条当前进度百分比
		tmd.put("finish", "0");//1:停止推送 0：继续推送
		tmd.put("data", "正在处理,请稍候...");
	}*/
	
	public void setTmd(TransactionMapData tmd) {
		System.out.println("BusParent->setTmd****"+Thread.currentThread().getName()+"****初始化bean:"+this.getClass().getName());
//		daoParent.isConnected();
		this.tmd = tmd;
	}
	public int getCount(HashMap<String,Object> condition,String tranCode){
		return 0;
	}
	
	/**
	 * 获取待插入记录主键值
	 * @param ja
	 * @param condition
	 * @param daoParent
	 * @param sqlStr
	 * @return 待插入记录主键值
	 */
	public String createPkValue(JSONArray ja,HashMap<String,Object> condition,DaoParent daoParent,String sqlStr){
		String pkValueRet = "";//返回耗材id
//		this.extQueryManu(ja, condition, sqlStr, daoParent, 1);
		if(ja.size() > 0){
			String consbId = iu.getRandom();
			iu.putCondition(condition, "consb_id", consbId);
			pkValueRet = this.createPkValue(ja, condition, daoParent, sqlStr);
		}else{
			pkValueRet = (String) condition.get("consb_id");
		}
		logger.debug("生成新记录主键值：pkValueRet："+pkValueRet);
		return pkValueRet;
	}
	
	/**
	 * 批量更新
	 * @param condList 批量更新数据
	 * @param obj dao子类
	 * @param retMap 返回map(recUpdCount:批量更新条数)
	 * @param sqlStr sql
	 * @param updFlag 更新标志  (最初设计：1 批量插入 2、批量删除  目前无用) 
	 */
	public void updateBat(final LinkedList<LinkedHashMap<String, Object>> condList,DaoParent obj,Map retMap,String sqlStr,int updFlag){
//		logger.info("extUpdateBatch");
		logger.debug("准备批量更新，sqlStr："+sqlStr+",condList:"+condList);
		obj.updateBat(sqlStr, condList,updFlag,retMap);
	}
	
	/**
	 * 更新
	 * @param condition 查询条件
	 * @param obj dao子类
	 * @param sqlStr 待组sql
	 * @param retMap 返回map
 	 */
	public void update(HashMap<String, Object> condition,DaoParent obj,String sqlStr,Map retMap){
		System.out.println("update");
//		logger.info("*****************update");
		StringBuffer sqlSb = new StringBuffer();
		sqlSb.append(sqlStr);
		Object[] objArr = new Object[condition.size()];
//		this.putSqlStrManu(sqlSb, objArr, condition);//放置查询条件
		this.putSqlStrManu(sqlSb, objArr, condition, 1, 1);
		obj.extUpdate(sqlSb.toString(),objArr,retMap);
	}
	
	/**
	 * 手动配置查询条件
	 * @param condition
	 * @param obj
	 * @param sqlStr
	 * @return
	 */
	public int countExtManu(HashMap<String, Object> condition,DaoParent obj,String sqlStr){
		logger.info("getCount");
//		String sqlStr = "select count(*) from (select * from t_consb_org) t where 1=1";
		sqlStr = "select count(*) from ("+sqlStr+") t where 1=1";
		System.out.println(sqlStr);
		StringBuffer sqlSb = new StringBuffer();
		sqlSb.append(sqlStr);
		Object[] objArr = new Object[condition.size()];
		
//		this.putSqlStrUpd(sqlSb, objArr, condition);//2015-02-04之前
//		this.putSqlStrPro(sqlSb, objArr, condition, 1, 1);//015-02-04之后
		this.putSqlStrManu(sqlSb, objArr, condition, 1, 1);
		logger.info("getCount sqlStr:"+sqlStr);
		return obj.countQuery(sqlSb.toString(),objArr);
	}
	
/**
 * 获取记录总数
 * @param condition
 * @param obj
 * @param sqlStr
 * @param queryType
 * @return
 */
	public int count(HashMap<String, Object> condition,DaoParent obj,String sqlStr,int queryType){
//		logger.info("getCount");
//		String sqlStr = "select count(*) from (select * from t_consb_org) t where 1=1";
		sqlStr = "select count(*) cnt from ("+sqlStr+") t where 1=1";
		ArrayList tableList = new ArrayList();
		int count = 0;
		System.out.println(sqlStr);
		StringBuffer sqlSb = new StringBuffer();
		sqlSb.append(sqlStr);
//		Object[] objArr = new Object[condition.size()-2];
		Object[] objArr = new Object[condition.size()];
//		this.putSqlStr(sqlSb, objArr, condition,1,2);//放置查询条件
		this.putSqlStrAuto(sqlSb, objArr, condition, 1, queryType);
		logger.debug("getCount sqlStr:"+sqlStr);
		logger.info("查询总记录数");
		iu.infoDbOper("查询总记录数，", sqlStr, objArr);
		daoParent.queryToAl(tableList, sqlSb.toString(), objArr);
		count = Integer.parseInt(((HashMap)tableList.get(0)).get("cnt").toString());
		return count;
	}
	
	/**
	 * 执行查询,非自动拼装sql（查询交易忽略空查询条件）
	 * @param objCarry 查询数据保存载体（ArrayList\JSONArray）
	 * @param condition 条件hashmap
	 * @param sqlStr  sql语句
	 * @param obj 执行dao
	 * @param queryType 1精确查询;其他： 模糊查询
	 */
	public void  queryManu(Object objCarry,HashMap<String, Object> condition,String sqlStr,DaoParent obj,int queryType){
//		logger.info("extQueryManu");
		StringBuffer sqlSb = new StringBuffer();
		sqlSb.append("select * from (");
		sqlStr = " select rownum rn,t.* from ("+sqlStr+") t where 1 = 1";//待放置查询条件sql
		Object[] objArr = new Object[condition.size()];
//		logger.info("condition.size():"+condition.size());
		sqlSb.append(sqlStr);
//		this.putSqlStrUpd(sqlSb, objArr, condition);;//放置查询条件  2015-02-04之前
		this.putSqlStrManu(sqlSb, objArr, condition, 1, queryType);//2015-02-04之后  放置查询条件
		
		sqlSb.append(" ) t where 1 = 1");
		
		this.putSqlStrManu(sqlSb, objArr, condition, 2, queryType);//2015-02-04之后  放置分页条件
		
//		this.pubSqlStrOrder(sqlSb, this.getOdName(), this.getOdWay());//放置排序条件
		if(objCarry instanceof JSONArray){
			obj.queryToJa((JSONArray)objCarry,sqlSb.toString(),objArr);
		}else if(objCarry instanceof ArrayList){
			obj.queryToAl((ArrayList)objCarry, sqlSb.toString(), objArr);
		}
	}
	
	/**
	 * 执行报表查询,自动拼装sql
	 * @param ja json对象数组
	 * @param condition 条件hashmap
	 * @param sqlStr  sql语句
	 * @param obj 执行dao
	 * @param queryType 1精确查询;其他： 模糊查询
	 */
	/*public void  extRptQuery(ArrayList ja,HashMap<String, Object> condition,String sqlStr,DaoParent obj,int queryType){
		logger.info("extRptQuery");
		StringBuffer sqlSb = new StringBuffer();
		sqlSb.append("select * from (");
		sqlStr = " select rownum rn,t.* from ("+sqlStr+") t where 1 = 1";//待放置查询条件sql
		Object[] objArr = new Object[condition.size()];
		logger.info("condition.size():"+condition.size());
		sqlSb.append(sqlStr);
		this.putSqlStr(sqlSb, objArr, condition,1,queryType);//放置查询条件
		sqlSb.append(" ) t where 1 = 1");
		this.putSqlStr(sqlSb, objArr, condition,2,queryType);//放置分页条件
		
		this.pubSqlStrOrder(sqlSb, this.getOdName(), this.getOdWay());//放置排序条件
		
//		sqlSb.append(" order by "+odColName +" "+odWay);
		
		obj.queryToAl(ja,sqlSb.toString(),objArr);
	}*/

	/**
	 * 
	 * @param objCarry 数据存储对象
	 * @param condition 条件hashmap
	 * @param sqlStr sql语句
	 * @param obj 执行dao
	 * @param queryType 1精确查询;其他： 模糊查询
	 */
	public void  queryAuto(Object objCarry,HashMap<String, Object> condition,String sqlStr,DaoParent obj,int queryType){
//		logger.info("extQuery:"+sqlStr);
		StringBuffer sqlSb = new StringBuffer();
		sqlSb.append("select * from (");
		sqlStr = " select rownum rn,t.* from ("+sqlStr+") t where 1 = 1";//待放置查询条件sql
		Object[] objArr = new Object[condition.size()];
//		logger.info("condition.size():"+condition.size());
		sqlSb.append(sqlStr);
		this.putSqlStrAuto(sqlSb, objArr, condition,1,queryType);//放置查询条件
		sqlSb.append(" ) t where 1 = 1");
		this.putSqlStrAuto(sqlSb, objArr, condition,2,queryType);//放置分页条件
		
//		this.pubSqlStrOrder(sqlSb, this.getOdName(), this.getOdWay());//放置排序条件
		
		if(objCarry instanceof JSONArray){
			obj.queryToJa((JSONArray)objCarry,sqlSb.toString(),objArr);
		}else if(objCarry instanceof ArrayList){
			obj.queryToAl((ArrayList)objCarry, sqlSb.toString(), objArr);
		}
//		obj.queryToJa(ja,sqlSb.toString(),objArr);
	}
	
	
	/**
	 * 组织条件（更新）
	 * @param sqlSb
	 * @param objArr
	 * @param condition
	 * @param countFlag  1、新增 2、修改 删除
	 */
	/*public void putSqlStrUpd(StringBuffer sqlSb,Object[] objArr,HashMap<String, Object> condition){
		logger.info("putSqlStrUpd");
		Iterator<String> keyIt = condition.keySet().iterator();
		int index = 0;
		while(keyIt.hasNext()){
			String key = keyIt.next();
			objArr[index] = condition.get(key);
			index++;
		}
		return;
	}*/
	
	/**
	 * 指定排序列
	 * @param sqlSb
	 * @param odColName
	 * @param ascFlag
	 */
	/*public void pubSqlStrOrder(StringBuffer sqlSb,String odColName,String odWay){
		logger.info("pubSqlStrOrder odColName:"+odColName+"|odWay:"+odWay);
		if(odColName != null && odColName != "")
				sqlSb.append(" order by "+odColName +" "+odWay);
	}*/
	/**
	 * 组织sqlStr、根据condition初始化objArr,对于原sql中的问号，直接赋值，最后再在sql末尾拼装分页部分
	 * （注意：condition中的参数个数和sql语句中的问号个数，在condition个数确定的情况下，很安全）
	 * @param sqlSb 待拼接sql语句
	 * @param objArr 参数数组
	 * @param condition 条件
	 * @param countFlag 1：放置sql条件 2：放置sql分页条件 
	 * @param queryType 1：精确设置条件 其他： 模糊设置条件
	 * @return
	 */
	public void putSqlStrManu(StringBuffer sqlSb,Object[] objArr,HashMap<String, Object> condition, int countFlag,int queryType){
		
		Iterator<String> keyIt = condition.keySet().iterator();
		int index = 0;
		while(keyIt.hasNext()){
			String key = keyIt.next();
			if(countFlag == 1){//查询条件
				if((!key.equals("start"))&&!(key.equals("end")))
				{
					if(queryType == 1){
						objArr[index] = condition.get(key).toString();//必须转为string，否则condition中放入float时，执行失败
//						sqlSb.append(" and "+key+" = ?");
					}else{
						objArr[index] = "%"+condition.get(key).toString()+"%";
//						sqlSb.append(" and "+key+" like ?");
					}
				}	
			}
			if(countFlag == 2){//分页
				if(key.equals("start")){
					objArr[index] = condition.get(key).toString();
					sqlSb.append(" and t.rn>=?");
				}else if(key.equals("end")){
					objArr[index] = condition.get(key).toString();
					sqlSb.append(" and t.rn<?");
				}
			}
			index++;
		}
		return;
	}
	
	/**
	 * 自动根据查询条件组织查询并初始化查询参数数组objArr（使用场景：带条件查询表格数据）
	 * @param sqlSb 待拼接sql
	 * @param objArr 参数名数组
	 * @param condition 条件
	 * @param countFlag 1：放置sql条件 2：放置sql分页条件 
	 * @param queryType 1：精确设置条件 其他： 模糊设置条件
	 * @return
	 */
	public void putSqlStrAuto(StringBuffer sqlSb,Object[] objArr,HashMap<String, Object> condition, int countFlag,int queryType){
		
		Iterator<String> keyIt = condition.keySet().iterator();
		int index = 0;
		while(keyIt.hasNext()){
//			logger.info("sqlStr put before sqlSb:"+sqlSb.toString());
			String key = keyIt.next();
			logger.debug("putSqlStrAuto放置条件，当前index:"+index+"|key:"+key+"|value:"+condition.get(key).toString());
//			System.out.println("before:"+sqlSb.toString());
			if(countFlag == 1){//查询条件
				if((!key.equals("start"))&&!(key.equals("end")))
				{
					if(key.equals("tmBegin")){
						objArr[index] = condition.get(key).toString();
						sqlSb.append(" and time >= ?");
//						timeOrderFlag = true;
					}else if(key.equals("tmEnd")){
						objArr[index] = condition.get(key).toString();
						sqlSb.append(" and time < ?");
//						timeOrderFlag = true;
					}else{
						if(queryType == 1){
							objArr[index] = condition.get(key).toString();
							sqlSb.append(" and "+key+" = ?");
						}else{
							objArr[index] = "%"+condition.get(key).toString()+"%";
							sqlSb.append(" and "+key+" like ?");
						}
					}
				}	
			}
			if(countFlag == 2){//分页
				if(key.equals("start")){
					objArr[index] = condition.get(key).toString();
					sqlSb.append(" and t.rn>=?");
				}else if(key.equals("end")){
					objArr[index] = condition.get(key).toString();
					sqlSb.append(" and t.rn<?");
				}
			}
			index++;
		}
		logger.debug("条件设置完成，sqlSb："+sqlSb.toString()+"【条件condition】:"+condition.toString());
		return;
	}
	
	
	

	public String getOdWay() {
		return odWay;
	}

	public void setOdWay(String odWay) {
		this.odWay = odWay;
	}

	public String getOdName() {
		return odName;
	}


	public void setOdName(String odName) {
		this.odName = odName;
	}

/*	public DaoParent getObjDao() {
		logger.info("getObjDao*****");
		return objDao;
	}

	public void setObjDao(DaoParent objDao) {
		logger.info("setObjDao*****objDao:"+objDao);
		this.objDao = objDao;
	}*/
	public IcbcUtil getIu() {
//		logger.info("getIu");
		return iu;
	}

	public void setIu(IcbcUtil iu) {
//		logger.info("setIu");
		this.iu = iu;
	}


	
}
