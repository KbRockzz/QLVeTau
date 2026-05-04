package com.trainstation.MySQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * ConnectSql: cung cấp Connection mới mỗi lần gọi getConnection().
 * Đã chuyển sang MariaDB JDBC driver.
 *
 * Lưu ý:
 * - Caller phải đóng Connection sau khi dùng (ví dụ bằng try-with-resources).
 * - Khi cần tối ưu hiệu năng, chuyển sang DataSource/connection pool (HikariCP).
 */
public class ConnectSql {
    private static ConnectSql instance;

    // MariaDB connection parameters
    private static final String SERVER = "localhost";
    private static final String PORT = "3306";
    private static final String DATABASE = "QLTauHoa";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "rootpassword";

    // Connection string for MariaDB
    private static final String CONNECTION_URL =
            "jdbc:mariadb://" + SERVER + ":" + PORT + "/" + DATABASE
                    + "?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh";

    private ConnectSql() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Không tìm thấy MariaDB JDBC Driver!");
            e.printStackTrace();
        }
    }

    public static synchronized ConnectSql getInstance() {
        if (instance == null) {
            instance = new ConnectSql();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(CONNECTION_URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy kết nối!");
            e.printStackTrace();
            throw new RuntimeException("Không thể tạo kết nối tới DB", e);
        }
    }

    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && conn.isValid(5);
        } catch (Exception e) {
            return false;
        }
    }
}