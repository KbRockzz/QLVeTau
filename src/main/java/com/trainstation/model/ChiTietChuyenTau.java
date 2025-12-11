/*
 * @ (#) ChiTietChuyenTau        1.0     12/11/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package com.trainstation.model;

import java.io.Serializable;

/*
 * @description:
 * @author: Thuy, Ly Thi
 * @version: 1.0
 * @created: 12/11/2025  11:03 PM
 */
public class ChiTietChuyenTau implements Serializable {
    private String maChuyenTau;
    private String maToaTau;
    private int soThuTuToa;
    private int sucChua;

    public ChiTietChuyenTau() {
    }

    public ChiTietChuyenTau(String maChuyenTau, String maToaTau, int soThuTuToa, int sucChua) {
        this.maChuyenTau = maChuyenTau;
        this.maToaTau = maToaTau;
        this.soThuTuToa = soThuTuToa;
        this.sucChua = sucChua;
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
    public int getSoThuTuToa() {
        return soThuTuToa;
    }
    public void setSoThuTuToa(int soThuTuToa) {
        this.soThuTuToa = soThuTuToa;
    }
    public int getSucChua() {
        return sucChua;
    }
    public void setSucChua(int sucChua) {
        this.sucChua = sucChua;
    }

    @Override
    public String toString() {
        return "ChiTietChuyenTau{" +
                "maChuyenTau='" + maChuyenTau + '\'' +
                ", maToaTau='" + maToaTau + '\'' +
                ", soThuTuToa=" + soThuTuToa +
                ", sucChua=" + sucChua +
                '}';
    }
}

