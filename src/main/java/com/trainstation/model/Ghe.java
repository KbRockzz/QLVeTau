package com.trainstation.model;

import lombok.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Ghe implements Serializable {
    private String maGhe;
    private String maToa;
    private String loaiGhe;
    private String trangThai;
}

