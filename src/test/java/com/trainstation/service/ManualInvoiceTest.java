package com.trainstation.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.io.File;

/**
 * Manual test to generate a sample invoice PDF for visual verification
 */
public class ManualInvoiceTest {

    public static void main(String[] args) {
        try {
            // Create invoices directory if not exists
            File invoicesDir = new File("invoices");
            if (!invoicesDir.exists()) {
                invoicesDir.mkdirs();
            }

            String testInvoiceId = "HD1760451544996";
            String fileName = "invoices/HoaDon_" + testInvoiceId + ".pdf";
            PdfWriter writer = new PdfWriter(fileName);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            try {
                // Load Tinos font for Vietnamese support
                PdfFont font = PdfFontFactory.createFont("fonts/Tinos-Regular.ttf", PdfEncodings.IDENTITY_H, 
                        PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
                document.setFont(font);

                // Company title
                Paragraph companyTitle = new Paragraph("CÔNG TY CỔ PHẦN VẬN TẢI ĐƯỜNG SẮT SÀI GÒN")
                        .setFont(font)
                        .setFontSize(14)
                        .setBold()
                        .setTextAlignment(TextAlignment.CENTER);
                document.add(companyTitle);

                // Invoice title
                Paragraph invoiceTitle = new Paragraph("HÓA ĐƠN BÁN VÉ TÀU HỎA")
                        .setFont(font)
                        .setFontSize(14)
                        .setBold()
                        .setTextAlignment(TextAlignment.CENTER);
                document.add(invoiceTitle);

                document.add(new Paragraph("\n").setFont(font));

                // Invoice info
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

                // Table
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

                // Data rows
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

            System.out.println("✓ Invoice PDF generated successfully: " + fileName);
            System.out.println("✓ PDF contains Vietnamese characters with Tinos font");
            System.out.println("✓ Please check the file to verify the output");
            
        } catch (Exception e) {
            System.err.println("✗ Error generating invoice PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
