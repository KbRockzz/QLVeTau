package com.trainstation.service;

import com.trainstation.MySQL.ConnectSql;
import com.trainstation.dao.ChuyenTauDAO;
import com.trainstation.dao.VeDAO;
import com.trainstation.dao.TauDAO;
import com.trainstation.model.ChuyenTau;
import com.trainstation.model.Ve;
import com.trainstation.model.Tau;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

/**
 * Service xử lý nghiệp vụ cho Chuyến tàu theo 'ngày' (không tạo ChuyenInstance).
 * - startChuyenOnDate: cập nhật trạng thái tàu (đang chạy) theo chuyến + date (ghi log)
 * - arriveChuyenOnDate: giải phóng ghế cho các vé thuộc chuyến vào ngày đó, cập nhật trạng thái vé, cập nhật trạng thái tàu
 * - cancelChuyenOnDate: hủy (theo policy)
 *
 * Note:
 * - VeDAO cần phương thức getByChuyenAndDate(Connection, String, LocalDate) hoặc tương đương.
 * - VeDAO có thể có phương thức countByChuyenAndDate(Connection, String, LocalDate) để tối ưu hiển thị.
 * - TauDAO cần phương thức capNhatTrangThai(Connection, String, String) và findById(String).
 * - ChuyenTauDAO đã có getAll() và findById() trong repo.
 *
 * Các chuỗi trạng thái ("Đã khởi hành", "Đã đến", "Sẵn sàng", "Đã hủy") có thể thay đổi theo dự án — điều chỉnh nếu DB lưu khác.
 */
public class ChuyenTauService {
    private static ChuyenTauService instance;
    private final ChuyenTauDAO chuyenTauDAO;
    private final VeDAO veDAO;
    private final TauDAO tauDAO;

    private final File actionLogFile;

    private ChuyenTauService() {
        chuyenTauDAO = ChuyenTauDAO.getInstance();
        veDAO = VeDAO.getInstance();
        tauDAO = TauDAO.getInstance();
        // log đơn giản vào file logs/chuyen_actions.log
        File logsDir = new File("logs");
        if (!logsDir.exists()) logsDir.mkdirs();
        actionLogFile = new File(logsDir, "chuyen_actions.log");
    }

    public static synchronized ChuyenTauService getInstance() {
        if (instance == null) instance = new ChuyenTauService();
        return instance;
    }

    public List<ChuyenTau> getAllChuyen() {
        return chuyenTauDAO.getAll();
    }

    /**
     * Count tickets for a chuyến on a particular date (delegates to VeDAO if available).
     */
    public int countTicketsForChuyenOnDate(String maChuyen, LocalDate date) {
        try (Connection conn = ConnectSql.getInstance().getConnection()) {
            // try to use specialized DAO count method if present
            try {
                return veDAO.countByChuyenAndDate(conn, maChuyen, date);
            } catch (NoSuchMethodError | AbstractMethodError | UnsupportedOperationException ex) {
                // fallback: get list and size
                List<Ve> list = veDAO.getByChuyenAndDate(conn, maChuyen, date);
                return list == null ? 0 : list.size();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Khởi hành chuyến vào ngày cụ thể: cập nhật trạng thái tàu = "Đang chạy".
     * Không cập nhật ChuyenTau toàn cục (theo yêu cầu).
     */
    public void startChuyenOnDate(String maChuyen, LocalDate date, String user) throws SQLException {
        Connection conn = null;
        boolean prevAuto = true;
        try {
            conn = ConnectSql.getInstance().getConnection();
            prevAuto = conn.getAutoCommit();
            conn.setAutoCommit(false);

            ChuyenTau ct = chuyenTauDAO.findById(maChuyen);
            if (ct == null) throw new SQLException("Không tìm thấy chuyến: " + maChuyen);
            String maTau = ct.getMaTau();

            // Kiểm tra trạng thái chuyến tàu
            ChuyenTau Ctau = chuyenTauDAO.findById(maTau);
            if (Ctau != null) {
                String cur = Ctau.getTrangThai() == null ? "" : Ctau.getTrangThai().trim();
                // chỉ cho start nếu ctàu đang "Sẵn sàng"
                if (!cur.isEmpty() || !cur.equalsIgnoreCase("Sẵn sàng")){
                    throw new SQLException("Tàu " + maTau + " hiện không sẵn sàng: " + cur);
                }
            }

            // update chuyen tau status
//            tauDAO.capNhatTrangThai(conn, maTau, "Đang chạy");
            chuyenTauDAO.capNhatTrangThai(conn, maChuyen, "Đang chạy");
            // commit và log action (we don't change ChuyenTau.trangThai)
            conn.commit();
            writeActionLog(String.format("START|%s|%s|%s|OK", maChuyen, date.toString(), user));
        } catch (SQLException ex) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ignore) {}
            writeActionLog(String.format("START|%s|%s|%s|ERROR:%s", maChuyen, date.toString(), user, ex.getMessage()));
            throw ex;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(prevAuto); conn.close(); } catch (SQLException ignore) {}
            }
        }

    }

    /**
     * Đến nơi: giải phóng ghế cho các vé của chuyến vào ngày date, cập nhật trạng thái vé (tùy policy),
     * và cập nhật trạng thái tàu -> "Sẵn sàng".
     */
    public int arriveChuyenOnDate(String maChuyen, LocalDate date, boolean freePaidSeats, String user) throws SQLException {
        Connection conn = null;
        boolean prevAuto = true;
        int updatedSeats = 0;
        try {
            conn = ConnectSql.getInstance().getConnection();
            prevAuto = conn.getAutoCommit();
            conn.setAutoCommit(false);

            // Load chuyến
            ChuyenTau ct = chuyenTauDAO.findById(maChuyen);
            if (ct == null) throw new SQLException("Không tìm thấy chuyến: " + maChuyen);

            // Optional: verify the chuyến's gioDi matches the provided date (if your model stores per-instance date/time)
            if (ct.getGioDi() != null) {
                if (!ct.getGioDi().toLocalDate().equals(date)) {
                    // If you want to enforce strict match, uncomment the next line:
                    // throw new SQLException("Chuyến " + maChuyen + " không chạy vào ngày: " + date);
                    // Otherwise continue — we still filter tickets by date below.
                }
            }

            // Check current chuyến status: should be "Đang chạy" before arriving
            String curStatus = ct.getTrangThai() == null ? "" : ct.getTrangThai().trim();
            if (!"Đang chạy".equalsIgnoreCase(curStatus)) {
                throw new SQLException("Chuyến " + maChuyen + " hiện không ở trạng thái 'Đang chạy' (hiện: " + curStatus + ")");
            }

            // Get tickets for this chuyến on the given date (VeDAO must have getByChuyenAndDate(conn, maChuyen, date))
            List<Ve> danhSachVe = veDAO.getByChuyenAndDate(conn, maChuyen, date);
            if (danhSachVe == null) danhSachVe = java.util.Collections.emptyList();

            String updateGheSql = "UPDATE Ghe SET trangThai = ? WHERE maGhe = ?";
            String updateVeSql = "UPDATE Ve SET trangThai = ? WHERE maVe = ?";
            try (PreparedStatement pstGhe = conn.prepareStatement(updateGheSql);
                 PreparedStatement pstVe = conn.prepareStatement(updateVeSql)) {

                for (Ve v : danhSachVe) {
                    if (v == null) continue;
                    String trangThaiVe = v.getTrangThai() == null ? "" : v.getTrangThai().trim();

                    // If policy says don't free already paid seats, skip those tickets
                    if (!freePaidSeats && "Đã thanh toán".equalsIgnoreCase(trangThaiVe)) {
                        continue;
                    }

                    String maGhe = v.getMaSoGhe();
                    if (maGhe != null && !maGhe.trim().isEmpty()) {
                        pstGhe.setString(1, "Rảnh");
                        pstGhe.setString(2, maGhe);
                        pstGhe.addBatch();
                    }

                    pstVe.setString(1, "Đã kết thúc");
                    pstVe.setString(2, v.getMaVe());
                    pstVe.addBatch();

                    updatedSeats++;
                }

                // Execute batches (if any)
                try {
                    int[] resGhe = pstGhe.executeBatch();
                    int[] resVe = pstVe.executeBatch();
                    // Note: res arrays may contain Statement.SUCCESS_NO_INFO / execute counts.
                } catch (BatchUpdateException bue) {
                    // If a batch fails, rethrow as SQLException to trigger rollback and logging
                    throw new SQLException("Lỗi khi cập nhật ghế/vé (batch): " + bue.getMessage(), bue);
                }
            }

            // Update chuyến status to Hoàn tất
            chuyenTauDAO.capNhatTrangThai(conn, maChuyen, "Hoàn tất");

            conn.commit();
            writeActionLog(String.format("ARRIVE|%s|%s|%s|freePaid=%s|updatedSeats=%d|OK",
                    maChuyen, date.toString(), user, freePaidSeats, updatedSeats));
            return updatedSeats;
        } catch (SQLException ex) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ignore) {}
            writeActionLog(String.format("ARRIVE|%s|%s|%s|freePaid=%s|ERROR:%s",
                    maChuyen, date.toString(), user, freePaidSeats, ex.getMessage()));
            throw ex;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(prevAuto); conn.close(); } catch (SQLException ignore) {}
            }
        }
    }

    /**
     * Hủy chuyến vào ngày date (tùy policy: có thể thông báo/ gỡ vé / hoàn tiền).
     * Ở đây chỉ cập nhật trạng thái tàu về Sẵn sàng và ghi log; không thay đổi dữ liệu vé tự động.
     */

    private synchronized void writeActionLog(String line) {
        try (PrintWriter out = new PrintWriter(new FileWriter(actionLogFile, true))) {
            out.println(java.time.ZonedDateTime.now(ZoneId.systemDefault()) + "|" + line);
        } catch (Exception ignored) {
        }
    }

    public String getChuyenTauStatus(String maTau) {
        ChuyenTau CT = chuyenTauDAO.findById(maTau);
        if (CT == null) return "Không tìm thấy chuyến tàu";
        return CT.getTrangThai();
    }
}