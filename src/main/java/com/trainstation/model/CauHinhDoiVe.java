package com.trainstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Model cho bảng CauHinhDoiVe - Tham số cấu hình cho đổi vé
 */
public class CauHinhDoiVe implements Serializable {
    private String maCauHinh;
    private String tenCauHinh;
    private Float giaTriSo;
    private String giaTriChuoi;
    private String moTa;
    private LocalDateTime ngayCapNhat;

    public CauHinhDoiVe() {
    }

    public CauHinhDoiVe(String maCauHinh, String tenCauHinh, Float giaTriSo, String moTa) {
        this.maCauHinh = maCauHinh;
        this.tenCauHinh = tenCauHinh;
        this.giaTriSo = giaTriSo;
        this.moTa = moTa;
        this.ngayCapNhat = LocalDateTime.now();
    }

    // Getters and Setters
    public String getMaCauHinh() {
        return maCauHinh;
    }

    public void setMaCauHinh(String maCauHinh) {
        this.maCauHinh = maCauHinh;
    }

    public String getTenCauHinh() {
        return tenCauHinh;
    }

    public void setTenCauHinh(String tenCauHinh) {
        this.tenCauHinh = tenCauHinh;
    }

    public Float getGiaTriSo() {
        return giaTriSo;
    }

    public void setGiaTriSo(Float giaTriSo) {
        this.giaTriSo = giaTriSo;
    }

    public String getGiaTriChuoi() {
        return giaTriChuoi;
    }

    public void setGiaTriChuoi(String giaTriChuoi) {
        this.giaTriChuoi = giaTriChuoi;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public LocalDateTime getNgayCapNhat() {
        return ngayCapNhat;
    }

    public void setNgayCapNhat(LocalDateTime ngayCapNhat) {
        this.ngayCapNhat = ngayCapNhat;
    }
}
