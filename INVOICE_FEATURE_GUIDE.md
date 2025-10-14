# Hướng Dẫn Sử Dụng Tính Năng Hóa Đơn và In Vé

## Tổng Quan

Hệ thống đã được nâng cấp với các tính năng quản lý hóa đơn và in vé tự động:

1. **Tự động tạo hóa đơn** khi đặt vé
2. **Xác nhận và xuất hóa đơn PDF** với lựa chọn phương thức thanh toán
3. **In vé PDF** theo định dạng Boarding Pass
4. **Xem lịch sử vé** của khách hàng thông qua hệ thống hóa đơn

## 📋 Quy Trình Đặt Vé và Tạo Hóa Đơn

### Bước 1: Đặt Vé (PnlDatVe)

1. Chọn khách hàng từ danh sách (hoặc thêm khách hàng mới)
2. Chọn loại vé
3. Chọn chuyến tàu
4. Chọn toa tàu
5. Chọn ghế trống (màu xanh lá)
6. Xác nhận đặt vé

**Kết quả:**
- Vé được tạo với trạng thái "Đã đặt"
- Hóa đơn tự động được tạo (hoặc dùng hóa đơn mở có sẵn) với trạng thái "Chờ xác nhận"
- Chi tiết vé được thêm vào hóa đơn

### Bước 2: Xác Nhận và Xuất Hóa Đơn (PnlQuanLyVe)

1. Mở panel "Quản Lý Vé và Hóa Đơn"
2. Chọn hóa đơn cần xuất (trạng thái "Chờ xác nhận")
3. Click nút **"Xuất hóa đơn"**
4. Dialog xác nhận sẽ hiển thị:
   - Mã hóa đơn
   - Tên khách hàng
   - Lựa chọn phương thức thanh toán
5. Chọn phương thức: **"Tiền mặt"** hoặc **"Chuyển khoản"**
6. Click **"Xác nhận"**

**Kết quả:**
- Hóa đơn được cập nhật:
  - `trangThai` = "Hoàn tất"
  - `ngayLap` = ngày giờ hiện tại
  - `phuongThucThanhToan` = phương thức đã chọn
- File PDF hóa đơn được tạo trong thư mục `/invoices/`
- File PDF vé được tạo cho mỗi vé trong thư mục `/tickets/`

## 📄 Định Dạng File PDF

### Hóa Đơn (Invoice)

**Tên file:** `HoaDon_[maHoaDon].pdf`

**Nội dung:**
```
CONG TY CO PHAN VAN TAI DUONG SAT SAI GON

Ma hoa don: HD...
Khach hang: [Tên khách hàng]
Ngay lap: dd/MM/yyyy HH:mm
Phuong thuc thanh toan: [Tiền mặt/Chuyển khoản]

╔════════════════════════════════════════════════════════╗
║ STT | Ma ve | Ga di | Ga den | Ngay di | Gia ve      ║
╠════════════════════════════════════════════════════════╣
║  1  | VE... | HN    | HCM    | ...     | 100,000 VND ║
╚════════════════════════════════════════════════════════╝

                              Tong tien: 100,000 VND

Trang thai: Hoan tat - Cam on quy khach da su dung dich vu.
```

### Vé (Ticket)

**Tên file:** `Ve_[maVe].pdf`

**Nội dung:**
```
                    BOARDING PASS
                     VE TAU HOA

                   Ma ve: VE001234567


        GA DI                         GA DEN
      HA NOI                     HO CHI MINH


TAU: SE1                    NGAY: 15/10/2025
GIO DI: 19:30              TOA: TOA01
GHE: A15                    LOAI CHO: Ngoi mem
LOAI VE: Nguoi lon


          Cam on quy khach da su dung dich vu!
```

## 🔍 Xem Lịch Sử Vé (PnlDoiVe)

### Tìm Kiếm Vé Của Khách Hàng

1. Mở panel "Đổi Vé"
2. Nhập mã khách hàng
3. Click **"Tìm kiếm"**

**Kết quả:**
- Hiển thị TẤT CẢ vé của khách hàng
- Bao gồm cả vé đã hoàn tất
- Thông tin: Mã vé, Chuyến, Ga đi, Ga đến, Giờ đi, Ghế, Trạng thái

### Đổi Vé

1. Chọn vé cần đổi từ danh sách
2. Click **"Đổi vé"**
3. Chọn chuyến tàu mới
4. Chọn ghế mới
5. Xác nhận đổi vé

## 🛠️ Cấu Trúc Dữ Liệu

### Bảng HoaDon
```sql
maHoaDon VARCHAR(50) PRIMARY KEY
maNV VARCHAR(50)              -- Mã nhân viên
maKH VARCHAR(50)              -- Mã khách hàng
ngayLap DATETIME              -- NULL khi chưa xác nhận
phuongThucThanhToan VARCHAR   -- NULL khi chưa xác nhận
trangThai VARCHAR             -- 'Chờ xác nhận' hoặc 'Hoàn tất'
```

### Bảng ChiTietHoaDon
```sql
maHoaDon VARCHAR(50)          -- FK to HoaDon
maVe VARCHAR(50)              -- FK to Ve
maLoaiVe VARCHAR(50)
giaGoc FLOAT
giaDaKM FLOAT                 -- Giá sau khuyến mãi
moTa VARCHAR
```

### Quan Hệ
```
KhachHang (1) ─── (N) HoaDon (1) ─── (N) ChiTietHoaDon (N) ─── (1) Ve
```

## 🎯 Các Service và Phương Thức Chính

### HoaDonService
```java
// Tính tổng tiền từ chi tiết hóa đơn
float capNhatTongTien(String maHoaDon)

// Xuất hóa đơn ra PDF
String xuatHoaDonPDF(String maHoaDon)
```

### ChiTietHoaDonService
```java
// Lấy chi tiết theo mã hóa đơn
List<ChiTietHoaDon> getByHoaDon(String maHoaDon)
```

### VeService
```java
// In vé ra PDF
String inVePDF(Ve ve)
```

### HoaDonDAO
```java
// Tìm hóa đơn theo khách hàng
List<HoaDon> findByKhachHang(String maKH)
```

### ChiTietHoaDonDAO
```java
// Tìm chi tiết theo hóa đơn
List<ChiTietHoaDon> findByHoaDon(String maHoaDon)
```

## 📦 Thư Viện Sử Dụng

- **iText7** (v7.2.5) - Thư viện tạo PDF
- Maven dependency:
```xml
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itext7-core</artifactId>
    <version>7.2.5</version>
    <type>pom</type>
</dependency>
```

## 🧪 Testing

Chạy test để kiểm tra PDF generation:
```bash
mvn test -Dtest=PDFGenerationTest
```

## 📁 Cấu Trúc Thư Mục

```
QLVeTau/
├── invoices/           # Thư mục chứa hóa đơn PDF (tự động tạo)
│   └── HoaDon_*.pdf
├── tickets/            # Thư mục chứa vé PDF (tự động tạo)
│   └── Ve_*.pdf
└── src/
    ├── main/java/com/trainstation/
    │   ├── service/
    │   │   ├── HoaDonService.java          (MỚI)
    │   │   ├── ChiTietHoaDonService.java   (MỚI)
    │   │   └── VeService.java              (CẬP NHẬT)
    │   ├── dao/
    │   │   ├── HoaDonDAO.java              (CẬP NHẬT)
    │   │   └── ChiTietHoaDonDAO.java       (CẬP NHẬT)
    │   └── gui/
    │       ├── PnlDatVe.java               (CẬP NHẬT)
    │       ├── PnlQuanLyVe.java            (CẬP NHẬT)
    │       └── PnlDoiVe.java               (CẬP NHẬT)
    └── test/java/com/trainstation/
        └── service/
            └── PDFGenerationTest.java      (MỚI)
```

## ⚠️ Lưu Ý

1. **Thư mục PDF**: Các thư mục `invoices/` và `tickets/` sẽ được tạo tự động khi lần đầu xuất PDF
2. **Mã hóa ký tự**: PDF hiện sử dụng ASCII cho tiếng Việt (không dấu). Để hiển thị đầy đủ dấu, cần thêm font Vietnamese
3. **.gitignore**: Các thư mục PDF đã được thêm vào .gitignore để không commit file PDF vào Git
4. **Database**: Cần đảm bảo kết nối database để hệ thống hoạt động đầy đủ

## 🚀 Tính Năng Tương Lai

- [ ] Thêm font tiếng Việt có dấu cho PDF
- [ ] Thêm mã QR/barcode cho vé
- [ ] Gửi email hóa đơn và vé cho khách hàng
- [ ] In trực tiếp từ ứng dụng
- [ ] Báo cáo doanh thu theo hóa đơn

---

**Phiên bản:** 1.0.0  
**Ngày cập nhật:** 14/10/2025
