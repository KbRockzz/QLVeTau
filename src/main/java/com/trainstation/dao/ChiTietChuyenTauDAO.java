package com.trainstation.dao;

import com.trainstation.model.ChiTietChuyenTau;
import com.trainstation.MySQL.ConnectSql;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietChuyenTauDAO {
    private static ChiTietChuyenTauDAO instance;

    private ChiTietChuyenTauDAO() {
    }

    public static synchronized ChiTietChuyenTauDAO getInstance() {
        if (instance == null) {
            instance = new ChiTietChuyenTauDAO();
        }
        return instance;
    }

    public List<ChiTietChuyenTau> getAll() {
        List<ChiTietChuyenTau> list = new ArrayList<>();
        String sql = "SELECT maChuyenTau, maToaTau, soThuTuToa, sucChua, isActive FROM ChiTietChuyenTau WHERE isActive = 1";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                ChiTietChuyenTau ct = mapResultSetToEntity(rs);
                list.add(ct);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<ChiTietChuyenTau> findByChuyenTau(String maChuyenTau) {
        List<ChiTietChuyenTau> list = new ArrayList<>();
        String sql = "SELECT maChuyenTau, maToaTau, soThuTuToa, sucChua, isActive FROM ChiTietChuyenTau WHERE maChuyenTau = ? AND isActive = 1 ORDER BY soThuTuToa";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maChuyenTau);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    ChiTietChuyenTau ct = mapResultSetToEntity(rs);
                    list.add(ct);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ChiTietChuyenTau findById(String maChuyenTau, String maToaTau) {
        String sql = "SELECT maChuyenTau, maToaTau, soThuTuToa, sucChua, isActive FROM ChiTietChuyenTau WHERE maChuyenTau = ? AND maToaTau = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maChuyenTau);
            pst.setString(2, maToaTau);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean add(ChiTietChuyenTau entity) {
        String sql = "INSERT INTO ChiTietChuyenTau (maChuyenTau, maToaTau, soThuTuToa, sucChua, isActive) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, entity.getMaChuyenTau());
            pst.setString(2, entity.getMaToaTau());
            if (entity.getSoThuTuToa() != null) {
                pst.setInt(3, entity.getSoThuTuToa());
            } else {
                pst.setNull(3, Types.INTEGER);
            }
            if (entity.getSucChua() != null) {
                pst.setInt(4, entity.getSucChua());
            } else {
                pst.setNull(4, Types.INTEGER);
            }
            pst.setBoolean(5, entity.isActive());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(ChiTietChuyenTau entity) {
        String sql = "UPDATE ChiTietChuyenTau SET soThuTuToa = ?, sucChua = ?, isActive = ? WHERE maChuyenTau = ? AND maToaTau = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            if (entity.getSoThuTuToa() != null) {
                pst.setInt(1, entity.getSoThuTuToa());
            } else {
                pst.setNull(1, Types.INTEGER);
            }
            if (entity.getSucChua() != null) {
                pst.setInt(2, entity.getSucChua());
            } else {
                pst.setNull(2, Types.INTEGER);
            }
            pst.setBoolean(3, entity.isActive());
            pst.setString(4, entity.getMaChuyenTau());
            pst.setString(5, entity.getMaToaTau());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String maChuyenTau, String maToaTau) {
        String sql = "UPDATE ChiTietChuyenTau SET isActive = 0 WHERE maChuyenTau = ? AND maToaTau = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maChuyenTau);
            pst.setString(2, maToaTau);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private ChiTietChuyenTau mapResultSetToEntity(ResultSet rs) throws SQLException {
        return new ChiTietChuyenTau(
                rs.getString("maChuyenTau"),
                rs.getString("maToaTau"),
                rs.getObject("soThuTuToa", Integer.class),
                rs.getObject("sucChua", Integer.class),
                rs.getBoolean("isActive")
        );
    }
}
