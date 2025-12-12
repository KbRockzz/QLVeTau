package com.trainstation.model;

import java.io.Serializable;

public class LoaiNV implements Serializable {
    private String maLoai;
    private String tenLoai;
    private String moTa;
    private boolean isActive;

    public LoaiNV() {
        this.isActive = true;
    }

    public LoaiNV(String maLoai, String tenLoai, String moTa, boolean isActive) {
        this.maLoai = maLoai;
        this.tenLoai = tenLoai;
        this.moTa = moTa;
        this.isActive = isActive;
    }

    public String getMaLoai() {
        return maLoai;
    }

    public void setMaLoai(String maLoai) {
        this.maLoai = maLoai;
    }

    public String getTenLoai() {
        return tenLoai;
    }

    public void setTenLoai(String tenLoai) {
        this.tenLoai = tenLoai;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "LoaiNV{" +
                "maLoai='" + maLoai + '\'' +
                ", tenLoai='" + tenLoai + '\'' +
                ", moTa='" + moTa + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
