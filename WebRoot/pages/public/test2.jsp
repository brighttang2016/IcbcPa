<%@page import="java.net.URLEncoder"%>
<%@page import="org.springframework.beans.propertyeditors.URIEditor"%>
<%@page import="java.net.URI"%>
<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://www.extremecomponents.org" prefix="ec" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <link rel="stylesheet" type="text/css" href="styles/extremecomponents.css">
	<link rel="stylesheet" type="text/css" href="styles/ntstyle.css">
    <link href="styles/bootstrap.css" rel="stylesheet">
     <link href="styles/bootstrap.min.css" rel="stylesheet" media="screen">
    <link href="styles/bootstrap-datetimepicker.min.css" rel="stylesheet" media="screen">
</head>
<style>
		 .tab { width:90%;margin:auto;}
		 .tab_menu { clear:both;}
		 .tab_menu li { float:left; text-align:center; cursor:pointer; list-style:none; padding:1px 6px; margin-right:4px; background:#F1F1F1; border:1px solid #898989; border-bottom:none;}
		 .tab_menu li.hover { background:#DFDFDF;}
		 .tab_menu li.selected { color:#FFF; background:#6D84B4;}
		 .tab_box { clear:both; border:1px solid #D9D9D9;}
		 .hide{display:none}
		 .formLabel2{width:120px}
		 .formInput{width:150px;height:30px}
  </style>
  
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

<script type="text/javascript" src="js/slide.js"></script>
<script type="text/javascript" src="<%=basePath%>dwr/engine.js"></script>
<script type="text/javascript" src="<%=basePath%>dwr/util.js"></script>
<script type="text/javascript" src="<%=basePath%>dwr/interface/remoteCtrl.js"></script>
<script type="text/javascript" src="js/jquery-1.9.0.js"></script>
<script type="text/javascript" src="js/bootstrap.min.js"></script>
<script type="text/javascript" src="js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="js/bootstrap-datetimepicker.zh-CN.js"></script>

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
		$("#queryForm").submit();
	}
</script>

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

<body class="frameBody">
<div class="form-group">
    <label for="dtp_input2" class="col-md-2 control-label">Date Picking</label>
    <div class="input-group date form_date col-md-5" data-date="" data-date-format="yyyy-MM-dd" data-link-field="dtp_input2" data-link-format="yyyy-mm-dd">
        <input class="form-control" size="16" type="text" value="" readonly>
        <span class="input-group-addon"><span class="glyphicon glyphicon-remove"></span></span>
        <span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
    </div>
    <input type="hidden" id="dtp_input2" value="" /><br/>
</div>


<div class="container">
    <form action="" class="form-horizontal"  role="form">
        <fieldset>
            <legend>Test</legend>
            <div class="form-group">
                <label for="dtp_input1" class="col-md-2 control-label">DateTime Picking</label>
                <div class="input-group date form_datetime col-md-5" data-date="1979-09-16T05:25:07Z" data-date-format="dd MM yyyy - HH:ii p" data-link-field="dtp_input1">
                    <input class="form-control" size="16" type="text" value="" readonly>
                    <span class="input-group-addon"><span class="glyphicon glyphicon-remove"></span></span>
					<span class="input-group-addon"><span class="glyphicon glyphicon-th"></span></span>
                </div>
				<input type="hidden" id="dtp_input1" value="" /><br/>
            </div>
			<div class="form-group">
                <label for="dtp_input2" class="col-md-2 control-label">Date Picking</label>
                <div class="input-group date form_date col-md-5" data-date="" data-date-format="yyyy-MM-dd" data-link-field="dtp_input2" data-link-format="yyyy-mm-dd">
                    <input class="form-control" size="16" type="text" value="" readonly>
                    <span class="input-group-addon"><span class="glyphicon glyphicon-remove"></span></span>
					<span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
                </div>
				<input type="hidden" id="dtp_input2" value="" /><br/>
            </div>
			<div class="form-group">
                <label for="dtp_input3" class="col-md-2 control-label">Time Picking</label>
                <div class="input-group date form_time col-md-5" data-date="" data-date-format="hh:ii" data-link-field="dtp_input3" data-link-format="hh:ii">
                    <input class="form-control" size="16" type="text" value="" readonly>
                    <span class="input-group-addon"><span class="glyphicon glyphicon-remove"></span></span>
					<span class="input-group-addon"><span class="glyphicon glyphicon-time"></span></span>
                </div>
				<input type="hidden" id="dtp_input3" value="" /><br/>
            </div>
        </fieldset>
    </form>
</div>

<div style="border:0px solid green;margin:auto;">
    &nbsp;&nbsp;
    <div id="updateMsg" style="display:inline;position:absolute">update result</div>
</div>
<!-- 查询条件 -->
<div id="queryDiv" style="border:1px solid #EFEFEF;width:600px;margin:auto" align=center>
	<form class="form-inline" id="queryForm" action="User_applyInfoQuery.action" method="post">
		<table border="0" cellspacing="1" cellpadding="5">
			<tr>
				<td>
					 <div class="form-group" style="border:0px solid red;background-color:#3A95C2;height:30px">
					    <label class="col-sm-2 control-label formLabel formLabel2" for="exampleInputEmail2" >机构:</label>
					  </div>
					  <div class="form-group">
					    <select id="orgId" name="orgBean.orgId" class="formInput"></select>
					  </div>
				</td>
				<td>
					<div class="form-group" style="border:0px solid red;background-color:#3A95C2;height:30px">
					    <label class="col-sm-2 control-label formLabel formLabel2" for="exampleInputEmail2">类型:</label>
					  </div>
					  <div class="form-group">
					  	<select id="consbType" name="orgBean.orgId" class="formInput"></select>
					  </div>
				</td>
			</tr>
			<tr>
				<td>
					<div class="form-group" style="border:0px solid red;background-color:#3A95C2;height:30px">
					    <label class="col-sm-2 control-label formLabel formLabel2" for="exampleInputEmail2">开始时间:</label>
					</div>
					<div class="form-group">
					  	<input class="form-control formInput" type="text" id="userId" name="applyBean.userId"/>
					</div>
				</td>
				<td>
					<div class="form-group" style="border:0px solid red;background-color:#3A95C2;height:30px">
					    <label class="col-sm-2 control-label formLabel formLabel2" for="exampleInputEmail2">结束时间:</label>
					</div>
					<div class="form-group">
					  	<input class="form-control formInput" type="text" id="userId" name="applyBean.userId" />
					</div>
				</td>
			</tr>
		</table>
	</form>
	<button  class="btn btn-default" onclick="query()" style="height:30px">查询</button>
</div>




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

<!--审批Modal -->
<div class="modal fade" id="checkModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true" onclick='$("#ldDescDiv").css("display","none");'>&times;</button>
        <h4 class="modal-title" id="myModalLabel">申请审批</h4>
      </div>
    	<div class="modal-body">
			<form class="form-horizontal" role="form" id="formCheck" action="User_applyCheck" method="post">
			<div style="display:none;"><input type="text" name="updTag" value="2"/></div>
			<div>
				<strong>申请理由</strong><hr/>
				<small id="userDesc">申请理由</small>
			</div>
			<div style="display:none"><input type="text" id="applyId" value="${pageScope.pres.APPLY_ID}" name="applyBean.applyId"/></div>
			<div style="display:none"><input type="text" id="userID" value="${pageScope.pres.USER_ID}" name="applyBean.userId"/></div>
			<div style="display:none"><input type="text" id="ldId" value="<%=accountId %>" name="applyBean.ldId"/></div>
			<div style="display:none"><input type="text" id="ldState" name="applyBean.ldState"/></div>
			<div style="display:none;border:0px solid red" id="ldDescDiv">
				<strong style="margin-left:0px;margin-bottom:4px">请填写拒绝理由</strong>
				<div><input type="text" class="form-control" id="ldDesc" name="applyBean.ldDesc"></div>
				<div id="notice" style="display:none">请填写拒绝理由</div>
			</div>
			</form>
		</div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary" onclick="agree()">同意</button>
        <button type="button" class="btn btn-default btn-danger" onclick="reject()">拒绝</button>
      </div>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->









</body>
</html>
