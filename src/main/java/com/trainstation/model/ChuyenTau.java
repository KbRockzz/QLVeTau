package com.trainstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ChuyenTau implements Serializable {
    private String maChuyen;
    private String maTau;
    private String maNV;
    private Ga gaDi;
    private Ga gaDen;
    private LocalDateTime gioDi;
    private LocalDateTime gioDen;
    private int soKm;
    private String maChang;
    private String trangThai; // Thêm trường trạng thái

    public ChuyenTau() {
    }

    public ChuyenTau(String maChuyen, String maTau, String maNV, Ga maGaDi, Ga maGaDen,
                     LocalDateTime gioDi, LocalDateTime gioDen, int soKm, String maChang, String trangThai) {
        this.maChuyen = maChuyen;
        this.maTau = maTau;
        this.maNV = maNV;
        this.gaDi = maGaDi;
        this.gaDen = maGaDen;
        this.gioDi = gioDi;
        this.gioDen = gioDen;
        this.soKm = soKm;
        this.maChang = maChang;
        this.trangThai = trangThai;
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

    public Ga getGaDi() {return gaDi;}

    public void setGaDi(Ga gaDi) {this.gaDi = gaDi;}

    public Ga getGaDen() {return gaDen;}

    public void setGaDen(Ga gaDen) {this.gaDen = gaDen;}

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

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return "ChuyenTau{" +
                "maChuyen='" + maChuyen + '\'' +
                ", maTau='" + maTau + '\'' +
                ", maNV='" + maNV + '\'' +
                ", maGaDi=" + gaDi.getMaGa() +
                ", maGaDen=" + gaDen.getMaGa() +
                ", gioDi=" + gioDi +
                ", gioDen=" + gioDen +
                ", soKm=" + soKm +
                ", maChang='" + maChang + '\'' +
                ", trangThai='" + trangThai + '\'' +
                '}';
    }


}
