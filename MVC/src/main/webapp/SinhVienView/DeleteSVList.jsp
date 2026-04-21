<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, Model.bean.SinhVien" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Quản lý xóa sinh viên</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
  <script>
    function confirmDelete(maSv, hoTen) {
      if (confirm("Bạn có chắc chắn muốn xóa sinh viên \"" + hoTen + "\" (Mã SV=" + maSv + ")?")) {
        window.location = "<%= request.getContextPath() %>/SinhVienServlet?action=deleteSV&id=" + maSv;
      }
    }
  </script>
</head>
<body>
  <h2>Danh sách sinh viên (xóa)</h2>

  <!-- Hiển thị thông báo nếu có -->
  <%
    String msg = (String) request.getAttribute("msg");
    if (msg != null) {
  %>
    <p style="color: green; font-weight: bold;"><%= msg %></p>
  <%
    }
  %>

  <table border="1" cellpadding="8">
    <tr>
      <th>Mã SV</th>
      <th>Họ tên</th>
      <th>Lớp</th>
      <th>Điểm TB</th>
      <th>Hành động</th>
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
        <button type="button"
                onclick="confirmDelete('<%= sv.getMaSv() %>', '<%= sv.getHoTen().replace("'", "\\'") %>')">
          Xóa
        </button>
      </td>
    </tr>
    <%
        }
      }
    %>
  </table>

  <div class="back-link">
    <a href="<%= request.getContextPath() %>/SinhVienServlet?action=list">← Quay lại danh sách sinh viên</a>
  </div>

  <div class="back-link">
    <a href="<%= request.getContextPath() %>/welcom.jsp">← Quay lại trang chủ</a>
  </div>
</body>
</html>
