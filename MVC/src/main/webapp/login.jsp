<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
    String error = request.getParameter("error");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Đăng nhập</title>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">

</head>
<body class="auth-page">
	<div class="page-card">
		<h2>Đăng nhập hệ thống</h2>
		<%
			if ("1".equals(error)) {
		%>
			<p class="error-msg">Sai tên đăng nhập hoặc mật khẩu.</p>
		<%
			}
		%>
		<form action="<%= request.getContextPath() %>/CheckLoginServlet" method="post" class="form-grid">
			<label for="username">Username:</label>
			<input type="text" id="username" name="username" required>

			<label for="password">Password:</label>
			<input type="password" id="password" name="password" required>

			<div class="action-row">
				<input type="submit" value="Login">
				<input type="reset" value="Reset" class="btn-secondary">
			</div>
		</form>
	</div>

</body>
</html>