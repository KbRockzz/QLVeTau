package com.trainstation.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ChangTauDTO {
    private String maChang;
    private Integer soKMToiThieu;
    private Integer soKMToiDa;
    private String moTa;
    private Float giaTien;
}
