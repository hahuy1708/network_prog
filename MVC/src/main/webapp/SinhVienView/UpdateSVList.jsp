<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.List, Model.bean.SinhVien"%>
<html>
<head>
  <meta charset="UTF-8">
  <title>Chọn sinh viên để cập nhật</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>

<body>
  <h2>Chọn sinh viên để cập nhật</h2>
  <table border="1" cellpadding="8">
    <tr>
      <th>Mã SV</th><th>Họ tên</th><th>Lớp</th><th>Điểm TB</th><th>Hành động</th>
    </tr>
    <%
      List<SinhVien> dsSV = (List<SinhVien>) request.getAttribute("dsSV");
      if (dsSV != null) {
        for (SinhVien sv : dsSV) {
    %>
    <tr>
      <td><%= sv.getMaSv() %></td>
      <td><%= sv.getHoTen() %></td>
      <td><%= sv.getLop() %></td>
      <td><%= sv.getDiemTb() %></td>
      <td>
        <a href="<%= request.getContextPath() %>/SinhVienServlet?action=editSV&id=<%= sv.getMaSv() %>">
          Cập nhật
        </a>
      </td>
    </tr>
    <%   }
      }
    %>
  </table>
  <p><a href="<%= request.getContextPath() %>/SinhVienServlet?action=list">← Quay lại danh sách sinh viên</a></p>
</body>
</html>
