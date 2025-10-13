package com.trainstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ChuyenTau implements Serializable {
    private String maChuyenTau;
    private String tenChuyenTau;
    private String maGaDi;
    private String maGaDen;
    private LocalDateTime thoiGianKhoiHanh;
    private LocalDateTime thoiGianDen;
    
    private ChangTau gaDi;
    private ChangTau gaDen;

    public ChuyenTau() {
    }

    public ChuyenTau(String maChuyenTau, String tenChuyenTau, String maGaDi, String maGaDen, 
                     LocalDateTime thoiGianKhoiHanh, LocalDateTime thoiGianDen) {
        this.maChuyenTau = maChuyenTau;
        this.tenChuyenTau = tenChuyenTau;
        this.maGaDi = maGaDi;
        this.maGaDen = maGaDen;
        this.thoiGianKhoiHanh = thoiGianKhoiHanh;
        this.thoiGianDen = thoiGianDen;
    }

    public String getMaChuyenTau() {
        return maChuyenTau;
    }

    public void setMaChuyenTau(String maChuyenTau) {
        this.maChuyenTau = maChuyenTau;
    }

    public String getTenChuyenTau() {
        return tenChuyenTau;
    }

    public void setTenChuyenTau(String tenChuyenTau) {
        this.tenChuyenTau = tenChuyenTau;
    }

    public String getMaGaDi() {
        return maGaDi;
    }

    public void setMaGaDi(String maGaDi) {
        this.maGaDi = maGaDi;
    }

    public String getMaGaDen() {
        return maGaDen;
    }

    public void setMaGaDen(String maGaDen) {
        this.maGaDen = maGaDen;
    }

    public LocalDateTime getThoiGianKhoiHanh() {
        return thoiGianKhoiHanh;
    }

    public void setThoiGianKhoiHanh(LocalDateTime thoiGianKhoiHanh) {
        this.thoiGianKhoiHanh = thoiGianKhoiHanh;
    }

    public LocalDateTime getThoiGianDen() {
        return thoiGianDen;
    }

    public void setThoiGianDen(LocalDateTime thoiGianDen) {
        this.thoiGianDen = thoiGianDen;
    }

    public ChangTau getGaDi() {
        return gaDi;
    }

    public void setGaDi(ChangTau gaDi) {
        this.gaDi = gaDi;
        if (gaDi != null) {
            this.maGaDi = gaDi.getMaChang();
        }
    }

    public ChangTau getGaDen() {
        return gaDen;
    }

    public void setGaDen(ChangTau gaDen) {
        this.gaDen = gaDen;
        if (gaDen != null) {
            this.maGaDen = gaDen.getMaChang();
        }
    }

    @Override
    public String toString() {
        return "ChuyenTau{" +
                "maChuyenTau='" + maChuyenTau + '\'' +
                ", tenChuyenTau='" + tenChuyenTau + '\'' +
                ", maGaDi='" + maGaDi + '\'' +
                ", maGaDen='" + maGaDen + '\'' +
                ", thoiGianKhoiHanh=" + thoiGianKhoiHanh +
                ", thoiGianDen=" + thoiGianDen +
                '}';
    }
}
