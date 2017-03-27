<%@page import="com.alibaba.fastjson.JSONObject"%>
<%@page import="com.alibaba.fastjson.JSON"%>
<%@page import="com.alibaba.fastjson.JSONArray"%>
<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>申请单</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
<style>
	.tdClass{
		width:175px;
		background-color:#FFFFFF;
		padding:5px;
		font-size:10px;
	}
	.tdClass2{
		width:100px;
		background-color:#FFFFFF;
		padding:5px;
		font-size:10px;
	}
	.tdTitle{
		background-color:#FFFFFF;
		font-size:16px;
		font-weight:600;
		padding:5px;
	}
	table{
		background-color:#000000;
	}
	.divTitle{
		font-size:16px;
		font-weight:600;
	}
</style>

  </head>
    <%
    JSONArray jaApplyInfo = (JSONArray)session.getAttribute("jaApplyInfo");
    JSONObject applyInfo = (JSONObject)jaApplyInfo.get(0);
    JSONArray jaConsbSave = (JSONArray)session.getAttribute("jaConsbSave");
    
  %>
  <script type="text/javascript" src="js/ext/ext-all.js"></script>
  <script type="text/javascript" src="js/ext/ext-lang-zh_CN.js"></script>
  <script type="text/javascript" src="<%=basePath%>js/common_fun.js"></script>
  <body>
  <OBJECT   id=WebBrowser   classid=CLSID:8856F961-340A-11D0-A96B-00C04FD705A2   height=0   width=0></OBJECT>
  <input type="button" value="打印" id="print" onclick="applyPrint()"/><hr/>
  <div style="width:700;border:0px solid red;margin:auto">
  	  <div style="width:700;font-size:25px;margin:10 0 10 0;font-weight:800" align="center">申请单(打印)</div>
	  <div class="divTitle">订单基本信息:</div>
	  <div>
		  <table border=0 cellspacing=1 cellpadding=1 >
		  <tr>
		  	<td class="tdClass">申请单号：</td><td class="tdClass"><%=applyInfo.get("apply_id") %></td>
		  	<td class="tdClass">申请人：</td><td class="tdClass"><%=applyInfo.get("user_id") %></td>
		  </tr>
		  <tr>
		  	<td class="tdClass">姓名：</td><td class="tdClass"><%=applyInfo.get("user_name") %></td>
		  	<td class="tdClass">所属部门：</td><td class="tdClass"><%=applyInfo.get("org_name") %></td>
		  </tr>
		  <tr>
		  	<td class="tdClass">申请时间：</td><td class="tdClass"><%=applyInfo.get("apply_time") %></td>
		  	<td class="tdClass">领用截止时间：</td><td class="tdClass"><%=applyInfo.get("dl_time")==null?"无":applyInfo.get("dl_time") %></td>
		  </tr>
		  <tr>
		  	<td class="tdClass">申请理由:</td><td colspan=3 class="tdClass"><%=applyInfo.get("apply_desc") %></td>
		  </tr>
		  <tr>
		  	<td class="tdClass">零星申请理由:</td><td colspan=3 class="tdClass"><%=(applyInfo.get("temp_desc") == null?"无":applyInfo.get("temp_desc"))%></td>
		  </tr>
		  </table>
	  </div><br/>
	  <div class="divTitle">订单耗材信息:</div>
	  <div>
	  <table border=0 cellspacing=1 cellpadding=1 >
	  	<tr><td class="tdTitle">物品编号</td><td class="tdTitle">物品名称</td><td class="tdTitle">型号</td><td class="tdTitle">大类</td><td class="tdTitle">小类</td><td class="tdTitle">单位</td><td class="tdTitle">单价</td><td class="tdTitle">申请数量</td><td class="tdTitle">金额合计</td></tr>
	  	<%
	  	float smTotalPrice = 0;//总价
	  	float summaryNum = 0;//总量
	  		for(int i = 0;i < jaConsbSave.size();i++){
	  			float smRowPrice = 0;//一行总价合计
	  			JSONObject consbSaveRow = (JSONObject)jaConsbSave.get(i);
	  			smRowPrice = Float.parseFloat(consbSaveRow.get("consb_price").toString()) * Float.parseFloat(consbSaveRow.get("gds_num").toString());
	  			smTotalPrice += smRowPrice;
	  			summaryNum += Float.parseFloat(consbSaveRow.get("gds_num").toString());
	  			%>
	  				<tr>
	  					<td td class="tdClass2"><%=consbSaveRow.get("consb_code") %></td>
	  					<td td class="tdClass2"><%=consbSaveRow.get("consb_name") %></td>
	  					<td td class="tdClass2"><%=consbSaveRow.get("consb_mn") %></td>
	  					<td td class="tdClass2"><%=consbSaveRow.get("type_namep") %></td>
	  					<td td class="tdClass2"><%=consbSaveRow.get("type_name") %></td>
	  					<td td class="tdClass2"><%=consbSaveRow.get("consb_unit") %></td>
	  					<td td class="tdClass2"><%=consbSaveRow.get("consb_price") %></td>
	  					<td td class="tdClass2"><%=consbSaveRow.get("gds_num") %></td>
	  					<td td class="tdClass2"><%=smRowPrice %></td>
	  				</tr>
	  			<%
	  		}
	  	%>
	  	<tr><td colspan="7" class="tdClass2">总计：</td><td class="tdClass2">总数量:<%=summaryNum%></td><td class="tdClass2">总金额:<%=smTotalPrice %></td></tr>
	  </table>
	  </div><br/>
	  
	  <div class="divTitle">部门领导审批结果:</div>
	  <div>
		  <table border=0 cellspacing=1 cellpadding=1  >
		  	<tr><td class="tdClass">领导id:</td><td class="tdClass"><%=applyInfo.get("cs_id") %></td><td class="tdClass">姓名:</td><td class="tdClass"><%=applyInfo.get("cs_name") %></td></tr>
		  	<tr>
		  		<td class="tdClass">审批时间:</td><td class="tdClass"><%=applyInfo.get("cs_time") %></td><td class="tdClass">审批意见:</td><td class="tdClass" class="tdClass"><%=applyInfo.get("cs_desc") %></td>
		  	</tr>
		  	<tr>
		  		<td class="tdClass">审批结果:</td><td colspan=3 class="tdClass"><%=applyInfo.get("cs_state").equals("1")?"审批通过":"审批未通过" %></td>
		  	</tr>
		  </table>
	  </div><br/>
	  
	  <div class="divTitle">主办处理结果:</div>
	  <div>
		  <table border=0 cellspacing=1 cellpadding=1  >
		  	<tr><td class="tdClass">主办id:</td><td class="tdClass"><%=applyInfo.get("zb_id") %></td><td class="tdClass">姓名</td><td class="tdClass"><%=applyInfo.get("zb_name") %></td></tr>
		  	
		  	<tr>
		  		<td class="tdClass">处理时间:</td><td class="tdClass"><%=applyInfo.get("zb_time") %></td><td class="tdClass">处理意见:</td><td class="tdClass" class="tdClass"><%=applyInfo.get("zb_desc") == null?"无":applyInfo.get("zb_desc") %></td>
		  	</tr>
		  </table>
	  </div>
	  <div style="width:600px;margin-top:20px" align="right">
	  	<div style="margin-bottom:10px">申请人:</div>
	  	<div>日&nbsp;&nbsp;&nbsp;&nbsp;期:</div>
	  </div>
  </div>
    
  </body>
</html>
