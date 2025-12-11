package com.trainstation.dao;

import com.trainstation.MySQL.ConnectSql;
import com.trainstation.model.ChuyenTau;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        String sql = "SELECT maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang, trangThai, isActive FROM ChuyenTau WHERE isActive = 1";
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
                        rs.getString("maDauMay"),
                        rs.getString("maNV"),
                        rs.getString("maGaDi"),
                        rs.getString("maGaDen"),
                        gioDi,
                        gioDen,
                        rs.getObject("soKm", Integer.class),
                        rs.getString("maChang"),
                        rs.getString("trangThai"),
                        rs.getBoolean("isActive")
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
        String sql = "SELECT maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang, trangThai, isActive FROM ChuyenTau WHERE maChuyen = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    LocalDateTime gioDi = null, gioDen = null;
                    Timestamp ts1 = rs.getTimestamp("gioDi");
                    if (ts1 != null) gioDi = ts1.toLocalDateTime();
                    Timestamp ts2 = rs.getTimestamp("gioDen");
                    if (ts2 != null) gioDen = ts2.toLocalDateTime();

                    return new ChuyenTau(
                            rs.getString("maChuyen"),
                            rs.getString("maDauMay"),
                            rs.getString("maNV"),
                            rs.getString("maGaDi"),
                            rs.getString("maGaDen"),
                            gioDi,
                            gioDen,
                            rs.getObject("soKm", Integer.class),
                            rs.getString("maChang"),
                            rs.getString("trangThai"),
                            rs.getBoolean("isActive")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean insert(ChuyenTau ct) {
        String sql = "INSERT INTO ChuyenTau (maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang, trangThai, isActive) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, ct.getMaChuyen());
            pst.setString(2, ct.getMaDauMay());
            pst.setString(3, ct.getMaNV());
            pst.setString(4, ct.getMaGaDi());
            pst.setString(5, ct.getMaGaDen());
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
            if (ct.getSoKm() != null) {
                pst.setInt(8, ct.getSoKm());
            } else {
                pst.setNull(8, Types.INTEGER);
            }
            pst.setString(9, ct.getMaChang());
            pst.setString(10, ct.getTrangThai());
            pst.setBoolean(11, ct.isActive());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(ChuyenTau ct) {
        String sql = "UPDATE ChuyenTau SET maDauMay = ?, maNV = ?, maGaDi = ?, maGaDen = ?, gioDi = ?, gioDen = ?, soKm = ?, maChang = ?, trangThai = ?, isActive = ? WHERE maChuyen = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, ct.getMaDauMay());
            pst.setString(2, ct.getMaNV());
            pst.setString(3, ct.getMaGaDi());
            pst.setString(4, ct.getMaGaDen());
            if (ct.getGioDi() != null) {
                pst.setTimestamp(5, Timestamp.valueOf(ct.getGioDi()));
            } else {
                pst.setNull(5, Types.TIMESTAMP);
            }
            if (ct.getGioDen() != null) {
                pst.setTimestamp(6, Timestamp.valueOf(ct.getGioDen()));
            } else {
                pst.setNull(6, Types.TIMESTAMP);
            }
            if (ct.getSoKm() != null) {
                pst.setInt(7, ct.getSoKm());
            } else {
                pst.setNull(7, Types.INTEGER);
            }
            pst.setString(8, ct.getMaChang());
            pst.setString(9, ct.getTrangThai());
            pst.setBoolean(10, ct.isActive());
            pst.setString(11, ct.getMaChuyen());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM ChuyenTau WHERE maChuyen = ?";
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
     * Tìm kiếm chuyến theo điều kiện tương tự mã cũ.
     */
    public List<ChuyenTau> timKiemChuyenTau(String gaDi, String gaDen, LocalDate ngayDi, LocalTime gioDi) {
        List<ChuyenTau> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT maChuyen, maTau, maNV, gaDi, gaDen, gioDi, gioDen, soKm, maChang, trangThai FROM ChuyenTau WHERE 1=1");

        if (gaDi != null && !gaDi.trim().isEmpty()) {
            sql.append(" AND gaDi = ?");
        }
        if (gaDen != null && !gaDen.trim().isEmpty()) {
            sql.append(" AND gaDen = ?");
        }
        if (ngayDi != null) {
            sql.append(" AND CAST(gioDi AS DATE) = ?");
        }
        if (gioDi != null) {
            sql.append(" AND gioDi >= ?");
        }

        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;

            if (gaDi != null && !gaDi.trim().isEmpty()) {
                pst.setString(paramIndex++, gaDi);
            }
            if (gaDen != null && !gaDen.trim().isEmpty()) {
                pst.setString(paramIndex++, gaDen);
            }
            if (ngayDi != null) {
                pst.setDate(paramIndex++, Date.valueOf(ngayDi));
            }
            if (gioDi != null) {
                LocalDateTime searchDateTime = LocalDateTime.of(ngayDi != null ? ngayDi : LocalDate.now(), gioDi);
                pst.setTimestamp(paramIndex++, Timestamp.valueOf(searchDateTime));
            }

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    LocalDateTime gioDiDT = null, gioDenDT = null;
                    Timestamp ts1 = rs.getTimestamp("gioDi");
                    if (ts1 != null) gioDiDT = ts1.toLocalDateTime();
                    Timestamp ts2 = rs.getTimestamp("gioDen");
                    if (ts2 != null) gioDenDT = ts2.toLocalDateTime();

                    ChuyenTau ct = new ChuyenTau(
                            rs.getString("maChuyen"),
                            rs.getString("maDauMay"),
                            rs.getString("maNV"),
                            rs.getString("maGaDi"),
                            rs.getString("maGaDen"),
                            gioDiDT,
                            gioDenDT,
                            rs.getObject("soKm", Integer.class),
                            rs.getString("maChang"),
                            rs.getString("trangThai"),
                            true
                    );
                    list.add(ct);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<String> getDistinctStations() {
        Set<String> stations = new HashSet<>();
        String sql = "SELECT DISTINCT gaDi FROM ChuyenTau UNION SELECT DISTINCT gaDen FROM ChuyenTau ORDER BY 1";
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