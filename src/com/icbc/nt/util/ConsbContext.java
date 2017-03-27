package com.icbc.nt.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.icbc.nt.bus.BusParent;


public class ConsbContext extends BusParent{
	public static Map<String,Object> context = new HashMap<String, Object>();
	
	/**
	 * 清空查询条件
	 */
	public void cleanCondition(){
		ConsbContext.context = new HashMap<String, Object>();
	}
	/**
	 * 设置查询条件
	 * @param sb sql字符串
	 * @param parmList sql占位符字符串参数值
	 */
	public void putCondition(StringBuffer sb,List<String> parmList){
		Map<String, String> condition = null;
		Iterator queryKeyIt = null;
		try{
			condition = (Map<String, String>) ConsbContext.context.get("condition");
			queryKeyIt = condition.keySet().iterator();
		}catch(Exception e){
			logger.info("ConsbContext->putCondition condition为空");
		}
		if(queryKeyIt!=null){
			while(queryKeyIt.hasNext()){
				String queryKey = queryKeyIt.next().toString();
				String queryValue = condition.get(queryKey);
				logger.info("ConsbContext->putCondition queryKey:"+queryKey+" queryValue:"+queryValue);
				if(queryKey.equals("timg_begin")){
					if(!queryValue.equals("null") && !queryValue.equals("")){
						sb.append(" and kg_time >= ?");
						parmList.add(queryValue);
					}
				} else if (queryKey.equals("timg_end")) {
					if (!queryValue.equals("null") && !queryValue.equals("")) {
						sb.append(" and kg_time < ?");
						parmList.add(queryValue);
					}
				}else{
					if(!queryValue.equals("null") && !queryValue.equals("") && !queryValue.equals("all")){
						sb.append(" and "+queryKey+" like '%"+queryValue+"%'");
						//sb.append(" and "+queryKey+"=?");
						//parmList.add(queryValue);
					}
				}
				/*if(!queryKey.equals("timg_begin") && !queryKey.equals("timg_end")){
					logger.info("ConsbContext->putCondition queryKey:"+queryKey+" queryValue:"+queryValue);
					if(!queryValue.equals("null") && !queryValue.equals("") && !queryValue.equals("all")){
						sb.append(" and "+queryKey+" = ?");
						parmList.add(queryValue);
					}
				}*/
			}
		}
	}

//	@Override
	public int getCount(HashMap<String, Object> condition) {
		// TODO Auto-generated method stub
		return 0;
	}
}
