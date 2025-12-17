package com.trainstation.dao;

import com.trainstation.model.TaiKhoan;
import com.trainstation.MySQL.ConnectSql;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaiKhoanDAO implements GenericDAO<TaiKhoan> {
    private static TaiKhoanDAO instance;

    private TaiKhoanDAO() {
        // Không giữ Connection làm trường
    }

    public static synchronized TaiKhoanDAO getInstance() {
        if (instance == null) {
            instance = new TaiKhoanDAO();
        }
        return instance;
    }

    @Override
    public List<TaiKhoan> getAll() {
        List<TaiKhoan> list = new ArrayList<>();
        // Only get active accounts (isActive = 1)
        String sql = "SELECT maTK, maNV, tenTaiKhoan, matKhau, trangThai FROM TaiKhoan WHERE isActive = 1";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                TaiKhoan t = new TaiKhoan(
                        rs.getString("maTK"),
                        rs.getString("maNV"),
                        rs.getString("tenTaiKhoan"),
                        rs.getString("matKhau"),
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
    public TaiKhoan findById(String id) {
        String sql = "SELECT maTK, maNV, tenTaiKhoan, matKhau, trangThai FROM TaiKhoan WHERE maTK = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new TaiKhoan(
                            rs.getString("maTK"),
                            rs.getString("maNV"),
                            rs.getString("tenTaiKhoan"),
                            rs.getString("matKhau"),
                            rs.getString("trangThai")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean insert(TaiKhoan t) {
        // Set isActive = 1 by default for new accounts
        String sql = "INSERT INTO TaiKhoan (maTK, maNV, tenTaiKhoan, matKhau, trangThai, isActive) VALUES (?, ?, ?, ?, ?, 1)";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, t.getMaTK());
            pst.setString(2, t.getMaNV());
            pst.setString(3, t.getTenTaiKhoan());
            pst.setString(4, t.getMatKhau());
            pst.setString(5, t.getTrangThai());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(TaiKhoan t) {
        String sql = "UPDATE TaiKhoan SET maNV = ?, tenTaiKhoan = ?, matKhau = ?, trangThai = ? WHERE maTK = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, t.getMaNV());
            pst.setString(2, t.getTenTaiKhoan());
            pst.setString(3, t.getMatKhau());
            pst.setString(4, t.getTrangThai());
            pst.setString(5, t.getMaTK());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        // Soft delete: set isActive = 0
        String sql = "UPDATE TaiKhoan SET isActive = 0 WHERE maTK = ?";
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
     * Get all deleted accounts (isActive = 0)
     * @return List of soft-deleted accounts
     */
    public List<TaiKhoan> getDeletedAccounts() {
        List<TaiKhoan> list = new ArrayList<>();
        String sql = "SELECT maTK, maNV, tenTaiKhoan, matKhau, trangThai FROM TaiKhoan WHERE isActive = 0";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                TaiKhoan t = new TaiKhoan(
                        rs.getString("maTK"),
                        rs.getString("maNV"),
                        rs.getString("tenTaiKhoan"),
                        rs.getString("matKhau"),
                        rs.getString("trangThai")
                );
                list.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Restore a soft-deleted account (set isActive = 1)
     * @param id Account ID to restore
     * @return true if restore was successful
     */
    public boolean restoreAccount(String id) {
        String sql = "UPDATE TaiKhoan SET isActive = 1 WHERE maTK = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}