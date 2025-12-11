package com.trainstation.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.trainstation.model.Ve;
import org.junit.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.*;

/**
 * Test for PDF generation functionality
 */
public class PDFGenerationTest {

    @Test
    public void testTicketPDFGenerationStandalone() throws Exception {
        // Create a test ticket with Vietnamese text
        Ve ve = new Ve();
        ve.setMaVe("VE_TEST_001");
        ve.setMaChuyen("SE1");
        ve.setMaGaDi("SG");
        ve.setMaGaDen("DN");
        ve.setTenGaDi("Sài Gòn");
        ve.setTenGaDen("Đồng Nai");
        ve.setGioDi(LocalDateTime.now());
        ve.setSoToa(2);
        ve.setMaSoGhe("A01");
        ve.setLoaiCho("Ngồi mềm điều hòa");
        ve.setLoaiVe("Người lớn");
        ve.setTrangThai("Đã thanh toán");
        
        // Create tickets directory if not exists
        File ticketsDir = new File("tickets");
        if (!ticketsDir.exists()) {
            ticketsDir.mkdirs();
        }

        String fileName = "tickets/Ve_" + ve.getMaVe() + ".pdf";
        PdfWriter writer = new PdfWriter(fileName);
        PdfDocument pdf = new PdfDocument(writer);
        
        // Set page size to A5
        Document document = new Document(pdf, PageSize.A5);

        try {
            // Load Arial Unicode MS font for Vietnamese support
            PdfFont font = PdfFontFactory.createFont("fonts/arialuni.ttf", PdfEncodings.IDENTITY_H, 
                    PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
            document.setFont(font);
            
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            // Header - Company name
            Paragraph header = new Paragraph("CÔNG TY CỔ PHẦN VẬN TẢI ĐƯỜNG SẮT SÀI GÒN")
                    .setFont(font)
                    .setFontSize(14)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(header);

            // Subtitle - Boarding Pass
            Paragraph subHeader = new Paragraph("THẺ LÊN TÀU HỎA / BOARDING PASS")
                    .setFont(font)
                    .setFontSize(12)
                    .setItalic()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(subHeader);

            document.add(new Paragraph("\n"));

            // QR Code section - Ticket ID
            Paragraph qrTitle = new Paragraph("MÃ QUÉT")
                    .setFont(font)
                    .setFontSize(13)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(qrTitle);

            Paragraph ticketId = new Paragraph("Mã vé: " + ve.getMaVe())
                    .setFont(font)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(ticketId);

            document.add(new Paragraph("\n"));

            // Station table (Ga đi / Ga đến) - 2 columns with borders
            Table gaTable = new Table(UnitValue.createPercentArray(new float[]{1, 1})).useAllAvailableWidth();
            
            // Header row
            gaTable.addCell(new Cell()
                    .add(new Paragraph("Ga đi").setFont(font).setBold())
                    .setTextAlignment(TextAlignment.CENTER));
            gaTable.addCell(new Cell()
                    .add(new Paragraph("Ga đến").setFont(font).setBold())
                    .setTextAlignment(TextAlignment.CENTER));
            
            // Station names row
            gaTable.addCell(new Cell()
                    .add(new Paragraph(ve.getTenGaDi() != null ? ve.getTenGaDi() : "N/A").setFont(font))
                    .setTextAlignment(TextAlignment.CENTER));
            gaTable.addCell(new Cell()
                    .add(new Paragraph(ve.getTenGaDen() != null ? ve.getTenGaDen() : "N/A").setFont(font))
                    .setTextAlignment(TextAlignment.CENTER));
            
            document.add(gaTable);
            document.add(new Paragraph("\n"));

            // Details table - 2 columns (label and value)
            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{2, 3})).useAllAvailableWidth();

            // Train number
            infoTable.addCell(new Cell()
                    .add(new Paragraph("Tàu/Train:").setFont(font).setBold()));
            infoTable.addCell(new Cell()
                    .add(new Paragraph(ve.getMaChuyen() != null ? ve.getMaChuyen() : "N/A").setFont(font)));

            // Date
            infoTable.addCell(new Cell()
                    .add(new Paragraph("Ngày đi/Date:").setFont(font).setBold()));
            infoTable.addCell(new Cell()
                    .add(new Paragraph(ve.getGioDi() != null ? ve.getGioDi().format(dateFormatter) : "N/A").setFont(font)));

            // Time
            infoTable.addCell(new Cell()
                    .add(new Paragraph("Giờ đi/Time:").setFont(font).setBold()));
            infoTable.addCell(new Cell()
                    .add(new Paragraph(ve.getGioDi() != null ? ve.getGioDi().format(timeFormatter) : "N/A").setFont(font)));

            // Carriage
            infoTable.addCell(new Cell()
                    .add(new Paragraph("Toa/Coach:").setFont(font).setBold()));
            infoTable.addCell(new Cell()
                    .add(new Paragraph(ve.getSoToa() != null ? String.valueOf(ve.getSoToa()) : "N/A").setFont(font)));

            // Seat
            infoTable.addCell(new Cell()
                    .add(new Paragraph("Chỗ/Seat:").setFont(font).setBold()));
            infoTable.addCell(new Cell()
                    .add(new Paragraph(ve.getMaSoGhe() != null ? ve.getMaSoGhe() : "N/A").setFont(font)));

            // Seat type
            infoTable.addCell(new Cell()
                    .add(new Paragraph("Loại chỗ/Class:").setFont(font).setBold()));
            infoTable.addCell(new Cell()
                    .add(new Paragraph(ve.getLoaiCho() != null ? ve.getLoaiCho() : "N/A").setFont(font)));

            // Ticket type
            infoTable.addCell(new Cell()
                    .add(new Paragraph("Loại vé/Type:").setFont(font).setBold()));
            infoTable.addCell(new Cell()
                    .add(new Paragraph(ve.getLoaiVe() != null ? ve.getLoaiVe() : "N/A").setFont(font)));

            // Price
            infoTable.addCell(new Cell()
                    .add(new Paragraph("Giá/Price:").setFont(font).setBold()));
            infoTable.addCell(new Cell()
                    .add(new Paragraph("250,000 VNĐ").setFont(font)));

            document.add(infoTable);

            // Footer - Thank you message
            document.add(new Paragraph("\nCảm ơn quý khách đã sử dụng dịch vụ!")
                    .setFont(font)
                    .setItalic()
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER));

        } finally {
            document.close();
        }
        
        // Verify file was created
        assertNotNull("File name should not be null", fileName);
        File pdfFile = new File(fileName);
        assertTrue("PDF file should exist", pdfFile.exists());
        assertTrue("PDF file should have content", pdfFile.length() > 0);
        
        System.out.println("Ticket PDF generated successfully: " + fileName);
        System.out.println("PDF contains Vietnamese characters: Sài Gòn, Đồng Nai, Người lớn");
    }

    @Test
    public void testDirectoryCreation() {
        File ticketsDir = new File("tickets");
        File invoicesDir = new File("invoices");
        
        // The directories should be created automatically by the services
        // This test just ensures they can be created if needed
        if (!ticketsDir.exists()) {
            assertTrue("Tickets directory should be creatable", ticketsDir.mkdirs());
        }
        if (!invoicesDir.exists()) {
            assertTrue("Invoices directory should be creatable", invoicesDir.mkdirs());
        }
        
        System.out.println("Directories verified/created successfully");
    }

    @Test
    public void testInvoicePDFGenerationStandalone() throws Exception {
        // Create invoices directory if not exists
        File invoicesDir = new File("invoices");
        if (!invoicesDir.exists()) {
            invoicesDir.mkdirs();
        }

        String testInvoiceId = "HD_TEST_001";
        String fileName = "invoices/HoaDon_" + testInvoiceId + ".pdf";
        PdfWriter writer = new PdfWriter(fileName);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        try {
            // Load Tinos font for Vietnamese support
            PdfFont font = PdfFontFactory.createFont("fonts/Tinos-Regular.ttf", PdfEncodings.IDENTITY_H, 
                    PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
            document.setFont(font);

            // Test company title
            Paragraph companyTitle = new Paragraph("CÔNG TY CỔ PHẦN VẬN TẢI ĐƯỜNG SẮT SÀI GÒN")
                    .setFont(font)
                    .setFontSize(14)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(companyTitle);

            // Test invoice title
            Paragraph invoiceTitle = new Paragraph("HÓA ĐƠN BÁN VÉ TÀU HỎA")
                    .setFont(font)
                    .setFontSize(14)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(invoiceTitle);

            document.add(new Paragraph("\n").setFont(font));

            // Test invoice info
            document.add(new Paragraph("Mã hóa đơn: HD1760451544996")
                    .setFont(font)
                    .setFontSize(11));
            document.add(new Paragraph("Khách hàng: Nguyễn Văn An")
                    .setFont(font)
                    .setFontSize(11));
            document.add(new Paragraph("Ngày lập: 14/10/2025 21:32")
                    .setFont(font)
                    .setFontSize(11));
            document.add(new Paragraph("Phương thức thanh toán: Tiền mặt")
                    .setFont(font)
                    .setFontSize(11));

            document.add(new Paragraph("\n").setFont(font));

            // Test table
            Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2, 2, 2, 2, 2}));
            table.setWidth(UnitValue.createPercentValue(100));

            // Header
            table.addHeaderCell(new Cell().add(new Paragraph("STT").setFont(font).setBold())
                    .setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Mã vé").setFont(font).setBold())
                    .setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Ga đi").setFont(font).setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Ga đến").setFont(font).setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Ngày đi").setFont(font).setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Giá vé").setFont(font).setBold()));

            // Test data rows
            table.addCell(new Cell().add(new Paragraph("1").setFont(font))
                    .setTextAlignment(TextAlignment.CENTER));
            table.addCell(new Cell().add(new Paragraph("VE1760451544735").setFont(font))
                    .setTextAlignment(TextAlignment.CENTER));
            table.addCell(new Cell().add(new Paragraph("Sài Gòn").setFont(font)));
            table.addCell(new Cell().add(new Paragraph("Hà Nội").setFont(font)));
            table.addCell(new Cell().add(new Paragraph("10/01/2025").setFont(font)));
            table.addCell(new Cell().add(new Paragraph("100.000 VNĐ").setFont(font)));

            table.addCell(new Cell().add(new Paragraph("2").setFont(font))
                    .setTextAlignment(TextAlignment.CENTER));
            table.addCell(new Cell().add(new Paragraph("VE1760451558661").setFont(font))
                    .setTextAlignment(TextAlignment.CENTER));
            table.addCell(new Cell().add(new Paragraph("Sài Gòn").setFont(font)));
            table.addCell(new Cell().add(new Paragraph("Hà Nội").setFont(font)));
            table.addCell(new Cell().add(new Paragraph("10/01/2025").setFont(font)));
            table.addCell(new Cell().add(new Paragraph("100.000 VNĐ").setFont(font)));

            document.add(table);

            document.add(new Paragraph("\n").setFont(font));

            // Total
            Paragraph totalParagraph = new Paragraph("Tổng tiền: 200.000 VNĐ")
                    .setFont(font)
                    .setFontSize(11)
                    .setBold()
                    .setTextAlignment(TextAlignment.RIGHT);
            document.add(totalParagraph);

            // Status
            Paragraph statusParagraph = new Paragraph("Trạng thái: Hoàn tất - Cảm ơn quý khách đã sử dụng dịch vụ!")
                    .setFont(font)
                    .setFontSize(11)
                    .setTextAlignment(TextAlignment.RIGHT);
            document.add(statusParagraph);

        } finally {
            document.close();
        }

        // Verify file was created
        assertNotNull("File name should not be null", fileName);
        File pdfFile = new File(fileName);
        assertTrue("PDF file should exist", pdfFile.exists());
        assertTrue("PDF file should have content", pdfFile.length() > 0);

        System.out.println("Invoice PDF generated successfully: " + fileName);
        System.out.println("PDF contains Vietnamese characters with Tinos font: Sài Gòn, Hà Nội, Nguyễn Văn An");
    }
}
