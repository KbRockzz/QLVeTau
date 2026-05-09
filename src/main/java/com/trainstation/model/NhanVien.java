package com.trainstation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "NhanVien")
public class NhanVien implements Serializable {
    @Id
    @Column(name = "maNV")
    private String maNV;

    @Column(name = "tenNV")
    private String tenNV;

    @Column(name = "soDienThoai")
    private String soDienThoai;

    @Column(name = "diaChi")
    private String diaChi;

    @Column(name = "ngaySinh")
    private LocalDate ngaySinh;

    @Column(name = "maLoaiNV")
    private String maLoaiNV;

    @Column(name = "trangThai")
    private String trangThai;
}
