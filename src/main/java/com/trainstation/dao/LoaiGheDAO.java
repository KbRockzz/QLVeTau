package com.trainstation.dao;

import com.trainstation.model.LoaiGhe;
import com.trainstation.MySQL.ConnectSql;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoaiGheDAO implements GenericDAO<LoaiGhe> {
    private static LoaiGheDAO instance;

    private LoaiGheDAO() {
        // Không giữ Connection làm trường
    }

    public static synchronized LoaiGheDAO getInstance() {
        if (instance == null) {
            instance = new LoaiGheDAO();
        }
        return instance;
    }

    @Override
    public List<LoaiGhe> getAll() {
        List<LoaiGhe> list = new ArrayList<>();
        String sql = "SELECT maLoai, tenLoai, moTa FROM LoaiGhe";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                LoaiGhe lg = new LoaiGhe(
                        rs.getString("maLoai"),
                        rs.getString("tenLoai"),
                        rs.getString("moTa")
                );
                list.add(lg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public LoaiGhe findById(String id) {
        String sql = "SELECT maLoai, tenLoai, moTa FROM LoaiGhe WHERE maLoai = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new LoaiGhe(
                            rs.getString("maLoai"),
                            rs.getString("tenLoai"),
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
    public boolean insert(LoaiGhe lg) {
        String sql = "INSERT INTO LoaiGhe (maLoai, tenLoai, moTa) VALUES (?, ?, ?)";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, lg.getMaLoai());
            pst.setString(2, lg.getTenLoai());
            pst.setString(3, lg.getMoTa());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(LoaiGhe lg) {
        String sql = "UPDATE LoaiGhe SET tenLoai = ?, moTa = ? WHERE maLoai = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, lg.getTenLoai());
            pst.setString(2, lg.getMoTa());
            pst.setString(3, lg.getMaLoai());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM LoaiGhe WHERE maLoai = ?";
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