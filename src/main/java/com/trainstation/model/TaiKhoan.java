package com.trainstation.model;

import com.trainstation.persistence.JpaEntityManagerProvider;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "TaiKhoan")
public class TaiKhoan implements Serializable {
    @Id
    @Column(name = "maTK")
    private String maTK;

    @Column(name = "maNV")
    private String maNV;

    @Column(name = "tenTaiKhoan")
    private String tenTaiKhoan;

    @Column(name = "matKhau")
    private String matKhau;

    @Column(name = "trangThai")
    private String trangThai;

    @Transient
    private String maLoaiNV;

    public TaiKhoan(String maTK, String maNV, String tenTaiKhoan, String matKhau, String trangThai) {
        this.maTK = maTK;
        this.maNV = maNV;
        this.tenTaiKhoan = tenTaiKhoan;
        this.matKhau = matKhau;
        this.trangThai = trangThai;
    }

    /**
     * Kiểm tra xem tài khoản có phải quản lý không
     * Nhân viên loại LNV02 (Quản lý) và LNV03 (Admin) có quyền quản lý
     */
    public boolean isManager() {
        if (maLoaiNV != null && !maLoaiNV.isBlank()) {
            return isManagerRole(maLoaiNV);
        }
        if (maNV == null) {
            return false;
        }
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            Object loaiNV = em.createNativeQuery(
                            "SELECT maLoaiNV FROM NhanVien WHERE maNV = ? AND isActive = 1"
                    )
                    .setParameter(1, maNV)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
            maLoaiNV = loaiNV != null ? loaiNV.toString() : null;
            return isManagerRole(maLoaiNV);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isManagerRole(String maLoaiNV) {
        return "LNV02".equals(maLoaiNV) || "LNV03".equals(maLoaiNV);
    }
}
