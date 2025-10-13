package com.trainstation.dao;

import com.trainstation.model.Account;
import com.trainstation.model.Employee;
import com.trainstation.MySQL.ConnectSql;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO implements GenericDAO<Account> {
    private static AccountDAO instance;
    private Connection connection;

    private AccountDAO() {
        connection = ConnectSql.getInstance().getConnection();
    }

    public static synchronized AccountDAO getInstance() {
        if (instance == null) {
            instance = new AccountDAO();
        }
        return instance;
    }

    @Override
    public void add(Account account) {
        String sql = "INSERT INTO Account (Username, Password, EmployeeID, Role, IsActive) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, account.getUsername());
            pstmt.setString(2, account.getPassword());
            pstmt.setString(3, account.getEmployeeId());
            pstmt.setString(4, account.getRole());
            pstmt.setBoolean(5, account.isActive());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Account account) {
        String sql = "UPDATE Account SET Password = ?, EmployeeID = ?, Role = ?, IsActive = ? WHERE Username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, account.getPassword());
            pstmt.setString(2, account.getEmployeeId());
            pstmt.setString(3, account.getRole());
            pstmt.setBoolean(4, account.isActive());
            pstmt.setString(5, account.getUsername());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String username) {
        String sql = "DELETE FROM Account WHERE Username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Account findById(String username) {
        String sql = "SELECT a.*, e.MaLoai FROM Account a LEFT JOIN Employee e ON a.EmployeeID = e.EmployeeID WHERE a.Username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractAccountFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Account> findAll() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT a.*, e.MaLoai FROM Account a LEFT JOIN Employee e ON a.EmployeeID = e.EmployeeID";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                accounts.add(extractAccountFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accounts;
    }

    public Account authenticate(String username, String password) {
        String sql = "SELECT a.*, e.MaLoai FROM Account a LEFT JOIN Employee e ON a.EmployeeID = e.EmployeeID WHERE a.Username = ? AND a.Password = ? AND a.IsActive = 1";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractAccountFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Account> findByRole(String role) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT a.*, e.MaLoai FROM Account a LEFT JOIN Employee e ON a.EmployeeID = e.EmployeeID WHERE a.Role = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, role);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                accounts.add(extractAccountFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accounts;
    }

    private Account extractAccountFromResultSet(ResultSet rs) throws SQLException {
        Account account = new Account();
        account.setUsername(rs.getString("Username"));
        account.setPassword(rs.getString("Password"));
        account.setEmployeeId(rs.getString("EmployeeID"));
        account.setRole(rs.getString("Role"));
        account.setActive(rs.getBoolean("IsActive"));
        account.setMaLoai(rs.getString("MaLoai"));
        return account;
    }
}
