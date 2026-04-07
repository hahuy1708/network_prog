package lab4.server;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public final class DBUtil {
    private static final String PROPERTIES_FILE = "db.properties";

    private DBUtil() {
    }

    public static Connection getConnection() throws Exception {
        Properties props = new Properties();

        try (InputStream in = new FileInputStream(PROPERTIES_FILE)) {
            props.load(in);
        }

        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(
                props.getProperty("db.url"),
                props.getProperty("db.user"),
                props.getProperty("db.password"));
    }
}
