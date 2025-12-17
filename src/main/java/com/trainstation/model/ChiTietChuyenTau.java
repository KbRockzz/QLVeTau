package com.trainstation.model;

import java.io.Serializable;

/**
 * ChiTietChuyenTau represents the details of train trip composition.
 * It links a specific train trip (ChuyenTau) to its assigned coaches (ToaTau).
 * This allows different trips to have different coach configurations.
 */
public class ChiTietChuyenTau implements Serializable {
    private String maChuyenTau;
    private String maToaTau;
    private Integer soThuTuToa;
    private Integer sucChua;
    private boolean isActive;

    public ChiTietChuyenTau() {
        this.isActive = true;
    }

    public ChiTietChuyenTau(String maChuyenTau, String maToaTau, Integer soThuTuToa, Integer sucChua) {
        this.maChuyenTau = maChuyenTau;
        this.maToaTau = maToaTau;
        this.soThuTuToa = soThuTuToa;
        this.sucChua = sucChua;
        this.isActive = true;
    }

    public String getMaChuyenTau() {
        return maChuyenTau;
    }

    public void setMaChuyenTau(String maChuyenTau) {
        this.maChuyenTau = maChuyenTau;
    }

    public String getMaToaTau() {
        return maToaTau;
    }

    public void setMaToaTau(String maToaTau) {
        this.maToaTau = maToaTau;
    }

    public Integer getSoThuTuToa() {
        return soThuTuToa;
    }

    public void setSoThuTuToa(Integer soThuTuToa) {
        this.soThuTuToa = soThuTuToa;
    }

    public Integer getSucChua() {
        return sucChua;
    }

    public void setSucChua(Integer sucChua) {
        this.sucChua = sucChua;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "ChiTietChuyenTau{" +
                "maChuyenTau='" + maChuyenTau + '\'' +
                ", maToaTau='" + maToaTau + '\'' +
                ", soThuTuToa=" + soThuTuToa +
                ", sucChua=" + sucChua +
                ", isActive=" + isActive +
                '}';
    }
}
