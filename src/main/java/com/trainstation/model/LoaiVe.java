package com.trainstation.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class LoaiVe implements Serializable {
    private String maLoaiVe;
    private String tenLoai;
    private BigDecimal heSoGia;
    private String moTa;
    private boolean isActive;

    public LoaiVe() {
        this.isActive = true;
    }

    public LoaiVe(String maLoaiVe, String tenLoai, BigDecimal heSoGia, String moTa, boolean isActive) {
        this.maLoaiVe = maLoaiVe;
        this.tenLoai = tenLoai;
        this.heSoGia = heSoGia;
        this.moTa = moTa;
        this.isActive = isActive;
    }

    public String getMaLoaiVe() {
        return maLoaiVe;
    }

    public void setMaLoaiVe(String maLoaiVe) {
        this.maLoaiVe = maLoaiVe;
    }

    public String getTenLoai() {
        return tenLoai;
    }

    public void setTenLoai(String tenLoai) {
        this.tenLoai = tenLoai;
    }

    public BigDecimal getHeSoGia() {
        return heSoGia;
    }

    public void setHeSoGia(BigDecimal heSoGia) {
        this.heSoGia = heSoGia;
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
        return "LoaiVe{" +
                "maLoaiVe='" + maLoaiVe + '\'' +
                ", tenLoai='" + tenLoai + '\'' +
                ", heSoGia=" + heSoGia +
                ", moTa='" + moTa + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
