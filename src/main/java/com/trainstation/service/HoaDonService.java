package com.trainstation.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.trainstation.MySQL.ConnectSql;
import com.trainstation.dao.*;
import com.trainstation.model.*;

import java.io.File;
import java.sql.*;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Service xử lý nghiệp vụ liên quan đến Hóa đơn
 */
public class HoaDonService {
    private static HoaDonService instance;
    private final HoaDonDAO hoaDonDAO;
    private final ChiTietHoaDonDAO chiTietHoaDonDAO;
    private final KhachHangDAO khachHangDAO;
    private final VeDAO veDAO;
    private final TinhGiaService tinhGia = TinhGiaService.getInstance();

    private HoaDonService() {
        this.hoaDonDAO = HoaDonDAO.getInstance();
        this.chiTietHoaDonDAO = ChiTietHoaDonDAO.getInstance();
        this.khachHangDAO = KhachHangDAO.getInstance();
        this.veDAO = VeDAO.getInstance();
    }

    public static synchronized HoaDonService getInstance() {
        if (instance == null) {
            instance = new HoaDonService();
        }
        return instance;
    }

    /**
     * Tạo hóa đơn mới
     */
    public HoaDon taoHoaDon(HoaDon hoaDon) {
        if (hoaDonDAO.insert(hoaDon)) {
            return hoaDon;
        }
        throw new RuntimeException("Không thể tạo hóa đơn");
    }

    /**
     * Cập nhật hóa đơn
     */
    public boolean capNhatHoaDon(HoaDon hoaDon) {
        return hoaDonDAO.update(hoaDon);
    }

    /**
     * Tìm hóa đơn theo mã
     */
    public HoaDon timHoaDonTheoMa(String maHoaDon) {
        return hoaDonDAO.findById(maHoaDon);
    }

    /**
     * Lấy tất cả hóa đơn
     */
    public List<HoaDon> layTatCaHoaDon() {
        return hoaDonDAO.getAll();
    }

    /**
     * Tính và cập nhật tổng tiền cho hóa đơn từ các chi tiết hóa đơn
     */
    public float capNhatTongTien(String maHoaDon) {
        List<ChiTietHoaDon> chiTietList = chiTietHoaDonDAO.getAll().stream()
                .filter(ct -> ct.getMaHoaDon().equals(maHoaDon))
                .toList();

        float tongTien = 0;
        for (ChiTietHoaDon ct : chiTietList) {
            tongTien += ct.getGiaDaKM();
        }

        return tongTien;
    }

    /**
     * Xuất hóa đơn ra file PDF
     */
    public String xuatHoaDonPDF(String maHoaDon) throws Exception {
        HoaDon hoaDon = hoaDonDAO.findById(maHoaDon);
        if (hoaDon == null) {
            throw new IllegalArgumentException("Không tìm thấy hóa đơn");
        }

        KhachHang khachHang = khachHangDAO.findById(hoaDon.getMaKH());
        List<ChiTietHoaDon> chiTietList = chiTietHoaDonDAO.getAll().stream()
                .filter(ct -> ct.getMaHoaDon().equals(maHoaDon))
                .toList();

        // Create invoices directory if not exists
        File invoicesDir = new File("invoices");
        if (!invoicesDir.exists()) {
            invoicesDir.mkdirs();
        }

        String fileName = "invoices/HoaDon_" + maHoaDon + ".pdf";
        PdfWriter writer = new PdfWriter(fileName);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        try {
            // Load Tinos font for Vietnamese support
            PdfFont font = PdfFontFactory.createFont("fonts/Tinos-Regular.ttf", PdfEncodings.IDENTITY_H,
                    PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
            document.setFont(font);

            // Tiêu đề công ty
            Paragraph companyTitle = new Paragraph("CÔNG TY CỔ PHẦN VẬN TẢI ĐƯỜNG SẮT SÀI GÒN")
                    .setFont(font)
                    .setFontSize(14)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(companyTitle);

            // Tiêu đề hóa đơn
            Paragraph invoiceTitle = new Paragraph("HÓA ĐƠN BÁN VÉ TÀU HỎA")
                    .setFont(font)
                    .setFontSize(14)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(invoiceTitle);

            document.add(new Paragraph("\n").setFont(font));

            // Thông tin hóa đơn
            document.add(new Paragraph("Mã hóa đơn: " + hoaDon.getMaHoaDon())
                    .setFont(font)
                    .setFontSize(11));
            document.add(new Paragraph("Khách hàng: " +
                    (khachHang != null ? khachHang.getTenKhachHang() : "N/A"))
                    .setFont(font)
                    .setFontSize(11));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            document.add(new Paragraph("Ngày lập: " +
                    (hoaDon.getNgayLap() != null ? hoaDon.getNgayLap().format(formatter) : "N/A"))
                    .setFont(font)
                    .setFontSize(11));
            document.add(new Paragraph("Phương thức thanh toán: " +
                    (hoaDon.getPhuongThucThanhToan() != null ? hoaDon.getPhuongThucThanhToan() : "N/A"))
                    .setFont(font)
                    .setFontSize(11));

            document.add(new Paragraph("\n").setFont(font));

            // Bảng vé
            Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2, 2, 2, 2, 2}));
            table.setWidth(UnitValue.createPercentValue(100));

            // Header - in đậm và căn giữa
            table.addHeaderCell(new Cell().add(new Paragraph("STT").setFont(font).setBold())
                    .setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Mã vé").setFont(font).setBold())
                    .setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Ga đi").setFont(font).setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Ga đến").setFont(font).setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Ngày đi").setFont(font).setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Giá vé").setFont(font).setBold()));

            // Data rows
            int stt = 1;
            float tongTien = 0;
            NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (ChiTietHoaDon ct : chiTietList) {
                Ve ve = veDAO.findById(ct.getMaVe());

                // STT - căn giữa
                table.addCell(new Cell().add(new Paragraph(String.valueOf(stt++)).setFont(font))
                        .setTextAlignment(TextAlignment.CENTER));

                // Mã vé - căn giữa
                table.addCell(new Cell().add(new Paragraph(ct.getMaVe()).setFont(font))
                        .setTextAlignment(TextAlignment.CENTER));

                // Ga đi
                table.addCell(new Cell().add(new Paragraph(ve != null ? ve.getGaDi() : "N/A").setFont(font)));

                // Ga đến
                table.addCell(new Cell().add(new Paragraph(ve != null ? ve.getGaDen() : "N/A").setFont(font)));

                // Ngày đi
                table.addCell(new Cell().add(new Paragraph(
                        ve != null && ve.getGioDi() != null ? ve.getGioDi().format(dateFormatter) : "N/A")
                        .setFont(font)));

                // Giá vé - format với dấu phẩy
                table.addCell(new Cell().add(new Paragraph(currencyFormat.format(ct.getGiaDaKM()) + " VNĐ")
                        .setFont(font)));

                tongTien += ct.getGiaDaKM();
            }

            document.add(table);

            document.add(new Paragraph("\n").setFont(font));

            // Tổng tiền - căn phải
            Paragraph totalParagraph = new Paragraph("Tổng tiền: " + currencyFormat.format(tongTien) + " VNĐ")
                    .setFont(font)
                    .setFontSize(11)
                    .setBold()
                    .setTextAlignment(TextAlignment.RIGHT);
            document.add(totalParagraph);

            // Trạng thái - căn phải
            String trangThai = hoaDon.getTrangThai() != null ? hoaDon.getTrangThai() : "Hoàn tất";
            Paragraph statusParagraph = new Paragraph("Trạng thái: " + trangThai + " - Cảm ơn quý khách đã sử dụng dịch vụ!")
                    .setFont(font)
                    .setFontSize(11)
                    .setTextAlignment(TextAlignment.RIGHT);
            document.add(statusParagraph);

        } finally {
            document.close();
        }

        return fileName;
    }

    // Helper: kiểm tra Ve tồn tại trên connection (không dùng DAO để tránh khác connection)
    private boolean veExistsOnConnection(String maVe, Connection conn) throws SQLException {
        String sql = "SELECT 1 FROM Ve WHERE maVe = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maVe);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Helper: kiểm tra ChiTietHoaDon tồn tại trên connection hiện tại
    private boolean cthdExistsOnConnection(String maHoaDon, String maVe, Connection conn) throws SQLException {
        String sql = "SELECT 1 FROM ChiTietHoaDon WHERE maHoaDon = ? AND maVe = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maHoaDon);
            pst.setString(2, maVe);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Checkout: bán một danh sách vé và tạo hoá đơn.
     * Thực hiện tất cả trên 1 Connection/transaction để tránh lỗi FK và đảm bảo atomicity.
     */
    public boolean checkout(HoaDon hoaDon, List<Ve> dsVe, String phuongThucThanhToan) {
        Connection connection = null;
        boolean originalAutoCommit = true;
        try {
            connection = ConnectSql.getInstance().getConnection();
            originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            // 1. Thiết lập thông tin hoá đơn (cập nhật đối tượng)
            hoaDon.setPhuongThucThanhToan(phuongThucThanhToan);
            hoaDon.setNgayLap(LocalDateTime.now());
            hoaDon.setTrangThai("Hoàn tất");

            // Insert hoặc update HoaDon trên cùng connection
            boolean existsHd = false;
            try (PreparedStatement pstCheckHd = connection.prepareStatement("SELECT 1 FROM HoaDon WHERE maHoaDon = ?")) {
                pstCheckHd.setString(1, hoaDon.getMaHoaDon());
                try (ResultSet rs = pstCheckHd.executeQuery()) {
                    existsHd = rs.next();
                }
            }

            if (!existsHd) {
                hoaDonDAO.insert(hoaDon, connection);
            } else {
                // update HoaDon using connection (we'll run an update statement here)
                String updateHdSql = "UPDATE HoaDon SET maNV = ?, maKH = ?, ngayLap = ?, phuongThucThanhToan = ?, trangThai = ? WHERE maHoaDon = ?";
                try (PreparedStatement pst = connection.prepareStatement(updateHdSql)) {
                    pst.setString(1, hoaDon.getMaNV());
                    pst.setString(2, hoaDon.getMaKH());
                    if (hoaDon.getNgayLap() != null) pst.setTimestamp(3, Timestamp.valueOf(hoaDon.getNgayLap()));
                    else pst.setNull(3, Types.TIMESTAMP);
                    pst.setString(4, hoaDon.getPhuongThucThanhToan());
                    pst.setString(5, hoaDon.getTrangThai());
                    pst.setString(6, hoaDon.getMaHoaDon());
                    pst.executeUpdate();
                }
            }

            // Prepared SQL for repeated use
            String checkVeSql = "SELECT 1 FROM Ve WHERE maVe = ?";
            String insertVeSql = "INSERT INTO Ve (maVe, maChuyen, maLoaiVe, maSoGhe, ngayIn, trangThai, gaDi, gaDen, gioDi, soToa, loaiCho, loaiVe) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            String checkCTHD = "SELECT 1 FROM ChiTietHoaDon WHERE maHoaDon = ? AND maVe = ?";
            String insertCTHDsql = "INSERT INTO ChiTietHoaDon (maHoaDon, maVe, maLoaiVe, giaGoc, giaDaKM, moTa) VALUES (?, ?, ?, ?, ?, ?)";
            String updateCTHDsql = "UPDATE ChiTietHoaDon SET maLoaiVe = ?, giaGoc = ?, giaDaKM = ?, moTa = ? WHERE maHoaDon = ? AND maVe = ?";
            String updateVeSql = "UPDATE Ve SET trangThai = ? WHERE maVe = ?";
            String updateGheSql = "UPDATE Ghe SET trangThai = ? WHERE maGhe = ?";

            try (PreparedStatement pstCheckVe = connection.prepareStatement(checkVeSql);
                 PreparedStatement pstInsertVe = connection.prepareStatement(insertVeSql);
                 PreparedStatement pstCheckCT = connection.prepareStatement(checkCTHD);
                 PreparedStatement pstInsertCT = connection.prepareStatement(insertCTHDsql);
                 PreparedStatement pstUpdateCT = connection.prepareStatement(updateCTHDsql);
                 PreparedStatement pstUpdateVe = connection.prepareStatement(updateVeSql);
                 PreparedStatement pstUpdateGhe = connection.prepareStatement(updateGheSql)) {

                for (Ve ve : dsVe) {
                    // 1) Nếu Ve chưa tồn tại trên DB thì insert Ve (parent) TRÊN CÙNG CONNECTION
                    pstCheckVe.setString(1, ve.getMaVe());
                    boolean existsVe;
                    try (ResultSet rs = pstCheckVe.executeQuery()) {
                        existsVe = rs.next();
                    }
                    if (!existsVe) {
                        // Use prepared statement to insert ve (keeps all work on the same connection)
                        pstInsertVe.setString(1, ve.getMaVe());
                        pstInsertVe.setString(2, ve.getMaChuyen());
                        pstInsertVe.setString(3, ve.getMaLoaiVe());
                        pstInsertVe.setString(4, ve.getMaSoGhe());
                        if (ve.getNgayIn() != null) pstInsertVe.setTimestamp(5, Timestamp.valueOf(ve.getNgayIn()));
                        else pstInsertVe.setNull(5, Types.TIMESTAMP);
                        pstInsertVe.setString(6, ve.getTrangThai());
                        pstInsertVe.setString(7, ve.getGaDi());
                        pstInsertVe.setString(8, ve.getGaDen());
                        if (ve.getGioDi() != null) pstInsertVe.setTimestamp(9, Timestamp.valueOf(ve.getGioDi()));
                        else pstInsertVe.setNull(9, Types.TIMESTAMP);
                        pstInsertVe.setString(10, ve.getSoToa());
                        pstInsertVe.setString(11, ve.getLoaiCho());
                        pstInsertVe.setString(12, ve.getLoaiVe());
                        pstInsertVe.executeUpdate();
                    }

                    // 2) Tính giá (giaGoc lấy từ BangGia, giaDaKM = round(giaCoBan * heSo))
                    TinhGiaService.KetQuaGia kq = tinhGia.tinhGiaChoVe(ve);

                    // 3) Kiểm tra ChiTietHoaDon tồn tại (trên cùng connection)
                    pstCheckCT.setString(1, hoaDon.getMaHoaDon());
                    pstCheckCT.setString(2, ve.getMaVe());
                    boolean existsCT = false;
                    try (ResultSet rs = pstCheckCT.executeQuery()) {
                        existsCT = rs.next();
                    }

                    if (existsCT) {
                        // update snapshot nếu cần
                        pstUpdateCT.setString(1, ve.getMaLoaiVe());
                        pstUpdateCT.setFloat(2, kq.giaGoc);
                        pstUpdateCT.setFloat(3, kq.giaDaKM);
                        pstUpdateCT.setString(4, kq.ghiChu);
                        pstUpdateCT.setString(5, hoaDon.getMaHoaDon());
                        pstUpdateCT.setString(6, ve.getMaVe());
                        pstUpdateCT.executeUpdate();
                    } else {
                        // insert ChiTietHoaDon (child)
                        pstInsertCT.setString(1, hoaDon.getMaHoaDon());
                        pstInsertCT.setString(2, ve.getMaVe());
                        pstInsertCT.setString(3, ve.getMaLoaiVe());
                        pstInsertCT.setFloat(4, kq.giaGoc);
                        pstInsertCT.setFloat(5, kq.giaDaKM);
                        pstInsertCT.setString(6, kq.ghiChu);
                        pstInsertCT.executeUpdate();
                    }

                    // 4) Update Ve.trangThai = 'Đã thanh toán'
                    pstUpdateVe.setString(1, "Đã thanh toán");
                    pstUpdateVe.setString(2, ve.getMaVe());
                    pstUpdateVe.executeUpdate();

                    // 5) Update Ghe.trangThai = 'Đã đặt' (nếu có bảng Ghe)
                    if (ve.getMaSoGhe() != null) {
                        pstUpdateGhe.setString(1, "Đã đặt");
                        pstUpdateGhe.setString(2, ve.getMaSoGhe());
                        try {
                            pstUpdateGhe.executeUpdate();
                        } catch (SQLException ignore) {
                            // Nếu không có bảng Ghe hoặc cột, bỏ qua
                        }
                    }
                }
            }

            connection.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (connection != null) {
                try { connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (connection != null) {
                try { connection.setAutoCommit(originalAutoCommit); } catch (SQLException ignored) {}
                try { connection.close(); } catch (SQLException ignored) {}
            }
        }
    }

    /**
     * Thêm vé vào hóa đơn (server-side reserve). Phương thức này sẽ thực hiện
     * insert Ve + ChiTietHoaDon + update Ghe trong một transaction trên một Connection.
     *
     * Nếu bạn đang giữ vé tạm client-side thì không cần gọi phương thức này —
     * chỉ gọi checkout(...) khi thanh toán.
     */
    public boolean addToHoaDon(HoaDon hoaDon, Ve ve, LoaiVe loaiVe) {
        Objects.requireNonNull(hoaDon, "hoaDon cannot be null");
        Objects.requireNonNull(ve, "ve cannot be null");

        Connection connection = null;
        boolean originalAutoCommit = true;
        try {
            connection = ConnectSql.getInstance().getConnection();
            originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            // 1) Kiểm tra trạng thái ghế trên cùng connection
            String selectGheSql = "SELECT trangThai FROM Ghe WHERE maGhe = ?";
            try (PreparedStatement pstGhe = connection.prepareStatement(selectGheSql)) {
                pstGhe.setString(1, ve.getMaSoGhe());
                try (ResultSet rs = pstGhe.executeQuery()) {
                    if (rs.next()) {
                        String trangThai = rs.getString("trangThai");
                        if (trangThai != null && !"Rảnh".equalsIgnoreCase(trangThai) && !"RANH".equalsIgnoreCase(trangThai)) {
                            // Ghế không rảnh
                            connection.rollback();
                            return false;
                        }
                    } else {
                        // Không tìm thấy ghế
                        connection.rollback();
                        return false;
                    }
                }
            }

            // 2) Ensure HoaDon exists (insert nếu chưa) on this connection
            boolean existsHd;
            try (PreparedStatement pstCheckHd = connection.prepareStatement("SELECT 1 FROM HoaDon WHERE maHoaDon = ?")) {
                pstCheckHd.setString(1, hoaDon.getMaHoaDon());
                try (ResultSet rs = pstCheckHd.executeQuery()) {
                    existsHd = rs.next();
                }
            }
            if (!existsHd) {
                hoaDonDAO.insert(hoaDon, connection);
            }

            // 3) Ensure Ve exists (insert nếu chưa) on this connection
            if (!veExistsOnConnection(ve.getMaVe(), connection)) {
                // insert ve
                String insertVeSql = "INSERT INTO Ve (maVe, maChuyen, maLoaiVe, maSoGhe, ngayIn, trangThai, gaDi, gaDen, gioDi, soToa, loaiCho, loaiVe) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pst = connection.prepareStatement(insertVeSql)) {
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
                    pst.executeUpdate();
                }
            }

            // 4) Tính giá
            if (loaiVe != null) {
                ve.setMaLoaiVe(loaiVe.getMaLoaiVe());
            }
            TinhGiaService.KetQuaGia kq = tinhGia.tinhGiaChoVe(ve);

            // 5) Chèn ChiTietHoaDon nếu chưa tồn tại
            if (chiTietHoaDonDAO.exists(hoaDon.getMaHoaDon(), ve.getMaVe(), connection) || cthdExistsOnConnection(hoaDon.getMaHoaDon(), ve.getMaVe(), connection)) {
                // đã tồn tại -> rollback và báo false (tuỳ nghiệp vụ bạn có thể UPDATE thay vì fail)
                connection.rollback();
                return false;
            } else {
                ChiTietHoaDon cthd = new ChiTietHoaDon();
                cthd.setMaHoaDon(hoaDon.getMaHoaDon());
                cthd.setMaVe(ve.getMaVe());
                cthd.setMaLoaiVe(ve.getMaLoaiVe());
                cthd.setGiaGoc(kq.giaGoc);
                cthd.setGiaDaKM(kq.giaDaKM);
                cthd.setMoTa(kq.ghiChu);
                chiTietHoaDonDAO.insert(cthd, connection);
            }

            // 6) Đánh dấu ghế tạm trong DB là "Đã giữ" (server-side reserve)
            try (PreparedStatement pstUpdGhe = connection.prepareStatement("UPDATE Ghe SET trangThai = ? WHERE maGhe = ?")) {
                pstUpdGhe.setString(1, "Đã giữ");
                pstUpdGhe.setString(2, ve.getMaSoGhe());
                pstUpdGhe.executeUpdate();
            } catch (SQLException ignore) {
                // nếu không có bảng Ghe hoặc cột, bỏ qua
            }

            connection.commit();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            if (connection != null) {
                try { connection.rollback(); } catch (SQLException ignore) {}
            }
            return false;
        } finally {
            if (connection != null) {
                try { connection.setAutoCommit(originalAutoCommit); } catch (SQLException ignored) {}
                try { connection.close(); } catch (SQLException ignored) {}
            }
        }
    }
}