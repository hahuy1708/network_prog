<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="Model.bean.SinhVien" %>

<html>
<head>
    <title>Danh sách sinh viên</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    
</head>
<body>
    <h2>Danh sách sinh viên</h2>
    <table border="1" cellpadding="10">
        <tr>
            <th>Mã SV</th>
            <th>Họ tên</th>
            <th>Lớp</th>
            <th>Điểm TB</th>
        </tr>
        <%
            ArrayList<SinhVien> dsSV = (ArrayList<SinhVien>) request.getAttribute("dsSV");
            if (dsSV != null) {
                for (SinhVien sv : dsSV) {
        %>
        <tr>
            <td><%= sv.getMaSv() %></td>
            <td><%= sv.getHoTen() %></td>
            <td><%= sv.getLop() %></td>
            <td><%= sv.getDiemTb() %></td>
        </tr>
        <%
                }
            }
        %>
    </table>
    <div class="back-link">
        <a href="<%= request.getContextPath() %>/welcom.jsp">← Quay lại trang chủ</a>
    </div>
</body>
</html>
