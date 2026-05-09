package com.trainstation.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CreateToaTauRequest {
    private String loaiToa;
    private Integer samSX;
    private String trangThai;
    private Integer sucChua;
}
