<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Model.bean.User" %>
<%
    String sessionUser = (String) session.getAttribute("user");
    User editUser = (User) request.getAttribute("editUser");
    String keyword = (String) request.getAttribute("keyword");
    if (keyword == null) {
        keyword = request.getParameter("keyword");
    }
    String keywordEncoded = "";
    if (keyword != null) {
        try {
            keywordEncoded = java.net.URLEncoder.encode(keyword, "UTF-8");
        } catch (Exception ex) {
            keywordEncoded = keyword;
        }
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Update User</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <div class="page-card">
        <h2>Update User</h2>
        <%
            if (sessionUser != null) {
        %>
            <p>Welcome, <strong><%= sessionUser %></strong></p>
        <%
            }
        %>
        <form action="<%= request.getContextPath() %>/UserServlet" method="post" class="form-grid">
            <input type="hidden" name="action" value="update"/>
            <input type="hidden" name="keyword" value="<%= keyword != null ? keyword : "" %>"/>

            <label for="username">Username:</label>
            <input type="text" id="username" name="username" value="<%= editUser != null ? editUser.getUsername() : "" %>" readonly/>

            <label for="firstname">First name:</label>
            <input type="text" id="firstname" name="firstname" value="<%= editUser != null ? editUser.getFirstName() : "" %>" required/>

            <label for="lastname">Last name:</label>
            <input type="text" id="lastname" name="lastname" value="<%= editUser != null ? editUser.getLastName() : "" %>" required/>

            <label for="roles">Roles:</label>
            <input type="text" id="roles" name="roles" value="<%= editUser != null ? editUser.getRoles() : "user" %>" required/>

            <div class="action-row">
                <input type="submit" value="Update">
                <input type="reset" value="Reset" class="btn-secondary">
            </div>
        </form>

        <div class="back-link">
            <a href="<%= request.getContextPath() %>/UserServlet?action=search<%= (keyword != null && !keyword.isEmpty()) ? "&keyword=" + keywordEncoded : "" %>">Back to search</a>
        </div>
    </div>
</body>
</html>
