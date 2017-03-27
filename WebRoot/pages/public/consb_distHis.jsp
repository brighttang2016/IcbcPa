<%@page import="java.net.URLEncoder"%>
<%@page import="org.springframework.beans.propertyeditors.URIEditor"%>
<%@page import="java.net.URI"%>
<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://www.extremecomponents.org" prefix="ec" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML>
<html>
  <head>
   <base href="<%=basePath%>">
    <title>耗材分发查询</title>

	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<link rel="stylesheet" type="text/css" href="<%=basePath%>styles/extremecomponents.css">
	<link rel="stylesheet" type="text/css" href="<%=basePath%>styles/ntstyle.css">
	
	<link href="<%=basePath%>styles/bootstrap.css" rel="stylesheet">
     <link href="<%=basePath%>styles/bootstrap.min.css" rel="stylesheet" media="screen">
    <link href="<%=basePath%>styles/bootstrap-datetimepicker.min.css" rel="stylesheet" media="screen">
    
  <style>
		 .tab { width:90%;margin:auto;}
		 .tab_menu { clear:both;}
		 .tab_menu li { float:left; text-align:center; cursor:pointer; list-style:none; padding:1px 6px; margin-right:4px; background:#F1F1F1; border:1px solid #898989; border-bottom:none;}
		 .tab_menu li.hover { background:#DFDFDF;}
		 .tab_menu li.selected { color:#FFF; background:#6D84B4;}
		 .tab_box { clear:both; border:1px solid #D9D9D9;}
		 .hide{display:none}
		 .formLabel2{width:120px;color:white;padding:4px 10px}
		.formInput{width:180px;height:30px}
  </style>
  </head>

<%
	HttpSession ss = request.getSession();
	String accountId = "";
	String updateMsg = "";
	String handleCount = "";
	try{
		accountId = (String) ss.getAttribute("accountId");
	}catch(Exception e){
	}
	try{
		updateMsg = (String) ss.getAttribute("updateMsg");
	}catch(Exception e){
	}
	try{
		handleCount = (String)ss.getAttribute("handleCount");
	}catch(Exception e){
	}
	
	
%>
<script type="text/javascript" src="js/nantian.js"></script>
<script type="text/javascript" src="js/slide.js"></script>
<script type="text/javascript" src="<%=basePath%>dwr/engine.js"></script>
<script type="text/javascript" src="<%=basePath%>dwr/util.js"></script>
<script type="text/javascript" src="<%=basePath%>dwr/interface/remoteCtrl.js"></script>
  
  <script type="text/javascript" src="<%=basePath%>js/jquery-1.9.0.js"></script>
<script type="text/javascript" src="<%=basePath%>js/bootstrap.min.js"></script>
<script type="text/javascript" >

	function handle(applyId){
		//alert(applyId+"  "+applyDesc);
		//alert(applyId);
		//parent.document.getElementById("consbFrame").contentWindow.location.href = "User_orderEdit?applyBean.applyId="+applyId;
		findframe("welcome","User_prepareOrder.action?applyBean.applyId="+applyId);
	}
	function agree(){
		$("#ldDesc").val("");
		$("#ldState").val("1");
		//alert($("#ld_id").val());
		$("#formCheck").submit();
	}
	function reject(){
		$("#ldState").val("0");
		$("#ldDescDiv").css("display","block");
		if($("#ldDesc").val() != ""){
			$("#formCheck").submit();
		}
	}
	function check(applyId,applyTime,userId,applyDesc){
		$("#userDesc").html(applyDesc);
		$('#checkModal').modal();
	}
	//所有类型
	function consbTypeQuery(){
  		remoteCtrl.consbTypeQuery(funRet);
		function funRet(data){
			var option="";
			for(var i = 0; i < data.length; i++){
				option+="<option value="+data[i].TYPE_ID+">"+data[i].TYPE_NAME+"</option>";
			}
			$("#consbType").html("<option value='all'>全部类型</option>"+option);
		}
  	}
	  //所有机构
  	function consbOrgQuery(){
  		remoteCtrl.consbOrgQuery(funRet);
  		function funRet(data){
  			var option = "<option value='all'>所有机构</option>";
  			for(var i = 0; i < data.length; i++){
  				option+="<option value="+data[i].ORG_ID+">"+data[i].ORG_NAME+"</option>";
  			}
  			$("#orgId").html(option);
  			consbTypeQuery();
  		}
  	}
	$(function(){
		//parent.$('#handleCount').html('<label class="badge"><%=handleCount%></label>');
		consbOrgQuery();
		$("#updateMsg").html("<%=updateMsg%>");
		setTimeout(function() {$("#updateMsg").html("");}, 3000);
		<%
		ss.setAttribute("updateMsg", "");
		%>
	});
	function query(){
		showMask();
		//alert($("#orgId").val());
		$("#queryForm").submit();
	}
</script>



  <body class="frameBody">
  <div style="border:0px solid green;margin:auto;">
    &nbsp;&nbsp;
    <div id="updateMsg" style="display:inline;position:absolute"></div>
</div>
<!-- 查询条件开始 -->
<div id="queryDiv" style="border:1px solid #EFEFEF;margin:auto;width:650px" align=center>
	<form class="form-inline" id="queryForm" action="Order_distHisQuery.action" method="post">
		<table border="0" cellspacing="1" cellpadding="5">
			<tr>
				<td>
					 <div class="form-group" style="border:0px solid red;background-color:#3A95C2;height:30px">
					    <label class="col-sm-2 control-label formLabel formLabel2" for="exampleInputEmail2" >机构:</label>
					  </div>
				</td>
				<td>
					  <div class="form-group">
					    <select id="orgId" name="orgBean.orgId" class="formInput"></select>
					  </div>
				</td>
				<td>
					<div class="form-group" style="border:0px solid red;background-color:#3A95C2;height:30px">
					    <label class="col-sm-2 control-label formLabel formLabel2" for="exampleInputEmail2">类型:</label>
					  </div>
				</td>
				<td>
					  <div class="form-group">
					  	<select id="consbType" name="applyBean.typeId" class="formInput"></select>
					  </div>
				</td>
			</tr>
			<tr>
				<td>
					<div class="form-group" style="border:0px solid red;background-color:#3A95C2;height:30px">
					    <label class="col-sm-2 control-label formLabel formLabel2" for="exampleInputEmail2">开始时间:</label>
					</div>
				</td>
				<td>
					<div class="form-group">
						<div class="form-group" style="border:0px solid red;width:180px">
			                <div class="input-group date form_date col-md-12" style="border:0px solid red;" data-date="" data-date-format="yyyy-MM-dd" data-link-field="dtp_input2" data-link-format="yyyy-mm-dd">
			                    <input class="form-control" name="timeBegin" size="16" type="text" value="" readonly>
			                    <span class="input-group-addon"><span class="glyphicon glyphicon-remove"></span></span>
								<span class="input-group-addon"><span class="glyphicon glyphicon-calendar" ></span></span>
			                </div>
	               		</div>
					 </div>
				</td>
				<td>
					<div class="form-group" style="border:0px solid red;background-color:#3A95C2;height:30px">
					    <label class="col-sm-2 control-label formLabel formLabel2" for="exampleInputEmail2">结束时间:</label>
					</div>
				</td>
				<td>
					<div class="form-group">
					  	<div class="form-group" style="border:0px solid red;width:180px">
			                <div class="input-group date form_date col-md-12" style="border:0px solid red;" data-date="" data-date-format="yyyy-MM-dd" data-link-field="dtp_input2" data-link-format="yyyy-mm-dd">
			                    <input class="form-control" name="timeEnd"  size="16" type="text" value="" readonly>
			                    <span class="input-group-addon"><span class="glyphicon glyphicon-remove"></span></span>
								<span class="input-group-addon"><span class="glyphicon glyphicon-calendar" ></span></span>
			                </div>
	               		</div>
					</div>
				</td>
			</tr>
		</table>
	</form>
	<button  class="btn btn-default" onclick="query()" style="height:30px">查询</button>
</div>
<!-- 查询条件结束 -->


<div class="tableclass" align="center">
    <ec:table 
		items="distHisQuery"
		action="${pageContext.request.contextPath}/Order_distHisQuery.action"
		imagePath="${pageContext.request.contextPath}/images/table/*.gif"
		title="耗材分发历史查询"
		rowsDisplayed="10"
		filterable="false"
		style="margin-top:3px"
		var="pres"
		>
		<ec:row>
			<ec:column property="ORG_NAME" title="机构" style="width:180px"></ec:column>
			<ec:column property="USER_ID" title="申请人" style="width:100px"></ec:column>
			<ec:column property="KG_TIME" title="领取时间" style="width:200px"></ec:column>
			<ec:column property="TYPE_NAME" title="种类"></ec:column>
			<ec:column property="CONSB_NAME" title="名称" style="width:200px"></ec:column>
			<ec:column property="GDS_DISTNUM" title="领取数量" style="width:100px"></ec:column>
		</ec:row>
	</ec:table>
</div>

  </body>
  <script type="text/javascript" src="<%=basePath%>js/bootstrap-datetimepicker.js" charset="utf-8"></script>
<script type="text/javascript" src="<%=basePath%>js/bootstrap-datetimepicker.zh-CN.js" charset="utf-8"></script>

<script type="text/javascript">
    $('.form_datetime').datetimepicker({
        //language:  'fr',
        weekStart: 1,
        todayBtn:  1,
		autoclose: 1,
		todayHighlight: 1,
		startView: 2,
		forceParse: 0,
        showMeridian: 1
    });
	$('.form_date').datetimepicker({
        language:  'zh-CN',
        weekStart: 1,
        todayBtn:  1,
		autoclose: 1,
		todayHighlight: 1,
		startView: 2,
		minView: 2,
		forceParse: 0
    });
	$('.form_time').datetimepicker({
        language:  'zh-CN',
        weekStart: 1,
        todayBtn:  1,
		autoclose: 1,
		todayHighlight: 1,
		startView: 1,
		minView: 0,
		maxView: 1,
		forceParse: 0
    });
</script>
</html>
