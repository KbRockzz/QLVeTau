/*
 * @ (#) BangGiaService        1.0     12/15/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package com.trainstation.service;

import com.trainstation.dao.BangGiaDAO;
import com.trainstation.model.BangGia;
import com.trainstation.model.Ga;

import java.util.List;

/*
 * @description:
 * @author: Thuy, Ly Thi
 * @version: 1.0
 * @created: 12/15/2025  9:53 PM
 */
public class BangGiaService {
    private static BangGiaService instance;
    private BangGiaDAO bangGiaDao = BangGiaDAO.getInstance();

    private BangGiaService() {
    }
    public static synchronized BangGiaService getInstance() {
        if (instance == null) {
            instance = new BangGiaService();
        }
        return instance;
    }
    public List<BangGia> layTatCaBangGia(){
        return bangGiaDao.getAll();
    }
    
    public BangGia timBangGiaTheoMa(String maBG) {
        return bangGiaDao.findById(maBG);
    }

    public String taoMaBangGia() {
        List<BangGia> danhSach = bangGiaDao.getAll();
        int maxId = 0;
        for (BangGia bg : danhSach) {
            String maBG = bg.getMaBangGia();
            if (maBG != null && maBG.startsWith("BG")) {
                try {
                    int id = Integer.parseInt(maBG.substring(2));
                    if (id > maxId) {
                        maxId = id;
                    }
                } catch (NumberFormatException e) {
                }
            }
        }
        return String.format("BG%03d", maxId + 1);
    }
    
    public boolean themBangGia(BangGia bg) {
        return bangGiaDao.insert(bg);
    }
    public boolean capNhatBangGia(BangGia bg) {
        return bangGiaDao.update(bg);
    }
    public boolean xoaBangGia(String maBG) {
        return bangGiaDao.delete(maBG);
    }
    
}

