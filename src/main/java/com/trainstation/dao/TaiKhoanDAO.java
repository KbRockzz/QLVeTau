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
        String sql = "SELECT maTK, maNV, tenTaiKhoan, matKhau, trangThai, isActive FROM TaiKhoan WHERE isActive = 1";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                TaiKhoan t = new TaiKhoan(
                        rs.getString("maTK"),
                        rs.getString("maNV"),
                        rs.getString("tenTaiKhoan"),
                        rs.getString("matKhau"),
                        rs.getString("trangThai"),
                        rs.getBoolean("isActive")
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
        String sql = "SELECT maTK, maNV, tenTaiKhoan, matKhau, trangThai, isActive FROM TaiKhoan WHERE maTK = ?";
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
                            rs.getString("trangThai"),
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
    public boolean insert(TaiKhoan t) {
        String sql = "INSERT INTO TaiKhoan (maTK, maNV, tenTaiKhoan, matKhau, trangThai) VALUES (?, ?, ?, ?, ?)";
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
        // Soft delete: set isActive = 0 instead of deleting the record
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
}