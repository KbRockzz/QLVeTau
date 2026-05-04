package com.trainstation.model;

import lombok.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TaiKhoan implements Serializable {
    private String maTK;
    private String maNV;
    private String tenTaiKhoan;
    private String matKhau;
    private String trangThai;

    /**
     * Kiểm tra xem tài khoản có phải quản lý không
     * Nhân viên loại LNV02 (Quản lý) và LNV03 (Admin) có quyền quản lý
     */
    public boolean isManager() {
        if (maNV == null) {
            return false;
        }
        try {
            com.trainstation.dao.NhanVienDAO nhanVienDAO = com.trainstation.dao.NhanVienDAO.getInstance();
            String loaiNV = nhanVienDAO.getLoaiNV(maNV);
            return "LNV02".equals(loaiNV) || "LNV03".equals(loaiNV);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
