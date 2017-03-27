
package com.icbc.nt.dom;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import oracle.jdbc.OracleTypes;
import oracle.jdbc.oracore.OracleType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.nt.bean.UserBean;
import com.icbc.nt.util.IcbcUtil;
import com.icbc.nt.util.TransactionMapData;

@Repository
public class DaoParent implements DaoInterface,Serializable{

	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private TransactionMapData tmd;
	private ResultSetMetaData rsmd =  null;
	
	protected Logger logger = new IcbcUtil().getLogger();
	
	public boolean isConnected(){
		boolean cntState = false;
		try {
			String dbVersion = jdbcTemplate.getDataSource().getConnection().getMetaData().getDatabaseProductVersion();
			System.out.println("**********:"+dbVersion);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return cntState;
	}
	
	public int countQuery(String sqlStr,Object[] obj){
		return 0;
	}
	

	/**
	 * 设置交易状态
	 * @param retMap
	 * @param updFlag 1、add 2、update 3 delete
	 * @param recUpdCount 0、failed 1、success
	 */
	public void putMsg(Map retMap,int updFlag,int recUpdCount){
		retMap.put("recUpdCount", recUpdCount);//更新记录数
		if(recUpdCount != 0){
			retMap.put("msg", retMap.get("operName")+"成功！");
		}else{
			retMap.put("msg", retMap.get("operName")+"失败！");
		}
	}
	
	public void writeLog(String sqlStr,Object[] objArr){
//		logger.info("writeLog");
		String varStr = sqlStr+"[";
		if(objArr.length >=1){
			for(int i = 0;i < objArr.length-1;i++){
				varStr+= (objArr[i]+",");
			}
			varStr += objArr[objArr.length-1]+"]";
		}
		logger.info("执行sql varStr:"+varStr);
	}

	/**
	 * 批量更新数据
	 * @param condList 预编译条件map对象list
	 */
	public void updateBat(String sqlStr,final LinkedList<LinkedHashMap<String, Object>> condList,int updFlag,Map retMap){
		int recUpdCountArr[];
		int recUpdCount = 0;
		BatchPreparedStatementSetter bpss = new BatchPreparedStatementSetter() {
//			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				LinkedHashMap<String, Object> condMap = condList.get(i);
				Iterator<String> it = condMap.keySet().iterator();
				int index = 1;
				while(it.hasNext()){//遍历条件map，预编译sql条件
					String key = it.next();
					logger.debug("批量更新预编译 i:"+i+" index:"+index+" key:"+key+" value:"+condMap.get(key));
					ps.setString(index, condMap.get(key).toString());
					index++;
				}
			}
//			@Override
			public int getBatchSize() {
				return condList.size();
			}
		};
		try{
			logger.info("执行批量更新，sqlStr："+sqlStr);
			recUpdCountArr = jdbcTemplate.batchUpdate(sqlStr, bpss);
			for(int i = 0;i < recUpdCountArr.length;i++){
//				logger.info("批量更新数量："+recUpdCountArr[i]);//每次返回值均为为-2    ？？？？？？？2015-01-15
				recUpdCount += recUpdCountArr[i];
			}
//			this.putMsg(retMap, updFlag, recUpdCount);
			retMap.put("errorCode", "01");
			if(!"操作失败".equals(tmd.get("errorMsg"))){
				tmd.put("errorMsg", "操作成功");
				retMap.put("errorMsg", "操作成功");
				retMap.put("errorCode", "01");
				retMap.put("recUpdCount", recUpdCount);
			}
			logger.info("批量更新:"+retMap.get("errorMsg")+"，总更新记录数："+recUpdCount);
//			throw new RuntimeException("抛出运行错误事物回滚测试");
		}catch(Exception e){
			e.printStackTrace();
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			retMap.put("errorCode", "01");
			retMap.put("errorMsg", "操作失败");
			retMap.put("recUpdCount", "0");
			logger.info("批量更新异常:"+retMap.get("errorMsg"));
		}
	}
	
	/**
	 * 更新
	 * @param sqlStr 更新sql语句
	 * @param objArr 参数数组
	 * @param retMap 返回map
	 * @param updFlag 更新标志
	 */
	public void extUpdate(String sqlStr,Object[] objArr,Map retMap){
//		System.out.println("UserDao  userUpdateExt");
		System.out.println("extUpdate");
		this.writeLog(sqlStr, objArr);
		final UserBean userBean = new UserBean();
		int recUpdCount = 0;
		try{
//			logger.info("准备更新.............");
			System.out.println("准备更新.............");
			recUpdCount = jdbcTemplate.update(sqlStr, objArr);
//			this.putMsg(retMap, updFlag, recUpdCount);
//			retMap.put("msg", "操作成功");
			System.out.println("操作成功");
			tmd.put("errorCode", "01");
			retMap.put("errorCode", "01");
			if(!"操作失败".equals(tmd.get("errorMsg"))){
				tmd.put("errorMsg", "操作成功");
				retMap.put("errorMsg", "操作成功");
				retMap.put("errorCode", "01");
				retMap.put("recUpdCount", recUpdCount);
			}
			logger.info("单次更新:"+retMap.get("errorMsg"));
			tmd.put("recUpdCount", recUpdCount);
		}catch(Exception e){
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();//异常，回滚当前事务
//			retMap.put("msg", "操作失败");
//			retMap.put("recUpdCount", "0");
//			this.putMsg(retMap, updFlag, 0);
//			tmd.put("errorCode", "01");
			tmd.put("errorMsg", "操作失败");
			tmd.put("recUpdCount", recUpdCount);
			retMap.put("errorMsg", "操作失败");
			retMap.put("errorCode", "01");
			retMap.put("recUpdCount", recUpdCount);
			logger.error("单次更新抛出异常:"+retMap.get("errorMsg"));
		}
		logger.info("更新记录数：recUpdCount："+recUpdCount);
		return;
	}
	
	/**
	 * 查询结果为list
	 * @param al
	 * @param sqlStr sql语句
	 * @param objArr 参数对象数组
	 */
	public void queryToAl(final ArrayList al,String sqlStr,Object[] objArr){
//		this.writeLog(sqlStr, objArr);
//		logger.info("jdbcTemplate:"+jdbcTemplate);
		jdbcTemplate.query(sqlStr, objArr,
				new RowCallbackHandler() {
//					@Override
					public void processRow(ResultSet rs) throws SQLException {
						LinkedHashMap map = new LinkedHashMap();
						rsmd = rs.getMetaData(); 
						try{
//							logger.info("*****************************");
							for(int i = 1;i <= rsmd.getColumnCount();i++){
								String colName = rsmd.getColumnName(i).toLowerCase();
								map.put(colName, rs.getString(colName) == null?"无":rs.getString(colName).trim());
//								logger.info("列名：colName："+colName+"值："+rs.getString(colName));
							}
						}catch(Exception e){
							logger.info("查询异常",e);
						}
						al.add(map);
					}
				});
		return;
	}
	
	/**
	 * 查询结果为JSONArray
	 * @param ja json对象数组
	 * @param sqlStr sql语句
	 * @param objArr 参数对象数组
	 */
	public void queryToJa(final JSONArray ja,String sqlStr,Object[] objArr){
//		this.writeLog(sqlStr, objArr);
		jdbcTemplate.query(sqlStr, objArr,
				new RowCallbackHandler() {
//					@Override
					public void processRow(ResultSet rs) throws SQLException {
						HashMap map = new HashMap();
						rsmd = rs.getMetaData(); 
						try{
//							logger.info("*****************************");
							for(int i = 1;i <= rsmd.getColumnCount();i++){
								String colName = rsmd.getColumnName(i).toLowerCase();
//								logger.info("列名：colName："+colName);
								map.put(colName, rs.getString(colName) == null?"":rs.getString(colName).trim());
//								map.put(colName, rs.getString(colName));
							}
						}catch(Exception e){
							logger.info("查询异常",e);
						}
						JSONObject json = (JSONObject) JSONObject.toJSON(map);
//						String jsonStr = JSON.toJSONString(json);
//						logger.info("DaoParent jsonStr:"+jsonStr);
						ja.add(json);
					}
				});
		return;
	}
}
