package com.trainstation.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ChiTietHoaDonDTO {
    private String maHoaDon;
    private String maVe;
    private String maLoaiVe;
    private Float giaGoc;
    private Float giaDaKM;
    private String moTa;
}
