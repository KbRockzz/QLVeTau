package com.trainstation.model;

import lombok.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class LoaiVe implements Serializable {
    private String maLoaiVe;
    private String tenLoai;
    private BigDecimal heSoGia;
    private String moTa;
}
