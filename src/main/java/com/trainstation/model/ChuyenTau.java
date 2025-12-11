package com.trainstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ChuyenTau implements Serializable {
    private String maChuyen;
    private String maDauMay;
    private String maNV;
    private String maGaDi;
    private String maGaDen;
    private LocalDateTime gioDi;
    private LocalDateTime gioDen;
    private Integer soKm;
    private String maChang;
    private String trangThai;
    private boolean isActive;

    public ChuyenTau() {
        this.isActive = true;
    }

    public ChuyenTau(String maChuyen, String maDauMay, String maNV, String maGaDi, String maGaDen,
                     LocalDateTime gioDi, LocalDateTime gioDen, Integer soKm, String maChang,
                     String trangThai, boolean isActive) {
        this.maChuyen = maChuyen;
        this.maDauMay = maDauMay;
        this.maNV = maNV;
        this.maGaDi = maGaDi;
        this.maGaDen = maGaDen;
        this.gioDi = gioDi;
        this.gioDen = gioDen;
        this.soKm = soKm;
        this.maChang = maChang;
        this.trangThai = trangThai;
        this.isActive = isActive;
    }

    public String getMaChuyen() {
        return maChuyen;
    }

    public void setMaChuyen(String maChuyen) {
        this.maChuyen = maChuyen;
    }

    public String getMaDauMay() {
        return maDauMay;
    }

    public void setMaDauMay(String maDauMay) {
        this.maDauMay = maDauMay;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getMaGaDi() {
        return maGaDi;
    }

    public void setMaGaDi(String maGaDi) {
        this.maGaDi = maGaDi;
    }

    public String getMaGaDen() {
        return maGaDen;
    }

    public void setMaGaDen(String maGaDen) {
        this.maGaDen = maGaDen;
    }

    public LocalDateTime getGioDi() {
        return gioDi;
    }

    public void setGioDi(LocalDateTime gioDi) {
        this.gioDi = gioDi;
    }

    public LocalDateTime getGioDen() {
        return gioDen;
    }

    public void setGioDen(LocalDateTime gioDen) {
        this.gioDen = gioDen;
    }

    public Integer getSoKm() {
        return soKm;
    }

    public void setSoKm(Integer soKm) {
        this.soKm = soKm;
    }

    public String getMaChang() {
        return maChang;
    }

    public void setMaChang(String maChang) {
        this.maChang = maChang;
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
        return "ChuyenTau{" +
                "maChuyen='" + maChuyen + '\'' +
                ", maDauMay='" + maDauMay + '\'' +
                ", maNV='" + maNV + '\'' +
                ", maGaDi='" + maGaDi + '\'' +
                ", maGaDen='" + maGaDen + '\'' +
                ", gioDi=" + gioDi +
                ", gioDen=" + gioDen +
                ", soKm=" + soKm +
                ", maChang='" + maChang + '\'' +
                ", trangThai='" + trangThai + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
