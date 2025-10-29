package com.trainstation.MySQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * ConnectSql: cung cấp Connection mới mỗi lần gọi getConnection().
 * Tránh giữ 1 Connection singleton chia sẻ giữa nhiều DAO để không bị lỗi "The connection is closed".
 *
 * Lưu ý:
 * - Caller phải đóng Connection sau khi dùng (ví dụ bằng try-with-resources).
 * - Khi cần tối ưu hiệu năng, chuyển sang DataSource/connection pool (HikariCP).
 */
public class ConnectSql {
    private static ConnectSql instance;

    // SQL Server connection parameters
    private static final String SERVER = "localhost";
    private static final String PORT = "1433";
    private static final String DATABASE = "QLTauHoa";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "sapassword";

    // Connection string for SQL Server
    private static final String CONNECTION_URL =
            "jdbc:sqlserver://" + SERVER + ":" + PORT +
                    ";databaseName=" + DATABASE +
                    ";encrypt=true;trustServerCertificate=true";

    private ConnectSql() {
        try {
            // Load SQL Server JDBC driver once
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("Không tìm thấy SQL Server JDBC Driver!");
            e.printStackTrace();
        }
    }

    /**
     * Get singleton instance of ConnectSql
     * @return ConnectSql instance
     */
    public static synchronized ConnectSql getInstance() {
        if (instance == null) {
            instance = new ConnectSql();
        }
        return instance;
    }

    /**
     * Get a new database connection.
     * IMPORTANT: caller phải đóng Connection sau khi dùng (try-with-resources).
     * @return a new Connection
     * @throws RuntimeException if cannot get connection
     */
    public Connection getConnection() {
        try {
            // Always return a fresh connection (or from pool in future)
            return DriverManager.getConnection(CONNECTION_URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy kết nối!");
            e.printStackTrace();
            throw new RuntimeException("Không thể tạo kết nối tới DB", e);
        }
    }

    /**
     * Helper test connection: thử mở và đóng ngay để kiểm tra cấu hình.
     * @return true nếu có thể kết nối
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && conn.isValid(5);
        } catch (Exception e) {
            return false;
        }
    }
}