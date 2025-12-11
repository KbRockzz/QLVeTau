package com.trainstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Ve implements Serializable {
    private String maVe;
    private String maChuyen;
    private String maLoaiVe;
    private String maSoGhe;
    private String maGaDi;
    private String maGaDen;
    private String tenGaDi;
    private String tenGaDen;
    private LocalDateTime ngayIn;
    private String trangThai;
    private LocalDateTime gioDi;
    private LocalDateTime gioDenDuKien;
    private Integer soToa;
    private String loaiCho;
    private String loaiVe;
    private String maBangGia;
    private Float giaThanhToan;
    private boolean isActive;

    public Ve() {
        this.isActive = true;
    }

    public Ve(String maVe, String maChuyen, String maLoaiVe, String maSoGhe, String maGaDi, String maGaDen,
              String tenGaDi, String tenGaDen, LocalDateTime ngayIn, String trangThai, LocalDateTime gioDi,
              LocalDateTime gioDenDuKien, Integer soToa, String loaiCho, String loaiVe, String maBangGia,
              Float giaThanhToan, boolean isActive) {
        this.maVe = maVe;
        this.maChuyen = maChuyen;
        this.maLoaiVe = maLoaiVe;
        this.maSoGhe = maSoGhe;
        this.maGaDi = maGaDi;
        this.maGaDen = maGaDen;
        this.tenGaDi = tenGaDi;
        this.tenGaDen = tenGaDen;
        this.ngayIn = ngayIn;
        this.trangThai = trangThai;
        this.gioDi = gioDi;
        this.gioDenDuKien = gioDenDuKien;
        this.soToa = soToa;
        this.loaiCho = loaiCho;
        this.loaiVe = loaiVe;
        this.maBangGia = maBangGia;
        this.giaThanhToan = giaThanhToan;
        this.isActive = isActive;
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

    public LocalDateTime getNgayIn() {
        return ngayIn;
    }

    public void setNgayIn(LocalDateTime ngayIn) {
        this.ngayIn = ngayIn;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
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

    public String getTenGaDi() {
        return tenGaDi;
    }

    public void setTenGaDi(String tenGaDi) {
        this.tenGaDi = tenGaDi;
    }

    public String getTenGaDen() {
        return tenGaDen;
    }

    public void setTenGaDen(String tenGaDen) {
        this.tenGaDen = tenGaDen;
    }

    public LocalDateTime getGioDenDuKien() {
        return gioDenDuKien;
    }

    public void setGioDenDuKien(LocalDateTime gioDenDuKien) {
        this.gioDenDuKien = gioDenDuKien;
    }

    public LocalDateTime getGioDi() {
        return gioDi;
    }

    public void setGioDi(LocalDateTime gioDi) {
        this.gioDi = gioDi;
    }

    public Integer getSoToa() {
        return soToa;
    }

    public void setSoToa(Integer soToa) {
        this.soToa = soToa;
    }

    public Float getGiaThanhToan() {
        return giaThanhToan;
    }

    public void setGiaThanhToan(Float giaThanhToan) {
        this.giaThanhToan = giaThanhToan;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
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
    // Chỉ là ví dụ phần liên quan, chèn vào class Ve hiện tại

    private transient com.trainstation.model.ChiTietHoaDon chiTietHoaDon; // transient: không serial hóa / không map DB

    public com.trainstation.model.ChiTietHoaDon getChiTietHoaDon() {
        return chiTietHoaDon;
    }

    public void setChiTietHoaDon(com.trainstation.model.ChiTietHoaDon chiTietHoaDon) {
        this.chiTietHoaDon = chiTietHoaDon;
    }

    /**
     * Trả về giá để hiển thị cho vé này, ưu tiên giá trong chiTietHoaDon (giaDaKM),
     * nếu không có thì tính bằng PricingService (giaCoBan * heSoLoaiVe).
     */
    public float getDisplayPrice() {
        if (chiTietHoaDon != null && chiTietHoaDon.getGiaDaKM() > 0) {
            return chiTietHoaDon.getGiaDaKM();
        }
        // fallback: tính tạm nếu bạn có PricingService sẵn
        try {
            return com.trainstation.service.TinhGiaService.getInstance().tinhGiaChoVe(this).giaDaKM;
        } catch (Exception ex) {
            return 0f;
        }
    }
    @Override
    public String toString() {
        return "Ve{" +
                "maVe='" + maVe + '\'' +
                ", maChuyen='" + maChuyen + '\'' +
                ", maLoaiVe='" + maLoaiVe + '\'' +
                ", maSoGhe='" + maSoGhe + '\'' +
                ", maGaDi='" + maGaDi + '\'' +
                ", maGaDen='" + maGaDen + '\'' +
                ", tenGaDi='" + tenGaDi + '\'' +
                ", tenGaDen='" + tenGaDen + '\'' +
                ", ngayIn=" + ngayIn +
                ", trangThai='" + trangThai + '\'' +
                ", gioDi=" + gioDi +
                ", gioDenDuKien=" + gioDenDuKien +
                ", soToa=" + soToa +
                ", loaiCho='" + loaiCho + '\'' +
                ", loaiVe='" + loaiVe + '\'' +
                ", maBangGia='" + maBangGia + '\'' +
                ", giaThanhToan=" + giaThanhToan +
                ", isActive=" + isActive +
                '}';
    }
}

