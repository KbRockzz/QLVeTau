/*
 * @ (#) Ga        1.0     11/15/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package com.trainstation.model;

/*
 * @description:
 * @author: Thuy, Ly Thi
 * @version: 1.0
 * @created: 11/15/2025  7:10 PM
 */
public class Ga {
    private String maGa;
    private String tenGa;
    private String moTa;
    private boolean tinhTrang;
    private String diaChi;

    public Ga() {
    }

    public Ga(String maGa, String tenGa, String diaChi, boolean tinhTrang, String moTa) {
        this.maGa = maGa;
        this.tenGa = tenGa;
        this.diaChi = diaChi;
        this.tinhTrang = tinhTrang;
        this.moTa = moTa;
    }

    public String getMaGa() {
        return maGa;
    }

    public void setMaGa(String maGa) {
        this.maGa = maGa;
    }

    public String getTenGa() {
        return tenGa;
    }

    public void setTenGa(String tenGa) {
        this.tenGa = tenGa;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public boolean isTinhTrang() {
        return tinhTrang;
    }

    public void setTinhTrang(boolean tinhTrang) {
        this.tinhTrang = tinhTrang;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }



    @Override
    public String toString() {
        return "Ga{" +
                "maGa='" + maGa + '\'' +
                ", tenGa='" + tenGa + '\'' +
                ", diaChi='" + diaChi + '\'' +
                ", tinhTrang=" + tinhTrang +
                ", moTa='" + moTa + '\'' +
                '}';
    }
}

