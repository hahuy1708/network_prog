<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Thêm sinh viên</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    
</head>
<body>

    <h2>Thêm sinh viên mới</h2>

    <form method="post" action="<%= request.getContextPath() %>/SinhVienServlet" class="form-grid">
        <input type="hidden" name="action" value="insert">

        <label for="maSv">Mã sinh viên:</label>
        <input type="text" name="maSv" id="maSv" required maxlength="10">

        <label for="hoTen">Họ tên:</label>
        <input type="text" name="hoTen" id="hoTen" required>

	    <label for="lop">Lớp:</label>
	    <input type="text" name="lop" id="lop" required maxlength="20">

        <label for="diemTb">Điểm trung bình:</label>
        <input type="number" name="diemTb" id="diemTb" step="0.01" min="0" max="10" required>
		
        <input type="submit" value="Thêm sinh viên">
    </form>

    <div class="back-link">
        <a href="<%= request.getContextPath() %>/SinhVienServlet?action=list">← Quay lại danh sách</a>
    </div>
    
    <div class="back-link">
        <a href="<%= request.getContextPath() %>/welcom.jsp">← Quay lại trang chủ</a>
    </div>

</body>
</html>
