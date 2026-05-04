package com.trainstation.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class VeDTO {
    private String maVe;
    private String maChuyen;
    private String maLoaiVe;
    private String maSoGhe;
    private String maGaDi;
    private String maGaDen;
    private String tenGaDi;
    private String tenGaDen;
    private LocalDateTime ngayIn;
    private String trangThai;
    private LocalDateTime gioDi;
    private LocalDateTime gioDenDuKien;
    private Integer soToa;
    private String loaiCho;
    private String loaiVe;
    private String maBangGia;
    private Float giaThanhToan;
}
