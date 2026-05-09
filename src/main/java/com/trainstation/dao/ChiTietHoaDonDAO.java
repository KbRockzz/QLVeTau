package com.trainstation.dao;

import com.trainstation.MySQL.ConnectSql;
import com.trainstation.model.ChiTietHoaDon;
import com.trainstation.model.HoaDon;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietHoaDonDAO implements GenericDAO<ChiTietHoaDon> {
    private static ChiTietHoaDonDAO instance;

    private ChiTietHoaDonDAO() {
    }

    public static synchronized ChiTietHoaDonDAO getInstance() {
        if (instance == null) {
            instance = new ChiTietHoaDonDAO();
        }
        return instance;
    }

    @Override
    public List<ChiTietHoaDon> getAll() {
        List<ChiTietHoaDon> list = new ArrayList<>();
        String sql = "SELECT maHoaDon, maVe, maLoaiVe, giaGoc, giaDaKM, moTa FROM ChiTietHoaDon WHERE isActive = 1";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                ChiTietHoaDon ct = new ChiTietHoaDon();
                ct.setMaHoaDon(rs.getString("maHoaDon"));
                ct.setMaVe(rs.getString("maVe"));
                ct.setMaLoaiVe(rs.getString("maLoaiVe"));
                ct.setGiaGoc(rs.getFloat("giaGoc"));
                ct.setGiaDaKM(rs.getFloat("giaDaKM"));
                ct.setMoTa(rs.getString("moTa"));
                list.add(ct);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public ChiTietHoaDon findById(String id) {
        List<ChiTietHoaDon> list = new ArrayList<>();
        list = getAll();
        return list.stream()
                .filter(ct -> ct.getMaVe().equals(id))
                .findFirst()
                .orElse(null);
    }

    public ChiTietHoaDon findById(String id, Connection conn) throws SQLException {
        String sql = "SELECT maHoaDon, maVe, maLoaiVe, giaGoc, giaDaKM, moTa FROM ChiTietHoaDon WHERE maVe = ? AND isActive = 1";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (!rs.next()) return null;
                ChiTietHoaDon ct = new ChiTietHoaDon();
                ct.setMaHoaDon(rs.getString("maHoaDon"));
                ct.setMaVe(rs.getString("maVe"));
                ct.setMaLoaiVe(rs.getString("maLoaiVe"));
                ct.setGiaGoc(rs.getFloat("giaGoc"));
                ct.setGiaDaKM(rs.getFloat("giaDaKM"));
                ct.setMoTa(rs.getString("moTa"));
                return ct;
            }
        }
    }

    @Override
    public boolean insert(ChiTietHoaDon ct) {
        String sql = "INSERT INTO ChiTietHoaDon (maHoaDon, maVe, maLoaiVe, giaGoc, giaDaKM, moTa) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, ct.getMaHoaDon());
            pst.setString(2, ct.getMaVe());
            pst.setString(3, ct.getMaLoaiVe());
            pst.setFloat(4, ct.getGiaGoc());
            pst.setFloat(5, ct.getGiaDaKM());
            pst.setString(6, ct.getMoTa());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // New: insert using existing Connection (for transactional checkout)
    public boolean insert(ChiTietHoaDon ct, Connection conn) throws SQLException {
        String sql = "INSERT INTO ChiTietHoaDon (maHoaDon, maVe, maLoaiVe, giaGoc, giaDaKM, moTa) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, ct.getMaHoaDon());
            pst.setString(2, ct.getMaVe());
            pst.setString(3, ct.getMaLoaiVe());
            pst.setFloat(4, ct.getGiaGoc());
            pst.setFloat(5, ct.getGiaDaKM());
            pst.setString(6, ct.getMoTa());
            return pst.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(ChiTietHoaDon ct) {
        String sql = "UPDATE ChiTietHoaDon SET maLoaiVe = ?, giaGoc = ?, giaDaKM = ?, moTa = ? WHERE maHoaDon = ? AND maVe = ? AND isActive = 1";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, ct.getMaLoaiVe());
            pst.setFloat(2, ct.getGiaGoc());
            pst.setFloat(3, ct.getGiaDaKM());
            pst.setString(4, ct.getMoTa());
            pst.setString(5, ct.getMaHoaDon());
            pst.setString(6, ct.getMaVe());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        String sql = "UPDATE ChiTietHoaDon SET isActive = 0 WHERE maVe = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // New: exists check
    public boolean exists(String maHoaDon, String maVe) {
        List<ChiTietHoaDon> list = new ArrayList<>();
        list = getAll();
        return list.stream()
                .anyMatch(ct -> ct.getMaHoaDon().equals(maHoaDon) && ct.getMaVe().equals(maVe));
    }

    // exists using provided connection (transactional)
    public boolean exists(String maHoaDon, String maVe, Connection conn) throws SQLException {
        String sql = "SELECT 1 FROM ChiTietHoaDon WHERE maHoaDon = ? AND maVe = ? AND isActive = 1";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maHoaDon);
            pst.setString(2, maVe);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<ChiTietHoaDon> findByHoaDon(String maHoaDon) {
        List<ChiTietHoaDon> list = new ArrayList<>();
        list = getAll();
        return list.stream()
                .filter(ct -> ct.getMaHoaDon().equals(maHoaDon))
                .toList();
    }


    /**
     * Update moTa field using provided connection (for transactional operations)
     */
    public boolean updateMoTa(String maHoaDon, String maVe, String moTa, Connection conn) throws SQLException {
        String sql = "UPDATE ChiTietHoaDon SET moTa = ? WHERE maHoaDon = ? AND maVe = ? AND isActive = 1";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, moTa);
            pst.setString(2, maHoaDon);
            pst.setString(3, maVe);
            return pst.executeUpdate() > 0;
        }
    }
}