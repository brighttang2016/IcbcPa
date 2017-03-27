package com.icbc.nt.bus;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.message.push.MessagePushHelper;
import com.icbc.message.push.PushThread;
import com.icbc.nt.bus.dispatcher.BusDispatcherImpl;
import com.icbc.nt.dom.ParamDao;
import com.icbc.nt.util.FileTranImpl;
import com.icbc.nt.util.IcbcUtil;
import com.icbc.nt.util.TransactionMapData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.log4j.Logger;
import org.directwebremoting.json.types.JsonBoolean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class JxBus extends BusParent{
	@Autowired
	private ParamDao paramDao;
	@Autowired
	private MediumBus mediumBus;
	@Autowired
	private FileTranImpl fileTranImpl;
	/*@Value("${jxExcelPath}")
	private String jxExcelPath;*/
	@Autowired
	BusDispatcherImpl busDispatcherImpl;
	
	
	/**
	 * 周期查询
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void jxZqQuery(JSONArray ja,
			LinkedHashMap<String, Object> condition, Map retMap,
			TransactionMapData tmd){
		busDispatcherImpl.jxZqQuery(ja, condition, retMap, tmd);
	}
	/**
	 * 绩效查询
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void jxQuery(JSONArray ja,
			LinkedHashMap<String, Object> condition, Map retMap,
			TransactionMapData tmd){
		String userId = tmd.get("userId").toString();
//		String orgId = mediumBus.getUserOrg(userId);
//		String orgIn = mediumBus.getOrgIn(orgId);
		tmd.put("currUser", userId);
		String orgId = busDispatcherImpl.userOrgQuery(tmd);
		tmd.put("orgIdCurr", orgId);
//		String orgIn = busDispatcherImpl.userOrgIn(ja, condition, retMap, tmd);
		String orgIn = busDispatcherImpl.userOrgIn(tmd);
		this.iu.rmCondition(condition);
		sqlStr = "select a.userid,a.name,a.orgid,a.depid,b.orgname,c.depname,d.jx_ywl,d.jx_bzcp,d.jx_zhts,d.jx_dx,d.zsf_ywl,d.zsf_bzcp,d.zsf_zhts,d.zsf_dx,d.zq"
				+ " from t_ntmisc_user a,t_ntmisc_org b,t_ntmisc_dept c,t_ntmisc_userjx d"
				+ " where a.orgid = b.orgid and a.depid = c.depid and a.userid = d.user_id and a.orgid in"
				+ orgIn;
//				+ " and d.zq = ?";
		iu.putCondition(condition, "zq", tmd.get("queryZq"));
		sqlStr = mediumBus.putOrgCond(sqlStr, tmd, condition);
		tmd.put("count",this.count(condition, daoParent, sqlStr, 1));//总记录数入变量池
		iu.putCondition(condition, "start", tmd.get("start"));
		iu.putCondition(condition, "end", tmd.get("end"));
		this.queryAuto(ja, condition, sqlStr, daoParent, 2);//分页查询
	}
	
	/**
	 * 绩效计算业务逻辑处理中转
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void calcBus(JSONArray ja,
			LinkedHashMap<String, Object> condition, Map retMap){
		String userId = tmd.get("userIdLogin").toString();
		logger.info("绩效计算业务逻辑calcBus");
		tmd.tmdLogger();
		int index = Integer.parseInt(tmd.get("tx_code").toString());
		switch(index){//1、2、3、4：四种绩效单项测试使用 5：生产环境使用
		case 1:
			busDispatcherImpl.jxYwlCalc(ja, condition, retMap, tmd);//业务量绩效计算
			break;
		case 2:
			busDispatcherImpl.jxBzcpCalc(ja, condition, retMap, tmd);//标准产品绩效计算
			break;
		case 3:
			busDispatcherImpl.jxZhtsCalc(ja, condition, retMap, tmd);//支行特色产品绩效计算
			break;
		case 4:
			busDispatcherImpl.jxDxCalc(ja, condition, retMap, tmd);//定性绩效计算
			break;
		case 10000070://一键计算四种绩效
			this.startPush(userId,tmd);
			busDispatcherImpl.f10000070(ja, condition, retMap, tmd);
			tmd.put("finish", "1");//终止推送线程
			retMap.put("errorCode", "10");
			retMap.put("errorMsg", "绩效计算完成");
			break;
		case 10000071://当期绩效查询
			tmd.put("zqQuery", tmd.get("zqCurr"));
			busDispatcherImpl.f10000071(ja, condition, retMap, tmd);
			break;
		case 10000080://历史绩效查询
//			orgIn = mediumBus.getOrgIn(orgId);//当前用户所在机构下属所有网点号
//			tmd.put("orgIn", orgIn);
			busDispatcherImpl.f10000080(ja, condition, retMap, tmd);
			break;
		case 20000002://员工业绩导入结果查询
			busDispatcherImpl.f20000002(ja, condition, retMap, tmd);
			break;	
		case 20000004://分支行考核部分总包导入结果查询
			busDispatcherImpl.f20000004(ja, condition, retMap, tmd);
			break;
		case 20000006://网点考核部分总包导入结果查询
			busDispatcherImpl.f20000006(ja, condition, retMap, tmd);
			break;
		case 20000007://绩效计算（新版）
			this.startPush(userId,tmd.clone());
			busDispatcherImpl.f20000007(ja, condition, retMap, tmd);
			logger.info("准备终止推送线程");
			tmd.put("finish", "1");//终止推送线程
			logger.info("终止推送线程");
			retMap.put("errorCode", "10");
			retMap.put("errorMsg", "绩效计算完成");
			break;
		case 20000008://绩效查询（新版）
//			orgIn = mediumBus.getOrgIn(orgId);//当前机构及下属所有网点号（当前机构查询当前及以下所有机构）
//			tmd.put("orgIn", orgIn);
			busDispatcherImpl.f20000008(ja, condition, retMap, tmd);
			break;
		case 20000011://历史绩效查询
//			orgIn = mediumBus.getOrgIn(orgId);//当前机构及下属所有网点号（当前机构查询当前及以下所有机构）
//			tmd.put("orgIn", orgIn);
			busDispatcherImpl.f20000008(ja, condition, retMap, tmd);
			break;
		case 20000014://网点手动分配绩效导入查询
			busDispatcherImpl.f20000014(ja, condition, retMap, tmd);
			break;
		}
	}
	
	/**
	 * 机构总包查询
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 * @return
	 */
	public Object zbQuery(JSONArray ja,
			LinkedHashMap<String, Object> condition, Map retMap,
			TransactionMapData tmd){
		String userId = tmd.get("userId").toString();
		String orgId = mediumBus.getUserOrg(userId);
		String orgIn = mediumBus.getOrgIn(orgId);
		this.iu.rmCondition(condition);
		sqlStr =  "select a.orgid,b.orgname,a.zq,a.zb_ywl,a.zb_bzcp,a.zb_zhts,a.zb_dx from t_ntmisc_zbbl a,t_ntmisc_org b where a.orgid = b.orgid  and a.orgid in "
				+ orgIn;
		sqlStr = mediumBus.putOrgCond(sqlStr, tmd, condition);
		tmd.put("count",this.count(condition, daoParent, sqlStr, 1));//总记录数入变量池
		iu.putCondition(condition, "start", tmd.get("start"));
		iu.putCondition(condition, "end", tmd.get("end"));
		this.queryAuto(ja, condition, sqlStr, daoParent, 2);//分页查询
		return ja;
	}
	
	/**
	 * 总包比例批量插入
	 * @param retMap
	 * @param tmd
	 */
	public void zbBat(Map retMap,TransactionMapData tmd){
		String data = tmd.get("data").toString();
		JSONArray jsonArray = JSONArray.parseArray(data);
		LinkedList<LinkedHashMap<String,Object>> condList = new LinkedList<LinkedHashMap<String,Object>>();
		LinkedList<LinkedHashMap<String,Object>> condDeleteList = new LinkedList<LinkedHashMap<String,Object>>();
		//插入当前机构 标准产品配置
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject json = jsonArray.getJSONObject(i);
			LinkedHashMap<String, Object> condMap = new LinkedHashMap<String, Object>();
			LinkedHashMap<String, Object> condDeleteMap = new LinkedHashMap<String, Object>();
			String orgid = json.getString("orgid");
			String zb_ywl = json.getString("zb_ywl");
			String zb_bzcp = json.getString("zb_bzcp");
			String zb_zhts = json.getString("zb_zhts");
			String zb_dx = json.getString("zb_dx");
			String zq = json.getString("zq");
			
			condMap.put("orgid",orgid);
			condMap.put("zb_ywl",zb_ywl);
			condMap.put("zb_ywl",zb_ywl);
			condMap.put("zb_bzcp", zb_bzcp);
			condMap.put("zb_zhts", zb_zhts);
			condMap.put("zb_dx", zb_dx);
			condMap.put("zq", zq);
			condList.add(condMap);
		}
		logger.info("condList:"+condList);
		sqlStr = "insert into t_ntmisc_zbbl(orgid,zb_ywl,zb_bzcp,zb_zhts,zb_dx,zq) values(?,?,?,?,?,?)";
		this.updateBat(condList, daoParent, retMap, sqlStr, 1);
	}
	
	/**
	 * 总包比例插入
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 * @return
	 */
	public Object zbInsert(JSONArray ja,
			LinkedHashMap<String, Object> condition, Map retMap,
			TransactionMapData tmd){
		JSONArray zbJa = new JSONArray();//总包json数组
		boolean dataValid = true;
		try {
			zbJa = JSONObject.parseArray(tmd.get("data").toString());
		} catch (Exception e) {
			// TODO: handle exception
		}
		for (int i = 0; i < zbJa.size(); i++) {
			JSONObject json = zbJa.getJSONObject(i);
			String zb_ywl = json.getString("zb_ywl");
			String zb_bzcp = json.getString("zb_bzcp");
			String zb_zhts = json.getString("zb_zhts");
			String zb_dx = json.getString("zb_dx");
			if(!iu.isNumber(zb_ywl) || !iu.isNumber(zb_bzcp) || !iu.isNumber(zb_zhts) || !iu.isNumber(zb_dx)){
				dataValid = false;
				retMap.put("errorMsg", "操作失败,总包必须为数字!");
				retMap.put("errorCode", "1");
				logger.info("总包必须为数字");
				break;
			}
		}
		if(dataValid){
			this.zbBat(retMap, tmd);
		}
		return ja;
	}
	
	
	/**
	 * 指标（产品）下拉框查询
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 * @return
	 */
	public Object zbManage(JSONArray ja,
			LinkedHashMap<String, Object> condition, Map retMap,
			TransactionMapData tmd){
		int index = Integer.parseInt(tmd.get("index").toString());
		HashMap<String,String> colNameMap = new HashMap<String, String>();
		iu.rmCondition(condition);
		iu.putCondition(condition, "seq_num", tmd.get("seqNumQuery"));
		logger.info("zbManage index:"+index);
		switch(index){
		case 1://标准产品查询(mova标准产品库)
			sqlStr = "select seq_num,name as zb_name,point,unit from t_ntmisc_bzcp order by to_number(seq_num)";
			break;
		case 2://特色产品查询(特色产品库)
			sqlStr = "select wd_id,seq_num,name as zb_name from t_ntmisc_tscp order by to_number(seq_num)";
			break;
		}
		this.queryAuto(ja, condition, sqlStr, daoParent, 1);
		return ja;
	}
	
	/**
	 * 机构标准产品批量插入
	 * @param retMap
	 * @param tmd
	 * @param orgId
	 */
	public void orgBzcpBat(Map retMap,TransactionMapData tmd,String orgId){
		String data = tmd.get("data").toString();
		JSONArray jsonArray = JSONArray.parseArray(data);
		LinkedList<LinkedHashMap<String,Object>> condList = new LinkedList<LinkedHashMap<String,Object>>();
		LinkedList<LinkedHashMap<String,Object>> condDeleteList = new LinkedList<LinkedHashMap<String,Object>>();
		//插入当前机构 标准产品配置
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject json = jsonArray.getJSONObject(i);
			LinkedHashMap<String, Object> condMap = new LinkedHashMap<String, Object>();
			LinkedHashMap<String, Object> condDeleteMap = new LinkedHashMap<String, Object>();
			condMap.put("orgid",orgId);
			condMap.put("seq_num", json.getString("seq_num"));
			condMap.put("org_point", json.getString("org_point")==null?"":json.getString("org_point"));
			
			condDeleteMap.put("orgid",orgId);
			condDeleteMap.put("seq_num", json.getString("seq_num"));
			condList.add(condMap);
			condDeleteList.add(condDeleteMap);
		}
		logger.info("condList:"+condList+"|condDeleteList:"+condDeleteList);

		int txCode = Integer.parseInt(tmd.get("txCode").toString());
		switch(txCode){
		case 30141:
			sqlStr = "delete t_ntmisc_orgbzcp where orgid=? and seq_num=?";
			this.updateBat(condDeleteList, daoParent, retMap, sqlStr, 1);
			sqlStr = "insert into t_ntmisc_orgbzcp(orgid,seq_num,point) values(?,?,?)";
			this.updateBat(condList, daoParent, retMap, sqlStr, 1);
			break;
		case 30151:
			sqlStr = "delete t_ntmisc_orgzhts where orgid=? and seq_num=?";
			this.updateBat(condDeleteList, daoParent, retMap, sqlStr, 1);
			sqlStr = "insert into t_ntmisc_orgzhts(orgid,seq_num,point) values(?,?,?)";
			this.updateBat(condList, daoParent, retMap, sqlStr, 1);
			break;
		}
	}
	
	/**
	 * 指标范围管理
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 * @return
	 */
	public Object zbfwManage(JSONArray ja,
			LinkedHashMap<String, Object> condition, Map retMap,
			TransactionMapData tmd){
		JSONArray jsonArray = new JSONArray();
		LinkedList<LinkedHashMap<String,Object>> condList = new LinkedList<LinkedHashMap<String,Object>>();
		String orgId = "";
		String orgIn = "";//结果：('机构号1','机构号2','机构号3','机构号4')
		try {
			orgId = mediumBus.getUserOrg(tmd.get("userId").toString());
			orgIn = mediumBus.getOrgIn(orgId);
		} catch (Exception e) {}
		tmd.put("orgIdCurr", orgId);
		int txCode = Integer.parseInt(tmd.get("txCode").toString());
		Object scope = tmd.get("scope");
		String data = "";
		switch(txCode){
		case 30141://标准产品指标范围新增
			this.orgBzcpBat(retMap, tmd, orgId);
			if("2".equals(scope)){
				JSONArray orgJa = new JSONArray();
				mediumBus.getSubOrg(orgJa, orgId);
				//设置子机构标准产品
				logger.info("orgId:"+orgId);
				for (int i = 0; i < orgJa.size(); i++) {
					JSONObject orgJson = orgJa.getJSONObject(i);
					String tempSubOrg = orgJson.getString("menuid");
					logger.info("tempSubOrg:"+tempSubOrg);
					this.orgBzcpBat(retMap, tmd, tempSubOrg);
				}
			}
			break;
		case 30142://标准产品指标范围编辑
			iu.rmCondition(condition);
			iu.putCondition(condition, "org_point", tmd.get("org_point"));
			iu.putCondition(condition, "orgid", tmd.get("orgid"));
			iu.putCondition(condition, "seq_num", tmd.get("seq_num"));
			sqlStr = "update t_ntmisc_orgbzcp set point = ? where orgid=? and seq_num=?";
			this.update(condition, daoParent, sqlStr, retMap);
			break;
		case 30143://标准产品指标范围删除
			data = tmd.get("data").toString();
			condList = new LinkedList<LinkedHashMap<String,Object>>();
			jsonArray = JSONArray.parseArray(data);
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject json = jsonArray.getJSONObject(i);
				LinkedHashMap<String, Object> hashMap = new LinkedHashMap<String, Object>();
				hashMap.put("seq_num", json.getString("seq_num"));
				hashMap.put("orgid", json.getString("orgid"));
				condList.add(hashMap);
			}
			sqlStr = "delete t_ntmisc_orgbzcp t where t.seq_num = ? and orgid=?";
			this.updateBat(condList, daoParent, retMap, sqlStr, 1);
			break;
		case 30144://标准产品指标范围查询
			sqlStr = " select a.orgid,b.orgname,a.point org_point,a.seq_num,c.name zb_name,c.unit,c.point from t_ntmisc_orgbzcp a ,t_ntmisc_org b,t_ntmisc_bzcp c "
					+ " where a.orgid = b.orgid and a.seq_num = c.seq_num and a.orgid in "
					+  orgIn;
			sqlStr = mediumBus.putOrgCond(sqlStr, tmd,condition);
			tmd.put("count",this.count(condition, daoParent, sqlStr, 1));//总记录数入变量池
			iu.putCondition(condition, "start", tmd.get("start"));
			iu.putCondition(condition, "end", tmd.get("end"));
			this.queryAuto(ja, condition, sqlStr, daoParent, 2);//分页查询
			break;
			
			
		case 30151://特色业务指标范围新增
			this.orgBzcpBat(retMap, tmd, orgId);
			if("2".equals(scope)){
				JSONArray orgJa = new JSONArray();
				mediumBus.getSubOrg(orgJa, orgId);
				//设置子机构标准产品
				logger.info("orgId:"+orgId);
				for (int i = 0; i < orgJa.size(); i++) {
					JSONObject orgJson = orgJa.getJSONObject(i);
					String tempSubOrg = orgJson.getString("menuid");
					logger.info("tempSubOrg:"+tempSubOrg);
					this.orgBzcpBat(retMap, tmd, tempSubOrg);
				}
			}
			break;
		case 30152://特色业务指标范围编辑
			iu.rmCondition(condition);
			iu.putCondition(condition, "org_point", tmd.get("org_point"));
			iu.putCondition(condition, "orgid", tmd.get("orgid"));
			iu.putCondition(condition, "seq_num", tmd.get("seq_num"));
			sqlStr = "update t_ntmisc_orgzhts set point = ? where orgid=? and seq_num=?";
			this.update(condition, daoParent, sqlStr, retMap);
			break;
		case 30153://特色业务指标范围删除
			data = tmd.get("data").toString();
			condList = new LinkedList<LinkedHashMap<String,Object>>();
			jsonArray = JSONArray.parseArray(data);
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject json = jsonArray.getJSONObject(i);
				LinkedHashMap<String, Object> hashMap = new LinkedHashMap<String, Object>();
				hashMap.put("seq_num", json.getString("seq_num"));
				hashMap.put("orgid", json.getString("orgid"));
				condList.add(hashMap);
			}
			sqlStr = "delete t_ntmisc_orgzhts t where t.seq_num = ? and orgid=?";
			this.updateBat(condList, daoParent, retMap, sqlStr, 1);
			break;
		case 30154://特色业务指标范围查询
			sqlStr = " select a.orgid,b.orgname,a.seq_num,c.name zb_name,a.point org_point from t_ntmisc_orgzhts a,t_ntmisc_org b,t_ntmisc_tscp c"
					+ " where a.orgid = b.orgid and a.seq_num = c.seq_num and a.orgid in "
					+  orgIn
					+" order by a.orgid,a.seq_num";
			sqlStr = mediumBus.putOrgCond(sqlStr, tmd,condition);
			tmd.put("count",this.count(condition, daoParent, sqlStr, 1));//总记录数入变量池
			iu.putCondition(condition, "start", tmd.get("start"));
			iu.putCondition(condition, "end", tmd.get("end"));
			this.queryAuto(ja, condition, sqlStr, daoParent, 2);//分页查询
			break;
		}
		return ja;
	}
	
	/**
	 * 机构总包比例查询
	 * @param zbblJa
	 */
	public void zbblQuery(JSONArray zbblJa) {
		iu.rmCondition(condition);
		sqlStr = "select * from t_ntmisc_zbbl t";
		this.queryAuto(zbblJa, condition, sqlStr, daoParent, 1);
	}
	public void setColName(HashMap<String,String> colNameMap,JSONArray ja){
		logger.info("放置列名前ja:"+ja);
		logger.info("放置列名前colNameMap:"+colNameMap);
		JSONObject rowJson = new JSONObject();
		try {
			rowJson = ja.getJSONObject(0);
		} catch (Exception e) {}
		Iterator jsonKeyIt = rowJson.keySet().iterator();
		ArrayList<String> al = new ArrayList();
		while(jsonKeyIt.hasNext()){
			String jsonKeyItNext = ""; 
			jsonKeyItNext = jsonKeyIt.next()+"";
//			logger.info("jsonKeyItNext:"+jsonKeyItNext);
			al.add(jsonKeyItNext);
		}
		for (int i = 0; i < ja.size(); i++) {
			rowJson = ja.getJSONObject(i);
			for (int j = 0; j < al.size(); j++) {
				String jsonKeyItNext = al.get(j)+"_colname";
				Iterator mapKeyIt = colNameMap.keySet().iterator();
				while(mapKeyIt.hasNext()){
					String colMapKey = mapKeyIt.next()+"";
					if(jsonKeyItNext.equals(colMapKey)){
						rowJson.put(jsonKeyItNext, colNameMap.get(colMapKey));
					}
				}
			}
		}
		for (int i = 0; i < ja.size(); i++) {
			rowJson = ja.getJSONObject(i);
			rowJson.put("userid_colname", "人力资源编码");
			rowJson.put("username_colname", "姓名");
//			rowJson.put("orgid_colname", "机构号");
			rowJson.put("orgname_colname", "机构名称");
//			rowJson.put("depid_colname", "部门编号");
			rowJson.put("depname_colname", "部门名称");
			rowJson.put("zq_colname", "周期");
		}
		logger.info("放置列名后ja："+ja.toJSONString());
	}
	/**
	 * 设置列显示中文名
	 * @param colNameMap
	 * @param ja
	 */
	public void setColMap(HashMap<String,String> colNameMap,JSONArray ja){
		for (int i = 0; i < ja.size(); i++) {
			JSONObject json = ja.getJSONObject(i);
			colNameMap.put(json.getString("col_name")+"_colname", json.getString("name"));
		}
		logger.info("setColMap放置列名后ja："+ja.toJSONString());
	}
	
	/**
	 * 机构考核标准产品、特色产品查询
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 * @return
	 */
	public Object orgCp(JSONArray ja,
			LinkedHashMap<String, Object> condition, Map retMap,
			TransactionMapData tmd){
		logger.info("机构产品查询orgCp");
		String orgIdCurr = tmd.get("orgIdCurr").toString();
		String orgIn = mediumBus.getOrgIn(orgIdCurr);
		//初始化列名
		LinkedHashMap<String, Object> condMap = new LinkedHashMap<String, Object>();
//		iu.putCondition(condMap, "orgId", orgId);
		String txCode = tmd.get("txCode").toString();
		logger.info("txCode:"+txCode);
		String logMsg = "";
		switch(Integer.parseInt(txCode)){
		case 30111://机构标准产品
			logMsg = " 机构标准产品";
			sqlStr = "select a.*,b.orgid from t_ntmisc_bzcp a,t_ntmisc_orgbzcp b where a.seq_num = b.seq_num and b.orgid ="
					+ orgIdCurr;
			break;
		case 30121://机构特色产品
			logMsg = " 机构特色产品";
			sqlStr = "select a.*,b.orgid from t_ntmisc_tscp a,t_ntmisc_orgzhts b where a.seq_num = b.seq_num and b.orgid ="
					+ orgIdCurr;
		case 30021://机构特色产品（业绩展示）
			logMsg = " 机构特色产品";
			sqlStr = "select a.*,b.orgid from t_ntmisc_tscp a,t_ntmisc_orgzhts b where a.seq_num = b.seq_num and b.orgid ="
					+ orgIdCurr;
			break;
		}
		this.queryAuto(ja, condMap, sqlStr, daoParent, 1);
		logger.info("txCode"+txCode+logMsg+" ja："+ja);
		return ja;
	}
	
	/**
	 * 考核基础数据查询
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 * @return
	 */
	public Object initDataManage(JSONArray ja,
			LinkedHashMap<String, Object> condition, Map retMap,
			TransactionMapData tmd){
//		String orgId = mediumBus.getUserOrg(tmd.get("userId").toString());
		//查询当前机构
		tmd.put("currUser", tmd.get("userId").toString());
		String orgId = busDispatcherImpl.userOrgQuery(tmd);
//		String orgIn = mediumBus.getOrgIn(orgId);
		//查询当前可见机构
		tmd.put("orgIdCurr", orgId);
//		String orgIn = busDispatcherImpl.userOrgIn(ja, condition, retMap, tmd);
		String orgIn = busDispatcherImpl.userOrgIn(tmd);
		tmd.put("orgIn", orgIn);
		int txCode = Integer.parseInt(tmd.get("txCode").toString());
		HashMap<String,String> colNameMap = new HashMap<String, String>();
		StringBuffer sqlCaseWhen = new StringBuffer();
		JSONObject rowJson = new JSONObject();
		JSONArray orgTscpJa = new JSONArray();//机构特色产品
		
		iu.rmCondition(condition);
		iu.putCondition(condition, "userid", tmd.get("userIdQuery"));
		
		switch(txCode){
		case 10000053://查询机构分配数据导入结果
			busDispatcherImpl.f10000053(ja, condition, retMap, tmd);
			break;
		case 10000054://机构分配结果查询
			iu.putCondition(condition, "zq", tmd.get("zqQuery"));
			busDispatcherImpl.f10000054(ja, condition, retMap, tmd);
			break;
		case 10000056://查询MOVA机构得分导入结果
			busDispatcherImpl.f10000056(ja, condition, retMap, tmd);
			break;
		case 10000069://人员分配比例查询
			iu.putCondition(condition, "zq", tmd.get("zqQuery"));
			busDispatcherImpl.f10000069(ja, condition, retMap, tmd);
			break;
		case 30101://查询业务量考核基础数据
			sqlStr = " select a.*,b.jb_name,c.orgname,d.depname,e.dj,e.sg,e.zs,e.ts,e.rj,e.oper_time,e.used,e.zq"
					+ " from t_ntmisc_user a ,t_ntmisc_job b,t_ntmisc_org c,t_ntmisc_dept d,t_ntmisc_ywlmx e"
					+ " where a.jb_id = b.jb_id and a.orgid = c.orgid and a.depid = d.depid and a.userid = e.user_id and e.used='0'"
					+ " and a.orgid in"
					+ orgIn;
			sqlStr = mediumBus.putOrgCond(sqlStr, tmd,condition);
			
			tmd.put("count",this.count(condition, daoParent, sqlStr, 1));//总记录数入变量池
			iu.putCondition(condition, "start", tmd.get("start"));
			iu.putCondition(condition, "end", tmd.get("end"));
			this.queryAuto(ja, condition, sqlStr, daoParent, 2);//分页查询
			this.setColName(colNameMap, ja);
			
			break;
		case 30041://查询业务量考核业绩展示
			sqlStr = " select a.*,b.jb_name,c.orgname,d.depname,e.dj,e.sg,e.zs,e.ts,e.rj,e.oper_time,e.used,e.zq"
					+ " from t_ntmisc_user a ,t_ntmisc_job b,t_ntmisc_org c,t_ntmisc_dept d,t_ntmisc_ywlmx e"
					+ " where a.jb_id = b.jb_id and a.orgid = c.orgid and a.depid = d.depid and a.userid = e.user_id and e.used='1'"
					+ " and a.orgid in"
					+ orgIn;
			sqlStr = mediumBus.putOrgCond(sqlStr, tmd,condition);
			tmd.put("count",this.count(condition, daoParent, sqlStr, 1));//总记录数入变量池
			iu.putCondition(condition, "start", tmd.get("start"));
			iu.putCondition(condition, "end", tmd.get("end"));
			this.queryAuto(ja, condition, sqlStr, daoParent, 2);//分页查询
			this.setColName(colNameMap, ja);
			break;
		case 30111://查询标准产品考核基础数据
			sqlStr = " select a.orgid,a.orgname,a.depid,a.depname,a.userid,a.name username,b.zq,b.point from ( "
					+ " select a.userid,a.name,a.orgid,a.depid,b.orgname,c.depname from t_ntmisc_user a,t_ntmisc_org b,t_ntmisc_dept c "
					+ " where a.orgid = b.orgid and a.depid = c.depid)a,t_ntmisc_usermova b "
					+ " where a.userid = b.user_id and b.used='0' and a.orgid in"
					+  orgIn;
			sqlStr = mediumBus.putOrgCond(sqlStr, tmd,condition);
			tmd.put("count",this.count(condition, daoParent, sqlStr, 1));//总记录数入变量池
			iu.putCondition(condition, "start", tmd.get("start"));
			iu.putCondition(condition, "end", tmd.get("end"));
			this.queryAuto(ja, condition, sqlStr, daoParent, 2);//分页查询
//			this.setColName(colNameMap, ja);
			rowJson = new JSONObject();
			for (int i = 0; i < ja.size(); i++) {
				rowJson = ja.getJSONObject(i);
				rowJson.put("userid_colname", "人力资源编码");
				rowJson.put("username_colname", "姓名");
				rowJson.put("orgname_colname", "机构名称");
				rowJson.put("depname_colname", "部门名称");
				rowJson.put("zq_colname", "周期");
				rowJson.put("point_colname", "MOVA员工业界视图得分");
				
				rowJson.put("userid_width", "110");
				rowJson.put("username_width", "120");
				rowJson.put("orgname_width", "110");
				rowJson.put("depname_width", "180");
				rowJson.put("zq_width", "110");
				rowJson.put("point_width", "150");
			}
			break;
		case 30014://标准产品考核业绩展示
			sqlStr = " select a.orgid,a.orgname,a.depid,a.depname,a.userid,a.name username,b.zq,b.point from ( "
					+ " select a.userid,a.name,a.orgid,a.depid,b.orgname,c.depname from t_ntmisc_user a,t_ntmisc_org b,t_ntmisc_dept c "
					+ " where a.orgid = b.orgid and a.depid = c.depid)a,t_ntmisc_usermova b "
					+ " where a.userid = b.user_id and b.used='1' and a.orgid in"
					+  orgIn;
			sqlStr = mediumBus.putOrgCond(sqlStr, tmd,condition);
			tmd.put("count",this.count(condition, daoParent, sqlStr, 1));//总记录数入变量池
			iu.putCondition(condition, "start", tmd.get("start"));
			iu.putCondition(condition, "end", tmd.get("end"));
			this.queryAuto(ja, condition, sqlStr, daoParent, 2);//分页查询
			rowJson = new JSONObject();
			for (int i = 0; i < ja.size(); i++) {
				rowJson = ja.getJSONObject(i);
				rowJson.put("userid_colname", "人力资源编码");
				rowJson.put("username_colname", "姓名");
				rowJson.put("orgname_colname", "机构名称");
				rowJson.put("depname_colname", "部门名称");
				rowJson.put("zq_colname", "周期");
				rowJson.put("point_colname", "MOVA员工业界视图得分");
				
				rowJson.put("userid_width", "110");
				rowJson.put("username_width", "120");
				rowJson.put("orgname_width", "110");
				rowJson.put("depname_width", "180");
				rowJson.put("zq_width", "110");
				rowJson.put("point_width", "150");
			}
			break;
		case 30121://查询特色业务考核基础数据
			logger.info("查询特色业务考核基础数据开始");
			orgTscpJa = new JSONArray();//机构特色产品
			this.orgCp(orgTscpJa, condition, retMap, tmd);
			this.setColMap(colNameMap,orgTscpJa);
			sqlCaseWhen = new StringBuffer();
			for (int i = 0; i < orgTscpJa.size(); i++) {
				JSONObject orgBzcpJson = orgTscpJa.getJSONObject(i);
				String seqNum = orgBzcpJson.getString("seq_num");
				String colName = orgBzcpJson.getString("col_name");
				sqlCaseWhen.append(", sum(case when t.seq_num='"+seqNum+"' then t.rs  end) as "+colName+" ");
				/*if(i == 0)
					sqlCaseWhen.append(" sum(case when t.seq_num='"+seqNum+"' then t.rs  end) as "+colName+" ");
				else
					sqlCaseWhen.append(", sum(case when t.seq_num='"+seqNum+"' then t.rs  end) as "+colName+" ");*/
			}
			logger.info("设置显示列名colNameMap："+colNameMap);
			sqlStr = " select userid,username,orgid,depid,orgname,depname,zq "//orgid\depid putOrgCond中使用
//					+ " sum(case when t.seq_num='1' then t.rs  end) as col1, "
//					+ " sum(case when t.seq_num='2' then t.rs  end) as col2  "
					+ sqlCaseWhen
					+ " from ( "
					+ " select a.userid,a.name username,a.orgid,a.orgname,a.depid,a.depname,b.seq_num,b.rs,b.zq,b.col_name "
					+ " from (select a.userid,a.name,a.orgid,a.depid,b.orgname,c.depname from t_ntmisc_user a,t_ntmisc_org b,t_ntmisc_dept c "
					+ " where a.orgid = b.orgid and a.depid = c.depid and a.orgid in"
					+  orgIn
					+ ") a, "
					+ " (select a.*,b.col_name from t_ntmisc_zhtsmx a,t_ntmisc_tscp b where a.seq_num = b.seq_num and a.used='0') b "
					+ " where a.userid = b.user_id " 
					+ " ) t "
					+ " group by userid,username,orgid,depid,orgname,depname,zq ";
			sqlStr = mediumBus.putOrgCond(sqlStr, tmd,condition);
			tmd.put("count",this.count(condition, daoParent, sqlStr, 1));//总记录数入变量池
			iu.putCondition(condition, "start", tmd.get("start"));
			iu.putCondition(condition, "end", tmd.get("end"));
			this.queryAuto(ja, condition, sqlStr, daoParent, 2);//分页查询
			this.setColName(colNameMap, ja);
			logger.info("查询特色业务考核基础数据结束");
			break;
		case 30021://特色产品考核业绩展示
			orgTscpJa = new JSONArray();//机构特色产品
			this.orgCp(orgTscpJa, condition, retMap, tmd);
			this.setColMap(colNameMap,orgTscpJa);
			sqlCaseWhen = new StringBuffer();
			for (int i = 0; i < orgTscpJa.size(); i++) {
				JSONObject orgBzcpJson = orgTscpJa.getJSONObject(i);
				String seqNum = orgBzcpJson.getString("seq_num");
				String colName = orgBzcpJson.getString("col_name");
				sqlCaseWhen.append(", sum(case when t.seq_num='"+seqNum+"' then t.rs  end) as "+colName+" ");
			}
			logger.info("设置显示列名colNameMap："+colNameMap);
			sqlStr = " select userid,username,orgid,depid,orgname,depname,zq "//orgid\depid putOrgCond中使用
					+ sqlCaseWhen
					+ " from ( "
					+ " select a.userid,a.name username,a.orgid,a.orgname,a.depid,a.depname,b.seq_num,b.rs,b.zq,b.col_name "
					+ " from (select a.userid,a.name,a.orgid,a.depid,b.orgname,c.depname from t_ntmisc_user a,t_ntmisc_org b,t_ntmisc_dept c "
					+ " where a.orgid = b.orgid and a.depid = c.depid and a.orgid in"
					+  orgIn
					+ ") a, "
					+ " (select a.*,b.col_name from t_ntmisc_zhtsmx a,t_ntmisc_tscp b where a.seq_num = b.seq_num and a.used='1') b "
					+ " where a.userid = b.user_id " 
					+ " ) t "
					+ " group by userid,username,orgid,depid,orgname,depname,zq ";
			sqlStr = mediumBus.putOrgCond(sqlStr, tmd,condition);
			tmd.put("count",this.count(condition, daoParent, sqlStr, 1));//总记录数入变量池
			iu.putCondition(condition, "start", tmd.get("start"));
			iu.putCondition(condition, "end", tmd.get("end"));
			this.queryAuto(ja, condition, sqlStr, daoParent, 2);//分页查询
			this.setColName(colNameMap, ja);
			logger.info("查询特色业务考核数据展示结束");
			break;
		case 30131://查询定性考核基础数据
			sqlStr = "select a.userid,a.name username,a.orgid,a.orgname,a.depid,a.depname,b.point,b.zq"
					+ " from (select a.userid,a.name,a.orgid,a.depid,b.orgname,c.depname from t_ntmisc_user a,t_ntmisc_org b,t_ntmisc_dept c"
					+ " where a.orgid = b.orgid and a.depid = c.depid) a,t_ntmisc_dx b"
					+ " where a.userid = b.user_id and b.used='0' and a.orgid in "
					+ orgIn;
			sqlStr = mediumBus.putOrgCond(sqlStr, tmd,condition);
			tmd.put("count",this.count(condition, daoParent, sqlStr, 1));//总记录数入变量池
			iu.putCondition(condition, "start", tmd.get("start"));
			iu.putCondition(condition, "end", tmd.get("end"));
			this.queryAuto(ja, condition, sqlStr, daoParent, 2);//分页查询
			
			colNameMap.put("point_colname", "加扣分");
			this.setColName(colNameMap, ja);
			break;
		case 30141://查询定性考核基础业绩展示
			sqlStr = "select a.userid,a.name username,a.orgid,a.orgname,a.depid,a.depname,b.point,b.zq"
					+ " from (select a.userid,a.name,a.orgid,a.depid,b.orgname,c.depname from t_ntmisc_user a,t_ntmisc_org b,t_ntmisc_dept c"
					+ " where a.orgid = b.orgid and a.depid = c.depid) a,t_ntmisc_dx b"
					+ " where a.userid = b.user_id and b.used='1' and a.orgid in "
					+ orgIn;
			sqlStr = mediumBus.putOrgCond(sqlStr, tmd,condition);
			tmd.put("count",this.count(condition, daoParent, sqlStr, 1));//总记录数入变量池
			iu.putCondition(condition, "start", tmd.get("start"));
			iu.putCondition(condition, "end", tmd.get("end"));
			this.queryAuto(ja, condition, sqlStr, daoParent, 2);//分页查询
			
			colNameMap.put("point_colname", "加扣分");
			this.setColName(colNameMap, ja);
			break;
			
		}
		return ja;
	}
	
	public void startPush(String receiver,TransactionMapData tmd){
		tmd.put("percent", "0");
		tmd.put("errorCode", "0");
		tmd.put("errorMsg", "");
		tmd.put("finish", "0");
		tmd.put("progress", "0");
		MessagePushHelper mph = new MessagePushHelper();
		new PushThread(receiver,mph,tmd).start();
//		new PushThread(receiver,mph).start();
	}
}