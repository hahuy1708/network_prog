<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Model.bean.Student" %>
<%
    String sessionUser = (String) session.getAttribute("user");
    Student student = (Student) request.getAttribute("student");
    String formMode = (String) request.getAttribute("formMode");
    String keyword = (String) request.getAttribute("keyword");
    String error = (String) request.getAttribute("error");

    boolean isEdit = "update".equals(formMode);
    if (!isEdit && student != null && student.getId() > 0) {
        isEdit = true;
    }

    String title = isEdit ? "Update Student" : "Add Student";
    String nameValue = (student != null && student.getName() != null) ? student.getName() : "";
    String universityValue = (student != null && student.getUniversity() != null) ? student.getUniversity() : "";
    String ageValue = "";
    if (student != null && student.getAge() > 0) {
        ageValue = String.valueOf(student.getAge());
    }

    String keywordEncoded = "";
    if (keyword != null) {
        try {
            keywordEncoded = java.net.URLEncoder.encode(keyword, "UTF-8");
        } catch (Exception ex) {
            keywordEncoded = keyword;
        }
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><%= title %></title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <div class="page-card">
        <h2><%= title %></h2>
        <%
            if (sessionUser != null) {
        %>
            <p>Welcome, <strong><%= sessionUser %></strong></p>
        <%
            }
        %>
        <%
            if (error != null) {
        %>
            <p class="error-msg"><%= error %></p>
        <%
            }
        %>
        <form action="<%= request.getContextPath() %>/StudentServlet" method="post" class="form-grid">
            <input type="hidden" name="action" value="<%= isEdit ? "update" : "insert" %>"/>
            <input type="hidden" name="keyword" value="<%= keyword != null ? keyword : "" %>"/>

            <%
                if (isEdit) {
            %>
                <label for="id">ID:</label>
                <input type="text" id="id" name="id" value="<%= student != null ? student.getId() : "" %>" readonly/>
            <%
                }
            %>

            <label for="name">Name:</label>
            <input type="text" id="name" name="name" value="<%= nameValue %>" required/>

            <label for="age">Age:</label>
            <input type="number" id="age" name="age" min="1" value="<%= ageValue %>" required/>

            <label for="university">University:</label>
            <input type="text" id="university" name="university" value="<%= universityValue %>" required/>

            <div class="action-row">
                <input type="submit" value="<%= isEdit ? "Update" : "Create" %>"/>
                <input type="reset" value="Reset" class="btn-secondary"/>
            </div>
        </form>

        <div class="back-link">
            <a href="<%= request.getContextPath() %>/StudentServlet?action=search<%= (keyword != null && !keyword.trim().isEmpty()) ? "&keyword=" + keywordEncoded : "" %>">Back to list</a>
        </div>
    </div>
</body>
</html>
