package com.trainstation.model;

import java.io.Serializable;

public class Ga implements Serializable {
    private String maGa;
    private String tenGa;
    private String moTa;
    private String tinhTrang;
    private String diaChi;

    public Ga() {
    }

    public Ga(String maGa, String tenGa, String moTa, String tinhTrang, String diaChi) {
        this.maGa = maGa;
        this.tenGa = tenGa;
        this.moTa = moTa;
        this.tinhTrang = tinhTrang;
        this.diaChi = diaChi;    }

    public String getMaGa() {
        return maGa;
    }

    public void setMaGa(String maGa) {
        this.maGa = maGa;
    }

    public String getTenGa() {
        return tenGa;
    }

    public void setTenGa(String tenGa) {
        this.tenGa = tenGa;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getTinhTrang() {
        return tinhTrang;
    }

    public void setTinhTrang(String tinhTrang) {
        this.tinhTrang = tinhTrang;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }    @Override
    public String toString() {
        return "Ga{" +
                "maGa='" + maGa + '\'' +
                ", tenGa='" + tenGa + '\'' +
                ", moTa='" + moTa + '\'' +
                ", tinhTrang='" + tinhTrang + '\'' +
                ", diaChi='" + diaChi + '\'' + +
                '}';
    }
}
