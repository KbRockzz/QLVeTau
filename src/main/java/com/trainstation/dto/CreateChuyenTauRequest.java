package com.trainstation.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CreateChuyenTauRequest {
    private String maDauMay;
    private String maNV;
    private String maGaDi;
    private String maGaDen;
    private LocalDateTime gioDi;
    private LocalDateTime gioDen;
    private Integer soKm;
    private String maChang;
}
