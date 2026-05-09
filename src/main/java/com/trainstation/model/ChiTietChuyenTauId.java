package com.trainstation.model;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ChiTietChuyenTauId implements Serializable {
    private String maChuyenTau;
    private String maToaTau;
}
