package com.trainstation.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UpdateNhanVienRequest {
    private String maNV;
    private String tenNV;
    private String soDienThoai;
    private String diaChi;
    private LocalDate ngaySinh;
    private String maLoaiNV;
    private String trangThai;
}
