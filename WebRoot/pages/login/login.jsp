<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String userIdLogin = request.getParameter("userid");
System.out.println("userIdLogin:"+userIdLogin);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
  <head>
    <title>用户登录</title>
    
     <base href="<%=basePath%>"> 
      
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--  
	<link rel="stylesheet" type="text/css" href="<%=basePath%>resources/css/ext-all.css"/>
	
	-->
	<link rel="stylesheet" type="text/css" href="resources/css/ext-all.css"/>
	<style type= "text/css" >      
    .x-selectable, .x-selectable * {      
        -moz-user-select: text! important ;      
        -khtml-user-select: text! important ;      
    }      
	</style> 
	<!-- 
	<script type="text/javascript" src="<%=basePath%>js/ext/ext-all.js"></script>
	<script type="text/javascript" src="<%=basePath%>js/ext/ext-lang-zh_CN.js"></script>
	<script type="text/javascript" src="<%=basePath%>pages/login/login.js"></script>
	<script type="text/javascript" src="<%=basePath%>js/ext-nantian.js"></script>
	 -->
	 
	<script type="text/javascript" src="js/des.js"></script>
	<script>
	var userIdLogin = "";
		<%
		try{
			if("admin".equals(userIdLogin.trim()) 
					|| "visitor".equals(userIdLogin.trim()) 
					|| "visitor1".equals(userIdLogin.trim()) 
					|| "visitor2".equals(userIdLogin.trim())){
				%>
				userIdLogin = "<%=userIdLogin%>";//生产
				<%
			}else{
				%>
				userIdLogin = decode("<%=userIdLogin%>");//生产
				<%
			}
		}catch(Exception e){}
		%> 
		//310000697、31000admi\0000808291
		//userIdLogin = "admin";//测试,passid登录(测试时打开)
		//alert(userIdLogin);
	</script>
	
	 <script type="text/javascript" src="js/ext/ext-all.js"></script>
	<script type="text/javascript" src="js/ext/ext-lang-zh_CN.js"></script>
	<script type="text/javascript" src="pages/login/login.js"></script>
	<script type="text/javascript" src="js/ext-nantian.js"></script>
	<script type="text/javascript" src="js/comm.js"></script>
	<script type="text/javascript" src="js/common_fun.js"></script>
	
	
	
	<%@ include file=".././public/include.jsp"%> 
  </head>

<body style="background-color:#EFF4FA">
	<div id="loginForm" style="border:0px solid red; width:200px;margin:auto;margin-top:150px"></div>
</body>
</html>
