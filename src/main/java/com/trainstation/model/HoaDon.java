package com.trainstation.model;

import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class HoaDon implements Serializable {
    private String maHoaDon;
    private String maNV;
    private String maKH;
    private String tenKH;
    private String soDienThoai;
    private LocalDateTime ngayLap;
    private String phuongThucThanhToan;
    private String trangThai;
}
