# QLVeTau - Hệ thống Quản lý Vé Tàu

## Mô tả

QLVeTau là một ứng dụng Java Swing để quản lý việc bán vé của một ga tàu hỏa. Ứng dụng được thiết kế dành cho nhân viên bán vé và nhân viên quản lý, được xây dựng theo hướng đối tượng (OOP).

## Tính năng mới nhất ✨

### 🎨 Giao diện trang chủ mới
- ✅ Hình nền nhà ga với gradient và silhouette
- ✅ Lời chào mừng và hiển thị chức vụ
- ✅ Giao diện đơn giản, tập trung

### 🎫 Quản lý vé toàn diện
- ✅ **Dropdown menu "Quản lý vé"** với 3 chức năng:
  - 📝 **Đặt vé**: Giao diện mới với chọn toa và ghế trực quan
  - 💰 **Hoàn vé**: Hoàn lại tiền cho khách hàng
  - 🔄 **Đổi vé**: Thay đổi chuyến tàu và ghế ngồi
- ✅ Bỏ chức năng hủy vé (thay bằng đổi vé)
- ✅ Hiển thị danh sách toa tàu
- ✅ Hiển thị sơ đồ ghế với màu sắc:
  - 🟢 Xanh lá: Ghế trống
  - 🔴 Đỏ: Ghế đã đặt

### 🚂 Quản lý toa và ghế
- ✅ Model mới: CarriageType (Loại toa), Carriage (Toa), Seat (Ghế)
- ✅ DAO hỗ trợ SQL Server cho các entity mới
- ✅ Quản lý trạng thái ghế tự động

### 💾 Kết nối SQL Server
- ✅ SQL Server JDBC Driver (mssql-jdbc 12.4.1)
- ✅ Schema SQL đầy đủ (`database_schema.sql`)
- ✅ DAOs hỗ trợ SQL Server
- ✅ Dữ liệu mẫu tự động
- ✅ Hướng dẫn thiết lập chi tiết (`DATABASE_SETUP.md`)

### 🔐 Phân quyền theo loại nhân viên (maLoai)
- ✅ **LNV01**: Nhân viên thường - Truy cập cơ bản
- ✅ **LNV02**: Nhân viên cao cấp - Truy cập cơ bản
- ✅ **LNV03**: Quản lý - Truy cập đầy đủ (quản lý nhân viên, tài khoản, thống kê)

## Tính năng chi tiết

### Quản lý vé
- ✅ **Đặt vé** - Giao diện mới với chọn toa và ghế
  - Chọn chuyến tàu và khách hàng
  - Hiển thị danh sách toa tàu
  - Sơ đồ ghế với màu trạng thái
  - Đặt vé với 1 click
- ✅ **Hoàn vé** - Hoàn lại tiền cho khách hàng
  - Xem danh sách vé đã đặt
  - Chi tiết thông tin vé
  - Xác nhận hoàn vé
- ✅ **Đổi vé** - Thay đổi chuyến và ghế
  - Chọn vé cần đổi
  - Chọn chuyến tàu mới
  - Chọn toa và ghế mới
  - Tự động cập nhật giá

### Quản lý khách hàng
- ✅ Thêm khách hàng mới
- ✅ Cập nhật thông tin khách hàng
- ✅ Xóa khách hàng - có xác nhận
- ✅ **Tìm kiếm theo số điện thoại** - mới ✨
- ✅ **Làm mới danh sách** - mới ✨

### Quản lý chuyến tàu
- ✅ Thêm chuyến tàu mới
- ✅ Cập nhật thông tin chuyến tàu
- ✅ Xóa chuyến tàu - có xác nhận
- ✅ Xem lịch trình tàu

### Quản lý nhân viên (Chỉ LNV03)
- ✅ Thêm nhân viên mới với loại nhân viên (LNV01/LNV02/LNV03)
- ✅ Cập nhật thông tin nhân viên
- ✅ Xóa nhân viên - có xác nhận
- ✅ Xem danh sách nhân viên với loại

### Quản lý tài khoản (Chỉ LNV03)
- ✅ Tạo tài khoản mới
- ✅ Cập nhật thông tin tài khoản
- ✅ Xóa tài khoản - có xác nhận
- ✅ Phân quyền tự động dựa trên loại nhân viên
- ✅ Bảo vệ tài khoản admin

### Thống kê (Chỉ LNV03)
- ✅ Tổng doanh thu
- ✅ Số vé đã bán
- ✅ Số vé đổi trả/hủy
- ✅ Thống kê theo chuyến tàu

## Công nghệ sử dụng

- **Java 17**: Ngôn ngữ lập trình chính
- **Swing**: Framework để xây dựng giao diện người dùng
- **Maven**: Quản lý dự án và dependencies
- **OOP Design Patterns**: Singleton, DAO Pattern

## Cấu trúc dự án

```
QLVeTau/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/trainstation/
│   │   │       ├── model/          # Các lớp mô hình dữ liệu
│   │   │       │   ├── Account.java
│   │   │       │   ├── Customer.java
│   │   │       │   ├── Employee.java
│   │   │       │   ├── Ticket.java
│   │   │       │   ├── Train.java
│   │   │       │   ├── CarriageType.java  # MỚI
│   │   │       │   ├── Carriage.java      # MỚI
│   │   │       │   └── Seat.java          # MỚI
│   │   │       ├── dao/            # Data Access Objects
│   │   │       │   ├── GenericDAO.java
│   │   │       │   ├── AccountDAO.java
│   │   │       │   ├── CustomerDAO.java
│   │   │       │   ├── EmployeeDAO.java
│   │   │       │   ├── TicketDAO.java
│   │   │       │   ├── TrainDAO.java
│   │   │       │   ├── CarriageTypeDAO.java  # MỚI
│   │   │       │   ├── CarriageDAO.java      # MỚI
│   │   │       │   └── SeatDAO.java          # MỚI
│   │   │       ├── service/        # Business Logic Layer
│   │   │       │   ├── TicketService.java
│   │   │       │   └── StatisticsService.java
│   │   │       ├── gui/            # Giao diện người dùng
│   │   │       │   ├── LoginFrame.java
│   │   │       │   ├── MainFrame.java
│   │   │       │   ├── HomePanel.java       # CẬP NHẬT
│   │   │       │   ├── BookTicketPanel.java # MỚI
│   │   │       │   ├── RefundTicketPanel.java # MỚI
│   │   │       │   ├── ChangeTicketPanel.java # MỚI
│   │   │       │   ├── CustomerPanel.java
│   │   │       │   ├── TrainPanel.java
│   │   │       │   ├── EmployeePanel.java
│   │   │       │   ├── AccountPanel.java
│   │   │       │   └── StatisticsPanel.java
│   │   │       ├── MySQL/
│   │   │       │   └── ConnectSql.java    # Kết nối SQL Server
│   │   │       └── MainApplication.java
│   │   └── resources/
│   └── test/
│       └── java/
├── database_schema.sql      # MỚI - Script SQL
├── DATABASE_SETUP.md        # MỚI - Hướng dẫn DB
├── pom.xml
└── README.md
```

## Cài đặt và chạy

### Yêu cầu hệ thống
- Java JDK 17 hoặc cao hơn
- Apache Maven 3.6+
- SQL Server 2019+ (khuyến nghị) hoặc SQL Server Express

### Hướng dẫn cài đặt

#### 1. Thiết lập cơ sở dữ liệu (SQL Server)

Xem hướng dẫn chi tiết trong file [DATABASE_SETUP.md](DATABASE_SETUP.md)

**Tóm tắt:**
- Cài đặt SQL Server
- Chạy script `database_schema.sql` để tạo database và bảng
- Cấu hình kết nối trong `src/main/java/com/trainstation/MySQL/ConnectSql.java`

```java
private static final String SERVER = "localhost";
private static final String USERNAME = "sa";
private static final String PASSWORD = "your_password";
```

#### 2. Clone repository:
```bash
git clone https://github.com/KbRockzz/QLVeTau.git
cd QLVeTau
```

2. Build project bằng Maven:
```bash
mvn clean compile
mvn clean package
```

3. Chạy ứng dụng:
```bash
java -jar target/QLVeTau-1.0.0.jar
```

Hoặc có thể chạy trực tiếp từ Maven:
```bash
mvn exec:java -Dexec.mainClass="com.trainstation.MainApplication"
```

### Cấu hình SQL Server (Tùy chọn)

Để kết nối với SQL Server, chỉnh sửa file `src/main/java/com/trainstation/MySQL/ConnectSql.java`:

```java
private static final String SERVER = "localhost";
private static final String PORT = "1433";
private static final String DATABASE = "QLVeTau";
private static final String USERNAME = "your_username";
private static final String PASSWORD = "your_password";
```

## Thông tin đăng nhập mặc định

- **Tên đăng nhập**: `admin`
- **Mật khẩu**: `admin123`
- **Vai trò**: ADMIN
- **Loại nhân viên**: LNV03 (Quản lý - có quyền truy cập đầy đủ)

## 📚 Tài liệu hướng dẫn

Dự án có đầy đủ tài liệu tiếng Việt và tiếng Anh:

- **[SUMMARY.md](SUMMARY.md)** - Tổng quan dự án và thống kê
- **[FEATURES_GUIDE.md](FEATURES_GUIDE.md)** - Hướng dẫn sử dụng tính năng (Tiếng Việt)
- **[IMPLEMENTATION_NOTES.md](IMPLEMENTATION_NOTES.md)** - Chi tiết kỹ thuật
- **[APPLICATION_FLOW.md](APPLICATION_FLOW.md)** - Sơ đồ kiến trúc và luồng ứng dụng
- **[DEVELOPER_REFERENCE.md](DEVELOPER_REFERENCE.md)** - Tài liệu tham khảo cho lập trình viên

## Hướng dẫn sử dụng

### 1. Đăng nhập
- Khởi động ứng dụng
- Nhập tên đăng nhập: `admin` và mật khẩu: `admin123`
- Nhấn "Đăng nhập"

### 2. Điều hướng
- Sử dụng thanh điều hướng ở trên cùng để chuyển giữa các trang
- Nhấn **Trang chủ** để quay về màn hình chính
- Các nút điều hướng luôn hiện diện trên mọi trang

### 3. Quản lý khách hàng
- Nhấn nút **Khách hàng** trên thanh điều hướng
- **Tìm kiếm**: Nhập số điện thoại và nhấn "Tìm kiếm"
- **Làm mới**: Nhấn "Làm mới" để hiển thị lại tất cả khách hàng
- Nhập thông tin khách hàng và sử dụng các nút Thêm/Cập nhật/Xóa

### 4. Quản lý chuyến tàu
- Nhấn nút **Chuyến tàu** trên thanh điều hướng
- Nhập thông tin chuyến tàu (định dạng ngày: yyyy-MM-dd HH:mm)
- Sử dụng các nút để thêm/sửa/xóa (có xác nhận)

### 5. Đặt vé (Giao diện mới)
- Nhấn **Quản lý vé ▾** trên thanh điều hướng
- Chọn **Đặt vé**
- Chọn chuyến tàu và khách hàng
- Chọn toa tàu từ danh sách bên trái
- Chọn ghế trống (màu xanh) từ sơ đồ ghế
- Nhấn "Đặt vé"

### 6. Hoàn vé
- Nhấn **Quản lý vé ▾** → **Hoàn vé**
- Chọn vé cần hoàn từ danh sách
- Xem chi tiết vé
- Nhấn "Hoàn vé" và xác nhận

### 7. Đổi vé (Tính năng mới)
- Nhấn **Quản lý vé ▾** → **Đổi vé**
- Chọn vé cần đổi từ danh sách
- Chọn chuyến tàu mới
- Chọn toa và ghế mới
- Nhấn "Đổi vé" để xác nhận

### 8. Quản lý nhân viên (Chỉ LNV03)
- Nhấn **Nhân viên** trên thanh điều hướng
- Chọn **Quản lý nhân viên**
- Thêm nhân viên mới với loại nhân viên (LNV01/LNV02/LNV03)
- Cập nhật hoặc xóa nhân viên (có xác nhận)

### 9. Xem thống kê (Chỉ LNV03)
- Nhấn **Nhân viên** trên thanh điều hướng
- Chọn **Thống kê**
- Xem tổng quan về doanh thu và số lượng vé

### 10. Đăng xuất
- Nhấn nút **Đăng xuất** trên thanh điều hướng
- Xác nhận để đăng xuất

💡 **Xem hướng dẫn chi tiết**: [FEATURES_GUIDE.md](FEATURES_GUIDE.md)

## Kiến trúc và thiết kế

### Kiến trúc 3 lớp (3-Tier Architecture)
1. **Presentation Layer (GUI)**: Các lớp trong package `gui`
2. **Business Logic Layer (Service)**: Các lớp trong package `service`
3. **Data Access Layer (DAO)**: Các lớp trong package `dao`

### Design Patterns
- **Singleton Pattern**: Sử dụng cho tất cả DAO và Service classes
- **DAO Pattern**: Tách biệt logic truy xuất dữ liệu khỏi business logic
- **MVC Pattern**: Model-View-Controller để tổ chức code

## Đóng góp

Mọi đóng góp đều được hoan nghênh. Vui lòng:
1. Fork repository
2. Tạo branch mới (`git checkout -b feature/AmazingFeature`)
3. Commit thay đổi (`git commit -m 'Add some AmazingFeature'`)
4. Push lên branch (`git push origin feature/AmazingFeature`)
5. Tạo Pull Request

## Tác giả

- KbRockzz

## License

Dự án này được phân phối dưới giấy phép MIT.#   Q L T a u H o a  
 