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

    public List<Tau> layTatCaTau() {
        return tauDAO.getAll();
    }

    public Tau timTauTheoMa(String maTau) {
        return tauDAO.findById(maTau);
    }

    public List<ToaTau> layDanhSachToaTau(String maTau) {
        return toaTauDAO.getAll().stream()
                .filter(toa -> toa.getMaTau() != null && toa.getMaTau().equals(maTau))
                .collect(Collectors.toList());
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

    public boolean themTau(Tau tau) {
        return tauDAO.insert(tau);
    }

    public boolean capNhatTau(Tau tau) {
        return tauDAO.update(tau);
    }

    public boolean xoaTau(String maTau) {
        return tauDAO.delete(maTau);
    }

    public boolean dungHoatDongTau(String maTau) {
        return tauDAO.dungHoatDongTau(maTau);
    }

    public List<Tau> layTauHoatDong() {
        return tauDAO.layTauHoatDong();
    }
}
