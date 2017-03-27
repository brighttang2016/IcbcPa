/**
 * 总包导入（柜员）
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

public class Excelf30052 extends BusParent implements FileUploadListener {
	private static String txCode = "30052";
	@Autowired
	MediumBus mediumBus;
	@Autowired
	BusDispatcherImpl busDispatcherImpl;
	

	/**
	 * 查询机构柜员(客服类)人数
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
		logger.info("准备批量导入柜员总包");
		int gyNum = 0;
		JSONArray jsonArray = JSONArray.parseArray(data);
		logger.info("jsonArray.toJSONString():"+jsonArray.toJSONString());
		boolean queryFlag = false;//机构柜员人数查询标识，false:未查询，true:已查询
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject zbGyJson = jsonArray.getJSONObject(i);
			JSONArray zbblJa = new JSONArray();
			iu.rmCondition(condition);
			String orgId = zbGyJson.getString("orgId");
			iu.putCondition(condition, "orgid", orgId);
			iu.putCondition(condition, "zq", zbGyJson.getString("zq"));
			sqlStr = "select * from t_ntmisc_zbbl t";
			this.queryAuto(zbblJa, condition, sqlStr, daoParent, 1);
			if(!queryFlag){
				gyNum = this.getGyNum(orgId);//机构柜员数
				queryFlag = true;
			}
			
			float zbGyAvg = zbGyJson.getFloatValue("zbGyAvg");//柜员人均总包
			float blGyYwl = zbGyJson.getFloatValue("blGyYwl");//柜员业务量比例
			float blGyBzcp = zbGyJson.getFloatValue("blGyBzcp");//柜员标准产品比例
			float blGyZhts = zbGyJson.getFloatValue("blGyZhts");//柜员支行特色比例
			float blGydx = zbGyJson.getFloatValue("blGydx");//柜员定性比例
			
			float zbGyYwl = (zbGyAvg*gyNum)*blGyYwl;//机构柜员业务量总包
			float zbGyBzcp = (zbGyAvg*gyNum)*blGyBzcp;//机构柜员标准产品总包
			float zbGyZhts = (zbGyAvg*gyNum)*blGyZhts;//机构柜员支行特色总包
			float zbGyDx = (zbGyAvg*gyNum)*blGydx;//机构柜员定性总包
			
			if(zbblJa.size() > 0){
				logger.info("将执行update操作");
				iu.rmCondition(condition);
				iu.putCondition(condition, "zbGyAvg", zbGyAvg+"");
				iu.putCondition(condition, "blGyYwl", blGyYwl+"");
				iu.putCondition(condition, "blGyBzcp", blGyBzcp+"");
				iu.putCondition(condition, "blGyZhts", blGyZhts+"");
				iu.putCondition(condition, "blGydx", blGydx+"");
				
				iu.putCondition(condition, "gyNum", gyNum+"");
				
				iu.putCondition(condition, "zbGyYwl", zbGyYwl+"");
				iu.putCondition(condition, "zbGyBzcp", zbGyBzcp+"");
				iu.putCondition(condition, "zbGyZhts", zbGyZhts+"");
				iu.putCondition(condition, "zbGyDx", zbGyDx+"");
				
				iu.putCondition(condition, "orgId", orgId);
				iu.putCondition(condition, "zq", zbGyJson.getString("zq"));
				sqlStr = " update t_ntmisc_zbbl t set t.zb_gyavg=?,t.bl_gyywl=?,t.bl_gybzcp=?,t.bl_gyzhts=?,t.bl_gydx =?,t.gy_num=?,t.zb_gyywl=?,t.zb_gybzcp=?,t.zb_gyzhts=?,t.zb_gydx=? where t.orgid=? and t.zq=? ";
			}else{
				logger.info("将执行insert操作");
				iu.rmCondition(condition);
				iu.putCondition(condition, "orgId", orgId);
				iu.putCondition(condition, "zq", zbGyJson.getString("zq"));
				iu.putCondition(condition, "zbGyAvg", zbGyAvg+"");
				iu.putCondition(condition, "blGyYwl", blGyYwl+"");
				iu.putCondition(condition, "blGyBzcp", blGyBzcp+"");
				iu.putCondition(condition, "blGyZhts", blGyZhts+"");
				iu.putCondition(condition, "blGydx", blGydx+"");
				
				iu.putCondition(condition, "gyNum", gyNum+"");
				
				iu.putCondition(condition, "zbGyYwl", zbGyYwl+"");
				iu.putCondition(condition, "zbGyBzcp", zbGyBzcp+"");
				iu.putCondition(condition, "zbGyZhts", zbGyZhts+"");
				iu.putCondition(condition, "zbGyDx", zbGyDx+"");
				
				sqlStr = "insert into t_ntmisc_zbbl(orgid,zq,zb_gyavg,bl_gyywl,bl_gybzcp,bl_gyzhts,bl_gydx,gy_num,zb_gyywl,zb_gybzcp,zb_gyzhts,zb_gydx) values(?,?,?,?,?,?,?,?,?,?,?,?)";
			}
			this.update(condition, daoParent, sqlStr, retMap);
		}
	}
	
	public void actionToFileUpload(FileUploadEvent e) {
		logger.info("准备解析柜员总包批量文件 txCode:"+txCode);
		JSONArray ja = new JSONArray();
		String point = "";//员工mova标准业绩视图得分
		HashMap retMap = new HashMap();
		tmd = e.getTmd();
		logger.info("tmd txCode:"+tmd.get("txCode"));
//		mpc.pushMessage(tmd.get("userId").toString(), "准备写入excel");
		if(txCode.equals(tmd.get("txCode"))){
			ArrayList<String> typeList = new ArrayList<String>();
			tmd.put("title", "批量导入柜员总包数据");
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
//				String exlUserId = sheet.getCell(0, i).getContents();
//				tmd.put("exlUserId", exlUserId);//当前验证excel行userid
//				point = sheet.getCell(2,i).getContents();//完成值
//				String zq = sheet.getCell(3,i).getContents();//考核周期数
				String orgId = sheet.getCell(0, i).getContents();
				String zbGyAvg = sheet.getCell(2, i).getContents();
				String blGyYwl = sheet.getCell(3, i).getContents();
				String blGyBzcp = sheet.getCell(4, i).getContents();
				String blGyZhts = sheet.getCell(5, i).getContents();
				String blGydx = sheet.getCell(6, i).getContents();
				String zq = sheet.getCell(7, i).getContents();
				
				/*if(!iu.isNumber(point)){
					tmd.put("errorCode", "11");
					tmd.put("errorMsg", "完成值必须为数字,导入失败!");
					tmd.put("finish", "1");
					logger.info("完成值必须为数字,导入失败！");
					return;
				}*/
				rowJson.put("orgId", orgId);
				rowJson.put("zbGyAvg", zbGyAvg);
				rowJson.put("blGyYwl", blGyYwl);
				rowJson.put("blGyBzcp", blGyBzcp);
				rowJson.put("blGyZhts", blGyZhts);
				rowJson.put("blGydx", blGydx);
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
			logger.info("批量导入柜员总包 tableJa:"+tableJa);
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
