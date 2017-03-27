/**
 * @author brighttang
 * 
 */
package com.icbc.nt.dom;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;

public interface DaoInterface {
	
	/**
	 * 批量更新数据
	 * @param sqlStr
	 * @param condList
	 * @param updFlag
	 * @param retMap
	 */
	public void updateBat(String sqlStr,final LinkedList<LinkedHashMap<String, Object>> condList,int updFlag,Map retMap);
	
	/**
	 * 单笔更新
	 * @param sqlStr
	 * @param objArr
	 * @param retMap
	 */
	public void extUpdate(String sqlStr,Object[] objArr,Map retMap);
	
	/**
	 * 查询结果为list
	 * @param al
	 * @param sqlStr
	 * @param objArr
	 */
	public void queryToAl(final ArrayList al,String sqlStr,Object[] objArr);
	
	/**
	 * 查询结果为JSONArray
	 * @param ja
	 * @param sqlStr
	 * @param objArr
	 */
	public void queryToJa(final JSONArray ja,String sqlStr,Object[] objArr);
}
