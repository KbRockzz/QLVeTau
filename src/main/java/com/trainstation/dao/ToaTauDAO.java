package com.trainstation.dao;

import com.trainstation.model.ToaTau;
import com.trainstation.MySQL.ConnectSql;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ToaTauDAO implements GenericDAO<ToaTau> {
    private static ToaTauDAO instance;

    private ToaTauDAO() {
        // Không giữ Connection làm trường
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
        String sql = "SELECT maToa, loaiToa, samSX, trangThai, sucChua, isActive FROM ToaTau WHERE isActive = 1";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                ToaTau t = new ToaTau(
                        rs.getString("maToa"),
                        rs.getString("loaiToa"),
                        rs.getObject("samSX", Integer.class),
                        rs.getString("trangThai"),
                        rs.getObject("sucChua", Integer.class),
                        rs.getBoolean("isActive")
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
        String sql = "SELECT maToa, loaiToa, samSX, trangThai, sucChua, isActive FROM ToaTau WHERE maToa = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new ToaTau(
                            rs.getString("maToa"),
                            rs.getString("loaiToa"),
                            rs.getObject("samSX", Integer.class),
                            rs.getString("trangThai"),
                            rs.getObject("sucChua", Integer.class),
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
    public boolean insert(ToaTau t) {
        String sql = "INSERT INTO ToaTau (maToa, loaiToa, samSX, trangThai, sucChua, isActive) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, t.getMaToa());
            pst.setString(2, t.getLoaiToa());
            if (t.getSamSX() != null) pst.setInt(3, t.getSamSX()); else pst.setNull(3, Types.INTEGER);
            pst.setString(4, t.getTrangThai());
            if (t.getSucChua() != null) pst.setInt(5, t.getSucChua()); else pst.setNull(5, Types.INTEGER);
            pst.setBoolean(6, t.isActive());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(ToaTau t) {
        String sql = "UPDATE ToaTau SET loaiToa = ?, samSX = ?, trangThai = ?, sucChua = ?, isActive = ? WHERE maToa = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, t.getLoaiToa());
            if (t.getSamSX() != null) pst.setInt(2, t.getSamSX()); else pst.setNull(2, Types.INTEGER);
            pst.setString(3, t.getTrangThai());
            if (t.getSucChua() != null) pst.setInt(4, t.getSucChua()); else pst.setNull(4, Types.INTEGER);
            pst.setBoolean(5, t.isActive());
            pst.setString(6, t.getMaToa());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        String sql = "UPDATE ToaTau SET isActive = 0 WHERE maToa = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<ToaTau> getByTau(String maTau) {
        List<ToaTau> list = new ArrayList<>();
        String sql = "SELECT maToa, tenToa, loaiToa, maTau, sucChua FROM ToaTau WHERE maTau = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
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