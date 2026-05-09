package com.trainstation.service;

import com.trainstation.dao.DauMayDAO;
import com.trainstation.model.DauMay;
import com.trainstation.network.AppClient;
import com.trainstation.network.AppRequest;
import com.trainstation.network.AppResponse;
import com.trainstation.network.NetworkConfig;
import com.trainstation.network.RequestType;
import com.trainstation.service.impl.DauMayServiceImpl;

import java.util.List;

/**
 * Service xử lý nghiệp vụ liên quan đến Đầu máy (Locomotive)
 * Note: Maintains backwards compatibility with Tau for existing GUI code
 */
public class DauMayService {
    private static DauMayService instance;
    private final DauMayDAO dauMayDAO;
    private final DauMayServiceImpl dauMayServiceImpl;

    private DauMayService() {
        this.dauMayDAO = DauMayDAO.getInstance();
        this.dauMayServiceImpl = DauMayServiceImpl.getInstance();
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
        return dauMayServiceImpl.layTatCaDauMay();
    }

    public DauMay timDauMayTheoMa(String maDauMay) {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.FIND_DAUMAY_BY_ID, maDauMay));
            if (resp.isSuccess()) return (DauMay) resp.getData();
            return null;
        }
        return dauMayServiceImpl.timDauMayTheoMa(maDauMay);
    }

    public String taoMaDauMay() {
        return dauMayServiceImpl.taoMaDauMay();
    }

    public boolean themDauMay(DauMay dauMay) {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.INSERT_DAUMAY, dauMay));
            if (resp.isSuccess() && resp.getData() != null) return (Boolean) resp.getData();
            return false;
        }
        return dauMayServiceImpl.themDauMay(dauMay);
    }

    public boolean capNhatDauMay(DauMay dauMay) {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.UPDATE_DAUMAY, dauMay));
            if (resp.isSuccess() && resp.getData() != null) return (Boolean) resp.getData();
            return false;
        }
        return dauMayServiceImpl.capNhatDauMay(dauMay);
    }

    public boolean xoaDauMay(String maDauMay) {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.DELETE_DAUMAY, maDauMay));
            if (resp.isSuccess() && resp.getData() != null) return (Boolean) resp.getData();
            return false;
        }
        return dauMayServiceImpl.xoaDauMay(maDauMay);
    }

    public boolean dungHoatDongDauMay(String maDauMay) {
        return dauMayServiceImpl.dungHoatDongDauMay(maDauMay);
    }


    public List<DauMay> layDauMayDangHoatDong() {
        return dauMayServiceImpl.layDauMayDangHoatDong();
    }
}
