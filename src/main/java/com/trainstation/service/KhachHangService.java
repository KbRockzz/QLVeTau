package com.trainstation.service;

import com.trainstation.dao.KhachHangDAO;
import com.trainstation.model.KhachHang;
import com.trainstation.model.NhanVien;

import java.util.List;

/**
 * Service xử lý nghiệp vụ liên quan đến Khách hàng
 */
public class KhachHangService {
    private static KhachHangService instance;
    private final KhachHangDAO khachHangDAO;

    private KhachHangService() {
        this.khachHangDAO = KhachHangDAO.getInstance();
    }

    public static synchronized KhachHangService getInstance() {
        if (instance == null) {
            instance = new KhachHangService();
        }
        return instance;
    }

    /**
     * Lấy tất cả khách hàng
     */
    public List<KhachHang> layTatCaKhachHang() {
        return khachHangDAO.getAll();
    }

    /**
     * Tìm khách hàng theo mã
     */
    public KhachHang timKhachHangTheoMa(String maKH) {
        return khachHangDAO.findById(maKH);
    }

    /**
     * Thêm khách hàng mới
     */
    public boolean themKhachHang(KhachHang kh) {
        return khachHangDAO.insert(kh);
    }

    /**
     * Cập nhật thông tin khách hàng
     */
    public boolean capNhatKhachHang(KhachHang kh) {
        return khachHangDAO.update(kh);
    }

    /**
     * Xóa khách hàng
     */
    public boolean xoaKhachHang(String maKH) {
        return khachHangDAO.delete(maKH);
    }

    /**
     * Tìm khách hàng theo số điện thoại
     */
    public KhachHang timKhachHangTheoSoDienThoai(String soDienThoai) {
        return khachHangDAO.timTheoSoDienThoai(soDienThoai);
    }

    /**
     * Tạo mã khách hàng tự động
     */
    public String taoMaKhachHang() {
        List<KhachHang> danhSach = khachHangDAO.getAll();
        int maxId = 0;
        for (KhachHang kh : danhSach) {
            String maKH = kh.getMaKhachHang();
            if (maKH != null && maKH.startsWith("KH")) {
                try {
                    int id = Integer.parseInt(maKH.substring(2));
                    if (id > maxId) {
                        maxId = id;
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid IDs
                }
            }
        }
        return String.format("KH%02d", maxId + 1);
    }
}
