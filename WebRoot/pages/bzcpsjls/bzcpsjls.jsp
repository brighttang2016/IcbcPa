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
    <title>标准产品考核基础数据业绩展示（往期历史数据记录）</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<link rel="stylesheet" type="text/css" href="<%=basePath%>resources/css/ext-all-debug.css"/>
	<link rel="stylesheet" type="text/css" href="<%=basePath%>resources/css/ItemSelector.css"/>
	<link rel="stylesheet" type="text/css" href="<%=basePath%>resources/css/icon.css"/>
	<link rel="stylesheet" type="text/css" href="<%=basePath %>styles/consb-style.css"/>
	<style type= "text/css" >      
    .x-selectable, .x-selectable * {      
        -moz-user-select: text! important ;      
        -khtml-user-select: text! important ;   
    }
    body{background-color:#DFE9F6} ;
    .noBackground{background-color:#ffffff} 
    .sgtz{background-image:url(images/ext/sgtz.png)}  
    .zhxf{background-image:url(images/ext/zhxf.png)} 
    .shxf{background-image:url(images/ext/shxf.png)}
    .fjxf{background-image:url(images/ext/fjxf.png)}
    .menuVCenter{padding:10px;}  
	</style> 
	<script type="text/javascript" src="<%=basePath%>dwr/engine.js"></script>
    <script type="text/javascript" src="<%=basePath%>dwr/util.js"></script>
    <script type="text/javascript" src="<%=basePath%>dwr/interface/MessagePush.js"></script>

	<script type="text/javascript" src="js/ext/ext-all-debug.js"></script>
	<script type="text/javascript" src="js/ext/ext-lang-zh_CN.js"></script>
	
	<script type="text/javascript" src="js/ext-nantian.js"></script>
	<script type="text/javascript" src="js/common_fun.js"></script>
	<script type="text/javascript" src="js/common_store.js"></script>
	<script type="text/javascript" src="js/common_mod.js"></script>
	<script type="text/javascript" src="js/comm.js"></script>
	  
	<script type="text/javascript" src="pages/bzcpsjls/bzcpsjls_store.js"></script>
	<script type="text/javascript" src="pages/bzcpsjls/bzcpsjls_mod.js"></script>
	<script type="text/javascript" src="js/component/fileupload.js"></script>
	<script type="text/javascript" src="js/component/org.js"></script>
	<script type="text/javascript" src="js/component/grid.js"></script>
	
	<script type="text/javascript" src="pages/bzcpsjls/bzcpsjls.js"></script>
	<script type="text/javascript" src="pages/bzcpsjls/bzcpsjls_fun.js"></script>
	<script>
	var userId = "<%=userId%>";
	</script>
  </head>
<body>
<!-- 文件下载专用iframe,文件下载是，直接使用document.location=XXX，会使得当前页面注册的后台监听失效 -->
	 <iframe id='downloadIframe' frameborder=0 "/>
</body>
</html>
