package com.trainstation.service;

import com.trainstation.dao.GaDAO;
import com.trainstation.model.Ga;
import com.trainstation.network.AppClient;
import com.trainstation.network.AppRequest;
import com.trainstation.network.AppResponse;
import com.trainstation.network.NetworkConfig;
import com.trainstation.network.RequestType;
import java.util.List;

/**
 * Service xử lý nghiệp vụ liên quan đến Ga (Station)
 */
public class GaService {
    private static GaService instance;
    private final GaDAO gaDAO;

    private GaService() {
        this.gaDAO = GaDAO.getInstance();
    }

    public static synchronized GaService getInstance() {
        if (instance == null) {
            instance = new GaService();
        }
        return instance;
    }

    public List<Ga> layTatCaGa() {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.GET_ALL_GA));
            if (resp.isSuccess() && resp.getData() != null) return (List<Ga>) resp.getData();
            return java.util.Collections.emptyList();
        }
        return gaDAO.getAll();
    }

    public Ga timGaTheoMa(String maGa) {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.FIND_GA_BY_ID, maGa));
            if (resp.isSuccess()) return (Ga) resp.getData();
            return null;
        }
        return gaDAO.findById(maGa);
    }

    public String taoMaGa() {
        List<Ga> danhSach = gaDAO.getAll();
        int maxId = 0;
        for (Ga ga : danhSach) {
            String maGa = ga.getMaGa();
            if (maGa != null && maGa.startsWith("GA")) {
                try {
                    int id = Integer.parseInt(maGa.substring(2));
                    if (id > maxId) {
                        maxId = id;
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid IDs
                }
            }
        }
        return String.format("GA%03d", maxId + 1);
    }

    public boolean themGa(Ga ga) {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.INSERT_GA, ga));
            if (resp.isSuccess() && resp.getData() != null) return (Boolean) resp.getData();
            return false;
        }
        return gaDAO.insert(ga);
    }

    public boolean capNhatGa(Ga ga) {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.UPDATE_GA, ga));
            if (resp.isSuccess() && resp.getData() != null) return (Boolean) resp.getData();
            return false;
        }
        return gaDAO.update(ga);
    }

    public boolean xoaGa(String maGa) {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.DELETE_GA, maGa));
            if (resp.isSuccess() && resp.getData() != null) return (Boolean) resp.getData();
            return false;
        }
        return gaDAO.delete(maGa);
    }

    public List<Ga> layGaDaXoa() {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.GET_DELETED_GA));
            if (resp.isSuccess() && resp.getData() != null) return (List<Ga>) resp.getData();
            return java.util.Collections.emptyList();
        }
        return gaDAO.getDeletedStations();
    }

    public boolean khoiPhucGa(String maGa) {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.RESTORE_GA, maGa));
            if (resp.isSuccess() && resp.getData() != null) return (Boolean) resp.getData();
            return false;
        }
        return gaDAO.restoreStation(maGa);
    }
}
