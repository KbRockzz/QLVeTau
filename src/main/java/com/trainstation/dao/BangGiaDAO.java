// Thay thế toàn bộ file com/trainstation/dao/BangGiaDAO.java hoặc ít nhất thay method findApplicable bằng nội dung sau:

package com.trainstation.dao;

import com.trainstation.MySQL.ConnectSql;
import com.trainstation.model.BangGia;
import com.trainstation.model.ChuyenTau;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho BangGia (cột: maBangGia, maChang, loaiGhe, giaCoBan, ngayBatDau, ngayKetThuc)
 *
 * LƯU Ý: findApplicable hiện không chấp nhận ngayKetThuc = NULL là "vô hạn".
 * Chỉ những bản ghi có ngayBatDau <= refDate AND ngayKetThuc >= refDate mới được áp dụng.
 */
public class BangGiaDAO implements GenericDAO<BangGia> {
    private static BangGiaDAO instance;

    private BangGiaDAO() {}

    public static synchronized BangGiaDAO getInstance() {
        if (instance == null) instance = new BangGiaDAO();
        return instance;
    }

    @Override
    public List<BangGia> getAll() {
        List<BangGia> list = new ArrayList<>();
        String sql = "SELECT maBangGia, maChang, loaiGhe, giaCoBan, ngayBatDau, ngayKetThuc FROM BangGia";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                LocalDateTime ngayBatDau = null, ngayKetThuc = null;
                Timestamp ts1 = rs.getTimestamp("ngayBatDau");
                if (ts1 != null) ngayBatDau = ts1.toLocalDateTime();
                Timestamp ts2 = rs.getTimestamp("ngayKetThuc");
                if (ts2 != null) ngayKetThuc = ts2.toLocalDateTime();

                BangGia bg = new BangGia(
                        rs.getString("maBangGia"),
                        rs.getString("maChang"),
                        rs.getString("loaiGhe"),
                        rs.getFloat("giaCoBan"),
                        ngayBatDau,
                        ngayKetThuc
                );
                list.add(bg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public BangGia findById(String id) {
        String sql = "SELECT maBangGia, maChang, loaiGhe, giaCoBan, ngayBatDau, ngayKetThuc FROM BangGia WHERE maBangGia = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    LocalDateTime ngayBatDau = null, ngayKetThuc = null;
                    Timestamp ts1 = rs.getTimestamp("ngayBatDau");
                    if (ts1 != null) ngayBatDau = ts1.toLocalDateTime();
                    Timestamp ts2 = rs.getTimestamp("ngayKetThuc");
                    if (ts2 != null) ngayKetThuc = ts2.toLocalDateTime();

                    return new BangGia(
                            rs.getString("maBangGia"),
                            rs.getString("maChang"),
                            rs.getString("loaiGhe"),
                            rs.getFloat("giaCoBan"),
                            ngayBatDau,
                            ngayKetThuc
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean insert(BangGia bg) {
        String sql = "INSERT INTO BangGia (maBangGia, maChang, loaiGhe, giaCoBan, ngayBatDau, ngayKetThuc) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, bg.getMaBangGia());
            pst.setString(2, bg.getMaChang());
            pst.setString(3, bg.getLoaiGhe());
            pst.setFloat(4, bg.getGiaCoBan());
            if (bg.getNgayBatDau() != null) pst.setTimestamp(5, Timestamp.valueOf(bg.getNgayBatDau())); else pst.setNull(5, Types.TIMESTAMP);
            if (bg.getNgayKetThuc() != null) pst.setTimestamp(6, Timestamp.valueOf(bg.getNgayKetThuc())); else pst.setNull(6, Types.TIMESTAMP);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(BangGia bg) {
        String sql = "UPDATE BangGia SET maChang = ?, loaiGhe = ?, giaCoBan = ?, ngayBatDau = ?, ngayKetThuc = ? WHERE maBangGia = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, bg.getMaChang());
            pst.setString(2, bg.getLoaiGhe());
            pst.setFloat(3, bg.getGiaCoBan());
            if (bg.getNgayBatDau() != null) pst.setTimestamp(4, Timestamp.valueOf(bg.getNgayBatDau())); else pst.setNull(4, Types.TIMESTAMP);
            if (bg.getNgayKetThuc() != null) pst.setTimestamp(5, Timestamp.valueOf(bg.getNgayKetThuc())); else pst.setNull(5, Types.TIMESTAMP);
            pst.setString(6, bg.getMaBangGia());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM BangGia WHERE maBangGia = ?";
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
     * Tìm bảng giá áp dụng cho một maChang và loaiGhe tại thời điểm refDate.
     * Không chấp nhận ngayKetThuc = NULL là "vô hạn".
     * Nếu không tìm thấy, fallback sang BG_DEFAULT nếu có.
     */
    public BangGia findApplicable(String maChang, String loaiGhe, LocalDateTime refDate) throws SQLException {
        if (loaiGhe == null || loaiGhe.trim().isEmpty()) return null;
        if (refDate == null) refDate = LocalDateTime.now();

        System.out.println("DEBUG BangGiaDAO.findApplicable params: maChang=" + maChang + ", loaiGhe=" + loaiGhe + ", refDate=" + refDate);

        // SQL Server: TOP 1, ưu tiên maChang cụ thể trước; require ngayKetThuc >= refDate (no NULL infinite)
        String sql = "SELECT TOP 1 maBangGia, maChang, loaiGhe, giaCoBan, ngayBatDau, ngayKetThuc " +
                "FROM BangGia " +
                "WHERE loaiGhe = ? " +
                "  AND (maChang = ? OR maChang IS NULL) " +
                "  AND ngayBatDau <= ? " +
                "  AND ngayKetThuc >= ? " +
                "ORDER BY CASE WHEN maChang = ? THEN 0 ELSE 1 END, ngayBatDau DESC";

        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, loaiGhe);
            pst.setString(2, maChang);
            pst.setTimestamp(3, Timestamp.valueOf(refDate));
            pst.setTimestamp(4, Timestamp.valueOf(refDate));
            pst.setString(5, maChang);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    LocalDateTime ngayBatDau = null, ngayKetThuc = null;
                    Timestamp ts1 = rs.getTimestamp("ngayBatDau");
                    if (ts1 != null) ngayBatDau = ts1.toLocalDateTime();
                    Timestamp ts2 = rs.getTimestamp("ngayKetThuc");
                    if (ts2 != null) ngayKetThuc = ts2.toLocalDateTime();

                    BangGia bg = new BangGia(
                            rs.getString("maBangGia"),
                            rs.getString("maChang"),
                            rs.getString("loaiGhe"),
                            rs.getFloat("giaCoBan"),
                            ngayBatDau,
                            ngayKetThuc
                    );
                    System.out.println("DEBUG BangGiaDAO.findApplicable -> found maBangGia=" + bg.getMaBangGia());
                    return bg;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

        // fallback to BG_DEFAULT if present
        System.out.println("DEBUG BangGiaDAO.findApplicable -> not found, trying BG_DEFAULT fallback");
        String sqlDefault = "SELECT maBangGia, maChang, loaiGhe, giaCoBan, ngayBatDau, ngayKetThuc FROM BangGia WHERE maBangGia = 'BG_DEFAULT'";
        try (Connection conn2 = ConnectSql.getInstance().getConnection();
             PreparedStatement pst2 = conn2.prepareStatement(sqlDefault);
             ResultSet rs2 = pst2.executeQuery()) {
            if (rs2.next()) {
                LocalDateTime ngayBatDau = null, ngayKetThuc = null;
                Timestamp ts1 = rs2.getTimestamp("ngayBatDau");
                if (ts1 != null) ngayBatDau = ts1.toLocalDateTime();
                Timestamp ts2 = rs2.getTimestamp("ngayKetThuc");
                if (ts2 != null) ngayKetThuc = ts2.toLocalDateTime();

                BangGia bg = new BangGia(
                        rs2.getString("maBangGia"),
                        rs2.getString("maChang"),
                        rs2.getString("loaiGhe"),
                        rs2.getFloat("giaCoBan"),
                        ngayBatDau,
                        ngayKetThuc
                );
                System.out.println("DEBUG BangGiaDAO.findApplicable -> using BG_DEFAULT maBangGia=" + bg.getMaBangGia());
                return bg;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public BangGia findApplicableForChuyen(String maChuyen, String loaiGheOrLoaiVe, LocalDateTime refDate) throws SQLException {
        if (maChuyen == null) {
            return findApplicable(null, loaiGheOrLoaiVe, refDate);
        }
        ChuyenTau ch = null;
        try {
            ch = ChuyenTauDAO.getInstance().findById(maChuyen);
        } catch (Exception ex) {
            // ignore and fallback to null maChang
        }
        String maChang = (ch != null) ? ch.getMaChang() : null;
        return findApplicable(maChang, loaiGheOrLoaiVe, refDate);
    }
}