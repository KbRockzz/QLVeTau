package com.trainstation.model;

import java.io.Serializable;

public class Tau implements Serializable {
    private String maTau;
    private int soToa;
    private String tenTau;
    private String trangThai;

    public Tau() {
    }

    public Tau(String maTau, int soToa, String tenTau, String trangThai) {
        this.maTau = maTau;
        this.soToa = soToa;
        this.tenTau = tenTau;
        this.trangThai = trangThai;
    }

    public String getMaTau() {
        return maTau;
    }

    public void setMaTau(String maTau) {
        this.maTau = maTau;
    }

    public int getSoToa() {
        return soToa;
    }

    public void setSoToa(int soToa) {
        this.soToa = soToa;
    }

    public String getTenTau() {
        return tenTau;
    }

    public void setTenTau(String tenTau) {
        this.tenTau = tenTau;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return "Tau{" +
                "maTau='" + maTau + '\'' +
                ", soToa=" + soToa +
                ", tenTau='" + tenTau + '\'' +
                ", trangThai='" + trangThai + '\'' +
                '}';
    }
}

