# Hướng dẫn sử dụng QLVeTau

## Mục lục
1. [Giới thiệu](#giới-thiệu)
2. [Đăng nhập](#đăng-nhập)
3. [Quản lý Khách hàng](#quản-lý-khách-hàng)
4. [Quản lý Chuyến tàu](#quản-lý-chuyến-tàu)
5. [Đặt và Quản lý Vé](#đặt-và-quản-lý-vé)
6. [Quản lý Nhân viên](#quản-lý-nhân-viên-admin)
7. [Quản lý Tài khoản](#quản-lý-tài-khoản-admin)
8. [Xem Thống kê](#xem-thống-kê-admin)

## Giới thiệu

QLVeTau là hệ thống quản lý bán vé tàu hỏa với giao diện đồ họa thân thiện. Hệ thống hỗ trợ hai loại tài khoản:
- **EMPLOYEE**: Nhân viên bán vé - có quyền quản lý khách hàng, chuyến tàu và bán vé
- **ADMIN**: Quản lý - có tất cả quyền của EMPLOYEE + quản lý nhân viên, tài khoản và xem thống kê

## Đăng nhập

### Bước 1: Khởi động ứng dụng
```bash
java -jar target/QLVeTau-1.0.0.jar
```

### Bước 2: Nhập thông tin đăng nhập
- **Tài khoản mặc định**:
  - Tên đăng nhập: `admin`
  - Mật khẩu: `admin123`
  - Vai trò: ADMIN

### Bước 3: Nhấn "Đăng nhập"
- Hệ thống sẽ kiểm tra thông tin
- Nếu đúng, chuyển đến màn hình chính
- Nếu sai, hiển thị thông báo lỗi

## Quản lý Khách hàng

### Xem danh sách khách hàng
1. Chọn tab **"👥 Khách hàng"**
2. Bảng hiển thị tất cả khách hàng với các thông tin:
   - Mã khách hàng
   - Họ tên
   - Số điện thoại
   - Email
   - CMND/CCCD
   - Địa chỉ

### Thêm khách hàng mới
1. Nhập thông tin vào các trường:
   - **Mã KH**: Mã định danh duy nhất (VD: KH001)
   - **Họ tên**: Tên đầy đủ của khách hàng
   - **Số điện thoại**: Số điện thoại liên lạc
   - **Email**: Địa chỉ email (không bắt buộc)
   - **CMND/CCCD**: Số chứng minh nhân dân hoặc căn cước công dân
   - **Địa chỉ**: Địa chỉ liên hệ (không bắt buộc)
2. Nhấn nút **"Thêm"**
3. Hệ thống sẽ kiểm tra:
   - Mã KH không được trùng
   - Các trường bắt buộc phải được điền
4. Thông báo kết quả

### Cập nhật thông tin khách hàng
1. Chọn một khách hàng trong bảng (click vào dòng)
2. Thông tin sẽ tự động hiển thị trong form
3. Chỉnh sửa các thông tin cần thiết
4. Nhấn nút **"Cập nhật"**
5. Xác nhận thay đổi

### Xóa khách hàng
1. Chọn khách hàng cần xóa trong bảng
2. Nhấn nút **"Xóa"**
3. Xác nhận trong hộp thoại
4. Khách hàng sẽ bị xóa khỏi hệ thống

### Làm mới form
- Nhấn nút **"Làm mới"** để xóa toàn bộ dữ liệu trong form và bỏ chọn khách hàng

## Quản lý Chuyến tàu

### Xem danh sách chuyến tàu
1. Chọn tab **"🚆 Chuyến tàu"**
2. Bảng hiển thị tất cả chuyến tàu với:
   - Mã tàu
   - Tên tàu
   - Ga đi
   - Ga đến
   - Giờ đi
   - Giờ đến
   - Tổng số chỗ
   - Chỗ còn trống
   - Giá vé

### Thêm chuyến tàu mới
1. Nhập đầy đủ thông tin:
   - **Mã tàu**: Mã định danh (VD: SE1, SE2)
   - **Tên tàu**: Tên chuyến tàu (VD: Thống Nhất)
   - **Ga đi**: Ga xuất phát (VD: Sài Gòn)
   - **Ga đến**: Ga đích (VD: Hà Nội)
   - **Giờ đi**: Định dạng `yyyy-MM-dd HH:mm` (VD: 2024-12-20 19:30)
   - **Giờ đến**: Định dạng `yyyy-MM-dd HH:mm` (VD: 2024-12-21 04:00)
   - **Tổng số chỗ**: Số chỗ ngồi (VD: 200)
   - **Giá vé**: Giá tiền (VD: 850000)
2. Nhấn **"Thêm"**
3. Hệ thống kiểm tra và thêm chuyến tàu

### Cập nhật chuyến tàu
1. Chọn chuyến tàu trong bảng
2. Chỉnh sửa thông tin
3. Nhấn **"Cập nhật"**

### Xóa chuyến tàu
1. Chọn chuyến tàu
2. Nhấn **"Xóa"**
3. Xác nhận

**Lưu ý**: Khi có vé được đặt, số chỗ trống sẽ tự động giảm.

## Đặt và Quản lý Vé

### Xem danh sách vé
1. Chọn tab **"🎫 Đặt vé"**
2. Bảng hiển thị tất cả vé đã đặt với:
   - Mã vé
   - Mã tàu
   - Mã khách hàng
   - Mã nhân viên
   - Ngày đặt
   - Số ghế
   - Giá vé
   - Trạng thái (BOOKED, REFUNDED, CANCELLED)

### Đặt vé mới
1. Chọn **chuyến tàu** từ dropdown list
   - Dropdown hiển thị: Mã tàu - Tên tàu (Ga đi → Ga đến)
2. Chọn **khách hàng** từ dropdown list
   - Dropdown hiển thị: Mã KH - Họ tên
3. Nhập **số ghế** (VD: A1, B5, C10)
4. Nhấn **"Đặt vé"**
5. Hệ thống sẽ:
   - Kiểm tra chỗ trống
   - Tạo mã vé tự động
   - Giảm số chỗ trống của tàu
   - Hiển thị mã vé đã đặt

**Lưu ý**: 
- Nhân viên đặt vé sẽ được ghi nhận tự động
- Giá vé lấy từ giá của chuyến tàu
- Không thể đặt vé nếu tàu hết chỗ

### Hoàn vé (Refund)
1. Chọn vé cần hoàn trong bảng
2. Nhấn **"Hoàn vé"**
3. Xác nhận trong hộp thoại
4. Hệ thống sẽ:
   - Đổi trạng thái vé thành REFUNDED
   - Tăng số chỗ trống của tàu lên 1

**Điều kiện**: Chỉ hoàn được vé có trạng thái BOOKED

### Hủy vé (Cancel)
1. Chọn vé cần hủy trong bảng
2. Nhấn **"Hủy vé"**
3. Xác nhận
4. Hệ thống sẽ:
   - Đổi trạng thái vé thành CANCELLED
   - Tăng số chỗ trống của tàu lên 1

**Điều kiện**: Chỉ hủy được vé có trạng thái BOOKED

### Làm mới dữ liệu
- Nhấn **"Làm mới"** để cập nhật danh sách vé, chuyến tàu và khách hàng

## Quản lý Nhân viên (ADMIN)

Tab này chỉ hiển thị với tài khoản ADMIN.

### Xem danh sách nhân viên
1. Chọn tab **"👤 Nhân viên"**
2. Bảng hiển thị:
   - Mã nhân viên
   - Họ tên
   - Số điện thoại
   - Email
   - Chức vụ
   - Ngày vào làm
   - Lương

### Thêm nhân viên mới
1. Nhập thông tin:
   - **Mã NV**: Mã định danh (VD: EMP001)
   - **Họ tên**: Tên đầy đủ
   - **Số điện thoại**: Số liên lạc
   - **Email**: Địa chỉ email
   - **Chức vụ**: Vị trí công việc (VD: Nhân viên bán vé, Quản lý)
   - **Ngày vào**: Định dạng `yyyy-MM-dd` (VD: 2024-01-15)
   - **Lương**: Mức lương (VD: 10000000)
2. Nhấn **"Thêm"**

### Cập nhật/Xóa nhân viên
- Tương tự như quản lý khách hàng
- Chọn nhân viên → Chỉnh sửa → **"Cập nhật"** hoặc **"Xóa"**

## Quản lý Tài khoản (ADMIN)

Tab này chỉ hiển thị với tài khoản ADMIN.

### Xem danh sách tài khoản
1. Chọn tab **"🔐 Tài khoản"**
2. Bảng hiển thị:
   - Tên đăng nhập
   - Vai trò (ADMIN/EMPLOYEE)
   - Mã nhân viên
   - Trạng thái (Kích hoạt/Vô hiệu hóa)

### Thêm tài khoản mới
1. Nhập thông tin:
   - **Tên đăng nhập**: Username duy nhất
   - **Mật khẩu**: Password để đăng nhập
   - **Vai trò**: Chọn EMPLOYEE hoặc ADMIN
   - **Mã nhân viên**: Mã nhân viên liên kết
   - **Trạng thái**: Tick để kích hoạt
2. Nhấn **"Thêm"**

### Cập nhật tài khoản
1. Chọn tài khoản trong bảng
2. Chỉnh sửa thông tin:
   - Có thể đổi mật khẩu
   - Thay đổi vai trò
   - Bật/tắt trạng thái hoạt động
3. Nhấn **"Cập nhật"**

### Xóa tài khoản
1. Chọn tài khoản cần xóa
2. Nhấn **"Xóa"**
3. Xác nhận

**Lưu ý**: 
- Không thể xóa tài khoản admin mặc định
- Tài khoản bị vô hiệu hóa không thể đăng nhập

## Xem Thống kê (ADMIN)

Tab này chỉ hiển thị với tài khoản ADMIN.

### Truy cập thống kê
1. Chọn tab **"📊 Thống kê"**
2. Màn hình hiển thị:

### Tổng quan
- **💰 Tổng doanh thu**: Tổng tiền từ vé đã bán (trạng thái BOOKED)
- **🎫 Vé đã bán**: Số lượng vé có trạng thái BOOKED
- **↩️ Vé hoàn trả**: Số lượng vé có trạng thái REFUNDED
- **❌ Vé hủy**: Số lượng vé có trạng thái CANCELLED

### Thống kê theo chuyến tàu
Bảng hiển thị số lượng vé đã bán cho từng chuyến tàu

### Làm mới thống kê
- Nhấn nút **"🔄 Làm mới"** để cập nhật số liệu mới nhất

## Dữ liệu Mẫu

Hệ thống được khởi tạo với dữ liệu mẫu:

### Tài khoản
- admin / admin123 (ADMIN)

### Nhân viên
- EMP001 - Nguyễn Văn Admin (Quản lý)
- EMP002 - Trần Thị Bình (Nhân viên bán vé)
- EMP003 - Lê Văn Cường (Nhân viên bán vé)

### Khách hàng
- KH001 - Phạm Minh Anh
- KH002 - Võ Thị Mai
- KH003 - Hoàng Văn Nam

### Chuyến tàu
- SE1 - Thống Nhất (Sài Gòn → Hà Nội): 850,000 VNĐ
- SE2 - Thống Nhất (Hà Nội → Sài Gòn): 850,000 VNĐ
- SNT1 - Sài Gòn - Nha Trang: 350,000 VNĐ
- HP1 - Hà Nội - Hải Phòng: 100,000 VNĐ
- DN1 - Sài Gòn - Đà Nẵng: 550,000 VNĐ

## Lưu ý quan trọng

1. **Lưu trữ dữ liệu**: Dữ liệu được lưu trong bộ nhớ (in-memory), không lưu vào file. Khi tắt ứng dụng, dữ liệu sẽ mất.

2. **Định dạng ngày giờ**:
   - Ngày: `yyyy-MM-dd` (VD: 2024-12-20)
   - Ngày giờ: `yyyy-MM-dd HH:mm` (VD: 2024-12-20 19:30)

3. **Quyền truy cập**:
   - EMPLOYEE: Quản lý khách hàng, chuyến tàu, đặt vé
   - ADMIN: Tất cả quyền + quản lý nhân viên, tài khoản, xem thống kê

4. **Đăng xuất**: Chọn menu **Hệ thống → Đăng xuất**

5. **Thoát ứng dụng**: Chọn menu **Hệ thống → Thoát**

## Xử lý lỗi thường gặp

### Lỗi đăng nhập
- Kiểm tra tên đăng nhập và mật khẩu
- Đảm bảo tài khoản đang ở trạng thái kích hoạt

### Không đặt được vé
- Kiểm tra chuyến tàu còn chỗ trống
- Đảm bảo đã chọn khách hàng và chuyến tàu

### Không hoàn/hủy được vé
- Chỉ hoàn/hủy được vé có trạng thái BOOKED
- Vé đã hoàn hoặc hủy không thể thay đổi

### Lỗi định dạng dữ liệu
- Kiểm tra định dạng ngày giờ
- Đảm bảo số liệu nhập vào đúng kiểu (số cho giá vé, lương, số chỗ)

## Hỗ trợ

Nếu gặp vấn đề, vui lòng liên hệ:
- Email: support@trainstation.com
- GitHub Issues: https://github.com/KbRockzz/QLVeTau/issues
