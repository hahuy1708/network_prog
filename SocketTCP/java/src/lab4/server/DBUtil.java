package lab4.server;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBUtil {
    public static Connection getConnection() throws Exception {
        Properties props = new Properties();
        
        try (InputStream in = new FileInputStream("db.properties")) {
            props.load(in);
        } catch (Exception e) {
            System.err.println("Lỗi: Không tìm thấy file db.properties tại thư mục gốc!");
            throw e;
        }

        Class.forName("com.mysql.cj.jdbc.Driver");

        return DriverManager.getConnection(
            props.getProperty("db.url"),
            props.getProperty("db.user"),
            props.getProperty("db.password")
        );
    }
}