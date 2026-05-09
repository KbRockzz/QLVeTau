package com.trainstation.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class DauMayDTO {
    private String maDauMay;
    private String loaiDauMay;
    private String tenDauMay;
    private Integer namSX;
    private LocalDateTime lanBaoTriGanNhat;
    private String trangThai;
}
