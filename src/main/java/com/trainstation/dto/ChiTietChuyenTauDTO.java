package com.trainstation.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ChiTietChuyenTauDTO {
    private String maChuyenTau;
    private String maToaTau;
    private Integer soThuTuToa;
    private Integer sucChua;
}
