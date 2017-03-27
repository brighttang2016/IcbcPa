/**
 * 绩效计算接收者
 */
package com.icbc.nt.bus.receiver;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.message.push.MessagePushHelper;
import com.icbc.message.push.PushThread;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.bus.MediumBus;
import com.icbc.nt.util.TransactionMapData;

public class JxCalcRcv extends BusParent implements BusReceiver{
	@Autowired
	MediumBus mediumBus;
	@Value("${jxExcelPath}")
	private String jxExcelPath;
	
	/**
	 * 数据准备
	 */
	public void dataPrepare(TransactionMapData tmd){
		JSONArray ja = new JSONArray();
		String zq = tmd.get("zq").toString();
		String fileName = "绩效收入计算结果-"+zq+".xls";
		String filePath = tmd.get("path")+jxExcelPath;
		logger.info("fileName:"+fileName);
		logger.info("filePath:"+filePath);
		LinkedList<String> table = new LinkedList<String>();//文件数据源
//		String fileName = "文件写入测试.txt";
		String path = filePath+"\\"+fileName;
		String userId = tmd.get("userId").toString();
		String orgId = mediumBus.getUserOrg(userId);
		String orgIn = mediumBus.getOrgIn(orgId);
//		int colCount = 8;//表格列数
//		int rowCount = 10;//表格行数
		/*for (int i = 0; i < rowCount; i++) {
			StringBuffer sb = new StringBuffer();
			HashMap<String,String> rowMap = new HashMap<String, String>();
			for (int j = 0; j < colCount; j++) {
				sb.append(i+"行"+j+"列"+"|");//行数据竖线分割
			}
			table.add(sb.toString());
		}*/
		iu.rmCondition(condition);
		iu.putCondition(condition, "zq", zq);
		sqlStr = "select a.userid,a.name,b.orgid,b.orgname,c.depid,c.depname,"
				+ " d.jx_ywl,d.jx_bzcp,d.jx_zhts,d.jx_dx,d.zsf_ywl,d.zsf_bzcp,d.zsf_zhts,d.zsf_dx,d.zq"
				+ " from t_ntmisc_user a,t_ntmisc_org b,t_ntmisc_dept c,t_ntmisc_userjx d"
				+ " where a.orgid = b.orgid and a.depid = c.depid and a.userid = d.user_id and a.orgid in"
				+ orgIn;
		this.queryAuto(ja, condition, sqlStr, daoParent, 1);
		logger.info("绩效考核结果ja:"+ja.toJSONString());
		
		//总计
		float hz_jx_ywl = 0;
		float hz_jx_bzcp = 0;
		float hz_jx_zhts = 0;
		float hz_jx_dx = 0;
		float hz_jx_hj = 0;
		//计算汇总
		for (int i = 0; i < ja.size(); i++) {
			JSONObject json = ja.getJSONObject(i);
			float jx_ywl = iu.getFloatDecimal(json.getString("jx_ywl"), 2);
			float jx_bzcp = iu.getFloatDecimal(json.getString("jx_bzcp"), 2);
			float jx_zhts = iu.getFloatDecimal(json.getString("jx_zhts"), 2);
			float jx_dx = iu.getFloatDecimal(json.getString("jx_dx"), 2);
			float jx_hj = jx_ywl+jx_bzcp+jx_zhts+jx_dx;
			hz_jx_ywl += jx_ywl;
			hz_jx_bzcp += jx_bzcp;
			hz_jx_zhts += jx_zhts;
			hz_jx_dx += jx_dx;
			hz_jx_hj += jx_hj;
			json.put("jx_hj", jx_hj+"");
		}
		JSONObject hzJson = new JSONObject();
		hzJson.put("hz_jx_ywl", iu.getFloatDecimal(hz_jx_ywl+"", 2));
		hzJson.put("hz_jx_bzcp", iu.getFloatDecimal(hz_jx_bzcp+"",2));
		hzJson.put("hz_jx_zhts", iu.getFloatDecimal(hz_jx_zhts+"", 2));
		hzJson.put("hz_jx_dx", iu.getFloatDecimal(hz_jx_dx+"", 2));
		hzJson.put("hz_jx_hj", iu.getFloatDecimal(hz_jx_hj+"",2));
		logger.info("总计hzJson："+hzJson);
		this.fileWrite(ja, path, fileName,hzJson);
	}
	/**
	 * 写入文件
	 * @param table 文件数据源
	 * @param path 文件路径（包含了文件名）
	 */
	public void  fileWrite(JSONArray ja,String path,String fileName,JSONObject hzJson){
		try {
//			FileOutputStream fos = new FileOutputStream(new File(path));
//			OutputStreamWriter osw = new OutputStreamWriter(fos);
//			for (int i = 0; i < table.size(); i++) {
//				System.out.println("写入行："+table.get(i));
//				osw.write(table.get(i)+"\r\n");
//			}
//			osw.flush();
//			osw.close();
//			fos.close();
			logger.info("path:"+path);
			WritableWorkbook book = Workbook.createWorkbook(new File(path));
			WritableSheet sheet  = book.createSheet("第一页", 1);
			sheet.setRowView(0, 1000);
			sheet.setRowView(1, 600);
			sheet.setRowView(2, 600);
			for (int i = 0; i < ja.size(); i++) {
				sheet.setRowView(i+3, 400);
			}
			for (int i = 0; i < 15; i++) {
				sheet.setColumnView(i,30);
			}
//			sheet.addCell();
			
			sheet.mergeCells(0, 0, 14, 0);
			sheet.mergeCells(0, 1, 1, 1);
			WritableFont fontTitle = new WritableFont(WritableFont.TIMES,20,WritableFont.BOLD);
			WritableFont fontColName = new WritableFont(WritableFont.TIMES,16,WritableFont.BOLD);
			WritableFont fontColCont = new WritableFont(WritableFont.TIMES,10,WritableFont.NO_BOLD);
			
			WritableCellFormat fmtTitle = new WritableCellFormat(fontTitle);
			WritableCellFormat fmtColName = new WritableCellFormat(fontColName);
			WritableCellFormat fmtColCont = new WritableCellFormat(fontColCont);
			
			fmtTitle.setVerticalAlignment(VerticalAlignment.CENTRE);
			fmtTitle.setAlignment(Alignment.CENTRE);
//			fmtColName.setVerticalAlignment(VerticalAlignment.CENTRE);
			fmtColName.setAlignment(Alignment.CENTRE);
//			fmtColCont.setVerticalAlignment(VerticalAlignment.CENTRE);
			fmtColCont.setAlignment(Alignment.CENTRE);
			
			sheet.addCell(new Label(0, 0, "绩效收入计算结果",fmtTitle));
			String zq = ja.getJSONObject(0)==null?"":ja.getJSONObject(0).getString("zq");
			sheet.addCell(new Label(0, 1, "考核周期："+zq,fmtColName));
			
			sheet.addCell(new Label(0, 2, "机构编码",fmtColName));
			sheet.addCell(new Label(1, 2, "机构名称",fmtColName));
			sheet.addCell(new Label(2, 2, "部门编码",fmtColName));
			sheet.addCell(new Label(3, 2, "部门名称",fmtColName));
			sheet.addCell(new Label(4, 2, "人力资源编码",fmtColName));
			sheet.addCell(new Label(5, 2, "姓名",fmtColName));
			
			sheet.addCell(new Label(6, 2, "业务量绩效",fmtColName));
			sheet.addCell(new Label(7, 2, "标准产品绩效",fmtColName));
			sheet.addCell(new Label(8, 2, "特色业务绩效",fmtColName));
			sheet.addCell(new Label(9, 2, "定性绩效",fmtColName));
			
			sheet.addCell(new Label(10, 2, "业务量折算分",fmtColName));
			sheet.addCell(new Label(11, 2, "标准产品折算分",fmtColName));
			sheet.addCell(new Label(12, 2, "特色业务折算分",fmtColName));
			sheet.addCell(new Label(13, 2, "定性折算分",fmtColName));
			
			sheet.addCell(new Label(14, 2, "绩效合计",fmtColName));
			
			for (int i = 0; i < ja.size(); i++) {
				JSONObject rowJson = ja.getJSONObject(i);
				sheet.addCell(new Label(0, i+3, rowJson.getString("orgid"),fmtColCont));
				sheet.addCell(new Label(1, i+3, rowJson.getString("orgname"),fmtColCont));
				sheet.addCell(new Label(2, i+3, rowJson.getString("depid"),fmtColCont));
				sheet.addCell(new Label(3, i+3, rowJson.getString("depname"),fmtColCont));
				sheet.addCell(new Label(4, i+3, rowJson.getString("userid"),fmtColCont));
				sheet.addCell(new Label(5, i+3, rowJson.getString("name"),fmtColCont));
				sheet.addCell(new Label(6, i+3, rowJson.getString("jx_ywl"),fmtColCont));
				sheet.addCell(new Label(7, i+3, rowJson.getString("jx_bzcp"),fmtColCont));
				sheet.addCell(new Label(8, i+3, rowJson.getString("jx_zhts"),fmtColCont));
				sheet.addCell(new Label(9, i+3, rowJson.getString("jx_dx"),fmtColCont));
				sheet.addCell(new Label(10, i+3, rowJson.getString("zsf_ywl"),fmtColCont));
				sheet.addCell(new Label(11, i+3, rowJson.getString("zsf_bzcp"),fmtColCont));
				sheet.addCell(new Label(12, i+3, rowJson.getString("zsf_zhts"),fmtColCont));
				sheet.addCell(new Label(13, i+3, rowJson.getString("zsf_dx"),fmtColCont));
				sheet.addCell(new Label(14, i+3, rowJson.getString("jx_hj"),fmtColCont));
			}
			int i = ja.size();
			sheet.addCell(new Label(0, i+3, "总计:",fmtColCont));
			sheet.addCell(new Label(6, i+3, hzJson.getString("hz_jx_ywl"),fmtColCont));
			sheet.addCell(new Label(7, i+3, hzJson.getString("hz_jx_bzcp"),fmtColCont));
			sheet.addCell(new Label(8, i+3, hzJson.getString("hz_jx_zhts"),fmtColCont));
			sheet.addCell(new Label(9, i+3, hzJson.getString("hz_jx_dx"),fmtColCont));
			sheet.addCell(new Label(14, i+3, hzJson.getString("hz_jx_hj"),fmtColCont));
		
			book.write();
			book.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	
	/**
	 * 插入考核绩效收入
	 */
	public void khsrInsert(TransactionMapData tmd, Map retMap) {
		logger.info("khsrInsert");
		int index = Integer.parseInt(tmd.get("index").toString());
		String tempLogStr = "";
		String ywlSr = tmd.get("ywlSr").toString();
		String fw = tmd.get("fw").toString();
		String userId = tmd.get("userId").toString();
		String zq = tmd.get("zq").toString();
		iu.rmCondition(condition);
		iu.putCondition(condition, "ywlSr", ywlSr);
		iu.putCondition(condition, "fw", fw);
		iu.putCondition(condition, "userId", userId);
		iu.putCondition(condition, "zq", zq);
		switch(index){
		case 1://业务量考核收入
			tempLogStr  ="业务量考核收入";
			sqlStr = "update t_ntmisc_userjx t set t.jx_ywl=?,t.zsf_ywl=? where user_id=? and zq=?";
			break;
		case 2://标准产品考核收入
			tempLogStr  ="标准产品考核收入";
			sqlStr = "update t_ntmisc_userjx t set t.jx_bzcp=?,t.zsf_bzcp=? where user_id=? and zq=?";
			break;
		case 3://特色业务考核收入
			tempLogStr  ="特色业务考核收入";
			sqlStr = "update t_ntmisc_userjx t set t.jx_zhts=?,t.zsf_zhts=? where user_id=? and zq=?";
			break;
		case 4://定性考核收入
			tempLogStr  ="定性考核收入";
			sqlStr = "update t_ntmisc_userjx t set t.jx_dx=?,t.zsf_dx=? where user_id=? and zq=?";
			break;
		}
		this.update(condition, daoParent, sqlStr, retMap);
		if (!"0".equals(retMap.get("recUpdCount")))
			logger.info(tempLogStr+" 写入用户绩效表成功");
		else
			logger.info(tempLogStr+" 写入用户绩效表失败");
		iu.rmCondition(condition);
		iu.putCondition(condition, "used", "1");
		iu.putCondition(condition, "userId", userId);
		iu.putCondition(condition, "zq", zq);
		switch(index){
		case 1://业务量考核收入
			tempLogStr = "业务量表使用标识置1";
			sqlStr = "update t_ntmisc_ywlmx t set t.used = ? where user_id = ? and zq=?";
			break;
		case 2://标准产品考核收入
			tempLogStr = "标准产品表使用标识置1";
			sqlStr = "update t_ntmisc_bzcpmx t set t.used = ? where user_id = ? and zq=?";
			break;
		case 3://特色业务考核收入
			tempLogStr = "特色业务表使用标识置1";
			sqlStr = "update t_ntmisc_zhtsmx t set t.used = ? where user_id = ? and zq=?";
			break;
		case 4://定性考核收入
			tempLogStr = "特色业务表使用标识置1";
			sqlStr = "update t_ntmisc_dx t set t.used = ? where user_id = ? and zq=?";
			break;
		}
		this.update(condition, daoParent, sqlStr, retMap);
		if (!"0".equals(retMap.get("recUpdCount")))
			logger.info(tempLogStr+"  成功");
		else
			logger.info(tempLogStr+"  失败");
	}
	
	/**
	 * 机构总包比例查询
	 * 
	 * @param zbblJa
	 */
	public void zbblQuery(JSONArray zbblJa) {
		iu.rmCondition(condition);
		sqlStr = "select * from t_ntmisc_zbbl t";
		this.queryAuto(zbblJa, condition, sqlStr, daoParent, 1);
	}
	
	/**
	 * 考核收入计算逻辑(业务量考核收入、标准产品考核收入、特色业务考核收入公用)
	 * @param index
	 * @param userYwlJa
	 * @param tmd
	 */
	public void pubCalc(JSONArray userYwlJa, TransactionMapData tmd,Map retMap) {
		
		float w = 0;
		float wSum = 0;
		float fw = 0;
		float fwSum = 0;
		float ywlSr = 0;// 业务量考核收入
		String zq = "";// 绩效考核周期
		float total = userYwlJa.size();// 当前统计总人数
		
		String userId = tmd.get("userId").toString();
		String orgId = mediumBus.getUserOrg(userId);
		String orgIn = mediumBus.getOrgIn(orgId);
		JSONArray zbblJa = new JSONArray();// 查询总包比例表格
		this.zbblQuery(zbblJa);
		int index = Integer.parseInt(tmd.get("index").toString());
		logger.info("pubCalc index:"+index);
		if(index < 4){//业务量考核收入、标准产品考核收入、特色业务考核收入
			// 计算wSum
			for (int i = 0; i < total; i++) {
				JSONObject userYwlJson = userYwlJa.getJSONObject(i);
				userId = userYwlJson.getString("user_id");
				/*float dj = this.iu.getFloat(userYwlJson.getFloat("dj"));
				float sg = this.iu.getFloat(userYwlJson.getFloat("sg"));
				float zs = this.iu.getFloat(userYwlJson.getFloat("zs"));
				float zsAvg = this.iu.getFloat(userYwlJson.getFloat("zs_avg"));*/
				w = this.iu.getFloat(userYwlJson.getFloat("w"));
				wSum += w;
			}
			// 计算fw、fwSum
			for (int i = 0; i < total; i++) {
				JSONObject userYwlJson = userYwlJa.getJSONObject(i);
				w = this.iu.getFloat(userYwlJson.getFloat("w"));
				try {
					fw = this.iu.getFloatDecimal(w / wSum * 1000 * total + "", 2);
					fwSum += fw;
					userYwlJson.put("fw", Float.valueOf(fw));
				} catch (Exception e) {
					this.logger.error("业务量考核收入计算-》浮点数格式化为两位小数出错");
				}
				this.logger.info("用户：" + userId + " 个人核算业务量/网点季度人均核算业务量：" + w
						+ "|fw：" + fw);
			}
		}else if(index == 4){//定性考核收入
			// 计算fw、fwSum
			for (int i = 0; i < total; i++) {
				JSONObject userYwlJson = userYwlJa.getJSONObject(i);
				userId = userYwlJson.getString("user_id");
				float point = userYwlJson.getFloatValue("point");
				fw = 1000 + point;
				userYwlJson.put("fw", Float.valueOf(fw));
				fwSum += fw;
			}
		}
		
		for (int i = 0; i < total; i++) {
			float a = 0;// 业务量总包
			JSONObject userYwlJson = userYwlJa.getJSONObject(i);
			userId = userYwlJson.getString("user_id");
			orgId = mediumBus.getUserOrg(userId);
			fw = userYwlJson.getFloatValue("fw");
			zq = userYwlJson.getString("zq");
			logger.info("zbblJa:" + zbblJa.toJSONString());
			for (int j = 0; j < zbblJa.size(); j++) {
				JSONObject zbblJson = zbblJa.getJSONObject(j);
				if (orgId.equals(zbblJson.getString("orgid"))
						&& zq.equals(zbblJson.getString("zq"))) {
					switch(index){
						case 1:
							a = zbblJson.getFloatValue("zb_ywl");// 当前机构（支行）业务量总包
							break;
						case 2:
							a = zbblJson.getFloatValue("zb_bzcp");// 当前机构（支行）标准产品总包
							break;
						case 3:
							a = zbblJson.getFloatValue("zb_zhts");// 当前机构（支行）特色产品总包
							break;
						case 4:
							a = zbblJson.getFloatValue("zb_dx");// 当前机构（支行）定性总包
							break;
					}
				}
			}
			if (a == 0) {
				logger.info("无相关总包，绩效计算周期zq:" + zq + "机构号orgId：" + orgId);
			}
			ywlSr = this.iu.getFloatDecimal(fw / fwSum * a + "", 2);
			zq = userYwlJson.getString("zq");
			this.logger.info("用户：" + userId + " 业务量绩效收入fw：" + fw + "|fwSum:"
					+ fwSum + "|ywlSr:" + ywlSr);
			tmd.put("ywlSr", ywlSr);
			tmd.put("fw", fw);
			tmd.put("zq", zq);
			tmd.put("userId", userId);
			this.khsrInsert(tmd, retMap);
		}
	}
	
	/**
	 * 绩效计算业务逻辑处理
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 * @return
	 */
	public Object calcManage(JSONArray ja,
			LinkedHashMap<String, Object> condition, Map retMap,
			TransactionMapData tmd){
		JSONArray userYwlJa = new JSONArray();// 用户统计
		String userId = tmd.get("userId").toString();
//		String orgId = mediumBus.getUserOrg(userId);
//		String orgIn = mediumBus.getOrgIn(orgId);
		String orgIn = tmd.get("orgIdIn").toString();
		this.iu.rmCondition(condition);
		int index = Integer.parseInt(tmd.get("index").toString());
		switch(index){
		case 1://业务量考核基础数据
			this.sqlStr = " select a.*,b.zs_sum,b.zs_avg,a.zs/b.zs_avg w from (  "
					+ " select a.orgid,a.depid,a.jb_id,a.jb_cat,a.name,b.* from t_ntmisc_user a,t_ntmisc_ywlmx b where a.userid = b.user_id and b.used='0' and orgid in "
					+ orgIn
					+ " ) a left join ( "
					+ " select orgid,sum(zs) zs_sum,avg(zs) zs_avg from( "
					+ " select a.orgid,a.depid,a.jb_id,a.jb_cat,a.name,b.* from t_ntmisc_user a,t_ntmisc_ywlmx b where a.userid = b.user_id and b.used='0' and orgid in "
					+ orgIn + " ) group by orgid) b " + " on a.orgid = b.orgid ";
			break;
		case 2://标准产品考核基础数据
			/*sqlStr = "select orgid,user_id,zq,sum(w) w from( "
					+ " select a.orgid,b.*,c.name bzcp_name,c.unit,b.rs*b.point w "
					+ " from t_ntmisc_user a,t_ntmisc_bzcpmx b,t_ntmisc_bzcp c "
					+ " where a.userid = b.user_id and b.seq_num = c.seq_num and b.used='0' and a.orgid in"
					+ orgIn + " ) group by orgid,user_id,zq ";*/
			sqlStr = " select a.*,b.point w,b.zq,b.oper_time,b.used from (select a.userid,a.name,a.orgid,a.depid,b.orgname,c.depname from t_ntmisc_user a,t_ntmisc_org b,t_ntmisc_dept c"
					+ " where a.orgid = b.orgid and a.depid = c.depid)a,t_ntmisc_usermova b"
					+ " where a.userid = b.user_id and b.used = '0' and a.orgid in"
					+ orgIn;
			break;
		case 3://特色业务考核基础数据
			sqlStr = " select user_id,zq,org_id,sum(w) w from( "
					+ " select a.user_id,a.seq_num,a.point,a.zq, a.rs * a.point w,a.org_id,a.depid "
					+ " from (select a.*,b.orgid org_id,b.depid from t_ntmisc_zhtsmx a ,t_ntmisc_user b where a.user_id = b.userid) a "
					+ " where a.used = '0' " 
					+ " and a.org_id in " 
					+  orgIn
					+ " ) group by user_id,zq,org_id ";
			break;
		case 4://定性考核基础数据
//			sqlStr = "select * from t_ntmisc_dx where used='0' where orgid in";
			sqlStr = " select a.*,b.orgid,c.orgname,b.depid,d.depname from t_ntmisc_dx a,t_ntmisc_user b,t_ntmisc_org c,t_ntmisc_dept d "
					+ "  where a.user_id = b.userid and  b.orgid=c.orgid and b.depid = d.depid and a.used='0' and b.orgid in "
					+ orgIn;
			break;
		case 5://一键计算前四项
			this.startPush(tmd.get("userId").toString(),tmd);
			tmd.put("errorCode", "0");
			tmd.put("errorMsg","准备计算");
			tmd.put("progress", 0);
			tmd.put("finish", "0");
			ArrayList<String> zqArray = (ArrayList<String>) tmd.get("zqArray");
			boolean isCons = true; //四个周期一致
			for (int i = 0; i < zqArray.size(); i++) {
				String zqTemp = zqArray.get(i);
				if(i <= zqArray.size()-2){
					String zqTempNext = zqArray.get(i+1);
					if(zqTemp.equals(zqTempNext)){
						isCons = false;
					}
				}
			}
			if(isCons){
				tmd.put("index", "1");
				this.calcManage(ja, condition, retMap, tmd);
				tmd.put("index", "2");
				this.calcManage(ja, condition, retMap, tmd);
				tmd.put("index", "3");
				this.calcManage(ja, condition, retMap, tmd);
				tmd.put("index", "4");
				this.calcManage(ja, condition, retMap, tmd);
				HttpServletResponse response = (HttpServletResponse) tmd.get("response");
				
				if(zqArray.size() == 0){
					tmd.put("errorCode", "10");
					tmd.put("errorMsg","待考核基础数据不存在,请提前导入基础数据");
					tmd.put("progress", "100");
					tmd.put("finish", "1");
					tmd.put("data", "");
				}else{
					//写文件
					tmd.put("zq", zqArray.get(0));
					this.dataPrepare(tmd);
					
					tmd.put("errorCode", "10");
					tmd.put("errorMsg","计算完成");
					tmd.put("progress", "100");
					tmd.put("finish", "1");
					tmd.put("data", zqArray.get(0));
					logger.info("当前考核周期："+tmd.get("data"));
				}
				
			}else{
				iu.infoPrgsIntrpt(tmd, "无法计算,存在不一致周期号");
				logger.error("无法计算,存在不一致周期号");
				/*tmd.put("errorCode", "10");
				tmd.put("errorMsg","无法计算,存在不一致周期号");
				tmd.put("progress", "100");
				tmd.put("finish", "1");
				tmd.put("data", "");*/
			}
			break;
		}
		if(index < 5){
			this.queryAuto(userYwlJa, condition, this.sqlStr, this.daoParent, 1);
			JSONObject jsonTemp = null;
			//放入所有待考核基础数据周期
			for (int i = 0; i < userYwlJa.size(); i++) {
				try {
					jsonTemp = userYwlJa.getJSONObject(i);
				} catch (Exception e) {
					// TODO: handle exception
				}
				if(jsonTemp != null){
					String zq = jsonTemp.getString("zq");
					((ArrayList<String>) tmd.get("zqArray")).add(zq);
				}
			}
			this.pubCalc(userYwlJa, tmd, retMap);
		}
		return ja;
	}
	
	public void doWork(JSONArray ja,
			LinkedHashMap<String, Object> condition, Map retMap,
			TransactionMapData tmd){
		this.calcManage(ja, condition, retMap, tmd);
	}
}
