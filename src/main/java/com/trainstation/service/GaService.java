package com.trainstation.service;

import com.trainstation.dao.GaDAO;
import com.trainstation.model.Ga;
import com.trainstation.network.AppClient;
import com.trainstation.network.AppRequest;
import com.trainstation.network.AppResponse;
import com.trainstation.network.NetworkConfig;
import com.trainstation.network.RequestType;
import com.trainstation.service.impl.GaServiceImpl;
import java.util.List;

/**
 * Service xử lý nghiệp vụ liên quan đến Ga (Station)
 */
public class GaService {
    private static GaService instance;
    private final GaDAO gaDAO;
    private final GaServiceImpl gaServiceImpl;

    private GaService() {
        this.gaDAO = GaDAO.getInstance();
        this.gaServiceImpl = GaServiceImpl.getInstance();
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
        return gaServiceImpl.layTatCaGa();
    }

    public Ga timGaTheoMa(String maGa) {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.FIND_GA_BY_ID, maGa));
            if (resp.isSuccess()) return (Ga) resp.getData();
            return null;
        }
        return gaServiceImpl.timGaTheoMa(maGa);
    }

    public String taoMaGa() {
        return gaServiceImpl.taoMaGa();
    }

    public boolean themGa(Ga ga) {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.INSERT_GA, ga));
            if (resp.isSuccess() && resp.getData() != null) return (Boolean) resp.getData();
            return false;
        }
        return gaServiceImpl.themGa(ga);
    }

    public boolean capNhatGa(Ga ga) {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.UPDATE_GA, ga));
            if (resp.isSuccess() && resp.getData() != null) return (Boolean) resp.getData();
            return false;
        }
        return gaServiceImpl.capNhatGa(ga);
    }

    public boolean xoaGa(String maGa) {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.DELETE_GA, maGa));
            if (resp.isSuccess() && resp.getData() != null) return (Boolean) resp.getData();
            return false;
        }
        return gaServiceImpl.xoaGa(maGa);
    }

    public List<Ga> layGaDaXoa() {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.GET_DELETED_GA));
            if (resp.isSuccess() && resp.getData() != null) return (List<Ga>) resp.getData();
            return java.util.Collections.emptyList();
        }
        return gaServiceImpl.layGaDaXoa();
    }

    public boolean khoiPhucGa(String maGa) {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.RESTORE_GA, maGa));
            if (resp.isSuccess() && resp.getData() != null) return (Boolean) resp.getData();
            return false;
        }
        return gaServiceImpl.khoiPhucGa(maGa);
    }
}
