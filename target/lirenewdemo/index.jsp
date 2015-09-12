<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="js/jquery-1.11.3.min.js"></script>
<script type="text/javascript" src="js/imageutil.js"></script>
<title>Insert title here</title>
</head>
<body>
	<input value="创建索引" type="button" onclick="createImageIndex()"/>
	<input value="拷贝图片" type="button" onclick="copyImages()"/>
	<p id="info"></p>
	<a href="<%=request.getContextPath() %>/linkFindSimilarImage" target="_blank">去查找相似图片</a>
</body>
</html>