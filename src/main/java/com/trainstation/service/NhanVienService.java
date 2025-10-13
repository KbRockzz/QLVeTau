package com.trainstation.service;

import com.trainstation.dao.NhanVienDAO;
import com.trainstation.model.NhanVien;
import java.util.List;

/**
 * Service xử lý nghiệp vụ liên quan đến Nhân viên
 */
public class NhanVienService {
    private static NhanVienService instance;
    private final NhanVienDAO nhanVienDAO;

    private NhanVienService() {
        this.nhanVienDAO = NhanVienDAO.getInstance();
    }

    public static synchronized NhanVienService getInstance() {
        if (instance == null) {
            instance = new NhanVienService();
        }
        return instance;
    }

    /**
     * Lấy tất cả nhân viên
     */
    public List<NhanVien> layTatCaNhanVien() {
        return nhanVienDAO.getAll();
    }

    /**
     * Tìm nhân viên theo mã
     */
    public NhanVien timNhanVienTheoMa(String maNV) {
        return nhanVienDAO.findById(maNV);
    }

    /**
     * Thêm nhân viên mới
     */
    public boolean themNhanVien(NhanVien nv) {
        return nhanVienDAO.insert(nv);
    }

    /**
     * Cập nhật thông tin nhân viên
     */
    public boolean capNhatNhanVien(NhanVien nv) {
        return nhanVienDAO.update(nv);
    }

    /**
     * Xóa nhân viên
     */
    public boolean xoaNhanVien(String maNV) {
        return nhanVienDAO.delete(maNV);
    }
}
