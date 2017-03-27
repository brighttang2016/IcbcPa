/**
 * 总包导入（客户经理） 2015-11-19
 */
package com.icbc.nt.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.bus.FileUploadEvent;
import com.icbc.nt.bus.MediumBus;
import com.icbc.nt.bus.dispatcher.BusDispatcherImpl;
import com.icbc.nt.util.TransactionMapData;

public class Excelf30053 extends BusParent implements FileUploadListener {
	private static String txCode = "30053";
	@Autowired
	MediumBus mediumBus;
	@Autowired
	BusDispatcherImpl busDispatcherImpl;
	
	/**
	 * 查询机构客户经理(销售类)人数
	 * @param orgId
	 * @return
	 */
	public int getGyNum(String orgId){
		int gyNum = 0;
		iu.rmCondition(condition);
		iu.putCondition(condition, "orgId", orgId);
		JSONArray ja = new JSONArray();
		sqlStr = " select a.orgid,b.jb_name,count(*) gy_num from t_ntmisc_user a,t_ntmisc_job b "
				+ " where a.jb_cat = b.jb_id and b.jb_name = '客服类岗位' and a.orgid=? "
				+ " group by a.orgid,b.jb_name ";
		this.queryManu(ja, condition, sqlStr, daoParent, 1);
		try {
			JSONObject zbblJson = ja.getJSONObject(0);
			gyNum = zbblJson.getIntValue("gy_num");
		} catch (Exception e) {}
		return gyNum;
	}
	
	public void doBat(Map retMap,String data){
		logger.info("准备批量导入客户经理总包");
		int khjlNum = 0;
		JSONArray jsonArray = JSONArray.parseArray(data);
		logger.info("jsonArray.toJSONString():"+jsonArray.toJSONString());
		boolean queryFlag = false;//机构客户经理人数查询标识，false:未查询，true:已查询
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject zbJson = jsonArray.getJSONObject(i);
			JSONArray zbblJa = new JSONArray();
			iu.rmCondition(condition);
			String orgId = zbJson.getString("orgId");
			iu.putCondition(condition, "orgid", orgId);
			iu.putCondition(condition, "zq", zbJson.getString("zq"));
			sqlStr = "select * from t_ntmisc_zbbl t";
			this.queryAuto(zbblJa, condition, sqlStr, daoParent, 1);
			
			if(!queryFlag){
				khjlNum = this.getGyNum(orgId);//机构客户经理数
				queryFlag = true;
			}
			
			float zbKhjlAvg = zbJson.getFloatValue("zbKhjlAvg");//客户经理人均总包
			float blKhjlBzcp = zbJson.getFloatValue("blKhjlBzcp");//客户经理标准产品比例
			float blKhjlZhts = zbJson.getFloatValue("blKhjlZhts");//客户经理支行特色比例
			float blKhjlDx = zbJson.getFloatValue("blKhjlDx");//客户经理定性比例
			
		
			float zbKhjlBzcp = (zbKhjlAvg*khjlNum)*blKhjlBzcp;//机构客户经理标准产品总包
			float zbKhjlZhts = (zbKhjlAvg*khjlNum)*blKhjlZhts;//机构客户经理支行特色总包
			float zbKhjlDx = (zbKhjlAvg*khjlNum)*blKhjlDx;//机构客户经理定性总包
			
			if(zbblJa.size() > 0){
				logger.info("将执行update操作");
				iu.rmCondition(condition);
				iu.putCondition(condition, "zbKhjlAvg", zbKhjlAvg+"");
				iu.putCondition(condition, "blKhjlBzcp", blKhjlBzcp+"");
				iu.putCondition(condition, "blKhjlZhts", blKhjlZhts+"");
				iu.putCondition(condition, "blKhjlDx", blKhjlDx+"");
				
				iu.putCondition(condition, "khjlNum", khjlNum+"");
				
				iu.putCondition(condition, "zbKhjlBzcp", zbKhjlBzcp+"");
				iu.putCondition(condition, "zbKhjlZhts", zbKhjlZhts+"");
				iu.putCondition(condition, "zbKhjlDx", zbKhjlDx+"");
				
				iu.putCondition(condition, "orgId", orgId);
				iu.putCondition(condition, "zq", zbJson.getString("zq"));
				sqlStr = " update t_ntmisc_zbbl t set t.zb_Khjlavg=?,t.bl_Khjlbzcp=?,t.bl_Khjlzhts=?,t.bl_Khjldx =?,t.KHJL_NUM=?,t.ZB_KHJLBZCP=?,t.ZB_KHJLZHTS=?,t.ZB_KHJLDX=? where t.orgid=? and t.zq=? ";
			}else{
				logger.info("将执行insert操作");
				iu.rmCondition(condition);
				iu.putCondition(condition, "orgId", orgId);
				iu.putCondition(condition, "zq", zbJson.getString("zq"));
				iu.putCondition(condition, "zbKhjlAvg", zbKhjlAvg+"");
				iu.putCondition(condition, "blKhjlBzcp", blKhjlBzcp+"");
				iu.putCondition(condition, "blKhjlZhts", blKhjlZhts+"");
				iu.putCondition(condition, "blKhjlDx", blKhjlDx+"");
				
				iu.putCondition(condition, "khjlNum", khjlNum+"");
				
				iu.putCondition(condition, "zbKhjlBzcp", zbKhjlBzcp+"");
				iu.putCondition(condition, "zbKhjlZhts", zbKhjlZhts+"");
				iu.putCondition(condition, "zbKhjlDx", zbKhjlDx+"");
				
				sqlStr = "insert into t_ntmisc_zbbl(orgid,zq,zb_Khjlavg,bl_Khjlbzcp,bl_Khjlzhts,bl_Khjldx,KHJL_NUM,ZB_KHJLBZCP,ZB_KHJLZHTS,ZB_KHJLDX) values(?,?,?,?,?,?,?,?,?,?)";
			}
			this.update(condition, daoParent, sqlStr, retMap);
		}
	}
	

	public void actionToFileUpload(FileUploadEvent e) {
		logger.info("准备解析客户经理总包批量文件 txCode:"+txCode);
		JSONArray ja = new JSONArray();
		String point = "";//员工mova标准业绩视图得分
		HashMap retMap = new HashMap();
		tmd = e.getTmd();
		logger.info("tmd txCode:"+tmd.get("txCode"));
//		mpc.pushMessage(tmd.get("userId").toString(), "准备写入excel");
		if(txCode.equals(tmd.get("txCode"))){
			ArrayList<String> typeList = new ArrayList<String>();
			tmd.put("title", "批量导入客户经理总包数据");
			tmd.put("errorMsg", "上传成功,正在解析文件");
			logger.info("上传成功,正在解析文件");
			//解析excel
			String title = tmd.get("title").toString();
			
			FileInputStream fis = null;
			Workbook workBook = null;
			Sheet  sheet = null;
			try {
				fis = new FileInputStream(new File(tmd.get("path")+"\\"+tmd.get("fileName")));
				workBook = Workbook.getWorkbook(fis);
				sheet = workBook.getSheet(0);
			} catch (Exception e1) {
				e1.printStackTrace();
				iu.error(logger, e1);
			}
			tmd.put("errorMsg", "解析成功,准备批量导入");
			logger.info("解析成功,准备批量导入");
			int rowLenth = sheet.getRows();
			boolean excFlag = false;//异常标识，false:无异常 true:存在异常
			LinkedList<LinkedHashMap<String, Object>> condList = new LinkedList<LinkedHashMap<String,Object>>();
			JSONArray tableJa = new JSONArray();//表格数据
			logger.info("rowLenth:"+rowLenth);
			for (int i = 3; i < rowLenth; i++) {
				JSONObject rowJson = new JSONObject();
				String orgId = sheet.getCell(0, i).getContents();
				String zbKhjlAvg = sheet.getCell(2, i).getContents();
				String blKhjlBzcp = sheet.getCell(3, i).getContents();
				String blKhjlZhts = sheet.getCell(4, i).getContents();
				String blKhjlDx = sheet.getCell(5, i).getContents();
				String zq = sheet.getCell(6, i).getContents();
				
				/*if(!iu.isNumber(point)){
					tmd.put("errorCode", "11");
					tmd.put("errorMsg", "完成值必须为数字,导入失败!");
					tmd.put("finish", "1");
					logger.info("完成值必须为数字,导入失败！");
					return;
				}*/
				rowJson.put("orgId", orgId);
				rowJson.put("zbKhjlAvg", zbKhjlAvg);
				rowJson.put("blKhjlBzcp", blKhjlBzcp);
				rowJson.put("blKhjlZhts", blKhjlZhts);
				rowJson.put("blKhjlDx", blKhjlDx);
				rowJson.put("zq", zq);
				//用户验证
//				busDispatcherImpl.exlUserValidate(ja, condition, retMap, tmd);
				try {
					excFlag = (Boolean) tmd.get("excFlag");
				} catch (Exception e2) {
					// TODO: handle exception
				}
				
				if(!excFlag)
					tableJa.add(rowJson);
				tableJa.add(rowJson);
				logger.info("tableJa:"+tableJa.toJSONString());
				
			}
			logger.info("批量导入客户经理总包 tableJa:"+tableJa);
			if(!excFlag){
//					mediumBus.uctBat(retMap, tableJa+"");
				this.doBat(retMap, tableJa+"");
				if(!"0".equals(retMap.get("recUpdCount"))){
					tmd.put("errorCode", "10");
					tmd.put("errorMsg", "批量导入成功!");
					logger.info("批量导入成功！");
				}else{
					tmd.put("errorCode", "11");
					tmd.put("errorMsg", "导入失败,数据录入失败!");
					logger.info("导入失败,数据录入失败！");
				}
			}
			tmd.put("finish", "1");
		}
	}
}
