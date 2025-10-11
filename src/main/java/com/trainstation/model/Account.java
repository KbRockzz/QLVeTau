package com.trainstation.model;

import java.io.Serializable;

public class Account implements Serializable {
    private String username;
    private String password;
    private String role; // "ADMIN" or "EMPLOYEE"
    private String employeeId;
    private String maLoai; // Employee type from Employee: LNV01, LNV02, LNV03 (Manager)
    private boolean active;

    public Account() {
    }

    public Account(String username, String password, String role, String employeeId, boolean active) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.employeeId = employeeId;
        this.maLoai = null;
        this.active = active;
    }

    public Account(String username, String password, String role, String employeeId, String maLoai, boolean active) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.employeeId = employeeId;
        this.maLoai = maLoai;
        this.active = active;
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

    public String getMaLoai() {
        return maLoai;
    }

    public void setMaLoai(String maLoai) {
        this.maLoai = maLoai;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Check if the account is a manager (LNV03)
     * @return true if maLoai is LNV03, false otherwise
     */
    public boolean isManager() {
        return "LNV03".equals(maLoai);
    }

    @Override
    public String toString() {
        return "Account{" +
                "username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", employeeId='" + employeeId + '\'' +
                ", maLoai='" + maLoai + '\'' +
                ", active=" + active +
                '}';
    }
}
