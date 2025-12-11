package com.trainstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Model cho lịch sử đổi vé (audit trail)
 */
public class LichSuDoiVe implements Serializable {
    private String maLichSu;
    private String maVe;
    private String maNV;  // Nhân viên thực hiện
    private LocalDateTime thoiGian;
    private String chiTietCu;  // JSON hoặc text mô tả vé cũ
    private String chiTietMoi;  // JSON hoặc text mô tả vé mới
    private String lyDo;
    private String trangThai;  // "Chờ duyệt", "Đã duyệt", "Từ chối"
    private float chenhLechGia;

    public LichSuDoiVe() {
    }

    public LichSuDoiVe(String maLichSu, String maVe, String maNV, LocalDateTime thoiGian, 
                       String chiTietCu, String chiTietMoi, String lyDo, String trangThai, float chenhLechGia) {
        this.maLichSu = maLichSu;
        this.maVe = maVe;
        this.maNV = maNV;
        this.thoiGian = thoiGian;
        this.chiTietCu = chiTietCu;
        this.chiTietMoi = chiTietMoi;
        this.lyDo = lyDo;
        this.trangThai = trangThai;
        this.chenhLechGia = chenhLechGia;
    }

    public String getMaLichSu() {
        return maLichSu;
    }

    public void setMaLichSu(String maLichSu) {
        this.maLichSu = maLichSu;
    }

    public String getMaVe() {
        return maVe;
    }

    public void setMaVe(String maVe) {
        this.maVe = maVe;
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

    public String getChiTietCu() {
        return chiTietCu;
    }

    public void setChiTietCu(String chiTietCu) {
        this.chiTietCu = chiTietCu;
    }

    public String getChiTietMoi() {
        return chiTietMoi;
    }

    public void setChiTietMoi(String chiTietMoi) {
        this.chiTietMoi = chiTietMoi;
    }

    public String getLyDo() {
        return lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public float getChenhLechGia() {
        return chenhLechGia;
    }

    public void setChenhLechGia(float chenhLechGia) {
        this.chenhLechGia = chenhLechGia;
    }

    @Override
    public String toString() {
        return "LichSuDoiVe{" +
                "maLichSu='" + maLichSu + '\'' +
                ", maVe='" + maVe + '\'' +
                ", maNV='" + maNV + '\'' +
                ", thoiGian=" + thoiGian +
                ", chiTietCu='" + chiTietCu + '\'' +
                ", chiTietMoi='" + chiTietMoi + '\'' +
                ", lyDo='" + lyDo + '\'' +
                ", trangThai='" + trangThai + '\'' +
                ", chenhLechGia=" + chenhLechGia +
                '}';
    }
}
