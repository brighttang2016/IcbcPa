/**
 * excel解析公用业务处理类（学习积分管理模块）
 * 总行基础学习积分、市行基础学分、附件学分、学分手工调整四个模块上传excel模版完全相同
 * @author brighttang 2015-10-21
 */
package com.icbc.nt.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import com.icbc.message.push.MessagePushClient;
import com.icbc.message.push.MessagePushHelper;
import com.icbc.message.push.PushThread;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.bus.FileUploadEvent;
import com.icbc.nt.util.TransactionMapData;

public class ExcelBusImpl extends ExcelBus{

	@Override
	public void excelParseToDb(TransactionMapData tmd) {
		// TODO Auto-generated method stub
		String title = tmd.get("title").toString();
		ArrayList<String> typeList = (ArrayList<String>) tmd.get("typeList");
//		tmd.put("errorCode", "10");
		tmd.put("errorMsg", "上传成功,正在解析文件");
		try {
			FileInputStream fis = new FileInputStream(new File(tmd.get("path")+"\\"+tmd.get("fileName")));
			Workbook workBook = Workbook.getWorkbook(fis);
			Sheet  sheet = workBook.getSheet(0);
			
			int rowLenth = sheet.getRows();
			int colLenth = sheet.getColumns();
//			System.out.println(rowLenth+"|"+colLenth);
			LinkedList<LinkedHashMap<String, Object>> condList = new LinkedList<LinkedHashMap<String,Object>>();
			boolean excFlag = false;//异常标识，false:无异常 true:存在异常
			for (int i = 3; i < rowLenth; i++) {
//				boolean rowExc = false;
				LinkedHashMap<String, Object> rowMap = new LinkedHashMap<String, Object>();
				Cell cell = sheet.getCell(0, i);
				rowMap.put("column"+1, cell.getContents());
				cell = sheet.getCell(2, i);
				String typeId = cell.getContents();
				logger.info("typeId:"+typeId);
				rowMap.put("column"+2, typeId);
				
				cell = sheet.getCell(4, i);
				rowMap.put("column"+3, cell.getContents());
				rowMap.put("column"+4, iu.getYear());
				rowMap.put("column"+5, iu.getTime());
//				logger.info("rowMap:"+rowMap);
				
				//行数据校验
				boolean rowExc = false;
				//都不为空
				String userIdTemp = rowMap.get("column1").toString().trim();
				String typeIdTemp = rowMap.get("column2").toString().trim();
				String pointTemp = rowMap.get("column3").toString().trim();
				if(!"".equals(userIdTemp) && !"".equals(typeIdTemp) && !"".equals(pointTemp)){
					if(!iu.isNumber(rowMap.get("column3").toString().trim())){//第五列积分必须为数字
						rowExc = true;
						excFlag = true;
						tmd.put("finish", "1");
						tmd.put("errorCode", "11");
						tmd.put("errorMsg", "格式错误,积分为数字!");
						logger.info("格式错误,积分为数字!");
					}
					
					boolean typeIdExist = false;//当前excel行typeid是否为typeList中之一
					for (int j = 0; j < typeList.size(); j++) {
						logger.info("typeId:"+typeId);
						logger.info("typeId:"+typeId+"|"+typeList.get(j)+"|"+typeId.equals(typeList.get(j)));
						if(typeId.equals(typeList.get(j)))
							typeIdExist = true;
					}
					logger.info("typeIdExist:"+typeIdExist);
					if(!typeIdExist){
						rowExc = true;
						excFlag  = true;
						tmd.put("finish", "1");
						tmd.put("errorCode", "11");
						tmd.put("errorMsg", "错误,积分类型编号错误!");
						logger.info("错误,积分类型编号错误!");
					}
					if(!rowExc){
						condList.add(rowMap);
						logger.info("导入行rowMap:"+rowMap);
					}
				}else if((!userIdTemp.equals("") || !typeIdTemp.equals("") || !pointTemp.equals("")) 
						&& (userIdTemp.equals("") || typeIdTemp.equals("") || pointTemp.equals(""))){//存在部分为空，部分不为空
					excFlag = true;
					tmd.put("finish", "1");
					tmd.put("errorCode", "11");
					tmd.put("errorMsg", "错误,行空与非空数据并存!");
					logger.info("错误,行空与非空数据并存!");
				}
			}
			if(!excFlag){
				tmd.put("errorMsg", "解析成功,准备批量导入");
				logger.info("condList.size():"+condList.size());
				Map retMap = new HashMap();
				sqlStr = "insert into t_ntmisc_credit(user_id,ctype_id,point,point_year,oper_date) values(?,?,?,?,?)"; 
				this.updateBat(condList, daoParent, retMap, sqlStr, 1);
				try {
					if(Integer.parseInt(retMap.get("recUpdCount").toString()) != 0){
						tmd.put("errorCode", "10");
						tmd.put("errorMsg", "批量导入"+title+"成功");
					}else{
						tmd.put("errorCode", "11");
						tmd.put("errorMsg", "批量导入"+title+"失败");
					}
				} catch (Exception e) {
					tmd.put("errorCode", "11");
					tmd.put("errorMsg", "批量导入"+title+"失败");
				}
				tmd.put("finish", "1");
			}
		} catch (Exception e1) {
			tmd.put("finish", "1");
			tmd.put("errorCode", "11");
			tmd.put("errorMsg", "导入失败,系统异常!");
			logger.error("导入失败,系统异常!");
			e1.printStackTrace();
			iu.error(logger, e1);
		}
	}
}
