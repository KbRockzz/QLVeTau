# Cập Nhật PDF Vé Tàu - Vietnamese Ticket PDF Update

## Tổng Quan (Overview)

Đã cập nhật phương thức `VeService.inVePDF(Ve ve)` để tạo vé PDF với bố cục chuyên nghiệp và hỗ trợ đầy đủ tiếng Việt có dấu.

The `VeService.inVePDF(Ve ve)` method has been updated to generate professional PDF tickets with full Vietnamese language support including diacritics.

## Thay Đổi Chính (Key Changes)

### 1. Font Chữ Tiếng Việt (Vietnamese Font Support)
- **Font được sử dụng**: Arial Unicode MS (`fonts/arialuni.ttf`)
- **Encoding**: IDENTITY_H (Unicode)
- **Embedding Strategy**: PREFER_EMBEDDED
- Hỗ trợ đầy đủ các ký tự tiếng Việt có dấu: á, à, ả, ã, ạ, ă, ắ, ằ, ẳ, ẵ, ặ, â, ấ, ầ, ẩ, ẫ, ậ, đ, é, è, ẻ, ẽ, ẹ, ê, ế, ề, ể, ễ, ệ, í, ì, ỉ, ĩ, ị, ó, ò, ỏ, õ, ọ, ô, ố, ồ, ổ, ỗ, ộ, ơ, ớ, ờ, ở, ỡ, ợ, ú, ù, ủ, ũ, ụ, ư, ứ, ừ, ử, ữ, ự, ý, ỳ, ỷ, ỹ, ỵ

### 2. Kích Thước Trang (Page Size)
- **Kích thước**: A5 (148mm x 210mm)
- Phù hợp với kích thước vé tàu thực tế

### 3. Bố Cục Vé (Ticket Layout)

#### Header (Phần Đầu)
```
CÔNG TY CỔ PHẦN VẬN TẢI ĐƯỜNG SẮT SÀI GÒN
(Font size 14, bold, centered)

THẺ LÊN TÀU HỎA / BOARDING PASS
(Font size 12, italic, centered)
```

#### Mã Vé (Ticket Code Section)
```
MÃ QUÉT
(Font size 13, bold, centered)

Mã vé: [Mã vé]
(Centered)
```

#### Bảng Ga Đi/Ga Đến (Station Table)
2 cột với viền, canh giữa:
| Ga đi | Ga đến |
|-------|--------|
| Sài Gòn | Đồng Nai |

#### Bảng Thông Tin Chi Tiết (Details Table)
2 cột với viền:
| Label (2 phần) | Value (3 phần) |
|----------------|----------------|
| Tàu/Train: | SE1 |
| Ngày đi/Date: | 14/10/2025 |
| Giờ đi/Time: | 15:00 |
| Toa/Coach: | 02 |
| Chỗ/Seat: | A01 |
| Loại chỗ/Class: | Ngồi mềm điều hòa |
| Loại vé/Type: | Người lớn |
| Giá/Price: | 250,000 VNĐ |

#### Footer (Phần Cuối)
```
Cảm ơn quý khách đã sử dụng dịch vụ!
(Font size 10, italic, centered)
```

## Sử Dụng (Usage)

### Code Example
```java
// Import
import com.trainstation.service.VeService;
import com.trainstation.model.Ve;
import java.time.LocalDateTime;

// Tạo vé (Create ticket)
Ve ve = new Ve();
ve.setMaVe("VE001");
ve.setMaChuyen("SE1");
ve.setGaDi("Sài Gòn");
ve.setGaDen("Đồng Nai");
ve.setGioDi(LocalDateTime.now());
ve.setSoToa("02");
ve.setMaSoGhe("A01");
ve.setLoaiCho("Ngồi mềm điều hòa");
ve.setLoaiVe("Người lớn");

// Tạo PDF
VeService veService = VeService.getInstance();
String fileName = veService.inVePDF(ve);
System.out.println("PDF created: " + fileName);
```

### Output
- File PDF sẽ được tạo trong thư mục `tickets/`
- Tên file: `Ve_[MaVe].pdf`
- Ví dụ: `tickets/Ve_VE001.pdf`

## Kiểm Thử (Testing)

### Chạy Test (Run Tests)
```bash
mvn test -Dtest=PDFGenerationTest
```

### Kết Quả Mong Đợi (Expected Results)
- ✅ PDF file được tạo thành công
- ✅ Tiếng Việt hiển thị đúng dấu
- ✅ Bố cục chuyên nghiệp, rõ ràng
- ✅ Tất cả thông tin hiển thị đầy đủ

## Yêu Cầu (Requirements)

### Font File
- File font `arialuni.ttf` phải tồn tại trong thư mục `fonts/`
- Font này đã có sẵn trong repository

### Dependencies (pom.xml)
```xml
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itext7-core</artifactId>
    <version>7.2.5</version>
    <type>pom</type>
</dependency>
```

## Cải Tiến Trong Tương Lai (Future Improvements)

1. **Mã QR**: Thêm mã QR thực tế cho việc quét vé
2. **Logo**: Thêm logo công ty vào header
3. **Barcode**: Thêm barcode dạng 1D cho mã vé
4. **Màu sắc**: Thêm màu sắc để làm nổi bật các thông tin quan trọng
5. **Background**: Thêm watermark hoặc background pattern
6. **Multi-language**: Hỗ trợ nhiều ngôn ngữ (English, Chinese, etc.)

## Thay Đổi So Với Phiên Bản Cũ (Changes from Previous Version)

### Trước (Before)
- ❌ Không hỗ trợ tiếng Việt có dấu
- ❌ Bố cục đơn giản, không có viền
- ❌ Thiếu thông tin công ty
- ❌ Không có định dạng chuyên nghiệp

### Sau (After)
- ✅ Hỗ trợ đầy đủ tiếng Việt có dấu
- ✅ Bố cục chuyên nghiệp với bảng có viền
- ✅ Có tên công ty và tiêu đề đầy đủ
- ✅ Format giống vé tàu thực tế

## Tác Giả (Author)
- Updated by: GitHub Copilot
- Date: October 14, 2025
- Version: 1.0.0

## Tài Liệu Tham Khảo (References)
- iText 7 Documentation: https://kb.itextpdf.com/itext/
- Vietnamese Unicode: https://vi.wikipedia.org/wiki/Unicode
- Railway Ticket Standards: Vietnam Railway Corporation
