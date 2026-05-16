package com.example.util;

import com.example.model.AssignmentResult;
import com.example.model.CanBo;
import com.example.model.PhanCong;
import com.example.model.PhongThi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

public class DatabaseManager {

        private static final String ENV_FILE = ".env";
        private static final String KEY_DB_HOST = "EXAM_DB_HOST";
        private static final String KEY_DB_PORT = "EXAM_DB_PORT";
        private static final String KEY_DB_NAME = "EXAM_DB_NAME";
        private static final String KEY_DB_USER = "EXAM_DB_USER";
        private static final String KEY_DB_PASS = "EXAM_DB_PASS";
        private static final String KEY_DB_URL = "EXAM_DB_URL";

        private static final Map<String, String> ENV = EnvLoader.loadFromWorkingDir(ENV_FILE);

        private static final String DB_HOST = getEnvOrDefault(KEY_DB_HOST, "localhost");
        private static final String DB_PORT = getEnvOrDefault(KEY_DB_PORT, "3306");
        private static final String DB_NAME = getEnvOrDefault(KEY_DB_NAME, "exam_db");
        private static final String DB_USER = getEnvOrDefault(KEY_DB_USER, "root");
        private static final String DB_PASS = getEnvOrDefault(KEY_DB_PASS, "your_password");
        private static final String DB_URL = resolveDbUrl();

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    private static String resolveDbUrl() {
        String override = getEnv(KEY_DB_URL);
        if (override != null && !override.isBlank()) {
            return override;
        }
        return "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME
                + "?useUnicode=true&characterEncoding=UTF-8"
                + "&useSSL=false&allowPublicKeyRetrieval=true"
                + "&serverTimezone=Asia/Ho_Chi_Minh";
    }

    private static String getEnvOrDefault(String key, String defaultValue) {
        return EnvLoader.getOrDefault(ENV, key, defaultValue);
    }

    private static String getEnv(String key) {
        return ENV.get(key);
    }


    public static void initDB() {
        String sqlPC = """
            CREATE TABLE IF NOT EXISTS phan_cong (
                id           INT AUTO_INCREMENT PRIMARY KEY,
                ma_gv        VARCHAR(50)  NOT NULL,
                ho_ten       VARCHAR(200),
                phong_thi    VARCHAR(50),
                vi_tri       INT,
                so_thu_tu_ca INT,
                thoi_gian    DATETIME DEFAULT CURRENT_TIMESTAMP
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """;

        String sqlGS = """
            CREATE TABLE IF NOT EXISTS giam_sat (
                id        INT AUTO_INCREMENT PRIMARY KEY,
                ma_gv     VARCHAR(50)  NOT NULL,
                ho_ten    VARCHAR(200),
                tu_phong  VARCHAR(50),
                den_phong VARCHAR(50),
                thoi_gian DATETIME DEFAULT CURRENT_TIMESTAMP
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlPC);
            stmt.execute(sqlGS);
            System.out.println("Database da san sang.");
        } catch (SQLException e) {
            System.err.println("Loi khoi tao DB: " + e.getMessage());
        }
    }

    public static void luuKetQua(AssignmentResult result) throws SQLException {
        initDB();

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                String sqlPC = """
                    INSERT INTO phan_cong (ma_gv, ho_ten, phong_thi, vi_tri, so_thu_tu_ca)
                    VALUES (?, ?, ?, ?, ?)
                    """;
                try (PreparedStatement ps = conn.prepareStatement(sqlPC)) {
                    for (PhanCong pc : result.getDanhSachPhanCong()) {
                        ps.setString(1, pc.getCanBo().getMaGV());
                        ps.setString(2, pc.getCanBo().getHoTen());
                        ps.setString(3, pc.getPhong().getMaPhong());
                        ps.setInt(4, pc.getViTri());
                        ps.setInt(5, pc.getSoThuTuCa());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }

                List<PhongThi> phongList = result.getDanhSachPhong();
                List<CanBo> giamSat = result.getGiamSatHanhLang();
                int soPhong = phongList.size();
                int soGS = giamSat.size();

                if (soGS > 0) {
                    String sqlGS = """
                        INSERT INTO giam_sat (ma_gv, ho_ten, tu_phong, den_phong)
                        VALUES (?, ?, ?, ?)
                        """;
                    try (PreparedStatement ps = conn.prepareStatement(sqlGS)) {
                        int phongPerGS = soPhong / soGS;
                        for (int i = 0; i < soGS; i++) {
                            CanBo cb = giamSat.get(i);
                            String tuPhong = "";
                            String denPhong = "";

                            if (phongPerGS > 0) {
                                int tuIdx = i * phongPerGS;
                                int denIdx = (i == soGS - 1) ? soPhong - 1 : (i + 1) * phongPerGS - 1;
                                if (tuIdx < soPhong) {
                                    tuPhong = phongList.get(tuIdx).getMaPhong();
                                    denPhong = phongList.get(Math.min(denIdx, soPhong - 1)).getMaPhong();
                                }
                            }

                            ps.setString(1, cb.getMaGV());
                            ps.setString(2, cb.getHoTen());
                            ps.setString(3, tuPhong);
                            ps.setString(4, denPhong);
                            ps.addBatch();
                        }
                        ps.executeBatch();
                    }
                }

                conn.commit();
                System.out.println("Da luu ket qua vao MySQL thanh cong.");
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Khong ket noi duoc MySQL: " + e.getMessage());
            return false;
        }
    }
}
