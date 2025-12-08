package com.trainstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Model cho bảng GiaoDichDoiVe - Giao dịch đổi vé
 */
public class GiaoDichDoiVe implements Serializable {
    private String maGiaoDich;
    private String maVeCu;
    private String maVeMoi;
    private LocalDateTime ngayDoi;
    private String maNV;
    private String maKH;
    
    // Thông tin tài chính
    private float giaVeCu;
    private float giaVeMoi;
    private float phiDoiVe;
    private float chenhLechGia;
    private float soTienThu; // Số tiền phải thu thêm (nếu > 0)
    private float soTienHoan; // Số tiền hoàn lại (nếu > 0)
    
    // Thông tin phê duyệt
    private String trangThai; // 'CHO_DUYET', 'DA_DUYET', 'TU_CHOI', 'HOAN_THANH'
    private String nguoiDuyet;
    private LocalDateTime ngayDuyet;
    private String lyDoTuChoi;
    private String ghiChu;

    public GiaoDichDoiVe() {
    }

    public GiaoDichDoiVe(String maGiaoDich, String maVeCu, String maVeMoi, LocalDateTime ngayDoi, 
                         String maNV, String maKH, String trangThai) {
        this.maGiaoDich = maGiaoDich;
        this.maVeCu = maVeCu;
        this.maVeMoi = maVeMoi;
        this.ngayDoi = ngayDoi;
        this.maNV = maNV;
        this.maKH = maKH;
        this.trangThai = trangThai;
    }

    // Getters and Setters
    public String getMaGiaoDich() {
        return maGiaoDich;
    }

    public void setMaGiaoDich(String maGiaoDich) {
        this.maGiaoDich = maGiaoDich;
    }

    public String getMaVeCu() {
        return maVeCu;
    }

    public void setMaVeCu(String maVeCu) {
        this.maVeCu = maVeCu;
    }

    public String getMaVeMoi() {
        return maVeMoi;
    }

    public void setMaVeMoi(String maVeMoi) {
        this.maVeMoi = maVeMoi;
    }

    public LocalDateTime getNgayDoi() {
        return ngayDoi;
    }

    public void setNgayDoi(LocalDateTime ngayDoi) {
        this.ngayDoi = ngayDoi;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public float getGiaVeCu() {
        return giaVeCu;
    }

    public void setGiaVeCu(float giaVeCu) {
        this.giaVeCu = giaVeCu;
    }

    public float getGiaVeMoi() {
        return giaVeMoi;
    }

    public void setGiaVeMoi(float giaVeMoi) {
        this.giaVeMoi = giaVeMoi;
    }

    public float getPhiDoiVe() {
        return phiDoiVe;
    }

    public void setPhiDoiVe(float phiDoiVe) {
        this.phiDoiVe = phiDoiVe;
    }

    public float getChenhLechGia() {
        return chenhLechGia;
    }

    public void setChenhLechGia(float chenhLechGia) {
        this.chenhLechGia = chenhLechGia;
    }

    public float getSoTienThu() {
        return soTienThu;
    }

    public void setSoTienThu(float soTienThu) {
        this.soTienThu = soTienThu;
    }

    public float getSoTienHoan() {
        return soTienHoan;
    }

    public void setSoTienHoan(float soTienHoan) {
        this.soTienHoan = soTienHoan;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getNguoiDuyet() {
        return nguoiDuyet;
    }

    public void setNguoiDuyet(String nguoiDuyet) {
        this.nguoiDuyet = nguoiDuyet;
    }

    public LocalDateTime getNgayDuyet() {
        return ngayDuyet;
    }

    public void setNgayDuyet(LocalDateTime ngayDuyet) {
        this.ngayDuyet = ngayDuyet;
    }

    public String getLyDoTuChoi() {
        return lyDoTuChoi;
    }

    public void setLyDoTuChoi(String lyDoTuChoi) {
        this.lyDoTuChoi = lyDoTuChoi;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    /**
     * Tính tổng số tiền cần thu hoặc hoàn
     */
    public float getTongTienCanThu() {
        return phiDoiVe + chenhLechGia;
    }

    /**
     * Kiểm tra xem có cần thu thêm tiền không
     */
    public boolean canThuThem() {
        return getTongTienCanThu() > 0;
    }

    /**
     * Kiểm tra xem có cần hoàn tiền không
     */
    public boolean canHoanTien() {
        return getTongTienCanThu() < 0;
    }
}
