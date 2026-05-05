<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="Model.bean.Student" %>
<%
    String sessionUser = (String) session.getAttribute("user");
    String keyword = request.getParameter("keyword");
    if (keyword == null) {
        keyword = (String) request.getAttribute("keyword");
    }
    String msg = (String) request.getAttribute("msg");
    List<Student> students = (List<Student>) request.getAttribute("students");

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
    <title>Student Manager</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    <script>
        function confirmDelete(id) {
            var keyword = "<%= keywordEncoded %>";
            var url = "<%= request.getContextPath() %>/StudentServlet?action=delete&id=" + encodeURIComponent(id);
            if (keyword && keyword.length > 0) {
                url += "&keyword=" + keyword;
            }
            if (confirm("Delete student " + id + "?")) {
                window.location = url;
            }
        }
    </script>
</head>
<body>
    <div class="page-card">
        <h2>Student Manager</h2>
        <%
            if (sessionUser != null) {
        %>
            <p>Welcome, <strong><%= sessionUser %></strong></p>

            <form action="<%= request.getContextPath() %>/StudentServlet" method="get" class="form-grid">
                <input type="hidden" name="action" value="search"/>

                <label for="keyword">Search by id, name, or university:</label>
                <input type="text" id="keyword" name="keyword" value="<%= keyword != null ? keyword : "" %>"/>

                <div class="action-row">
                    <input type="submit" value="Search"/>
                    <button type="button"
                            onclick="window.location='<%= request.getContextPath() %>/StudentServlet?action=add<%= keywordQuery %>'">Add student</button>
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
                if (students != null) {
            %>
                <table border="1" cellpadding="8">
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Age</th>
                        <th>University</th>
                        <th>Action</th>
                    </tr>
                    <%
                        for (Student student : students) {
                    %>
                        <tr>
                            <td><%= student.getId() %></td>
                            <td><%= student.getName() %></td>
                            <td><%= student.getAge() %></td>
                            <td><%= student.getUniversity() %></td>
                            <td>
                                <a href="<%= request.getContextPath() %>/StudentServlet?action=edit&id=<%= student.getId() %><%= keywordQuery %>">Update</a>
                                |
                                <a href="#" onclick="confirmDelete('<%= student.getId() %>'); return false;">Delete</a>
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
