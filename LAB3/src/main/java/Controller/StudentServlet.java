package Controller;

import javax.servlet.http.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

import Model.bo.StudentBO;
import Model.bean.Student;

@WebServlet("/StudentServlet")
public class StudentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private StudentBO studentBO = new StudentBO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isAuthenticated(request, response)) {
            return;
        }

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null) {
            action = "search";
        }

        switch (action) {
            case "search":
                forwardSearch(request, response);
                break;

            case "add":
                showAddForm(request, response);
                break;

            case "edit":
                showEditForm(request, response);
                break;

            case "delete":
                handleDelete(request, response);
                break;

            default:
                response.sendRedirect(request.getContextPath() + "/StudentServlet?action=search");
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isAuthenticated(request, response)) {
            return;
        }

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }

        switch (action) {
            case "insert":
                handleInsert(request, response);
                break;

            case "update":
                handleUpdate(request, response);
                break;

            case "search":
                forwardSearch(request, response);
                break;

            default:
                response.sendRedirect(request.getContextPath() + "/StudentServlet?action=search");
                break;
        }
    }

    private void forwardSearch(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        ArrayList<Student> students = studentBO.searchStudents(keyword);
        request.setAttribute("students", students);
        request.setAttribute("keyword", keyword);

        if (keyword != null && !keyword.trim().isEmpty() && students.isEmpty()) {
            request.setAttribute("msg", "No results found.");
        }

        request.getRequestDispatcher("/student_list.jsp").forward(request, response);
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("formMode", "insert");
        request.setAttribute("keyword", request.getParameter("keyword"));
        request.getRequestDispatcher("/StudentView/StudentForm.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = parseInt(request.getParameter("id"));
        if (id <= 0) {
            redirectToSearch(request, response);
            return;
        }

        Student student = studentBO.getStudentById(id);
        if (student == null) {
            redirectToSearch(request, response);
            return;
        }

        request.setAttribute("formMode", "update");
        request.setAttribute("student", student);
        request.setAttribute("keyword", request.getParameter("keyword"));
        request.getRequestDispatcher("/StudentView/StudentForm.jsp").forward(request, response);
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = parseInt(request.getParameter("id"));
        if (id > 0) {
            studentBO.deleteStudent(id);
        }
        redirectToSearch(request, response);
    }

    private void handleInsert(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Student student = buildStudentFromRequest(request);
        if (!isValidStudent(student, false)) {
            request.setAttribute("formMode", "insert");
            request.setAttribute("keyword", request.getParameter("keyword"));
            request.setAttribute("student", student);
            request.setAttribute("error", "Name, age, and university are required.");
            request.getRequestDispatcher("/StudentView/StudentForm.jsp").forward(request, response);
            return;
        }

        studentBO.insertStudent(student);
        redirectToSearch(request, response);
    }

    private void handleUpdate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Student student = buildStudentFromRequest(request);
        if (!isValidStudent(student, true)) {
            request.setAttribute("formMode", "update");
            request.setAttribute("keyword", request.getParameter("keyword"));
            request.setAttribute("student", student);
            request.setAttribute("error", "Valid student id, name, age, and university are required.");
            request.getRequestDispatcher("/StudentView/StudentForm.jsp").forward(request, response);
            return;
        }

        studentBO.updateStudent(student);
        redirectToSearch(request, response);
    }

    private Student buildStudentFromRequest(HttpServletRequest request) {
        int id = parseInt(request.getParameter("id"));
        String name = trimValue(request.getParameter("name"));
        String university = trimValue(request.getParameter("university"));
        Integer ageValue = parseIntOrNull(request.getParameter("age"));
        int age = (ageValue != null) ? ageValue : 0;
        return new Student(id, name, age, university);
    }

    private boolean isValidStudent(Student student, boolean requireId) {
        if (student == null) {
            return false;
        }
        if (requireId && student.getId() <= 0) {
            return false;
        }
        if (isBlank(student.getName()) || isBlank(student.getUniversity())) {
            return false;
        }
        return student.getAge() > 0;
    }

    private void redirectToSearch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String keyword = request.getParameter("keyword");
        StringBuilder redirectUrl = new StringBuilder(request.getContextPath())
                .append("/StudentServlet?action=search");

        if (keyword != null && !keyword.trim().isEmpty()) {
            redirectUrl.append("&keyword=").append(encodeParam(keyword.trim()));
        }

        response.sendRedirect(redirectUrl.toString());
    }

    private String encodeParam(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (Exception ex) {
            return value;
        }
    }

    private int parseInt(String value) {
        if (value == null) {
            return -1;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private Integer parseIntOrNull(String value) {
        if (value == null) {
            return null;
        }
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String trimValue(String value) {
        return value == null ? null : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private boolean isAuthenticated(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return false;
        }
        return true;
    }
}
