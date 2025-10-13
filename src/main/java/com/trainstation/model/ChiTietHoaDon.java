package com.trainstation.model;

import java.io.Serializable;

public class ChiTietHoaDon implements Serializable {
    private String maHoaDon;
    private String maVe;
    private String maLoaiVe;
    private float giaGoc;
    private float giaDaKM;
    private String moTa;

    public ChiTietHoaDon() {
    }

    public ChiTietHoaDon(String maHoaDon, String maVe, String maLoaiVe, float giaGoc, float giaDaKM, String moTa) {
        this.maHoaDon = maHoaDon;
        this.maVe = maVe;
        this.maLoaiVe = maLoaiVe;
        this.giaGoc = giaGoc;
        this.giaDaKM = giaDaKM;
        this.moTa = moTa;
    }

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public String getMaVe() {
        return maVe;
    }

    public void setMaVe(String maVe) {
        this.maVe = maVe;
    }

    public String getMaLoaiVe() {
        return maLoaiVe;
    }

    public void setMaLoaiVe(String maLoaiVe) {
        this.maLoaiVe = maLoaiVe;
    }

    public float getGiaGoc() {
        return giaGoc;
    }

    public void setGiaGoc(float giaGoc) {
        this.giaGoc = giaGoc;
    }

    public float getGiaDaKM() {
        return giaDaKM;
    }

    public void setGiaDaKM(float giaDaKM) {
        this.giaDaKM = giaDaKM;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    @Override
    public String toString() {
        return "ChiTietHoaDon{" +
                "maHoaDon='" + maHoaDon + '\'' +
                ", maVe='" + maVe + '\'' +
                ", maLoaiVe='" + maLoaiVe + '\'' +
                ", giaGoc=" + giaGoc +
                ", giaDaKM=" + giaDaKM +
                ", moTa='" + moTa + '\'' +
                '}';
    }
}
