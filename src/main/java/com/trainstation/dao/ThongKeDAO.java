package com.trainstation.dao;

import com.trainstation.MySQL.ConnectSql;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO class for statistical queries
 */
public class ThongKeDAO {
    private static ThongKeDAO instance;

    private ThongKeDAO() {
        // Không giữ Connection làm trường
    }

    public static synchronized ThongKeDAO getInstance() {
        if (instance == null) {
            instance = new ThongKeDAO();
        }
        return instance;
    }

    public Map<String, Double> thongKeDoanhThu(LocalDate tuNgay, LocalDate denNgay) {
        Map<String, Double> result = new HashMap<>();
        String sql = "SELECT DATE(hd.ngayLap) as ngay, SUM(ct.giaDaKM) as tongDoanhThu " +
                "FROM HoaDon hd " +
                "JOIN ChiTietHoaDon ct ON hd.maHoaDon = ct.maHoaDon " +
                "JOIN Ve v ON ct.maVe = v.maVe " +
                "WHERE DATE(hd.ngayLap) BETWEEN ? AND ? " +
                "AND hd.trangThai = 'Hoàn tất' " +
                "AND hd.isActive = 1 " +
                "AND ct.isActive = 1 " +
                "AND v.isActive = 1 " +
                "AND v.trangThai != 'Đã đổi' " +
                "GROUP BY DATE(hd.ngayLap) " +
                "ORDER BY DATE(hd.ngayLap)";

        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setDate(1, Date.valueOf(tuNgay));
            pst.setDate(2, Date.valueOf(denNgay));

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Date ngay = rs.getDate("ngay");
                    double doanhThu = rs.getDouble("tongDoanhThu");
                    result.put(ngay.toString(), doanhThu);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public List<Map<String, Object>> thongKeDoanhThuTheoHoaDon(LocalDate tuNgay, LocalDate denNgay) {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT hd.maHoaDon, hd.ngayLap, COUNT(ct.maVe) as soVe, SUM(ct.giaDaKM) as tongTien " +
                "FROM HoaDon hd " +
                "JOIN ChiTietHoaDon ct ON hd.maHoaDon = ct.maHoaDon " +
                "JOIN Ve v ON ct.maVe = v.maVe " +
                "WHERE DATE(hd.ngayLap) BETWEEN ? AND ? " +
                "AND hd.trangThai = 'Hoàn tất' " +
                "AND hd.isActive = 1 " +
                "AND ct.isActive = 1 " +
                "AND v.isActive = 1 " +
                "AND v.trangThai != 'Đã đổi' " +
                "GROUP BY hd.maHoaDon, hd.ngayLap " +
                "ORDER BY hd.ngayLap DESC";

        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setDate(1, Date.valueOf(tuNgay));
            pst.setDate(2, Date.valueOf(denNgay));

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("maHoaDon", rs.getString("maHoaDon"));
                    row.put("ngayLap", rs.getTimestamp("ngayLap"));
                    row.put("soVe", rs.getInt("soVe"));
                    row.put("tongTien", rs.getDouble("tongTien"));
                    result.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public List<Map<String, Object>> thongKeLoaiVeTheoDoanhThu(LocalDate tuNgay, LocalDate denNgay) {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT lv.tenLoai, COUNT(ct.maVe) as soLuong " +
                "FROM HoaDon hd " +
                "JOIN ChiTietHoaDon ct ON hd.maHoaDon = ct.maHoaDon " +
                "JOIN Ve v ON ct.maVe = v.maVe " +
                "JOIN LoaiVe lv ON ct.maLoaiVe = lv.maLoaiVe " +
                "WHERE DATE(hd.ngayLap) BETWEEN ? AND ? " +
                "AND hd.trangThai = 'Hoàn tất' " +
                "AND hd.isActive = 1 " +
                "AND ct.isActive = 1 " +
                "AND v.isActive = 1 " +
                "AND v.trangThai != 'Đã đổi' " +
                "AND lv.isActive = 1 " +
                "GROUP BY lv.tenLoai " +
                "ORDER BY soLuong DESC";

        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setDate(1, Date.valueOf(tuNgay));
            pst.setDate(2, Date.valueOf(denNgay));

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("tenLoai", rs.getString("tenLoai"));
                    row.put("soLuong", rs.getInt("soLuong"));
                    result.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public List<Map<String, Object>> thongKeVeDoiHoan(LocalDate tuNgay, LocalDate denNgay) {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT v.maVe, ct.maHoaDon, v.ngayIn, " +
                "CASE " +
                "  WHEN v.trangThai = 'Đã hoàn' THEN 'Hoàn vé' " +
                "  WHEN v.trangThai = 'Đã đổi' THEN 'Đổi vé' " +
                "  ELSE v.trangThai " +
                "END as hinhThuc, " +
                "v.trangThai " +
                "FROM Ve v " +
                "JOIN ChiTietHoaDon ct ON v.maVe = ct.maVe " +
                "WHERE DATE(v.ngayIn) BETWEEN ? AND ? " +
                "AND (v.trangThai = 'Đã hoàn' OR v.trangThai = 'Đã đổi') " +
                "AND v.isActive = 1 " +
                "AND ct.isActive = 1 " +
                "ORDER BY v.ngayIn DESC";

        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setDate(1, Date.valueOf(tuNgay));
            pst.setDate(2, Date.valueOf(denNgay));

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("maVe", rs.getString("maVe"));
                    row.put("maHoaDon", rs.getString("maHoaDon"));
                    row.put("ngayGiaoDich", rs.getTimestamp("ngayIn"));
                    row.put("hinhThuc", rs.getString("hinhThuc"));
                    row.put("trangThai", rs.getString("trangThai"));
                    result.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public List<Map<String, Object>> thongKeDoPhuGhe(LocalDate tuNgay, LocalDate denNgay) {
        List<Map<String, Object>> result = new ArrayList<>();

        String sql = "SELECT " +
                "ct.maChuyen, " +
                "CONCAT(gdi.tenGa, ' - ', gden.tenGa) as tenChuyen, " +
                "ct.gioDi, " +
                "COALESCE((SELECT SUM(sucChua) FROM ChiTietChuyenTau WHERE maChuyenTau = ct.maChuyen AND isActive = 1), 0) as tongSoGhe, " +
                "COALESCE((SELECT COUNT(*) FROM Ve WHERE maChuyen = ct.maChuyen " +
                "   AND trangThai IN ('Đã thanh toán', 'Đã đổi') " +
                "   AND isActive = 1 " +
                "   AND DATE(ngayIn) BETWEEN ? AND ?), 0) as soGheBan " +
                "FROM ChuyenTau ct " +
                "LEFT JOIN Ga gdi ON ct.maGaDi = gdi.maGa " +
                "LEFT JOIN Ga gden ON ct.maGaDen = gden.maGa " +
                "WHERE DATE(ct.gioDi) BETWEEN ? AND ? " +
                "AND ct.isActive = 1 " +
                "AND gdi.isActive = 1 " +
                "AND gden.isActive = 1 " +
                "ORDER BY ct.gioDi DESC";

        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            // Parameters for soGheBan subquery (Ve.ngayIn date range)
            pst.setDate(1, Date.valueOf(tuNgay));
            pst.setDate(2, Date.valueOf(denNgay));
            // Parameters for main query WHERE clause (ChuyenTau.gioDi date range)
            pst.setDate(3, Date.valueOf(tuNgay));
            pst.setDate(4, Date.valueOf(denNgay));

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    String maChuyen = rs.getString("maChuyen");
                    String tenChuyen = rs.getString("tenChuyen");
                    Timestamp gioDi = rs.getTimestamp("gioDi");
                    int tongSoGhe = rs.getInt("tongSoGhe");
                    int soGheBan = rs.getInt("soGheBan");
                    int soGheTrong = tongSoGhe - soGheBan;
                    double tyLePhu = tongSoGhe > 0 ? (soGheBan * 100.0 / tongSoGhe) : 0;

                    row.put("maChuyen", maChuyen);
                    row.put("tenChuyen", tenChuyen);
                    row.put("gioDi", gioDi);
                    row.put("soGheTrong", soGheTrong);
                    row.put("soGheBan", soGheBan);
                    row.put("tyLePhu", tyLePhu);
                    result.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    private int getTongSoGhe() {
        String sql = "SELECT COUNT(*) as total FROM Ghe";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}