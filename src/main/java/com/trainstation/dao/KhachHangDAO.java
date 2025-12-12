package com.trainstation.dao;

import com.trainstation.model.KhachHang;
import com.trainstation.MySQL.ConnectSql;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KhachHangDAO implements GenericDAO<KhachHang> {
    private static KhachHangDAO instance;

    private KhachHangDAO() {
        // Không giữ Connection làm trường
    }

    public static synchronized KhachHangDAO getInstance() {
        if (instance == null) {
            instance = new KhachHangDAO();
        }
        return instance;
    }

    @Override
    public List<KhachHang> getAll() {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT maKhachHang, tenKhachHang, email, soDienThoai, isActive FROM KhachHang WHERE isActive = 1";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                KhachHang kh = new KhachHang(
                        rs.getString("maKhachHang"),
                        rs.getString("tenKhachHang"),
                        rs.getString("email"),
                        rs.getString("soDienThoai"),
                        rs.getBoolean("isActive")
                );
                list.add(kh);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public KhachHang findById(String id) {
        String sql = "SELECT maKhachHang, tenKhachHang, email, soDienThoai, isActive FROM KhachHang WHERE maKhachHang = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new KhachHang(
                            rs.getString("maKhachHang"),
                            rs.getString("tenKhachHang"),
                            rs.getString("email"),
                            rs.getString("soDienThoai"),
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
    public boolean insert(KhachHang kh) {
        String sql = "INSERT INTO KhachHang (maKhachHang, tenKhachHang, email, soDienThoai) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, kh.getMaKhachHang());
            pst.setString(2, kh.getTenKhachHang());
            pst.setString(3, kh.getEmail());
            pst.setString(4, kh.getSoDienThoai());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(KhachHang kh) {
        String sql = "UPDATE KhachHang SET tenKhachHang = ?, email = ?, soDienThoai = ? WHERE maKhachHang = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, kh.getTenKhachHang());
            pst.setString(2, kh.getEmail());
            pst.setString(3, kh.getSoDienThoai());
            pst.setString(4, kh.getMaKhachHang());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        // Soft delete: set isActive = 0 instead of deleting the record
        String sql = "UPDATE KhachHang SET isActive = 0 WHERE maKhachHang = ?";
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
     * Get all customers including soft-deleted ones
     */
    public List<KhachHang> getAllIncludingDeleted() {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT maKhachHang, tenKhachHang, email, soDienThoai, isActive FROM KhachHang";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                KhachHang kh = new KhachHang(
                        rs.getString("maKhachHang"),
                        rs.getString("tenKhachHang"),
                        rs.getString("email"),
                        rs.getString("soDienThoai"),
                        rs.getBoolean("isActive")
                );
                list.add(kh);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Restore a soft-deleted customer by setting isActive = true
     */
    public boolean restore(String id) {
        String sql = "UPDATE KhachHang SET isActive = 1 WHERE maKhachHang = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, id);
            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public KhachHang timTheoSoDienThoai(String soDienThoai) {
        String sql = "SELECT maKhachHang, tenKhachHang, email, soDienThoai, isActive FROM KhachHang WHERE soDienThoai = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, soDienThoai);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new KhachHang(
                            rs.getString("maKhachHang"),
                            rs.getString("tenKhachHang"),
                            rs.getString("email"),
                            rs.getString("soDienThoai"),
                            rs.getBoolean("isActive")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}