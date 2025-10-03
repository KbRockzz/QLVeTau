# QLVeTau - Hệ thống Quản lý Vé Tàu

## Mô tả

QLVeTau là một ứng dụng Java Swing để quản lý việc bán vé của một ga tàu hỏa. Ứng dụng được thiết kế dành cho nhân viên bán vé và nhân viên quản lý, được xây dựng theo hướng đối tượng (OOP).

## Tính năng

### Quản lý vé
- ✅ Đặt vé tàu
- ✅ Hoàn vé (refund)
- ✅ Hủy vé (cancel)
- ✅ Xem danh sách vé đã đặt

### Quản lý khách hàng
- ✅ Thêm khách hàng mới
- ✅ Cập nhật thông tin khách hàng
- ✅ Xóa khách hàng
- ✅ Tìm kiếm khách hàng

### Quản lý chuyến tàu
- ✅ Thêm chuyến tàu mới
- ✅ Cập nhật thông tin chuyến tàu
- ✅ Xóa chuyến tàu
- ✅ Xem lịch trình tàu

### Quản lý nhân viên (Chỉ dành cho Admin)
- ✅ Thêm nhân viên mới
- ✅ Cập nhật thông tin nhân viên
- ✅ Xóa nhân viên
- ✅ Xem danh sách nhân viên

### Quản lý tài khoản (Chỉ dành cho Admin)
- ✅ Tạo tài khoản mới
- ✅ Cập nhật thông tin tài khoản
- ✅ Xóa tài khoản
- ✅ Phân quyền (Admin/Employee)

### Thống kê (Chỉ dành cho Admin)
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
│   │   │       │   └── Train.java
│   │   │       ├── dao/            # Data Access Objects
│   │   │       │   ├── GenericDAO.java
│   │   │       │   ├── AccountDAO.java
│   │   │       │   ├── CustomerDAO.java
│   │   │       │   ├── EmployeeDAO.java
│   │   │       │   ├── TicketDAO.java
│   │   │       │   └── TrainDAO.java
│   │   │       ├── service/        # Business Logic Layer
│   │   │       │   ├── TicketService.java
│   │   │       │   └── StatisticsService.java
│   │   │       ├── gui/            # Giao diện người dùng
│   │   │       │   ├── LoginFrame.java
│   │   │       │   ├── MainFrame.java
│   │   │       │   ├── CustomerPanel.java
│   │   │       │   ├── TrainPanel.java
│   │   │       │   ├── EmployeePanel.java
│   │   │       │   ├── AccountPanel.java
│   │   │       │   ├── TicketBookingPanel.java
│   │   │       │   └── StatisticsPanel.java
│   │   │       └── MainApplication.java
│   │   └── resources/
│   └── test/
│       └── java/
├── pom.xml
└── README.md
```

## Cài đặt và chạy

### Yêu cầu hệ thống
- Java JDK 17 hoặc cao hơn
- Apache Maven 3.6+

### Hướng dẫn cài đặt

1. Clone repository:
```bash
git clone https://github.com/KbRockzz/QLVeTau.git
cd QLVeTau
```

2. Build project bằng Maven:
```bash
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

## Thông tin đăng nhập mặc định

- **Tên đăng nhập**: `admin`
- **Mật khẩu**: `admin123`
- **Vai trò**: ADMIN

## Hướng dẫn sử dụng

### 1. Đăng nhập
- Khởi động ứng dụng
- Nhập tên đăng nhập và mật khẩu
- Nhấn "Đăng nhập"

### 2. Quản lý khách hàng
- Chọn tab "Khách hàng"
- Nhập thông tin khách hàng
- Nhấn "Thêm" để thêm mới, "Cập nhật" để chỉnh sửa, "Xóa" để xóa

### 3. Quản lý chuyến tàu
- Chọn tab "Chuyến tàu"
- Nhập thông tin chuyến tàu (định dạng ngày: yyyy-MM-dd HH:mm)
- Sử dụng các nút để thêm/sửa/xóa

### 4. Đặt vé
- Chọn tab "Đặt vé"
- Chọn chuyến tàu từ danh sách
- Chọn khách hàng
- Nhập số ghế
- Nhấn "Đặt vé"

### 5. Hoàn/Hủy vé
- Chọn vé trong bảng
- Nhấn "Hoàn vé" hoặc "Hủy vé"

### 6. Xem thống kê (Admin only)
- Chọn tab "Thống kê"
- Xem tổng quan về doanh thu và số lượng vé

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

Dự án này được phân phối dưới giấy phép MIT.