package com.trainstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ChuyenTau implements Serializable {
    private String maChuyen;
    private String maTau;
    private String maNV;
    private String gaDi;
    private String gaDen;
    private LocalDateTime gioDi;
    private LocalDateTime gioDen;
    private int soKm;
    private String maChang;

    public ChuyenTau() {
    }

    public ChuyenTau(String maChuyen, String maTau, String maNV, String gaDi, String gaDen, LocalDateTime gioDi, LocalDateTime gioDen, int soKm, String maChang) {
        this.maChuyen = maChuyen;
        this.maTau = maTau;
        this.maNV = maNV;
        this.gaDi = gaDi;
        this.gaDen = gaDen;
        this.gioDi = gioDi;
        this.gioDen = gioDen;
        this.soKm = soKm;
        this.maChang = maChang;
    }

    public String getMaChuyen() {
        return maChuyen;
    }

    public void setMaChuyen(String maChuyen) {
        this.maChuyen = maChuyen;
    }

    public String getMaTau() {
        return maTau;
    }

    public void setMaTau(String maTau) {
        this.maTau = maTau;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getGaDi() {
        return gaDi;
    }

    public void setGaDi(String gaDi) {
        this.gaDi = gaDi;
    }

    public String getGaDen() {
        return gaDen;
    }

    public void setGaDen(String gaDen) {
        this.gaDen = gaDen;
    }

    public LocalDateTime getGioDi() {
        return gioDi;
    }

    public void setGioDi(LocalDateTime gioDi) {
        this.gioDi = gioDi;
    }

    public LocalDateTime getGioDen() {
        return gioDen;
    }

    public void setGioDen(LocalDateTime gioDen) {
        this.gioDen = gioDen;
    }

    public int getSoKm() {
        return soKm;
    }

    public void setSoKm(int soKm) {
        this.soKm = soKm;
    }

    public String getMaChang() {
        return maChang;
    }

    public void setMaChang(String maChang) {
        this.maChang = maChang;
    }

    @Override
    public String toString() {
        return "ChuyenTau{" +
                "maChuyen='" + maChuyen + '\'' +
                ", maTau='" + maTau + '\'' +
                ", maNV='" + maNV + '\'' +
                ", gaDi='" + gaDi + '\'' +
                ", gaDen='" + gaDen + '\'' +
                ", gioDi=" + gioDi +
                ", gioDen=" + gioDen +
                ", soKm=" + soKm +
                ", maChang='" + maChang + '\'' +
                '}';
    }
}
