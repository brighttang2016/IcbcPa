<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
  <head>
    <title>My JSP 'include.jsp' starting page</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!-- 此页面保存所有界面公共变量2015-04-21-->
	<script>
<%
	 	String userId = "";
		Object userIdOb = session.getAttribute("userId");
		if(userIdOb != null)
			userId = userIdOb.toString();
	 %>
	 var userId = "<%=userId%>";
	 </script>
  </head>
</html>
