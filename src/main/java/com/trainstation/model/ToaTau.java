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
@Table(name = "ToaTau")
public class ToaTau implements Serializable {
    @Id
    @Column(name = "maToa")
    private String maToa;

    @Column(name = "loaiToa")
    private String loaiToa;

    @Column(name = "samSX")
    private Integer samSX;

    @Column(name = "trangThai")
    private String trangThai;

    @Column(name = "sucChua")
    private Integer sucChua;
}

