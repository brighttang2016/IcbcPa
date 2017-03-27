<%@page import="java.net.URLEncoder"%>
<%@page import="org.springframework.beans.propertyeditors.URIEditor"%>
<%@page import="java.net.URI"%>
<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://www.extremecomponents.org" prefix="ec" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<%
	HttpSession ss = request.getSession();
	String updateMsg = (String) ss.getAttribute("updateMsg");
	String accountId = (String)ss.getAttribute("accountId");
	List<Map<String,String>> userInfo = (ArrayList<Map<String,String>>)ss.getAttribute("userInfo");
	String orgName = userInfo.get(0).get("ORG_NAME").toString();
	List<Map<String,String>> orderDetail = (ArrayList<Map<String,String>>)ss.getAttribute("orderDetail");
	//System.out.println("orderDetail.size():"+orderDetail.size());
	Map<String,String> orderMap = orderDetail.get(0);
	//System.out.println("orderMap:"+orderMap);
	String applyId = orderMap.get("APPLY_ID");
	List<Map<String,String>> orderAcs = (ArrayList<Map<String,String>>)ss.getAttribute("orderAcs");
	String userId = orderDetail.get(0).get("USER_ID");
	List<Map<String,String>> orderGoodsInfo = (ArrayList<Map<String,String>>)ss.getAttribute("orderGoodsInfo");
	//String detailTag = ss.getAttribute("detailTag").toString();
	String detailTag = "";
	String orderDetailFlag = (String)ss.getAttribute("orderDetailFlag");
	//System.out.println("orderDetailFlag:"+orderDetailFlag);
	String proName = orderDetail.get(0).get("PRO_NAME");
	//System.out.println("order_detail.jsp proName:"+proName);
	String roleIdCur = (String)ss.getAttribute("roleIdCur");//当前角色
	//System.out.println("roleIdCur:"+roleIdCur);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>订单详情公用</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<link rel="stylesheet" type="text/css" href="styles/extremecomponents.css">
	<link rel="stylesheet" type="text/css" href="styles/ntstyle.css">
     <link href="styles/bootstrap.css" rel="stylesheet">
    
    
    <style>
    body{background-color:#fff}
    .show1{display:<%=(orderDetailFlag.equals("1"))?"block":"none"%>}
    .show2{display:<%=(orderDetailFlag.equals("2"))?"block":"none"%>}
    .show3{display:<%=(orderDetailFlag.equals("3"))?"block":"none"%>}
    .show4{display:<%=(orderDetailFlag.equals("4"))?"block":"none"%>}
    .show5{display:<%=(orderDetailFlag.equals("5"))?"block":"none"%>}
    .show6{display:<%=(orderDetailFlag.equals("6"))?"block":"none"%>}
    .show7{display:<%=(orderDetailFlag.equals("7"))?"block":"none"%>}
    .show8{display:<%=(orderDetailFlag.equals("8"))?"block":"none"%>}
    
    .hide1{display:<%=(!orderDetailFlag.equals("1"))?"block":"none"%>}
    .hide2{display:<%=(!orderDetailFlag.equals("2"))?"block":"none"%>}
    .hide3{display:<%=(!orderDetailFlag.equals("3"))?"block":"none"%>}
    .hide4{display:<%=(!orderDetailFlag.equals("4"))?"block":"none"%>}
    .hide5{display:<%=(!orderDetailFlag.equals("5"))?"block":"none"%>}
    .hide6{display:<%=(!orderDetailFlag.equals("6"))?"block":"none"%>}
    .hide7{display:<%=(!orderDetailFlag.equals("7"))?"block":"none"%>}
    .hide8{display:<%=(!orderDetailFlag.equals("8"))?"block":"none"%>}
    .divLerders{width:102%;height:35px;border:1px solid #E8E6E6;margin-left:-6px;margin-top:5px;padding-left:0px;margin-bottom:-6px}
    .divLerders table{width:100%;height:30px}
    .checkResult1{font:bold 17px  "宋体",Arial,Times }
    .checkResult2{font:17px  "宋体",Arial,Times }
    
</style>
  </head>

<script src='js/nantian.js'></script>
<script src="js/jquery-1.9.0.js"></script>
   <script src="js/bootstrap.min.js"></script> 
  <script type="text/javascript" src="js/slide.js"></script>
 <script type="text/javascript" src="js/nantian.js"></script>
  <script type="text/javascript" src="<%=basePath%>dwr/engine.js"></script>
<script type="text/javascript" src="<%=basePath%>dwr/util.js"></script>
<script type="text/javascript" src="<%=basePath%>dwr/interface/remoteCtrl.js"></script>
<script type="text/javascript" src="<%=basePath%>dwr/interface/FileUploadDwr.js"></script>

  <script>
  
  var applyId;
  var gdsUnum;
  	function formDel(){
		$("#formDel").submit();
  	}
	function formEdit(){
		$("#formEdit").submit();
  	}
  /*sqr begin*/
  function fileChange(filePath){
		var uploadFile = dwr.util.getValue("file");
		var fileNames = uploadFile.value.split("\\");
		var fileName = fileNames[fileNames.length-1];
		showMask();
		applyId = $("#applyId").val();
		FileUploadDwr.upload(uploadFile, fileName,"<%=accountId%>",applyId, function(data) {
			hideMask();
			var fileNameSave = data.split("|")[0];
			var fileNameShow = data.split("|")[1];
			var fileLen = data.split("|")[2];
			$("#fileName").append("<tr><td>"+"<a style=\"color:#090F14\" href='DownloadTest?fileName="+fileNameSave+"&fileNameShow="+fileNameShow+"&applyId="+applyId+"'>"+fileNameShow+"</a>"+"</td><td style=\"color:#090F14\">("+fileLen+")</td><td onclick='acsDel(this)' style=\"cursor:pointer;color:red\">删除</td><td class=\"applyId\" style=\"display:none\">"+applyId+"</td><td class=\"fileNameSave\" style=\"display:none\">"+fileNameSave+"</td></tr>");
		});
	}
	function acsDel(obj){
		var $del = $(obj);
		FileUploadDwr.acsDel($del.next().html(),$del.next().next().html(),funRet);
		function funRet(data){
		}
		$del.parent().remove();
	}
	/*sqr end*/
  function del(applyId,consbId,typeName,consbName,gdsNum){
		$('#del_applyId').val(applyId);
		$('#del_ConsbId').val(consbId);
		$('#del_consbName').val(consbName);
		$('#del_gdsNumNow').val(gdsNum);
		$('#delModal').modal();
	}
	function edit(applyId,consbId,typeName,consbName,gdsNum){
		alert(consbId);
		$('#edit_applyId').val(applyId);
		$('#edit_ConsbId').val(consbId);
		
		$('#edit_consbName').val(consbName);
		$('#edit_gdsNumNow').val(gdsNum);
		$('#editModal').modal();
	}
  
  function orderConsbAdd(consbType){
	  //alert(consbType);
	  findframe("welcome","User_orderConsbChoose.action");
  }
  	function ogsAdd(){
  		myclose();
		var jumpTag = 1;
		var noticeHtml = "";
		//alert(isNaN($("#gdsNum").val()));
		if(isNaN($("#gdsNum").val())){
			noticeHtml +="申请量须为数字<br/>";
			$("#notice").html(noticeHtml).show();
			jumpTag = 0;
		}
		//alert(parseInt($("#gdsNum").val())+"|"+parseInt(gdsUnum));
		if(parseInt($("#gdsNum").val()) > parseInt(gdsUnum)){
			noticeHtml +="申请量必须小于可用库存量<br/>";
			$("#notice").html(noticeHtml).show();
			
			jumpTag = 0;
		}
		if(jumpTag==1){
			//$("#ogsAdd").submit();
			window.frames["ogsEditFrame"].location.href="User_ogsUpdate.action?updTag="+$('#updTag').val()+"&ogsBean.consbId="+$('#consbId ').val()+"&ogsBean.consbName="+$('#consbName').val()+"&ogsBean.consbBn="+$('#consbBn').val()+"&ogsBean.gdsNum="+$('#gdsNum').val();
			//findframe("ogsEditFrame","User_ogsUpdate.action?updTag=$('#updTag').val()&ogsBean.consbId=$('#consbId ')&ogsBean.consbName=$('#consbName')&ogsBean.consbBn=$('#consbBn')&ogsBean.gdsNum=$('#gdsNum')");
		}
	}
	function putOrder(consbId,typeName,consbName,consbBn,applyId,gdsUnum1){
		gdsUnum = gdsUnum1;
		//alert(consbId+"|"+typeName+"|"+consbName+"|"+consbBn+"|"+applyId+"|"+gdsUnum1);
		$('#consbId').val(consbId);
		$('#consbName').val(consbName);
		$('#consbBn').val(consbBn);
		$('#consbBn').val(consbBn);
		$('#applyId').val("<%=applyId%>");
		$('#consbEditModal').modal();
	}
	function pageBack(){
		findframe("welcome","User_relateToMeHandle.action");
	}
	//1 同意 2拒绝
	function check(ldState){
  		switch(parseInt("<%=roleIdCur%>")){
  		case 2://主办领导（部领导）
  			findframe("welcome","User_applyCheck.action?applyBean.applyId="+"<%=applyId%>"+"&applyBean.userId="+"<%=userId%>"+"&applyBean.ldState="+ldState+"&applyBean.ldDesc="+$("#zbDesc").val());
  			break;
  		case 3://科室主办（初审员）
  			findframe("welcome","User_applyCheck.action?applyBean.applyId="+"<%=applyId%>"+"&applyBean.userId="+"<%=userId%>"+"&applyBean.ldState="+ldState+"&applyBean.ldDesc="+$("#csDesc").val());
  			break;
  		case 4://部门领导
  			findframe("welcome","User_applyCheck.action?applyBean.applyId="+"<%=applyId%>"+"&applyBean.userId="+"<%=userId%>"+"&applyBean.ldState="+ldState+"&applyBean.ldDesc="+$("#ldDesc").val());
  			break;
  		}
  	}
	
	function peditionSub(subTag){//0保存1提交
  		if($("#typeSelect1").val()=="0"){
  			alert("请选择大类");
  			return;
  		}
  		if($("#typeSelect2").val()=="0"){
  			alert("请选择小类");
  			return;
  		}
  		showMask();
  		$("#subTag").val(subTag);
  		//alert($('#typeId').val());
  		//alert($("#petition2").val());
  		$("#peditionForm").submit();
  	}
	var typeQueryFlag = 0;
  	/*所有耗材类型查询 flag:1查询大类 2查询大类对应小类*/
	function consbTypeQuery(flag,typeIdp,selectName){
		remoteCtrl.getConsbType(flag,typeIdp,funRet);
		function funRet(data){
			$("#"+selectName+">option").remove();
			$("#"+selectName).append("<option value=\"0\">选择申请类别("+(flag==1?"大类":"小类")+")</option>");
			for(var i = 0; i < data.length; i++){
				$("#"+selectName).append("<option value="+data[i].TYPE_ID +">"+data[i].TYPE_NAME+"</option>");
			}
			<%
				if(!(orderDetail.get(0).get("TYPE_ID") == null)){
					%>$("#"+selectName).val("<%=orderDetail.get(0).get("TYPE_ID")%>");<%
				}
			%>
			try{
				if(flag==1)
					$("#"+selectName).val("<%=orderDetail.get(0).get("TYPE_IDP")%>");
				else
					$("#"+selectName).val("<%=orderDetail.get(0).get("TYPE_ID")%>");
			}catch(e){
			}
			/*初始化各级领导审批意见*/
			<%
			if(!(orderDetail.get(0).get("APPLY_DESC") == null)){
				%>
				$("#petition1").val("<%=orderDetail.get(0).get("APPLY_DESC")%>");
				$("#petition2").val("<%=orderDetail.get(0).get("APPLY_DESC")%>");
				<%
			}
		%>
		typeQueryFlag = 1;
		}
	}
  	
	  function orderConsbAdd(consbType){
		  findframe("welcome","User_orderConsbChoose.action");
	  }
	  //小类查询
	 function querySubType(){
		if(typeQueryFlag == 1)//避免查询冲突，大类查询结束，再发起小类查询交易
			consbTypeQuery(2,1000,"typeSelect2");
		else
			setTimeout(querySubType,100);
	 }
	$(function() {
		$("#proName1").val("<%=proName%>");
		$("#proName2").html("<%=proName%>");
		consbTypeQuery(1,0,"typeSelect1");//大类查询
		setTimeout(querySubType,100);
		applyId = "<%=applyId%>";
		$("#labelApplyId").html("<%=orderDetail.get(0).get("APPLY_ID")%>");
		if("<%=updateMsg%>"!="null"){
			$("#updateMsg").html("<%=updateMsg%>");
		}
		setTimeout(function() {$("#updateMsg").html("");}, 3000);
		<%
		ss.setAttribute("updateMsg", "");
		%>
		<%
			for(int i = 0;i < orderAcs.size();i++){
				String fileNameSave = orderAcs.get(i).get("FILE_NAME1");
				String fileNameShow = orderAcs.get(i).get("FILE_NAME2");
				String fileLen = orderAcs.get(i).get("FILE_LENTH");
				%>
				$("#fileName").append("<tr><td>"+"<a style=\"color:#090F14\" href='DownloadTest?fileName=<%=fileNameSave%>&fileNameShow=<%=fileNameShow%>&applyId=<%=applyId%>'><%=fileNameShow%></a>"+"</td><td style=\"color:#090F14\">(<%=fileLen%>)</td><td onclick='acsDel(this)' style=\"cursor:pointer;color:red\">删除</td><td class=\"applyId\" style=\"display:none\"><%=applyId%></td><td class=\"fileNameSave\" style=\"display:none\"><%=fileNameSave%></td></tr>");
				$("#fileName2").append("<tr><td>"+"<a style=\"color:#090F14\" href='DownloadTest?fileName=<%=fileNameSave%>&fileNameShow=<%=fileNameShow%>&applyId=<%=applyId%>'><%=fileNameShow%></a>"+"</td><td style=\"color:#090F14\">(<%=fileLen%>)</td></tr>");
				<%
			}
		%>
	});
  </script>
  <body class="frameBody ">
  <div>&nbsp;
  	<div id="updateMsg" style="position:absolute"></div>
  </div>
  <div style="width:300px;margin:auto;border:0px solid red;font-size:22px;background-color:#D5D5D5" align="center"><label id="title">订单详情</label></div><br />
  <div style="border:1px dashed #E8E6E6;width:750px;margin:auto;padding:5px" >
  	
  	<form id="peditionForm" method="post" action="Order_applyInput.action" class="form-horizontal">
        <div>
        	<div>申请单号:<label id="labelApplyId"></label></div>
        	<table class="table table-bordered table-condensed"  border="0" width="750" cellspacing="1" cellpadding="1" style="background-color:#D5D5D5;">
        	<tr>
            	<td class="tdtitle">申请人</td>
            	<td class="cleanBack" style="width:200px"><%=accountId%></td>
            	<td class="tdtitle" width="150px">所属部门</td>
            	<td  class="cleanBack"><%=orgName%></td>
            </tr>
            <tr>
            	<td class="tdtitle">项目名称：</td>
            	<td colspan="3" class="cleanBack" style="display:<%=orderDetailFlag.equals("1")?"block":"none"%>"><input type="text" id="proName1" name="applyBean.proName" style="height:30px;width:363px;font-size:17px"/></td>
            	<td colspan="3" class="cleanBack" style="display:<%=!orderDetailFlag.equals("1")?"block":"none"%>"><div id="proName2">项目名称</div></td>
            </tr>
            <tr>
            	<td class="tdtitle">申请类别(大类)</td>
            	<td class="cleanBack" style='display:<%=(!orderDetailFlag.equals("1"))?"block":"none"%>'><%=orderDetail.get(0).get("TYPE_NAMEP")%></td>
            	<td class="cleanBack" style='display:<%=(orderDetailFlag.equals("1"))?"block":"none"%>'><select id="typeSelect1" name="typeBean.typeIdp" onchange="consbTypeQuery(2,this.value,'typeSelect2')" style="height:30px;width:220px;font-size:17px"><option value="0">选择申请类别(大类)</option></select></td>
            
            	<td class="tdtitle">申请类别(小类)</td>
            	<td class="cleanBack" style='display:<%=(!orderDetailFlag.equals("1"))?"block":"none"%>'><%=orderDetail.get(0).get("TYPE_NAME")%></td>
            	<td class="cleanBack" style='display:<%=(orderDetailFlag.equals("1"))?"block":"none"%>'><select id="typeSelect2" name="typeBean.typeId" onchange="$('#typeId').val(this.value)" style="height:30px;width:220px;font-size:17px"><option value="0">选择申请类别(小类)</option></select></td>
            </tr>
            <tr>
            	<td class="tdtitle">耗材申请理由</td>
                <td colspan="3" class="cleanBack contentHeight">
                	<textarea style='display:<%=(!orderDetailFlag.equals("1"))?"block":"none"%>' readonly class="leaderNote hide1" id="petition1"><%=orderDetail.get(0).get("APPLY_DESC")%></textarea>
               		<textarea style='display:<%=(orderDetailFlag.equals("1"))?"block":"none"%>' class="leaderNote" id="petition2" name="applyBean.applyDesc"></textarea>
                </td>
            </tr>
            
            <!-- 部门领导 -->
            <tr style='display:<%=(!orderDetailFlag.equals("1"))?"block":"none"%>'>
            	<td class="tdtitle">部门领导审批</td>
            	<td colspan="3" style='display:<%=(orderDetailFlag.equals("4"))?"block":"none"%>' class="cleanBack contentHeight">
                	<textarea class="leaderNote"  id="ldDesc" name="applyBean.ldDesc"></textarea>
                </td>
                <td colspan="3" class="cleanBack contentHeight" style='display:<%=(!orderDetailFlag.equals("4"))?"block":"none"%>'>
                	<textarea readonly class="leaderNote" style=""><%if(orderMap.get("LD_ID").equals("0")){
                					%>无审批意见<%
                				}else{
                					%><%=orderMap.get("LD_DESC") %><%
                				}
                	%></textarea>
					<div class="divLerders">
                		<table border="0">
                			<tr>
                				<td style="width:200px">
                					<div>
			                			<div class="inlineClass checkResult1">审批结果：</div>
			                			<div class="inlineClass checkResult2">
					                	<%
					                		if(orderMap.get("LD_STATE").equals("0")){
					                			%>未审批<%
					                		}else if(orderMap.get("LD_STATE").equals("1")){
					                			%>审批通过<%
					                		}else{
					                			%>审批未通过<%
					                		}
					                	%>
					                	</div>
				                	</div>
                				</td>
                				<td style="width:200px">
                					<div><div class="inlineClass checkResult1">id:</div><div class="inlineClass checkResult2"><%=orderMap.get("LD_ID")%></div></div>
                				</td>
                				<td>
                					<div><div class="inlineClass checkResult1">时间:</div><div class="inlineClass checkResult2"><%=orderMap.get("LD_TIME")%></div></div>
                				</td>
                			</tr>
                		</table>
                	</div>
                </td>
            </tr>
            <!-- 科室主办 -->
            <tr style='display:<%=(!orderDetailFlag.equals("1")) && !orderDetailFlag.equals("4")?"block":"none"%>'>
            	<td class="tdtitle" style="height:100px;vertical-align:center;line-height:100px">科室主办审批</td>
            	<td colspan="3"  class="cleanBack contentHeight" style='display:<%=(orderDetailFlag.equals("5"))?"block":"none"%>'>
                	<textarea class="leaderNote2"  id="csDesc" name="applyBean.csDesc"></textarea>
                </td>
                <td colspan="3" class="cleanBack contentHeight" style='display:<%=(!orderDetailFlag.equals("5"))?"block":"none"%>'>
                	<textarea readonly class="leaderNote" style=""><%if(orderMap.get("CS_ID").equals("0")){
                					%>未审批<%
                				}else{
                					%><%=orderMap.get("CS_DESC") %><%
                				}
                	%></textarea>
					<div class="divLerders">
                		<table border="0">
                			<tr>
                				<td style="width:200px">
                					<div>
			                			<div class="inlineClass checkResult1">审批结果：</div>
			                			<div class="inlineClass checkResult2">
					                	<%
					                		if(orderMap.get("LD_STATE").equals("0")){
					                			%>未审批<%
					                		}else if(orderMap.get("LD_STATE").equals("1")){
					                			%>审批通过<%
					                		}else{
					                			%>审批未通过<%
					                		}
					                	%>
					                	</div>
				                	</div>
                				</td>
                				<td style="width:200px">
                					<div><div class="inlineClass checkResult1">id:</div><div class="inlineClass checkResult2"><%=orderMap.get("LD_ID")%></div></div>
                				</td>
                				<td>
                					<div><div class="inlineClass checkResult1">时间:</div><div class="inlineClass checkResult2"><%=orderMap.get("LD_TIME")%></div></div>
                				</td>
                			</tr>
                		</table>
                	</div>
                </td>
            </tr>
            
            <!-- 部领导审批 -->
			<tr style='display:<%=(!orderDetailFlag.equals("1")) && !orderDetailFlag.equals("4") && !orderDetailFlag.equals("5")?"block":"none"%>'>
            	<td class="tdtitle" style="height:100px;vertical-align:center;line-height:100px">部领导审批</td>
            	<td colspan="3"  class="cleanBack contentHeight" style='display:<%=(orderDetailFlag.equals("6"))?"block":"none"%>'>
                	<textarea class="leaderNote2"  id="zbDesc" name="applyBean.zbDesc"></textarea>
                </td>
                <td colspan="3" class="cleanBack contentHeight" style='display:<%=(!orderDetailFlag.equals("6"))?"block":"none"%>'>
                	<textarea readonly class="leaderNote" style=""><%if(orderMap.get("ZB_ID").equals("0")){
                					%>未审批<%
                				}else{
                					%><%=orderMap.get("ZB_DESC") %><%
                				}
                	%></textarea>
					<div class="divLerders">
                		<table border="0">
                			<tr>
                				<td style="width:200px">
                					<div>
			                			<div class="inlineClass checkResult1">审批结果：</div>
			                			<div class="inlineClass checkResult2">
					                	<%
					                		if(orderMap.get("LD_STATE").equals("0")){
					                			%>未审批<%
					                		}else if(orderMap.get("LD_STATE").equals("1")){
					                			%>审批通过<%
					                		}else{
					                			%>审批未通过<%
					                		}
					                	%>
					                	</div>
				                	</div>
                				</td>
                				<td style="width:200px">
                					<div><div class="inlineClass checkResult1">id:</div><div class="inlineClass checkResult2"><%=orderMap.get("LD_ID")%></div></div>
                				</td>
                				<td>
                					<div><div class="inlineClass checkResult1">时间:</div><div class="inlineClass checkResult2"><%=orderMap.get("LD_TIME")%></div></div>
                				</td>
                			</tr>
                		</table>
                	</div>
                </td>
            </tr>
            
                        
        	</table>
        </div>
        
        
        
        <div style="display:none">
        <input id="applyId" name="applyBean.applyId" value="<%=applyId%>" style="display:block"/>
   		<input id="subTag" name="subTag" style="display:block"/>
   	 	<input id="insertFlag" name="insertFlag" style="display:block"/>
   	 	</div>
   	 	
   	 	
   	 	
   	 	
	<!-- 添加附件 -->
        <div style="margin-top:5px;border:0px solid red;display:<%=orderDetailFlag.equals("1")?"block":"none"%>">
	        <input type="button"  value="添加附件" style="width:70px"  
		    	onmousemove="file.style.pixelLeft=event.x-90;file.style.pixelTop=this.offsetTop;">  
			<input type="file" id="file" name="file" style="width:100px;height:30px;position:absolute;filter:alpha(opacity:0);cursor:pointer" size="1"   
		    	onchange="fileChange(this.value)"> 
		    <div style="color:red">
			  	<table id="fileName" border="0" style="font-size:14px;"></table>
			</div>
		</div>	
		
		
		
		<!--附件显示  -->
		<div style="display:<%=orderDetailFlag.equals("1")?"none":"block"%>">
		    <div class="assessory2">附件：</div>
		    <div style="color:red">
			  	<table id="fileName2" border="0" style="font-size:14px;"></table>
			</div>
		</div>
		
	</form>
	
	<!-- 拟配置数量 -->
	<div style='display:<%=(orderDetailFlag.equals("5") || orderDetailFlag.equals("6"))?"block":"none"%>'  align="center" >
	    <ec:table 
			items="orderGoodsInfo"
			action="${pageContext.request.contextPath}/User_prepareOrder.action"
			imagePath="${pageContext.request.contextPath}/images/table/*.gif"
			title="拟同意配置及数量"
			rowsDisplayed="10"
			filterable="false"
			style="margin-top:3px"
			var="pres"
			width="100%"
			>
			<ec:row  >
				<ec:column property="TYPE_ID" title="大类"/>
				<ec:column property="TYPE_NAME" title="小类"/>
				<ec:column property="CONSB_NAME" title="名称"/>
				<ec:column property="CONSB_BN" title="品牌"/>
				<ec:column property="CONSB_MN" title="型号"/>
				<ec:column property="CONSB_SD" title="规格"/>
				<ec:column property="GDS_NUM" title="审批数量"/>
				<ec:column property="CONSB_UNUM" title="库存可用数量"/>
				
				<ec:column alias="操作" filterable="false" sortable="false">
				<!-- 法2：不会自动提交http请求 -->
				<div>
					<div style="display:inline;">
					<a data-toggle="modal" onclick="edit('${pageScope.pres.APPLY_ID}','${pageScope.pres.CONSB_ID}','${pageScope.pres.TYPE_NAME}','${pageScope.pres.CONSB_NAME}','${pageScope.pres.GDS_NUM}','${pageScope.pres.CONSB_UNUM}')" class="btn btn-xs btn-primary">编辑</a>
					</div>
					<div style="display:inline;">
					<a data-toggle="modal" onclick="del('${pageScope.pres.APPLY_ID}','${pageScope.pres.CONSB_ID}','${pageScope.pres.TYPE_NAME}','${pageScope.pres.CONSB_NAME}','${pageScope.pres.GDS_NUM}')" class="btn btn-xs btn-danger">删除</a>	
					</div>
				</div>
				</ec:column>
			</ec:row>
		</ec:table>
		<button type="button" onclick="orderConsbAdd('<%=orderDetail.get(0).get("TYPE_ID")%>')">添加耗材</button>
	</div>
	
	<!-- 申请人 -->
		<div style="border:0px solid red;width:100%;margin-top:20px;display:<%=orderDetailFlag.equals("1")?"block":"none"%>" align="center">
				<button type="button" onclick="peditionSub('0')">保存</button>
		  		<button type="button" onclick="peditionSub('1')">保存&提交</button>
		</div>
  </div>  
 <hr/>
 <!-- 按钮区 部门领导、科室主办（初审员）、部领导（主办领导） 可见 -->
 <div style='display:<%=(roleIdCur.equals("02")||roleIdCur.equals("03")||roleIdCur.equals("04"))?"block":"none"%>;width:100%;border:0px red solid;margin:auto;text-align:center'>
	<button type="button" onclick="this.disabled=true;check(1)">同意</button>
	<button type="button" onclick="this.disabled=true;check(2)">拒绝</button>
	<button type="button" onclick="this.disabled=true;pageBack()" style="display:block">返回</button> 	
 </div>
 <script>
    <%
    	if(orderDetail.get(0).get("USER_ID").equals("0")){
    		%>
    		$("#insertFlag").val("1");
    		<%
    	}else{
    		%>
    		$("#insertFlag").val("0");
    		<%
    	}
    %>
    </script>
    
 <!-- 编辑耗材分配数量 -->
<div class="modal fade" id="editModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h4 class="modal-title" id="myModalLabel">编辑</h4>
      </div>
    	<div class="modal-body">
			<form class="form-horizontal" role="form" id="formEdit"
				action="User_ogsUpdate.action" method="post">
				<div style="display:none;"><input type="text" name="updTag" value="2"/></div>
				<div class="form-group">
					<label for="applyId" class="col-sm-2 control-label">订单号（显示用）</label>
					<div class="col-sm-10">
						<input type="text" class="form-control" id="edit_applyId" readonly>
					</div>
				</div>
				<div class="form-group">
					<label for="myConsbName" class="col-sm-2 control-label">耗材名称</label>
					<div class="col-sm-10">
						<input type="text" class="form-control" id="edit_consbName" readonly>
					</div>
				</div>
				<div class="form-group">
					<label for="gdsNumNow" class="col-sm-2 control-label">当前数量</label>
					<div class="col-sm-10">
						<input type="text" class="form-control" id="edit_gdsNumNow" name="gdsNumPre" readonly>
					</div>
				</div>
				<div class="form-group">
					<label for="gdsNumNew" class="col-sm-2 control-label">新数量</label>
					<div class="col-sm-10">
						<input type="text" class="form-control" id="edit_gdsNumNew" name="ogsBean.gdsNum">
					</div>
				</div>
				<input type="text" class="form-control" id="edit_ConsbId" name="ogsBean.consbId" style="display:none">
			</form>
		</div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
        <button type="button" class="btn btn-primary" onclick="formEdit()">确定</button>
      </div>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->


<!-- 拟分配耗材删除 -->
<div class="modal fade" id="delModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h4 class="modal-title" id="myModalLabel">确定要删除此项么？？？？？</h4>
      </div>
    	<div class="modal-body">
			<form class="form-horizontal" role="form" id="formDel"
				action="User_ogsUpdate.action" method="post">
				<div style="display:none;"><input type="text" name="updTag" value="3"/></div>
			<div class="form-group">
					<label for="applyId" class="col-sm-2 control-label">订单号（显示用）</label>
					<div class="col-sm-10">
						<input type="text" class="form-control" id="del_applyId" readonly>
					</div>
				</div>
				<div class="form-group">
					<label for="myConsbName" class="col-sm-2 control-label">耗材名称</label>
					<div class="col-sm-10">
						<input type="text" class="form-control" id="del_consbName" readonly>
					</div>
				</div>
				<div class="form-group">
					<label for="gdsNumNow" class="col-sm-2 control-label">当前数量</label>
					<div class="col-sm-10">
						<input type="text" class="form-control" id="del_gdsNumNow" readonly>
					</div>
				</div>
				<input type="text" class="form-control" id="del_ConsbId" name="ogsBean.consbId" style="display:none">
			</form>
		</div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
        <button type="button" class="btn btn-primary" onclick="formDel()">确定</button>
      </div>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->
  </body>
</html>
  



	