package com.trainstation.model;

import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class DauMay implements Serializable {
    private String maDauMay;
    private String loaiDauMay;
    private String tenDauMay;
    private Integer namSX;
    private LocalDateTime lanBaoTriGanNhat;
    private String trangThai;
}
