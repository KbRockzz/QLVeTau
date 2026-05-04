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
public class BangGia implements Serializable {
    private String maBangGia;
    private String maChang;
    private String loaiGhe;
    private Float giaCoBan;
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;
}
