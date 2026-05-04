package com.trainstation.model;

import lombok.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ToaTau implements Serializable {
    private String maToa;
    private String loaiToa;
    private Integer samSX;
    private String trangThai;
    private Integer sucChua;
}

