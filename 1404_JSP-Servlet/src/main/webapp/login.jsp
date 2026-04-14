<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Login</title>
</head>
<body>
	<form method="post" action="checklogin.jsp">
        Username: <input type="text" name="user"><br>
        Password: <input type="password" name="password"><br>
        <input type="submit" value="Login">
        <input type="reset" value="Cancel">
    </form>

</body>
</html>