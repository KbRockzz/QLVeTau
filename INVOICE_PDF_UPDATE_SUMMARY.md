# Invoice PDF Generation Update Summary

## Overview
This document summarizes the changes made to the invoice PDF generation functionality to support Vietnamese characters using the Tinos-Regular.ttf font.

## Changes Made

### 1. Updated `HoaDonService.java`
**File**: `src/main/java/com/trainstation/service/HoaDonService.java`

**Key Changes**:
- Added imports for font handling:
  ```java
  import com.itextpdf.kernel.font.PdfFont;
  import com.itextpdf.kernel.font.PdfFontFactory;
  import com.itextpdf.io.font.PdfEncodings;
  ```

- Updated `xuatHoaDonPDF()` method to:
  - Load Tinos-Regular.ttf font with Vietnamese support:
    ```java
    PdfFont font = PdfFontFactory.createFont("fonts/Tinos-Regular.ttf", 
        PdfEncodings.IDENTITY_H, 
        PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
    ```
  
  - Apply font to all text elements (Paragraphs, Cells)
  - Update invoice title format:
    - "CÔNG TY CỔ PHẦN VẬN TẢI ĐƯỜNG SẮT SÀI GÒN" (centered, bold, size 14)
    - "HÓA ĐƠN BÁN VÉ TÀU HỎA" (centered, bold, size 14)
  
  - Format invoice information (left-aligned, size 11):
    - Mã hóa đơn
    - Khách hàng
    - Ngày lập (format: dd/MM/yyyy HH:mm)
    - Phương thức thanh toán
  
  - Create properly formatted table:
    - Columns: STT, Mã vé, Ga đi, Ga đến, Ngày đi, Giá vé
    - Header cells: bold, with STT and Mã vé centered
    - Data cells: STT and Mã vé centered
    - Date format: dd/MM/yyyy (only date, not time)
    - Price format: ###,### VNĐ (with comma separators)
  
  - Add footer (right-aligned, size 11):
    - Tổng tiền: ###,### VNĐ
    - Trạng thái: [status] - Cảm ơn quý khách đã sử dụng dịch vụ!

### 2. Added Test Cases
**File**: `src/test/java/com/trainstation/service/PDFGenerationTest.java`

Added `testInvoicePDFGenerationStandalone()` test method to verify:
- Tinos font loads correctly
- Vietnamese characters display properly
- Invoice format matches requirements
- PDF file is generated successfully

### 3. Created Manual Test
**File**: `src/test/java/com/trainstation/service/ManualInvoiceTest.java`

Created a standalone Java program that can be run to generate a sample invoice PDF for visual verification.

**To run**:
```bash
mvn clean compile test-compile
java -cp "target/classes:target/test-classes:$(mvn dependency:build-classpath -DincludeScope=compile -Dmdep.outputFile=/dev/stdout -q)" com.trainstation.service.ManualInvoiceTest
```

## Requirements Compliance

✅ All requirements from the problem statement have been implemented:

1. ✅ Uses `fonts/Tinos-Regular.ttf` font
2. ✅ Proper Vietnamese character support with `PdfEncodings.IDENTITY_H`
3. ✅ Company title centered, bold, size 14
4. ✅ Invoice title centered, bold, size 14
5. ✅ Invoice info left-aligned, size 11
6. ✅ Table with 6 columns (STT, Mã vé, Ga đi, Ga đến, Ngày đi, Giá vé)
7. ✅ Data from ChiTietHoaDon and Ve models
8. ✅ Table with borders, bold headers, centered STT and Mã vé
9. ✅ Total calculated as sum of prices
10. ✅ Price format with comma separators (###,### VNĐ)
11. ✅ Status from HoaDon.trangThai
12. ✅ Thank you message in footer
13. ✅ Footer right-aligned, size 11
14. ✅ Files saved to `invoices/` directory
15. ✅ Filename format: `HoaDon_<maHoaDon>.pdf`
16. ✅ No employee signature

## Testing

All tests pass successfully:
```bash
mvn test
```

**Test Results**:
- ✅ testDirectoryCreation
- ✅ testTicketPDFGenerationStandalone
- ✅ testInvoicePDFGenerationStandalone

## Sample Output

The generated PDF includes:

```
CÔNG TY CỔ PHẦN VẬN TẢI ĐƯỜNG SẮT SÀI GÒN
HÓA ĐƠN BÁN VÉ TÀU HỎA

Mã hóa đơn: HD1760451544996
Khách hàng: Nguyễn Văn An
Ngày lập: 14/10/2025 21:32
Phương thức thanh toán: Tiền mặt

----------------------------------------------------------
| STT | Mã vé           | Ga đi     | Ga đến  | Ngày đi    | Giá vé      |
----------------------------------------------------------
| 1   | VE1760451544735 | Sài Gòn   | Hà Nội  | 10/01/2025 | 100.000 VNĐ |
| 2   | VE1760451558661 | Sài Gòn   | Hà Nội  | 10/01/2025 | 100.000 VNĐ |
----------------------------------------------------------

                              Tổng tiền: 200.000 VNĐ
      Trạng thái: Hoàn tất - Cảm ơn quý khách đã sử dụng dịch vụ!
```

## Notes

- The `invoices/` directory is automatically created if it doesn't exist
- The `invoices/` and `tickets/` directories are in `.gitignore` to prevent committing generated PDFs
- The font file `fonts/Tinos-Regular.ttf` must exist in the project root
- All Vietnamese characters (à, á, ả, ã, ạ, ă, ằ, ắ, ẳ, ẵ, ặ, â, ầ, ấ, ẩ, ẫ, ậ, đ, etc.) are properly displayed

## Usage

To generate an invoice PDF programmatically:

```java
HoaDonService hoaDonService = HoaDonService.getInstance();
String pdfPath = hoaDonService.xuatHoaDonPDF("HD1760451544996");
System.out.println("Invoice generated: " + pdfPath);
```

The method will:
1. Retrieve invoice data from database
2. Retrieve customer information
3. Retrieve ticket details from ChiTietHoaDon
4. Generate PDF with proper Vietnamese formatting
5. Return the file path

## Error Handling

The method throws `Exception` if:
- Invoice not found (IllegalArgumentException)
- Font file not found
- PDF generation fails
- File writing fails
