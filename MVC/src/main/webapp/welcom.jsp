<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="Model.bean.User" %>
<%
    String sessionUser = (String) session.getAttribute("user");
    String keyword = request.getParameter("keyword");
    String msg = (String) request.getAttribute("msg");
    List<User> users = (List<User>) request.getAttribute("users");

    String keywordEncoded = "";
    if (keyword != null) {
        try {
            keywordEncoded = java.net.URLEncoder.encode(keyword, "UTF-8");
        } catch (Exception ex) {
            keywordEncoded = keyword;
        }
    }

    String keywordQuery = "";
    if (keyword != null && !keyword.trim().isEmpty()) {
        keywordQuery = "&keyword=" + keywordEncoded;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>User Search</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    <script>
        function confirmDelete(username) {
            var keyword = "<%= keywordEncoded %>";
            var url = "<%= request.getContextPath() %>/UserServlet?action=delete&username=" + encodeURIComponent(username);
            if (keyword && keyword.length > 0) {
                url += "&keyword=" + keyword;
            }
            if (confirm("Delete user \"" + username + "\"?")) {
                window.location = url;
            }
        }
    </script>
</head>
<body>
    <div class="page-card">
        <h2>User Search</h2>
        <%
            if (sessionUser != null) {
        %>
            <p>Welcome, <strong><%= sessionUser %></strong></p>

            <form action="<%= request.getContextPath() %>/UserServlet" method="get" class="form-grid">
                <input type="hidden" name="action" value="search"/>

                <label for="keyword">Search by name:</label>
                <input type="text" id="keyword" name="keyword" value="<%= keyword != null ? keyword : "" %>"/>

                <div class="action-row">
                    <input type="submit" value="Search"/>
                    <input type="button" value="Reset" class="btn-secondary"
                           onclick="window.location='<%= request.getContextPath() %>/UserServlet?action=search'"/>
                </div>
            </form>

            <%
                if (msg != null) {
            %>
                <p class="error-msg"><%= msg %></p>
            <%
                }
            %>

            <%
                if (users != null) {
            %>
                <table border="1" cellpadding="8">
                    <tr>
                        <th>Username</th>
                        <th>First name</th>
                        <th>Last name</th>
                        <th>Roles</th>
                        <th>Action</th>
                    </tr>
                    <%
                        for (User user : users) {
                    %>
                        <tr>
                            <td><%= user.getUsername() %></td>
                            <td><%= user.getFirstName() %></td>
                            <td><%= user.getLastName() %></td>
                            <td><%= user.getRoles() %></td>
                            <td>
                                <a href="<%= request.getContextPath() %>/UserServlet?action=edit&username=<%= user.getUsername() %><%= keywordQuery %>">Update</a>
                                |
                                <a href="#" onclick="confirmDelete('<%= user.getUsername() %>'); return false;">Delete</a>
                            </td>
                        </tr>
                    <%
                        }
                    %>
                </table>
            <%
                }
            %>

            <div class="back-link">
                <a href="<%= request.getContextPath() %>/CheckLoginServlet?action=logout">Logout</a>
            </div>
        <%
            } else {
        %>
            <p>Please <a href="<%= request.getContextPath() %>/login.jsp">login</a>.</p>
        <%
            }
        %>
    </div>
</body>
</html>
