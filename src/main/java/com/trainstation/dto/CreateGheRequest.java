package com.trainstation.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CreateGheRequest {
    private String maToa;
    private String loaiGhe;
    private String trangThai;
}
