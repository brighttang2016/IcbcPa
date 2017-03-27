/**
 * 绩效计算 2015-12-25
 */
package com.icbc.nt.bus.receiver;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.bus.dispatcher.BusDispatcherImpl;
import com.icbc.nt.util.TransactionMapData;

public class F10000070Rcv extends BusParent implements BusReceiver {
	@Autowired
	private BusDispatcherImpl busDispatcherImpl;
	@Override
	public void doWork(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd) {
		// TODO Auto-generated method stub
		if("0310000000".equals(tmd.get("brOrgId"))){
			tmd.put("finish", "1");
			tmd.put("errorCode", "11");
			tmd.put("errorMsg", "非分支行用户,无法计算绩效");
			return;
		}
		/**********************************数据准备阶段开始**********************************/
//		1、初始化机构总包人均表t_ntmisc_orgzbrj中未初始化的总包相关部分:不考虑MOVA挂钩的网点人均总包、不参与考核人员总包、参与考核人员总包、纳入MOVA考核部分抢回总包、考虑mova后新网点总包、参与考核人员新总包、参与考核人均总包
		busDispatcherImpl.zbInitZbrj(ja, condition, retMap, tmd);
//		2、初始化机构总包人数表t_ntmisc_orgzbrs中总包相关部分：网点对应绩效考核岗位的绩效总包、网点对应绩效考核岗位的保留总包、网点对应的绩效考核岗位的当期可分配总包
		busDispatcherImpl.zbInitZbrs(ja, condition, retMap, tmd);
//		3、保存机构剩余总包
		busDispatcherImpl.zbSyCalc(tmd);
//		4、初始化总包分配表t_ntmisc_orgzbfp中：业务量总包、标准产品总包、支行特色总包、定性总包
		busDispatcherImpl.zbFpCalc(tmd);
//		retMap.put("errorCode", "12");
//		retMap.put("errorMsg", "数据初始化");
		/**********************************数据准备阶段结束**********************************/
		
		/**********************************绩效计算阶段开始**********************************/
//		5、业务量绩效计算(网点考核)
		tmd.put("progress", "0.1");
		tmd.put("progressName", "业务量绩效1");
		busDispatcherImpl.jxYwlCalc(ja, condition, retMap, tmd);
//		6、业务量绩效计算(支行考核)
		tmd.put("progress", "0.2");
		tmd.put("progressName", "业务量绩效2");
		busDispatcherImpl.jxYwlCalcZh(ja, condition, retMap, tmd);
//		7、标准产品绩效计算（网点考核）
		tmd.put("progress", "0.3");
		tmd.put("progressName", "标准产品绩效1");
		busDispatcherImpl.jxBzcpCalc(ja, condition, retMap, tmd);
//		8、标准产品绩效计算（支行考核）
		tmd.put("progress", "0.4");
		tmd.put("progressName", "标准产品绩效2");
		busDispatcherImpl.jxBzcpZhCalc(ja, condition, retMap, tmd);
//		9、支行特色绩效计算（网点考核）
		tmd.put("progress", "0.5");
		tmd.put("progressName", "支行特色绩效1");
		busDispatcherImpl.jxZhtsCalc(ja, condition, retMap, tmd);
//		10、支行特色绩效计算（支行考核）
		tmd.put("progress", "0.6");
		tmd.put("progressName", "支行特色绩效2");
		busDispatcherImpl.jxZhtsZhCalc(ja, condition, retMap, tmd);
//		11、定性绩效计算（网点考核）
		tmd.put("progress", "0.7");
		tmd.put("progressName", "定性绩效1");
		busDispatcherImpl.jxDxCalc(ja, condition, retMap, tmd);
//		12、定性绩效计算（支行考核）
		tmd.put("progress", "0.8");
		tmd.put("progressName", "定性绩效2");
		busDispatcherImpl.jxDxCalcZh(ja, condition, retMap, tmd);
//		13、生成绩效文件
		tmd.put("progress", "0.9");
		tmd.put("progressName", "生成绩效文件");
		busDispatcherImpl.jxFileWrite(ja, condition, retMap, tmd);//生成绩效文件
		
		/**********************************绩效计算阶段结束**********************************/
	}
}
