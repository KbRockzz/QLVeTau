package com.trainstation.dao;

import com.trainstation.model.GiaoDichDoiVe;
import com.trainstation.MySQL.ConnectSql;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho bảng GiaoDichDoiVe
 */
public class GiaoDichDoiVeDAO implements GenericDAO<GiaoDichDoiVe> {
    private static GiaoDichDoiVeDAO instance;

    private GiaoDichDoiVeDAO() {
    }

    public static synchronized GiaoDichDoiVeDAO getInstance() {
        if (instance == null) {
            instance = new GiaoDichDoiVeDAO();
        }
        return instance;
    }

    @Override
    public List<GiaoDichDoiVe> getAll() {
        List<GiaoDichDoiVe> list = new ArrayList<>();
        String sql = "SELECT * FROM GiaoDichDoiVe ORDER BY ngayDoi DESC";
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
    public GiaoDichDoiVe findById(String id) {
        String sql = "SELECT * FROM GiaoDichDoiVe WHERE maGiaoDich = ?";
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
    public boolean insert(GiaoDichDoiVe gd) {
        String sql = "INSERT INTO GiaoDichDoiVe (maGiaoDich, maVeCu, maVeMoi, ngayDoi, maNV, maKH, " +
                    "giaVeCu, giaVeMoi, phiDoiVe, chenhLechGia, soTienThu, soTienHoan, " +
                    "trangThai, nguoiDuyet, ngayDuyet, lyDoTuChoi, ghiChu) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, gd.getMaGiaoDich());
            pst.setString(2, gd.getMaVeCu());
            pst.setString(3, gd.getMaVeMoi());
            
            if (gd.getNgayDoi() != null) {
                pst.setTimestamp(4, Timestamp.valueOf(gd.getNgayDoi()));
            } else {
                pst.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            }
            
            pst.setString(5, gd.getMaNV());
            pst.setString(6, gd.getMaKH());
            pst.setFloat(7, gd.getGiaVeCu());
            pst.setFloat(8, gd.getGiaVeMoi());
            pst.setFloat(9, gd.getPhiDoiVe());
            pst.setFloat(10, gd.getChenhLechGia());
            pst.setFloat(11, gd.getSoTienThu());
            pst.setFloat(12, gd.getSoTienHoan());
            pst.setString(13, gd.getTrangThai());
            pst.setString(14, gd.getNguoiDuyet());
            
            if (gd.getNgayDuyet() != null) {
                pst.setTimestamp(15, Timestamp.valueOf(gd.getNgayDuyet()));
            } else {
                pst.setNull(15, Types.TIMESTAMP);
            }
            
            pst.setString(16, gd.getLyDoTuChoi());
            pst.setString(17, gd.getGhiChu());
            
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(GiaoDichDoiVe gd) {
        String sql = "UPDATE GiaoDichDoiVe SET trangThai = ?, nguoiDuyet = ?, ngayDuyet = ?, " +
                    "lyDoTuChoi = ?, ghiChu = ? WHERE maGiaoDich = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, gd.getTrangThai());
            pst.setString(2, gd.getNguoiDuyet());
            
            if (gd.getNgayDuyet() != null) {
                pst.setTimestamp(3, Timestamp.valueOf(gd.getNgayDuyet()));
            } else {
                pst.setNull(3, Types.TIMESTAMP);
            }
            
            pst.setString(4, gd.getLyDoTuChoi());
            pst.setString(5, gd.getGhiChu());
            pst.setString(6, gd.getMaGiaoDich());
            
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM GiaoDichDoiVe WHERE maGiaoDich = ?";
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
     * Lấy giao dịch đổi vé theo trạng thái
     */
    public List<GiaoDichDoiVe> getByTrangThai(String trangThai) {
        List<GiaoDichDoiVe> list = new ArrayList<>();
        String sql = "SELECT * FROM GiaoDichDoiVe WHERE trangThai = ? ORDER BY ngayDoi DESC";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, trangThai);
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
     * Lấy giao dịch đổi vé theo khách hàng
     */
    public List<GiaoDichDoiVe> getByKhachHang(String maKH) {
        List<GiaoDichDoiVe> list = new ArrayList<>();
        String sql = "SELECT * FROM GiaoDichDoiVe WHERE maKH = ? ORDER BY ngayDoi DESC";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maKH);
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
     * Map ResultSet to GiaoDichDoiVe
     */
    private GiaoDichDoiVe mapResultSet(ResultSet rs) throws SQLException {
        GiaoDichDoiVe gd = new GiaoDichDoiVe();
        gd.setMaGiaoDich(rs.getString("maGiaoDich"));
        gd.setMaVeCu(rs.getString("maVeCu"));
        gd.setMaVeMoi(rs.getString("maVeMoi"));
        
        Timestamp ngayDoi = rs.getTimestamp("ngayDoi");
        if (ngayDoi != null) gd.setNgayDoi(ngayDoi.toLocalDateTime());
        
        gd.setMaNV(rs.getString("maNV"));
        gd.setMaKH(rs.getString("maKH"));
        gd.setGiaVeCu(rs.getFloat("giaVeCu"));
        gd.setGiaVeMoi(rs.getFloat("giaVeMoi"));
        gd.setPhiDoiVe(rs.getFloat("phiDoiVe"));
        gd.setChenhLechGia(rs.getFloat("chenhLechGia"));
        gd.setSoTienThu(rs.getFloat("soTienThu"));
        gd.setSoTienHoan(rs.getFloat("soTienHoan"));
        gd.setTrangThai(rs.getString("trangThai"));
        gd.setNguoiDuyet(rs.getString("nguoiDuyet"));
        
        Timestamp ngayDuyet = rs.getTimestamp("ngayDuyet");
        if (ngayDuyet != null) gd.setNgayDuyet(ngayDuyet.toLocalDateTime());
        
        gd.setLyDoTuChoi(rs.getString("lyDoTuChoi"));
        gd.setGhiChu(rs.getString("ghiChu"));
        
        return gd;
    }
}
