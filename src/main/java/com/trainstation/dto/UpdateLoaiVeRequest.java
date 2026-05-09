package com.trainstation.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UpdateLoaiVeRequest {
    private String tenLoai;
    private BigDecimal heSoGia;
    private String moTa;
}
