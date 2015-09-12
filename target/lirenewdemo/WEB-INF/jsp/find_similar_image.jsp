<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<form action="<%=request.getContextPath() %>/findSimilarImage" enctype="multipart/form-data" method="post">
		<input id="doc" type="file" name="doc"/>
		<button type="submit">查找</button>
	</form>
	${error_message}
	<c:forEach items="${similarImage}" var="image">
		<p>${image.score}</p>
		<p><img src="<%=request.getContextPath() %>/src/imgfiles/${image.imagepath}" alt="" /></p>
	</tr>
	</c:forEach>
</body>
</html>