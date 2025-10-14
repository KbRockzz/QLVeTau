package com.trainstation.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.trainstation.dao.VeDAO;
import com.trainstation.dao.GheDAO;
import com.trainstation.dao.ChuyenTauDAO;
import com.trainstation.model.Ve;
import com.trainstation.model.Ghe;
import com.trainstation.model.ChuyenTau;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Service xử lý nghiệp vụ liên quan đến Vé
 */
public class VeService {
    private static VeService instance;
    private final VeDAO veDAO;
    private final GheDAO gheDAO;
    private final ChuyenTauDAO chuyenTauDAO;

    private VeService() {
        this.veDAO = VeDAO.getInstance();
        this.gheDAO = GheDAO.getInstance();
        this.chuyenTauDAO = ChuyenTauDAO.getInstance();
    }

    public static synchronized VeService getInstance() {
        if (instance == null) {
            instance = new VeService();
        }
        return instance;
    }

    /**
     * Tạo vé mới
     */
    public Ve taoVe(Ve ve) {
        if (veDAO.insert(ve)) {
            return ve;
        }
        throw new RuntimeException("Không thể tạo vé");
    }

    /**
     * Cập nhật thông tin vé
     */
    public boolean capNhatVe(Ve ve) {
        return veDAO.update(ve);
    }

    /**
     * Hủy vé
     */
    public boolean huyVe(String maVe) {
        Ve ve = veDAO.findById(maVe);
        if (ve == null) {
            throw new IllegalArgumentException("Không tìm thấy vé");
        }

        if (!"Đã thanh toán".equals(ve.getTrangThai())) {
            throw new IllegalStateException("Chỉ có thể hủy vé đã thanh toán");
        }

        ve.setTrangThai("Đã hủy");
        boolean result = veDAO.update(ve);

        // Cập nhật trạng thái ghế
        if (result && ve.getMaSoGhe() != null) {
            Ghe ghe = gheDAO.findById(ve.getMaSoGhe());
            if (ghe != null) {
                ghe.setTrangThai("Trống");
                gheDAO.update(ghe);
            }
        }

        return result;
    }

    /**
     * Hoàn vé
     */
    public boolean hoanVe(String maVe) {
        Ve ve = veDAO.findById(maVe);
        if (ve == null) {
            throw new IllegalArgumentException("Không tìm thấy vé");
        }

        if (!"Đã thanh toán".equals(ve.getTrangThai())) {
            throw new IllegalStateException("Chỉ có thể hoàn vé đã thanh toán");
        }

        ve.setTrangThai("Đã hoàn");
        boolean result = veDAO.update(ve);

        // Cập nhật trạng thái ghế
        if (result && ve.getMaSoGhe() != null) {
            Ghe ghe = gheDAO.findById(ve.getMaSoGhe());
            if (ghe != null) {
                ghe.setTrangThai("Trống");
                gheDAO.update(ghe);
            }
        }

        return result;
    }

    /**
     * Đổi vé
     */
    public boolean doiVe(String maVeCu, Ve veMoi) {
        Ve veCu = veDAO.findById(maVeCu);
        if (veCu == null) {
            throw new IllegalArgumentException("Không tìm thấy vé cũ");
        }

        if (!"Đã thanh toán".equals(veCu.getTrangThai())) {
            throw new IllegalStateException("Chỉ có thể đổi vé đã thanh toán");
        }

        // Giải phóng ghế cũ
        if (veCu.getMaSoGhe() != null) {
            Ghe gheCu = gheDAO.findById(veCu.getMaSoGhe());
            if (gheCu != null) {
                gheCu.setTrangThai("Trống");
                gheDAO.update(gheCu);
            }
        }

        // Cập nhật ghế mới
        if (veMoi.getMaSoGhe() != null) {
            Ghe gheMoi = gheDAO.findById(veMoi.getMaSoGhe());
            if (gheMoi != null) {
                if (!"Trống".equals(gheMoi.getTrangThai())) {
                    throw new IllegalStateException("Ghế mới đã được đặt");
                }
                gheMoi.setTrangThai("Bận");
                gheDAO.update(gheMoi);
            }
        }

        // Cập nhật thông tin vé
        veCu.setMaChuyen(veMoi.getMaChuyen());
        veCu.setMaSoGhe(veMoi.getMaSoGhe());
        veCu.setGaDi(veMoi.getGaDi());
        veCu.setGaDen(veMoi.getGaDen());
        veCu.setGioDi(veMoi.getGioDi());
        veCu.setSoToa(veMoi.getSoToa());
        veCu.setLoaiCho(veMoi.getLoaiCho());
        veCu.setMaBangGia(veMoi.getMaBangGia());

        return veDAO.update(veCu);
    }

    /**
     * Lấy tất cả vé
     */
    public List<Ve> layTatCaVe() {
        return veDAO.getAll();
    }

    /**
     * Tìm vé theo mã
     */
    public Ve timVeTheoMa(String maVe) {
        return veDAO.findById(maVe);
    }

    /**
     * Lấy danh sách vé theo chuyến tàu
     */
    public List<Ve> layVeTheoChuyenTau(String maChuyen) {
        return veDAO.getAll().stream()
                .filter(v -> v.getMaChuyen() != null && v.getMaChuyen().equals(maChuyen))
                .collect(Collectors.toList());
    }

    /**
     * Lấy danh sách vé theo trạng thái
     */
    public List<Ve> layVeTheoTrangThai(String trangThai) {
        return veDAO.getAll().stream()
                .filter(v -> v.getTrangThai() != null && v.getTrangThai().equals(trangThai))
                .collect(Collectors.toList());
    }

    /**
     * Đếm số vé theo trạng thái
     */
    public int demVeTheoTrangThai(String trangThai) {
        return layVeTheoTrangThai(trangThai).size();
    }

    /**
     * Xóa vé
     */
    public boolean xoaVe(String maVe) {
        return veDAO.delete(maVe);
    }

    /**
     * Lấy danh sách vé theo khách hàng
     */
    public List<Ve> layVeTheoKhachHang(String maKH) {
        return veDAO.getByKhachHang(maKH);
    }

    /**
     * Gửi yêu cầu hoàn vé (chuyển trạng thái thành 'Chờ duyệt')
     */
    public boolean guiYeuCauHoanVe(String maVe) {
        Ve ve = veDAO.findById(maVe);
        if (ve == null) {
            throw new IllegalArgumentException("Không tìm thấy vé");
        }

        if (!"Đã thanh toán".equals(ve.getTrangThai()) && !"Đã đặt".equals(ve.getTrangThai())) {
            throw new IllegalStateException("Chỉ có thể hoàn vé đã đặt hoặc đã thanh toán");
        }

        ve.setTrangThai("Chờ duyệt");
        return veDAO.update(ve);
    }

    /**
     * Duyệt yêu cầu hoàn vé
     */
    public boolean duyetHoanVe(String maVe, boolean chấpNhan) {
        Ve ve = veDAO.findById(maVe);
        if (ve == null) {
            throw new IllegalArgumentException("Không tìm thấy vé");
        }

        if (!"Chờ duyệt".equals(ve.getTrangThai())) {
            throw new IllegalStateException("Vé không trong trạng thái chờ duyệt");
        }

        if (chấpNhan) {
            // Chấp nhận hoàn vé
            ve.setTrangThai("Đã hoàn");
            boolean result = veDAO.update(ve);

            // Cập nhật trạng thái ghế
            if (result && ve.getMaSoGhe() != null) {
                Ghe ghe = gheDAO.findById(ve.getMaSoGhe());
                if (ghe != null) {
                    ghe.setTrangThai("Trống");
                    gheDAO.update(ghe);
                }
            }
            return result;
        } else {
            // Từ chối hoàn vé
            ve.setTrangThai("Đã đặt");
            return veDAO.update(ve);
        }
    }

    /**
     * In vé ra file PDF (Boarding Pass style)
     */
    public String inVePDF(Ve ve) throws FileNotFoundException {
        if (ve == null) {
            throw new IllegalArgumentException("Vé không hợp lệ");
        }

        // Create tickets directory if not exists
        File ticketsDir = new File("tickets");
        if (!ticketsDir.exists()) {
            ticketsDir.mkdirs();
        }

        String fileName = "tickets/Ve_" + ve.getMaVe() + ".pdf";
        PdfWriter writer = new PdfWriter(fileName);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

            // Title - Boarding Pass
            Paragraph title = new Paragraph("BOARDING PASS")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);

            Paragraph subtitle = new Paragraph("VE TAU HOA")
                    .setFontSize(16)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(subtitle);

            document.add(new Paragraph("\n"));

            // Ticket ID / Barcode placeholder
            Paragraph ticketId = new Paragraph("Ma ve: " + ve.getMaVe())
                    .setFontSize(14)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(ticketId);

            document.add(new Paragraph("\n"));

            // Main information table
            Table mainTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
            mainTable.setWidth(UnitValue.createPercentValue(100));

            // Station information
            mainTable.addCell(new Cell().add(new Paragraph("GA DI").setBold()).setBorder(null));
            mainTable.addCell(new Cell().add(new Paragraph("GA DEN").setBold()).setBorder(null));
            mainTable.addCell(new Cell().add(new Paragraph(ve.getGaDi() != null ? ve.getGaDi() : "N/A")
                    .setFontSize(16).setBold()).setBorder(null));
            mainTable.addCell(new Cell().add(new Paragraph(ve.getGaDen() != null ? ve.getGaDen() : "N/A")
                    .setFontSize(16).setBold()).setBorder(null));

            document.add(mainTable);
            document.add(new Paragraph("\n"));

            // Details table
            Table detailsTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
            detailsTable.setWidth(UnitValue.createPercentValue(100));

            // Train number
            detailsTable.addCell(new Cell().add(new Paragraph("TAU:").setBold()).setBorder(null));
            detailsTable.addCell(new Cell().add(new Paragraph(ve.getMaChuyen() != null ? ve.getMaChuyen() : "N/A"))
                    .setBorder(null));

            // Date
            detailsTable.addCell(new Cell().add(new Paragraph("NGAY:").setBold()).setBorder(null));
            detailsTable.addCell(new Cell().add(new Paragraph(
                    ve.getGioDi() != null ? ve.getGioDi().format(dateFormatter) : "N/A"))
                    .setBorder(null));

            // Time
            detailsTable.addCell(new Cell().add(new Paragraph("GIO DI:").setBold()).setBorder(null));
            detailsTable.addCell(new Cell().add(new Paragraph(
                    ve.getGioDi() != null ? ve.getGioDi().format(timeFormatter) : "N/A"))
                    .setBorder(null));

            // Carriage
            detailsTable.addCell(new Cell().add(new Paragraph("TOA:").setBold()).setBorder(null));
            detailsTable.addCell(new Cell().add(new Paragraph(ve.getSoToa() != null ? ve.getSoToa() : "N/A"))
                    .setBorder(null));

            // Seat
            detailsTable.addCell(new Cell().add(new Paragraph("GHE:").setBold()).setBorder(null));
            detailsTable.addCell(new Cell().add(new Paragraph(ve.getMaSoGhe() != null ? ve.getMaSoGhe() : "N/A"))
                    .setBorder(null));

            // Seat type
            detailsTable.addCell(new Cell().add(new Paragraph("LOAI CHO:").setBold()).setBorder(null));
            detailsTable.addCell(new Cell().add(new Paragraph(ve.getLoaiCho() != null ? ve.getLoaiCho() : "N/A"))
                    .setBorder(null));

            // Ticket type
            detailsTable.addCell(new Cell().add(new Paragraph("LOAI VE:").setBold()).setBorder(null));
            detailsTable.addCell(new Cell().add(new Paragraph(ve.getLoaiVe() != null ? ve.getLoaiVe() : "N/A"))
                    .setBorder(null));

            document.add(detailsTable);
            document.add(new Paragraph("\n"));

            // Footer
            Paragraph footer = new Paragraph("Cam on quy khach da su dung dich vu!")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setItalic();
            document.add(footer);

        } finally {
            document.close();
        }

        return fileName;
    }
}
