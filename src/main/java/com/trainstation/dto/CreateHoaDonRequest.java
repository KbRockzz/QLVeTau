package com.trainstation.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CreateHoaDonRequest {
    private String maNV;
    private String maKH;
    private String tenKH;
    private String soDienThoai;
    private LocalDateTime ngayLap;
    private String phuongThucThanhToan;
    private String trangThai;
}
