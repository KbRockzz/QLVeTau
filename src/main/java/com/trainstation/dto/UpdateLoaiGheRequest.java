package com.trainstation.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UpdateLoaiGheRequest {
    private String tenLoai;
    private String moTa;
}
