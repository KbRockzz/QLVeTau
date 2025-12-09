package com.trainstation.dao;

import com.trainstation.MySQL.ConnectSql;
import com.trainstation.model.ChuyenTau;
import com.trainstation.model.Ga;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Locale.filter;

/**
 * Data access object for ChuyenTau.
 *
 * Bổ sung:
 * - Các phương thức tiện ích để phục vụ auto-start / arrive logic tại tầng DAO:
 *   + countTicketsForChuyenOnDate(...)
 *   + startChuyenOnDate(...)
 *   + arriveChuyenOnDate(...)
 *
 * LƯU Ý:
 * - Một vài cột/structure của bảng Ve (vé) được giả định (ví dụ: cột daThanhToan, tinhTrang).
 *   Nếu schema thực tế khác, bạn cần đổi tên cột và logic tương ứng trong arriveChuyenOnDate(...) và countTicketsForChuyenOnDate(...).
 */
public class ChuyenTauDAO implements GenericDAO<ChuyenTau> {
    private static ChuyenTauDAO instance;
    private GaDAO gaDAO;

    private ChuyenTauDAO() {
        // Không giữ Connection làm trường
    }

    public static synchronized ChuyenTauDAO getInstance() {
        if (instance == null) {
            instance = new ChuyenTauDAO();
        }
        return instance;
    }

    @Override
    public List<ChuyenTau> getAll() {
        List<ChuyenTau> list = new ArrayList<>();
        String sql = "SELECT maChuyen, maTau, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang, trangThai FROM ChuyenTau";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                LocalDateTime gioDi = null, gioDen = null;
                Timestamp ts1 = rs.getTimestamp("gioDi");
                if (ts1 != null) gioDi = ts1.toLocalDateTime();
                Timestamp ts2 = rs.getTimestamp("gioDen");
                if (ts2 != null) gioDen = ts2.toLocalDateTime();

                ChuyenTau ct = new ChuyenTau(
                        rs.getString("maChuyen"),
                        rs.getString("maTau"),
                        rs.getString("maNV"),
                        gaDAO.findById(rs.getString("maGaDi")),
                        gaDAO.findById(rs.getString("maGaDen")),
                        gioDi,
                        gioDen,
                        rs.getInt("soKm"),
                        rs.getString("maChang"),
                        rs.getString("trangThai")
                );
                list.add(ct);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public ChuyenTau findById(String id) {
        List<ChuyenTau> list = new ArrayList<>();
        list = getAll();
        return list.stream().filter(ct -> ct.getMaChuyen().equals(id)).findFirst().orElse(null);
    }

    @Override
    public boolean insert(ChuyenTau ct) {
        String sql = "INSERT INTO ChuyenTau (maChuyen, maTau, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, ct.getMaChuyen());
            pst.setString(2, ct.getMaTau());
            pst.setString(3, ct.getMaNV());
            pst.setString(4, ct.getGaDi().toString());
            pst.setString(5, ct.getGaDen().toString());
            if (ct.getGioDi() != null) {
                pst.setTimestamp(6, Timestamp.valueOf(ct.getGioDi()));
            } else {
                pst.setNull(6, Types.TIMESTAMP);
            }
            if (ct.getGioDen() != null) {
                pst.setTimestamp(7, Timestamp.valueOf(ct.getGioDen()));
            } else {
                pst.setNull(7, Types.TIMESTAMP);
            }
            pst.setInt(8, ct.getSoKm());
            pst.setString(9, ct.getMaChang());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(ChuyenTau entity) {
        return false;
    }

    @Override
    public boolean delete(String id) {
        ChuyenTau ct = findById(id);
        if (ct == null) {
            return false;
        }
        ct.setTrangThai("Đã hủy");
        return true;
    }

    /**
     * Tìm kiếm chuyến theo điều kiện tương tự mã cũ.
     */
    public List<ChuyenTau> timKiemChuyenTau(String gaDi, String gaDen, LocalDate ngayDi, LocalTime gioDi) {
        List<ChuyenTau> list = new ArrayList<>();
        list = getAll();
        return list.stream().filter(ct -> {
            boolean match = true;
            if (gaDi != null && !gaDi.isEmpty()) {
                match = match && ct.getGaDi().getTenGa().toLowerCase().contains(gaDi.toLowerCase());
            }
            if (gaDen != null && !gaDen.isEmpty()) {
                match = match && ct.getGaDen().getTenGa().toLowerCase().contains(gaDen.toLowerCase());
            }
            if (ngayDi != null) {
                match = match && ct.getGioDi() != null && ct.getGioDi().toLocalDate().isEqual(ngayDi);
            }
            if (gioDi != null) {
                match = match && ct.getGioDi() != null && ct.getGioDi().toLocalTime().equals(gioDi);
            }
            return match;
        }).toList();
    }

    public List<String> getDistinctStations() {
        Set<String> stations = new HashSet<>();
        String sql = "SELECT DISTINCT maGaDi FROM ChuyenTau UNION SELECT DISTINCT maGaDen FROM ChuyenTau ORDER BY 1";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                stations.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(stations);
    }

    /**
     * Cập nhật trường trangThai cho chuyến trong Connection đã có (dùng cho transaction).
     */
    public void capNhatTrangThai(Connection conn, String maChuyen, String tThai) throws SQLException {
        String sql = "UPDATE ChuyenTau SET trangThai = ? WHERE maChuyen = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, tThai);
            pst.setString(2, maChuyen);
            pst.executeUpdate();
        }
    }

    /**
     * Đếm số vé cho chuyến trên một ngày cụ thể.
     * Giả định bảng Ve có cột maChuyen và gioDi (timestamp).
     */
    public int countTicketsForChuyenOnDate(String maChuyen, LocalDate date) {
        String sql = "SELECT COUNT(*) FROM Ve WHERE maChuyen = ? AND CAST(gioDi AS DATE) = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maChuyen);
            pst.setDate(2, Date.valueOf(date));
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Đánh dấu chuyến là "Đã khởi hành" cho ngày cụ thể.
     * Trả về true nếu cập nhật thành công.
     *
     * Lưu ý: phương thức này chỉ cập nhật trường trangThai ở bảng ChuyenTau.
     */
    public boolean startChuyenOnDate(String maChuyen, LocalDate date, String source) {
        String newStatus = "Đã khởi hành";
        // Nếu muốn lưu log source, cần bảng log riêng; hiện chỉ cập nhật trạng thái.
        try (Connection conn = ConnectSql.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            capNhatTrangThai(conn, maChuyen, newStatus);
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            // trường hợp lỗi trả false
            return false;
        }
    }

    /**
     * Xử lý "arrive" (đến nơi) cho chuyến vào ngày date:
     * - Cập nhật trạng thái chuyến (trangThai = 'Đã đến')
     * - Giải phóng ghế (cập nhật vé) theo chính sách:
     *     + nếu freePaid == false: chỉ giải phóng vé chưa thanh toán (giả sử daThanhToan = 0)
     *     + nếu freePaid == true: giải phóng tất cả vé của chuyến trong ngày
     *
     * Trả về số vé đã được thay đổi (số hàng Ve cập nhật).
     *
     * LƯU Ý RẤT QUAN TRỌNG: mình giả định bảng Vé có cột:
     *    - maVe (PK)
     *    - maChuyen
     *    - gioDi (timestamp)
     *    - daThanhToan (tinyint(1) hoặc int 0/1)
     *    - tinhTrang (varchar) -- sẽ cập nhật sang 'Hủy' để giải phóng ghế
     *
     * Nếu schema Vé khác, bạn cần chỉnh lại các tên cột và logic WHERE/SET.
     */
    public int arriveChuyenOnDate(String maChuyen, LocalDate date, boolean freePaid, String source) {
        String newStatus = "Đã đến";
        // cập nhật trạng thái chuyến + cập nhật vé trong transaction
        String updateVeSqlAll = "UPDATE Ve SET tinhTrang = ? WHERE maChuyen = ? AND CAST(gioDi AS DATE) = ? AND tinhTrang != ?";
        String updateVeSqlUnpaid = "UPDATE Ve SET tinhTrang = ? WHERE maChuyen = ? AND CAST(gioDi AS DATE) = ? AND daThanhToan = 0 AND tinhTrang != ?";

        int updatedCount = 0;
        try (Connection conn = ConnectSql.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            // 1) cập nhật trạng thái chuyến
            capNhatTrangThai(conn, maChuyen, newStatus);

            // 2) cập nhật vé: đánh dấu tinhTrang = 'Hủy' để giải phóng ghế
            String cancelStatus = "Hủy";

            try (PreparedStatement pst = conn.prepareStatement(freePaid ? updateVeSqlAll : updateVeSqlUnpaid)) {
                pst.setString(1, cancelStatus);
                pst.setString(2, maChuyen);
                pst.setDate(3, Date.valueOf(date));
                pst.setString(4, cancelStatus); // tránh cập nhật lại những vé đã 'Hủy'
                updatedCount = pst.executeUpdate();
            }

            conn.commit();
            return updatedCount;
        } catch (SQLException e) {
            e.printStackTrace();
            // nếu lỗi, rollback cố gắng thực hiện (try-catch)
            try {
                // attempt rollback
                Connection conn = ConnectSql.getInstance().getConnection();
                if (conn != null && !conn.getAutoCommit()) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return 0;
        }
    }
}