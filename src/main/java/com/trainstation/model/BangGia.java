package com.trainstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class BangGia implements Serializable {
    private String maBangGia;
    private String maChang;
    private String loaiGhe;
    private float giaCoBan;
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;

    public BangGia() {
    }

    public BangGia(String maBangGia, String maChang, String loaiGhe, float giaCoBan, LocalDateTime ngayBatDau, LocalDateTime ngayKetThuc) {
        this.maBangGia = maBangGia;
        this.maChang = maChang;
        this.loaiGhe = loaiGhe;
        this.giaCoBan = giaCoBan;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
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

    public float getGiaCoBan() {
        return giaCoBan;
    }

    public void setGiaCoBan(float giaCoBan) {
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

    @Override
    public String toString() {
        return "BangGia{" +
                "maBangGia='" + maBangGia + '\'' +
                ", maChang='" + maChang + '\'' +
                ", loaiGhe='" + loaiGhe + '\'' +
                ", giaCoBan=" + giaCoBan +
                ", ngayBatDau=" + ngayBatDau +
                ", ngayKetThuc=" + ngayKetThuc +
                '}';
    }
}
