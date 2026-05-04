package com.trainstation.model;

import lombok.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoaiGhe implements Serializable {
    private String maLoai;
    private String tenLoai;
    private String moTa;

    @Override
    public String toString() {
        return tenLoai;
    }
}
