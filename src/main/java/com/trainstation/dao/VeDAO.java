package com.trainstation.dao;

import com.trainstation.model.Ve;
import com.trainstation.MySQL.ConnectSql;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VeDAO implements GenericDAO<Ve> {
    private static VeDAO instance;

    private VeDAO() {
        // Không giữ Connection làm trường — lấy connection mỗi lần cần
    }

    public static synchronized VeDAO getInstance() {
        if (instance == null) {
            instance = new VeDAO();
        }
        return instance;
    }

    @Override
    public List<Ve> getAll() {
        List<Ve> list = new ArrayList<>();
        String sql = "SELECT maVe, maChuyen, maLoaiVe, maSoGhe, ngayIn, trangThai, gaDi, gaDen, gioDi, soToa, loaiCho, loaiVe, maBangGia FROM Ve";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                LocalDateTime ngayIn = null, gioDi = null;
                Timestamp ts1 = rs.getTimestamp("ngayIn");
                if (ts1 != null) ngayIn = ts1.toLocalDateTime();
                Timestamp ts2 = rs.getTimestamp("gioDi");
                if (ts2 != null) gioDi = ts2.toLocalDateTime();

                Ve v = new Ve(
                        rs.getString("maVe"),
                        rs.getString("maChuyen"),
                        rs.getString("maLoaiVe"),
                        rs.getString("maSoGhe"),
                        ngayIn,
                        rs.getString("trangThai"),
                        rs.getString("gaDi"),
                        rs.getString("gaDen"),
                        gioDi,
                        rs.getString("soToa"),
                        rs.getString("loaiCho"),
                        rs.getString("loaiVe"),
                        rs.getString("maBangGia")
                );
                list.add(v);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Ve findById(String id) {
        String sql = "SELECT maVe, maChuyen, maLoaiVe, maSoGhe, ngayIn, trangThai, gaDi, gaDen, gioDi, soToa, loaiCho, loaiVe, maBangGia FROM Ve WHERE maVe = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    LocalDateTime ngayIn = null, gioDi = null;
                    Timestamp ts1 = rs.getTimestamp("ngayIn");
                    if (ts1 != null) ngayIn = ts1.toLocalDateTime();
                    Timestamp ts2 = rs.getTimestamp("gioDi");
                    if (ts2 != null) gioDi = ts2.toLocalDateTime();

                    return new Ve(
                            rs.getString("maVe"),
                            rs.getString("maChuyen"),
                            rs.getString("maLoaiVe"),
                            rs.getString("maSoGhe"),
                            ngayIn,
                            rs.getString("trangThai"),
                            rs.getString("gaDi"),
                            rs.getString("gaDen"),
                            gioDi,
                            rs.getString("soToa"),
                            rs.getString("loaiCho"),
                            rs.getString("loaiVe"),
                            rs.getString("maBangGia")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean insert(Ve v) {
        String sql = "INSERT INTO Ve (maVe, maChuyen, maLoaiVe, maSoGhe, ngayIn, trangThai, gaDi, gaDen, gioDi, soToa, loaiCho, loaiVe, maBangGia) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, v.getMaVe());
            pst.setString(2, v.getMaChuyen());
            pst.setString(3, v.getMaLoaiVe());
            pst.setString(4, v.getMaSoGhe());
            if (v.getNgayIn() != null) {
                pst.setTimestamp(5, Timestamp.valueOf(v.getNgayIn()));
            } else {
                pst.setNull(5, Types.TIMESTAMP);
            }
            pst.setString(6, v.getTrangThai());
            pst.setString(7, v.getGaDi());
            pst.setString(8, v.getGaDen());
            if (v.getGioDi() != null) {
                pst.setTimestamp(9, Timestamp.valueOf(v.getGioDi()));
            } else {
                pst.setNull(9, Types.TIMESTAMP);
            }
            pst.setString(10, v.getSoToa());
            pst.setString(11, v.getLoaiCho());
            pst.setString(12, v.getLoaiVe());
            pst.setString(13, v.getMaBangGia());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Keep transactional insert using provided Connection (used by services that manage transactions)
    public boolean insert(Ve ve, Connection conn) throws SQLException {
        String sql = "INSERT INTO Ve (maVe, maChuyen, maLoaiVe, maSoGhe, ngayIn, trangThai, gaDi, gaDen, gioDi, soToa, loaiCho, loaiVe) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, ve.getMaVe());
            pst.setString(2, ve.getMaChuyen());
            pst.setString(3, ve.getMaLoaiVe());
            pst.setString(4, ve.getMaSoGhe());
            if (ve.getNgayIn() != null) pst.setTimestamp(5, Timestamp.valueOf(ve.getNgayIn()));
            else pst.setNull(5, Types.TIMESTAMP);
            pst.setString(6, ve.getTrangThai());
            pst.setString(7, ve.getGaDi());
            pst.setString(8, ve.getGaDen());
            if (ve.getGioDi() != null) pst.setTimestamp(9, Timestamp.valueOf(ve.getGioDi()));
            else pst.setNull(9, Types.TIMESTAMP);
            pst.setString(10, ve.getSoToa());
            pst.setString(11, ve.getLoaiCho());
            pst.setString(12, ve.getLoaiVe());
            return pst.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(Ve v) {
        String sql = "UPDATE Ve SET maChuyen = ?, maLoaiVe = ?, maSoGhe = ?, ngayIn = ?, trangThai = ?, gaDi = ?, gaDen = ?, gioDi = ?, soToa = ?, loaiCho = ?, loaiVe = ?, maBangGia = ? WHERE maVe = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, v.getMaChuyen());
            pst.setString(2, v.getMaLoaiVe());
            pst.setString(3, v.getMaSoGhe());
            if (v.getNgayIn() != null) {
                pst.setTimestamp(4, Timestamp.valueOf(v.getNgayIn()));
            } else {
                pst.setNull(4, Types.TIMESTAMP);
            }
            pst.setString(5, v.getTrangThai());
            pst.setString(6, v.getGaDi());
            pst.setString(7, v.getGaDen());
            if (v.getGioDi() != null) {
                pst.setTimestamp(8, Timestamp.valueOf(v.getGioDi()));
            } else {
                pst.setNull(8, Types.TIMESTAMP);
            }
            pst.setString(9, v.getSoToa());
            pst.setString(10, v.getLoaiCho());
            pst.setString(11, v.getLoaiVe());
            pst.setString(12, v.getMaBangGia());
            pst.setString(13, v.getMaVe());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM Ve WHERE maVe = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Ve> getByKhachHang(String maKH) {
        List<Ve> list = new ArrayList<>();
        String sql = "SELECT v.maVe, v.maChuyen, v.maLoaiVe, v.maSoGhe, v.ngayIn, v.trangThai, v.gaDi, v.gaDen, v.gioDi, v.soToa, v.loaiCho, v.loaiVe, v.maBangGia " +
                "FROM Ve v " +
                "INNER JOIN HoaDon hd ON v.maVe = hd.maVe " +
                "WHERE hd.maKH = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maKH);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    LocalDateTime ngayIn = null, gioDi = null;
                    Timestamp ts1 = rs.getTimestamp("ngayIn");
                    if (ts1 != null) ngayIn = ts1.toLocalDateTime();
                    Timestamp ts2 = rs.getTimestamp("gioDi");
                    if (ts2 != null) gioDi = ts2.toLocalDateTime();

                    Ve v = new Ve(
                            rs.getString("maVe"),
                            rs.getString("maChuyen"),
                            rs.getString("maLoaiVe"),
                            rs.getString("maSoGhe"),
                            ngayIn,
                            rs.getString("trangThai"),
                            rs.getString("gaDi"),
                            rs.getString("gaDen"),
                            gioDi,
                            rs.getString("soToa"),
                            rs.getString("loaiCho"),
                            rs.getString("loaiVe"),
                            rs.getString("maBangGia")
                    );
                    list.add(v);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public List<Ve> getByChuyen(Connection conn, String maChuyen) throws SQLException {
        List<Ve> list = new ArrayList<>();
        String sql = "SELECT maVe, maChuyen, maLoaiVe, maSoGhe, ngayIn, trangThai, gaDi, gaDen, gioDi, soToa, loaiCho, loaiVe, maBangGia FROM Ve WHERE maChuyen = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maChuyen);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    LocalDateTime ngayIn = null, gioDi = null;
                    Timestamp ts1 = rs.getTimestamp("ngayIn");
                    if (ts1 != null) ngayIn = ts1.toLocalDateTime();
                    Timestamp ts2 = rs.getTimestamp("gioDi");
                    if (ts2 != null) gioDi = ts2.toLocalDateTime();

                    Ve v = new Ve(
                            rs.getString("maVe"),
                            rs.getString("maChuyen"),
                            rs.getString("maLoaiVe"),
                            rs.getString("maSoGhe"),
                            ngayIn,
                            rs.getString("trangThai"),
                            rs.getString("gaDi"),
                            rs.getString("gaDen"),
                            gioDi,
                            rs.getString("soToa"),
                            rs.getString("loaiCho"),
                            rs.getString("loaiVe"),
                            rs.getString("maBangGia")
                    );
                    list.add(v);
                }
            }
        }
        return list;
    }
    public List<Ve> getByChuyenAndDate(Connection conn, String maChuyen, LocalDate date) throws SQLException {
        List<Ve> list = new ArrayList<>();
        String sql = "SELECT maVe, maChuyen, maSoGhe, trangThai, gioDi, ... FROM Ve WHERE maChuyen = ? AND DATE(gioDi) = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maChuyen);
            pst.setDate(2, Date.valueOf(date));
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Ve v = new Ve();
                    v.setMaVe(rs.getString("maVe"));
                    v.setMaChuyen(rs.getString("maChuyen"));
                    v.setMaSoGhe(rs.getString("maSoGhe"));
                    v.setTrangThai(rs.getString("trangThai"));
                    Timestamp ts = rs.getTimestamp("gioDi");
                    if (ts != null) v.setGioDi(ts.toLocalDateTime());
                    // set các field khác nếu cần
                    list.add(v);
                }
            }
        }
        return list;
    }

    public int countByChuyenAndDate(Connection conn, String maChuyen, LocalDate date) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Ve WHERE maChuyen = ? AND gioDi >= ? AND gioDi < ?";
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maChuyen);
            pst.setTimestamp(2, Timestamp.valueOf(start));
            pst.setTimestamp(3, Timestamp.valueOf(end));
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public int countByChuyenAndDate(String maChuyen, LocalDate date) {
        try (Connection conn = ConnectSql.getInstance().getConnection()) {
            return countByChuyenAndDate(conn, maChuyen, date);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    public List<Ve> getByChuyenAndDate(String maChuyen, java.time.LocalDate date) {
        try (java.sql.Connection conn = com.trainstation.MySQL.ConnectSql.getInstance().getConnection()) {
            return getByChuyenAndDate(conn, maChuyen, date);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

}