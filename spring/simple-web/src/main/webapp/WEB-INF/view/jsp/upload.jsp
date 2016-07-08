<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="zh">
<head>
<base href="<%=basePath %>">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="Keywords" content=""/>
<meta name="Description" content=""/>
<link rel="stylesheet" type="text/css" href="static/css/styles.css" />
<title>UPLOAD</title>
<!-- 
<script src="js/jquery.js"></script>
<script type="text/javascript"></script>
-->
</head>
<body>

<h2>upload</h2><br />

<%=request.getAttribute("count") %>


<form action="mvc/app/upload" method="POST" enctype="multipart/form-data">
	user name: <input type="text" name="username"/><br/>
	file 1 : <input type="file" name="files"/><br/>
	file 2 : <input type="file" name="files"/><br/>
	file 3 : <input type="file" name="files"/><br/>
	<input type="submit" value="提交上传文件"/>
</form>

</body>
</html>