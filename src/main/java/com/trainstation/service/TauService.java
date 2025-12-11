package com.trainstation.service;

import com.trainstation.dao.DauMayDAO;
import com.trainstation.dao.ToaTauDAO;
import com.trainstation.model.DauMay;
import com.trainstation.model.Tau;
import com.trainstation.model.ToaTau;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý nghiệp vụ liên quan đến Đầu máy (Locomotive)
 * Note: Maintains backwards compatibility with Tau for existing GUI code
 */
public class TauService {
    private static TauService instance;
    private final DauMayDAO dauMayDAO;
    private final ToaTauDAO toaTauDAO;

    private TauService() {
        this.dauMayDAO = DauMayDAO.getInstance();
        this.toaTauDAO = ToaTauDAO.getInstance();
    }

    public static synchronized TauService getInstance() {
        if (instance == null) {
            instance = new TauService();
        }
        return instance;
    }

    // New methods using DauMay directly
    public List<DauMay> layTatCaDauMay() {
        return dauMayDAO.getAll();
    }

    public DauMay timDauMayTheoMa(String maDauMay) {
        return dauMayDAO.findById(maDauMay);
    }

    public String taoMaDauMay() {
        List<DauMay> danhSach = dauMayDAO.getAll();
        int maxId = 0;
        for (DauMay dauMay : danhSach) {
            String maDauMay = dauMay.getMaDauMay();
            if (maDauMay != null && maDauMay.startsWith("DM")) {
                try {
                    int id = Integer.parseInt(maDauMay.substring(2));
                    if (id > maxId) {
                        maxId = id;
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid IDs
                }
            }
        }
        return String.format("DM%03d", maxId + 1);
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

    public boolean dungHoatDongDauMay(String maDauMay) {
        return dauMayDAO.dungHoatDongDauMay(maDauMay);
    }

    public List<DauMay> layDauMayHoatDong() {
        return dauMayDAO.layDauMayHoatDong();
    }

    // Backwards compatibility methods using Tau (adapter pattern)
    public List<Tau> layTatCaTau() {
        return dauMayDAO.getAll().stream()
            .map(this::convertDauMayToTau)
            .collect(Collectors.toList());
    }

    public Tau timTauTheoMa(String maTau) {
        DauMay dauMay = dauMayDAO.findById(maTau);
        return dauMay != null ? convertDauMayToTau(dauMay) : null;
    }

    public String taoMaTau() {
        // Delegate to new method
        return taoMaDauMay();
    }

    public boolean themTau(Tau tau) {
        DauMay dauMay = convertTauToDauMay(tau);
        return dauMayDAO.insert(dauMay);
    }

    public boolean capNhatTau(Tau tau) {
        DauMay dauMay = convertTauToDauMay(tau);
        return dauMayDAO.update(dauMay);
    }

    public boolean xoaTau(String maTau) {
        return dauMayDAO.delete(maTau);
    }

    public boolean dungHoatDongTau(String maTau) {
        return dauMayDAO.dungHoatDongDauMay(maTau);
    }

    public List<Tau> layTauHoatDong() {
        return dauMayDAO.layDauMayHoatDong().stream()
            .map(this::convertDauMayToTau)
            .collect(Collectors.toList());
    }

    public List<ToaTau> layDanhSachToaTau(String maDauMay) {
        // ToaTau no longer has maTau FK in new schema
        // Return all active ToaTau
        return toaTauDAO.getAll();
    }

    // Conversion helper methods
    private Tau convertDauMayToTau(DauMay dauMay) {
        return new Tau(
            dauMay.getMaDauMay(),
            0, // soToa not available in DauMay
            dauMay.getTenDauMay(),
            dauMay.getTrangThai()
        );
    }

    private DauMay convertTauToDauMay(Tau tau) {
        DauMay dauMay = new DauMay();
        dauMay.setMaDauMay(tau.getMaTau());
        dauMay.setTenDauMay(tau.getTenTau());
        dauMay.setTrangThai(tau.getTrangThai());
        dauMay.setActive(true);
        // loaiDauMay, namSX, lanBaoTriGanNhat are not set (will be null/default)
        return dauMay;
    }
}
