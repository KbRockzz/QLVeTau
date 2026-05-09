package com.trainstation.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class LoaiGheDTO {
    private String maLoai;
    private String tenLoai;
    private String moTa;
}
