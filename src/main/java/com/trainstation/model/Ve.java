package com.trainstation.model;

import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
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

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private transient ChiTietHoaDon chiTietHoaDon;

    @Builder
    public Ve(String maVe, String maChuyen, String maLoaiVe, String maSoGhe, String maGaDi, String maGaDen,
              String tenGaDi, String tenGaDen, LocalDateTime ngayIn, String trangThai, LocalDateTime gioDi,
              LocalDateTime gioDenDuKien, Integer soToa, String loaiCho, String loaiVe, String maBangGia,
              Float giaThanhToan) {
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
    }

    public ChiTietHoaDon getChiTietHoaDon() {
        return chiTietHoaDon;
    }

    public void setChiTietHoaDon(ChiTietHoaDon chiTietHoaDon) {
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
        try {
            return com.trainstation.service.TinhGiaService.getInstance().tinhGiaChoVe(this).giaDaKM;
        } catch (Exception ex) {
            return 0f;
        }
    }
}
