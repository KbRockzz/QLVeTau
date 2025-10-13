package com.trainstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class HoaDon implements Serializable {
    private String maHoaDon;
    private String customerId;
    private String employeeId;
    private LocalDateTime ngayLap;
    private double tongTien;
    private String trangThai;
    
    private Customer customer;
    private Employee employee;

    public HoaDon() {
    }

    public HoaDon(String maHoaDon, String customerId, String employeeId, LocalDateTime ngayLap, 
                  double tongTien, String trangThai) {
        this.maHoaDon = maHoaDon;
        this.customerId = customerId;
        this.employeeId = employeeId;
        this.ngayLap = ngayLap;
        this.tongTien = tongTien;
        this.trangThai = trangThai;
    }

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDateTime getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(LocalDateTime ngayLap) {
        this.ngayLap = ngayLap;
    }

    public double getTongTien() {
        return tongTien;
    }

    public void setTongTien(double tongTien) {
        this.tongTien = tongTien;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        if (customer != null) {
            this.customerId = customer.getCustomerId();
        }
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

    @Override
    public String toString() {
        return "HoaDon{" +
                "maHoaDon='" + maHoaDon + '\'' +
                ", customerId='" + customerId + '\'' +
                ", employeeId='" + employeeId + '\'' +
                ", ngayLap=" + ngayLap +
                ", tongTien=" + tongTien +
                ", trangThai='" + trangThai + '\'' +
                '}';
    }
}
