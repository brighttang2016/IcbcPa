/**
 * 历史绩效导出 2015-12-28
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

public class F10000081Rcv extends BusParent implements BusReceiver {
	//@Value("${jxExcelPath}")
	@Value("${jxExcelPath}")
	private String jxExcelPath;
	/**
	 * 写入文件
	 * @param table 文件数据源
	 * @param path 文件路径（包含了文件名）
	 */
	public void  fileWrite(JSONArray ja,String path,String fileName,JSONObject hzJson,TransactionMapData tmd){
		String zq = "";
		try {
			logger.info("path:"+path);
			WritableWorkbook book = Workbook.createWorkbook(new File(path));
			WritableSheet sheet  = book.createSheet("第一页", 1);
			sheet.setRowView(0, 800);
//			sheet.setRowView(1, 600);
//			sheet.setRowView(2, 600);
			for (int i = 0; i < ja.size(); i++) {
				sheet.setRowView(i+4, 400);
			}
			for (int i = 0; i < 23; i++) {
				sheet.setColumnView(i,20);
			}
//			sheet.addCell();
			
			sheet.mergeCells(0, 0, 22, 0);
			sheet.mergeCells(0, 1, 22, 1);
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
			
			sheet.addCell(new Label(0, 1, "操作："+tmd.get("userId"),zqWcf));
			sheet.mergeCells(0, 2, 0, 3);
			sheet.addCell(new Label(0, 2, "人力资源编码",fmtColName));
			sheet.mergeCells(1, 2, 1, 3);
			sheet.addCell(new Label(1, 2, "姓名",fmtColName));
			sheet.mergeCells(2, 2, 2, 3);
			sheet.addCell(new Label(2, 2, "机构",fmtColName));
			sheet.mergeCells(3, 2, 3, 3);
			sheet.addCell(new Label(3, 2, "部门",fmtColName));
			sheet.mergeCells(4, 2, 4, 3);
			sheet.addCell(new Label(4, 2, "父绩效岗位",fmtColName));
			sheet.mergeCells(5, 2, 5, 3);
			sheet.addCell(new Label(5, 2, "绩效考核岗位",fmtColName));
			sheet.mergeCells(6, 2, 6, 3);
			sheet.addCell(new Label(6, 2, "考核周期",fmtColName));
			sheet.mergeCells(7, 2, 10, 2);
			sheet.addCell(new Label(7, 2, "支行考核绩效",fmtColName));
			sheet.addCell(new Label(7, 3, "业务量",fmtColName));
			sheet.addCell(new Label(8, 3, "标准产品",fmtColName));
			sheet.addCell(new Label(9, 3, "特色业务",fmtColName));
			sheet.addCell(new Label(10, 3, "定性",fmtColName));
			
			sheet.mergeCells(11, 2, 14, 2);
			sheet.addCell(new Label(11, 2, "网点考核绩效",fmtColName));
			sheet.addCell(new Label(11, 3, "业务量",fmtColName));
			sheet.addCell(new Label(12, 3, "标准产品",fmtColName));
			sheet.addCell(new Label(13, 3, "特色业务",fmtColName));
			sheet.addCell(new Label(14, 3, "定性",fmtColName));
			
			sheet.mergeCells(15, 2, 18, 2);
			sheet.addCell(new Label(15, 2, "支行考核折算分",fmtColName));
			sheet.addCell(new Label(15, 3, "业务量",fmtColName));
			sheet.addCell(new Label(16, 3, "标准产品",fmtColName));
			sheet.addCell(new Label(17, 3, "特色业务",fmtColName));
			sheet.addCell(new Label(18, 3, "定性",fmtColName));
			
			
			sheet.mergeCells(19, 2, 22, 2);
			sheet.addCell(new Label(19, 2, "网点考核折算分",fmtColName));
			sheet.addCell(new Label(19, 3, "业务量",fmtColName));
			sheet.addCell(new Label(20, 3, "标准产品",fmtColName));
			sheet.addCell(new Label(21, 3, "特色业务",fmtColName));
			sheet.addCell(new Label(22, 3, "定性",fmtColName));
			
			sheet.mergeCells(23, 2, 23, 3);
			sheet.addCell(new Label(23, 2, "绩效合计",fmtColName));
			
			/***********************表头结束***********************/
			
			for (int i = 0; i < ja.size(); i++) {
				JSONObject rowJson = ja.getJSONObject(i);
				sheet.addCell(new Label(0, i+4, rowJson.getString("userid"),fmtColCont));
				sheet.addCell(new Label(1, i+4, rowJson.getString("name"),fmtColCont));
				sheet.addCell(new Label(2, i+4, rowJson.getString("orgname"),fmtColCont));
				sheet.addCell(new Label(3, i+4, rowJson.getString("depname"),fmtColCont));
				sheet.addCell(new Label(4, i+4, rowJson.getString("jbjx_pname"),fmtColCont));
				sheet.addCell(new Label(5, i+4, rowJson.getString("jbjx_name"),fmtColCont));
				sheet.addCell(new Label(6, i+4, rowJson.getString("zq"),fmtColCont));
				
				sheet.addCell(new Label(7, i+4, rowJson.getString("jx_ywl_zh"),fmtColCont));
				sheet.addCell(new Label(8, i+4, rowJson.getString("jx_bzcp_zh"),fmtColCont));
				sheet.addCell(new Label(9, i+4, rowJson.getString("jx_tscp_zh"),fmtColCont));
				sheet.addCell(new Label(10, i+4, rowJson.getString("jx_dx_zh"),fmtColCont));
				
				sheet.addCell(new Label(11, i+4, rowJson.getString("jx_ywl_wd"),fmtColCont));
				sheet.addCell(new Label(12, i+4, rowJson.getString("jx_bzcp_wd"),fmtColCont));
				sheet.addCell(new Label(13, i+4, rowJson.getString("jx_tscp_wd"),fmtColCont));
				sheet.addCell(new Label(14, i+4, rowJson.getString("jx_dx_wd"),fmtColCont));
				
				sheet.addCell(new Label(15, i+4, rowJson.getString("zsf_ywl_zh"),fmtColCont));
				sheet.addCell(new Label(16, i+4, rowJson.getString("zsf_bzcp_zh"),fmtColCont));
				sheet.addCell(new Label(17, i+4, rowJson.getString("zsf_tscp_zh"),fmtColCont));
				sheet.addCell(new Label(18, i+4, rowJson.getString("zsf_dx_zh"),fmtColCont));
				
				sheet.addCell(new Label(19, i+4, rowJson.getString("zsf_ywl_wd"),fmtColCont));
				sheet.addCell(new Label(20, i+4, rowJson.getString("zsf_bzcp_wd"),fmtColCont));
				sheet.addCell(new Label(21, i+4, rowJson.getString("zsf_tscp_wd"),fmtColCont));
				sheet.addCell(new Label(22, i+4, rowJson.getString("zsf_dx_wd"),fmtColCont));
				
				sheet.addCell(new Label(23, i+4, rowJson.getString("jx_hj"),fmtColCont));
				
			}
			int i = ja.size();
			sheet.addCell(new Label(0, i+6, "总计:",fmtColCont));
			sheet.addCell(new Label(7, i+6, hzJson.getString("hz_jx_ywl_zh"),fmtColCont));
			sheet.addCell(new Label(8, i+6, hzJson.getString("hz_jx_bzcp_zh"),fmtColCont));
			sheet.addCell(new Label(9, i+6, hzJson.getString("hz_jx_tscp_zh"),fmtColCont));
			sheet.addCell(new Label(10, i+6, hzJson.getString("hz_jx_dx_zh"),fmtColCont));
			
			sheet.addCell(new Label(11, i+6, hzJson.getString("hz_jx_ywl_wd"),fmtColCont));
			sheet.addCell(new Label(12, i+6, hzJson.getString("hz_jx_bzcp_wd"),fmtColCont));
			sheet.addCell(new Label(13, i+6, hzJson.getString("hz_jx_tscp_wd"),fmtColCont));
			sheet.addCell(new Label(14, i+6, hzJson.getString("hz_jx_dx_wd"),fmtColCont));
			
			sheet.addCell(new Label(15, i+6, hzJson.getString("hz_jx_hj"),fmtColCont));
		
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
//		this.jxCalcQuery(tmd, ja);
//		String zq = tmd.get("zqCurr").toString();
		String fileName = "历史绩效-"+tmd.get("userId")+".xls";
		String filePath = tmd.get("path")+jxExcelPath;
		String path = filePath+"\\"+fileName;
		logger.info("fileName:"+fileName);
		logger.info("filePath:"+filePath);
		//总计
		float hz_jx_ywl_zh = 0;
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
		
		
//		tmd.put("errorCode", "10");
//		tmd.put("errorMsg","计算完成");
//		tmd.put("progress", "1");
//		tmd.put("finish", "1");
//		tmd.put("data", tmd.get("zqCurr").toString());
		this.fileWrite(ja, path, fileName,hzJson,tmd);
	}
	@Override
	public void doWork(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub
		this.dataPrepare(tmd);
	}
}
