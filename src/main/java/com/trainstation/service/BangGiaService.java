/*
 * @ (#) BangGiaService        1.0     12/15/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package com.trainstation.service;

import com.trainstation.dao.BangGiaDAO;
import com.trainstation.model.BangGia;
import com.trainstation.network.AppClient;
import com.trainstation.network.AppRequest;
import com.trainstation.network.AppResponse;
import com.trainstation.network.NetworkConfig;
import com.trainstation.network.RequestType;
import com.trainstation.service.impl.BangGiaServiceImpl;

import java.util.List;

/*
 * @description:
 * @author: Thuy, Ly Thi
 * @version: 1.0
 * @created: 12/15/2025  9:53 PM
 */
public class BangGiaService {
    private static BangGiaService instance;
    private final BangGiaDAO bangGiaDao = BangGiaDAO.getInstance();
    private final BangGiaServiceImpl bangGiaServiceImpl = BangGiaServiceImpl.getInstance();

    private BangGiaService() {
    }
    public static synchronized BangGiaService getInstance() {
        if (instance == null) {
            instance = new BangGiaService();
        }
        return instance;
    }
    public List<BangGia> layTatCaBangGia(){
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.GET_ALL_BANGGIA));
            if (resp.isSuccess() && resp.getData() != null) return (List<BangGia>) resp.getData();
            return java.util.Collections.emptyList();
        }
        return bangGiaServiceImpl.layTatCaBangGia();
    }
    
    public BangGia timBangGiaTheoMa(String maBG) {
        if (!NetworkConfig.isClientMode()) {
            return bangGiaServiceImpl.timBangGiaTheoMa(maBG);
        }
        return layTatCaBangGia().stream()
                .filter(bg -> maBG != null && maBG.equals(bg.getMaBangGia()))
                .findFirst()
                .orElse(null);
    }

    public String taoMaBangGia() {
        if (!NetworkConfig.isClientMode()) {
            return bangGiaServiceImpl.taoMaBangGia();
        }
        List<BangGia> danhSach = layTatCaBangGia();
        int maxId = 0;
        for (BangGia bg : danhSach) {
            String currentMaBG = bg.getMaBangGia();
            if (currentMaBG != null && currentMaBG.startsWith("BG")) {
                try {
                    int id = Integer.parseInt(currentMaBG.substring(2));
                    if (id > maxId) {
                        maxId = id;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return String.format("BG%03d", maxId + 1);
    }
    
    public boolean themBangGia(BangGia bg) {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.INSERT_BANGGIA, bg));
            if (resp.isSuccess() && resp.getData() != null) return (Boolean) resp.getData();
            return false;
        }
        return bangGiaServiceImpl.themBangGia(bg);
    }
    public boolean capNhatBangGia(BangGia bg) {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.UPDATE_BANGGIA, bg));
            if (resp.isSuccess() && resp.getData() != null) return (Boolean) resp.getData();
            return false;
        }
        return bangGiaServiceImpl.capNhatBangGia(bg);
    }
    public boolean xoaBangGia(String maBG) {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.DELETE_BANGGIA, maBG));
            if (resp.isSuccess() && resp.getData() != null) return (Boolean) resp.getData();
            return false;
        }
        return bangGiaServiceImpl.xoaBangGia(maBG);
    }
    
}
