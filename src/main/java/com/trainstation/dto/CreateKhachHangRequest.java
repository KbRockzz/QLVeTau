package com.trainstation.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CreateKhachHangRequest {
    private String tenKhachHang;
    private String email;
    private String soDienThoai;
}
