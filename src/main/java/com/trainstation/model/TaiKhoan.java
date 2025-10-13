package com.trainstation.model;

import java.io.Serializable;

public class TaiKhoan implements Serializable {
    private String maTK;
    private String maNV;
    private String tenTaiKhoan;
    private String matKhau;
    private String trangThai;

    public TaiKhoan() {
    }

    public TaiKhoan(String maTK, String maNV, String tenTaiKhoan, String matKhau, String trangThai) {
        this.maTK = maTK;
        this.maNV = maNV;
        this.tenTaiKhoan = tenTaiKhoan;
        this.matKhau = matKhau;
        this.trangThai = trangThai;
    }

    public String getMaTK() {
        return maTK;
    }

    public void setMaTK(String maTK) {
        this.maTK = maTK;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getTenTaiKhoan() {
        return tenTaiKhoan;
    }

    public void setTenTaiKhoan(String tenTaiKhoan) {
        this.tenTaiKhoan = tenTaiKhoan;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return "TaiKhoan{" +
                "maTK='" + maTK + '\'' +
                ", maNV='" + maNV + '\'' +
                ", tenTaiKhoan='" + tenTaiKhoan + '\'' +
                ", matKhau='" + matKhau + '\'' +
                ", trangThai='" + trangThai + '\'' +
                '}';
    }
}
