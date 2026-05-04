package com.trainstation.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CreateVeRequest {
    private String maChuyen;
    private String maLoaiVe;
    private String maSoGhe;
    private String maGaDi;
    private String maGaDen;
    private String loaiCho;
    private Float giaThanhToan;
}
