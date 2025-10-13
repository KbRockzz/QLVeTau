package com.trainstation.dao;

import com.trainstation.model.KhachHang;
import com.trainstation.MySQL.ConnectSql;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KhachHangDAO implements GenericDAO<KhachHang> {
    private static KhachHangDAO instance;
    private Connection connection;

    private KhachHangDAO() {
        connection = ConnectSql.getInstance().getConnection();
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
        String sql = "SELECT maKhachHang, tenKhachHang, email, soDienThoai FROM KhachHang";
        try (PreparedStatement pst = connection.prepareStatement(sql);
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
        String sql = "SELECT maKhachHang, tenKhachHang, email, soDienThoai FROM KhachHang WHERE maKhachHang = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new KhachHang(
                        rs.getString("maKhachHang"),
                        rs.getString("tenKhachHang"),
                        rs.getString("email"),
                        rs.getString("soDienThoai")
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
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
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
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
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
        String sql = "DELETE FROM KhachHang WHERE maKhachHang = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
