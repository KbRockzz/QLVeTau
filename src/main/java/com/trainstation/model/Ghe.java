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
@Table(name = "Ghe")
public class Ghe implements Serializable {
    @Id
    @Column(name = "maGhe")
    private String maGhe;

    @Column(name = "maToa")
    private String maToa;

    @Column(name = "loaiGhe")
    private String loaiGhe;

    @Column(name = "trangThai")
    private String trangThai;
}

