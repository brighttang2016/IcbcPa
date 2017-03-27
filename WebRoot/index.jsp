<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'index.jsp' starting page</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
  </head>
  <script>
  var act;
  var i = 0;
  	function excTimeout(){
  		document.getElementById("test").value = i++;
  		act = setTimeout(excTimeout,1000);
  		if(i > 10)
  			clearTimeout(act);
  	}
  	function pageOnload(){
  		document.getElementById("test").value = "tttttt";
  		excTimeout();
  	}
  </script>
  <body onload="pageOnload()">
    <input type="text" id="test"/>
  </body>
</html>
