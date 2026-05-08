package com.trainstation.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class LoaiVeDTO {
    private String maLoaiVe;
    private String tenLoai;
    private BigDecimal heSoGia;
    private String moTa;
}
