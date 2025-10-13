package com.trainstation.dao;

import com.trainstation.model.ChuyenTau;
import com.trainstation.MySQL.ConnectSql;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChuyenTauDAO implements GenericDAO<ChuyenTau> {
    private static ChuyenTauDAO instance;
    private Connection connection;

    private ChuyenTauDAO() {
        connection = ConnectSql.getInstance().getConnection();
    }

    public static synchronized ChuyenTauDAO getInstance() {
        if (instance == null) {
            instance = new ChuyenTauDAO();
        }
        return instance;
    }

    @Override
    public void add(ChuyenTau chuyenTau) {
        String sql = "INSERT INTO ChuyenTau (MaChuyenTau, TenChuyenTau, MaGaDi, MaGaDen, ThoiGianKhoiHanh, ThoiGianDen) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, chuyenTau.getMaChuyenTau());
            pstmt.setString(2, chuyenTau.getTenChuyenTau());
            pstmt.setString(3, chuyenTau.getMaGaDi());
            pstmt.setString(4, chuyenTau.getMaGaDen());
            pstmt.setTimestamp(5, chuyenTau.getThoiGianKhoiHanh() != null ? Timestamp.valueOf(chuyenTau.getThoiGianKhoiHanh()) : null);
            pstmt.setTimestamp(6, chuyenTau.getThoiGianDen() != null ? Timestamp.valueOf(chuyenTau.getThoiGianDen()) : null);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(ChuyenTau chuyenTau) {
        String sql = "UPDATE ChuyenTau SET TenChuyenTau = ?, MaGaDi = ?, MaGaDen = ?, ThoiGianKhoiHanh = ?, ThoiGianDen = ? WHERE MaChuyenTau = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, chuyenTau.getTenChuyenTau());
            pstmt.setString(2, chuyenTau.getMaGaDi());
            pstmt.setString(3, chuyenTau.getMaGaDen());
            pstmt.setTimestamp(4, chuyenTau.getThoiGianKhoiHanh() != null ? Timestamp.valueOf(chuyenTau.getThoiGianKhoiHanh()) : null);
            pstmt.setTimestamp(5, chuyenTau.getThoiGianDen() != null ? Timestamp.valueOf(chuyenTau.getThoiGianDen()) : null);
            pstmt.setString(6, chuyenTau.getMaChuyenTau());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM ChuyenTau WHERE MaChuyenTau = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ChuyenTau findById(String id) {
        String sql = "SELECT * FROM ChuyenTau WHERE MaChuyenTau = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractChuyenTauFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<ChuyenTau> findAll() {
        List<ChuyenTau> chuyenTauList = new ArrayList<>();
        String sql = "SELECT * FROM ChuyenTau";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                chuyenTauList.add(extractChuyenTauFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chuyenTauList;
    }

    public List<ChuyenTau> findByRoute(String maGaDi, String maGaDen) {
        List<ChuyenTau> chuyenTauList = new ArrayList<>();
        String sql = "SELECT * FROM ChuyenTau WHERE MaGaDi = ? AND MaGaDen = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, maGaDi);
            pstmt.setString(2, maGaDen);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                chuyenTauList.add(extractChuyenTauFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chuyenTauList;
    }

    private ChuyenTau extractChuyenTauFromResultSet(ResultSet rs) throws SQLException {
        ChuyenTau chuyenTau = new ChuyenTau();
        chuyenTau.setMaChuyenTau(rs.getString("MaChuyenTau"));
        chuyenTau.setTenChuyenTau(rs.getString("TenChuyenTau"));
        chuyenTau.setMaGaDi(rs.getString("MaGaDi"));
        chuyenTau.setMaGaDen(rs.getString("MaGaDen"));
        Timestamp thoiGianKhoiHanh = rs.getTimestamp("ThoiGianKhoiHanh");
        if (thoiGianKhoiHanh != null) {
            chuyenTau.setThoiGianKhoiHanh(thoiGianKhoiHanh.toLocalDateTime());
        }
        Timestamp thoiGianDen = rs.getTimestamp("ThoiGianDen");
        if (thoiGianDen != null) {
            chuyenTau.setThoiGianDen(thoiGianDen.toLocalDateTime());
        }
        return chuyenTau;
    }
}
