package com.trainstation.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CreateTaiKhoanRequest {
    private String maNV;
    private String tenTaiKhoan;
    private String matKhau;
    private String trangThai;
}
