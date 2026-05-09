package com.trainstation.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UpdateGaRequest {
    private String maGa;
    private String tenGa;
    private String moTa;
    private String tinhTrang;
    private String diaChi;
}
