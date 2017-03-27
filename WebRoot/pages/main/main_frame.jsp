<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<%-- <%@ taglib uri="/struts-tags" prefix="s" %> --%>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
  <head>
  <base href="<%=basePath%>">
    <title>人力资源绩效考核数据平台</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<%-- <link rel="stylesheet" type="text/css" href="<%=basePath%>resources/css/ext-all.css"/>
	<link rel="stylesheet" type="text/css" href="<%=basePath%>styles/consb-style.css"/> --%>
	<link rel="stylesheet" type="text/css" href="resources/css/ext-all.css"/>
	<link rel="stylesheet" type="text/css" href="styles/consb-style.css"/>
	<link rel="stylesheet" type="text/css" href="<%=basePath%>resources/css/icon.css"/>
	<style type= "text/css" >      
    .x-selectable, .x-selectable * {      
        -moz-user-select: text! important ;      
        -khtml-user-select: text! important ;      
    }   
    body{
   	 /*background-color:#DFE8F6 ;*/
   	 margin:0px;
    }  
    .nav {
        	background-image:url(<%=basePath%>images/ext/folder_go.png);
    }
	</style> 
	<%-- <script type="text/javascript" src="<%=basePath%>js/ext/ext-all.js"></script>
	<script type="text/javascript" src="<%=basePath%>js/ext/ext-lang-zh_CN.js"></script>
	<script type="text/javascript" src="<%=basePath%>js/common_fun.js"></script>
	<script type="text/javascript" src="main.js"></script>
	<script type="text/javascript" src="<%=basePath%>js/ext-nantian.js"></script> --%>
	
	<script type="text/javascript" src="js/ext/ext-all.js"></script>
	<script type="text/javascript" src="js/ext/ext-lang-zh_CN.js"></script>
	<script type="text/javascript" src="js/ext-nantian.js"></script>
	<script type="text/javascript" src="js/common_fun.js"></script>
	
	<script type="text/javascript" src="pages/main/main.js"></script>
	
	<%-- <base href="<%=basePath%>"> --%>
	<%-- <%@ include file=".././public/include.jsp"%>  --%>
	<script>
		var basePath = "<%=basePath%>";
		
	</script>
  </head>
  
<body>
	<div id="myTable1" style="border:0px solid red; width:200px;margin-left:100px"></div>
	<div id="myTable2" style="border:0px solid red; width:200px;margin:auto"></div>
	
	<iframe id='contendIframe' frameborder=0 src='pages/main/welcome.jsp' style="width:100%;height:100%;border:0px solid red;"/>
</body>

</html>
