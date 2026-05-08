package com.trainstation.model;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ChiTietHoaDonId implements Serializable {
    private String maHoaDon;
    private String maVe;
}
