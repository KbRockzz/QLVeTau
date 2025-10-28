package com.trainstation.dao;

import com.trainstation.MySQL.ConnectSql;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThongKeDAO {
    private static ThongKeDAO instance;
    private Connection connection;

    private ThongKeDAO() {
        connection = ConnectSql.getInstance().getConnection();
    }

    public static synchronized ThongKeDAO getInstance() {
        if (instance == null) {
            instance = new ThongKeDAO();
        }
        return instance;
    }

    public Map<String, Double> thongKeDoanhThu(LocalDate tuNgay, LocalDate denNgay) {
        Map<String, Double> result = new HashMap<>();
        String sql = "SELECT CAST(hd.ngayLap AS DATE) as ngay, SUM(ct.giaDaKM) as tongDoanhThu " +
                "FROM HoaDon hd " +
                "JOIN ChiTietHoaDon ct ON hd.maHoaDon = ct.maHoaDon " +
                "WHERE CAST(hd.ngayLap AS DATE) BETWEEN ? AND ? " +
                "AND hd.trangThai = N'Hoàn tất' " +
                "GROUP BY CAST(hd.ngayLap AS DATE) " +
                "ORDER BY CAST(hd.ngayLap AS DATE)";

        try (PreparedStatement pst = connection.prepareStatement(sql)) {
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
    public List<Map<String, Object>> thongKeVeDoiHoan(LocalDate tuNgay, LocalDate denNgay) {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT v.maVe, ct.maHoaDon, v.ngayIn, " +
                "CASE " +
                "  WHEN v.trangThai = N'Đã hoàn' THEN N'Hoàn vé' " +
                "  WHEN v.trangThai = N'Đã đổi' THEN N'Đổi vé' " +
                "  ELSE v.trangThai " +
                "END as hinhThuc, " +
                "v.trangThai " +
                "FROM Ve v " +
                "JOIN ChiTietHoaDon ct ON v.maVe = ct.maVe " +
                "WHERE CAST(v.ngayIn AS DATE) BETWEEN ? AND ? " +
                "AND (v.trangThai = N'Đã hoàn' OR v.trangThai = N'Đã đổi') " +
                "ORDER BY v.ngayIn DESC";

        try (PreparedStatement pst = connection.prepareStatement(sql)) {
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

        int tongSoGhe = getTongSoGhe();

        String sql = "SELECT CAST(v.ngayIn AS DATE) as ngay, COUNT(v.maVe) as soVeBan " +
                "FROM Ve v " +
                "WHERE CAST(v.ngayIn AS DATE) BETWEEN ? AND ? " +
                "AND v.trangThai = N'Đã thanh toán' " +
                "GROUP BY CAST(v.ngayIn AS DATE) " +
                "ORDER BY CAST(v.ngayIn AS DATE)";

        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setDate(1, Date.valueOf(tuNgay));
            pst.setDate(2, Date.valueOf(denNgay));

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    Date ngay = rs.getDate("ngay");
                    int soVeBan = rs.getInt("soVeBan");
                    double tyLePhu = tongSoGhe > 0 ? (soVeBan * 100.0 / tongSoGhe) : 0;

                    row.put("ngay", ngay.toString());
                    row.put("soVeBan", soVeBan);
                    row.put("tongSoGhe", tongSoGhe);
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
        try (PreparedStatement pst = connection.prepareStatement(sql);
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
