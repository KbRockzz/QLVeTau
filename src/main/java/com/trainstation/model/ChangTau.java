package com.trainstation.model;

import java.io.Serializable;

public class ChangTau implements Serializable {
    private String maChang;
    private int soKMToiThieu;
    private int soKMToiDa;
    private String moTa;
    private float giaTien;

    public ChangTau() {
    }

    public ChangTau(String maChang, int soKMToiThieu, int soKMToiDa, String moTa, float giaTien) {
        this.maChang = maChang;
        this.soKMToiThieu = soKMToiThieu;
        this.soKMToiDa = soKMToiDa;
        this.moTa = moTa;
        this.giaTien = giaTien;
    }

    public String getMaChang() {
        return maChang;
    }

    public void setMaChang(String maChang) {
        this.maChang = maChang;
    }

    public int getSoKMToiThieu() {
        return soKMToiThieu;
    }

    public void setSoKMToiThieu(int soKMToiThieu) {
        this.soKMToiThieu = soKMToiThieu;
    }

    public int getSoKMToiDa() {
        return soKMToiDa;
    }

    public void setSoKMToiDa(int soKMToiDa) {
        this.soKMToiDa = soKMToiDa;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public float getGiaTien() {
        return giaTien;
    }

    public void setGiaTien(float giaTien) {
        this.giaTien = giaTien;
    }

    @Override
    public String toString() {
        return "ChangTau{" +
                "maChang='" + maChang + '\'' +
                ", soKMToiThieu=" + soKMToiThieu +
                ", soKMToiDa=" + soKMToiDa +
                ", moTa='" + moTa + '\'' +
                ", giaTien=" + giaTien +
                '}';
    }
}
