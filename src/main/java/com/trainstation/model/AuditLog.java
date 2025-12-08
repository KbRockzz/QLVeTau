package com.trainstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Model cho bảng AuditLog - Ghi lại thao tác người dùng
 */
public class AuditLog implements Serializable {
    private String maLog;
    private String loaiThaoTac;
    private String maThamChieu;
    private String maNV;
    private LocalDateTime thoiGian;
    private String noiDung;
    private String duLieuTruoc;
    private String duLieuSau;
    private String diaChiIP;

    public AuditLog() {
    }

    public AuditLog(String maLog, String loaiThaoTac, String maThamChieu, String maNV, 
                    LocalDateTime thoiGian, String noiDung) {
        this.maLog = maLog;
        this.loaiThaoTac = loaiThaoTac;
        this.maThamChieu = maThamChieu;
        this.maNV = maNV;
        this.thoiGian = thoiGian;
        this.noiDung = noiDung;
    }

    // Getters and Setters
    public String getMaLog() {
        return maLog;
    }

    public void setMaLog(String maLog) {
        this.maLog = maLog;
    }

    public String getLoaiThaoTac() {
        return loaiThaoTac;
    }

    public void setLoaiThaoTac(String loaiThaoTac) {
        this.loaiThaoTac = loaiThaoTac;
    }

    public String getMaThamChieu() {
        return maThamChieu;
    }

    public void setMaThamChieu(String maThamChieu) {
        this.maThamChieu = maThamChieu;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public LocalDateTime getThoiGian() {
        return thoiGian;
    }

    public void setThoiGian(LocalDateTime thoiGian) {
        this.thoiGian = thoiGian;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public String getDuLieuTruoc() {
        return duLieuTruoc;
    }

    public void setDuLieuTruoc(String duLieuTruoc) {
        this.duLieuTruoc = duLieuTruoc;
    }

    public String getDuLieuSau() {
        return duLieuSau;
    }

    public void setDuLieuSau(String duLieuSau) {
        this.duLieuSau = duLieuSau;
    }

    public String getDiaChiIP() {
        return diaChiIP;
    }

    public void setDiaChiIP(String diaChiIP) {
        this.diaChiIP = diaChiIP;
    }
}
