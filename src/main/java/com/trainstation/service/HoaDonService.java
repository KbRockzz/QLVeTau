package com.trainstation.service;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
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
 *
 * Phiên bản này sử dụng TinhGiaService.tinhGiaChoVe(ve) (trả về KetQuaGia.maBangGia và giaDaKM)
 * và đảm bảo khi persist Vé (bảng Ve) sẽ chỉ lưu maBangGia (không lưu donGia vào Ve vì model Ve của bạn không có trường donGia).
 *
 * Giá cuối cùng (giaDaKM) và giaGoc vẫn được lưu như snapshot trong ChiTietHoaDon.
 *
 * Tất cả thao tác insert/update Ve và ChiTietHoaDon được thực hiện trên cùng 1 Connection/transaction.
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
        if (instance == null) instance = new HoaDonService();
        return instance;
    }

    public HoaDon taoHoaDon(HoaDon hoaDon) {
        if (hoaDonDAO.insert(hoaDon)) return hoaDon;
        throw new RuntimeException("Không thể tạo hóa đơn");
    }

    public boolean capNhatHoaDon(HoaDon hoaDon) {
        return hoaDonDAO.update(hoaDon);
    }

    public HoaDon timHoaDonTheoMa(String maHoaDon) {
        return hoaDonDAO.findById(maHoaDon);
    }

    public List<HoaDon> layTatCaHoaDon() {
        return hoaDonDAO.getAll();
    }

    public float capNhatTongTien(String maHoaDon) {
        List<ChiTietHoaDon> chiTietList = chiTietHoaDonDAO.getAll().stream()
                .filter(ct -> ct.getMaHoaDon().equals(maHoaDon))
                .toList();

        float tongTien = 0;
        for (ChiTietHoaDon ct : chiTietList) tongTien += ct.getGiaDaKM();
        return tongTien;
    }

    public String xuatHoaDonPDF(String maHoaDon) throws Exception {
        HoaDon hoaDon = hoaDonDAO.findById(maHoaDon);
        if (hoaDon == null) throw new IllegalArgumentException("Không tìm thấy hóa đơn");

        KhachHang khachHang = khachHangDAO.findById(hoaDon.getMaKH());
        List<ChiTietHoaDon> chiTietList = chiTietHoaDonDAO.getAll().stream()
                .filter(ct -> ct.getMaHoaDon().equals(maHoaDon))
                .toList();

        File invoicesDir = new File("invoices");
        if (!invoicesDir.exists()) invoicesDir.mkdirs();

        String fileName = "invoices/HoaDon_" + maHoaDon + ".pdf";
        PdfWriter writer = new PdfWriter(fileName);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        try {
            PdfFont font = PdfFontFactory.createFont("fonts/Tinos-Regular.ttf", PdfEncodings.IDENTITY_H,
                    PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
            document.setFont(font);

            Paragraph companyTitle = new Paragraph("CÔNG TY CỔ PHẦN VẬN TẢI ĐƯỜNG SẮT SÀI GÒN")
                    .setFont(font).setFontSize(14).setBold().setTextAlignment(TextAlignment.CENTER);
            document.add(companyTitle);

            Paragraph invoiceTitle = new Paragraph("HÓA ĐƠN BÁN VÉ TÀU HỎA")
                    .setFont(font).setFontSize(14).setBold().setTextAlignment(TextAlignment.CENTER);
            document.add(invoiceTitle);

            document.add(new Paragraph("\n").setFont(font));

            document.add(new Paragraph("Mã hóa đơn: " + hoaDon.getMaHoaDon()).setFont(font).setFontSize(11));
            document.add(new Paragraph("Khách hàng: " + (khachHang != null ? khachHang.getTenKhachHang() : "N/A"))
                    .setFont(font).setFontSize(11));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            document.add(new Paragraph("Ngày lập: " +
                    (hoaDon.getNgayLap() != null ? hoaDon.getNgayLap().format(formatter) : "N/A"))
                    .setFont(font).setFontSize(11));
            document.add(new Paragraph("Phương thức thanh toán: " +
                    (hoaDon.getPhuongThucThanhToan() != null ? hoaDon.getPhuongThucThanhToan() : "N/A"))
                    .setFont(font).setFontSize(11));

            document.add(new Paragraph("\n").setFont(font));

            Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2, 2, 2, 2, 2}));
            table.setWidth(UnitValue.createPercentValue(100));
            table.addHeaderCell(new Cell().add(new Paragraph("STT").setFont(font).setBold()).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Mã vé").setFont(font).setBold()).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Ga đi").setFont(font).setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Ga đến").setFont(font).setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Ngày đi").setFont(font).setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Giá vé").setFont(font).setBold()));

            int stt = 1;
            float tongTien = 0;
            NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (ChiTietHoaDon ct : chiTietList) {
                Ve ve = veDAO.findById(ct.getMaVe());

                table.addCell(new Cell().add(new Paragraph(String.valueOf(stt++)).setFont(font)).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(ct.getMaVe()).setFont(font)).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(ve != null ? ve.getTenGaDi() : "N/A").setFont(font)));
                table.addCell(new Cell().add(new Paragraph(ve != null ? ve.getTenGaDen() : "N/A").setFont(font)));
                table.addCell(new Cell().add(new Paragraph(ve != null && ve.getGioDi() != null ? ve.getGioDi().format(dateFormatter) : "N/A").setFont(font)));
                table.addCell(new Cell().add(new Paragraph(currencyFormat.format(ct.getGiaDaKM()) + " VNĐ").setFont(font)));

                tongTien += ct.getGiaDaKM();
            }

            document.add(table);
            document.add(new Paragraph("\n").setFont(font));

            Paragraph totalParagraph = new Paragraph("Tổng tiền: " + currencyFormat.format(tongTien) + " VNĐ")
                    .setFont(font).setFontSize(11).setBold().setTextAlignment(TextAlignment.RIGHT);
            document.add(totalParagraph);

            String trangThai = hoaDon.getTrangThai() != null ? hoaDon.getTrangThai() : "Hoàn tất";
            Paragraph statusParagraph = new Paragraph("Trạng thái: " + trangThai + " - Cảm ơn quý khách đã sử dụng dịch vụ!")
                    .setFont(font).setFontSize(11).setTextAlignment(TextAlignment.RIGHT);
            document.add(statusParagraph);

        } finally {
            document.close();
        }

        return fileName;
    }

    // Helper methods reused from original implementation
    private boolean veExistsOnConnection(String maVe, Connection conn) throws SQLException {
        String sql = "SELECT 1 FROM Ve WHERE maVe = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maVe);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        }
    }

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
     * Toàn bộ thao tác được thực hiện trên cùng một transaction.
     *
     * Lưu ý: model Ve KHÔNG có trường donGia; donGia chỉ được lưu trong ChiTietHoaDon (snapshot).
     * Vì vậy khi persist vào bảng Ve chúng ta chỉ lưu maBangGia (nếu có).
     */
    public boolean checkout(HoaDon hoaDon, List<Ve> dsVe, String phuongThucThanhToan) {
        Connection connection = null;
        boolean originalAutoCommit = true;
        try {
            connection = ConnectSql.getInstance().getConnection();
            originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            hoaDon.setPhuongThucThanhToan(phuongThucThanhToan);
            hoaDon.setNgayLap(LocalDateTime.now());
            hoaDon.setTrangThai("Hoàn tất");

            // Insert or update HoaDon on same connection
            boolean existsHd = false;
            try (PreparedStatement pstCheckHd = connection.prepareStatement("SELECT 1 FROM HoaDon WHERE maHoaDon = ?")) {
                pstCheckHd.setString(1, hoaDon.getMaHoaDon());
                try (ResultSet rs = pstCheckHd.executeQuery()) { existsHd = rs.next(); }
            }

            if (!existsHd) {
                hoaDonDAO.insert(hoaDon, connection);
            } else {
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

            // Prepared statements
            String checkVeSql = "SELECT 1 FROM Ve WHERE maVe = ?";
            // INSERT Ve: do model Ve không có donGia, chỉ thêm maBangGia
            String insertVeSql = "INSERT INTO Ve (maVe, maChuyen, maLoaiVe, maSoGhe, ngayIn, trangThai, gaDi, gaDen, gioDi, soToa, loaiCho, loaiVe, maBangGia) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            String updateVePriceSql = "UPDATE Ve SET maBangGia = ? WHERE maVe = ?";
            String checkCTHD = "SELECT 1 FROM ChiTietHoaDon WHERE maHoaDon = ? AND maVe = ?";
            String insertCTHDsql = "INSERT INTO ChiTietHoaDon (maHoaDon, maVe, maLoaiVe, giaGoc, giaDaKM, moTa) VALUES (?, ?, ?, ?, ?, ?)";
            String updateCTHDsql = "UPDATE ChiTietHoaDon SET maLoaiVe = ?, giaGoc = ?, giaDaKM = ?, moTa = ? WHERE maHoaDon = ? AND maVe = ?";
            String updateVeSql = "UPDATE Ve SET trangThai = ? WHERE maVe = ?";
            String updateGheSql = "UPDATE Ghe SET trangThai = ? WHERE maGhe = ?";

            try (PreparedStatement pstCheckVe = connection.prepareStatement(checkVeSql);
                 PreparedStatement pstInsertVe = connection.prepareStatement(insertVeSql);
                 PreparedStatement pstUpdateVePrice = connection.prepareStatement(updateVePriceSql);
                 PreparedStatement pstCheckCT = connection.prepareStatement(checkCTHD);
                 PreparedStatement pstInsertCT = connection.prepareStatement(insertCTHDsql);
                 PreparedStatement pstUpdateCT = connection.prepareStatement(updateCTHDsql);
                 PreparedStatement pstUpdateVe = connection.prepareStatement(updateVeSql);
                 PreparedStatement pstUpdateGhe = connection.prepareStatement(updateGheSql)) {

                for (Ve ve : dsVe) {
                    // compute price result (may return applied maBangGia)
                    TinhGiaService.KetQuaGia kq = tinhGia.tinhGiaChoVe(ve);

                    // insert Ve if not exists, persisting maBangGia only
                    pstCheckVe.setString(1, ve.getMaVe());
                    boolean existsVe;
                    try (ResultSet rs = pstCheckVe.executeQuery()) { existsVe = rs.next(); }

                    String maBangGiaToPersist = ve.getMaBangGia();
                    if ((maBangGiaToPersist == null || maBangGiaToPersist.trim().isEmpty()) && kq != null) {
                        maBangGiaToPersist = kq.maBangGia;
                    }

                    if (!existsVe) {
                        pstInsertVe.setString(1, ve.getMaVe());
                        pstInsertVe.setString(2, ve.getMaChuyen());
                        pstInsertVe.setString(3, ve.getMaLoaiVe());
                        pstInsertVe.setString(4, ve.getMaSoGhe());
                        if (ve.getNgayIn() != null) pstInsertVe.setTimestamp(5, Timestamp.valueOf(ve.getNgayIn()));
                        else pstInsertVe.setNull(5, Types.TIMESTAMP);
                        pstInsertVe.setString(6, ve.getTrangThai());
                        pstInsertVe.setString(7, ve.getTenGaDi());
                        pstInsertVe.setString(8, ve.getTenGaDen());
                        if (ve.getGioDi() != null) pstInsertVe.setTimestamp(9, Timestamp.valueOf(ve.getGioDi()));
                        else pstInsertVe.setNull(9, Types.TIMESTAMP);
                        pstInsertVe.setString(10, ve.getSoToa());
                        pstInsertVe.setString(11, ve.getLoaiCho());
                        pstInsertVe.setString(12, ve.getLoaiVe());
                        if (maBangGiaToPersist != null) pstInsertVe.setString(13, maBangGiaToPersist); else pstInsertVe.setNull(13, Types.VARCHAR);

                        pstInsertVe.executeUpdate();
                    } else {
                        // update only maBangGia on existing Ve row
                        if (maBangGiaToPersist == null) pstUpdateVePrice.setNull(1, Types.VARCHAR); else pstUpdateVePrice.setString(1, maBangGiaToPersist);
                        pstUpdateVePrice.setString(2, ve.getMaVe());
                        pstUpdateVePrice.executeUpdate();
                    }

                    // Ensure Ve object carries persisted maBangGia for downstream usage
                    if ((ve.getMaBangGia() == null || ve.getMaBangGia().trim().isEmpty()) && maBangGiaToPersist != null) {
                        ve.setMaBangGia(maBangGiaToPersist);
                    }

                    // Insert or update ChiTietHoaDon snapshot (giaGoc, giaDaKM come from kq)
                    pstCheckCT.setString(1, hoaDon.getMaHoaDon());
                    pstCheckCT.setString(2, ve.getMaVe());
                    boolean existsCT;
                    try (ResultSet rs = pstCheckCT.executeQuery()) { existsCT = rs.next(); }

                    if (existsCT) {
                        pstUpdateCT.setString(1, ve.getMaLoaiVe());
                        pstUpdateCT.setFloat(2, kq.giaGoc);
                        pstUpdateCT.setFloat(3, kq.giaDaKM);
                        pstUpdateCT.setString(4, kq.ghiChu);
                        pstUpdateCT.setString(5, hoaDon.getMaHoaDon());
                        pstUpdateCT.setString(6, ve.getMaVe());
                        pstUpdateCT.executeUpdate();
                    } else {
                        pstInsertCT.setString(1, hoaDon.getMaHoaDon());
                        pstInsertCT.setString(2, ve.getMaVe());
                        pstInsertCT.setString(3, ve.getMaLoaiVe());
                        pstInsertCT.setFloat(4, kq.giaGoc);
                        pstInsertCT.setFloat(5, kq.giaDaKM);
                        pstInsertCT.setString(6, kq.ghiChu);
                        pstInsertCT.executeUpdate();
                    }

                    // Update Ve.trangThai = 'Đã thanh toán'
                    pstUpdateVe.setString(1, "Đã thanh toán");
                    pstUpdateVe.setString(2, ve.getMaVe());
                    pstUpdateVe.executeUpdate();

                    // Update Ghe.trangThai = 'Đã đặt' (nếu có)
                    if (ve.getMaSoGhe() != null) {
                        pstUpdateGhe.setString(1, "Đã đặt");
                        pstUpdateGhe.setString(2, ve.getMaSoGhe());
                        try { pstUpdateGhe.executeUpdate(); } catch (SQLException ignore) {}
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
     * Vì model Ve không có trường donGia, khi persist Ve chỉ lưu maBangGia (nếu có).
     * Giá chi tiết (giaGoc/giaDaKM) vẫn được lưu trong ChiTietHoaDon.
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
                            connection.rollback();
                            return false;
                        }
                    } else {
                        connection.rollback();
                        return false;
                    }
                }
            }

            // 2) Ensure HoaDon exists on this connection
            boolean existsHd;
            try (PreparedStatement pstCheckHd = connection.prepareStatement("SELECT 1 FROM HoaDon WHERE maHoaDon = ?")) {
                pstCheckHd.setString(1, hoaDon.getMaHoaDon());
                try (ResultSet rs = pstCheckHd.executeQuery()) {
                    existsHd = rs.next();
                }
            }
            if (!existsHd) hoaDonDAO.insert(hoaDon, connection);

            // 3) Ensure Ve exists (insert nếu chưa) and persist maBangGia using tinhGia
            if (!veExistsOnConnection(ve.getMaVe(), connection)) {
                TinhGiaService.KetQuaGia kq = tinhGia.tinhGiaChoVe(ve);

                String insertVeSql = "INSERT INTO Ve (maVe, maChuyen, maLoaiVe, maSoGhe, ngayIn, trangThai,tenGaDi, tenGaDen, gaDi, gaDen, gioDi, soToa, loaiCho, loaiVe, maBangGia) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pst = connection.prepareStatement(insertVeSql)) {
                    pst.setString(1, ve.getMaVe());
                    pst.setString(2, ve.getMaChuyen());
                    pst.setString(3, ve.getMaLoaiVe());
                    pst.setString(4, ve.getMaSoGhe());
                    if (ve.getNgayIn() != null) pst.setTimestamp(5, Timestamp.valueOf(ve.getNgayIn()));
                    else pst.setNull(5, Types.TIMESTAMP);
                    pst.setString(6, ve.getTrangThai());
                    pst.setString(7, ve.getTenGaDi());
                    pst.setString(8, ve.getTenGaDen());
                    pst.setString(9, ve.getGaDi().getMaGa());
                    pst.setString(10, ve.getGaDen().getMaGa());
                    if (ve.getGioDi() != null) pst.setTimestamp(9, Timestamp.valueOf(ve.getGioDi()));
                    else pst.setNull(9, Types.TIMESTAMP);
                    pst.setString(10, ve.getSoToa());
                    pst.setString(11, ve.getLoaiCho());
                    pst.setString(12, ve.getLoaiVe());

                    String maBangGiaToPersist = ve.getMaBangGia();
                    if ((maBangGiaToPersist == null || maBangGiaToPersist.trim().isEmpty()) && kq != null) maBangGiaToPersist = kq.maBangGia;
                    if (maBangGiaToPersist != null) pst.setString(13, maBangGiaToPersist); else pst.setNull(13, Types.VARCHAR);

                    pst.executeUpdate();
                }
            } else {
                // If exists, update its maBangGia only
                TinhGiaService.KetQuaGia kq = tinhGia.tinhGiaChoVe(ve);
                String updatePriceSql = "UPDATE Ve SET maBangGia = ? WHERE maVe = ?";
                try (PreparedStatement pst = connection.prepareStatement(updatePriceSql)) {
                    String maBangGiaToPersist = ve.getMaBangGia();
                    if ((maBangGiaToPersist == null || maBangGiaToPersist.trim().isEmpty()) && kq != null) maBangGiaToPersist = kq.maBangGia;
                    if (maBangGiaToPersist == null) pst.setNull(1, Types.VARCHAR); else pst.setString(1, maBangGiaToPersist);
                    pst.setString(2, ve.getMaVe());
                    pst.executeUpdate();
                }
            }

            // 4) Tính giá để tạo ChiTietHoaDon snapshot
            if (loaiVe != null) ve.setMaLoaiVe(loaiVe.getMaLoaiVe());
            TinhGiaService.KetQuaGia kq = tinhGia.tinhGiaChoVe(ve);

            // 5) Insert ChiTietHoaDon (nếu chưa có)
            if (chiTietHoaDonDAO.exists(hoaDon.getMaHoaDon(), ve.getMaVe(), connection) || cthdExistsOnConnection(hoaDon.getMaHoaDon(), ve.getMaVe(), connection)) {
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

            // 6) Mark seat as reserved in DB
            try (PreparedStatement pstUpdGhe = connection.prepareStatement("UPDATE Ghe SET trangThai = ? WHERE maGhe = ?")) {
                pstUpdGhe.setString(1, "Đã giữ");
                pstUpdGhe.setString(2, ve.getMaSoGhe());
                pstUpdGhe.executeUpdate();
            } catch (SQLException ignore) {}

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