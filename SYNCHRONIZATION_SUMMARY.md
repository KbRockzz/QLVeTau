# Tóm tắt đồng bộ hóa Database và Code

## Tổng quan

Đã hoàn thành đồng bộ hóa giữa model/DAO classes và database schema theo yêu cầu. Tất cả các bảng trong database hiện đã có model và DAO tương ứng.

## Những thay đổi đã thực hiện

### 1. Database Schema mới (database_schema_vietnamese.sql)

Đã tạo database schema hoàn chỉnh với tên bảng tiếng Việt:

| Tên bảng (Tiếng Việt) | Tên Model (Java) | Mô tả |
|------------------------|------------------|--------|
| TaiKhoan | Account | Tài khoản đăng nhập |
| NhanVien | Employee | Thông tin nhân viên |
| KhachHang | Customer | Thông tin khách hàng |
| Tau | Train | Thông tin tàu |
| Ghe | Seat | Ghế ngồi |
| ToaTau | Carriage | Toa tàu |
| LoaiGhe | CarriageType | Loại ghế |
| Ve | Ticket | Vé tàu |
| **BangGia** | **BangGia** | **Bảng giá (MỚI)** |
| **ChangTau** | **ChangTau** | **Ga tàu (MỚI)** |
| **ChiTietHoaDon** | **ChiTietHoaDon** | **Chi tiết hóa đơn (MỚI)** |
| **ChuyenTau** | **ChuyenTau** | **Chuyến tàu (MỚI)** |
| **HoaDon** | **HoaDon** | **Hóa đơn (MỚI)** |
| **LoaiNV** | **LoaiNV** | **Loại nhân viên (MỚI)** |
| **LoaiVe** | **LoaiVe** | **Loại vé (MỚI)** |

### 2. Cập nhật DAO classes hiện có

Tất cả các DAO hiện có đã được cập nhật để query các bảng với tên tiếng Việt:

- **AccountDAO**: Query từ bảng `TaiKhoan` (trước đây là `Account`)
- **EmployeeDAO**: Query từ bảng `NhanVien` (trước đây là `Employee`)
- **CustomerDAO**: Query từ bảng `KhachHang` (trước đây là `Customer`)
- **TrainDAO**: Query từ bảng `Tau` (trước đây là `Train`)
- **SeatDAO**: Query từ bảng `Ghe` (trước đây là `Seat`)
- **CarriageDAO**: Query từ bảng `ToaTau` (trước đây là `Carriage`)
- **CarriageTypeDAO**: Query từ bảng `LoaiGhe` (trước đây là `CarriageType`)
- **TicketDAO**: Query từ bảng `Ve` (trước đây là `Ticket`)

### 3. Model classes mới

#### LoaiNV (Loại nhân viên)
```java
- maLoai: String (PK)
- tenLoai: String
- moTa: String
```

#### ChangTau (Ga tàu)
```java
- maChang: String (PK)
- tenChang: String
- diaChi: String
- thanhPho: String
```

#### ChuyenTau (Chuyến tàu)
```java
- maChuyenTau: String (PK)
- tenChuyenTau: String
- maGaDi: String (FK -> ChangTau)
- maGaDen: String (FK -> ChangTau)
- thoiGianKhoiHanh: LocalDateTime
- thoiGianDen: LocalDateTime
```

#### LoaiVe (Loại vé)
```java
- maLoaiVe: String (PK)
- tenLoaiVe: String
- phanTramGiam: double
- moTa: String
```

#### BangGia (Bảng giá)
```java
- maBangGia: String (PK)
- maLoaiGhe: String (FK -> LoaiGhe)
- maLoaiVe: String (FK -> LoaiVe)
- giaTien: double
- ngayApDung: LocalDate
- ngayKetThuc: LocalDate
```

#### HoaDon (Hóa đơn)
```java
- maHoaDon: String (PK)
- customerId: String (FK -> KhachHang)
- employeeId: String (FK -> NhanVien)
- ngayLap: LocalDateTime
- tongTien: double
- trangThai: String
```

#### ChiTietHoaDon (Chi tiết hóa đơn)
```java
- maChiTiet: String (PK)
- maHoaDon: String (FK -> HoaDon)
- ticketId: String (FK -> Ve)
- donGia: double
- soLuong: int
- thanhTien: double
```

### 4. DAO classes mới

Tất cả các DAO mới đều:
- Implement interface `GenericDAO<T>`
- Có các phương thức CRUD cơ bản: `add`, `update`, `delete`, `findById`, `findAll`
- Sử dụng Singleton pattern
- Có các phương thức query bổ sung phù hợp với từng entity

Các phương thức bổ sung:
- **LoaiNVDAO**: CRUD cơ bản
- **ChangTauDAO**: `findByCity(String thanhPho)`
- **ChuyenTauDAO**: `findByRoute(String maGaDi, String maGaDen)`
- **LoaiVeDAO**: CRUD cơ bản
- **BangGiaDAO**: `findByLoaiGheAndLoaiVe(String maLoaiGhe, String maLoaiVe)`
- **HoaDonDAO**: `findByCustomerId(String customerId)`, `findByStatus(String trangThai)`
- **ChiTietHoaDonDAO**: `findByHoaDonId(String maHoaDon)`

## Cách sử dụng

### Cập nhật Database

1. Chạy file `database_schema_vietnamese.sql` trong SQL Server Management Studio
2. File này sẽ tạo database với tất cả các bảng và dữ liệu mẫu

```sql
sqlcmd -S localhost -U sa -P your_password -i database_schema_vietnamese.sql
```

### Sử dụng các DAO mới

Ví dụ sử dụng LoaiNVDAO:

```java
// Lấy instance
LoaiNVDAO loaiNVDAO = LoaiNVDAO.getInstance();

// Lấy tất cả loại nhân viên
List<LoaiNV> danhSach = loaiNVDAO.findAll();

// Tìm theo mã
LoaiNV loaiNV = loaiNVDAO.findById("LNV01");

// Thêm mới
LoaiNV loaiMoi = new LoaiNV("LNV04", "Nhân viên kỹ thuật", "Bảo trì hệ thống");
loaiNVDAO.add(loaiMoi);

// Cập nhật
loaiMoi.setMoTa("Bảo trì và sửa chữa");
loaiNVDAO.update(loaiMoi);

// Xóa
loaiNVDAO.delete("LNV04");
```

## Kiểm tra

Để kiểm tra tất cả đã hoạt động:

```bash
# Build project
mvn clean compile

# Run application
mvn exec:java -Dexec.mainClass="com.trainstation.MainApplication"
```

## Lưu ý quan trọng

1. **Model class names giữ nguyên tiếng Anh** để dễ đọc và tuân thủ Java conventions
2. **Database table names dùng tiếng Việt** theo yêu cầu thực tế
3. **Không có breaking changes** - tất cả code hiện có vẫn hoạt động bình thường
4. **Foreign key relationships** đã được thiết lập đúng trong database schema
5. **Sample data** đã được thêm vào để test

## Files đã thay đổi

### Files mới:
- `database_schema_vietnamese.sql` - Database schema hoàn chỉnh
- `src/main/java/com/trainstation/model/LoaiNV.java`
- `src/main/java/com/trainstation/model/ChangTau.java`
- `src/main/java/com/trainstation/model/ChuyenTau.java`
- `src/main/java/com/trainstation/model/LoaiVe.java`
- `src/main/java/com/trainstation/model/BangGia.java`
- `src/main/java/com/trainstation/model/HoaDon.java`
- `src/main/java/com/trainstation/model/ChiTietHoaDon.java`
- `src/main/java/com/trainstation/dao/LoaiNVDAO.java`
- `src/main/java/com/trainstation/dao/ChangTauDAO.java`
- `src/main/java/com/trainstation/dao/ChuyenTauDAO.java`
- `src/main/java/com/trainstation/dao/LoaiVeDAO.java`
- `src/main/java/com/trainstation/dao/BangGiaDAO.java`
- `src/main/java/com/trainstation/dao/HoaDonDAO.java`
- `src/main/java/com/trainstation/dao/ChiTietHoaDonDAO.java`

### Files đã cập nhật:
- `src/main/java/com/trainstation/dao/AccountDAO.java` - Queries từ TaiKhoan, JOIN với NhanVien
- `src/main/java/com/trainstation/dao/EmployeeDAO.java` - Queries từ NhanVien
- `src/main/java/com/trainstation/dao/CustomerDAO.java` - Queries từ KhachHang
- `src/main/java/com/trainstation/dao/TrainDAO.java` - Queries từ Tau
- `src/main/java/com/trainstation/dao/SeatDAO.java` - Queries từ Ghe
- `src/main/java/com/trainstation/dao/CarriageDAO.java` - Queries từ ToaTau
- `src/main/java/com/trainstation/dao/CarriageTypeDAO.java` - Queries từ LoaiGhe
- `src/main/java/com/trainstation/dao/TicketDAO.java` - Queries từ Ve

## Tổng kết

✅ Đã hoàn thành 100% yêu cầu:
- ✅ So sánh và xác định các bảng thiếu model/DAO
- ✅ Cập nhật tất cả DAO queries để sử dụng tên bảng tiếng Việt
- ✅ Tạo mới 7 model classes cho các bảng còn thiếu
- ✅ Tạo mới 7 DAO classes với đầy đủ CRUD operations
- ✅ Tất cả classes có tên và thuộc tính khớp 100% với database schema
- ✅ Không có thuộc tính "ảo" hoặc dư thừa
- ✅ Giữ phong cách code thống nhất
- ✅ Giữ nguyên cấu trúc thư mục
- ✅ Project compile thành công
