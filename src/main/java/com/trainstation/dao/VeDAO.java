package com.trainstation.dao;

import com.trainstation.model.BangGia;
import com.trainstation.model.ChuyenTau;
import com.trainstation.model.Ve;
import com.trainstation.model.Ga;
import com.trainstation.MySQL.ConnectSql;

import java.lang.reflect.Method;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VeDAO implements GenericDAO<Ve> {
    private static VeDAO instance;
    private final GaDAO gaDAO;

    private VeDAO() {
        // Không giữ Connection làm trường — lấy connection mỗi lần cần
        this.gaDAO = GaDAO.getInstance();
    }

    public static synchronized VeDAO getInstance() {
        if (instance == null) {
            instance = new VeDAO();
        }
        return instance;
    }

    /**
     * Helper method to ensure station names are properly populated.
     * If tenGa is null or appears to be a code (short), lookup from GaDAO.
     */
    private void ensureStationNames(Ve ve) {
        if (ve == null) return;
        
        // Check and fix tenGaDi
        String tenGaDi = ve.getTenGaDi();
        if (tenGaDi == null || tenGaDi.trim().isEmpty() || tenGaDi.length() <= 3) {
            // Looks like a code or missing, lookup from database
            String maGaDi = ve.getMaGaDi();
            if (maGaDi != null && !maGaDi.isEmpty()) {
                try {
                    Ga ga = gaDAO.findById(maGaDi);
                    if (ga != null) {
                        ve.setTenGaDi(ga.getTenGa());
                    }
                } catch (Exception e) {
                    // Keep existing value if lookup fails
                }
            }
        }
        
        // Check and fix tenGaDen
        String tenGaDen = ve.getTenGaDen();
        if (tenGaDen == null || tenGaDen.trim().isEmpty() || tenGaDen.length() <= 3) {
            // Looks like a code or missing, lookup from database
            String maGaDen = ve.getMaGaDen();
            if (maGaDen != null && !maGaDen.isEmpty()) {
                try {
                    Ga ga = gaDAO.findById(maGaDen);
                    if (ga != null) {
                        ve.setTenGaDen(ga.getTenGa());
                    }
                } catch (Exception e) {
                    // Keep existing value if lookup fails
                }
            }
        }
    }

    @Override
    public List<Ve> getAll() {
        List<Ve> list = new ArrayList<>();
        String sql = "SELECT maVe, maChuyen, maLoaiVe, maSoGhe, maGaDi, maGaDen, tenGaDi, tenGaDen, ngayIn, trangThai, gioDi, gioDenDuKien, soToa, loaiCho, loaiVe, maBangGia, giaThanhToan, isActive FROM Ve WHERE isActive = 1";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                LocalDateTime ngayIn = null, gioDi = null, gioDenDuKien = null;
                Timestamp ts1 = rs.getTimestamp("ngayIn");
                if (ts1 != null) ngayIn = ts1.toLocalDateTime();
                Timestamp ts2 = rs.getTimestamp("gioDi");
                if (ts2 != null) gioDi = ts2.toLocalDateTime();
                Timestamp ts3 = rs.getTimestamp("gioDenDuKien");
                if (ts3 != null) gioDenDuKien = ts3.toLocalDateTime();

                Ve v = new Ve(
                        rs.getString("maVe"),
                        rs.getString("maChuyen"),
                        rs.getString("maLoaiVe"),
                        rs.getString("maSoGhe"),
                        rs.getString("maGaDi"),
                        rs.getString("maGaDen"),
                        rs.getString("tenGaDi"),
                        rs.getString("tenGaDen"),
                        ngayIn,
                        rs.getString("trangThai"),
                        gioDi,
                        gioDenDuKien,
                        rs.getObject("soToa", Integer.class),
                        rs.getString("loaiCho"),
                        rs.getString("loaiVe"),
                        rs.getString("maBangGia"),
                        rs.getObject("giaThanhToan", Float.class),
                        rs.getBoolean("isActive")
                );
                ensureStationNames(v);
                list.add(v);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Ve findById(String id) {
        String sql = "SELECT maVe, maChuyen, maLoaiVe, maSoGhe, maGaDi, maGaDen, tenGaDi, tenGaDen, ngayIn, trangThai, gioDi, gioDenDuKien, soToa, loaiCho, loaiVe, maBangGia, giaThanhToan, isActive FROM Ve WHERE maVe = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    LocalDateTime ngayIn = null, gioDi = null, gioDenDuKien = null;
                    Timestamp ts1 = rs.getTimestamp("ngayIn");
                    if (ts1 != null) ngayIn = ts1.toLocalDateTime();
                    Timestamp ts2 = rs.getTimestamp("gioDi");
                    if (ts2 != null) gioDi = ts2.toLocalDateTime();
                    Timestamp ts3 = rs.getTimestamp("gioDenDuKien");
                    if (ts3 != null) gioDenDuKien = ts3.toLocalDateTime();

                    Ve result = new Ve(
                            rs.getString("maVe"),
                            rs.getString("maChuyen"),
                            rs.getString("maLoaiVe"),
                            rs.getString("maSoGhe"),
                            rs.getString("maGaDi"),
                            rs.getString("maGaDen"),
                            rs.getString("tenGaDi"),
                            rs.getString("tenGaDen"),
                            ngayIn,
                            rs.getString("trangThai"),
                            gioDi,
                            gioDenDuKien,
                            rs.getObject("soToa", Integer.class),
                            rs.getString("loaiCho"),
                            rs.getString("loaiVe"),
                            rs.getString("maBangGia"),
                            rs.getObject("giaThanhToan", Float.class),
                            rs.getBoolean("isActive")
                    );
                    ensureStationNames(result);
                    return result;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean insert(Ve v) {
        String sql = "INSERT INTO Ve (maVe, maChuyen, maLoaiVe, maSoGhe, maGaDi, maGaDen, tenGaDi, tenGaDen, ngayIn, trangThai, gioDi, gioDenDuKien, soToa, loaiCho, loaiVe, maBangGia, giaThanhToan, isActive) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pst = null;
        try {
            conn = ConnectSql.getInstance().getConnection();
            // Tìm maChang từ maChuyen
            String maChang = null;
            try {
                ChuyenTauDAO ctDAO = ChuyenTauDAO.getInstance();
                ChuyenTau ct = ctDAO.findById(v.getMaChuyen());
                if (ct != null) {
                    // Giả sử ChuyenTau có phương thức getMaChang()
                    maChang = ct.getMaChang();
                }
            } catch (Exception ignored) {
                // Nếu không tìm được hoặc ChuyenTau thiếu trường, tiếp tục với maChang = null
            }

            // Tìm Bảng Giá áp dụng tại thời điểm đặt vé (ngay hiện tại)
            BangGiaDAO bgDAO = BangGiaDAO.getInstance();
            LocalDateTime now = LocalDateTime.now();
            BangGia applicable = null;
            if (maChang != null && v.getLoaiCho() != null) {
                applicable = bgDAO.findApplicable(maChang, v.getLoaiCho(), now);
            }

            if (applicable != null) {
                v.setMaBangGia(applicable.getMaBangGia());
            } else {
                // Không tìm thấy bảng giá phù hợp -> maBangGia để null (hoặc xử lý theo yêu cầu)
                v.setMaBangGia(null);
            }

            pst = conn.prepareStatement(sql);
            pst.setString(1, v.getMaVe());
            pst.setString(2, v.getMaChuyen());
            pst.setString(3, v.getMaLoaiVe());
            pst.setString(4, v.getMaSoGhe());
            pst.setString(5, v.getMaGaDi());
            pst.setString(6, v.getMaGaDen());
            pst.setString(7, v.getTenGaDi());
            pst.setString(8, v.getTenGaDen());
            if (v.getNgayIn() != null) pst.setTimestamp(9, Timestamp.valueOf(v.getNgayIn())); else pst.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));
            pst.setString(10, v.getTrangThai());
            if (v.getGioDi() != null) pst.setTimestamp(11, Timestamp.valueOf(v.getGioDi())); else pst.setNull(11, Types.TIMESTAMP);
            if (v.getGioDenDuKien() != null) pst.setTimestamp(12, Timestamp.valueOf(v.getGioDenDuKien())); else pst.setNull(12, Types.TIMESTAMP);
            if (v.getSoToa() != null) pst.setInt(13, v.getSoToa()); else pst.setNull(13, Types.INTEGER);
            pst.setString(14, v.getLoaiCho());
            pst.setString(15, v.getLoaiVe());
            pst.setString(16, v.getMaBangGia());
            if (v.getGiaThanhToan() != null) pst.setFloat(17, v.getGiaThanhToan()); else pst.setNull(17, Types.FLOAT);
            pst.setBoolean(18, v.isActive());
            System.out.println("DEBUG Ve.insert: inserting maVe=" + v.getMaVe() + " maBangGia=" + v.getMaBangGia());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try { if (pst != null) pst.close(); } catch (Exception ignored) {}
            try { if (conn != null) conn.close(); } catch (Exception ignored) {}
        }
    }

    @Override
    public boolean update(Ve v) {
        String sql = "UPDATE Ve SET maChuyen = ?, maLoaiVe = ?, maSoGhe = ?, maGaDi = ?, maGaDen = ?, tenGaDi = ?, tenGaDen = ?, ngayIn = ?, trangThai = ?, gioDi = ?, gioDenDuKien = ?, soToa = ?, loaiCho = ?, loaiVe = ?, maBangGia = ?, giaThanhToan = ?, isActive = ? WHERE maVe = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, v.getMaChuyen());
            pst.setString(2, v.getMaLoaiVe());
            pst.setString(3, v.getMaSoGhe());
            pst.setString(4, v.getMaGaDi());
            pst.setString(5, v.getMaGaDen());
            pst.setString(6, v.getTenGaDi());
            pst.setString(7, v.getTenGaDen());
            if (v.getNgayIn() != null) {
                pst.setTimestamp(8, Timestamp.valueOf(v.getNgayIn()));
            } else {
                pst.setNull(8, Types.TIMESTAMP);
            }
            pst.setString(9, v.getTrangThai());
            if (v.getGioDi() != null) {
                pst.setTimestamp(10, Timestamp.valueOf(v.getGioDi()));
            } else {
                pst.setNull(10, Types.TIMESTAMP);
            }
            if (v.getGioDenDuKien() != null) {
                pst.setTimestamp(11, Timestamp.valueOf(v.getGioDenDuKien()));
            } else {
                pst.setNull(11, Types.TIMESTAMP);
            }
            if (v.getSoToa() != null) {
                pst.setInt(12, v.getSoToa());
            } else {
                pst.setNull(12, Types.INTEGER);
            }
            pst.setString(13, v.getLoaiCho());
            pst.setString(14, v.getLoaiVe());
            pst.setString(15, v.getMaBangGia());
            if (v.getGiaThanhToan() != null) {
                pst.setFloat(16, v.getGiaThanhToan());
            } else {
                pst.setNull(16, Types.FLOAT);
            }
            pst.setBoolean(17, v.isActive());
            pst.setString(18, v.getMaVe());
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

    /**
     * Lấy danh sách vé theo mã khách hàng
     * Dùng cho đổi vé (phát triển trong tương lai)
     */
    public List<Ve> getByKhachHang(String maKH) {
        List<Ve> list = new ArrayList<>();
        String sql = "SELECT v.maVe, v.maChuyen, v.maLoaiVe, v.maSoGhe, v.maGaDi, v.maGaDen, v.tenGaDi, v.tenGaDen, v.ngayIn, v.trangThai, v.gioDi, v.gioDenDuKien, v.soToa, v.loaiCho, v.loaiVe, v.maBangGia, v.giaThanhToan, v.isActive " +
                "FROM Ve v " +
                "INNER JOIN HoaDon hd ON v.maVe = hd.maVe " +
                "WHERE hd.maKH = ? AND v.isActive = 1";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maKH);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    LocalDateTime ngayIn = null, gioDi = null, gioDenDuKien = null;
                    Timestamp ts1 = rs.getTimestamp("ngayIn");
                    if (ts1 != null) ngayIn = ts1.toLocalDateTime();
                    Timestamp ts2 = rs.getTimestamp("gioDi");
                    if (ts2 != null) gioDi = ts2.toLocalDateTime();
                    Timestamp ts3 = rs.getTimestamp("gioDenDuKien");
                    if (ts3 != null) gioDenDuKien = ts3.toLocalDateTime();

                    Ve v = new Ve(
                            rs.getString("maVe"),
                            rs.getString("maChuyen"),
                            rs.getString("maLoaiVe"),
                            rs.getString("maSoGhe"),
                            rs.getString("maGaDi"),
                            rs.getString("maGaDen"),
                            rs.getString("tenGaDi"),
                            rs.getString("tenGaDen"),
                            ngayIn,
                            rs.getString("trangThai"),
                            gioDi,
                            gioDenDuKien,
                            rs.getObject("soToa", Integer.class),
                            rs.getString("loaiCho"),
                            rs.getString("loaiVe"),
                            rs.getString("maBangGia"),
                            rs.getObject("giaThanhToan", Float.class),
                            rs.getBoolean("isActive")
                    );
                    ensureStationNames(v);
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
        String sql = "SELECT maVe, maChuyen, maLoaiVe, maSoGhe, maGaDi, maGaDen, tenGaDi, tenGaDen, ngayIn, trangThai, gioDi, gioDenDuKien, soToa, loaiCho, loaiVe, maBangGia, giaThanhToan, isActive FROM Ve WHERE maChuyen = ? AND isActive = 1";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maChuyen);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    LocalDateTime ngayIn = null, gioDi = null, gioDenDuKien = null;
                    Timestamp ts1 = rs.getTimestamp("ngayIn");
                    if (ts1 != null) ngayIn = ts1.toLocalDateTime();
                    Timestamp ts2 = rs.getTimestamp("gioDi");
                    if (ts2 != null) gioDi = ts2.toLocalDateTime();
                    Timestamp ts3 = rs.getTimestamp("gioDenDuKien");
                    if (ts3 != null) gioDenDuKien = ts3.toLocalDateTime();

                    Ve v = new Ve(
                            rs.getString("maVe"),
                            rs.getString("maChuyen"),
                            rs.getString("maLoaiVe"),
                            rs.getString("maSoGhe"),
                            rs.getString("maGaDi"),
                            rs.getString("maGaDen"),
                            rs.getString("tenGaDi"),
                            rs.getString("tenGaDen"),
                            ngayIn,
                            rs.getString("trangThai"),
                            gioDi,
                            gioDenDuKien,
                            rs.getObject("soToa", Integer.class),
                            rs.getString("loaiCho"),
                            rs.getString("loaiVe"),
                            rs.getString("maBangGia"),
                            rs.getObject("giaThanhToan", Float.class),
                            rs.getBoolean("isActive")
                    );
                    ensureStationNames(v);
                    list.add(v);
                }
            }
        }
        return list;
    }
    public List<Ve> getByChuyenAndDate(Connection conn, String maChuyen, LocalDate date) throws SQLException {
        List<Ve> list = new ArrayList<>();
        if (conn == null) throw new SQLException("Connection is null");
        if (maChuyen == null || maChuyen.trim().isEmpty() || date == null) return list;

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        String sql = "SELECT maVe, maChuyen, maSoGhe, trangThai, gioDi FROM Ve " +
                "WHERE maChuyen = ? AND gioDi >= ? AND gioDi < ? ORDER BY gioDi";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maChuyen);
            pst.setTimestamp(2, Timestamp.valueOf(start));
            pst.setTimestamp(3, Timestamp.valueOf(end));

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Ve v = new Ve();
                    v.setMaVe(rs.getString("maVe"));
                    v.setMaChuyen(rs.getString("maChuyen"));
                    v.setMaSoGhe(rs.getString("maSoGhe"));
                    v.setTrangThai(rs.getString("trangThai"));
                    Timestamp ts = rs.getTimestamp("gioDi");
                    if (ts != null) v.setGioDi(ts.toLocalDateTime());
                    // set thêm các field khác nếu cần (maKH, maLoaiVe, maBangGia, donGia, ngayIn, ...)
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