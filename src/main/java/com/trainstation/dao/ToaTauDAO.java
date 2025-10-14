package com.trainstation.dao;

import com.trainstation.model.ToaTau;
import com.trainstation.MySQL.ConnectSql;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ToaTauDAO implements GenericDAO<ToaTau> {
    private static ToaTauDAO instance;
    private Connection connection;

    private ToaTauDAO() {
        connection = ConnectSql.getInstance().getConnection();
    }

    public static synchronized ToaTauDAO getInstance() {
        if (instance == null) {
            instance = new ToaTauDAO();
        }
        return instance;
    }

    @Override
    public List<ToaTau> getAll() {
        List<ToaTau> list = new ArrayList<>();
        String sql = "SELECT maToa, tenToa, loaiToa, maTau, sucChua FROM ToaTau";
        try (PreparedStatement pst = connection.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                ToaTau t = new ToaTau(
                    rs.getString("maToa"),
                    rs.getString("tenToa"),
                    rs.getString("loaiToa"),
                    rs.getString("maTau"),
                    rs.getInt("sucChua")
                );
                list.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public ToaTau findById(String id) {
        String sql = "SELECT maToa, tenToa, loaiToa, maTau, sucChua FROM ToaTau WHERE maToa = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new ToaTau(
                        rs.getString("maToa"),
                        rs.getString("tenToa"),
                        rs.getString("loaiToa"),
                        rs.getString("maTau"),
                        rs.getInt("sucChua")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean insert(ToaTau t) {
        String sql = "INSERT INTO ToaTau (maToa, tenToa, loaiToa, maTau, sucChua) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, t.getMaToa());
            pst.setString(2, t.getTenToa());
            pst.setString(3, t.getLoaiToa());
            pst.setString(4, t.getMaTau());
            pst.setInt(5, t.getSucChua());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(ToaTau t) {
        String sql = "UPDATE ToaTau SET tenToa = ?, loaiToa = ?, maTau = ?, sucChua = ? WHERE maToa = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, t.getTenToa());
            pst.setString(2, t.getLoaiToa());
            pst.setString(3, t.getMaTau());
            pst.setInt(4, t.getSucChua());
            pst.setString(5, t.getMaToa());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM ToaTau WHERE maToa = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy danh sách toa tàu theo mã tàu
     */
    public List<ToaTau> getByTau(String maTau) {
        List<ToaTau> list = new ArrayList<>();
        String sql = "SELECT maToa, tenToa, loaiToa, maTau, sucChua FROM ToaTau WHERE maTau = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, maTau);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    ToaTau t = new ToaTau(
                        rs.getString("maToa"),
                        rs.getString("tenToa"),
                        rs.getString("loaiToa"),
                        rs.getString("maTau"),
                        rs.getInt("sucChua")
                    );
                    list.add(t);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
