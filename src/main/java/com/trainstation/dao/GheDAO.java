package com.trainstation.dao;

import com.trainstation.model.Ghe;
import com.trainstation.MySQL.ConnectSql;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GheDAO implements GenericDAO<Ghe> {
    private static GheDAO instance;
    private Connection connection;

    private GheDAO() {
        connection = ConnectSql.getInstance().getConnection();
    }

    public static synchronized GheDAO getInstance() {
        if (instance == null) {
            instance = new GheDAO();
        }
        return instance;
    }

    @Override
    public List<Ghe> getAll() {
        List<Ghe> list = new ArrayList<>();
        String sql = "SELECT maGhe, maToa, loaiGhe, trangThai FROM Ghe";
        try (PreparedStatement pst = connection.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                Ghe g = new Ghe(
                    rs.getString("maGhe"),
                    rs.getString("maToa"),
                    rs.getString("loaiGhe"),
                    rs.getString("trangThai")
                );
                list.add(g);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Ghe findById(String id) {
        String sql = "SELECT maGhe, maToa, loaiGhe, trangThai FROM Ghe WHERE maGhe = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new Ghe(
                        rs.getString("maGhe"),
                        rs.getString("maToa"),
                        rs.getString("loaiGhe"),
                        rs.getString("trangThai")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean insert(Ghe g) {
        String sql = "INSERT INTO Ghe (maGhe, maToa, loaiGhe, trangThai) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, g.getMaGhe());
            pst.setString(2, g.getMaToa());
            pst.setString(3, g.getLoaiGhe());
            pst.setString(4, g.getTrangThai());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Ghe g) {
        String sql = "UPDATE Ghe SET maToa = ?, loaiGhe = ?, trangThai = ? WHERE maGhe = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, g.getMaToa());
            pst.setString(2, g.getLoaiGhe());
            pst.setString(3, g.getTrangThai());
            pst.setString(4, g.getMaGhe());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM Ghe WHERE maGhe = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy danh sách ghế theo mã toa
     */
    public List<Ghe> getByToa(String maToa) {
        List<Ghe> list = new ArrayList<>();
        String sql = "SELECT maGhe, maToa, loaiGhe, trangThai FROM Ghe WHERE maToa = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, maToa);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Ghe g = new Ghe(
                        rs.getString("maGhe"),
                        rs.getString("maToa"),
                        rs.getString("loaiGhe"),
                        rs.getString("trangThai")
                    );
                    list.add(g);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
