package com.trainstation.dao;

import com.trainstation.MySQL.ConnectSql;
import com.trainstation.model.ChuyenTau;
import com.trainstation.model.Ve;

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
    private static VeDAO veDAO = VeDAO.getInstance();

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
        String sql = "SELECT maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang, trangThai FROM ChuyenTau WHERE isActive = 1";
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
        return list.stream()
                .filter(ct -> ct.getMaChuyen().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean insert(ChuyenTau ct) {
        String sql = "INSERT INTO ChuyenTau (maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang, trangThai) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(ChuyenTau ct) {
        String sql = "UPDATE ChuyenTau SET maDauMay = ?, maNV = ?, maGaDi = ?, maGaDen = ?, gioDi = ?, gioDen = ?, soKm = ?, maChang = ?, trangThai = ? WHERE maChuyen = ? AND isActive = 1";
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
            pst.setString(10, ct.getMaChuyen());
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
    public List<ChuyenTau> timKiemChuyenTau(String maGaDi, String maGaDen, LocalDate ngayDi, LocalTime gioDi) {
        return getAll().stream()
            .filter(ct -> maGaDi == null || maGaDi.isBlank() || maGaDi.equals(ct.getMaGaDi()))
            .filter(ct -> maGaDen == null || maGaDen.isBlank() || maGaDen.equals(ct.getMaGaDen()))
            .filter(ct -> ngayDi == null || (ct.getGioDi() != null && ct.getGioDi().toLocalDate().equals(ngayDi)))
            .filter(ct -> gioDi == null || (ct.getGioDi() != null &&
                !ct.getGioDi().isBefore(
                    LocalDateTime.of(ngayDi != null ? ngayDi : ct.getGioDi().toLocalDate(), gioDi))))
            .collect(java.util.stream.Collectors.toList());
    }

    public List<String> getDistinctStations() {
        Set<String> stations = new HashSet<>();
        List<ChuyenTau> list = new ArrayList<>();
        list = getAll();
        list.stream()
                .forEach(ct -> {
                    stations.add(ct.getMaGaDi());
                    stations.add(ct.getMaGaDen());
                });
        return new ArrayList<>(stations);
    }

    /**
     * Cập nhật trường trangThai cho chuyến trong Connection đã có (dùng cho transaction).
     */
    public void capNhatTrangThai(Connection conn, String maChuyen, String tThai) throws SQLException {
        String sql = "UPDATE ChuyenTau SET trangThai = ? WHERE maChuyen = ? AND isActive = 1";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, tThai);
            pst.setString(2, maChuyen);
            pst.executeUpdate();
        }
    }

    /**
     * Đếm số vé của một chuyến (không phân biệt ngày).
     * Giả định bảng Ve có cột maChuyen.
     */
    public int countTicketsForChuyenOnDate(String maChuyen) {
        List<Ve> list = veDAO.getAll();
        return list.stream()
                .filter(v -> v.getMaChuyen().equals(maChuyen))
                .mapToInt(v -> 1)
                .sum();

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
}