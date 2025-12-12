package com.trainstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Ve implements Serializable {
    private String maVe;
    private String maChuyen;
    private String maLoaiVe;
    private String maSoGhe;
    private LocalDateTime ngayIn;
    private String trangThai;
    private String gaDi;
    private String gaDen;
    private LocalDateTime gioDi;
    private String soToa;
    private String loaiCho;
    private String loaiVe;
    private String maBangGia;

    public Ve() {
    }

    public Ve(String maVe, String maChuyen, String maLoaiVe, String maSoGhe, LocalDateTime ngayIn, String trangThai, String gaDi, String gaDen, LocalDateTime gioDi, String soToa, String loaiCho, String loaiVe, String maBangGia) {
        this.maVe = maVe;
        this.maChuyen = maChuyen;
        this.maLoaiVe = maLoaiVe;
        this.maSoGhe = maSoGhe;
        this.ngayIn = ngayIn;
        this.trangThai = trangThai;
        this.gaDi = gaDi;
        this.gaDen = gaDen;
        this.gioDi = gioDi;
        this.soToa = soToa;
        this.loaiCho = loaiCho;
        this.loaiVe = loaiVe;
        this.maBangGia = maBangGia;
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
                ", ngayIn=" + ngayIn +
                ", trangThai='" + trangThai + '\'' +
                ", gaDi='" + gaDi + '\'' +
                ", gaDen='" + gaDen + '\'' +
                ", gioDi=" + gioDi +
                ", soToa='" + soToa + '\'' +
                ", loaiCho='" + loaiCho + '\'' +
                ", loaiVe='" + loaiVe + '\'' +
                ", maBangGia='" + maBangGia + '\'' +
                '}';
    }
}

