package com.trainstation.dao;

import com.trainstation.model.DauMay;
import com.trainstation.MySQL.ConnectSql;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DauMayDAO implements GenericDAO<DauMay> {
    private static DauMayDAO instance;

    private DauMayDAO() {
        // Không giữ Connection làm trường
    }

    public static synchronized DauMayDAO getInstance() {
        if (instance == null) {
            instance = new DauMayDAO();
        }
        return instance;
    }

    @Override
    public List<DauMay> getAll() {
        List<DauMay> list = new ArrayList<>();
        String sql = "SELECT maDauMay, loaiDauMay, tenDauMay, namSX, lanBaoTriGanNhat, trangThai FROM DauMay WHERE isActive = 1";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                DauMay t = new DauMay(
                        rs.getString("maDauMay"),
                        rs.getString("loaiDauMay"),
                        rs.getString("tenDauMay"),
                        rs.getInt("namSX"),
                        rs.getTimestamp("lanBaoTriGanNhat").toLocalDateTime(),
                        rs.getString("trangThai")
                );
                list.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public DauMay findById(String id) {
        List<DauMay> list = new ArrayList<>();
        list = getAll();
        return list.stream()
                .filter(t -> t.getMaDauMay().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean insert(DauMay t) {
        String sql = "INSERT INTO DauMay(maDauMay, loaiDauMay, tenDauMay, namSX, lanBaoTriGanNhat, trangThai) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, t.getMaDauMay());
            pst.setString(2, t.getLoaiDauMay());
            pst.setString(3, t.getTenDauMay());
            pst.setInt(4, t.getNamSX());
            pst.setTimestamp(5, Timestamp.valueOf(t.getLanBaoTriGanNhat()));
            pst.setString(6, t.getTrangThai());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(DauMay t) {
        String sql = "UPDATE DauMay SET loaiDauMay = ?, tenDauMay = ?, namSX = ?, lanBaoTriGanNhat = ?, trangThai = ? WHERE maDauMay = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, t.getLoaiDauMay());
            pst.setString(2, t.getTenDauMay());
            pst.setInt(3, t.getNamSX());
            pst.setTimestamp(4, Timestamp.valueOf(t.getLanBaoTriGanNhat()));
            pst.setString(5, t.getTrangThai());
            pst.setString(6, t.getMaDauMay());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM DauMay WHERE maDauMay = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean dungHoatDongDauMay(String maTau) {
        String sql = "UPDATE DauMay SET trangThai = N'Dừng hoạt động' WHERE maDauMay = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maTau);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<DauMay> layDauMayHoatDong() {
        List<DauMay> list = new ArrayList<>();
        String sql = "SELECT maDauMay, loaiDauMay, tenDauMay, namSX, lanBaoTriGanNhat, trangThai WHERE trangThai != N'Dừng hoạt động'";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                DauMay t = new DauMay(
                        rs.getString("maDauMay"),
                        rs.getString("loaiDauMay"),
                        rs.getString("tenDauMay"),
                        rs.getInt("namSX"),
                        rs.getTimestamp("lanBaoTriGanNhat").toLocalDateTime(),
                        rs.getString("trangThai")
                );
                list.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}