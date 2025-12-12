package com.trainstation.model;

import java.io.Serializable;

public class KhachHang implements Serializable {
    private String maKhachHang;
    private String tenKhachHang;
    private String email;
    private String soDienThoai;
    private boolean isActive;

    public KhachHang() {
        this.isActive = true;
    }

    public KhachHang(String maKhachHang, String tenKhachHang, String email, String soDienThoai, boolean isActive) {
        this.maKhachHang = maKhachHang;
        this.tenKhachHang = tenKhachHang;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.isActive = isActive;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public String getTenKhachHang() {
        return tenKhachHang;
    }

    public void setTenKhachHang(String tenKhachHang) {
        this.tenKhachHang = tenKhachHang;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "KhachHang{" +
                "maKhachHang='" + maKhachHang + '\'' +
                ", tenKhachHang='" + tenKhachHang + '\'' +
                ", email='" + email + '\'' +
                ", soDienThoai='" + soDienThoai + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}

