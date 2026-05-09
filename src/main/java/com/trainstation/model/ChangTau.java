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
@Table(name = "ChangTau")
public class ChangTau implements Serializable {
    @Id
    @Column(name = "maChang")
    private String maChang;

    @Column(name = "soKMToiThieu")
    private Integer soKMToiThieu;

    @Column(name = "soKMToiDa")
    private Integer soKMToiDa;

    @Column(name = "moTa")
    private String moTa;

    @Column(name = "giaTien")
    private Float giaTien;

    @Override
    public String toString() {
        return maChang;
    }
}
