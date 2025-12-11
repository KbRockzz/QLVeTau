package com.trainstation.dao;

import com.trainstation.MySQL.ConnectSql;
import com.trainstation.model.HoaDon;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO implements GenericDAO<HoaDon> {
    private static HoaDonDAO instance;

    private HoaDonDAO() {
    }

    public static synchronized HoaDonDAO getInstance() {
        if (instance == null) {
            instance = new HoaDonDAO();
        }
        return instance;
    }

    @Override
    public List<HoaDon> getAll() {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT maHoaDon, maNV, maKH, ngayLap, phuongThucThanhToan, trangThai FROM HoaDon";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("ngayLap");
                LocalDateTime ngayLap = ts != null ? ts.toLocalDateTime() : null;
                HoaDon hd = new HoaDon(
                        rs.getString("maHoaDon"),
                        rs.getString("maNV"),
                        rs.getString("maKH"),
                        ngayLap,
                        rs.getString("phuongThucThanhToan"),
                        rs.getString("trangThai")
                );
                list.add(hd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public HoaDon findById(String id) {
        String sql = "SELECT maHoaDon, maNV, maKH, ngayLap, phuongThucThanhToan, trangThai FROM HoaDon WHERE maHoaDon = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Timestamp ts = rs.getTimestamp("ngayLap");
                    LocalDateTime ngayLap = ts != null ? ts.toLocalDateTime() : null;
                    return new HoaDon(
                            rs.getString("maHoaDon"),
                            rs.getString("maNV"),
                            rs.getString("maKH"),
                            ngayLap,
                            rs.getString("phuongThucThanhToan"),
                            rs.getString("trangThai")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public List<HoaDon> findByKhachHang(String maKH) {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT maHoaDon, maNV, maKH, ngayLap, phuongThucThanhToan, trangThai FROM HoaDon WHERE maKH = ? ORDER BY ngayLap DESC";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maKH);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Timestamp ts = rs.getTimestamp("ngayLap");
                    java.time.LocalDateTime ngayLap = ts != null ? ts.toLocalDateTime() : null;
                    HoaDon hd = new HoaDon(
                            rs.getString("maHoaDon"),
                            rs.getString("maNV"),
                            rs.getString("maKH"),
                            ngayLap,
                            rs.getString("phuongThucThanhToan"),
                            rs.getString("trangThai")
                    );
                    list.add(hd);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    @Override
    public boolean insert(HoaDon hd) {
        String sql = "INSERT INTO HoaDon (maHoaDon, maNV, maKH, ngayLap, phuongThucThanhToan, trangThai) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, hd.getMaHoaDon());
            pst.setString(2, hd.getMaNV());
            pst.setString(3, hd.getMaKH());
            if (hd.getNgayLap() != null) pst.setTimestamp(4, Timestamp.valueOf(hd.getNgayLap()));
            else pst.setNull(4, Types.TIMESTAMP);
            pst.setString(5, hd.getPhuongThucThanhToan());
            pst.setString(6, hd.getTrangThai());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // New: insert using provided Connection
    public boolean insert(HoaDon hd, Connection conn) throws SQLException {
        String sql = "INSERT INTO HoaDon (maHoaDon, maNV, maKH, ngayLap, phuongThucThanhToan, trangThai) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, hd.getMaHoaDon());
            pst.setString(2, hd.getMaNV());
            pst.setString(3, hd.getMaKH());
            if (hd.getNgayLap() != null) pst.setTimestamp(4, Timestamp.valueOf(hd.getNgayLap()));
            else pst.setNull(4, Types.TIMESTAMP);
            pst.setString(5, hd.getPhuongThucThanhToan());
            pst.setString(6, hd.getTrangThai());
            return pst.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(HoaDon hd) {
        String sql = "UPDATE HoaDon SET maNV = ?, maKH = ?, ngayLap = ?, phuongThucThanhToan = ?, trangThai = ? WHERE maHoaDon = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, hd.getMaNV());
            pst.setString(2, hd.getMaKH());
            if (hd.getNgayLap() != null) pst.setTimestamp(3, Timestamp.valueOf(hd.getNgayLap()));
            else pst.setNull(3, Types.TIMESTAMP);
            pst.setString(4, hd.getPhuongThucThanhToan());
            pst.setString(5, hd.getTrangThai());
            pst.setString(6, hd.getMaHoaDon());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM HoaDon WHERE maHoaDon = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Transaction-aware findById method for ticket exchange
     */
    public HoaDon findById(String id, Connection conn) throws SQLException {
        String sql = "SELECT maHoaDon, maNV, maKH, ngayLap, phuongThucThanhToan, trangThai FROM HoaDon WHERE maHoaDon = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Timestamp ts = rs.getTimestamp("ngayLap");
                    LocalDateTime ngayLap = ts != null ? ts.toLocalDateTime() : null;
                    return new HoaDon(
                            rs.getString("maHoaDon"),
                            rs.getString("maNV"),
                            rs.getString("maKH"),
                            ngayLap,
                            rs.getString("phuongThucThanhToan"),
                            rs.getString("trangThai")
                    );
                }
            }
        }
        return null;
    }

}