package com.trainstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class HoaDon implements Serializable {
    private String maHoaDon;
    private String maNV;
    private String maKH;
    private LocalDateTime ngayLap;
    private String phuongThucThanhToan;
    private String trangThai;

    public HoaDon() {
    }

    public HoaDon(String maHoaDon, String maNV, String maKH, LocalDateTime ngayLap, String phuongThucThanhToan, String trangThai) {
        this.maHoaDon = maHoaDon;
        this.maNV = maNV;
        this.maKH = maKH;
        this.ngayLap = ngayLap;
        this.phuongThucThanhToan = phuongThucThanhToan;
        this.trangThai = trangThai;
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

    @Override
    public String toString() {
        return "HoaDon{" +
                "maHoaDon='" + maHoaDon + '\'' +
                ", maNV='" + maNV + '\'' +
                ", maKH='" + maKH + '\'' +
                ", ngayLap=" + ngayLap +
                ", phuongThucThanhToan='" + phuongThucThanhToan + '\'' +
                ", trangThai='" + trangThai + '\'' +
                '}';
    }
}
