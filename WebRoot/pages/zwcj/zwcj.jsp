<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
  <head>
  <base href="<%=basePath%>">
    <title>职务层级设置</title>
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
    body{background-color:#DFE9F6}      
    
	</style> 
	<script type="text/javascript" src="js/ext/ext-all.js"></script>
	<script type="text/javascript" src="js/ext/ext-lang-zh_CN.js"></script>
	<script type="text/javascript" src="js/ext-nantian.js"></script>
	
	 <script type="text/javascript" src="js/common_fun.js"></script>
	<script type="text/javascript" src="js/common_store.js"></script>
	<script type="text/javascript" src="pages/zwcj/zwcj_store.js"></script>
	<script type="text/javascript" src="pages/zwcj/zwcj_mod.js"></script>
	<script type="text/javascript" src="pages/zwcj/zwcj_fun.js"></script>
	<script type="text/javascript" src="pages/zwcj/zwcj.js"></script>
  </head>
<body>
</body>
</html>
