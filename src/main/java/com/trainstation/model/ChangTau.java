package com.trainstation.model;

import lombok.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangTau implements Serializable {
    private String maChang;
    private Integer soKMToiThieu;
    private Integer soKMToiDa;
    private String moTa;
    private Float giaTien;

    @Override
    public String toString() {
        return maChang;
    }
}
