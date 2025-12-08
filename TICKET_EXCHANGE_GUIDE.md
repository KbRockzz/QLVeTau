# Hướng Dẫn Chức Năng Đổi Vé

## 1. TỔNG QUAN

Chức năng đổi vé cho phép nhân viên xử lý yêu cầu thay đổi thông tin vé đã phát hành của khách hàng, bao gồm:
- Đổi chuyến tàu
- Đổi ghế/toa
- Đổi loại vé
- Cập nhật thông tin hành khách

Hệ thống tự động:
- Kiểm tra điều kiện đổi vé theo quy định
- Tính phí đổi vé dựa trên thời gian
- Tính chênh lệch giá vé
- Lưu lịch sử thay đổi
- Ghi log audit đầy đủ

## 2. CÁC BẢNG DATABASE MỚI

### 2.1 VeHistory
Lưu lịch sử thay đổi của vé
```sql
CREATE TABLE VeHistory (
    maLichSu VARCHAR(50) PRIMARY KEY,
    maVe VARCHAR(50) NOT NULL,
    maChuyen VARCHAR(50),
    ... (snapshot đầy đủ thông tin vé)
    ngayThayDoi DATETIME NOT NULL,
    loaiThayDoi NVARCHAR(50), -- 'DOI_VE', 'HOAN_VE', 'HUY_VE'
    nguoiThucHien VARCHAR(50),
    ghiChu NVARCHAR(500)
);
```

### 2.2 GiaoDichDoiVe
Lưu thông tin giao dịch đổi vé
```sql
CREATE TABLE GiaoDichDoiVe (
    maGiaoDich VARCHAR(50) PRIMARY KEY,
    maVeCu VARCHAR(50) NOT NULL,
    maVeMoi VARCHAR(50) NOT NULL,
    ngayDoi DATETIME NOT NULL,
    maNV VARCHAR(50),
    maKH VARCHAR(50),
    -- Thông tin tài chính
    giaVeCu FLOAT,
    giaVeMoi FLOAT,
    phiDoiVe FLOAT,
    chenhLechGia FLOAT,
    soTienThu FLOAT,
    soTienHoan FLOAT,
    -- Thông tin phê duyệt
    trangThai NVARCHAR(50), -- 'CHO_DUYET', 'DA_DUYET', 'TU_CHOI', 'HOAN_THANH'
    nguoiDuyet VARCHAR(50),
    ngayDuyet DATETIME,
    ...
);
```

### 2.3 AuditLog
Ghi lại tất cả thao tác quan trọng
```sql
CREATE TABLE AuditLog (
    maLog VARCHAR(50) PRIMARY KEY,
    loaiThaoTac NVARCHAR(100),
    maThamChieu VARCHAR(50),
    maNV VARCHAR(50),
    thoiGian DATETIME NOT NULL,
    noiDung NVARCHAR(1000),
    duLieuTruoc NVARCHAR(MAX),
    duLieuSau NVARCHAR(MAX),
    ...
);
```

### 2.4 CauHinhDoiVe
Tham số cấu hình cho đổi vé
```sql
CREATE TABLE CauHinhDoiVe (
    maCauHinh VARCHAR(50) PRIMARY KEY,
    tenCauHinh NVARCHAR(100),
    giaTriSo FLOAT,
    giaTriChuoi NVARCHAR(500),
    moTa NVARCHAR(500),
    ngayCapNhat DATETIME
);
```

## 3. QUY TẮC NGHIỆP VỤ

### 3.1 Trạng Thái Vé Được Phép Đổi
- ✓ **Đã đặt** (Booked)
- ✓ **Đã thanh toán** (Paid)

### 3.2 Trạng Thái Vé KHÔNG Được Đổi
- ✗ Đã sử dụng
- ✗ Đã hoàn (Refunded)
- ✗ Đã hủy (Cancelled)
- ✗ Đã khóa (Locked)

### 3.3 Thời Hạn Đổi Vé
- **Trước 72 giờ**: Được phép đổi, phí 10% (tối thiểu 20,000đ)
- **24-72 giờ**: Được phép đổi, phí 20% (tối thiểu 50,000đ)
- **Dưới 24 giờ**: KHÔNG cho phép đổi

### 3.4 Tính Phí Đổi Vé
```
Phí đổi vé = MAX(Giá vé cũ × Tỷ lệ phí, Phí tối thiểu)
Chênh lệch = Giá vé mới - Giá vé cũ
Tổng cần thu = Phí đổi vé + Chênh lệch

Nếu Tổng > 0: Thu thêm tiền
Nếu Tổng < 0: Hoàn lại tiền
```

### 3.5 Phê Duyệt
Giao dịch đổi vé cần phê duyệt nếu:
- Giá trị vé mới > Ngưỡng phê duyệt (mặc định: 5,000,000đ)
- Có thể cấu hình thêm điều kiện khác

## 4. HƯỚNG DẪN SỬ DỤNG GUI

### 4.1 Tìm Kiếm Vé
Có 3 cách tìm kiếm:

1. **Theo mã vé**: Nhập mã vé trực tiếp
2. **Theo số điện thoại**: Tìm tất cả vé của khách hàng có SĐT
3. **Theo CCCD**: Tìm tất cả vé của khách hàng có CCCD

**Cách sử dụng:**
- Chọn radio button tương ứng
- Nhập thông tin tìm kiếm
- Nhấn "Tìm kiếm"

### 4.2 Xem Danh Sách Vé
Bảng hiển thị các cột:
- Mã vé
- Chuyến
- Ga đi/đến
- Giờ đi
- Ghế
- Trạng thái
- **Cho phép đổi**: Hiển thị vé có được phép đổi không

### 4.3 Xem Thông Tin Chi Tiết
Khi chọn một vé, panel bên phải hiển thị:
- Thông tin vé đầy đủ
- Thông tin khách hàng
- Kết quả kiểm tra điều kiện đổi vé

### 4.4 Thực Hiện Đổi Vé

1. Chọn vé cần đổi
2. Nhấn nút "Đổi vé"
3. Trong dialog đổi vé:
   - **Tab "Đổi chuyến tàu"**:
     - Chọn chuyến mới từ dropdown
     - Chọn ghế mới từ sơ đồ (xanh: trống, cam: đã chọn)
   - **Tab "Cập nhật thông tin"**: (Đang phát triển)
   
4. **Panel tài chính** (bên phải) tự động hiển thị:
   - Giá vé cũ
   - Giá vé mới
   - Chênh lệch
   - Phí đổi vé
   - Tổng tiền cần thu/hoàn

5. Nhập ghi chú (tùy chọn)
6. Nhấn "Xác nhận đổi vé"

### 4.5 Kết Quả
Sau khi đổi thành công, hệ thống hiển thị:
- Mã giao dịch
- Phí đổi vé
- Số tiền cần thu hoặc hoàn
- Thông báo nếu cần phê duyệt

## 5. KIẾN TRÚC CODE

### 5.1 Model Classes
```
- VeHistory.java: Lịch sử vé
- GiaoDichDoiVe.java: Giao dịch đổi vé
- AuditLog.java: Log audit
- CauHinhDoiVe.java: Cấu hình tham số
```

### 5.2 DAO Classes
```
- VeHistoryDAO.java
- GiaoDichDoiVeDAO.java
- AuditLogDAO.java
- CauHinhDoiVeDAO.java
```

### 5.3 Service Classes
```
DoiVeService.java - Service chính cho đổi vé:
  + kiemTraChoPhepDoiVe(Ve ve): Kiểm tra điều kiện
  + tinhPhiDoiVe(Ve veCu, Ve veMoi): Tính phí
  + doiVe(...): Thực hiện đổi vé
  + pheDuyetDoiVe(...): Phê duyệt giao dịch
```

### 5.4 GUI Classes
```
PnlDoiVe.java - Panel đổi vé nâng cao:
  - Tìm kiếm đa phương thức
  - Hiển thị thông tin chi tiết
  - Dialog đổi vé với tabs
  - Tính toán tài chính real-time
```

## 6. LUỒNG XỬ LÝ

```
1. Nhân viên tìm vé
   ↓
2. Hệ thống hiển thị danh sách vé + kiểm tra điều kiện
   ↓
3. Nhân viên chọn vé và nhấn "Đổi vé"
   ↓
4. Hệ thống kiểm tra lại điều kiện đổi
   ↓
5. Nhân viên chọn chuyến và ghế mới
   ↓
6. Hệ thống tính phí real-time
   ↓
7. Nhân viên xác nhận
   ↓
8. Hệ thống thực hiện:
   - Lưu lịch sử vé cũ
   - Giải phóng ghế cũ
   - Đặt ghế mới
   - Cập nhật thông tin vé
   - Tạo giao dịch đổi vé
   - Ghi audit log
   ↓
9. Kiểm tra cần phê duyệt?
   - Nếu CÓ: Chuyển trạng thái "CHO_DUYET"
   - Nếu KHÔNG: Trạng thái "HOAN_THANH"
   ↓
10. Thông báo kết quả cho nhân viên
```

## 7. CẤU HÌNH HỆ THỐNG

Các tham số có thể cấu hình trong bảng `CauHinhDoiVe`:

| Mã cấu hình | Mô tả | Giá trị mặc định |
|------------|-------|------------------|
| PHI_DOI_72H | Phí đổi vé trước 72 giờ (%) | 10 |
| PHI_DOI_24_72H | Phí đổi vé 24-72 giờ (%) | 20 |
| PHI_DOI_MIN_72H | Phí đổi vé tối thiểu trước 72h | 20,000 |
| PHI_DOI_MIN_24_72H | Phí đổi vé tối thiểu 24-72h | 50,000 |
| THOI_HAN_DOI_MIN | Thời hạn đổi vé tối thiểu (giờ) | 24 |
| NGUONG_DUYET_DOI_VE | Ngưỡng phê duyệt giao dịch | 5,000,000 |

## 8. TRUY VẤN DỮ LIỆU

### 8.1 Xem Lịch Sử Đổi Vé
```sql
SELECT * FROM VeHistory 
WHERE maVe = 'V001' 
ORDER BY ngayThayDoi DESC;
```

### 8.2 Xem Giao Dịch Cần Phê Duyệt
```sql
SELECT * FROM GiaoDichDoiVe 
WHERE trangThai = 'CHO_DUYET'
ORDER BY ngayDoi DESC;
```

### 8.3 Xem Audit Log của Nhân Viên
```sql
SELECT * FROM AuditLog 
WHERE maNV = 'NV001' AND loaiThaoTac = 'DOI_VE'
ORDER BY thoiGian DESC;
```

### 8.4 Thống Kê Đổi Vé Theo Ngày
```sql
SELECT 
    CAST(ngayDoi AS DATE) as Ngay,
    COUNT(*) as SoLuongGiaoDich,
    SUM(soTienThu) as TongTienThu,
    SUM(soTienHoan) as TongTienHoan
FROM GiaoDichDoiVe
WHERE trangThai = 'HOAN_THANH'
GROUP BY CAST(ngayDoi AS DATE)
ORDER BY Ngay DESC;
```

## 9. XỬ LÝ LỖI

### 9.1 Lỗi Thường Gặp

**Lỗi: "Không thể đổi vé. Phải đổi trước ít nhất 24 giờ"**
- Nguyên nhân: Vé quá gần giờ khởi hành
- Giải pháp: Không thể đổi, chỉ có thể hủy/hoàn vé

**Lỗi: "Chỉ được đổi vé ở trạng thái 'Đã đặt' hoặc 'Đã thanh toán'"**
- Nguyên nhân: Vé đã bị hủy, hoàn, hoặc sử dụng
- Giải pháp: Không thể đổi vé này

**Lỗi: "Ghế mới đã được đặt"**
- Nguyên nhân: Ghế vừa được đặt bởi giao dịch khác
- Giải pháp: Chọn ghế khác

### 9.2 Rollback Transaction
Nếu có lỗi trong quá trình đổi vé, hệ thống sẽ:
- Không cập nhật database
- Không thay đổi trạng thái ghế
- Không tạo giao dịch
- Hiển thị thông báo lỗi chi tiết

## 10. BẢO MẬT

### 10.1 Quyền Truy Cập
- Nhân viên quầy: Có thể đổi vé thông thường
- Quản lý: Có thể phê duyệt đổi vé vượt ngưỡng

### 10.2 Audit Trail
Tất cả thao tác đổi vé được ghi log:
- Người thực hiện
- Thời gian
- Dữ liệu trước/sau thay đổi
- Địa chỉ IP (nếu có)

### 10.3 Không Xóa Dữ Liệu
- VeHistory: Không cho phép xóa
- GiaoDichDoiVe: Không cho phép xóa
- AuditLog: Không cho phép xóa

## 11. TESTING

### 11.1 Test Cases
1. Đổi vé trước 72 giờ → Phí 10%
2. Đổi vé 24-72 giờ → Phí 20%
3. Đổi vé dưới 24 giờ → Từ chối
4. Đổi vé đã hủy → Từ chối
5. Đổi sang ghế đã đặt → Lỗi
6. Đổi vé có giá trị cao → Cần phê duyệt

### 11.2 Test Data
Xem file: `database/ticket_exchange_tables.sql`

## 12. PHÁT TRIỂN TƯƠNG LAI

Các chức năng có thể mở rộng:
- [ ] Đổi loại vé (nâng hạng/hạ hạng)
- [ ] Cập nhật thông tin hành khách (tên, CCCD)
- [ ] Gửi email/SMS xác nhận đổi vé
- [ ] In vé mới sau khi đổi
- [ ] Dashboard thống kê đổi vé
- [ ] API cho mobile app

## 13. LIÊN HỆ HỖ TRỢ

Nếu gặp vấn đề, vui lòng liên hệ:
- Email: support@trainstation.com
- Hotline: 1900-xxxx

---

**Phiên bản**: 1.0  
**Ngày cập nhật**: December 2024  
**Tác giả**: Development Team
