<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String user = (String) session.getAttribute("user");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Trang chủ</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    
</head>
<body>
    <div class="page-card">
        <h2>Trang Home</h2>
        <%
            if (user != null) {
        %>
            <p>Chào mừng, <strong><%= user %></strong>!</p>

            <h3>Chức năng sinh viên</h3>
            <ul class="menu-list">
                <li><a href="<%= request.getContextPath() %>/SinhVienServlet?action=addSV">Thêm sinh viên</a></li>
                <li><a href="<%= request.getContextPath() %>/SinhVienServlet?action=editSV">Cập nhật sinh viên</a></li>
                <li><a href="<%= request.getContextPath() %>/SinhVienServlet?action=deleteSV">Xóa sinh viên</a></li>
                <li><a href="<%= request.getContextPath() %>/SinhVienServlet?action=deleteAll">Xóa nhiều sinh viên</a></li>
                <li><a href="<%= request.getContextPath() %>/SinhVienServlet?action=list">Danh sách sinh viên</a></li>
            </ul>

            <div class="back-link">
                <a href="<%= request.getContextPath() %>/CheckLoginServlet?action=logout">Đăng xuất</a>
            </div>
        <%
            } else {
        %>
            <p>Bạn chưa đăng nhập. Vui lòng <a href="<%= request.getContextPath() %>/login.jsp">đăng nhập</a>.</p>
        <%
            }
        %>
    </div>
</body>
</html>
