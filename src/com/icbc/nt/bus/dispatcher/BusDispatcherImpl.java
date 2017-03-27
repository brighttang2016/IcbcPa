/**
 * 业务逻辑转发器2015-11-16
 */
package com.icbc.nt.bus.dispatcher;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.alibaba.fastjson.JSONArray;
import com.icbc.nt.bus.BusParent;
import com.icbc.nt.bus.command.BrOrgQueryCm;
import com.icbc.nt.bus.command.BusCommand;
import com.icbc.nt.bus.command.CountKhjlCm;
import com.icbc.nt.bus.command.F10000001Cm;
import com.icbc.nt.bus.command.F10000002Cm;
import com.icbc.nt.bus.command.F10000053Cm;
import com.icbc.nt.bus.command.F10000054Cm;
import com.icbc.nt.bus.command.F10000056Cm;
import com.icbc.nt.bus.command.F10000069Cm;
import com.icbc.nt.bus.command.F10000070Cm;
import com.icbc.nt.bus.command.F10000071Cm;
import com.icbc.nt.bus.command.F10000072Cm;
import com.icbc.nt.bus.command.F10000080Cm;
import com.icbc.nt.bus.command.F10000081Cm;
import com.icbc.nt.bus.command.F20000002Cm;
import com.icbc.nt.bus.command.F20000004Cm;
import com.icbc.nt.bus.command.F20000006Cm;
import com.icbc.nt.bus.command.F20000007Cm;
import com.icbc.nt.bus.command.F20000008Cm;
import com.icbc.nt.bus.command.F20000012Cm;
import com.icbc.nt.bus.command.F20000014Cm;
import com.icbc.nt.bus.command.JxBzcpCalcCm;
import com.icbc.nt.bus.command.JxBzcpCalcZhCm;
import com.icbc.nt.bus.command.JxDxCalcCm;
import com.icbc.nt.bus.command.JxDxCalcZhCm;
import com.icbc.nt.bus.command.JxFileWriteCm;
import com.icbc.nt.bus.command.JxYwlCalcCm;
import com.icbc.nt.bus.command.JxYwlCalcZhCm;
import com.icbc.nt.bus.command.JxZhtsCalcCm;
import com.icbc.nt.bus.command.JxZhtsCalcZhCm;
import com.icbc.nt.bus.command.JxZqQueryCm;
import com.icbc.nt.bus.command.KhrwCm;
import com.icbc.nt.bus.command.OrgRsQueryCm;
import com.icbc.nt.bus.command.OrgTreeQueryCm;
import com.icbc.nt.bus.command.QzqjCm;
import com.icbc.nt.bus.command.UserJxInitCm;
import com.icbc.nt.bus.command.UserOrgInCm;
import com.icbc.nt.bus.command.UserOrgQueryCm;
import com.icbc.nt.bus.command.WdQueryCm;
import com.icbc.nt.bus.command.ZbFpCm;
import com.icbc.nt.bus.command.ZbInitCm;
import com.icbc.nt.bus.command.ZbSdfpQueryCm;
import com.icbc.nt.bus.command.ZbSyCm;
import com.icbc.nt.bus.command.ZbzbCm;
import com.icbc.nt.bus.command.ZqCurrCm;
import com.icbc.nt.bus.command.ZwcjCm;
import com.icbc.nt.bus.receiver.BrOrgQueryRcv;
import com.icbc.nt.bus.receiver.BusReceiver;
import com.icbc.nt.bus.receiver.JxYwlCalcZhRcv;
import com.icbc.nt.util.TransactionMapData;

public class BusDispatcherImpl extends BusParent implements BusDispatcher{
	@Autowired
	private BusCommand zbQueryCm;
	@Autowired
	private ZbSdfpQueryCm zbSdfpQueryCm;
	@Autowired
	private BusCommand exlUserValidateCm;
	@Autowired
	private BusCommand jxCalcCm;
	@Autowired
	private ZwcjCm zwcjCm;
	@Autowired
	private JxYwlCalcCm jxYwlCalcCm;
	@Autowired
	private JxYwlCalcZhCm jxYwlCalcZhCm;
	@Autowired
	private JxBzcpCalcCm jxBzcpCalcCm;
	@Autowired
	private JxBzcpCalcZhCm jxBzcpCalcZhCm;
	@Autowired
	private JxZhtsCalcCm jxZhtsCalcCm;
	@Autowired
	private JxZhtsCalcZhCm jxZhtsCalcZhCm;
	@Autowired
	private JxDxCalcCm jxDxCalcCm;
	@Autowired
	private JxDxCalcZhCm jxDxCalcZhCm;
	@Autowired
	private WdQueryCm wdQueryCm;
	@Autowired
	private JxFileWriteCm jxFileWriteCm;
	@Autowired
	private UserJxInitCm userJxInitCm;
	@Autowired
	private OrgTreeQueryCm orgTreeQueryCm;
	@Autowired
	private UserOrgQueryCm userOrgQueryCm;
	@Autowired
	private UserOrgInCm userOrgInCm;
	@Autowired
	private JxZqQueryCm jxZqQueryCm;
	@Autowired
	private CountKhjlCm countKhjlCm;
	@Autowired
	private OrgRsQueryCm orgRsQueryCm;
	@Autowired 
	private ZbInitCm zbInitCm;
	@Autowired
	private ZqCurrCm zqCurrCm;
	@Autowired
	private ZbSyCm zbSyCm;
	@Autowired
	private BrOrgQueryCm brOrgQueryCm;
	@Autowired
	private ZbFpCm zbFpCm;
	@Autowired
	private QzqjCm qzqjCm;
	@Autowired
	private KhrwCm khrwCm;
	@Autowired
	private ZbzbCm zbzbCm;
	
	@Autowired
	private F10000001Cm f10000001Cm;
	@Autowired
	private F10000002Cm f10000002Cm;
	@Autowired
	private F10000053Cm f10000053Cm;
	@Autowired
	private F10000054Cm f10000054Cm;
	@Autowired
	private F10000056Cm f10000056Cm;
	@Autowired
	private F10000069Cm f10000069Cm;
	@Autowired
	private F10000070Cm f10000070Cm;
	@Autowired
	private F10000071Cm f10000071Cm;
	@Autowired
	private F10000072Cm f10000072Cm;
	@Autowired
	private F10000080Cm f10000080Cm;
	@Autowired
	private F10000081Cm f10000081Cm;
	@Autowired
	private F20000002Cm f20000002Cm;
	@Autowired
	private F20000004Cm f20000004Cm;
	@Autowired
	private F20000006Cm f20000006Cm;
	@Autowired
	private F20000007Cm f20000007Cm;
	@Autowired
	private F20000008Cm f20000008Cm;
	@Autowired
	private F20000012Cm f20000012Cm;
	@Autowired
	private F20000014Cm f20000014Cm;
	
	/**
	 * 网点柜员可用手动分配总包
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void zbSdfpQuery(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd){
		zbSdfpQueryCm.execute(ja, condition, retMap, tmd);
	}
	
	/**
	 * 考核任务管理
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void khrwManage(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap){
		khrwCm.execute(ja, condition, retMap);
	}
	
	/**
	 * 总包占比管理
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void zbzbManage(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd){
		zbzbCm.execute(ja, condition, retMap);
	}
	
	/**
	 * 权重区间管理
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void qzqjManage(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd){
		qzqjCm.execute(ja, condition, retMap, tmd);
	}
	
	/**
	 * 总包分配
	 * @param tmd
	 */
	public void zbFpCalc(TransactionMapData tmd){
		zbFpCm.execute(tmd);
	}
	/**
	 * 机构所在分支行号查询
	 */
	public String  brOrgQuery(String orgId){
		return brOrgQueryCm.execute(orgId);
	}
	/**
	 * 剩余总包计算
	 */
	public void zbSyCalc(TransactionMapData tmd){
		zbSyCm.execute(tmd);
	}
	
	/**
	 * 获取当前考核周期
	 * @return
	 */
	public String zqCurr(){
		return zqCurrCm.execute();
	}
	/**
	 * 初始化机构总包人均表t_ntmisc_orgzbrj中未初始化的总包相关部分:不考虑MOVA挂钩的网点人均总包、不参与考核人员总包、参与考核人员总包、纳入MOVA考核部分抢回总包、考虑mova后新网点总包、参与考核人员新总包、参与考核人均总包
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void zbInitZbrj(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd){
		zbInitCm.zbInitZbrj(ja, condition, retMap, tmd);
	} 
	/**
	 * 初始化机构总包人数表t_ntmisc_orgzbrs中总包相关部分：网点对应绩效考核岗位的绩效总包、网点对应绩效考核岗位的保留总包、网点对应的绩效考核岗位的当期可分配总包
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void zbInitZbrs(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd){
		zbInitCm.zbInitZbrs(ja, condition, retMap, tmd);
	} 
	
	
	/**
	 * 网点各绩效考核岗位对应人数：机构号、绩效岗位编号、机构总人数、机构总人数转换、机构参与考核总人数、机构参与考核总人数转换
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void empNumJbjx(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd){
		orgRsQueryCm.empNumJbjx(ja, condition, retMap, tmd);
	} 
	
	/**
	 * 网点总人数：机构id、机构下属总人数、机构下属总人数转换、机构下属参与考核人数、机构下属不参与考核人数、机构下属参与考核人数转换
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void empNumOrg(JSONArray ja, LinkedHashMap<String, Object> condition,
			Map retMap, TransactionMapData tmd){
		orgRsQueryCm.empNumOrg(ja, condition, retMap, tmd);
	} 
	
	/**
	 * 机构部门初始化
	 */
	public void f10000001(TransactionMapData tmd){
		f10000001Cm.execute(tmd);
	}
	/**
	 * 用户初始化
	 */
	public void f10000002(TransactionMapData tmd){
		f10000002Cm.execute(tmd);
	}
	
	/**
	 * 历史绩效查询
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void f10000080(JSONArray ja, LinkedHashMap<String, Object> condition,Map retMap, TransactionMapData tmd){
		f10000080Cm.execute(ja, condition, retMap, tmd);
	}
	/**
	 * 当期绩效导出
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void f10000072(JSONArray ja, LinkedHashMap<String, Object> condition,Map retMap, TransactionMapData tmd){
		f10000072Cm.execute(ja, condition, retMap, tmd);
	}
	/**
	 * 当期绩效查询
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void f10000071(JSONArray ja, LinkedHashMap<String, Object> condition,Map retMap, TransactionMapData tmd){
		f10000071Cm.execute(ja, condition, retMap, tmd);
	}
	/**
	 * 人员分配比例导入结果查询
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void f10000069(JSONArray ja, LinkedHashMap<String, Object> condition,Map retMap, TransactionMapData tmd){
		f10000069Cm.execute(ja, condition, retMap, tmd);
	}
	/**
	 * 客户经理数目查询
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 * @return
	 */
	public int countKhjl(TransactionMapData tmd){
		return countKhjlCm.execute(tmd);
	}
	
	
	/**
	 * 绩效周期查询（周期下拉框）
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void jxZqQuery(JSONArray ja, LinkedHashMap<String, Object> condition,Map retMap, TransactionMapData tmd){
		jxZqQueryCm.execute(ja, condition, retMap, tmd);
	}
	
	/**
	 * 用户所有可见机构查询
	 * @param tmd
	 * @return 用户所有可见机构号，格式“(机构号1、机构号2、机构号3.....)”
	 */
	public String userOrgIn(TransactionMapData tmd){
//		userOrgInCm.execute(ja, condition, retMap, tmd);
		userOrgInCm.execute(tmd);//2015-12-11，去掉无用形参：ja, condition, retMap
		return tmd.get("orgIn").toString();
	}
	
	/**
	 * 用户机构查询
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 * @return 用户所在机构号
	 */
	public String userOrgQuery(TransactionMapData tmd){
//		userOrgQueryCm.execute(ja, condition, retMap, tmd);
		userOrgQueryCm.execute(tmd);
		return tmd.get("orgIdCurr").toString();
	}
	
	/**
	 * 机构树查询命令
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void orgTreeQuery(JSONArray ja, LinkedHashMap<String, Object> condition,Map retMap, TransactionMapData tmd){
		orgTreeQueryCm.execute(ja, condition, retMap, tmd);
	}
	
	/**
	 * 考核周期查询
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	/*public String khZqCurr(){
		return khrwCm.execute();
	}*/
	
	/**
	 * 用户绩效表初始化
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void userJxInit(JSONArray ja, LinkedHashMap<String, Object> condition,Map retMap, TransactionMapData tmd){
		userJxInitCm.execute(ja, condition, retMap, tmd);
	}
	
	/**
	 * 生成绩效文件
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void jxFileWrite(JSONArray ja, LinkedHashMap<String, Object> condition,Map retMap, TransactionMapData tmd){
		jxFileWriteCm.execute(ja, condition, retMap, tmd);
	}
	
	/**
	 * 机构下属所有网点查询
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void wdQuery(JSONArray ja, LinkedHashMap<String, Object> condition,Map retMap, TransactionMapData tmd){
		wdQueryCm.execute(ja, condition, retMap, tmd);
	}
	
	/**
	 * 定性绩效计算（网点考核）
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void jxDxCalc(JSONArray ja, LinkedHashMap<String, Object> condition,Map retMap, TransactionMapData tmd){
		jxDxCalcCm.execute(ja, condition, retMap, tmd);
	}
	/**
	 * 定性绩效计算（支行考核）
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void jxDxCalcZh(JSONArray ja, LinkedHashMap<String, Object> condition,Map retMap, TransactionMapData tmd){
		jxDxCalcZhCm.execute(ja, condition, retMap, tmd);
	}
	/**
	 * 支行特色业务绩效计算（网点考核）
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void jxZhtsCalc(JSONArray ja, LinkedHashMap<String, Object> condition,Map retMap, TransactionMapData tmd){
		jxZhtsCalcCm.execute(ja, condition, retMap, tmd);
	}
	/**
	 * 支行特色业务绩效计算(分支行考核)
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void jxZhtsZhCalc(JSONArray ja, LinkedHashMap<String, Object> condition,Map retMap, TransactionMapData tmd){
		jxZhtsCalcZhCm.execute(ja, condition, retMap, tmd);
	}
	
	/**
	 * 标准产品绩效计算(支行)
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void jxBzcpZhCalc(JSONArray ja, LinkedHashMap<String, Object> condition,Map retMap, TransactionMapData tmd){
		jxBzcpCalcZhCm.execute(ja, condition, retMap, tmd);
	}
	
	/**
	 * 标准产品绩效计算(网点)
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void jxBzcpCalc(JSONArray ja, LinkedHashMap<String, Object> condition,Map retMap, TransactionMapData tmd){
		jxBzcpCalcCm.execute(ja, condition, retMap, tmd);
	}
	
	/**
	 * 业务量绩效计算（网点考核部分）
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void jxYwlCalc(JSONArray ja, LinkedHashMap<String, Object> condition,Map retMap, TransactionMapData tmd){
		jxYwlCalcCm.execute(ja, condition, retMap, tmd);
	}
	/**
	 * 业务量绩效计算（支行考核部分）
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void jxYwlCalcZh(JSONArray ja, LinkedHashMap<String, Object> condition,Map retMap, TransactionMapData tmd){
		jxYwlCalcZhCm.execute(ja, condition, retMap, tmd);
	}
	
	/**
	 * 职务层级管理
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void zwcj(JSONArray ja, LinkedHashMap<String, Object> condition,Map retMap, TransactionMapData tmd){
		zwcjCm.execute(ja, condition, retMap, tmd);
	}
	
	/**
	 * 绩效计算
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void jxCalc(JSONArray ja, LinkedHashMap<String, Object> condition,Map retMap, TransactionMapData tmd){
		jxCalcCm.execute(ja, condition, retMap, tmd);
	}
	/**
	 * excel用户唯一合法性验证
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	@Override
	public void exlUserValidate(JSONArray ja, LinkedHashMap<String, Object> condition,Map retMap, TransactionMapData tmd) {
		exlUserValidateCm.execute(ja, condition, retMap, tmd);
	}
	
	/**
	 * 机构当期总包查询
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	@Override
	public void zbQuery(JSONArray ja, LinkedHashMap<String, Object> condition,Map retMap, TransactionMapData tmd) {
		zbQueryCm.execute(ja, condition, retMap, tmd);
	}
	
	/**
	 * MOVA机构得分导入结果查询
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void f10000056(JSONArray ja, LinkedHashMap<String, Object> condition,Map retMap, TransactionMapData tmd){
		f10000056Cm.execute(ja, condition, retMap, tmd);
	}
	/**
	 * 机构分配数据导入结果查询
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void f10000053(JSONArray ja, LinkedHashMap<String, Object> condition,Map retMap, TransactionMapData tmd){
		f10000053Cm.execute(ja, condition, retMap, tmd);
	}
	/**
	 * 机构分配结果查询
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void f10000054(JSONArray ja, LinkedHashMap<String, Object> condition,Map retMap, TransactionMapData tmd){
		f10000054Cm.execute(ja, condition, retMap, tmd);
	}
	/**
	 * 绩效计算
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void f10000070(JSONArray ja, LinkedHashMap<String, Object> condition,Map retMap, TransactionMapData tmd){
		f10000070Cm.execute(ja, condition, retMap, tmd);
	}
	/**
	 * 历史绩效导出
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void f10000081(JSONArray ja, LinkedHashMap<String, Object> condition,Map retMap, TransactionMapData tmd){
		f10000081Cm.execute(ja, condition, retMap, tmd);
	}
	
	/**
	 * 员工业绩导入结果查询
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void f20000002(JSONArray ja, LinkedHashMap<String, Object> condition,Map retMap, TransactionMapData tmd){
		f20000002Cm.execute(ja, condition, retMap);
	}
	
	/**
	 * 分支行考核部分总包导入结果查询
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void f20000004(JSONArray ja, LinkedHashMap<String, Object> condition,Map retMap, TransactionMapData tmd){
		f20000004Cm.execute(ja, condition, retMap, tmd);
	}
	
	/**
	 * 网点考核部分总包导入结果查询
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void f20000006(JSONArray ja, LinkedHashMap<String, Object> condition,Map retMap, TransactionMapData tmd){
		f20000006Cm.execute(ja, condition, retMap);
	}
	
	/**
	 * 绩效计算（新版）
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void f20000007(JSONArray ja, LinkedHashMap<String, Object> condition,Map retMap, TransactionMapData tmd){
		f20000007Cm.execute(ja, condition, retMap, tmd);
	}
	/**
	 * 绩效查询（新版）
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void f20000008(JSONArray ja, LinkedHashMap<String, Object> condition,Map retMap, TransactionMapData tmd){
		f20000008Cm.execute(ja, condition, retMap, tmd);
	}
	/**
	 * 历史绩效导出（新版）
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void f20000012(TransactionMapData tmd){
		f20000012Cm.execute(tmd);
	}
	
	/**
	 * 网点手动分配绩效导入查询
	 * @param ja
	 * @param condition
	 * @param retMap
	 * @param tmd
	 */
	public void f20000014(JSONArray ja, LinkedHashMap<String, Object> condition,Map retMap, TransactionMapData tmd){
		f20000014Cm.execute(ja, condition, retMap, tmd);
	}
	
	
}
