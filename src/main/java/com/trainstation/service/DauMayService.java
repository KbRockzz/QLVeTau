package com.trainstation.service;

import com.trainstation.dao.DauMayDAO;
import com.trainstation.model.DauMay;

import java.util.List;

/**
 * Service xử lý nghiệp vụ liên quan đến Tàu
 */
public class DauMayService {
    private static DauMayService instance;
    private final DauMayDAO dauMayDAO;

    private DauMayService() {
        this.dauMayDAO = DauMayDAO.getInstance();
    }

    public static synchronized DauMayService getInstance() {
        if (instance == null) {
            instance = new DauMayService();
        }
        return instance;
    }

    public List<DauMay> layTatCaDauMay() {
        return dauMayDAO.getAll();
    }

    public DauMay timTauTheoMa(String maTau) {
        return dauMayDAO.findById(maTau);
    }


    /**
     * Tạo mã nhân viên tự động
     */
    public String taoMaDauMay() {
        List<DauMay> danhSach = dauMayDAO.getAll();
        int maxId = 0;
        for (DauMay dauMay : danhSach) {
            String maDauMay = dauMay.getMaDauMay();
            if (maDauMay != null && maDauMay.startsWith("T")) {
                try {
                    int id = Integer.parseInt(maDauMay.substring(1));
                    if (id > maxId) {
                        maxId = id;
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid IDs
                }
            }
        }
        return String.format("T%03d", maxId + 1);
    }

    public boolean themDauMay(DauMay dauMay) {
        return dauMayDAO.insert(dauMay);
    }

    public boolean capNhatDauMay(DauMay dauMay) {
        return dauMayDAO.update(dauMay);
    }

    public boolean xoaDauMay(String maDauMay) {
        return dauMayDAO.delete(maDauMay);
    }

    public boolean dungHoatDongDauMay(String maTau) {
        return dauMayDAO.dungHoatDongDauMay(maTau);
    }

    public List<DauMay> layDauMayHoatDong() {
        return dauMayDAO.layDauMayHoatDong();
    }
}
