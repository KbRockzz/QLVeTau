package com.trainstation.dao;

import com.trainstation.model.VeHistory;
import com.trainstation.MySQL.ConnectSql;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho bảng VeHistory
 */
public class VeHistoryDAO implements GenericDAO<VeHistory> {
    private static VeHistoryDAO instance;

    private VeHistoryDAO() {
    }

    public static synchronized VeHistoryDAO getInstance() {
        if (instance == null) {
            instance = new VeHistoryDAO();
        }
        return instance;
    }

    @Override
    public List<VeHistory> getAll() {
        List<VeHistory> list = new ArrayList<>();
        String sql = "SELECT * FROM VeHistory ORDER BY ngayThayDoi DESC";
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
    public VeHistory findById(String id) {
        String sql = "SELECT * FROM VeHistory WHERE maLichSu = ?";
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
    public boolean insert(VeHistory history) {
        String sql = "INSERT INTO VeHistory (maLichSu, maVe, maChuyen, maLoaiVe, maSoGhe, trangThai, " +
                    "gaDi, gaDen, gioDi, soToa, loaiCho, loaiVe, maBangGia, ngayThayDoi, loaiThayDoi, " +
                    "nguoiThucHien, ghiChu) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, history.getMaLichSu());
            pst.setString(2, history.getMaVe());
            pst.setString(3, history.getMaChuyen());
            pst.setString(4, history.getMaLoaiVe());
            pst.setString(5, history.getMaSoGhe());
            pst.setString(6, history.getTrangThai());
            pst.setString(7, history.getGaDi());
            pst.setString(8, history.getGaDen());
            
            if (history.getGioDi() != null) {
                pst.setTimestamp(9, Timestamp.valueOf(history.getGioDi()));
            } else {
                pst.setNull(9, Types.TIMESTAMP);
            }
            
            pst.setString(10, history.getSoToa());
            pst.setString(11, history.getLoaiCho());
            pst.setString(12, history.getLoaiVe());
            pst.setString(13, history.getMaBangGia());
            
            if (history.getNgayThayDoi() != null) {
                pst.setTimestamp(14, Timestamp.valueOf(history.getNgayThayDoi()));
            } else {
                pst.setTimestamp(14, Timestamp.valueOf(LocalDateTime.now()));
            }
            
            pst.setString(15, history.getLoaiThayDoi());
            pst.setString(16, history.getNguoiThucHien());
            pst.setString(17, history.getGhiChu());
            
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(VeHistory history) {
        // VeHistory thường không cập nhật, chỉ insert
        return false;
    }

    @Override
    public boolean delete(String id) {
        // VeHistory thường không xóa để giữ lịch sử
        return false;
    }

    /**
     * Lấy lịch sử của một vé
     */
    public List<VeHistory> getByVe(String maVe) {
        List<VeHistory> list = new ArrayList<>();
        String sql = "SELECT * FROM VeHistory WHERE maVe = ? ORDER BY ngayThayDoi DESC";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maVe);
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
     * Map ResultSet to VeHistory
     */
    private VeHistory mapResultSet(ResultSet rs) throws SQLException {
        VeHistory history = new VeHistory();
        history.setMaLichSu(rs.getString("maLichSu"));
        history.setMaVe(rs.getString("maVe"));
        history.setMaChuyen(rs.getString("maChuyen"));
        history.setMaLoaiVe(rs.getString("maLoaiVe"));
        history.setMaSoGhe(rs.getString("maSoGhe"));
        history.setTrangThai(rs.getString("trangThai"));
        history.setGaDi(rs.getString("gaDi"));
        history.setGaDen(rs.getString("gaDen"));
        
        Timestamp gioDi = rs.getTimestamp("gioDi");
        if (gioDi != null) history.setGioDi(gioDi.toLocalDateTime());
        
        history.setSoToa(rs.getString("soToa"));
        history.setLoaiCho(rs.getString("loaiCho"));
        history.setLoaiVe(rs.getString("loaiVe"));
        history.setMaBangGia(rs.getString("maBangGia"));
        
        Timestamp ngayThayDoi = rs.getTimestamp("ngayThayDoi");
        if (ngayThayDoi != null) history.setNgayThayDoi(ngayThayDoi.toLocalDateTime());
        
        history.setLoaiThayDoi(rs.getString("loaiThayDoi"));
        history.setNguoiThucHien(rs.getString("nguoiThucHien"));
        history.setGhiChu(rs.getString("ghiChu"));
        
        return history;
    }
}
