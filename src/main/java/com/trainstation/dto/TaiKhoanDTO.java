package com.trainstation.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TaiKhoanDTO {
    private String maTK;
    private String maNV;
    private String tenTaiKhoan;
    private String trangThai;
}
