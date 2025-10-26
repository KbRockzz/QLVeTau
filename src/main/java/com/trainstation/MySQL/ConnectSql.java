package com.trainstation.MySQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectSql {
    private static ConnectSql instance;
    private Connection connection;
    
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
            // Load SQL Server JDBC driver
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            
            // Establish connection
            connection = DriverManager.getConnection(CONNECTION_URL, USERNAME, PASSWORD);
            System.out.println("Kết nối SQL Server thành công!");
        } catch (ClassNotFoundException e) {
            System.err.println("Không tìm thấy SQL Server JDBC Driver!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối SQL Server!");
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
     * Get database connection
     * @return Connection object
     */
    public Connection getConnection() {
        try {
            // Check if connection is closed or invalid
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(CONNECTION_URL, USERNAME, PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy kết nối!");
            e.printStackTrace();
        }
        return connection;
    }
    
    /**
     * Close database connection
     */
    public void closeConnection() {
    }
    
    /**
     * Test connection
     * @return true if connection is valid, false otherwise
     */
    public boolean testConnection() {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(5);
        } catch (SQLException e) {
            return false;
        }
    }
}
