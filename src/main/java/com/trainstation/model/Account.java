package com.trainstation.model;

import java.io.Serializable;

public class Account implements Serializable {
    private String username;
    private String password;
    private String employeeId;
    private String role;
    private boolean isActive;
    
    private Employee employee;

    public Account() {
    }

    public Account(String username, String password, String employeeId, String role, boolean isActive) {
        this.username = username;
        this.password = password;
        this.employeeId = employeeId;
        this.role = role;
        this.isActive = isActive;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
        if (employee != null) {
            this.employeeId = employee.getEmployeeId();
        }
    }

    public boolean isManager() {
        if (employee != null) {
            return "LNV03".equals(employee.getMaLoai());
        }
        return false;
    }

    @Override
    public String toString() {
        return "Account{" +
                "username='" + username + '\'' +
                ", employeeId='" + employeeId + '\'' +
                ", role='" + role + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
