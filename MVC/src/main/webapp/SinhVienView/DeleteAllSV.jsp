<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, Model.bean.SinhVien" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Xóa nhiều sinh viên</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">

  <script>
    function checkSubmit() {
      let checked = document.querySelectorAll('input[name="maSv"]:checked');
      if (checked.length === 0) {
        alert('Bạn chưa chọn sinh viên nào!');
        return false;
      }
      return confirm('Bạn có chắc muốn xóa ' + checked.length + ' sinh viên đã chọn?');
    }
  </script>
</head>
<body>
  <h2>Xóa nhiều sinh viên</h2>

  <% String msg = (String) request.getAttribute("msg");
     if (msg != null) { %>
    <p style="color: green; font-weight: bold;"><%= msg %></p>
  <% } %>

  <form method="post" action="<%= request.getContextPath() %>/SinhVienServlet" onsubmit="return checkSubmit();">
    <input type="hidden" name="action" value="deleteAll"/>

    <table border="1" cellpadding="8">
      <tr>
        <th>Chọn</th><th>Mã SV</th><th>Họ tên</th><th>Lớp</th><th>Điểm TB</th>
      </tr>
      <%
        List<SinhVien> dsSV = (List<SinhVien>) request.getAttribute("dsSV");
        if (dsSV != null) {
          for (SinhVien sv : dsSV) {
      %>
        <tr>
          <td><input type="checkbox" name="maSv" value="<%= sv.getMaSv() %>"/></td>
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
    <br/>
    <button type="submit">Xóa các sinh viên đã chọn</button>
  </form>

  <p><a href="<%= request.getContextPath() %>/SinhVienServlet?action=list">← Quay lại danh sách sinh viên</a></p>
  <p><a href="<%= request.getContextPath() %>/welcom.jsp">← Quay lại trang chủ</a></p>
</body>
</html>
