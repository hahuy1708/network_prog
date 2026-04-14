<%@ page import="java.sql.*" %>
<%
request.setCharacterEncoding("UTF-8");

String user = request.getParameter("user");
String pass = request.getParameter("password");
String address = "193nlb";


try {
    Class.forName("com.mysql.cj.jdbc.Driver");

    String url = "jdbc:mysql://localhost:3307/dulieu1?useSSL=false&serverTimezone=UTC";

    Connection conn = DriverManager.getConnection(url, "root", "123456");

    String sql = "SELECT * FROM users WHERE username=? AND password=?";
    PreparedStatement ps = conn.prepareStatement(sql);
    ps.setString(1, user);
    ps.setString(2, pass);

    ResultSet rs = ps.executeQuery();

    if(rs.next()){
        session.setAttribute("user", user);
        session.setAttribute("address", address);
        response.sendRedirect("welcome.jsp");
    }else{
        response.sendRedirect("login.jsp");
    }

} catch(Exception e){
    out.println(e.toString());
}
%>