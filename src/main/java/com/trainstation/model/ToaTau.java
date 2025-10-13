package com.trainstation.model;

import java.io.Serializable;

public class ToaTau implements Serializable {
    private String maToa;
    private String tenToa;
    private String loaiToa;
    private String maTau;
    private int sucChua;

    public ToaTau() {
    }

    public ToaTau(String maToa, String tenToa, String loaiToa, String maTau, int sucChua) {
        this.maToa = maToa;
        this.tenToa = tenToa;
        this.loaiToa = loaiToa;
        this.maTau = maTau;
        this.sucChua = sucChua;
    }

    public String getMaToa() {
        return maToa;
    }

    public void setMaToa(String maToa) {
        this.maToa = maToa;
    }

    public String getTenToa() {
        return tenToa;
    }

    public void setTenToa(String tenToa) {
        this.tenToa = tenToa;
    }

    public String getLoaiToa() {
        return loaiToa;
    }

    public void setLoaiToa(String loaiToa) {
        this.loaiToa = loaiToa;
    }

    public String getMaTau() {
        return maTau;
    }

    public void setMaTau(String maTau) {
        this.maTau = maTau;
    }

    public int getSucChua() {
        return sucChua;
    }

    public void setSucChua(int sucChua) {
        this.sucChua = sucChua;
    }

    @Override
    public String toString() {
        return "ToaTau{" +
                "maToa='" + maToa + '\'' +
                ", tenToa='" + tenToa + '\'' +
                ", loaiToa='" + loaiToa + '\'' +
                ", maTau='" + maTau + '\'' +
                ", sucChua=" + sucChua +
                '}';
    }
}

