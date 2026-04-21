<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>

<html>
<head>
  <meta charset="UTF-8">
  <title>Cập nhật sinh viên</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>

<body>
  <h2>Cập nhật sinh viên</h2>
  <form action="<%= request.getContextPath() %>/SinhVienServlet" method="post" class="form-grid">
    <input type="hidden" name="action" value="update"/>

    <label>Mã sinh viên:</label>
    <input type="text" name="maSv" value="${sinhVien.maSv}" readonly />

    <label>Họ tên:</label>
    <input type="text" name="hoTen" value="${sinhVien.hoTen}" required/>

    <label>Lớp:</label>
    <input type="text" name="lop" value="${sinhVien.lop}" required/>

    <label>Điểm trung bình:</label>
    <input type="number" name="diemTb" value="${sinhVien.diemTb}" step="0.01" min="0" max="10" required/>

    <input type="submit" value="Cập nhật"/>
  </form>

  <div class="back-link">
    <a href="<%= request.getContextPath() %>/SinhVienServlet?action=editSV">← Quay lại</a>
  </div>
</body>
</html>
