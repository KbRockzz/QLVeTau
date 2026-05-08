package com.trainstation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "ChuyenTau")
public class ChuyenTau implements Serializable {
    @Id
    @Column(name = "maChuyen")
    private String maChuyen;

    @Column(name = "maDauMay")
    private String maDauMay;

    @Column(name = "maNV")
    private String maNV;

    @Column(name = "maGaDi")
    private String maGaDi;

    @Column(name = "maGaDen")
    private String maGaDen;

    @Column(name = "gioDi")
    private LocalDateTime gioDi;

    @Column(name = "gioDen")
    private LocalDateTime gioDen;

    @Column(name = "soKm")
    private Integer soKm;

    @Column(name = "maChang")
    private String maChang;

    @Column(name = "trangThai")
    private String trangThai;
}
