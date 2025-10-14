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
}
