package com.trainstation.dao;

import com.trainstation.model.LoaiVe;
import com.trainstation.MySQL.ConnectSql;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoaiVeDAO implements GenericDAO<LoaiVe> {
    private static LoaiVeDAO instance;
    private Connection connection;

    private LoaiVeDAO() {
        connection = ConnectSql.getInstance().getConnection();
    }

    public static synchronized LoaiVeDAO getInstance() {
        if (instance == null) {
            instance = new LoaiVeDAO();
        }
        return instance;
    }

    @Override
    public void add(LoaiVe loaiVe) {
        String sql = "INSERT INTO LoaiVe (MaLoaiVe, TenLoaiVe, PhanTramGiam, MoTa) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, loaiVe.getMaLoaiVe());
            pstmt.setString(2, loaiVe.getTenLoaiVe());
            pstmt.setDouble(3, loaiVe.getPhanTramGiam());
            pstmt.setString(4, loaiVe.getMoTa());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(LoaiVe loaiVe) {
        String sql = "UPDATE LoaiVe SET TenLoaiVe = ?, PhanTramGiam = ?, MoTa = ? WHERE MaLoaiVe = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, loaiVe.getTenLoaiVe());
            pstmt.setDouble(2, loaiVe.getPhanTramGiam());
            pstmt.setString(3, loaiVe.getMoTa());
            pstmt.setString(4, loaiVe.getMaLoaiVe());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM LoaiVe WHERE MaLoaiVe = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public LoaiVe findById(String id) {
        String sql = "SELECT * FROM LoaiVe WHERE MaLoaiVe = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractLoaiVeFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<LoaiVe> findAll() {
        List<LoaiVe> loaiVeList = new ArrayList<>();
        String sql = "SELECT * FROM LoaiVe";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                loaiVeList.add(extractLoaiVeFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loaiVeList;
    }

    private LoaiVe extractLoaiVeFromResultSet(ResultSet rs) throws SQLException {
        LoaiVe loaiVe = new LoaiVe();
        loaiVe.setMaLoaiVe(rs.getString("MaLoaiVe"));
        loaiVe.setTenLoaiVe(rs.getString("TenLoaiVe"));
        loaiVe.setPhanTramGiam(rs.getDouble("PhanTramGiam"));
        loaiVe.setMoTa(rs.getString("MoTa"));
        return loaiVe;
    }
}
