package com.trainstation.service;

import com.trainstation.dao.DauMayDAO;
import com.trainstation.model.DauMay;
import com.trainstation.network.AppClient;
import com.trainstation.network.AppRequest;
import com.trainstation.network.AppResponse;
import com.trainstation.network.NetworkConfig;
import com.trainstation.network.RequestType;

import java.util.List;

/**
 * Service xử lý nghiệp vụ liên quan đến Đầu máy (Locomotive)
 * Note: Maintains backwards compatibility with Tau for existing GUI code
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
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.GET_ALL_DAUMAY));
            if (resp.isSuccess() && resp.getData() != null) return (List<DauMay>) resp.getData();
            return java.util.Collections.emptyList();
        }
        return dauMayDAO.getAll();
    }

    public DauMay timDauMayTheoMa(String maDauMay) {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.FIND_DAUMAY_BY_ID, maDauMay));
            if (resp.isSuccess()) return (DauMay) resp.getData();
            return null;
        }
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
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.INSERT_DAUMAY, dauMay));
            if (resp.isSuccess() && resp.getData() != null) return (Boolean) resp.getData();
            return false;
        }
        return dauMayDAO.insert(dauMay);
    }

    public boolean capNhatDauMay(DauMay dauMay) {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.UPDATE_DAUMAY, dauMay));
            if (resp.isSuccess() && resp.getData() != null) return (Boolean) resp.getData();
            return false;
        }
        return dauMayDAO.update(dauMay);
    }

    public boolean xoaDauMay(String maDauMay) {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.DELETE_DAUMAY, maDauMay));
            if (resp.isSuccess() && resp.getData() != null) return (Boolean) resp.getData();
            return false;
        }
        return dauMayDAO.delete(maDauMay);
    }

    public boolean dungHoatDongDauMay(String maDauMay) {
        return dauMayDAO.dungHoatDongDauMay(maDauMay);
    }


    public List<DauMay> layDauMayDangHoatDong() {
        return dauMayDAO.layDauMayHoatDong();
    }
}
