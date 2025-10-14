package com.trainstation.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.trainstation.dao.ChiTietHoaDonDAO;
import com.trainstation.dao.HoaDonDAO;
import com.trainstation.dao.KhachHangDAO;
import com.trainstation.dao.VeDAO;
import com.trainstation.model.ChiTietHoaDon;
import com.trainstation.model.HoaDon;
import com.trainstation.model.KhachHang;
import com.trainstation.model.Ve;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Service xử lý nghiệp vụ liên quan đến Hóa đơn
 */
public class HoaDonService {
    private static HoaDonService instance;
    private final HoaDonDAO hoaDonDAO;
    private final ChiTietHoaDonDAO chiTietHoaDonDAO;
    private final KhachHangDAO khachHangDAO;
    private final VeDAO veDAO;

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
    public String xuatHoaDonPDF(String maHoaDon) throws FileNotFoundException {
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
            // Tiêu đề
            Paragraph title = new Paragraph("CONG TY CO PHAN VAN TAI DUONG SAT SAI GON")
                    .setFontSize(16)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);

            document.add(new Paragraph("\n"));

            // Thông tin hóa đơn
            document.add(new Paragraph("Ma hoa don: " + hoaDon.getMaHoaDon()));
            document.add(new Paragraph("Khach hang: " + 
                    (khachHang != null ? khachHang.getTenKhachHang() : "N/A")));
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            document.add(new Paragraph("Ngay lap: " + 
                    (hoaDon.getNgayLap() != null ? hoaDon.getNgayLap().format(formatter) : "N/A")));
            document.add(new Paragraph("Phuong thuc thanh toan: " + 
                    (hoaDon.getPhuongThucThanhToan() != null ? hoaDon.getPhuongThucThanhToan() : "N/A")));

            document.add(new Paragraph("\n"));

            // Bảng vé
            Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2, 2, 2, 2, 2}));
            table.setWidth(UnitValue.createPercentValue(100));

            // Header
            table.addHeaderCell(new Cell().add(new Paragraph("STT").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Ma ve").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Ga di").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Ga den").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Ngay di").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Gia ve").setBold()));

            // Data rows
            int stt = 1;
            float tongTien = 0;
            NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
            
            for (ChiTietHoaDon ct : chiTietList) {
                Ve ve = veDAO.findById(ct.getMaVe());
                table.addCell(String.valueOf(stt++));
                table.addCell(ct.getMaVe());
                table.addCell(ve != null ? ve.getGaDi() : "N/A");
                table.addCell(ve != null ? ve.getGaDen() : "N/A");
                table.addCell(ve != null && ve.getGioDi() != null ? ve.getGioDi().format(formatter) : "N/A");
                table.addCell(currencyFormat.format(ct.getGiaDaKM()) + " VND");
                tongTien += ct.getGiaDaKM();
            }

            document.add(table);

            document.add(new Paragraph("\n"));

            // Tổng tiền
            Paragraph totalParagraph = new Paragraph("Tong tien: " + currencyFormat.format(tongTien) + " VND")
                    .setFontSize(14)
                    .setBold()
                    .setTextAlignment(TextAlignment.RIGHT);
            document.add(totalParagraph);

            document.add(new Paragraph("\n"));

            // Footer
            Paragraph footer = new Paragraph("Trang thai: Hoan tat - Cam on quy khach da su dung dich vu.")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setItalic();
            document.add(footer);

        } finally {
            document.close();
        }

        return fileName;
    }
}
