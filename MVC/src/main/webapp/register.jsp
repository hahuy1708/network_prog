<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String error = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Register</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body class="auth-page">
    <div class="page-card">
        <h2>Create Account</h2>
        <%
            if (error != null) {
        %>
            <p class="error-msg"><%= error %></p>
        <%
            }
        %>
        <form action="<%= request.getContextPath() %>/RegisterServlet" method="post" class="form-grid">
            <label for="username">Username:</label>
            <input type="text" id="username" name="username" required>

            <label for="password">Password:</label>
            <input type="password" id="password" name="password" required>

            <label for="firstname">First name:</label>
            <input type="text" id="firstname" name="firstname" required>

            <label for="lastname">Last name:</label>
            <input type="text" id="lastname" name="lastname" required>

            <label for="roles">Roles:</label>
            <input type="text" id="roles" name="roles" value="user" required>

            <div class="action-row">
                <input type="submit" value="Register">
                <a href="<%= request.getContextPath() %>/login.jsp">Back to login</a>
            </div>
        </form>
    </div>
</body>
</html>
