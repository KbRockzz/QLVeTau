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
     *
     * @param maChuyen chuyến mẫu
     * @param date     ngày chạy (LocalDate)
     * @param user     người thực hiện (cho log)
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

            // Kiểm tra trạng thái tàu
            Tau tau = tauDAO.findById(maTau);
            if (tau != null) {
                String cur = tau.getTrangThai() == null ? "" : tau.getTrangThai().trim();
                // policy: chỉ cho start nếu tàu đang "Sẵn sàng" hoặc rảnh
                if (!cur.isEmpty() && !cur.equalsIgnoreCase("Sẵn sàng") && !cur.equalsIgnoreCase("Rảnh")) {
                    throw new SQLException("Tàu " + maTau + " hiện không sẵn sàng: " + cur);
                }
            }

            // update tau status
            tauDAO.capNhatTrangThai(conn, maTau, "Đang chạy");

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
     *
     * @param maChuyen     mã chuyến template
     * @param date         ngày của lần chạy
     * @param freePaidSeats nếu true giải phóng cả ghế của vé đã thanh toán
     * @param user         người thực hiện (cho log)
     * @return số ghế được thay đổi
     * @throws SQLException khi lỗi DB
     */
    public int arriveChuyenOnDate(String maChuyen, LocalDate date, boolean freePaidSeats, String user) throws SQLException {
        Connection conn = null;
        boolean prevAuto = true;
        int updatedSeats = 0;
        try {
            conn = ConnectSql.getInstance().getConnection();
            prevAuto = conn.getAutoCommit();
            conn.setAutoCommit(false);

            ChuyenTau ct = chuyenTauDAO.findById(maChuyen);
            if (ct == null) throw new SQLException("Không tìm thấy chuyến: " + maChuyen);
            String maTau = ct.getMaTau();

            // Lấy danh sách vé của chuyến vào ngày date - VeDAO cần có getByChuyenAndDate(conn,...)
            List<Ve> danhSachVe = veDAO.getByChuyenAndDate(conn, maChuyen, date);

            // Cập nhật ghế & vé
            String updateGheSql = "UPDATE Ghe SET trangThai = ? WHERE maGhe = ?";
            String updateVeSql = "UPDATE Ve SET trangThai = ? WHERE maVe = ?";
            try (PreparedStatement pstGhe = conn.prepareStatement(updateGheSql);
                 PreparedStatement pstVe = conn.prepareStatement(updateVeSql)) {

                for (Ve v : danhSachVe) {
                    String trangThaiVe = v.getTrangThai() == null ? "" : v.getTrangThai().trim();

                    if (!freePaidSeats && trangThaiVe.equalsIgnoreCase("Đã thanh toán")) {
                        // skip paid tickets if policy says so
                        continue;
                    }

                    String maGhe = v.getMaSoGhe();
                    if (maGhe != null && !maGhe.trim().isEmpty()) {
                        pstGhe.setString(1, "Trống");
                        pstGhe.setString(2, maGhe);
                        pstGhe.addBatch();
                    }

                    pstVe.setString(1, "Đã kết thúc");
                    pstVe.setString(2, v.getMaVe());
                    pstVe.addBatch();

                    updatedSeats++;
                }

                pstGhe.executeBatch();
                pstVe.executeBatch();
            }

            // cập nhật trạng thái tàu về "Sẵn sàng"
            tauDAO.capNhatTrangThai(conn, maTau, "Sẵn sàng");

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
    public void cancelChuyenOnDate(String maChuyen, LocalDate date, String user) throws SQLException {
        Connection conn = null;
        boolean prevAuto = true;
        try {
            conn = ConnectSql.getInstance().getConnection();
            prevAuto = conn.getAutoCommit();
            conn.setAutoCommit(false);

            ChuyenTau ct = chuyenTauDAO.findById(maChuyen);
            if (ct == null) throw new SQLException("Không tìm thấy chuyến: " + maChuyen);
            String maTau = ct.getMaTau();

            // policy: set train to "Sẵn sàng"
            tauDAO.capNhatTrangThai(conn, maTau, "Sẵn sàng");

            conn.commit();
            writeActionLog(String.format("CANCEL|%s|%s|%s|OK", maChuyen, date.toString(), user));
        } catch (SQLException ex) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ignore) {}
            writeActionLog(String.format("CANCEL|%s|%s|%s|ERROR:%s", maChuyen, date.toString(), user, ex.getMessage()));
            throw ex;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(prevAuto); conn.close(); } catch (SQLException ignore) {}
            }
        }
    }

    private synchronized void writeActionLog(String line) {
        try (PrintWriter out = new PrintWriter(new FileWriter(actionLogFile, true))) {
            out.println(java.time.ZonedDateTime.now(ZoneId.systemDefault()) + "|" + line);
        } catch (Exception ignored) {
        }
    }
}