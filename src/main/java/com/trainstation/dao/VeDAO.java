package com.trainstation.dao;

import com.trainstation.model.BangGia;
import com.trainstation.model.ChuyenTau;
import com.trainstation.model.Ve;
import com.trainstation.MySQL.ConnectSql;

import java.lang.reflect.Method;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VeDAO implements GenericDAO<Ve> {
    private static VeDAO instance;
    private GaDAO gaDAO = GaDAO.getInstance();

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
        String sql = "SELECT maVe, maChuyen, maLoaiVe, maSoGhe, ngayIn, trangThai, tenGaDi, tenGaDen, maGaDi, maGaDen, gioDi, soToa, loaiCho, loaiVe, maBangGia FROM Ve";
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
                        rs.getString("TenGaDi"),
                        rs.getString("TenGaDen"),
                        gaDAO.findById(rs.getString("maGaDi")),
                        gaDAO.findById(rs.getString("maGaDen")),
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
        List<Ve> list = new ArrayList<>();
        list = getAll();
        return list.stream().filter(v -> v.getMaVe().equals(id)).findFirst().orElse(null);
    }

    @Override
    public boolean insert(Ve v) {
        String sql = "INSERT INTO Ve (maVe, maChuyen, maLoaiVe, maSoGhe, ngayIn, trangThai, tenGaDi, tenGaDen, maGaDi, maGaDen, gioDi, soToa, loaiCho, loaiVe, maBangGia) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
            if (v.getNgayIn() != null) pst.setTimestamp(5, Timestamp.valueOf(v.getNgayIn())); else pst.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            pst.setString(6, v.getTrangThai());
            pst.setString(7, v.getTenGaDi());
            pst.setString(8, v.getTenGaDen());
            pst.setString(8, v.getGaDen().getTenGa());
            pst.setString(8, v.getGaDen().getTenGa());
            if (v.getGioDi() != null) pst.setTimestamp(9, Timestamp.valueOf(v.getGioDi())); else pst.setNull(9, Types.TIMESTAMP);
            pst.setString(10, v.getSoToa());
            pst.setString(11, v.getLoaiCho());
            pst.setString(12, v.getLoaiVe());
            pst.setString(13, v.getMaBangGia());
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
        String sql = "UPDATE Ve SET maChuyen = ?, maLoaiVe = ?, maSoGhe = ?, ngayIn = ?, trangThai = ?, tenGaDi = ?, tenGaDen = ?, maGaDi = ?, maGaDen = ?, gioDi = ?, soToa = ?, loaiCho = ?, loaiVe = ?, maBangGia = ? WHERE maVe = ?";
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
            pst.setString(6, v.getTenGaDi());
            pst.setString(7, v.getTenGaDen());
            pst.setString(8, v.getGaDi().getMaGa());
            pst.setString(9, v.getGaDen().getMaGa());
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

    /**
     * Lấy danh sách vé theo mã khách hàng
     * Dùng cho đổi vé (phát triển trong tương lai)
     */
    public List<Ve> getByKhachHang(String maKH) {
        List<Ve> list = new ArrayList<>();
        list = getAll();
        return list.stream().filter(v -> {
            try {
                Method getMaKHMethod = Ve.class.getMethod("getMaKH");
                String veMaKH = (String) getMaKHMethod.invoke(v);
                return veMaKH != null && veMaKH.equals(maKH);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }).toList();
    }
    public List<Ve> getByChuyen(Connection conn, String maChuyen) throws SQLException {
        List<Ve> list = new ArrayList<>();
        list = getAll();
        return list.stream().filter(v -> v.getMaChuyen().equals(maChuyen)).toList();
    }
    public List<Ve> getByChuyenAndDate(Connection conn, String maChuyen, LocalDate date) throws SQLException {
        List<Ve> list = new ArrayList<>();
        list = getAll();
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        return list.stream().filter(v -> v.getMaChuyen().equals(maChuyen)
                && v.getGioDi() != null
                && !v.getGioDi().isBefore(start)
                && v.getGioDi().isBefore(end)
        ).toList();
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