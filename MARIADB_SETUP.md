# Hướng dẫn cài đặt MariaDB cho QLVeTau

Kể từ phiên bản này, **QLVeTau** đã chuyển từ **SQL Server** sang **MariaDB**.  
Dưới đây là hướng dẫn cài đặt và cấu hình đầy đủ để chạy ứng dụng.

---

## 1. Cài đặt MariaDB

### Windows
1. Tải installer từ https://mariadb.org/download/
2. Chạy installer, đặt `root` password (ví dụ: `rootpassword`)
3. Đảm bảo dịch vụ MariaDB chạy trên cổng mặc định **3306**

### Ubuntu / Debian
```bash
sudo apt update
sudo apt install mariadb-server
sudo systemctl start mariadb
sudo mysql_secure_installation
```

### macOS (Homebrew)
```bash
brew install mariadb
brew services start mariadb
```

### Docker (khuyến nghị cho dev)
```bash
docker run --name mariadb-qltauhoa \
  -e MYSQL_ROOT_PASSWORD=rootpassword \
  -e MYSQL_DATABASE=QLTauHoa \
  -p 3306:3306 \
  -d mariadb:latest
```

---

## 2. Tạo database và schema

### Bước 1: Tạo schema
Chạy file `database/mariadb_schema.sql` để tạo toàn bộ cấu trúc bảng:

```bash
mysql -u root -p < database/mariadb_schema.sql
```

Hoặc kết nối trực tiếp rồi chạy:
```sql
SOURCE /đường/dẫn/tới/database/mariadb_schema.sql;
```

### Bước 2: Nạp dữ liệu mẫu
```bash
mysql -u root -p < seed/seed_mariadb.sql
```

---

## 3. Cấu hình kết nối

Chỉnh sửa thông số kết nối trong  
`src/main/java/com/trainstation/MySQL/ConnectSql.java`:

```java
private static final String SERVER   = "localhost";   // host MariaDB
private static final String PORT     = "3306";         // cổng mặc định
private static final String DATABASE = "QLTauHoa";     // tên database
private static final String USERNAME = "root";         // tên user
private static final String PASSWORD = "rootpassword"; // mật khẩu
```

URL kết nối hiện tại:
```
jdbc:mariadb://localhost:3306/QLTauHoa?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh
```

---

## 4. Build và chạy ứng dụng

```bash
# Build (bỏ qua test vì cần kết nối DB)
mvn clean package -DskipTests

# Chạy
java -jar target/QLVeTau-1.0.0.jar
```

---

## 5. Kiểm tra kết nối

Đảm bảo MariaDB đang chạy và user `root` có quyền truy cập:

```sql
-- Kiểm tra database tồn tại
SHOW DATABASES LIKE 'QLTauHoa';

-- Kiểm tra các bảng đã được tạo
USE QLTauHoa;
SHOW TABLES;
```

Kết quả mong đợi — phải thấy đủ các bảng:
```
BangGia
ChangTau
ChiTietChuyenTau
ChiTietHoaDon
ChuyenTau
DauMay
Ga
Ghe
HoaDon
KhachHang
LoaiGhe
LoaiNV
LoaiVe
NhanVien
TaiKhoan
ToaTau
Ve
```

---

## 6. So sánh cấu hình cũ (SQL Server) và mới (MariaDB)

| Thông số        | SQL Server (cũ)                              | MariaDB (mới)                                         |
|-----------------|----------------------------------------------|-------------------------------------------------------|
| Driver class    | `com.microsoft.sqlserver.jdbc.SQLServerDriver` | `org.mariadb.jdbc.Driver`                            |
| URL prefix      | `jdbc:sqlserver://`                          | `jdbc:mariadb://`                                     |
| Cổng mặc định   | 1433                                         | 3306                                                  |
| Dependency      | `com.microsoft.sqlserver:mssql-jdbc`         | `org.mariadb.jdbc:mariadb-java-client`               |
| Kiểu BIT        | `BIT`                                        | `TINYINT(1)`                                          |
| Kiểu chuỗi      | `NVARCHAR`                                   | `VARCHAR` (với `utf8mb4`)                             |
| Lệnh GO         | Có (T-SQL)                                   | Không dùng                                            |
| Điều kiện tạo   | `IF NOT EXISTS (SELECT * FROM sys.objects…)` | `CREATE TABLE IF NOT EXISTS`                          |

---

## 7. Khắc phục sự cố

### Lỗi "Access denied for user 'root'@'localhost'"
```sql
-- Kết nối với tài khoản admin/root rồi cấp quyền
GRANT ALL PRIVILEGES ON QLTauHoa.* TO 'root'@'localhost' IDENTIFIED BY 'rootpassword';
FLUSH PRIVILEGES;
```

### Lỗi "Unknown database 'QLTauHoa'"
Chạy lại bước tạo schema:
```bash
mysql -u root -p < database/mariadb_schema.sql
```

### Lỗi "Communications link failure" (không kết nối được)
- Kiểm tra MariaDB đang chạy: `sudo systemctl status mariadb`
- Kiểm tra cổng 3306 không bị chặn: `telnet localhost 3306`
- Kiểm tra `SERVER`, `PORT` trong `ConnectSql.java` khớp với cấu hình thực tế

---

## 8. Kiến trúc mới (sau khi nâng cấp)

```
GUI (Swing panels)
      │
      ▼
Service layer (iface + impl)
  ├── IKhachHangService / KhachHangServiceImpl
  ├── IChuyenTauService / ChuyenTauServiceImpl
  └── IVeService        / VeServiceImpl
      │
      ▼
DAO / Repository layer
  ├── KhachHangDAO, ChuyenTauDAO, VeDAO, ...
  └── (IKhachHangRepository, IChuyenTauRepository, IVeRepository interfaces)
      │
      ▼
ConnectSql (MariaDB JDBC)
      │
      ▼
MariaDB Database (QLTauHoa)
```

### DTO + Mapper (Jackson)
Các use-case tạo/cập nhật dữ liệu sử dụng DTO và Mapper riêng biệt:

```
CreateKhachHangRequest ──► JacksonKhachHangMapper ──► KhachHang (entity)
KhachHang (entity)     ──► JacksonKhachHangMapper ──► KhachHangDTO (response)
```

Tương tự cho `ChuyenTau` và `Ve`.

---

## Liên kết liên quan

- [README.md](README.md) — Tổng quan dự án
- [QUICKSTART.md](QUICKSTART.md) — Khởi động nhanh
- [database/mariadb_schema.sql](database/mariadb_schema.sql) — Schema MariaDB
- [seed/seed_mariadb.sql](seed/seed_mariadb.sql) — Dữ liệu mẫu MariaDB
