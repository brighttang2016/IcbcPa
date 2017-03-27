<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<%-- <%@ taglib uri="/struts-tags" prefix="s" %> --%>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String userId = "";
String userName = "";
String  orgName = "";
try{
	userId = session.getAttribute("userId").toString();
	userName = session.getAttribute("userName").toString();
	orgName = session.getAttribute("orgName").toString();
}catch(Exception e){}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
  <head>
  	<%-- <base href="<%=basePath%>"> --%>
    <title>前端变量池</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
  </head>
  <script>
	/*定义变量池变量*/
    var userId = "<%=userId%>"; 
    var myName = "test";
    var userName = "<%=userName%>";
    var orgName = "<%=orgName%>";
    /* var downloadIframe = document.createElement("iframe");
    downloadIframe.setAttribute("id","downloadIframe");
    window.document.getElementsByTagName("body")[0].appendChild(downloadIframe);  */
    //downloadIframe.setAttribute("id","downloadIframe");
  	function pageOnload(){
  		//parent.funpool.location = "<%=basePath%>pages/main/main_frame.jsp";//声明basePath时，兼容ie、谷歌两种内核。【ie:main_frame.jsp;谷歌：pages/main/main_frame.jsp】2015-12-22
  		parent.funpool.location = "main_frame.jsp";//不声明basePath时，兼容两种浏览器
  	}
  </script>
<body onload="pageOnload()">前端变量池</body>
</html>
