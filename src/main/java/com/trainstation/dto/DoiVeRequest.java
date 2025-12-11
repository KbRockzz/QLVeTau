package com.trainstation.dto;

import java.io.Serializable;

/**
 * DTO cho yêu cầu đổi vé
 */
public class DoiVeRequest implements Serializable {
    private String maVeCu;
    private String maChuyenMoi;
    private String maToaMoi;
    private String maGheMoi;
    private String maLoaiVeMoi;
    private String lyDo;
    private String maNV;  // Nhân viên thực hiện
    
    public DoiVeRequest() {
    }

    public DoiVeRequest(String maVeCu, String maChuyenMoi, String maToaMoi, 
                        String maGheMoi, String maLoaiVeMoi, String lyDo, String maNV) {
        this.maVeCu = maVeCu;
        this.maChuyenMoi = maChuyenMoi;
        this.maToaMoi = maToaMoi;
        this.maGheMoi = maGheMoi;
        this.maLoaiVeMoi = maLoaiVeMoi;
        this.lyDo = lyDo;
        this.maNV = maNV;
    }

    public String getMaVeCu() {
        return maVeCu;
    }

    public void setMaVeCu(String maVeCu) {
        this.maVeCu = maVeCu;
    }

    public String getMaChuyenMoi() {
        return maChuyenMoi;
    }

    public void setMaChuyenMoi(String maChuyenMoi) {
        this.maChuyenMoi = maChuyenMoi;
    }

    public String getMaToaMoi() {
        return maToaMoi;
    }

    public void setMaToaMoi(String maToaMoi) {
        this.maToaMoi = maToaMoi;
    }

    public String getMaGheMoi() {
        return maGheMoi;
    }

    public void setMaGheMoi(String maGheMoi) {
        this.maGheMoi = maGheMoi;
    }

    public String getMaLoaiVeMoi() {
        return maLoaiVeMoi;
    }

    public void setMaLoaiVeMoi(String maLoaiVeMoi) {
        this.maLoaiVeMoi = maLoaiVeMoi;
    }

    public String getLyDo() {
        return lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    @Override
    public String toString() {
        return "DoiVeRequest{" +
                "maVeCu='" + maVeCu + '\'' +
                ", maChuyenMoi='" + maChuyenMoi + '\'' +
                ", maToaMoi='" + maToaMoi + '\'' +
                ", maGheMoi='" + maGheMoi + '\'' +
                ", maLoaiVeMoi='" + maLoaiVeMoi + '\'' +
                ", lyDo='" + lyDo + '\'' +
                ", maNV='" + maNV + '\'' +
                '}';
    }
}
