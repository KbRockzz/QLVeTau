package com.trainstation.model;

import lombok.*;
import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class NhanVien implements Serializable {
    private String maNV;
    private String tenNV;
    private String soDienThoai;
    private String diaChi;
    private LocalDate ngaySinh;
    private String maLoaiNV;
    private String trangThai;
}

