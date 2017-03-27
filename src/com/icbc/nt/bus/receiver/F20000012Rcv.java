/**
 * 历史绩效导出（新版） 2015-01-13
 */
package com.icbc.nt.bus.receiver;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.util.TransactionMapData;

public class F20000012Rcv extends BusParent implements BusReceiver {
	//@Value("${jxExcelPath}")
	@Value("${jxExcelPath}")
	private String jxExcelPath;
	
	/**
	 * 写入文件
	 * @param tmd 变量池
	 */
	public void  fileWrite(TransactionMapData tmd){
		String zq = "";
		try {
			String filePath = tmd.get("filePath").toString();//文件路径
			String fileName = tmd.get("fileName").toString();
			JSONArray ja = (JSONArray) tmd.get("ja");//数据源
			logger.debug("path:"+filePath+"|数据源ja:"+ja.toJSONString());
			WritableWorkbook book = Workbook.createWorkbook(new File(filePath+"\\"+fileName));
			WritableSheet sheet  = book.createSheet("第一页", 1);
			sheet.setRowView(0, 800);
//			sheet.setRowView(1, 600);
//			sheet.setRowView(2, 600);
			for (int i = 0; i < ja.size(); i++) {
				sheet.setRowView(i+4, 400);
			}
			for (int i = 0; i < 11; i++) {
				sheet.setColumnView(i,20);
			}
//			sheet.addCell();
			
			sheet.mergeCells(0, 0, 10, 0);
			sheet.mergeCells(0, 1, 10, 1);
			WritableFont fontTitle = new WritableFont(WritableFont.TIMES,20,WritableFont.BOLD);
			WritableFont zqWf = new WritableFont(WritableFont.TIMES,12,WritableFont.BOLD);
			WritableFont fontColName = new WritableFont(WritableFont.TIMES,12,WritableFont.BOLD);
			WritableFont fontColCont = new WritableFont(WritableFont.TIMES,10,WritableFont.NO_BOLD);
			
			WritableCellFormat fmtTitle = new WritableCellFormat(fontTitle);
			WritableCellFormat zqWcf = new WritableCellFormat(zqWf);
			WritableCellFormat fmtColName = new WritableCellFormat(fontColName);
			WritableCellFormat fmtColCont = new WritableCellFormat(fontColCont);
			
			fmtTitle.setVerticalAlignment(VerticalAlignment.CENTRE);
			fmtTitle.setAlignment(Alignment.LEFT);
			zqWcf.setAlignment(Alignment.LEFT);
//			fmtColName.setVerticalAlignment(VerticalAlignment.CENTRE);
			fmtColName.setAlignment(Alignment.CENTRE);
//			fmtColName.setBackground(Colour.GRAY_25);//设置列名背景色
//			fmtColCont.setVerticalAlignment(VerticalAlignment.CENTRE);
			fmtColCont.setAlignment(Alignment.CENTRE);
			/***********************表头开始***********************/
			sheet.addCell(new Label(0, 0, "绩效收入计算结果",fmtTitle));
			/*if(ja.size() > 0){
				zq = ja.getJSONObject(0)==null?"":ja.getJSONObject(0).getString("zq");
			}*/
			
			sheet.addCell(new Label(0, 1, "制表人："+tmd.get("userId"),zqWcf));
			sheet.mergeCells(0, 2, 0, 3);
			
			sheet.addCell(new Label(0, 2, "机构名称",fmtColName));
			sheet.mergeCells(1, 2, 1, 3);
			sheet.addCell(new Label(1, 2, "部门名称",fmtColName));
			sheet.mergeCells(2, 2, 2, 3);
			sheet.addCell(new Label(2, 2, "人力资源编码",fmtColName));
			sheet.mergeCells(3, 2, 3, 3);
			sheet.addCell(new Label(3, 2, "姓名",fmtColName));
			sheet.mergeCells(4, 2, 4, 3);
			sheet.addCell(new Label(4, 2, "岗位(绩效考核)",fmtColName));
			sheet.mergeCells(5, 2, 5, 3);
			sheet.addCell(new Label(5, 2, "考核周期",fmtColName));
			sheet.mergeCells(6, 2, 9, 2);
			sheet.addCell(new Label(6, 2, "绩效",fmtColName));
			sheet.addCell(new Label(6, 3, "岗位履职考核",fmtColName));
			sheet.addCell(new Label(7, 3, "网点自由考核",fmtColName));
			sheet.addCell(new Label(8, 3, "全员营销奖励",fmtColName));
			sheet.addCell(new Label(9, 3, "网点手动分配",fmtColName));
			
			sheet.mergeCells(10, 2, 10, 3);
			sheet.addCell(new Label(10, 2, "总绩效",fmtColName));
			/***********************表头结束***********************/
			for (int i = 0; i < ja.size(); i++) {
				JSONObject rowJson = ja.getJSONObject(i);
				sheet.addCell(new Label(0, i+4, rowJson.getString("orgname"),fmtColCont));
				sheet.addCell(new Label(1, i+4, rowJson.getString("depname"),fmtColCont));
				sheet.addCell(new Label(2, i+4, rowJson.getString("userid"),fmtColCont));
				sheet.addCell(new Label(3, i+4, rowJson.getString("name"),fmtColCont));
				sheet.addCell(new Label(4, i+4, rowJson.getString("jbjx_pname"),fmtColCont));
				sheet.addCell(new Label(5, i+4, rowJson.getString("zq"),fmtColCont));
				sheet.addCell(new Label(6, i+4, rowJson.getString("jx_gwlz"),fmtColCont));
				
				sheet.addCell(new Label(7, i+4, rowJson.getString("jx_wdzykh"),fmtColCont));
				sheet.addCell(new Label(8, i+4, rowJson.getString("jx_qyyxjl"),fmtColCont));
				sheet.addCell(new Label(9, i+4, rowJson.getString("jx_wdsdfp"),fmtColCont));
				sheet.addCell(new Label(10, i+4, rowJson.getString("jx_sum"),fmtColCont));
			}
			book.write();
			book.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 数据准备
	 */
	public void dataPrepare(TransactionMapData tmd){
//		System.out.println("********dataPrepare*********");
		JSONArray ja = (JSONArray) tmd.get("ja");//excel数据源
		String zqCurr = tmd.get("zqCurr").toString();
//		String fileName = "历史绩效-"+tmd.get("userId")+zqCurr+".xls";
		String fileName = tmd.get("fileName").toString();
//		String filePath = tmd.get("path")+jxExcelPath+"\\"+fileName;
//		String path = filePath+"\\"+fileName;
//		tmd.put("filePath", filePath);//文件路径（文件路径+文件名）入变量池
		logger.debug("fileName:"+fileName);
//		logger.info("filePath:"+filePath);
		//总计
		/*float hz_jx_ywl_zh = 0;
		float hz_jx_bzcp_zh = 0;
		float hz_jx_tscp_zh = 0;
		float hz_jx_dx_zh = 0;
		float hz_jx_ywl_wd = 0;
		float hz_jx_bzcp_wd = 0;
		float hz_jx_tscp_wd = 0;
		float hz_jx_dx_wd = 0;
		float hz_jx_hj = 0;
		//计算汇总
		for (int i = 0; i < ja.size(); i++) {
			JSONObject json = ja.getJSONObject(i);
			float jx_ywl_zh = iu.getFloatDecimal(json.getString("jx_ywl_zh"), 2);
			float jx_bzcp_zh = iu.getFloatDecimal(json.getString("jx_bzcp_zh"), 2);
			float jx_tscp_zh = iu.getFloatDecimal(json.getString("jx_tscp_zh"), 2);
			float jx_dx_zh = iu.getFloatDecimal(json.getString("jx_dx_zh"), 2);
			float jx_ywl_wd = iu.getFloatDecimal(json.getString("jx_ywl_wd"), 2);
			float jx_bzcp_wd = iu.getFloatDecimal(json.getString("jx_bzcp_wd"), 2);
			float jx_tscp_wd = iu.getFloatDecimal(json.getString("jx_tscp_wd"), 2);
			float jx_dx_wd = iu.getFloatDecimal(json.getString("jx_dx_wd"), 2);
			float jx_hj = jx_ywl_zh+jx_bzcp_zh+jx_tscp_zh+jx_dx_zh
					+jx_ywl_wd+jx_bzcp_wd+jx_tscp_wd+jx_dx_wd;
			hz_jx_ywl_zh += jx_ywl_zh;
			hz_jx_bzcp_zh += jx_bzcp_zh;
			hz_jx_tscp_zh += jx_tscp_zh;
			hz_jx_dx_zh += jx_dx_zh;
			hz_jx_ywl_wd += jx_ywl_wd;
			hz_jx_bzcp_wd += jx_bzcp_wd;
			hz_jx_tscp_wd += jx_tscp_wd;
			hz_jx_dx_wd += jx_dx_wd;
			hz_jx_hj += jx_hj;
			json.put("jx_hj", jx_hj+"");
		}
		JSONObject hzJson = new JSONObject();
		hzJson.put("hz_jx_ywl_zh", iu.getFloatDecimal(hz_jx_ywl_zh+"", 2));
		hzJson.put("hz_jx_bzcp_zh", iu.getFloatDecimal(hz_jx_bzcp_zh+"",2));
		hzJson.put("hz_jx_tscp_zh", iu.getFloatDecimal(hz_jx_tscp_zh+"", 2));
		hzJson.put("hz_jx_dx_zh", iu.getFloatDecimal(hz_jx_dx_zh+"", 2));
		
		hzJson.put("hz_jx_ywl_wd", iu.getFloatDecimal(hz_jx_ywl_wd+"", 2));
		hzJson.put("hz_jx_bzcp_wd", iu.getFloatDecimal(hz_jx_bzcp_wd+"",2));
		hzJson.put("hz_jx_tscp_wd", iu.getFloatDecimal(hz_jx_tscp_wd+"", 2));
		hzJson.put("hz_jx_dx_wd", iu.getFloatDecimal(hz_jx_dx_wd+"", 2));
		
		hzJson.put("hz_jx_hj", iu.getFloatDecimal(hz_jx_hj+"",2));
		logger.info("总计hzJson："+hzJson);
		
		*/
//		tmd.put("errorCode", "10");
//		tmd.put("errorMsg","计算完成");
//		tmd.put("progress", "1");
//		tmd.put("finish", "1");
//		tmd.put("data", tmd.get("zqCurr").toString());
		this.fileWrite(tmd);
	}
	@Override
	public void doWork(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub
	}
	public void doWork(TransactionMapData tmd) {
		logger.debug("数据导出");
		this.dataPrepare(tmd);
	}
}
