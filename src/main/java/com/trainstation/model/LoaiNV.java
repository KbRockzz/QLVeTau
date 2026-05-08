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
@Entity
@Table(name = "LoaiNV")
public class LoaiNV implements Serializable {
    @Id
    @Column(name = "maLoai")
    private String maLoai;

    @Column(name = "tenLoai")
    private String tenLoai;

    @Column(name = "moTa")
    private String moTa;

    @Override
    public String toString() {
        return tenLoai;
    }
}
