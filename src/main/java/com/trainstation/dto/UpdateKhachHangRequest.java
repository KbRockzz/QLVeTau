package com.trainstation.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UpdateKhachHangRequest {
    private String maKhachHang;
    private String tenKhachHang;
    private String email;
    private String soDienThoai;
}
