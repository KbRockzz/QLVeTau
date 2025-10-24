package com.trainstation.model;

import java.io.Serializable;
import java.time.LocalDate;

public class NhanVien implements Serializable {
    private String maNV;
    private String tenNV;
    private String soDienThoai;
    private String diaChi;
    private LocalDate ngaySinh;
    private String maLoaiNV;
    private String trangThai = "active";
    public NhanVien() {
    }

    public NhanVien(String maNV, String tenNV, String soDienThoai, String diaChi, LocalDate ngaySinh, String maLoaiNV) {
        this.maNV = maNV;
        this.tenNV = tenNV;
        this.soDienThoai = soDienThoai;
        this.diaChi = diaChi;
        this.ngaySinh = ngaySinh;
        this.maLoaiNV = maLoaiNV;
    }
    public NhanVien(String maNV, String tenNV, String soDienThoai, String diaChi, LocalDate ngaySinh, String maLoaiNV, String trangThai) {
        this.maNV = maNV;
        this.tenNV = tenNV;
        this.soDienThoai = soDienThoai;
        this.diaChi = diaChi;
        this.ngaySinh = ngaySinh;
        this.maLoaiNV = maLoaiNV;
        this.trangThai = trangThai;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getTenNV() {
        return tenNV;
    }

    public void setTenNV(String tenNV) {
        this.tenNV = tenNV;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getMaLoaiNV() {
        return maLoaiNV;
    }

    public void setMaLoaiNV(String maLoaiNV) {
        this.maLoaiNV = maLoaiNV;
    }

    public String getTrangThai() {
        return trangThai;
    }
    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
    @Override
    public String toString() {
        return "NhanVien{" +
                "maNV='" + maNV + '\'' +
                ", tenNV='" + tenNV + '\'' +
                ", soDienThoai='" + soDienThoai + '\'' +
                ", diaChi='" + diaChi + '\'' +
                ", ngaySinh=" + ngaySinh +
                ", maLoaiNV='" + maLoaiNV + '\'' +
                '}';
    }
}

