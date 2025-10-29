package com.trainstation.dao;

import com.trainstation.MySQL.ConnectSql;
import com.trainstation.model.ChiTietHoaDon;

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
        String sql = "SELECT maHoaDon, maVe, maLoaiVe, giaGoc, giaDaKM, moTa FROM ChiTietHoaDon";
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
        // composite PK (maHoaDon, maVe) - this method not applicable; keep simple lookup by maVe
        String sql = "SELECT maHoaDon, maVe, maLoaiVe, giaGoc, giaDaKM, moTa FROM ChiTietHoaDon WHERE maVe = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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
        String sql = "UPDATE ChiTietHoaDon SET maLoaiVe = ?, giaGoc = ?, giaDaKM = ?, moTa = ? WHERE maHoaDon = ? AND maVe = ?";
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
        String sql = "DELETE FROM ChiTietHoaDon WHERE maVe = ?";
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
        String sql = "SELECT 1 FROM ChiTietHoaDon WHERE maHoaDon = ? AND maVe = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maHoaDon);
            pst.setString(2, maVe);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // exists using provided connection (transactional)
    public boolean exists(String maHoaDon, String maVe, Connection conn) throws SQLException {
        String sql = "SELECT 1 FROM ChiTietHoaDon WHERE maHoaDon = ? AND maVe = ?";
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
        String sql = "SELECT maHoaDon, maVe, maLoaiVe, giaGoc, giaDaKM, moTa FROM ChiTietHoaDon WHERE maHoaDon = ? ORDER BY maVe";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maHoaDon);
            try (ResultSet rs = pst.executeQuery()) {
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}