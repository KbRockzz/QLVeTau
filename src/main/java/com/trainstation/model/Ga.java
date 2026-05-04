package com.trainstation.model;

import lombok.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Ga implements Serializable {
    private String maGa;
    private String tenGa;
    private String moTa;
    private String tinhTrang;
    private String diaChi;
}
