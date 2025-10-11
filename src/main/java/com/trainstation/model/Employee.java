package com.trainstation.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Employee implements Serializable {
    private String employeeId;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String position;
    private String maLoai; // Employee type: LNV01, LNV02, LNV03 (Manager)
    private LocalDate hireDate;
    private double salary;

    public Employee() {
    }

    public Employee(String employeeId, String fullName, String phoneNumber, String email, 
                    String position, String maLoai, LocalDate hireDate, double salary) {
        this.employeeId = employeeId;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.position = position;
        this.maLoai = maLoai;
        this.hireDate = hireDate;
        this.salary = salary;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getMaLoai() {
        return maLoai;
    }

    public void setMaLoai(String maLoai) {
        this.maLoai = maLoai;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeId='" + employeeId + '\'' +
                ", fullName='" + fullName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", position='" + position + '\'' +
                ", maLoai='" + maLoai + '\'' +
                ", hireDate=" + hireDate +
                ", salary=" + salary +
                '}';
    }
}
