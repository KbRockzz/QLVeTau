package com.trainstation.dao;

import com.trainstation.model.AuditLog;
import com.trainstation.MySQL.ConnectSql;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho bảng AuditLog
 */
public class AuditLogDAO implements GenericDAO<AuditLog> {
    private static AuditLogDAO instance;

    private AuditLogDAO() {
    }

    public static synchronized AuditLogDAO getInstance() {
        if (instance == null) {
            instance = new AuditLogDAO();
        }
        return instance;
    }

    @Override
    public List<AuditLog> getAll() {
        List<AuditLog> list = new ArrayList<>();
        String sql = "SELECT * FROM AuditLog ORDER BY thoiGian DESC";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public AuditLog findById(String id) {
        String sql = "SELECT * FROM AuditLog WHERE maLog = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean insert(AuditLog log) {
        String sql = "INSERT INTO AuditLog (maLog, loaiThaoTac, maThamChieu, maNV, thoiGian, " +
                    "noiDung, duLieuTruoc, duLieuSau, diaChiIP) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, log.getMaLog());
            pst.setString(2, log.getLoaiThaoTac());
            pst.setString(3, log.getMaThamChieu());
            pst.setString(4, log.getMaNV());
            
            if (log.getThoiGian() != null) {
                pst.setTimestamp(5, Timestamp.valueOf(log.getThoiGian()));
            } else {
                pst.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            }
            
            pst.setString(6, log.getNoiDung());
            pst.setString(7, log.getDuLieuTruoc());
            pst.setString(8, log.getDuLieuSau());
            pst.setString(9, log.getDiaChiIP());
            
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(AuditLog log) {
        // AuditLog không nên cập nhật, chỉ insert
        return false;
    }

    @Override
    public boolean delete(String id) {
        // AuditLog thường không xóa
        return false;
    }

    /**
     * Lấy audit log theo loại thao tác
     */
    public List<AuditLog> getByLoaiThaoTac(String loaiThaoTac) {
        List<AuditLog> list = new ArrayList<>();
        String sql = "SELECT * FROM AuditLog WHERE loaiThaoTac = ? ORDER BY thoiGian DESC";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, loaiThaoTac);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Lấy audit log theo nhân viên
     */
    public List<AuditLog> getByNhanVien(String maNV) {
        List<AuditLog> list = new ArrayList<>();
        String sql = "SELECT * FROM AuditLog WHERE maNV = ? ORDER BY thoiGian DESC";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maNV);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Map ResultSet to AuditLog
     */
    private AuditLog mapResultSet(ResultSet rs) throws SQLException {
        AuditLog log = new AuditLog();
        log.setMaLog(rs.getString("maLog"));
        log.setLoaiThaoTac(rs.getString("loaiThaoTac"));
        log.setMaThamChieu(rs.getString("maThamChieu"));
        log.setMaNV(rs.getString("maNV"));
        
        Timestamp thoiGian = rs.getTimestamp("thoiGian");
        if (thoiGian != null) log.setThoiGian(thoiGian.toLocalDateTime());
        
        log.setNoiDung(rs.getString("noiDung"));
        log.setDuLieuTruoc(rs.getString("duLieuTruoc"));
        log.setDuLieuSau(rs.getString("duLieuSau"));
        log.setDiaChiIP(rs.getString("diaChiIP"));
        
        return log;
    }
}
