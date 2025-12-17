package com.trainstation.dao;

import com.trainstation.model.LoaiNV;
import com.trainstation.MySQL.ConnectSql;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoaiNVDAO implements GenericDAO<LoaiNV> {
    private static LoaiNVDAO instance;

    private LoaiNVDAO() {
        // Không giữ Connection làm trường
    }

    public static synchronized LoaiNVDAO getInstance() {
        if (instance == null) {
            instance = new LoaiNVDAO();
        }
        return instance;
    }

    @Override
    public List<LoaiNV> getAll() {
        List<LoaiNV> list = new ArrayList<>();
        String sql = "SELECT maLoai, tenLoai, moTa, isActive FROM LoaiNV WHERE isActive = 1";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                LoaiNV lnv = new LoaiNV(
                        rs.getString("maLoai"),
                        rs.getString("tenLoai"),
                        rs.getString("moTa"),
                        rs.getBoolean("isActive")
                );
                list.add(lnv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public LoaiNV findById(String id) {
        String sql = "SELECT maLoai, tenLoai, moTa, isActive FROM LoaiNV WHERE maLoai = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new LoaiNV(
                            rs.getString("maLoai"),
                            rs.getString("tenLoai"),
                            rs.getString("moTa"),
                            rs.getBoolean("isActive")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean insert(LoaiNV lnv) {
        String sql = "INSERT INTO LoaiNV (maLoai, tenLoai, moTa) VALUES (?, ?, ?)";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, lnv.getMaLoai());
            pst.setString(2, lnv.getTenLoai());
            pst.setString(3, lnv.getMoTa());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(LoaiNV lnv) {
        String sql = "UPDATE LoaiNV SET tenLoai = ?, moTa = ? WHERE maLoai = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, lnv.getTenLoai());
            pst.setString(2, lnv.getMoTa());
            pst.setString(3, lnv.getMaLoai());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM LoaiNV WHERE maLoai = ?";
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