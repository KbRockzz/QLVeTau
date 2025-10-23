# Hướng Dẫn Tìm Kiếm Chuyến Tàu Linh Hoạt

## Tổng Quan

Tính năng tìm kiếm chuyến tàu đã được cập nhật để cho phép tìm kiếm linh hoạt theo bất kỳ tiêu chí nào. Người dùng không cần nhập đầy đủ tất cả các trường.

## Cách Sử Dụng

### 1. Tìm Kiếm Với Tất Cả Tiêu Chí
- **Ga đi:** Chọn ga xuất phát
- **Ga đến:** Chọn ga đích
- **Ngày đi:** Chọn ngày khởi hành
- **Giờ đi:** Chọn giờ khởi hành (từ giờ này trở đi)

Nhấn nút **"Tìm chuyến tàu"** → Hệ thống sẽ tìm các chuyến tàu phù hợp với TẤT CẢ các tiêu chí đã chọn.

### 2. Tìm Kiếm Chỉ Với Một Vài Tiêu Chí

#### Ví dụ 1: Chỉ tìm theo Ga đi
- **Ga đi:** Sài Gòn
- **Ga đến:** (để trống)
- **Ngày đi:** (để trống)
- **Giờ đi:** (để trống)

→ Kết quả: Tất cả các chuyến tàu khởi hành từ Sài Gòn

#### Ví dụ 2: Chỉ tìm theo Ga đến
- **Ga đi:** (để trống)
- **Ga đến:** Hà Nội
- **Ngày đi:** (để trống)
- **Giờ đi:** (để trống)

→ Kết quả: Tất cả các chuyến tàu đến Hà Nội

#### Ví dụ 3: Tìm theo Ngày đi
- **Ga đi:** (để trống)
- **Ga đến:** (để trống)
- **Ngày đi:** 25/12/2024
- **Giờ đi:** (để trống)

→ Kết quả: Tất cả các chuyến tàu chạy vào ngày 25/12/2024

#### Ví dụ 4: Kết hợp Ga đi và Ngày đi
- **Ga đi:** Sài Gòn
- **Ga đến:** (để trống)
- **Ngày đi:** 25/12/2024
- **Giờ đi:** (để trống)

→ Kết quả: Các chuyến tàu khởi hành từ Sài Gòn vào ngày 25/12/2024

#### Ví dụ 5: Kết hợp Ga đi, Ga đến và Giờ đi
- **Ga đi:** Sài Gòn
- **Ga đến:** Hà Nội
- **Ngày đi:** (để trống)
- **Giờ đi:** 14:00

→ Kết quả: Các chuyến tàu từ Sài Gòn đến Hà Nội, khởi hành từ 14:00 trở đi

### 3. Hiển Thị Tất Cả Chuyến Tàu
- Để trống TẤT CẢ các trường
- Nhấn nút **"Tìm chuyến tàu"**

→ Kết quả: Toàn bộ danh sách chuyến tàu trong hệ thống

## Lưu Ý

1. **Combo box Ga đi/Ga đến:**
   - Dòng đầu tiên (trống) = không lọc theo tiêu chí này
   - Chọn ga cụ thể = lọc theo ga đó

2. **Ngày đi:**
   - Không chọn ngày = không lọc theo ngày
   - Chọn ngày = chỉ hiển thị chuyến tàu chạy vào ngày đó

3. **Giờ đi:**
   - Mặc định sẽ hiển thị giờ hiện tại
   - Tìm kiếm sẽ lấy các chuyến từ giờ này TRỞ ĐI (>=)

4. **Không tìm thấy kết quả:**
   - Hệ thống sẽ hiển thị thông báo "Không tìm thấy chuyến tàu phù hợp!"
   - Thử giảm bớt số lượng tiêu chí tìm kiếm

## Cách Thức Hoạt Động (Technical)

### Backend (DAO Layer)
Phương thức `ChuyenTauDAO.timKiemChuyenTau()` xây dựng câu truy vấn SQL động:

```java
SELECT ... FROM ChuyenTau WHERE 1=1
  [AND gaDi = ? ]        // chỉ thêm nếu gaDi không null/empty
  [AND gaDen = ? ]       // chỉ thêm nếu gaDen không null/empty
  [AND CAST(gioDi AS DATE) = ? ]  // chỉ thêm nếu ngayDi không null
  [AND CAST(gioDi AS TIME) >= ? ] // chỉ thêm nếu gioDi không null
```

### Frontend (UI Layer)
Phương thức `PnlDatVe.timChuyenTau()` xử lý:

1. Lấy giá trị từ các controls (combo box, date picker, spinner)
2. Convert chuỗi rỗng ("") thành null
3. Truyền các tham số vào DAO
4. Hiển thị kết quả trong JTable

## Ví Dụ Workflow Hoàn Chỉnh

### Tình huống: Khách hàng muốn đi từ Sài Gòn đến Đà Nẵng vào ngày mai

1. **Bước 1:** Chọn Ga đi = "Sài Gòn"
2. **Bước 2:** Chọn Ga đến = "Đà Nẵng"
3. **Bước 3:** Chọn Ngày đi = ngày mai (ví dụ: 24/10/2024)
4. **Bước 4:** Giờ đi có thể để mặc định hoặc điều chỉnh nếu muốn
5. **Bước 5:** Nhấn "Tìm chuyến tàu"
6. **Kết quả:** Danh sách các chuyến tàu phù hợp hiển thị trong bảng
7. **Bước 6:** Chọn một chuyến tàu từ kết quả
8. **Bước 7:** Tiếp tục quy trình đặt vé (chọn toa, chọn ghế, xác nhận)

### Tình huống: Nhân viên muốn xem tất cả chuyến tàu

1. **Bước 1:** Để trống tất cả các trường
2. **Bước 2:** Nhấn "Tìm chuyến tàu"
3. **Kết quả:** Toàn bộ danh sách chuyến tàu hiển thị

## Kết Luận

Tính năng tìm kiếm linh hoạt giúp:
- ✅ Tăng tính tiện lợi cho người dùng
- ✅ Giảm thời gian tìm kiếm
- ✅ Không yêu cầu nhập đủ thông tin
- ✅ Phù hợp với nhiều tình huống sử dụng khác nhau
- ✅ Giao diện không thay đổi, dễ sử dụng
