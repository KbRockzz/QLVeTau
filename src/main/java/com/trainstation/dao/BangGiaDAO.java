package com.trainstation.dao;

import com.trainstation.model.BangGia;
import com.trainstation.MySQL.ConnectSql;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BangGiaDAO implements GenericDAO<BangGia> {
    private static BangGiaDAO instance;
    private Connection connection;

    private BangGiaDAO() {
        connection = ConnectSql.getInstance().getConnection();
    }

    public static synchronized BangGiaDAO getInstance() {
        if (instance == null) {
            instance = new BangGiaDAO();
        }
        return instance;
    }

    @Override
    public void add(BangGia bangGia) {
        String sql = "INSERT INTO BangGia (MaBangGia, MaLoaiGhe, MaLoaiVe, GiaTien, NgayApDung, NgayKetThuc) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, bangGia.getMaBangGia());
            pstmt.setString(2, bangGia.getMaLoaiGhe());
            pstmt.setString(3, bangGia.getMaLoaiVe());
            pstmt.setDouble(4, bangGia.getGiaTien());
            pstmt.setDate(5, bangGia.getNgayApDung() != null ? Date.valueOf(bangGia.getNgayApDung()) : null);
            pstmt.setDate(6, bangGia.getNgayKetThuc() != null ? Date.valueOf(bangGia.getNgayKetThuc()) : null);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(BangGia bangGia) {
        String sql = "UPDATE BangGia SET MaLoaiGhe = ?, MaLoaiVe = ?, GiaTien = ?, NgayApDung = ?, NgayKetThuc = ? WHERE MaBangGia = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, bangGia.getMaLoaiGhe());
            pstmt.setString(2, bangGia.getMaLoaiVe());
            pstmt.setDouble(3, bangGia.getGiaTien());
            pstmt.setDate(4, bangGia.getNgayApDung() != null ? Date.valueOf(bangGia.getNgayApDung()) : null);
            pstmt.setDate(5, bangGia.getNgayKetThuc() != null ? Date.valueOf(bangGia.getNgayKetThuc()) : null);
            pstmt.setString(6, bangGia.getMaBangGia());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM BangGia WHERE MaBangGia = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public BangGia findById(String id) {
        String sql = "SELECT * FROM BangGia WHERE MaBangGia = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractBangGiaFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<BangGia> findAll() {
        List<BangGia> bangGiaList = new ArrayList<>();
        String sql = "SELECT * FROM BangGia";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                bangGiaList.add(extractBangGiaFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bangGiaList;
    }

    public BangGia findByLoaiGheAndLoaiVe(String maLoaiGhe, String maLoaiVe) {
        String sql = "SELECT * FROM BangGia WHERE MaLoaiGhe = ? AND MaLoaiVe = ? AND (NgayKetThuc IS NULL OR NgayKetThuc >= GETDATE())";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, maLoaiGhe);
            pstmt.setString(2, maLoaiVe);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractBangGiaFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private BangGia extractBangGiaFromResultSet(ResultSet rs) throws SQLException {
        BangGia bangGia = new BangGia();
        bangGia.setMaBangGia(rs.getString("MaBangGia"));
        bangGia.setMaLoaiGhe(rs.getString("MaLoaiGhe"));
        bangGia.setMaLoaiVe(rs.getString("MaLoaiVe"));
        bangGia.setGiaTien(rs.getDouble("GiaTien"));
        Date ngayApDung = rs.getDate("NgayApDung");
        if (ngayApDung != null) {
            bangGia.setNgayApDung(ngayApDung.toLocalDate());
        }
        Date ngayKetThuc = rs.getDate("NgayKetThuc");
        if (ngayKetThuc != null) {
            bangGia.setNgayKetThuc(ngayKetThuc.toLocalDate());
        }
        return bangGia;
    }
}
