package com.trainstation.service;

import com.trainstation.dao.ChiTietHoaDonDAO;
import com.trainstation.model.ChiTietHoaDon;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý nghiệp vụ liên quan đến Chi tiết hóa đơn
 */
public class ChiTietHoaDonService {
    private static ChiTietHoaDonService instance;
    private final ChiTietHoaDonDAO chiTietHoaDonDAO;

    private ChiTietHoaDonService() {
        this.chiTietHoaDonDAO = ChiTietHoaDonDAO.getInstance();
    }

    public static synchronized ChiTietHoaDonService getInstance() {
        if (instance == null) {
            instance = new ChiTietHoaDonService();
        }
        return instance;
    }

    /**
     * Thêm chi tiết hóa đơn mới
     */
    public ChiTietHoaDon themChiTiet(ChiTietHoaDon chiTiet) {
        if (chiTietHoaDonDAO.insert(chiTiet)) {
            return chiTiet;
        }
        throw new RuntimeException("Không thể thêm chi tiết hóa đơn");
    }

    /**
     * Cập nhật chi tiết hóa đơn
     */
    public boolean capNhatChiTiet(ChiTietHoaDon chiTiet) {
        return chiTietHoaDonDAO.update(chiTiet);
    }

    /**
     * Lấy danh sách chi tiết theo mã hóa đơn
     */
    public List<ChiTietHoaDon> getByHoaDon(String maHoaDon) {
        return chiTietHoaDonDAO.getAll().stream()
                .filter(ct -> ct.getMaHoaDon().equals(maHoaDon))
                .collect(Collectors.toList());
    }

    /**
     * Lấy tất cả chi tiết hóa đơn
     */
    public List<ChiTietHoaDon> layTatCa() {
        return chiTietHoaDonDAO.getAll();
    }

    /**
     * Xóa chi tiết hóa đơn
     */
    public boolean xoaChiTiet(String maHoaDon, String maVe) {
        return chiTietHoaDonDAO.delete(maHoaDon + "," + maVe);
    }
}
