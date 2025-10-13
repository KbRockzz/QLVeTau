package com.trainstation.dao;

import com.trainstation.model.Customer;
import com.trainstation.MySQL.ConnectSql;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO implements GenericDAO<Customer> {
    private static CustomerDAO instance;
    private Connection connection;

    private CustomerDAO() {
        connection = ConnectSql.getInstance().getConnection();
    }

    public static synchronized CustomerDAO getInstance() {
        if (instance == null) {
            instance = new CustomerDAO();
        }
        return instance;
    }

    @Override
    public void add(Customer customer) {
        String sql = "INSERT INTO KhachHang (CustomerID, FullName, PhoneNumber, Email, IDNumber, Address) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, customer.getCustomerId());
            pstmt.setString(2, customer.getFullName());
            pstmt.setString(3, customer.getPhoneNumber());
            pstmt.setString(4, customer.getEmail());
            pstmt.setString(5, customer.getIdentityNumber());
            pstmt.setString(6, customer.getAddress());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Customer customer) {
        String sql = "UPDATE KhachHang SET FullName = ?, PhoneNumber = ?, Email = ?, IDNumber = ?, Address = ? WHERE CustomerID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, customer.getFullName());
            pstmt.setString(2, customer.getPhoneNumber());
            pstmt.setString(3, customer.getEmail());
            pstmt.setString(4, customer.getIdentityNumber());
            pstmt.setString(5, customer.getAddress());
            pstmt.setString(6, customer.getCustomerId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM KhachHang WHERE CustomerID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Customer findById(String id) {
        String sql = "SELECT * FROM KhachHang WHERE CustomerID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractCustomerFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Customer> findAll() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                customers.add(extractCustomerFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    public Customer findByPhoneNumber(String phoneNumber) {
        String sql = "SELECT * FROM KhachHang WHERE PhoneNumber = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, phoneNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractCustomerFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Customer extractCustomerFromResultSet(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setCustomerId(rs.getString("CustomerID"));
        customer.setFullName(rs.getString("FullName"));
        customer.setPhoneNumber(rs.getString("PhoneNumber"));
        customer.setEmail(rs.getString("Email"));
        customer.setIdentityNumber(rs.getString("IDNumber"));
        customer.setAddress(rs.getString("Address"));
        return customer;
    }
}
