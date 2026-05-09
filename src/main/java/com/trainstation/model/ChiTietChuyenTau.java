package com.trainstation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
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
@Table(name = "ChiTietChuyenTau")
@IdClass(ChiTietChuyenTauId.class)
public class ChiTietChuyenTau implements Serializable {
    @Id
    @Column(name = "maChuyenTau")
    private String maChuyenTau;

    @Id
    @Column(name = "maToaTau")
    private String maToaTau;

    @Column(name = "soThuTuToa")
    private Integer soThuTuToa;

    @Column(name = "sucChua")
    private Integer sucChua;
}
