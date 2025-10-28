package com.trainstation.dao;

import com.trainstation.MySQL.ConnectSql;
import com.trainstation.model.Ghe;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GheDAO {
    private static GheDAO instance;

    private GheDAO() {
    }

    public static synchronized GheDAO getInstance() {
        if (instance == null) {
            instance = new GheDAO();
        }
        return instance;
    }

    public List<Ghe> getByToa(String maToa) {
        List<Ghe> list = new ArrayList<>();
        // Đã thêm cột loaiGhe vào SELECT (tên cột có thể là 'loaiGhe' hoặc 'maLoaiGhe' tuỳ schema)
        String sql = "SELECT maGhe, maToa, trangThai, loaiGhe FROM Ghe WHERE maToa = ? ORDER BY maGhe";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maToa);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Ghe g = new Ghe();
                    g.setMaGhe(rs.getString("maGhe"));
                    g.setMaToa(rs.getString("maToa"));
                    g.setTrangThai(rs.getString("trangThai"));
                    // set loại ghế nếu có trong DB
                    try {
                        g.setLoaiGhe(rs.getString("loaiGhe"));
                    } catch (Throwable ignored) {
                        // nếu model không có setter/field, bỏ qua (mà nên bổ sung model)
                    }
                    list.add(g);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Ghe findById(String maGhe) {
        String sql = "SELECT maGhe, maToa, trangThai, loaiGhe FROM Ghe WHERE maGhe = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maGhe);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Ghe g = new Ghe();
                    g.setMaGhe(rs.getString("maGhe"));
                    g.setMaToa(rs.getString("maToa"));
                    g.setTrangThai(rs.getString("trangThai"));
                    try {
                        g.setLoaiGhe(rs.getString("loaiGhe"));
                    } catch (Throwable ignored) {}
                    return g;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public boolean update(Ghe ghe) {
        String sql = "UPDATE Ghe SET trangThai = ? WHERE maGhe = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, ghe.getTrangThai());
            pst.setString(2, ghe.getMaGhe());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}