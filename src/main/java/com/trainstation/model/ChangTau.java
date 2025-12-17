package com.trainstation.model;

import java.io.Serializable;

public class ChangTau implements Serializable {
    private String maChang;
    private Integer soKMToiThieu;
    private Integer soKMToiDa;
    private String moTa;
    private Float giaTien;

    public ChangTau() {
    }

    public ChangTau(String maChang, Integer soKMToiThieu, Integer soKMToiDa, String moTa, Float giaTien) {
        this.maChang = maChang;
        this.soKMToiThieu = soKMToiThieu;
        this.soKMToiDa = soKMToiDa;
        this.moTa = moTa;
        this.giaTien = giaTien;    }

    public String getMaChang() {
        return maChang;
    }

    public void setMaChang(String maChang) {
        this.maChang = maChang;
    }

    public Integer getSoKMToiThieu() {
        return soKMToiThieu;
    }

    public void setSoKMToiThieu(Integer soKMToiThieu) {
        this.soKMToiThieu = soKMToiThieu;
    }

    public Integer getSoKMToiDa() {
        return soKMToiDa;
    }

    public void setSoKMToiDa(Integer soKMToiDa) {
        this.soKMToiDa = soKMToiDa;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public Float getGiaTien() {
        return giaTien;
    }

    public void setGiaTien(Float giaTien) {
        this.giaTien = giaTien;
    }    @Override
    public String toString() {
        return "ChangTau{" +
                "maChang='" + maChang + '\'' +
                ", soKMToiThieu=" + soKMToiThieu +
                ", soKMToiDa=" + soKMToiDa +
                ", moTa='" + moTa + '\'' +
                ", giaTien=" + giaTien + +
                '}';
    }
}
