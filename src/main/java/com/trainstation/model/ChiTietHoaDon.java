package com.trainstation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "ChiTietHoaDon")
@IdClass(ChiTietHoaDonId.class)
public class ChiTietHoaDon implements Serializable {
    @Id
    @Column(name = "maHoaDon")
    private String maHoaDon;

    @Id
    @Column(name = "maVe")
    private String maVe;

    @Column(name = "maLoaiVe")
    private String maLoaiVe;

    @Column(name = "giaGoc")
    private Float giaGoc;

    @Column(name = "giaDaKM")
    private Float giaDaKM;

    @Column(name = "moTa")
    private String moTa;

    public void tinhVaGanGiaDaKM(float heSo) {
        this.giaDaKM = lamTronGia(this.giaGoc * heSo);
    }

    public void tinhVaGanGiaDaKM(float giaCoBan, float heSo) {
        this.giaGoc = lamTronGia(giaCoBan);
        this.giaDaKM = lamTronGia(giaCoBan * heSo);
    }

    private float lamTronGia(double value) {
        BigDecimal bd = BigDecimal.valueOf(value).setScale(0, RoundingMode.HALF_UP);
        return bd.floatValue();
    }
}
