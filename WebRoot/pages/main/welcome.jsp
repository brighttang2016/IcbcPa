<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    
    
    <title>welcome</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
<base href="<%=basePath%>">
	<%@ include file=".././public/include.jsp"%> 
  </head>
   <script>
  	function pageJump(){
  		parent.location.href = "pages/login/login.jsp";
  	}
  </script>
  <body style='background-color:#ffffff'>
    <img src="images/sys/welcome.png" style="position:absolute;left:20px;top:30%;"/>
    
    <!-- 
    <div><a href="FileDownload.action">下载</a></div>
	<div><input type="button" value="超时跳转" onclick="pageJump()"/></div>
	 -->
  </body>
</html>
