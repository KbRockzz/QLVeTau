package com.trainstation.model;

import java.io.Serializable;

public class Ghe implements Serializable {
    private String maGhe;
    private String maToa;
    private String loaiGhe;
    private String trangThai;
    private boolean isActive;

    public Ghe() {
        this.isActive = true;
    }

    public Ghe(String maGhe, String maToa, String loaiGhe, String trangThai, boolean isActive) {
        this.maGhe = maGhe;
        this.maToa = maToa;
        this.loaiGhe = loaiGhe;
        this.trangThai = trangThai;
        this.isActive = isActive;
    }

    public String getMaGhe() {
        return maGhe;
    }

    public void setMaGhe(String maGhe) {
        this.maGhe = maGhe;
    }

    public String getMaToa() {
        return maToa;
    }

    public void setMaToa(String maToa) {
        this.maToa = maToa;
    }

    public String getLoaiGhe() {
        return loaiGhe;
    }

    public void setLoaiGhe(String loaiGhe) {
        this.loaiGhe = loaiGhe;
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
        return "Ghe{" +
                "maGhe='" + maGhe + '\'' +
                ", maToa='" + maToa + '\'' +
                ", loaiGhe='" + loaiGhe + '\'' +
                ", trangThai='" + trangThai + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}

