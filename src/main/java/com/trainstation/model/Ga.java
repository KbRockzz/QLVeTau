package com.trainstation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "Ga")
public class Ga implements Serializable {
    @Id
    @Column(name = "maGa")
    private String maGa;

    @Column(name = "tenGa")
    private String tenGa;

    @Column(name = "moTa")
    private String moTa;

    @Column(name = "tinhTrang")
    private String tinhTrang;

    @Column(name = "diaChi")
    private String diaChi;
}
