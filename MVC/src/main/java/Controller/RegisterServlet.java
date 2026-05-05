package Controller;

import javax.servlet.http.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;

import java.io.IOException;

import Model.bean.User;
import Model.bo.UserBO;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserBO userBO = new UserBO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String firstName = request.getParameter("firstname");
        String lastName = request.getParameter("lastname");
        String roles = request.getParameter("roles");

        if (roles == null || roles.trim().isEmpty()) {
            roles = "user";
        }

        if (isBlank(username) || isBlank(password)) {
            request.setAttribute("error", "Username and password are required.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        User user = new User(username.trim(), password, firstName, lastName, roles);
        boolean created = userBO.registerUser(user);

        if (!created) {
            request.setAttribute("error", "Username already exists.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/login.jsp?registered=1");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
