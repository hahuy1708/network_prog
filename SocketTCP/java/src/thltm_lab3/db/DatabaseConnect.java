package thltm_lab3.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnect {
    private static final String URL = "jdbc:mysql://localhost:3307/QuanLyVatTu";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    private DatabaseConnect() {
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            throw new SQLException("MySQL JDBC Driver not found. Add mysql-connector-j to classpath.", ex);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
