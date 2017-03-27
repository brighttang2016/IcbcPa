<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'test.jsp' starting page</title>
    
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
  function ajaxRequest(){
		var username="唐亮",password="200810405234";
		var data_send="";
	 	ajaxreq = new XMLHttpRequest();
		ajaxreq.open("post","AjaxControl",true);
	 	//ajaxreq.open("get","questions.xml");//直接请求服务器上的xml文件
		ajaxreq.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"); 
		ajaxreq.onreadystatechange = ajaxResponse;
		data_send = username+"|"+password;
		ajaxreq.send("username="+username+"&password="+password+"&data_send="+data_send);
		//ajaxreq.send(null);
	}
	function ajaxResponse(){
		var data_rec="";
		alert("调用ajaxResponse()   ajaxreq.readyState="+ajaxreq.readyState);
		if(ajaxreq.readyState ==4 )
		{
			if(ajaxreq.status == 200){ 
				/*
				 * 读取servlet返回的字符串信息
				data_rec = ajaxreq.responseText;  
				 alert("取得服务器返回字符串="+data_rec);
				*/
				//读取responseXML对象中的信息
				data_rec = ajaxreq.responseXML;
				alert(data_rec);
				/* alert(data_rec.getElementsByTagName("q")[0].childNodes[0].nodeName);
				alert(data_rec.getElementsByTagName("q")[0].childNodes[0].firstChild.data);
				alert(data_rec.getElementsByTagName("q")[0].childNodes[1].nodeName);
				alert(data_rec.getElementsByTagName("q")[0].childNodes[1].firstChild.nodeValue);//nodeValue 和data效果相同
				alert(data_rec.getElementsByTagName("q")[1].childNodes[0].nodeName);
				alert(data_rec.getElementsByTagName("q")[1].childNodes[0].nodeValue); */
				
			}
		}
	}
  
  </script>
  <body onload="ajaxRequest()">
    This is my JSP page. <br>
  </body>
</html>
