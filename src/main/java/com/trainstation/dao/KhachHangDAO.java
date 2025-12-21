package com.trainstation.dao;

import com.trainstation.model.KhachHang;
import com.trainstation.MySQL.ConnectSql;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KhachHangDAO implements GenericDAO<KhachHang> {
    private static KhachHangDAO instance;

    private KhachHangDAO() {
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
        // Chỉ lấy khách hàng đang hoạt động
        String sql = "SELECT maKhachHang, tenKhachHang, email, soDienThoai FROM KhachHang WHERE isActive = 1";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                KhachHang kh = new KhachHang(
                        rs.getString("maKhachHang"),
                        rs.getString("tenKhachHang"),
                        rs.getString("email"),
                        rs.getString("soDienThoai")
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
        List<KhachHang> list = new ArrayList<>();
        list = getAll();
        return list.stream()
                .filter(kh -> kh.getMaKhachHang().equals(id))
                .findFirst()
                .orElse(null);
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
        String sql = "UPDATE KhachHang SET tenKhachHang = ?, email = ?, soDienThoai = ? WHERE maKhachHang = ? AND isActive = 1";
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
        // Xóa mềm: đặt isActive = 0
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
     * Lấy khách hàng đã xóa
     */
    public List<KhachHang> getDeletedCustomers() {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT maKhachHang, tenKhachHang, email, soDienThoai FROM KhachHang WHERE isActive = 0";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                KhachHang kh = new KhachHang(
                        rs.getString("maKhachHang"),
                        rs.getString("tenKhachHang"),
                        rs.getString("email"),
                        rs.getString("soDienThoai")
                );
                list.add(kh);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Restore a soft-deleted customer (set isActive = 1)
     * @param id Customer ID to restore
     * @return true if restore was successful
     */
    public boolean restoreCustomer(String id) {
        String sql = "UPDATE KhachHang SET isActive = 1 WHERE maKhachHang = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public KhachHang timTheoSoDienThoai(String soDienThoai) {
        List<KhachHang> list = new ArrayList<>();
        list = getAll();
        return list.stream()
                .filter(kh -> kh.getSoDienThoai().equals(soDienThoai))
                .findFirst()
                .orElse(null);
    }
}