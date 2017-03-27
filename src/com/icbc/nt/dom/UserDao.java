package com.icbc.nt.dom;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;










import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.nt.bean.MenuBean;
import com.icbc.nt.bean.UserBean;
import com.icbc.nt.util.IcbcUtil;

@Repository
public class UserDao extends DaoParent{
	@Autowired
	private JdbcTemplate jdbcTemplate;
	private ResultSetMetaData rsmd =  null;
	
	@Override
	public int countQuery(String sqlStr,Object[] objArr) {
		logger.info("countQuery");
		this.writeLog(sqlStr, objArr);
		return jdbcTemplate.queryForInt(sqlStr, objArr);
	}
}
