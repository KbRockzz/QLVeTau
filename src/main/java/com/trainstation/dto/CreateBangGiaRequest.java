package com.trainstation.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CreateBangGiaRequest {
    private String maChang;
    private String loaiGhe;
    private Float giaCoBan;
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;
}
