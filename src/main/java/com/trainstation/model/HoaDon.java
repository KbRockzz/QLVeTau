package com.trainstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class HoaDon implements Serializable {
    private String maHoaDon;
    private String maNV;
    private String maKH;
    private String tenKH;
    private String soDienThoai;
    private LocalDateTime ngayLap;
    private String phuongThucThanhToan;
    private String trangThai;
    private boolean isActive;

    public HoaDon() {
        this.isActive = true;
    }

    public HoaDon(String maHoaDon, String maNV, String maKH, String tenKH, String soDienThoai,
                  LocalDateTime ngayLap, String phuongThucThanhToan, String trangThai, boolean isActive) {
        this.maHoaDon = maHoaDon;
        this.maNV = maNV;
        this.maKH = maKH;
        this.tenKH = tenKH;
        this.soDienThoai = soDienThoai;
        this.ngayLap = ngayLap;
        this.phuongThucThanhToan = phuongThucThanhToan;
        this.trangThai = trangThai;
        this.isActive = isActive;
    }

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public String getTenKH() {
        return tenKH;
    }

    public void setTenKH(String tenKH) {
        this.tenKH = tenKH;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public LocalDateTime getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(LocalDateTime ngayLap) {
        this.ngayLap = ngayLap;
    }

    public String getPhuongThucThanhToan() {
        return phuongThucThanhToan;
    }

    public void setPhuongThucThanhToan(String phuongThucThanhToan) {
        this.phuongThucThanhToan = phuongThucThanhToan;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "HoaDon{" +
                "maHoaDon='" + maHoaDon + '\'' +
                ", maNV='" + maNV + '\'' +
                ", maKH='" + maKH + '\'' +
                ", tenKH='" + tenKH + '\'' +
                ", soDienThoai='" + soDienThoai + '\'' +
                ", ngayLap=" + ngayLap +
                ", phuongThucThanhToan='" + phuongThucThanhToan + '\'' +
                ", trangThai='" + trangThai + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
