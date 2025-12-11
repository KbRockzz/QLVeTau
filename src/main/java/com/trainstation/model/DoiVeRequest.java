package com.trainstation.model;

import java.io.Serializable;

/**
 * DTO for ticket exchange request
 */
public class DoiVeRequest implements Serializable {
    private String maVeCu;
    private String maChuyen;
    private String maToa;
    private String maGhe;
    private String maLoaiVe;
    private String lyDo;
    private String phuongThucThanhToan;
    private String maNhanVien;

    public DoiVeRequest() {
    }

    public DoiVeRequest(String maVeCu, String maChuyen, String maToa, String maGhe, String maLoaiVe, String lyDo, String phuongThucThanhToan, String maNhanVien) {
        this.maVeCu = maVeCu;
        this.maChuyen = maChuyen;
        this.maToa = maToa;
        this.maGhe = maGhe;
        this.maLoaiVe = maLoaiVe;
        this.lyDo = lyDo;
        this.phuongThucThanhToan = phuongThucThanhToan;
        this.maNhanVien = maNhanVien;
    }

    public String getMaVeCu() {
        return maVeCu;
    }

    public void setMaVeCu(String maVeCu) {
        this.maVeCu = maVeCu;
    }

    public String getMaChuyen() {
        return maChuyen;
    }

    public void setMaChuyen(String maChuyen) {
        this.maChuyen = maChuyen;
    }

    public String getMaToa() {
        return maToa;
    }

    public void setMaToa(String maToa) {
        this.maToa = maToa;
    }

    public String getMaGhe() {
        return maGhe;
    }

    public void setMaGhe(String maGhe) {
        this.maGhe = maGhe;
    }

    public String getMaLoaiVe() {
        return maLoaiVe;
    }

    public void setMaLoaiVe(String maLoaiVe) {
        this.maLoaiVe = maLoaiVe;
    }

    public String getLyDo() {
        return lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }

    public String getPhuongThucThanhToan() {
        return phuongThucThanhToan;
    }

    public void setPhuongThucThanhToan(String phuongThucThanhToan) {
        this.phuongThucThanhToan = phuongThucThanhToan;
    }

    public String getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    @Override
    public String toString() {
        return "DoiVeRequest{" +
                "maVeCu='" + maVeCu + '\'' +
                ", maChuyen='" + maChuyen + '\'' +
                ", maToa='" + maToa + '\'' +
                ", maGhe='" + maGhe + '\'' +
                ", maLoaiVe='" + maLoaiVe + '\'' +
                ", lyDo='" + lyDo + '\'' +
                ", phuongThucThanhToan='" + phuongThucThanhToan + '\'' +
                ", maNhanVien='" + maNhanVien + '\'' +
                '}';
    }
}
