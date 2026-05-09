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
@Table(name = "HoaDon")
public class HoaDon implements Serializable {
    @Id
    @Column(name = "maHoaDon")
    private String maHoaDon;

    @Column(name = "maNV")
    private String maNV;

    @Column(name = "maKH")
    private String maKH;

    @Column(name = "tenKH")
    private String tenKH;

    @Column(name = "soDienThoai")
    private String soDienThoai;

    @Column(name = "ngayLap")
    private LocalDateTime ngayLap;

    @Column(name = "phuongThucThanhToan")
    private String phuongThucThanhToan;

    @Column(name = "trangThai")
    private String trangThai;
}
