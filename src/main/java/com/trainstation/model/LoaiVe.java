package com.trainstation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "LoaiVe")
public class LoaiVe implements Serializable {
    @Id
    @Column(name = "maLoaiVe")
    private String maLoaiVe;

    @Column(name = "tenLoai")
    private String tenLoai;

    @Column(name = "heSoGia")
    private BigDecimal heSoGia;

    @Column(name = "moTa")
    private String moTa;
}
