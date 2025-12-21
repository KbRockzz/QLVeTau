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
        String sql = "SELECT maChuyenTau, maToaTau, soThuTuToa, sucChua FROM ChiTietChuyenTau";
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
        list = getAll();
        return list.stream()
                .filter(ct -> ct.getMaChuyenTau().equals(maChuyenTau))
                .toList();
    }

    public ChiTietChuyenTau findById(String maChuyenTau, String maToaTau) {
        List<ChiTietChuyenTau> list = new ArrayList<>();
        list = getAll();
        return list.stream()
                .filter(ct -> ct.getMaChuyenTau().equals(maChuyenTau) && ct.getMaToaTau().equals(maToaTau))
                .findFirst()
                .orElse(null);
    }

    public boolean add(ChiTietChuyenTau entity) {
        String sql = "INSERT INTO ChiTietChuyenTau (maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES (?, ?, ?, ?)";
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
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(ChiTietChuyenTau entity) {
        String sql = "UPDATE ChiTietChuyenTau SET soThuTuToa = ?, sucChua = ? WHERE maChuyenTau = ? AND maToaTau = ?";

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

            pst.setString(3, entity.getMaChuyenTau());
            pst.setString(4, entity.getMaToaTau());

            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean delete(String maChuyenTau, String maToaTau) {
        String sql = "DELETE FROM ChiTietChuyenTau WHERE maChuyenTau = ? AND maToaTau = ?";
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
                rs.getObject("sucChua", Integer.class)
        );
    }

    /**
     * Đánh lại số thứ tự toa (soThuTuToa) cho 1 chuyến trong DB, luôn từ 1..n
     * theo thứ tự hiện tại (ORDER BY soThuTuToa, maToaTau).
     */
    public void reindexSoThuTuToa(String maChuyenTau) {
        if (maChuyenTau == null || maChuyenTau.trim().isEmpty()) return;

        String selectSql = "SELECT maToaTau FROM ChiTietChuyenTau " +
                "WHERE maChuyenTau = ? " +
                "ORDER BY COALESCE(soThuTuToa, 0), maToaTau";

        String updateSql = "UPDATE ChiTietChuyenTau SET soThuTuToa = ? " +
                "WHERE maChuyenTau = ? AND maToaTau = ?";

        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement sel = conn.prepareStatement(selectSql);
             PreparedStatement upd = conn.prepareStatement(updateSql)) {

            conn.setAutoCommit(false);

            sel.setString(1, maChuyenTau);
            try (ResultSet rs = sel.executeQuery()) {
                int idx = 1;
                while (rs.next()) {
                    String maToaTau = rs.getString("maToaTau");
                    upd.setInt(1, idx++);
                    upd.setString(2, maChuyenTau);
                    upd.setString(3, maToaTau);
                    upd.addBatch();
                }
            }

            upd.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}