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

    /**
     * Tạo mã nhân viên tự động
     */
    public String taoMaNhanVien() {
        List<NhanVien> danhSach = nhanVienDAO.getAll();
        int maxId = 0;
        for (NhanVien nv : danhSach) {
            String maNV = nv.getMaNV();
            if (maNV != null && maNV.startsWith("NV")) {
                try {
                    int id = Integer.parseInt(maNV.substring(2));
                    if (id > maxId) {
                        maxId = id;
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid IDs
                }
            }
        }
        return String.format("NV%02d", maxId + 1);
    }
}
