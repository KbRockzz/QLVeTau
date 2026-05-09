package com.trainstation.service;

import com.trainstation.dao.ChiTietHoaDonDAO;
import com.trainstation.model.ChiTietHoaDon;
import com.trainstation.service.impl.ChiTietHoaDonServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý nghiệp vụ liên quan đến Chi tiết hóa đơn
 */
public class ChiTietHoaDonService {
    private static ChiTietHoaDonService instance;
    private final ChiTietHoaDonDAO chiTietHoaDonDAO;
    private final ChiTietHoaDonServiceImpl chiTietHoaDonServiceImpl;

    private ChiTietHoaDonService() {
        this.chiTietHoaDonDAO = ChiTietHoaDonDAO.getInstance();
        this.chiTietHoaDonServiceImpl = ChiTietHoaDonServiceImpl.getInstance();
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
        ChiTietHoaDon created = chiTietHoaDonServiceImpl.themChiTiet(chiTiet);
        if (created != null) return created;
        throw new RuntimeException("Không thể thêm chi tiết hóa đơn");
    }

    /**
     * Cập nhật chi tiết hóa đơn
     */
    public boolean capNhatChiTiet(ChiTietHoaDon chiTiet) {
        return chiTietHoaDonServiceImpl.capNhatChiTiet(chiTiet);
    }

    /**
     * Lấy danh sách chi tiết theo mã hóa đơn
     */
    public List<ChiTietHoaDon> getByHoaDon(String maHoaDon) {
        return chiTietHoaDonServiceImpl.getByHoaDon(maHoaDon);
    }

    /**
     * Lấy tất cả chi tiết hóa đơn
     */
    public List<ChiTietHoaDon> layTatCa() {
        return chiTietHoaDonServiceImpl.layTatCa();
    }

    /**
     * Xóa chi tiết hóa đơn
     */
    public boolean xoaChiTiet(String maHoaDon, String maVe) {
        return chiTietHoaDonServiceImpl.xoaChiTiet(maHoaDon, maVe);
    }
}
