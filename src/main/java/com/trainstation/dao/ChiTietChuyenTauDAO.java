/*
 * @ (#) ChiTietChuyenTauDAO        1.0     12/11/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package com.trainstation.dao;

import com.trainstation.MySQL.ConnectSql;
import com.trainstation.model.ChiTietChuyenTau;
import com.trainstation.model.ChiTietHoaDon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/*
 * @description:
 * @author: Thuy, Ly Thi
 * @version: 1.0
 * @created: 12/11/2025  11:05 PM
 */
public class ChiTietChuyenTauDAO implements GenericDAO<ChiTietChuyenTau> {
    private static ChiTietChuyenTauDAO instance;

    private ChiTietChuyenTauDAO() {
    }

    public static synchronized ChiTietChuyenTauDAO getInstance() {
        if (instance == null) {
            instance = new ChiTietChuyenTauDAO();
        }
        return instance;
    }

    @Override
    public List<ChiTietChuyenTau> getAll() {
        List<ChiTietChuyenTau> list = new ArrayList<>();
        String sql = "SELECT maChuyenTau, maToaTau, soThuTuToa, sucChua FROM ChiTietChuyenTau";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                ChiTietChuyenTau ct = new ChiTietChuyenTau();
                ct.setMaChuyenTau(rs.getString("maChuyenTau"));
                ct.setMaToaTau(rs.getString("maToaTau"));
                ct.setSoThuTuToa(rs.getInt("soThuTuToa"));
                ct.setSucChua(rs.getInt("sucChua"));
                list.add(ct);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean delete(String id) {
        return false;
    }

    @Override
    public boolean insert(ChiTietChuyenTau entity) {
        return false;
    }

    @Override
    public boolean update(ChiTietChuyenTau entity) {
        return false;
    }

    @Override
    public ChiTietChuyenTau findById(String id) {
        return null;
    }

}

