package com.trainstation.dao;

import com.trainstation.model.LoaiNV;
import com.trainstation.MySQL.ConnectSql;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoaiNVDAO implements GenericDAO<LoaiNV> {
    private static LoaiNVDAO instance;
    private Connection connection;

    private LoaiNVDAO() {
        connection = ConnectSql.getInstance().getConnection();
    }

    public static synchronized LoaiNVDAO getInstance() {
        if (instance == null) {
            instance = new LoaiNVDAO();
        }
        return instance;
    }

    @Override
    public void add(LoaiNV loaiNV) {
        String sql = "INSERT INTO LoaiNV (MaLoai, TenLoai, MoTa) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, loaiNV.getMaLoai());
            pstmt.setString(2, loaiNV.getTenLoai());
            pstmt.setString(3, loaiNV.getMoTa());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(LoaiNV loaiNV) {
        String sql = "UPDATE LoaiNV SET TenLoai = ?, MoTa = ? WHERE MaLoai = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, loaiNV.getTenLoai());
            pstmt.setString(2, loaiNV.getMoTa());
            pstmt.setString(3, loaiNV.getMaLoai());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM LoaiNV WHERE MaLoai = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public LoaiNV findById(String id) {
        String sql = "SELECT * FROM LoaiNV WHERE MaLoai = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractLoaiNVFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<LoaiNV> findAll() {
        List<LoaiNV> loaiNVList = new ArrayList<>();
        String sql = "SELECT * FROM LoaiNV";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                loaiNVList.add(extractLoaiNVFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loaiNVList;
    }

    private LoaiNV extractLoaiNVFromResultSet(ResultSet rs) throws SQLException {
        LoaiNV loaiNV = new LoaiNV();
        loaiNV.setMaLoai(rs.getString("MaLoai"));
        loaiNV.setTenLoai(rs.getString("TenLoai"));
        loaiNV.setMoTa(rs.getString("MoTa"));
        return loaiNV;
    }
}
