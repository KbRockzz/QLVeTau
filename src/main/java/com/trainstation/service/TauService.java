package com.trainstation.service;

import com.trainstation.dao.TauDAO;
import com.trainstation.dao.ToaTauDAO;
import com.trainstation.model.NhanVien;
import com.trainstation.model.Tau;
import com.trainstation.model.ToaTau;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý nghiệp vụ liên quan đến Tàu
 */
public class TauService {
    private static TauService instance;
    private final TauDAO tauDAO;
    private final ToaTauDAO toaTauDAO;

    private TauService() {
        this.tauDAO = TauDAO.getInstance();
        this.toaTauDAO = ToaTauDAO.getInstance();
    }

    public static synchronized TauService getInstance() {
        if (instance == null) {
            instance = new TauService();
        }
        return instance;
    }

    /**
     * Tạo mã nhân viên tự động
     */
    public String taoMaTau() {
        List<Tau> danhSach = tauDAO.getAll();
        int maxId = 0;
        for (Tau tau : danhSach) {
            String maTau = tau.getMaTau();
            if (maTau != null && maTau.startsWith("T")) {
                try {
                    int id = Integer.parseInt(maTau.substring(1));
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

    /**
     * Lấy tất cả tàu
     */
    public List<Tau> layTatCaTau() {
        return tauDAO.getAll();
    }

    /**
     * Tìm tàu theo mã
     */
    public Tau timTauTheoMa(String maTau) {
        return tauDAO.findById(maTau);
    }

    /**
     * Lấy danh sách toa tàu theo mã tàu
     */
    public List<ToaTau> layDanhSachToaTau(String maTau) {
        return toaTauDAO.getAll().stream()
                .filter(toa -> toa.getMaTau() != null && toa.getMaTau().equals(maTau))
                .collect(Collectors.toList());
    }

    /**
     * Thêm tàu mới
     */
    public boolean themTau(Tau tau) {
        return tauDAO.insert(tau);
    }

    /**
     * Cập nhật thông tin tàu
     */
    public boolean capNhatTau(Tau tau) {
        return tauDAO.update(tau);
    }

    /**
     * Xóa tàu
     */
    public boolean xoaTau(String maTau) {
        return tauDAO.delete(maTau);
    }

    /**
     * Dừng hoạt động tàu (soft delete)
     */
    public boolean dungHoatDongTau(String maTau) {
        return tauDAO.dungHoatDongTau(maTau);
    }

    /**
     * Lấy danh sách tàu đang hoạt động
     */
    public List<Tau> layTauHoatDong() {
        return tauDAO.layTauHoatDong();
    }
}
