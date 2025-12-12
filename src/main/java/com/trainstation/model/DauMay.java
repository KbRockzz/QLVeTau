package com.trainstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class DauMay implements Serializable {
    private String maDauMay;
    private String loaiDauMay;
    private String tenDauMay;
    private Integer namSX;
    private LocalDateTime lanBaoTriGanNhat;
    private String trangThai;
    private boolean isActive;

    public DauMay() {
        this.isActive = true;
    }

    public DauMay(String maDauMay, String loaiDauMay, String tenDauMay, Integer namSX, 
                  LocalDateTime lanBaoTriGanNhat, String trangThai, boolean isActive) {
        this.maDauMay = maDauMay;
        this.loaiDauMay = loaiDauMay;
        this.tenDauMay = tenDauMay;
        this.namSX = namSX;
        this.lanBaoTriGanNhat = lanBaoTriGanNhat;
        this.trangThai = trangThai;
        this.isActive = isActive;
    }

    public String getMaDauMay() {
        return maDauMay;
    }

    public void setMaDauMay(String maDauMay) {
        this.maDauMay = maDauMay;
    }

    public String getLoaiDauMay() {
        return loaiDauMay;
    }

    public void setLoaiDauMay(String loaiDauMay) {
        this.loaiDauMay = loaiDauMay;
    }

    public String getTenDauMay() {
        return tenDauMay;
    }

    public void setTenDauMay(String tenDauMay) {
        this.tenDauMay = tenDauMay;
    }

    public Integer getNamSX() {
        return namSX;
    }

    public void setNamSX(Integer namSX) {
        this.namSX = namSX;
    }

    public LocalDateTime getLanBaoTriGanNhat() {
        return lanBaoTriGanNhat;
    }

    public void setLanBaoTriGanNhat(LocalDateTime lanBaoTriGanNhat) {
        this.lanBaoTriGanNhat = lanBaoTriGanNhat;
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
        return "DauMay{" +
                "maDauMay='" + maDauMay + '\'' +
                ", loaiDauMay='" + loaiDauMay + '\'' +
                ", tenDauMay='" + tenDauMay + '\'' +
                ", namSX=" + namSX +
                ", lanBaoTriGanNhat=" + lanBaoTriGanNhat +
                ", trangThai='" + trangThai + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
