<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<html>
<head><title>Welcome</title></head>
<body>
    <%
        // Nhận lại thông tin từ session [17]
        String user = (String) session.getAttribute("user");
        String address = (String) session.getAttribute("address");
    %>
    
    
    <h1>Xin chào: <%= user %></h1> 
    <p>Địa chỉ của bạn: <%= address %></p>
</body>
</html>
