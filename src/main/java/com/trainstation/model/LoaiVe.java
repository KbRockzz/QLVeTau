package com.trainstation.model;

import java.io.Serializable;

public class LoaiVe implements Serializable {
    private String maLoaiVe;
    private String tenLoaiVe;
    private double phanTramGiam;
    private String moTa;

    public LoaiVe() {
    }

    public LoaiVe(String maLoaiVe, String tenLoaiVe, double phanTramGiam, String moTa) {
        this.maLoaiVe = maLoaiVe;
        this.tenLoaiVe = tenLoaiVe;
        this.phanTramGiam = phanTramGiam;
        this.moTa = moTa;
    }

    public String getMaLoaiVe() {
        return maLoaiVe;
    }

    public void setMaLoaiVe(String maLoaiVe) {
        this.maLoaiVe = maLoaiVe;
    }

    public String getTenLoaiVe() {
        return tenLoaiVe;
    }

    public void setTenLoaiVe(String tenLoaiVe) {
        this.tenLoaiVe = tenLoaiVe;
    }

    public double getPhanTramGiam() {
        return phanTramGiam;
    }

    public void setPhanTramGiam(double phanTramGiam) {
        this.phanTramGiam = phanTramGiam;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    @Override
    public String toString() {
        return "LoaiVe{" +
                "maLoaiVe='" + maLoaiVe + '\'' +
                ", tenLoaiVe='" + tenLoaiVe + '\'' +
                ", phanTramGiam=" + phanTramGiam +
                ", moTa='" + moTa + '\'' +
                '}';
    }
}
