package com.trainstation.model;

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
public class ChiTietHoaDon implements Serializable {
    private String maHoaDon;
    private String maVe;
    private String maLoaiVe;
    private Float giaGoc;
    private Float giaDaKM;
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