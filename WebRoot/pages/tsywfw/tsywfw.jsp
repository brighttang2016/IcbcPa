<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String userId = "test";
try{
	userId = session.getAttribute("userId").toString();
}catch(Exception e){}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
  <head>
  <base href="<%=basePath%>">
    <title>支行特色业务指标范围设置</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<link rel="stylesheet" type="text/css" href="<%=basePath%>resources/css/ext-all.css"/>
	<link rel="stylesheet" type="text/css" href="<%=basePath%>resources/css/ItemSelector.css"/>
	<link rel="stylesheet" type="text/css" href="<%=basePath %>styles/consb-style.css"/>
	<link rel="stylesheet" type="text/css" href="<%=basePath%>resources/css/icon.css"/>
	<style type= "text/css" >      
    .x-selectable, .x-selectable * {      
        -moz-user-select: text! important ;      
        -khtml-user-select: text! important ;   
    }
    body{background-color:#DFE9F6}      
	</style> 
	<script type="text/javascript" src="<%=basePath%>dwr/engine.js"></script>
    <script type="text/javascript" src="<%=basePath%>dwr/util.js"></script>
    <script type="text/javascript" src="<%=basePath%>dwr/interface/MessagePush.js"></script>
	
	<script type="text/javascript" src="js/ext/ext-all.js"></script>
	<script type="text/javascript" src="js/ext/ext-lang-zh_CN.js"></script>
	
	<script type="text/javascript" src="js/ext-nantian.js"></script>
	<script type="text/javascript" src="js/common_fun.js"></script>
	 <script type="text/javascript" src="js/common_store.js"></script>
	 <script type="text/javascript" src="js/common_mod.js"></script>
	 <script type="text/javascript" src="js/component/fileupload.js"></script>
	<script type="text/javascript" src="pages/tsywfw/tsywfw_store.js"></script>
 	<script type="text/javascript" src="js/component/org.js"></script>
 	<script type="text/javascript" src="pages/tsywfw/tsywfw_mod.js"></script>
	<script type="text/javascript" src="pages/tsywfw/tsywfw.js"></script>
	<script type="text/javascript" src="pages/tsywfw/tsywfw_fun.js"></script>
	<script>
	var userId = "<%=userId%>";
	</script>
  </head>
<body>
	 <!-- 
	  <div id="myTable1" style="border:1px solid red; width:100%"></div>
	     -->
	      <iframe id='downloadIframe' frameborder=0 "/>
</body>
</html>
