package com.trainstation.MySQL;

import com.trainstation.persistence.JpaEntityManagerProvider;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.sql.Connection;
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

    private ConnectSql() {
    }

    public static synchronized ConnectSql getInstance() {
        if (instance == null) {
            instance = new ConnectSql();
        }
        return instance;
    }

    /**
     * Returns a JDBC {@link Connection} backed by persistence context.
     * <p>
     * Always close this connection (try-with-resources) so the underlying EntityManager is released.
     */
    public Connection getConnection() {
        EntityManager em = JpaEntityManagerProvider.createEntityManager();
        try {
            Session session = em.unwrap(Session.class);
            // Keep legacy JDBC callers working while routing connection acquisition through persistence.
            Connection rawConnection = session.doReturningWork(connection -> connection);
            return wrapJpaManagedConnection(rawConnection, em);
        } catch (Exception e) {
            if (em.isOpen()) {
                em.close();
            }
            System.err.println("Lỗi khi lấy kết nối!");
            e.printStackTrace();
            throw new RuntimeException("Không thể tạo kết nối tới DB", e);
        }
    }

    private Connection wrapJpaManagedConnection(Connection rawConnection, EntityManager em) {
        return (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class[]{Connection.class},
                (proxy, method, args) -> {
                    String methodName = method.getName();
                    if ("close".equals(methodName)) {
                        if (em.isOpen()) {
                            em.close();
                        }
                        return null;
                    }
                    if ("isClosed".equals(methodName)) {
                        return !em.isOpen() ? true : (boolean) method.invoke(rawConnection, args);
                    }
                    if (!em.isOpen()) {
                        throw new SQLException("Cannot perform operation: EntityManager is closed");
                    }
                    try {
                        return method.invoke(rawConnection, args);
                    } catch (InvocationTargetException ex) {
                        throw ex.getCause();
                    }
                }
        );
    }

    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && conn.isValid(5);
        } catch (Exception e) {
            return false;
        }
    }
}
