<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
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
  
  <script type="text/javascript" src="<%=basePath%>dwr/engine.js"></script>
  <script type="text/javascript" src="<%=basePath%>dwr/util.js"></script>
  <script type="text/javascript" src="<%=basePath%>dwr/interface/MessagePush.js"></script>
  
  <script>
  	function testMethod3(){
  		alert("tttt");
  		MessagePush.testMethod3("client客户端传入参数",getTestMethod3Ret);
  		function getTestMethod3Ret(data){
  			alert(data);
  		}
  	}
  	
  	function onPageLoad(){
  		//alert("onPageLoad");
        MessagePush.onPageLoad("12345");
    }
  	
  	function showMessage(msg){
  		alert(msg);
  	}
  	function push(){
  		//MessagePush.pushBegin("receiver1|receiver2");
  		MessagePush.pushBegin("receiver1");
  	}

  </script>
  
  <body onload="dwr.engine.setActiveReverseAjax(true);dwr.engine.setNotifyServerOnPageUnload(true);onPageLoad();">
    <h1>sender</h1><br>
    <a href="#">tet</a>
    <input type="button" value="click" onclick="testMethod3()"/>
    <input type="button" value="push" onclick="push()"/>
  </body>
</html>
