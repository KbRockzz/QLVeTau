package com.trainstation.model;

import java.io.Serializable;

public class ChangTau implements Serializable {
    private String maChang;
    private String tenChang;
    private String diaChi;
    private String thanhPho;

    public ChangTau() {
    }

    public ChangTau(String maChang, String tenChang, String diaChi, String thanhPho) {
        this.maChang = maChang;
        this.tenChang = tenChang;
        this.diaChi = diaChi;
        this.thanhPho = thanhPho;
    }

    public String getMaChang() {
        return maChang;
    }

    public void setMaChang(String maChang) {
        this.maChang = maChang;
    }

    public String getTenChang() {
        return tenChang;
    }

    public void setTenChang(String tenChang) {
        this.tenChang = tenChang;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getThanhPho() {
        return thanhPho;
    }

    public void setThanhPho(String thanhPho) {
        this.thanhPho = thanhPho;
    }

    @Override
    public String toString() {
        return "ChangTau{" +
                "maChang='" + maChang + '\'' +
                ", tenChang='" + tenChang + '\'' +
                ", diaChi='" + diaChi + '\'' +
                ", thanhPho='" + thanhPho + '\'' +
                '}';
    }
}
