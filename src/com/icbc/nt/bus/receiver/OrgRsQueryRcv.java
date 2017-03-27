/**
 * 机构各种人数查询服务 2015-12-14
 */
package com.icbc.nt.bus.receiver;

import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.util.TransactionMapData;

public class OrgRsQueryRcv extends BusParent implements BusReceiver {
	JSONArray ja = new JSONArray();
	@Override
	public void doWork(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub
	}
	
	/**
	 * 网点各绩效考核岗位对应人数：机构号、绩效岗位编号、机构总人数、机构总人数转换、机构参与考核总人数、机构参与考核总人数转换
	 * @param tmd
	 * @return
	 */
	public void empNumJbjx(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		String orgId = tmd.get("orgId").toString();
		iu.rmCondition(condition);
		iu.putCondition(condition, "orgId1", orgId);
		iu.putCondition(condition, "orgId2", orgId);
		sqlStr =  " select a.orgid,a.jbjx_id,a.zrs,a.zrs_zh,b.zrs_kh,b.zrs_kh_zh,to_number(a.zrs-b.zrs_kh) zrs_bcykh,to_number(a.zrs_zh-b.zrs_kh_zh) zrs_bcykh_zh " 
				+  " from( " 
				+  " select orgid,jbjx_id,sum(zrs) zrs,sum(zsr_zh) zrs_zh " 
				+  " from( " 
				+  " select a.orgid,a.depid,a.userid,a.name,a.ass_flag,a.jbjx_id,b.zq,b.jbjx_pid, " 
				+  " case when b.xs_ry is not null then b.xs_ry else '1' end zsr_zh,'1' zrs,b.xs_bl " 
				+  " from t_ntmisc_user a left join t_ntmisc_orgzbxs b on a.jbjx_id = b.jbjx_id and a.orgid = b.orgid) " 
				+  " where orgid=? " 
				+  " group by orgid,jbjx_id " 
				+  " order by jbjx_id " 
				+  " )a left join( " 
				+  " select orgid,jbjx_id,sum(zrs) zrs_kh,sum(zsr_zh) zrs_kh_zh " 
				+  " from( " 
				+  " select a.orgid,a.depid,a.userid,a.name,a.ass_flag,a.jbjx_id,b.zq,b.jbjx_pid, " 
				+  " case when b.xs_ry is not null then b.xs_ry else '1' end zsr_zh,'1' zrs,b.xs_bl " 
				+  " from t_ntmisc_user a left join t_ntmisc_orgzbxs b on a.jbjx_id = b.jbjx_id and a.orgid = b.orgid) " 
				+  " where orgid=? and ass_flag = '1' " 
				+  " group by orgid,jbjx_id " 
				+  " order by jbjx_id " 
				+  " )b " 
				+  " on a.orgid = b.orgid and a.jbjx_id = b.jbjx_id " ;
		logger.info("执行sql，网点各绩效考核岗位对应人数查询 sqlStr："+sqlStr+"*********condition:"+condition.toString());
		this.queryManu(ja, condition, sqlStr, daoParent, 1);
		
	}
	/**
	 * 网点总人数：机构id、机构下属总人数、机构下属总人数转换、机构下属参与考核人数、机构下属不参与考核人数、机构下属参与考核人数转换
	 * @param tmd
	 * @return
	 */
	public void empNumOrg(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		String orgId = tmd.get("orgId").toString();
		iu.rmCondition(condition);
		iu.putCondition(condition, "orgId1", orgId);
		iu.putCondition(condition, "orgId2", orgId);
		sqlStr = " select orgid,sum(zrs) zrs,sum(zrs_zh) zrs_zh,sum(zrs_kh) zrs_kh,sum(zrs_kh_zh) zrs_kh_zh,sum(zrs)-sum(zrs_kh) zrs_bcykh,sum(zrs_zh)-sum(zrs_kh_zh) zrs_bcykh_zh from ("
				+ "select a.orgid,a.jbjx_id,a.zrs,a.zrs_zh,b.zrs_kh,b.zrs_kh_zh,to_number(a.zrs-b.zrs_kh) zrs_bcykh,to_number(a.zrs_zh-b.zrs_kh_zh) zrs_bcykh_zh "
				+ " from( "
				+ " select orgid,jbjx_id,sum(zrs) zrs,sum(zsr_zh) zrs_zh "
				+ " from( "
				+ " select a.orgid,a.depid,a.userid,a.name,a.ass_flag,a.jbjx_id,b.zq,b.jbjx_pid, "
				+ " case when b.xs_ry is not null then b.xs_ry else '1' end zsr_zh,'1' zrs,b.xs_bl "
				+ " from t_ntmisc_user a left join t_ntmisc_orgzbxs b on a.jbjx_id = b.jbjx_id and a.orgid = b.orgid) "
				+ " where orgid=? "
				+ " group by orgid,jbjx_id "
				+ " order by jbjx_id "
				+ " )a left join( "
				+ " select orgid,jbjx_id,sum(zrs) zrs_kh,sum(zsr_zh) zrs_kh_zh "
				+ " from( "
				+ " select a.orgid,a.depid,a.userid,a.name,a.ass_flag,a.jbjx_id,b.zq,b.jbjx_pid, "
				+ " case when b.xs_ry is not null then b.xs_ry else '1' end zsr_zh,'1' zrs,b.xs_bl "
				+ " from t_ntmisc_user a left join t_ntmisc_orgzbxs b on a.jbjx_id = b.jbjx_id and a.orgid = b.orgid) "
				+ " where orgid=? and ass_flag = '1' "
				+ " group by orgid,jbjx_id "
				+ " order by jbjx_id "
				+ " )b "
				+ " on a.orgid = b.orgid and a.jbjx_id = b.jbjx_id "
				+ " ) "
				+ " group by orgid ";
		logger.info("执行sql，网点总人数查询 sqlStr："+sqlStr+"*********condition:"+condition.toString());
		this.queryManu(ja, condition, sqlStr, daoParent, 1);
	}
}
