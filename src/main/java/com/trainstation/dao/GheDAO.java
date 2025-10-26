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
        String sql = "SELECT maGhe, maToa, trangThai FROM Ghe WHERE maToa = ? ORDER BY maGhe";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maToa);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Ghe g = new Ghe();
                    g.setMaGhe(rs.getString("maGhe"));
                    g.setMaToa(rs.getString("maToa"));
                    g.setTrangThai(rs.getString("trangThai"));
                    list.add(g);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Ghe findById(String maGhe) {
        String sql = "SELECT maGhe, maToa, trangThai FROM Ghe WHERE maGhe = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maGhe);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Ghe g = new Ghe();
                    g.setMaGhe(rs.getString("maGhe"));
                    g.setMaToa(rs.getString("maToa"));
                    g.setTrangThai(rs.getString("trangThai"));
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