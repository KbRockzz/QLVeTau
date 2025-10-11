# Hướng dẫn sử dụng các tính năng mới

## Thanh điều hướng

### Cấu trúc thanh điều hướng
Thanh điều hướng xuất hiện ở trên cùng của mọi trang trong hệ thống, bao gồm:

1. **Trang chủ** - Màn hình chào mừng với truy cập nhanh
2. **Vé** - Quản lý đặt vé, hoàn vé, hủy vé
3. **Khách hàng** - Quản lý thông tin khách hàng và tìm kiếm
4. **Chuyến tàu** - Quản lý lịch trình chuyến tàu
5. **Nhân viên** (dropdown - chỉ quản lý)
   - Quản lý nhân viên
   - Thống kê
6. **Tài khoản** (chỉ quản lý)
7. **Đăng xuất** (có xác nhận)
8. **Tên nhân viên** (góc phải)

### Phân quyền truy cập

#### Tất cả nhân viên có thể:
- Xem trang chủ
- Đặt vé, hoàn vé, hủy vé
- Quản lý khách hàng
- Xem và quản lý chuyến tàu

#### Chỉ quản lý (maLoai = LNV03) có thể:
- Quản lý nhân viên
- Quản lý tài khoản hệ thống
- Xem báo cáo thống kê

## Loại nhân viên (maLoai)

Hệ thống hỗ trợ 3 loại nhân viên:

- **LNV01**: Nhân viên thường
- **LNV02**: Nhân viên cao cấp
- **LNV03**: Quản lý (có quyền truy cập đầy đủ)

## Tính năng tìm kiếm khách hàng

### Tìm kiếm theo số điện thoại:
1. Vào trang **Khách hàng**
2. Nhập số điện thoại (có thể nhập một phần)
3. Nhấn nút **Tìm kiếm**
4. Bảng sẽ chỉ hiển thị khách hàng có số điện thoại khớp
5. Nhấn **Làm mới** để trở lại hiển thị toàn bộ khách hàng

### Ví dụ:
- Nhập "0911" sẽ tìm tất cả số điện thoại có chứa "0911"
- Nhập "111111" sẽ tìm số có chứa "111111"

## Xác nhận thao tác

Tất cả các thao tác quan trọng đều yêu cầu xác nhận:

### Xác nhận khi đăng xuất:
- Nhấn nút **Đăng xuất**
- Hộp thoại xuất hiện: "Bạn có chắc muốn đăng xuất?"
- Chọn **Yes** để đăng xuất, **No** để hủy

### Xác nhận khi xóa:
Các thao tác xóa sau đây đều có xác nhận:
- Xóa khách hàng
- Xóa nhân viên
- Xóa chuyến tàu
- Xóa tài khoản
- Hoàn vé
- Hủy vé

## Kết nối SQL Server

### Cấu hình kết nối
File: `src/main/java/com/trainstation/MySQL/ConnectSql.java`

Các tham số cần cấu hình:
```java
private static final String SERVER = "localhost";
private static final String PORT = "1433";
private static final String DATABASE = "QLVeTau";
private static final String USERNAME = "sa";
private static final String PASSWORD = "your_password";
```

### Sử dụng:
```java
// Lấy instance
ConnectSql connectSql = ConnectSql.getInstance();

// Lấy kết nối
Connection conn = connectSql.getConnection();

// Kiểm tra kết nối
boolean isConnected = connectSql.testConnection();

// Đóng kết nối
connectSql.closeConnection();
```

## Đăng nhập

### Tài khoản mặc định:
- **Tên đăng nhập**: admin
- **Mật khẩu**: admin123
- **Loại nhân viên**: LNV03 (Quản lý)

Tài khoản admin có quyền truy cập đầy đủ vào tất cả các tính năng.

## Giao diện trang chủ

Trang chủ hiển thị:
1. Tiêu đề hệ thống
2. Lời chào và tên nhân viên
3. Vai trò của nhân viên
4. Các nút truy cập nhanh đến các chức năng chính
5. Thông tin bản quyền

## Quản lý nhân viên

### Thêm nhân viên mới:
1. Điền đầy đủ thông tin
2. Chọn **Loại nhân viên** (LNV01, LNV02, hoặc LNV03)
3. Nhấn **Thêm**

### Cập nhật nhân viên:
1. Chọn nhân viên trong bảng
2. Chỉnh sửa thông tin
3. Có thể thay đổi loại nhân viên
4. Nhấn **Cập nhật**

### Xóa nhân viên:
1. Chọn nhân viên
2. Nhấn **Xóa**
3. Xác nhận trong hộp thoại

## Quản lý tài khoản

### Tạo tài khoản mới:
1. Nhập tên đăng nhập
2. Nhập mật khẩu
3. Chọn vai trò (EMPLOYEE hoặc ADMIN)
4. Nhập mã nhân viên liên kết
5. Chọn trạng thái kích hoạt
6. Nhấn **Thêm**

**Lưu ý**: Loại nhân viên (maLoai) sẽ tự động được lấy từ thông tin nhân viên.

## Lưu ý quan trọng

1. **Không thể xóa tài khoản admin** - Hệ thống sẽ chặn thao tác này
2. **Chỉ quản lý mới thấy menu Nhân viên** - Nếu không phải LNV03, menu sẽ bị ẩn
3. **Truy cập trái phép sẽ bị chặn** - Cố gắng vào trang quản lý khi không phải LNV03 sẽ hiển thị thông báo lỗi
4. **Tất cả thao tác xóa đều có xác nhận** - Không thể xóa nhầm do yêu cầu xác nhận

## Hỗ trợ

Nếu gặp vấn đề:
1. Kiểm tra quyền truy cập (maLoai)
2. Đảm bảo đã đăng nhập đúng tài khoản
3. Kiểm tra cấu hình SQL Server (nếu sử dụng database)
4. Xem log trong console để biết chi tiết lỗi

## Các màn hình chính

1. **LoginFrame** - Đăng nhập
2. **MainFrame** - Khung chính với navigation bar
3. **HomePanel** - Trang chủ
4. **TicketBookingPanel** - Quản lý vé
5. **CustomerPanel** - Quản lý khách hàng (có tìm kiếm)
6. **TrainPanel** - Quản lý chuyến tàu
7. **EmployeePanel** - Quản lý nhân viên (chỉ LNV03)
8. **AccountPanel** - Quản lý tài khoản (chỉ LNV03)
9. **StatisticsPanel** - Thống kê (chỉ LNV03)
