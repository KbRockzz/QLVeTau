# Hướng dẫn thiết lập cơ sở dữ liệu SQL Server

## Yêu cầu

- SQL Server 2019 hoặc cao hơn
- SQL Server Management Studio (SSMS) hoặc Azure Data Studio

## Các bước thiết lập

### 1. Cài đặt SQL Server

Nếu chưa có SQL Server, tải và cài đặt từ: https://www.microsoft.com/en-us/sql-server/sql-server-downloads

Có thể sử dụng SQL Server Express (miễn phí) cho mục đích phát triển.

### 2. Tạo cơ sở dữ liệu

Mở SQL Server Management Studio hoặc Azure Data Studio và thực hiện các bước sau:

1. Kết nối đến SQL Server instance của bạn
2. Mở file `database_schema.sql` trong thư mục gốc của dự án
3. Chạy toàn bộ script để tạo database và các bảng

Hoặc chạy từ command line:

```bash
sqlcmd -S localhost -U sa -P your_password -i database_schema.sql
```

### 3. Cấu hình kết nối

Mở file `src/main/java/com/trainstation/MySQL/ConnectSql.java` và cập nhật thông tin kết nối:

```java
private static final String SERVER = "localhost";  // Địa chỉ SQL Server
private static final String PORT = "1433";         // Cổng SQL Server (mặc định 1433)
private static final String DATABASE = "QLVeTau";  // Tên database
private static final String USERNAME = "sa";       // Tên đăng nhập
private static final String PASSWORD = "your_password"; // Mật khẩu
```

### 4. Kiểm tra kết nối

Sau khi cấu hình, chạy ứng dụng và kiểm tra console. Nếu kết nối thành công, bạn sẽ thấy:

```
Kết nối SQL Server thành công!
```

## Cấu trúc cơ sở dữ liệu

### Bảng chính

1. **Customer** - Thông tin khách hàng
2. **Employee** - Thông tin nhân viên
3. **Account** - Tài khoản đăng nhập
4. **Train** - Thông tin chuyến tàu
5. **CarriageType** - Loại toa tàu
6. **Carriage** - Toa tàu
7. **Seat** - Ghế ngồi
8. **Ticket** - Vé đã đặt

### Dữ liệu mẫu

Script đã bao gồm dữ liệu mẫu để test:

#### Tài khoản đăng nhập:
- **Admin**: username: `admin`, password: `admin123`
- **Nhân viên 1**: username: `nvhung`, password: `hung123`
- **Nhân viên 2**: username: `nvlan`, password: `lan123`

#### Chuyến tàu:
- SE1: Sài Gòn → Hà Nội (850,000 VNĐ)
- SE2: Hà Nội → Sài Gòn (850,000 VNĐ)
- SNT1: Sài Gòn → Nha Trang (350,000 VNĐ)
- HP1: Hà Nội → Hải Phòng (100,000 VNĐ)
- DN1: Sài Gòn → Đà Nẵng (550,000 VNĐ)

#### Loại toa:
- Ghế ngồi cứng (64 ghế)
- Ghế ngồi mềm (48 ghế)
- Giường nằm 6 (36 giường)
- Giường nằm 4 (24 giường)

## Chế độ hoạt động

### Sử dụng SQL Server (Khuyến nghị)

Khi kết nối SQL Server thành công, tất cả dữ liệu sẽ được lưu trữ vào database.

### Chế độ In-Memory (Fallback)

Nếu không thể kết nối SQL Server, ứng dụng sẽ tự động chuyển sang chế độ in-memory với dữ liệu mẫu.

## Xử lý sự cố

### Lỗi kết nối
- Kiểm tra SQL Server đang chạy
- Kiểm tra firewall cho phép kết nối đến port 1433
- Kiểm tra thông tin đăng nhập (username/password)

### Lỗi database không tồn tại
- Chạy lại script `database_schema.sql`
- Kiểm tra quyền tạo database

### Lỗi JDBC Driver
- Maven sẽ tự động tải driver khi build
- Nếu gặp lỗi, chạy: `mvn clean install`

## Backup và Restore

### Backup
```sql
BACKUP DATABASE QLVeTau 
TO DISK = 'C:\Backup\QLVeTau.bak'
WITH FORMAT;
```

### Restore
```sql
RESTORE DATABASE QLVeTau 
FROM DISK = 'C:\Backup\QLVeTau.bak'
WITH REPLACE;
```

## Liên hệ hỗ trợ

Nếu gặp vấn đề, vui lòng tạo issue trên GitHub repository.
