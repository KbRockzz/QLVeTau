package com.trainstation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "Ve")
public class Ve implements Serializable {
    @Id
    @Column(name = "maVe")
    private String maVe;

    @Column(name = "maChuyen")
    private String maChuyen;

    @Column(name = "maLoaiVe")
    private String maLoaiVe;

    @Column(name = "maSoGhe")
    private String maSoGhe;

    @Column(name = "maGaDi")
    private String maGaDi;

    @Column(name = "maGaDen")
    private String maGaDen;

    @Column(name = "tenGaDi")
    private String tenGaDi;

    @Column(name = "tenGaDen")
    private String tenGaDen;

    @Column(name = "ngayIn")
    private LocalDateTime ngayIn;

    @Column(name = "trangThai")
    private String trangThai;

    @Column(name = "gioDi")
    private LocalDateTime gioDi;

    @Column(name = "gioDenDuKien")
    private LocalDateTime gioDenDuKien;

    @Column(name = "soToa")
    private Integer soToa;

    @Column(name = "loaiCho")
    private String loaiCho;

    @Column(name = "loaiVe")
    private String loaiVe;

    @Column(name = "maBangGia")
    private String maBangGia;

    @Column(name = "giaThanhToan")
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
