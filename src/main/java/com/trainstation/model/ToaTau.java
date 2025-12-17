package com.trainstation.model;

import java.io.Serializable;

public class ToaTau implements Serializable {
    private String maToa;
    private String loaiToa;
    private Integer samSX;
    private String trangThai;
    private Integer sucChua;

    public ToaTau() {
    }

    public ToaTau(String maToa, String loaiToa, Integer samSX, String trangThai, Integer sucChua) {
        this.maToa = maToa;
        this.loaiToa = loaiToa;
        this.samSX = samSX;
        this.trangThai = trangThai;
        this.sucChua = sucChua;    }

    public String getMaToa() {
        return maToa;
    }

    public void setMaToa(String maToa) {
        this.maToa = maToa;
    }

    public String getLoaiToa() {
        return loaiToa;
    }

    public void setLoaiToa(String loaiToa) {
        this.loaiToa = loaiToa;
    }

    public Integer getSamSX() {
        return samSX;
    }

    public void setSamSX(Integer samSX) {
        this.samSX = samSX;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public Integer getSucChua() {
        return sucChua;
    }

    public void setSucChua(Integer sucChua) {
        this.sucChua = sucChua;
    }    @Override
    public String toString() {
        return "ToaTau{" +
                "maToa='" + maToa + '\'' +
                ", loaiToa='" + loaiToa + '\'' +
                ", samSX=" + samSX +
                ", trangThai='" + trangThai + '\'' +
                ", sucChua=" + sucChua + +
                '}';
    }
}

