package com.icbc.nt.dom;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oracle.jdbc.driver.OracleCallableStatement;
import oracle.jdbc.driver.OracleTypes;
import oracle.jdbc.oracore.OracleType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Repository;

@Repository
public class ParamDao extends DaoParent{
	@Autowired
	private JdbcTemplate jdbcTemplate;
	private ResultSetMetaData rsmd =  null;
	
	public <T> void creditQuery(final String sqlStr,final String[] inArray,final String [] outArray,HashMap outMap){
		
		ArrayList retList = jdbcTemplate.execute(sqlStr, new CallableStatementCallback() {
			@Override
			public Object doInCallableStatement(CallableStatement cs)
					throws SQLException, DataAccessException {
				int i = 0;
				for (i = 0; i < inArray.length; i++) {
					cs.setString(i+1, inArray[i]);
				}
				for (int j = 0; j < outArray.length; j++) {
					cs.registerOutParameter(i+j+1, OracleTypes.CURSOR);
				}
				cs.execute();
				ResultSet cursor = (ResultSet) cs.getObject(2);
				ArrayList listTemp = new ArrayList();
				while(cursor.next()){
					HashMap map = new HashMap();
					ResultSetMetaData rsmd = cursor.getMetaData();//获取结果集元素据
					int colCount = rsmd.getColumnCount();
					for (int j = 1; j <= colCount; j++) {
						map.put(rsmd.getColumnName(j), cursor.getString(j));
					}
					listTemp.add(map);
				}
				return listTemp;
			}
		});
		for (int i = 0; i < retList.size(); i++) {
			HashMap map = (HashMap) retList.get(i);
			System.out.println("第"+(i+1)+"行:"+map.toString());
		}
	}
	
	
	@Override
	public int countQuery(String sqlStr,Object[] objArr) {
		logger.info("countQuery");
		this.writeLog(sqlStr, objArr);
		return jdbcTemplate.queryForInt(sqlStr, objArr);
	}
}
