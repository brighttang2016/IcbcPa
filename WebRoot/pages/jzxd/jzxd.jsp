<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String userId = "";
try{
	userId = session.getAttribute("userId").toString();
}catch(Exception e){
}

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
  <head>
  <base href="<%=basePath%>">
    <title>基准薪点初始化</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<link rel="stylesheet" type="text/css" href="<%=basePath%>resources/css/ext-all.css"/>
	<link rel="stylesheet" type="text/css" href="<%=basePath %>styles/ntstyle.css"/>
	<style type= "text/css" >      
    .x-selectable, .x-selectable * {      
        -moz-user-select: text! important ;      
        -khtml-user-select: text! important ; 
         
    }
    .bodyClass{background-color:#ffffff}
    /* body{background-color:red}      */
    
	</style> 
	<script type="text/javascript" src="<%=basePath%>dwr/engine.js"></script>
    <script type="text/javascript" src="<%=basePath%>dwr/util.js"></script>
    <script type="text/javascript" src="<%=basePath%>dwr/interface/MessagePush.js"></script>
    
    
   
	<script type="text/javascript" src="js/ext/ext-all.js"></script>
	<script type="text/javascript" src="js/ext/ext-lang-zh_CN.js"></script>
	<script type="text/javascript" src="js/ext-nantian.js"></script>
	<script type="text/javascript" src="js/component/fileupload.js"></script><!-- fileupload.js置于ext-all.js之后，jzxd.js之前 -->
	<script type="text/javascript" src="js/common_fun.js"></script>
	<script type="text/javascript" src="pages/jzxd/jzxd.js"></script>
  </head>
  
  <script>
  /* function pageOnload(){
	  MessagePush.onPageLoad("receiver1");
  }
  
  function push(){
		MessagePush.pushBegin("receiver1");
	} */
	/* function showMessage(msg){
		document.getElementById("ta").value = msg;
	} */
	var userId = "<%=userId%>";
  </script>
<!-- <body class="bodyClass" onload="pageOnload()"> -->
<body class="bodyClass">
	<!--  <input type="button" value="push" onclick="push()"/>-->
	<!--  <textarea rows="10" cols="10" id="ta" style="margin-top:200px"></textarea>  -->
</body>
</html>
