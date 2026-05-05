package Controller;

import javax.servlet.http.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

import Model.bo.UserBO;
import Model.bean.User;

@WebServlet("/UserServlet")
public class UserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserBO userBO = new UserBO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isAuthenticated(request, response)) {
            return;
        }

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null) action = "search";

        switch (action) {
            case "search":
                forwardSearch(request, response);
                break;

            case "edit":
                String username = request.getParameter("username");
                if (username == null || username.trim().isEmpty()) {
                    response.sendRedirect(request.getContextPath() + "/UserServlet?action=search");
                    return;
                }
                User editUser = userBO.getUserByUsername(username);
                if (editUser == null) {
                    response.sendRedirect(request.getContextPath() + "/UserServlet?action=search");
                    return;
                }
                request.setAttribute("editUser", editUser);
                request.setAttribute("keyword", request.getParameter("keyword"));
                request.getRequestDispatcher("/UserView/FormUpdateUser.jsp").forward(request, response);
                break;

            case "delete":
                String userDelete = request.getParameter("username");
                if (userDelete != null && !userDelete.trim().isEmpty()) {
                    userBO.deleteUser(userDelete);
                }
                redirectToSearch(request, response);
                break;

            default:
                response.sendRedirect(request.getContextPath() + "/UserServlet?action=search");
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
        if (action == null) action = "";

        switch (action) {
            case "update":
                String username = request.getParameter("username");
                if (username == null || username.trim().isEmpty()) {
                    response.sendRedirect(request.getContextPath() + "/UserServlet?action=search");
                    return;
                }
                String firstName = request.getParameter("firstname");
                String lastName = request.getParameter("lastname");
                String roles = request.getParameter("roles");

                if (roles == null || roles.trim().isEmpty()) {
                    roles = "user";
                }

                User updateUser = new User(username, firstName, lastName, roles);
                userBO.updateUser(updateUser);
                redirectToSearch(request, response);
                break;

            case "search":
                forwardSearch(request, response);
                break;

            default:
                response.sendRedirect(request.getContextPath() + "/UserServlet?action=search");
                break;
        }
    }

    private void forwardSearch(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        ArrayList<User> users = userBO.searchUsers(keyword);
        request.setAttribute("users", users);

        if (keyword != null && !keyword.trim().isEmpty() && users.isEmpty()) {
            request.setAttribute("msg", "No Result is matched!");
        }

        request.getRequestDispatcher("/welcom.jsp").forward(request, response);
    }

    private void redirectToSearch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String keyword = request.getParameter("keyword");
        StringBuilder redirectUrl = new StringBuilder(request.getContextPath())
                .append("/UserServlet?action=search");

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

    private boolean isAuthenticated(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return false;
        }
        return true;
    }
}
