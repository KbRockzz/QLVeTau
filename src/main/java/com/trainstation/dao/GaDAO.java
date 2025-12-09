/*
 * @ (#) GaDAO        1.0     11/15/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package com.trainstation.dao;

import com.trainstation.MySQL.ConnectSql;
import com.trainstation.model.ChuyenTau;
import com.trainstation.model.Ga;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*
 * @description:
 * @author: Thuy, Ly Thi
 * @version: 1.0
 * @created: 11/15/2025  7:17 PM
 */
public class GaDAO implements GenericDAO<Ga>{
    private static GaDAO instance;

    private GaDAO() {
        // Không giữ Connection làm trường
    }

    public static synchronized GaDAO getInstance() {
        if (instance == null) {
            instance = new GaDAO();
        }
        return instance;
    }

    @Override
    public List<Ga> getAll() {
        List<Ga> list = new ArrayList<>();
        String sql = "SELECT maGa, tenGa, moTa, trangThai, diaChi FROM Ga";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Ga ga = new Ga(
                        rs.getString("maGa"),
                        rs.getString("tenGa"),
                        rs.getString("moTa"),
                        rs.getBoolean("trangThai"),
                        rs.getString("diaChi")
                );
                list.add(ga);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Ga findById(String id) {
        List<Ga> list = new ArrayList<>();
        list = getAll();
        return list.stream().filter(ga -> ga.getMaGa().equals(id)).findFirst().orElse(null);
    }

    @Override
    public boolean insert(Ga entity) {
        String sql = "INSERT INTO ChuyenTau (maChuyen, maTau, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, entity.getMaGa());
            pst.setString(2, entity.getTenGa());
            pst.setString(3, entity.getDiaChi());
            pst.setBoolean(4, entity.isTinhTrang());
            pst.setString(5, entity.getMoTa());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Ga entity) {
        String sql = "UPDATE Ga SET tenGa = ?, diaChi = ?, trangThai = ?, moTa = ? WHERE maGa = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, entity.getTenGa());
            pst.setString(2, entity.getDiaChi());
            pst.setBoolean(3, entity.isTinhTrang());
            pst.setString(4, entity.getMoTa());
            pst.setString(5, entity.getMaGa());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        Ga ga = findById(id);
        ga.setTinhTrang(false);
        return false;
    }
}

