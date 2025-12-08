package com.trainstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Model cho bảng VeHistory - Lưu lịch sử thay đổi vé
 */
public class VeHistory implements Serializable {
    private String maLichSu;
    private String maVe;
    private String maChuyen;
    private String maLoaiVe;
    private String maSoGhe;
    private String trangThai;
    private String gaDi;
    private String gaDen;
    private LocalDateTime gioDi;
    private String soToa;
    private String loaiCho;
    private String loaiVe;
    private String maBangGia;
    private LocalDateTime ngayThayDoi;
    private String loaiThayDoi; // 'DOI_VE', 'HOAN_VE', 'HUY_VE'
    private String nguoiThucHien;
    private String ghiChu;

    public VeHistory() {
    }

    public VeHistory(String maLichSu, String maVe, LocalDateTime ngayThayDoi, String loaiThayDoi, String nguoiThucHien) {
        this.maLichSu = maLichSu;
        this.maVe = maVe;
        this.ngayThayDoi = ngayThayDoi;
        this.loaiThayDoi = loaiThayDoi;
        this.nguoiThucHien = nguoiThucHien;
    }

    // Getters and Setters
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

    public String getMaChuyen() {
        return maChuyen;
    }

    public void setMaChuyen(String maChuyen) {
        this.maChuyen = maChuyen;
    }

    public String getMaLoaiVe() {
        return maLoaiVe;
    }

    public void setMaLoaiVe(String maLoaiVe) {
        this.maLoaiVe = maLoaiVe;
    }

    public String getMaSoGhe() {
        return maSoGhe;
    }

    public void setMaSoGhe(String maSoGhe) {
        this.maSoGhe = maSoGhe;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
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

    public String getSoToa() {
        return soToa;
    }

    public void setSoToa(String soToa) {
        this.soToa = soToa;
    }

    public String getLoaiCho() {
        return loaiCho;
    }

    public void setLoaiCho(String loaiCho) {
        this.loaiCho = loaiCho;
    }

    public String getLoaiVe() {
        return loaiVe;
    }

    public void setLoaiVe(String loaiVe) {
        this.loaiVe = loaiVe;
    }

    public String getMaBangGia() {
        return maBangGia;
    }

    public void setMaBangGia(String maBangGia) {
        this.maBangGia = maBangGia;
    }

    public LocalDateTime getNgayThayDoi() {
        return ngayThayDoi;
    }

    public void setNgayThayDoi(LocalDateTime ngayThayDoi) {
        this.ngayThayDoi = ngayThayDoi;
    }

    public String getLoaiThayDoi() {
        return loaiThayDoi;
    }

    public void setLoaiThayDoi(String loaiThayDoi) {
        this.loaiThayDoi = loaiThayDoi;
    }

    public String getNguoiThucHien() {
        return nguoiThucHien;
    }

    public void setNguoiThucHien(String nguoiThucHien) {
        this.nguoiThucHien = nguoiThucHien;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    /**
     * Tạo VeHistory từ Ve hiện tại
     */
    public static VeHistory fromVe(Ve ve, String maLichSu, String loaiThayDoi, String nguoiThucHien, String ghiChu) {
        VeHistory history = new VeHistory();
        history.setMaLichSu(maLichSu);
        history.setMaVe(ve.getMaVe());
        history.setMaChuyen(ve.getMaChuyen());
        history.setMaLoaiVe(ve.getMaLoaiVe());
        history.setMaSoGhe(ve.getMaSoGhe());
        history.setTrangThai(ve.getTrangThai());
        history.setGaDi(ve.getGaDi());
        history.setGaDen(ve.getGaDen());
        history.setGioDi(ve.getGioDi());
        history.setSoToa(ve.getSoToa());
        history.setLoaiCho(ve.getLoaiCho());
        history.setLoaiVe(ve.getLoaiVe());
        history.setMaBangGia(ve.getMaBangGia());
        history.setNgayThayDoi(LocalDateTime.now());
        history.setLoaiThayDoi(loaiThayDoi);
        history.setNguoiThucHien(nguoiThucHien);
        history.setGhiChu(ghiChu);
        return history;
    }
}
