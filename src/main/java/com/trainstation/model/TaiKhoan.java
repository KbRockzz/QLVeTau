package com.trainstation.model;

import com.trainstation.persistence.JpaEntityManagerProvider;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
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

    /**
     * Kiểm tra xem tài khoản có phải quản lý không
     * Nhân viên loại LNV02 (Quản lý) và LNV03 (Admin) có quyền quản lý
     */
    public boolean isManager() {
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
            return "LNV02".equals(loaiNV) || "LNV03".equals(loaiNV);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
