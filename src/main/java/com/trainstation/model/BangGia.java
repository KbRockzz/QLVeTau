package com.trainstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class BangGia implements Serializable {
    private String maBangGia;
    private String maChang;
    private String loaiGhe;
    private Float giaCoBan;
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;
    private boolean isActive;

    public BangGia() {
        this.isActive = true;
    }

    public BangGia(String maBangGia, String maChang, String loaiGhe, Float giaCoBan, LocalDateTime ngayBatDau, LocalDateTime ngayKetThuc, boolean isActive) {
        this.maBangGia = maBangGia;
        this.maChang = maChang;
        this.loaiGhe = loaiGhe;
        this.giaCoBan = giaCoBan;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.isActive = isActive;
    }

    public String getMaBangGia() {
        return maBangGia;
    }

    public void setMaBangGia(String maBangGia) {
        this.maBangGia = maBangGia;
    }

    public String getMaChang() {
        return maChang;
    }

    public void setMaChang(String maChang) {
        this.maChang = maChang;
    }

    public String getLoaiGhe() {
        return loaiGhe;
    }

    public void setLoaiGhe(String loaiGhe) {
        this.loaiGhe = loaiGhe;
    }

    public Float getGiaCoBan() {
        return giaCoBan;
    }

    public void setGiaCoBan(Float giaCoBan) {
        this.giaCoBan = giaCoBan;
    }

    public LocalDateTime getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(LocalDateTime ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public LocalDateTime getNgayKetThuc() {
        return ngayKetThuc;
    }

    public void setNgayKetThuc(LocalDateTime ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "BangGia{" +
                "maBangGia='" + maBangGia + '\'' +
                ", maChang='" + maChang + '\'' +
                ", loaiGhe='" + loaiGhe + '\'' +
                ", giaCoBan=" + giaCoBan +
                ", ngayBatDau=" + ngayBatDau +
                ", ngayKetThuc=" + ngayKetThuc +
                ", isActive=" + isActive +
                '}';
    }
}
