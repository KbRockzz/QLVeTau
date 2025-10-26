package com.trainstation.dao;

import com.trainstation.model.BangGia;
import com.trainstation.MySQL.ConnectSql;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BangGiaDAO implements GenericDAO<BangGia> {
    private static BangGiaDAO instance;

    private BangGiaDAO() {
        // Không lưu Connection làm trường
    }

    public static synchronized BangGiaDAO getInstance() {
        if (instance == null) {
            instance = new BangGiaDAO();
        }
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
            if (bg.getNgayBatDau() != null) {
                pst.setTimestamp(5, Timestamp.valueOf(bg.getNgayBatDau()));
            } else {
                pst.setNull(5, Types.TIMESTAMP);
            }
            if (bg.getNgayKetThuc() != null) {
                pst.setTimestamp(6, Timestamp.valueOf(bg.getNgayKetThuc()));
            } else {
                pst.setNull(6, Types.TIMESTAMP);
            }
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
            if (bg.getNgayBatDau() != null) {
                pst.setTimestamp(4, Timestamp.valueOf(bg.getNgayBatDau()));
            } else {
                pst.setNull(4, Types.TIMESTAMP);
            }
            if (bg.getNgayKetThuc() != null) {
                pst.setTimestamp(5, Timestamp.valueOf(bg.getNgayKetThuc()));
            } else {
                pst.setNull(5, Types.TIMESTAMP);
            }
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
}