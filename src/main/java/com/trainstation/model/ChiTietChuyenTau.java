package com.trainstation.model;

import lombok.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ChiTietChuyenTau implements Serializable {
    private String maChuyenTau;
    private String maToaTau;
    private Integer soThuTuToa;
    private Integer sucChua;
}
