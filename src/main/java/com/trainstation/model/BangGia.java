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
@Table(name = "BangGia")
public class BangGia implements Serializable {
    @Id
    @Column(name = "maBangGia")
    private String maBangGia;

    @Column(name = "maChang")
    private String maChang;

    @Column(name = "loaiGhe")
    private String loaiGhe;

    @Column(name = "giaCoBan")
    private Float giaCoBan;

    @Column(name = "ngayBatDau")
    private LocalDateTime ngayBatDau;

    @Column(name = "ngayKetThuc")
    private LocalDateTime ngayKetThuc;
}
