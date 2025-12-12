package com.trainstation.model;

import java.io.Serializable;

public class TaiKhoan implements Serializable {
    private String maTK;
    private String maNV;
    private String tenTaiKhoan;
    private String matKhau;
    private String trangThai;
    private boolean isActive;

    public TaiKhoan() {
        this.isActive = true;
    }

    public TaiKhoan(String maTK, String maNV, String tenTaiKhoan, String matKhau, String trangThai, boolean isActive) {
        this.maTK = maTK;
        this.maNV = maNV;
        this.tenTaiKhoan = tenTaiKhoan;
        this.matKhau = matKhau;
        this.trangThai = trangThai;
        this.isActive = isActive;
    }

    public String getMaTK() {
        return maTK;
    }

    public void setMaTK(String maTK) {
        this.maTK = maTK;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getTenTaiKhoan() {
        return tenTaiKhoan;
    }

    public void setTenTaiKhoan(String tenTaiKhoan) {
        this.tenTaiKhoan = tenTaiKhoan;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

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

    @Override
    public String toString() {
        return "TaiKhoan{" +
                "maTK='" + maTK + '\'' +
                ", maNV='" + maNV + '\'' +
                ", tenTaiKhoan='" + tenTaiKhoan + '\'' +
                ", matKhau='" + matKhau + '\'' +
                ", trangThai='" + trangThai + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
