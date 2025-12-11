package com.trainstation.dao;

import com.trainstation.MySQL.ConnectSql;
import com.trainstation.model.LichSuDoiVe;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho bảng LichSuDoiVe (audit trail cho đổi vé)
 */
public class LichSuDoiVeDAO implements GenericDAO<LichSuDoiVe> {
    private static LichSuDoiVeDAO instance;

    private LichSuDoiVeDAO() {
    }

    public static synchronized LichSuDoiVeDAO getInstance() {
        if (instance == null) {
            instance = new LichSuDoiVeDAO();
        }
        return instance;
    }

    @Override
    public List<LichSuDoiVe> getAll() {
        List<LichSuDoiVe> list = new ArrayList<>();
        String sql = "SELECT maLichSu, maVe, maNV, thoiGian, chiTietCu, chiTietMoi, lyDo, trangThai, chenhLechGia FROM LichSuDoiVe ORDER BY thoiGian DESC";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public LichSuDoiVe findById(String id) {
        String sql = "SELECT maLichSu, maVe, maNV, thoiGian, chiTietCu, chiTietMoi, lyDo, trangThai, chenhLechGia FROM LichSuDoiVe WHERE maLichSu = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, id);
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

    @Override
    public boolean insert(LichSuDoiVe entity) {
        String sql = "INSERT INTO LichSuDoiVe (maLichSu, maVe, maNV, thoiGian, chiTietCu, chiTietMoi, lyDo, trangThai, chenhLechGia) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            setPreparedStatement(pst, entity);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Insert với Connection có sẵn (cho transaction)
     */
    public boolean insert(LichSuDoiVe entity, Connection conn) throws SQLException {
        String sql = "INSERT INTO LichSuDoiVe (maLichSu, maVe, maNV, thoiGian, chiTietCu, chiTietMoi, lyDo, trangThai, chenhLechGia) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            setPreparedStatement(pst, entity);
            return pst.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(LichSuDoiVe entity) {
        String sql = "UPDATE LichSuDoiVe SET maVe = ?, maNV = ?, thoiGian = ?, chiTietCu = ?, chiTietMoi = ?, lyDo = ?, trangThai = ?, chenhLechGia = ? WHERE maLichSu = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, entity.getMaVe());
            pst.setString(2, entity.getMaNV());
            if (entity.getThoiGian() != null) {
                pst.setTimestamp(3, Timestamp.valueOf(entity.getThoiGian()));
            } else {
                pst.setNull(3, Types.TIMESTAMP);
            }
            pst.setString(4, entity.getChiTietCu());
            pst.setString(5, entity.getChiTietMoi());
            pst.setString(6, entity.getLyDo());
            pst.setString(7, entity.getTrangThai());
            pst.setFloat(8, entity.getChenhLechGia());
            pst.setString(9, entity.getMaLichSu());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM LichSuDoiVe WHERE maLichSu = ?";
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
     * Lấy lịch sử đổi vé theo mã vé
     */
    public List<LichSuDoiVe> findByMaVe(String maVe) {
        List<LichSuDoiVe> list = new ArrayList<>();
        String sql = "SELECT maLichSu, maVe, maNV, thoiGian, chiTietCu, chiTietMoi, lyDo, trangThai, chenhLechGia FROM LichSuDoiVe WHERE maVe = ? ORDER BY thoiGian DESC";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maVe);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Lấy danh sách yêu cầu đổi vé theo trạng thái
     */
    public List<LichSuDoiVe> findByTrangThai(String trangThai) {
        List<LichSuDoiVe> list = new ArrayList<>();
        String sql = "SELECT maLichSu, maVe, maNV, thoiGian, chiTietCu, chiTietMoi, lyDo, trangThai, chenhLechGia FROM LichSuDoiVe WHERE trangThai = ? ORDER BY thoiGian DESC";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, trangThai);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Tạo mã lịch sử tự động
     */
    public String generateMaLichSu() {
        String sql = "SELECT TOP 1 maLichSu FROM LichSuDoiVe ORDER BY maLichSu DESC";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                String lastMa = rs.getString("maLichSu");
                if (lastMa != null && lastMa.startsWith("LSDV")) {
                    try {
                        int num = Integer.parseInt(lastMa.substring(4));
                        return String.format("LSDV%04d", num + 1);
                    } catch (NumberFormatException e) {
                        return "LSDV0001";
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "LSDV0001";
    }

    // Helper methods
    private LichSuDoiVe mapResultSetToEntity(ResultSet rs) throws SQLException {
        LichSuDoiVe entity = new LichSuDoiVe();
        entity.setMaLichSu(rs.getString("maLichSu"));
        entity.setMaVe(rs.getString("maVe"));
        entity.setMaNV(rs.getString("maNV"));
        Timestamp ts = rs.getTimestamp("thoiGian");
        if (ts != null) {
            entity.setThoiGian(ts.toLocalDateTime());
        }
        entity.setChiTietCu(rs.getString("chiTietCu"));
        entity.setChiTietMoi(rs.getString("chiTietMoi"));
        entity.setLyDo(rs.getString("lyDo"));
        entity.setTrangThai(rs.getString("trangThai"));
        entity.setChenhLechGia(rs.getFloat("chenhLechGia"));
        return entity;
    }

    private void setPreparedStatement(PreparedStatement pst, LichSuDoiVe entity) throws SQLException {
        pst.setString(1, entity.getMaLichSu());
        pst.setString(2, entity.getMaVe());
        pst.setString(3, entity.getMaNV());
        if (entity.getThoiGian() != null) {
            pst.setTimestamp(4, Timestamp.valueOf(entity.getThoiGian()));
        } else {
            pst.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
        }
        pst.setString(5, entity.getChiTietCu());
        pst.setString(6, entity.getChiTietMoi());
        pst.setString(7, entity.getLyDo());
        pst.setString(8, entity.getTrangThai());
        pst.setFloat(9, entity.getChenhLechGia());
    }
}
