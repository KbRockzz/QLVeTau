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
@Table(name = "DauMay")
public class DauMay implements Serializable {
    @Id
    @Column(name = "maDauMay")
    private String maDauMay;

    @Column(name = "loaiDauMay")
    private String loaiDauMay;

    @Column(name = "tenDauMay")
    private String tenDauMay;

    @Column(name = "namSX")
    private Integer namSX;

    @Column(name = "lanBaoTriGanNhat")
    private LocalDateTime lanBaoTriGanNhat;

    @Column(name = "trangThai")
    private String trangThai;
}
