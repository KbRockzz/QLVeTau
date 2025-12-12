package com.trainstation.model;

import java.io.Serializable;

public class Ghe implements Serializable {
    private String maGhe;
    private String maToa;
    private String loaiGhe;
    private String trangThai;

    public Ghe() {
    }

    public Ghe(String maGhe, String maToa, String loaiGhe, String trangThai) {
        this.maGhe = maGhe;
        this.maToa = maToa;
        this.loaiGhe = loaiGhe;
        this.trangThai = trangThai;
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

    @Override
    public String toString() {
        return "Ghe{" +
                "maGhe='" + maGhe + '\'' +
                ", maToa='" + maToa + '\'' +
                ", loaiGhe='" + loaiGhe + '\'' +
                ", trangThai='" + trangThai + '\'' +
                '}';
    }
}

