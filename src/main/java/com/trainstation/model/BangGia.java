package com.trainstation.model;

import java.io.Serializable;
import java.time.LocalDate;

public class BangGia implements Serializable {
    private String maBangGia;
    private String maLoaiGhe;
    private String maLoaiVe;
    private double giaTien;
    private LocalDate ngayApDung;
    private LocalDate ngayKetThuc;
    
    private CarriageType loaiGhe;
    private LoaiVe loaiVe;

    public BangGia() {
    }

    public BangGia(String maBangGia, String maLoaiGhe, String maLoaiVe, double giaTien, 
                   LocalDate ngayApDung, LocalDate ngayKetThuc) {
        this.maBangGia = maBangGia;
        this.maLoaiGhe = maLoaiGhe;
        this.maLoaiVe = maLoaiVe;
        this.giaTien = giaTien;
        this.ngayApDung = ngayApDung;
        this.ngayKetThuc = ngayKetThuc;
    }

    public String getMaBangGia() {
        return maBangGia;
    }

    public void setMaBangGia(String maBangGia) {
        this.maBangGia = maBangGia;
    }

    public String getMaLoaiGhe() {
        return maLoaiGhe;
    }

    public void setMaLoaiGhe(String maLoaiGhe) {
        this.maLoaiGhe = maLoaiGhe;
    }

    public String getMaLoaiVe() {
        return maLoaiVe;
    }

    public void setMaLoaiVe(String maLoaiVe) {
        this.maLoaiVe = maLoaiVe;
    }

    public double getGiaTien() {
        return giaTien;
    }

    public void setGiaTien(double giaTien) {
        this.giaTien = giaTien;
    }

    public LocalDate getNgayApDung() {
        return ngayApDung;
    }

    public void setNgayApDung(LocalDate ngayApDung) {
        this.ngayApDung = ngayApDung;
    }

    public LocalDate getNgayKetThuc() {
        return ngayKetThuc;
    }

    public void setNgayKetThuc(LocalDate ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }

    public CarriageType getLoaiGhe() {
        return loaiGhe;
    }

    public void setLoaiGhe(CarriageType loaiGhe) {
        this.loaiGhe = loaiGhe;
        if (loaiGhe != null) {
            this.maLoaiGhe = loaiGhe.getCarriageTypeId();
        }
    }

    public LoaiVe getLoaiVe() {
        return loaiVe;
    }

    public void setLoaiVe(LoaiVe loaiVe) {
        this.loaiVe = loaiVe;
        if (loaiVe != null) {
            this.maLoaiVe = loaiVe.getMaLoaiVe();
        }
    }

    @Override
    public String toString() {
        return "BangGia{" +
                "maBangGia='" + maBangGia + '\'' +
                ", maLoaiGhe='" + maLoaiGhe + '\'' +
                ", maLoaiVe='" + maLoaiVe + '\'' +
                ", giaTien=" + giaTien +
                ", ngayApDung=" + ngayApDung +
                ", ngayKetThuc=" + ngayKetThuc +
                '}';
    }
}
