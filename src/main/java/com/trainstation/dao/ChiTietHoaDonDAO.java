package com.trainstation.dao;

import com.trainstation.model.ChiTietHoaDon;
import com.trainstation.MySQL.ConnectSql;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietHoaDonDAO implements GenericDAO<ChiTietHoaDon> {
    private static ChiTietHoaDonDAO instance;
    private Connection connection;

    private ChiTietHoaDonDAO() {
        connection = ConnectSql.getInstance().getConnection();
    }

    public static synchronized ChiTietHoaDonDAO getInstance() {
        if (instance == null) {
            instance = new ChiTietHoaDonDAO();
        }
        return instance;
    }

    @Override
    public void add(ChiTietHoaDon chiTietHoaDon) {
        String sql = "INSERT INTO ChiTietHoaDon (MaChiTiet, MaHoaDon, TicketID, DonGia, SoLuong, ThanhTien) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, chiTietHoaDon.getMaChiTiet());
            pstmt.setString(2, chiTietHoaDon.getMaHoaDon());
            pstmt.setString(3, chiTietHoaDon.getTicketId());
            pstmt.setDouble(4, chiTietHoaDon.getDonGia());
            pstmt.setInt(5, chiTietHoaDon.getSoLuong());
            pstmt.setDouble(6, chiTietHoaDon.getThanhTien());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(ChiTietHoaDon chiTietHoaDon) {
        String sql = "UPDATE ChiTietHoaDon SET MaHoaDon = ?, TicketID = ?, DonGia = ?, SoLuong = ?, ThanhTien = ? WHERE MaChiTiet = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, chiTietHoaDon.getMaHoaDon());
            pstmt.setString(2, chiTietHoaDon.getTicketId());
            pstmt.setDouble(3, chiTietHoaDon.getDonGia());
            pstmt.setInt(4, chiTietHoaDon.getSoLuong());
            pstmt.setDouble(5, chiTietHoaDon.getThanhTien());
            pstmt.setString(6, chiTietHoaDon.getMaChiTiet());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM ChiTietHoaDon WHERE MaChiTiet = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ChiTietHoaDon findById(String id) {
        String sql = "SELECT * FROM ChiTietHoaDon WHERE MaChiTiet = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractChiTietHoaDonFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<ChiTietHoaDon> findAll() {
        List<ChiTietHoaDon> chiTietHoaDonList = new ArrayList<>();
        String sql = "SELECT * FROM ChiTietHoaDon";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                chiTietHoaDonList.add(extractChiTietHoaDonFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chiTietHoaDonList;
    }

    public List<ChiTietHoaDon> findByHoaDonId(String maHoaDon) {
        List<ChiTietHoaDon> chiTietHoaDonList = new ArrayList<>();
        String sql = "SELECT * FROM ChiTietHoaDon WHERE MaHoaDon = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, maHoaDon);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                chiTietHoaDonList.add(extractChiTietHoaDonFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chiTietHoaDonList;
    }

    private ChiTietHoaDon extractChiTietHoaDonFromResultSet(ResultSet rs) throws SQLException {
        ChiTietHoaDon chiTietHoaDon = new ChiTietHoaDon();
        chiTietHoaDon.setMaChiTiet(rs.getString("MaChiTiet"));
        chiTietHoaDon.setMaHoaDon(rs.getString("MaHoaDon"));
        chiTietHoaDon.setTicketId(rs.getString("TicketID"));
        chiTietHoaDon.setDonGia(rs.getDouble("DonGia"));
        chiTietHoaDon.setSoLuong(rs.getInt("SoLuong"));
        chiTietHoaDon.setThanhTien(rs.getDouble("ThanhTien"));
        return chiTietHoaDon;
    }
}
