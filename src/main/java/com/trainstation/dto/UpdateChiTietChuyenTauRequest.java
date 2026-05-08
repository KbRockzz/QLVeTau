package com.trainstation.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UpdateChiTietChuyenTauRequest {
    private Integer soThuTuToa;
    private Integer sucChua;
}
