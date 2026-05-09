package com.trainstation.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class BangGiaDTO {
    private String maBangGia;
    private String maChang;
    private String loaiGhe;
    private Float giaCoBan;
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;
}
