package com.trainstation.dao;

import com.trainstation.model.LoaiVe;
import com.trainstation.MySQL.ConnectSql;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoaiVeDAO implements GenericDAO<LoaiVe> {
    private static LoaiVeDAO instance;

    private LoaiVeDAO() {
        // Không giữ Connection làm trường
    }

    public static synchronized LoaiVeDAO getInstance() {
        if (instance == null) {
            instance = new LoaiVeDAO();
        }
        return instance;
    }

    @Override
    public List<LoaiVe> getAll() {
        List<LoaiVe> list = new ArrayList<>();
        String sql = "SELECT maLoaiVe, tenLoai, heSoGia, moTa FROM LoaiVe";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                LoaiVe lv = new LoaiVe(
                        rs.getString("maLoaiVe"),
                        rs.getString("tenLoai"),
                        rs.getBigDecimal("heSoGia"),
                        rs.getString("moTa")
                );
                list.add(lv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public LoaiVe findById(String id) {
        String sql = "SELECT maLoaiVe, tenLoai, heSoGia, moTa FROM LoaiVe WHERE maLoaiVe = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new LoaiVe(
                            rs.getString("maLoaiVe"),
                            rs.getString("tenLoai"),
                            rs.getBigDecimal("heSoGia"),
                            rs.getString("moTa")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean insert(LoaiVe lv) {
        String sql = "INSERT INTO LoaiVe (maLoaiVe, tenLoai, heSoGia, moTa) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, lv.getMaLoaiVe());
            pst.setString(2, lv.getTenLoai());
            pst.setBigDecimal(3, lv.getHeSoGia());
            pst.setString(4, lv.getMoTa());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(LoaiVe lv) {
        String sql = "UPDATE LoaiVe SET tenLoai = ?, heSoGia = ?, moTa = ? WHERE maLoaiVe = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, lv.getTenLoai());
            pst.setBigDecimal(2, lv.getHeSoGia());
            pst.setString(3, lv.getMoTa());
            pst.setString(4, lv.getMaLoaiVe());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM LoaiVe WHERE maLoaiVe = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}