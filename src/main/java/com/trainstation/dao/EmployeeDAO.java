package com.trainstation.dao;

import com.trainstation.model.Employee;
import com.trainstation.MySQL.ConnectSql;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO implements GenericDAO<Employee> {
    private static EmployeeDAO instance;
    private Connection connection;

    private EmployeeDAO() {
        connection = ConnectSql.getInstance().getConnection();
    }

    public static synchronized EmployeeDAO getInstance() {
        if (instance == null) {
            instance = new EmployeeDAO();
        }
        return instance;
    }

    @Override
    public void add(Employee employee) {
        String sql = "INSERT INTO NhanVien (EmployeeID, FullName, PhoneNumber, Email, Position, Salary, HireDate, MaLoai) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, employee.getEmployeeId());
            pstmt.setString(2, employee.getFullName());
            pstmt.setString(3, employee.getPhoneNumber());
            pstmt.setString(4, employee.getEmail());
            pstmt.setString(5, employee.getPosition());
            pstmt.setDouble(6, employee.getSalary());
            pstmt.setDate(7, employee.getHireDate() != null ? Date.valueOf(employee.getHireDate()) : null);
            pstmt.setString(8, employee.getMaLoai());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Employee employee) {
        String sql = "UPDATE NhanVien SET FullName = ?, PhoneNumber = ?, Email = ?, Position = ?, Salary = ?, HireDate = ?, MaLoai = ? WHERE EmployeeID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, employee.getFullName());
            pstmt.setString(2, employee.getPhoneNumber());
            pstmt.setString(3, employee.getEmail());
            pstmt.setString(4, employee.getPosition());
            pstmt.setDouble(5, employee.getSalary());
            pstmt.setDate(6, employee.getHireDate() != null ? Date.valueOf(employee.getHireDate()) : null);
            pstmt.setString(7, employee.getMaLoai());
            pstmt.setString(8, employee.getEmployeeId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM NhanVien WHERE EmployeeID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Employee findById(String id) {
        String sql = "SELECT * FROM NhanVien WHERE EmployeeID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractEmployeeFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Employee> findAll() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                employees.add(extractEmployeeFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    public List<Employee> findByPosition(String position) {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien WHERE Position = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, position);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                employees.add(extractEmployeeFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    private Employee extractEmployeeFromResultSet(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setEmployeeId(rs.getString("EmployeeID"));
        employee.setFullName(rs.getString("FullName"));
        employee.setPhoneNumber(rs.getString("PhoneNumber"));
        employee.setEmail(rs.getString("Email"));
        employee.setPosition(rs.getString("Position"));
        employee.setSalary(rs.getDouble("Salary"));
        Date hireDate = rs.getDate("HireDate");
        if (hireDate != null) {
            employee.setHireDate(hireDate.toLocalDate());
        }
        employee.setMaLoai(rs.getString("MaLoai"));
        return employee;
    }
}
