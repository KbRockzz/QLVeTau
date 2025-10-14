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
    public List<ChiTietHoaDon> getAll() {
        List<ChiTietHoaDon> list = new ArrayList<>();
        String sql = "SELECT maHoaDon, maVe, maLoaiVe, giaGoc, giaDaKM, moTa FROM ChiTietHoaDon";
        try (PreparedStatement pst = connection.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                ChiTietHoaDon cthd = new ChiTietHoaDon(
                    rs.getString("maHoaDon"),
                    rs.getString("maVe"),
                    rs.getString("maLoaiVe"),
                    rs.getFloat("giaGoc"),
                    rs.getFloat("giaDaKM"),
                    rs.getString("moTa")
                );
                list.add(cthd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public ChiTietHoaDon findById(String id) {
        String[] ids = id.split(",");
        if (ids.length != 2) return null;
        
        String sql = "SELECT maHoaDon, maVe, maLoaiVe, giaGoc, giaDaKM, moTa FROM ChiTietHoaDon WHERE maHoaDon = ? AND maVe = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, ids[0]);
            pst.setString(2, ids[1]);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new ChiTietHoaDon(
                        rs.getString("maHoaDon"),
                        rs.getString("maVe"),
                        rs.getString("maLoaiVe"),
                        rs.getFloat("giaGoc"),
                        rs.getFloat("giaDaKM"),
                        rs.getString("moTa")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean insert(ChiTietHoaDon cthd) {
        String sql = "INSERT INTO ChiTietHoaDon (maHoaDon, maVe, maLoaiVe, giaGoc, giaDaKM, moTa) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, cthd.getMaHoaDon());
            pst.setString(2, cthd.getMaVe());
            pst.setString(3, cthd.getMaLoaiVe());
            pst.setFloat(4, cthd.getGiaGoc());
            pst.setFloat(5, cthd.getGiaDaKM());
            pst.setString(6, cthd.getMoTa());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(ChiTietHoaDon cthd) {
        String sql = "UPDATE ChiTietHoaDon SET maLoaiVe = ?, giaGoc = ?, giaDaKM = ?, moTa = ? WHERE maHoaDon = ? AND maVe = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, cthd.getMaLoaiVe());
            pst.setFloat(2, cthd.getGiaGoc());
            pst.setFloat(3, cthd.getGiaDaKM());
            pst.setString(4, cthd.getMoTa());
            pst.setString(5, cthd.getMaHoaDon());
            pst.setString(6, cthd.getMaVe());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        String[] ids = id.split(",");
        if (ids.length != 2) return false;
        
        String sql = "DELETE FROM ChiTietHoaDon WHERE maHoaDon = ? AND maVe = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, ids[0]);
            pst.setString(2, ids[1]);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy danh sách chi tiết hóa đơn theo mã hóa đơn
     */
    public List<ChiTietHoaDon> findByHoaDon(String maHoaDon) {
        List<ChiTietHoaDon> list = new ArrayList<>();
        String sql = "SELECT maHoaDon, maVe, maLoaiVe, giaGoc, giaDaKM, moTa FROM ChiTietHoaDon WHERE maHoaDon = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, maHoaDon);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    ChiTietHoaDon cthd = new ChiTietHoaDon(
                        rs.getString("maHoaDon"),
                        rs.getString("maVe"),
                        rs.getString("maLoaiVe"),
                        rs.getFloat("giaGoc"),
                        rs.getFloat("giaDaKM"),
                        rs.getString("moTa")
                    );
                    list.add(cthd);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
