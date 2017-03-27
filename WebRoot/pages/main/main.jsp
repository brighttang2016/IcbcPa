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
  </head>
  <script>
  window.onresize = function(){
	 parent.setId.rows="0,100%";
  };  
  
  </script>
<frameset id='setId' rows="0,100%" border="0">
    	<frame  name="varpool" src="pages/main/main_var.jsp"/>
        <frame  name="funpool" id="funpoolId"/>
</frameset><noframes></noframes>  
<body></body>
</html>
