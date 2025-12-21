# HƯỚNG DẪN SỬ DỤNG HỆ THỐNG QUẢN LÝ VÉ TÀU - QLVeTau

## 📋 Mục lục

1. [Giới thiệu tổng quan](#1-giới-thiệu-tổng-quan)
2. [Cài đặt và khởi chạy](#2-cài-đặt-và-khởi-chạy)
3. [Đăng nhập hệ thống](#3-đăng-nhập-hệ-thống)
4. [Giao diện chính](#4-giao-diện-chính)
5. [Quản lý khách hàng](#5-quản-lý-khách-hàng)
6. [Quản lý chuyến tàu](#6-quản-lý-chuyến-tàu)
7. [Quản lý vé](#7-quản-lý-vé)
8. [Quản lý hóa đơn](#8-quản-lý-hóa-đơn)
9. [Quản lý nhân viên](#9-quản-lý-nhân-viên)
10. [Quản lý tài khoản](#10-quản-lý-tài-khoản)
11. [Thống kê doanh thu](#11-thống-kê-doanh-thu)
12. [Tìm kiếm và tra cứu](#12-tìm-kiếm-và-tra-cứu)
13. [Xử lý lỗi thường gặp](#13-xử-lý-lỗi-thường-gặp)

---

## 1. Giới thiệu tổng quan

### 1.1. Giới thiệu hệ thống

**QLVeTau** là hệ thống quản lý bán vé tàu hỏa chuyên nghiệp được phát triển bằng Java Swing, giúp tự động hóa quy trình bán vé, quản lý khách hàng, chuyến tàu và báo cáo doanh thu.

### 1.2. Đối tượng sử dụng

Hệ thống hỗ trợ 2 loại người dùng chính:

- **👤 Nhân viên bán vé (EMPLOYEE)**: 
  - Quản lý khách hàng
  - Đặt, đổi, hoàn vé
  - Quản lý chuyến tàu
  - Xuất hóa đơn

- **👨‍💼 Quản lý (ADMIN)**:
  - Tất cả quyền của nhân viên
  - Quản lý nhân viên và tài khoản
  - Xem thống kê doanh thu
  - Quản lý dữ liệu đã xóa

### 1.3. Tính năng nổi bật

✅ Giao diện thân thiện, dễ sử dụng  
✅ Quản lý khách hàng toàn diện  
✅ Đặt vé trực quan với sơ đồ ghế  
✅ Đổi và hoàn vé linh hoạt  
✅ Xuất hóa đơn PDF và in vé  
✅ Thanh toán QR Code  
✅ Thống kê doanh thu chi tiết  
✅ Phân quyền rõ ràng theo vai trò  

---

## 2. Cài đặt và khởi chạy

### 2.1. Yêu cầu hệ thống

- **Java**: JDK 17 trở lên
- **Cơ sở dữ liệu**: SQL Server 2019+ hoặc SQL Server Express
- **Maven**: 3.6+ (để build project)
- **Bộ nhớ RAM**: Tối thiểu 4GB
- **Ổ cứng**: 500MB trống

### 2.2. Cài đặt cơ sở dữ liệu

#### Bước 1: Cài đặt SQL Server
- Tải và cài đặt SQL Server từ trang chủ Microsoft
- Hoặc sử dụng SQL Server Express (miễn phí)

#### Bước 2: Tạo cơ sở dữ liệu
```sql
-- Mở SQL Server Management Studio (SSMS)
-- Chạy file database_schema.sql để tạo database và các bảng
```

#### Bước 3: Cấu hình kết nối
Mở file `src/main/java/com/trainstation/MySQL/ConnectSql.java` và chỉnh sửa:

```java
private static final String SERVER = "localhost";
private static final String PORT = "1433";
private static final String DATABASE = "QLTauHoa";
private static final String USERNAME = "sa";
private static final String PASSWORD = "your_password_here";
```

### 2.3. Build và chạy ứng dụng

#### Sử dụng Maven:
```bash
# Clone repository
git clone https://github.com/KbRockzz/QLVeTau.git
cd QLVeTau

# Build project
mvn clean package

# Chạy ứng dụng
java -jar target/QLVeTau-1.0.0.jar
```

#### Chạy trực tiếp từ IDE:
- Mở project trong IntelliJ IDEA hoặc Eclipse
- Chạy class `MainApplication.java`

---

## 3. Đăng nhập hệ thống

### 3.1. Màn hình đăng nhập

Khi khởi động ứng dụng, màn hình đăng nhập sẽ hiển thị với 2 trường:
- **Tên đăng nhập**
- **Mật khẩu**

### 3.2. Tài khoản mặc định

Hệ thống không cung cấp tài khoản quản trị viên sẵn có.

### 3.3. Quy trình đăng nhập

1. **Nhập thông tin**: Điền tên đăng nhập và mật khẩu
2. **Nhấn "Đăng nhập"**: Hệ thống kiểm tra thông tin
3. **Xử lý kết quả**:
   - ✅ Thành công: Chuyển đến màn hình chính
   - ❌ Thất bại: Hiển thị thông báo lỗi

### 3.4. Lưu ý bảo mật

⚠️ **Quan trọng**: Đổi mật khẩu mặc định ngay sau lần đăng nhập đầu tiên  
🔒 Không chia sẻ thông tin đăng nhập với người khác  
📝 Sử dụng mật khẩu mạnh (ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường và số)

---

## 4. Giao diện chính

### 4.1. Thanh điều hướng (Navigation Bar)

Thanh điều hướng màu xanh ở phía trên cùng bao gồm các menu chính:

#### 🏠 Trang chủ
- Hiển thị tổng quan hệ thống
- Lời chào mừng người dùng
- Thông tin vai trò và chức danh

#### 🚂 Chuyến tàu
Menu dropdown với các chức năng:
- **Chuyến tàu**: Quản lý danh sách chuyến tàu
- **Đầu máy**: Quản lý đầu máy tàu
- **Toa tàu**: Quản lý toa tàu
- **Ga tàu**: Quản lý ga tàu
- **Tìm kiếm**: Tìm kiếm chuyến tàu

#### 🎫 Quản lý vé
Menu dropdown với các chức năng:
- **Bảng giá**: Xem và cập nhật bảng giá vé
- **Đặt vé**: Đặt vé mới cho khách hàng
- **Tìm vé**: Tra cứu thông tin vé
- **Đổi vé**: Đổi vé sang chuyến khác
- **Hoàn vé**: Hoàn trả vé cho khách hàng

#### 👥 Khách hàng
Menu dropdown với:
- **Khách hàng**: Quản lý danh sách khách hàng
- **Tìm kiếm khách hàng**: Tìm kiếm theo số điện thoại

#### 👤 Nhân viên (Chỉ Quản lý)
Menu dropdown với:
- **Nhân viên**: Quản lý danh sách nhân viên
- **Tài khoản**: Quản lý tài khoản đăng nhập
- **Tìm kiếm**: Tìm kiếm nhân viên/tài khoản

#### 💰 Hóa đơn
Menu dropdown với:
- **Hóa đơn**: Xem và xuất hóa đơn
- **Tìm kiếm hóa đơn**: Tra cứu hóa đơn
- **Thống kê**: Xem báo cáo doanh thu (chỉ ADMIN)

#### 🗑️ Dữ liệu đã xóa (Chỉ Quản lý)
- Khôi phục dữ liệu đã xóa

#### 🚪 Đăng xuất
- Thoát khỏi tài khoản hiện tại

### 4.2. Vùng làm việc chính

- Hiển thị nội dung tương ứng với menu được chọn
- Bảng dữ liệu, form nhập liệu và các nút chức năng

### 4.3. Thanh trạng thái

- Hiển thị tên nhân viên đang đăng nhập
- Vai trò của người dùng

---

## 5. Quản lý khách hàng

### 5.1. Xem danh sách khách hàng

**Cách truy cập**: Thanh menu → **👥 Khách hàng** → **Khách hàng**

**Thông tin hiển thị**:
- Mã khách hàng
- Tên khách hàng
- Số điện thoại
- Email
- CMND/CCCD
- Địa chỉ

### 5.2. Thêm khách hàng mới

#### Bước 1: Điền thông tin
- **Mã KH**: Mã tự động (VD: KH01, KH02, ...)
- **Tên KH** (*): Họ và tên đầy đủ
- **Số điện thoại** (*): 10 chữ số
- **Email**: Địa chỉ email (không bắt buộc)
- **CMND/CCCD** (*): Số chứng minh thư hoặc căn cước
- **Địa chỉ**: Địa chỉ liên hệ (không bắt buộc)

(*) = Trường bắt buộc

#### Bước 2: Nhấn nút "Thêm"

#### Bước 3: Kiểm tra kết quả
- ✅ Thành công: Khách hàng mới xuất hiện trong bảng
- ❌ Thất bại: Hiển thị thông báo lỗi

### 5.3. Cập nhật thông tin khách hàng

1. **Chọn khách hàng**: Click vào dòng trong bảng
2. **Chỉnh sửa**: Thay đổi thông tin trong form
3. **Lưu thay đổi**: Nhấn nút "Cập nhật"
4. **Xác nhận**: Nhấn "Yes" trong hộp thoại xác nhận

⚠️ **Lưu ý**: Không thể thay đổi mã khách hàng

### 5.4. Xóa khách hàng

1. **Chọn khách hàng**: Click vào dòng cần xóa
2. **Nhấn nút "Xóa"**
3. **Xác nhận**: Đọc kỹ và xác nhận xóa

⚠️ **Cảnh báo**: 
- Xóa khách hàng sẽ không xóa các vé đã đặt
- Dữ liệu đã xóa có thể khôi phục từ menu "Dữ liệu đã xóa"

### 5.5. Tìm kiếm khách hàng

**Cách 1: Tìm theo số điện thoại**
1. Truy cập: **👥 Khách hàng** → **Tìm kiếm khách hàng**
2. Nhập số điện thoại vào ô tìm kiếm
3. Nhấn "Tìm kiếm"
4. Kết quả hiển thị trong bảng

**Cách 2: Tìm trực tiếp trong danh sách**
- Sử dụng thanh tìm kiếm trên bảng (nếu có)
- Cuộn danh sách để tìm

### 5.6. Làm mới danh sách

- Nhấn nút **"Làm mới"** để cập nhật danh sách mới nhất từ database
- Form nhập liệu sẽ được xóa trắng

---

## 6. Quản lý chuyến tàu

### 6.1. Xem danh sách chuyến tàu

**Cách truy cập**: **🚂 Chuyến tàu** → **Chuyến tàu**

**Thông tin hiển thị**:
- Mã chuyến
- Tên chuyến
- Ga đi - Ga đến
- Giờ đi - Giờ đến
- Trạng thái
- Số ghế đã bán

### 6.2. Quản lý ga tàu

#### Thêm ga mới
1. Truy cập: **🚂 Chuyến tàu** → **Ga tàu**
2. Điền thông tin:
   - Mã ga (VD: GA001)
   - Tên ga (VD: Ga Sài Gòn)
   - Địa chỉ
3. Nhấn "Thêm"

#### Cập nhật/Xóa ga
- Chọn ga trong bảng
- Chỉnh sửa thông tin và nhấn "Cập nhật"
- Hoặc nhấn "Xóa" để xóa ga

### 6.3. Quản lý đầu máy

1. Truy cập: **🚂 Chuyến tàu** → **Đầu máy**
2. Quản lý thông tin các đầu máy tàu

### 6.4. Quản lý toa tàu

1. Truy cập: **🚂 Chuyến tàu** → **Toa tàu**
2. Xem và quản lý các toa tàu
3. Cấu hình số ghế cho mỗi toa

### 6.5. Thêm chuyến tàu mới

1. Truy cập: **🚂 Chuyến tàu** → **Chuyến tàu**
2. Điền đầy đủ thông tin:
   - **Mã chuyến**: Tự động hoặc nhập thủ công
   - **Ga đi**: Chọn từ dropdown
   - **Ga đến**: Chọn từ dropdown
   - **Giờ đi**: Chọn ngày giờ
   - **Giờ đến**: Chọn ngày giờ (phải sau giờ đi)
   - **Chặng**: Tự động tính từ ga đi và ga đến
3. Nhấn "Thêm"

### 6.6. Tìm kiếm chuyến tàu

1. Truy cập: **🚂 Chuyến tàu** → **Tìm kiếm**
2. Bộ lọc tìm kiếm:
   - Ga đi
   - Ga đến
   - Ngày đi
   - Trạng thái
3. Nhấn "Tìm kiếm"

---

## 7. Quản lý vé

### 7.1. Đặt vé mới

#### Bước 1: Truy cập chức năng
**🎫 Quản lý vé** → **Đặt vé**

#### Bước 2: Tìm/Thêm khách hàng
- Nhập số điện thoại khách hàng
- Nhấn "Tìm" để tìm khách hàng có sẵn
- Hoặc nhập thông tin khách hàng mới

#### Bước 3: Chọn chuyến tàu
- Chọn ga đi và ga đến
- Chọn ngày đi
- Nhấn "Tìm chuyến"
- Chọn chuyến tàu phù hợp từ danh sách

#### Bước 4: Chọn loại vé
- Người lớn
- Trẻ em
- Sinh viên
- Người cao tuổi

#### Bước 5: Chọn ghế
- Xem sơ đồ toa tàu
- Ghế trống: **🟢 Màu xanh**
- Ghế đã đặt: **🔴 Màu đỏ**
- Ghế đang chọn: **🔵 Màu xanh đậm**
- Click vào ghế để chọn

#### Bước 6: Xác nhận đặt vé
- Kiểm tra thông tin:
  - Thông tin khách hàng
  - Thông tin chuyến tàu
  - Ghế đã chọn
  - Loại vé và giá
- Nhấn "Đặt vé"
- Hệ thống tạo mã vé tự động

#### Bước 7: In vé (tùy chọn)
- Sau khi đặt vé thành công
- Chọn "In vé" để in vé PDF
- Vé được lưu trong thư mục `tickets/`

### 7.2. Đổi vé

#### Điều kiện đổi vé
✅ Vé có trạng thái "Đã thanh toán"  
✅ Đổi trước giờ khởi hành ít nhất 2 giờ  
✅ Ghế mới phải cùng loại với ghế cũ  
✅ Ghế mới phải còn trống  

#### Quy trình đổi vé

**Bước 1**: Truy cập **🎫 Quản lý vé** → **Đổi vé**

**Bước 2**: Tìm vé cần đổi
- Nhập mã vé hoặc số điện thoại
- Chọn vé từ danh sách

**Bước 3**: Chọn chuyến mới
- Chọn chuyến tàu mới
- Xem sơ đồ ghế

**Bước 4**: Chọn ghế mới
- Ghế mới phải cùng loại ghế cũ
- Click vào ghế trống

**Bước 5**: Xác nhận đổi vé
- Xem phí chênh lệch (nếu có)
- Nhấn "Đổi vé"
- Hệ thống tự động:
  - Đánh dấu vé cũ là "Đã đổi"
  - Tạo vé mới
  - Giải phóng ghế cũ
  - Đặt ghế mới

### 7.3. Hoàn vé

#### Điều kiện hoàn vé
✅ Vé có trạng thái "Đã thanh toán"  
✅ Hoàn trước giờ khởi hành ít nhất 24 giờ: 100% giá vé  
✅ Hoàn trước giờ khởi hành từ 12-24 giờ: 50% giá vé  
✅ Hoàn trước giờ khởi hành dưới 12 giờ: 25% giá vé  

#### Quy trình hoàn vé

**Bước 1**: Truy cập **🎫 Quản lý vé** → **Hoàn vé**

**Bước 2**: Tìm vé cần hoàn
- Nhập mã vé hoặc mã hóa đơn
- Chọn vé từ danh sách

**Bước 3**: Yêu cầu hoàn vé
- Nhấn "Yêu cầu hoàn vé"
- Vé chuyển sang trạng thái "Chờ duyệt"

**Bước 4**: Duyệt hoàn vé (Quản lý)
- Nhấn "Chấp nhận" hoặc "Từ chối"
- Nếu chấp nhận:
  - Vé chuyển sang "Đã hoàn"
  - Giải phóng ghế
  - Tính tiền hoàn lại theo quy định

### 7.4. Tìm vé

1. Truy cập: **🎫 Quản lý vé** → **Tìm vé**
2. Tìm kiếm theo:
   - Mã vé
   - Số điện thoại khách hàng
   - Mã chuyến tàu
   - Trạng thái vé
   - Ngày đặt vé
3. Xem chi tiết vé

---

## 8. Quản lý hóa đơn

### 8.1. Xem danh sách hóa đơn

**Cách truy cập**: **💰 Hóa đơn** → **Hóa đơn**

**Thông tin hiển thị**:
- Mã hóa đơn
- Mã khách hàng
- Tên khách hàng
- Số điện thoại
- Ngày lập
- Phương thức thanh toán
- Trạng thái
- Số vé

### 8.2. Xuất hóa đơn

#### Quy trình xuất hóa đơn

**Bước 1**: Chọn hóa đơn trong danh sách

**Bước 2**: Nhấn "Xuất hóa đơn"

**Bước 3**: Chọn phương thức thanh toán (nếu chưa thanh toán)
- Tiền mặt
- Chuyển khoản
- Thẻ

**Bước 4**: Xác nhận
- Hệ thống xuất file PDF
- File được lưu trong thư mục `invoices/`

#### Nội dung hóa đơn
- Thông tin công ty
- Thông tin khách hàng
- Thông tin nhân viên
- Chi tiết các vé
- Tổng tiền
- Mã QR thanh toán

### 8.3. Xem chi tiết hóa đơn

1. Chọn hóa đơn trong bảng
2. Nhấn "Xem chi tiết"
3. Cửa sổ hiển thị:
   - Danh sách vé trong hóa đơn
   - Thông tin từng vé
   - Tổng tiền
   - Nút "In vé" để in từng vé

### 8.4. In vé từ hóa đơn

1. Mở chi tiết hóa đơn
2. Chọn vé cần in
3. Nhấn "In vé"
4. File PDF vé được lưu trong `tickets/`

### 8.5. Thanh toán QR Code

Khi xuất hóa đơn, hệ thống tự động:
- Tạo mã QR thanh toán VietQR
- Thông tin chuyển khoản:
  - Ngân hàng: TPBank (970423)
  - Số tài khoản: 48608112005
  - Số tiền: Tổng tiền hóa đơn
  - Nội dung: Mã hóa đơn
- Mã QR được nhúng trong hóa đơn PDF

### 8.6. Tìm kiếm hóa đơn

1. Truy cập: **💰 Hóa đơn** → **Tìm kiếm hóa đơn**
2. Bộ lọc:
   - Mã hóa đơn
   - Mã khách hàng
   - Số điện thoại
   - Từ ngày - Đến ngày
   - Trạng thái
   - Phương thức thanh toán
3. Nhấn "Tìm kiếm"

---

## 9. Quản lý nhân viên

**⚠️ Chức năng này chỉ dành cho ADMIN**

### 9.1. Xem danh sách nhân viên

**Cách truy cập**: **👤 Nhân viên** → **Nhân viên**

**Thông tin hiển thị**:
- Mã nhân viên
- Tên nhân viên
- Giới tính
- Ngày sinh
- Số điện thoại
- Email
- Địa chỉ
- Chức vụ
- Ngày vào làm
- Trạng thái

### 9.2. Thêm nhân viên mới

1. Điền đầy đủ thông tin:
   - Mã NV: Tự động
   - Tên NV (*)
   - Giới tính
   - Ngày sinh
   - Số điện thoại (*)
   - Email
   - Địa chỉ
   - Chức vụ
   - Ngày vào làm
2. Nhấn "Thêm"

### 9.3. Cập nhật thông tin nhân viên

1. Chọn nhân viên trong bảng
2. Chỉnh sửa thông tin
3. Nhấn "Cập nhật"

### 9.4. Xóa nhân viên

1. Chọn nhân viên
2. Nhấn "Xóa"
3. Xác nhận

⚠️ **Lưu ý**: Không thể xóa nhân viên đang có tài khoản hoạt động

### 9.5. Tìm kiếm nhân viên

1. Truy cập: **👤 Nhân viên** → **Tìm kiếm**
2. Tìm theo:
   - Mã nhân viên
   - Tên nhân viên
   - Số điện thoại
   - Chức vụ

---

## 10. Quản lý tài khoản

**⚠️ Chức năng này chỉ dành cho ADMIN**

### 10.1. Xem danh sách tài khoản

**Cách truy cập**: **👤 Nhân viên** → **Tài khoản**

**Thông tin hiển thị**:
- Tên tài khoản
- Mã nhân viên
- Tên nhân viên
- Vai trò
- Trạng thái

### 10.2. Tạo tài khoản mới

1. Điền thông tin:
   - Tên tài khoản (*): Duy nhất
   - Mật khẩu (*): Ít nhất 8 ký tự
   - Mã nhân viên (*): Chọn từ danh sách
2. Vai trò được tự động gán dựa trên chức vụ nhân viên
3. Nhấn "Thêm"

### 10.3. Đổi mật khẩu

1. Chọn tài khoản cần đổi mật khẩu
2. Nhập mật khẩu mới
3. Nhấn "Cập nhật"

⚠️ **Lưu ý bảo mật**:
- Mật khẩu mạnh: Chữ hoa, chữ thường, số, ký tự đặc biệt
- Độ dài tối thiểu: 8 ký tự
- Không sử dụng thông tin cá nhân

### 10.4. Vô hiệu hóa tài khoản

1. Chọn tài khoản
2. Bỏ tick "Trạng thái hoạt động"
3. Nhấn "Cập nhật"
4. Tài khoản sẽ không thể đăng nhập

### 10.5. Xóa tài khoản

1. Chọn tài khoản
2. Nhấn "Xóa"
3. Xác nhận

⚠️ **Lưu ý**: Không thể xóa tài khoản admin mặc định

---

## 11. Thống kê doanh thu

**⚠️ Chức năng này chỉ dành cho ADMIN**

### 11.1. Báo cáo tổng quan

**Cách truy cập**: **💰 Hóa đơn** → **Thống kê**

**Thông tin hiển thị**:

#### Tổng quan (4 thẻ lớn)
- 💰 **Tổng doanh thu**: Tổng tiền từ vé đã thanh toán
- 🎫 **Vé đã bán**: Số lượng vé đã thanh toán thành công
- 🔄 **Vé đã đổi**: Số lượng vé đã được đổi
- ↩️ **Vé đã hoàn**: Số lượng vé đã hoàn trả

### 11.2. Thống kê theo thời gian

**Bộ lọc thời gian**:
- Từ ngày: Chọn ngày bắt đầu
- Đến ngày: Chọn ngày kết thúc
- Nhấn "Lọc" để xem thống kê theo khoảng thời gian

### 11.3. Báo cáo doanh thu

**Bảng thống kê chi tiết**:
- Mã chuyến tàu
- Tên chuyến
- Chặng (Ga đi - Ga đến)
- Giờ đi
- Số ghế bán được
- Doanh thu

**Tính năng**:
- Sắp xếp theo cột
- Xuất báo cáo Excel (tùy chọn)
- In báo cáo PDF (tùy chọn)

### 11.4. Biểu đồ thống kê

**Biểu đồ cột**:
- Top 5 chuyến tàu có doanh thu cao nhất
- Trục X: Tên chuyến tàu
- Trục Y: Doanh thu (VNĐ)

### 11.5. Làm mới dữ liệu

- Nhấn nút **"🔄 Làm mới"** để cập nhật số liệu mới nhất

---

## 12. Tìm kiếm và tra cứu

### 12.1. Tìm kiếm khách hàng

**Phương pháp 1**: Tìm theo số điện thoại
- Truy cập: **👥 Khách hàng** → **Tìm kiếm khách hàng**
- Nhập số điện thoại (10 chữ số)
- Nhấn "Tìm kiếm"

**Phương pháp 2**: Tìm trong danh sách
- Sử dụng thanh tìm kiếm nhanh
- Lọc theo tên hoặc CMND

### 12.2. Tìm kiếm chuyến tàu

**Cách truy cập**: **🚂 Chuyến tàu** → **Tìm kiếm**

**Bộ lọc**:
- Ga đi (dropdown)
- Ga đến (dropdown)
- Ngày đi (date picker)
- Trạng thái chuyến

**Kết quả hiển thị**:
- Danh sách chuyến tàu phù hợp
- Thông tin chi tiết mỗi chuyến
- Số ghế còn trống

### 12.3. Tìm kiếm vé

**Cách truy cập**: **🎫 Quản lý vé** → **Tìm vé**

**Tìm kiếm theo**:
- Mã vé
- Số điện thoại khách hàng
- Mã chuyến tàu
- Trạng thái vé
- Khoảng thời gian

### 12.4. Tìm kiếm hóa đơn

**Cách truy cập**: **💰 Hóa đơn** → **Tìm kiếm hóa đơn**

**Bộ lọc**:
- Mã hóa đơn
- Mã/Tên khách hàng
- Số điện thoại
- Từ ngày - Đến ngày
- Trạng thái
- Phương thức thanh toán

### 12.5. Tìm kiếm nhân viên/tài khoản

**Cách truy cập**: **👤 Nhân viên** → **Tìm kiếm**

**Tùy chọn**:
- Tab "Nhân viên": Tìm theo mã NV, tên, SĐT
- Tab "Tài khoản": Tìm theo tên tài khoản, mã NV

---

## 13. Xử lý lỗi thường gặp

### 13.1. Lỗi đăng nhập

#### ❌ "Tên đăng nhập hoặc mật khẩu không đúng"
**Nguyên nhân**:
- Sai tên đăng nhập hoặc mật khẩu
- Tài khoản bị vô hiệu hóa

**Giải pháp**:
- Kiểm tra lại thông tin đăng nhập
- Liên hệ quản trị viên nếu quên mật khẩu
- Đảm bảo tài khoản đang hoạt động

#### ❌ "Không thể kết nối đến cơ sở dữ liệu"
**Nguyên nhân**:
- SQL Server chưa khởi động
- Thông tin kết nối sai
- Firewall chặn kết nối

**Giải pháp**:
- Khởi động SQL Server
- Kiểm tra thông tin trong ConnectSql.java
- Tắt firewall hoặc cho phép kết nối port 1433

### 13.2. Lỗi đặt vé

#### ❌ "Ghế đã được đặt"
**Nguyên nhân**: Ghế vừa được người khác đặt

**Giải pháp**:
- Nhấn "Làm mới" để cập nhật trạng thái ghế
- Chọn ghế khác

#### ❌ "Không đủ thông tin khách hàng"
**Nguyên nhân**: Thiếu thông tin bắt buộc

**Giải pháp**:
- Điền đầy đủ: Tên, SĐT, CMND
- Kiểm tra định dạng số điện thoại (10 chữ số)

#### ❌ "Chuyến tàu đã khởi hành"
**Nguyên nhân**: Chuyến tàu đã đi hoặc quá giờ đặt vé

**Giải pháp**:
- Chọn chuyến tàu khác
- Đặt vé trước giờ khởi hành ít nhất 30 phút

### 13.3. Lỗi đổi vé

#### ❌ "Không thể đổi vé này"
**Nguyên nhân**:
- Vé không ở trạng thái "Đã thanh toán"
- Quá thời gian cho phép đổi (2 giờ trước giờ đi)
- Vé đã bị đổi hoặc hoàn trước đó

**Giải pháp**:
- Kiểm tra trạng thái vé
- Đổi trước giờ khởi hành 2 giờ
- Liên hệ quản lý nếu cần hỗ trợ

#### ❌ "Ghế mới không cùng loại với ghế cũ"
**Nguyên nhân**: Đổi sang loại ghế khác (VD: từ VIP sang thường)

**Giải pháp**:
- Chọn ghế cùng loại (cùng toa)
- Nếu muốn nâng hạng, cần hoàn vé và đặt lại

### 13.4. Lỗi hoàn vé

#### ❌ "Không thể hoàn vé"
**Nguyên nhân**:
- Vé không ở trạng thái phù hợp
- Đã quá thời gian hoàn vé

**Giải pháp**:
- Hoàn vé trước giờ khởi hành
- Kiểm tra chính sách hoàn vé

### 13.5. Lỗi xuất hóa đơn

#### ❌ "Không thể tạo file PDF"
**Nguyên nhân**:
- Thiếu quyền ghi file
- Thư mục không tồn tại
- Font tiếng Việt không có

**Giải pháp**:
- Chạy ứng dụng với quyền administrator
- Tạo thư mục `invoices/` và `tickets/`
- Đảm bảo có font Tinos-Regular.ttf trong thư mục `fonts/`

#### ❌ "Lỗi tạo mã QR"
**Nguyên nhân**: Không có kết nối internet

**Giải pháp**:
- Kết nối internet để tạo mã QR từ VietQR API
- Hoặc bỏ qua mã QR, chỉ xuất hóa đơn

### 13.6. Lỗi hiển thị

#### ❌ "Giao diện bị lỗi font, không hiển thị tiếng Việt"
**Giải pháp**:
- Đảm bảo file `fonts/Tinos-Regular.ttf` tồn tại
- Cài đặt font tiếng Việt trên hệ thống

#### ❌ "Bảng không hiển thị dữ liệu"
**Giải pháp**:
- Nhấn nút "Làm mới"
- Kiểm tra kết nối database
- Kiểm tra có dữ liệu trong bảng không

### 13.7. Lỗi khác

#### ❌ "Ứng dụng chạy chậm"
**Giải pháp**:
- Đóng các ứng dụng khác
- Tăng RAM cho Java JVM
- Kiểm tra kết nối mạng đến database

#### ❌ "Mất dữ liệu sau khi tắt ứng dụng"
**Giải pháp**:
- Dữ liệu được lưu trong SQL Server
- Kiểm tra kết nối database
- Đảm bảo SQL Server đang chạy

---

## 📞 Hỗ trợ và liên hệ

### Thông tin liên hệ

- **GitHub Repository**: [https://github.com/KbRockzz/QLVeTau](https://github.com/KbRockzz/QLVeTau)
- **Issues**: [https://github.com/KbRockzz/QLVeTau/issues](https://github.com/KbRockzz/QLVeTau/issues)
- **Email hỗ trợ**: support@qlvetau.com (nếu có)

### Tài liệu tham khảo

- **README.md**: Tổng quan dự án
- **DATABASE_SETUP.md**: Hướng dẫn cài đặt database chi tiết
- **FEATURES_GUIDE.md**: Hướng dẫn tính năng
- **DEVELOPER_REFERENCE.md**: Tài liệu cho lập trình viên

### Đóng góp

Nếu bạn muốn đóng góp cho dự án:
1. Fork repository
2. Tạo branch mới: `git checkout -b feature/TinhNangMoi`
3. Commit thay đổi: `git commit -m 'Thêm tính năng mới'`
4. Push lên branch: `git push origin feature/TinhNangMoi`
5. Tạo Pull Request

---

## 📋 Phụ lục

### Phím tắt hữu ích

| Phím tắt | Chức năng |
|----------|-----------|
| `Alt + H` | Về trang chủ |
| `F5` | Làm mới dữ liệu |
| `Ctrl + F` | Tìm kiếm |
| `Ctrl + N` | Thêm mới |
| `Ctrl + S` | Lưu/Cập nhật |
| `Delete` | Xóa |
| `Esc` | Đóng dialog |

### Định dạng dữ liệu

| Kiểu dữ liệu | Định dạng | Ví dụ |
|--------------|-----------|-------|
| Ngày | `dd/MM/yyyy` | 20/12/2024 |
| Giờ | `HH:mm` | 14:30 |
| Ngày giờ | `dd/MM/yyyy HH:mm` | 20/12/2024 14:30 |
| Số điện thoại | `0XXXXXXXXX` | 0901234567 |
| Tiền tệ | `###,###` VNĐ | 850,000 VNĐ |

### Trạng thái vé

| Trạng thái | Mô tả |
|------------|-------|
| **Đang giữ** | Vé vừa được chọn, chưa thanh toán |
| **Đã thanh toán** | Vé đã thanh toán thành công |
| **Chờ duyệt** | Yêu cầu hoàn vé đang chờ xét duyệt |
| **Đã hoàn** | Vé đã được hoàn trả thành công |
| **Đã đổi** | Vé đã được đổi sang vé khác |
| **Đã kết thúc** | Chuyến tàu đã hoàn thành |

### Phân quyền chi tiết

| Chức năng | Nhân viên | Quản lý |
|-----------|-----------|---------|
| Quản lý khách hàng | ✅ | ✅ |
| Quản lý chuyến tàu | ✅ | ✅ |
| Đặt vé | ✅ | ✅ |
| Đổi vé | ✅ | ✅ |
| Hoàn vé (yêu cầu) | ✅ | ✅ |
| Hoàn vé (duyệt) | ❌ | ✅ |
| Xuất hóa đơn | ✅ | ✅ |
| Quản lý nhân viên | ❌ | ✅ |
| Quản lý tài khoản | ❌ | ✅ |
| Xem thống kê | ❌ | ✅ |
| Khôi phục dữ liệu đã xóa | ❌ | ✅ |

---

**Phiên bản**: 1.0.0  
**Ngày cập nhật**: 21/12/2024  
**Tác giả**: KbRockzz  

---

*📖 Hết hướng dẫn sử dụng. Chúc bạn sử dụng hệ thống hiệu quả!*
