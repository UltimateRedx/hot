
<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>error</title>
</head>
<body>
<%--打印异常堆栈--%>
<%exception.printStackTrace();%>
系统错误，错误信息：<%=exception.getMessage()%><br>
</body>
</html>
