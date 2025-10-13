package com.trainstation.dao;

import com.trainstation.model.HoaDon;
import com.trainstation.MySQL.ConnectSql;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO implements GenericDAO<HoaDon> {
    private static HoaDonDAO instance;
    private Connection connection;

    private HoaDonDAO() {
        connection = ConnectSql.getInstance().getConnection();
    }

    public static synchronized HoaDonDAO getInstance() {
        if (instance == null) {
            instance = new HoaDonDAO();
        }
        return instance;
    }

    @Override
    public void add(HoaDon hoaDon) {
        String sql = "INSERT INTO HoaDon (MaHoaDon, CustomerID, EmployeeID, NgayLap, TongTien, TrangThai) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, hoaDon.getMaHoaDon());
            pstmt.setString(2, hoaDon.getCustomerId());
            pstmt.setString(3, hoaDon.getEmployeeId());
            pstmt.setTimestamp(4, hoaDon.getNgayLap() != null ? Timestamp.valueOf(hoaDon.getNgayLap()) : null);
            pstmt.setDouble(5, hoaDon.getTongTien());
            pstmt.setString(6, hoaDon.getTrangThai());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(HoaDon hoaDon) {
        String sql = "UPDATE HoaDon SET CustomerID = ?, EmployeeID = ?, NgayLap = ?, TongTien = ?, TrangThai = ? WHERE MaHoaDon = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, hoaDon.getCustomerId());
            pstmt.setString(2, hoaDon.getEmployeeId());
            pstmt.setTimestamp(3, hoaDon.getNgayLap() != null ? Timestamp.valueOf(hoaDon.getNgayLap()) : null);
            pstmt.setDouble(4, hoaDon.getTongTien());
            pstmt.setString(5, hoaDon.getTrangThai());
            pstmt.setString(6, hoaDon.getMaHoaDon());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM HoaDon WHERE MaHoaDon = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public HoaDon findById(String id) {
        String sql = "SELECT * FROM HoaDon WHERE MaHoaDon = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractHoaDonFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<HoaDon> findAll() {
        List<HoaDon> hoaDonList = new ArrayList<>();
        String sql = "SELECT * FROM HoaDon";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                hoaDonList.add(extractHoaDonFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hoaDonList;
    }

    public List<HoaDon> findByCustomerId(String customerId) {
        List<HoaDon> hoaDonList = new ArrayList<>();
        String sql = "SELECT * FROM HoaDon WHERE CustomerID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                hoaDonList.add(extractHoaDonFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hoaDonList;
    }

    public List<HoaDon> findByStatus(String trangThai) {
        List<HoaDon> hoaDonList = new ArrayList<>();
        String sql = "SELECT * FROM HoaDon WHERE TrangThai = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, trangThai);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                hoaDonList.add(extractHoaDonFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hoaDonList;
    }

    private HoaDon extractHoaDonFromResultSet(ResultSet rs) throws SQLException {
        HoaDon hoaDon = new HoaDon();
        hoaDon.setMaHoaDon(rs.getString("MaHoaDon"));
        hoaDon.setCustomerId(rs.getString("CustomerID"));
        hoaDon.setEmployeeId(rs.getString("EmployeeID"));
        Timestamp ngayLap = rs.getTimestamp("NgayLap");
        if (ngayLap != null) {
            hoaDon.setNgayLap(ngayLap.toLocalDateTime());
        }
        hoaDon.setTongTien(rs.getDouble("TongTien"));
        hoaDon.setTrangThai(rs.getString("TrangThai"));
        return hoaDon;
    }
}
