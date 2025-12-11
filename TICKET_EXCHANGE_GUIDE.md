# Hướng Dẫn Sử Dụng Chức Năng Đổi Vé

## Tổng Quan

Chức năng đổi vé cho phép khách hàng hoặc nhân viên đổi vé đã đặt sang chuyến tàu khác, ghế khác hoặc thay đổi các thông tin liên quan với đầy đủ ràng buộc nghiệp vụ và ghi nhận lịch sử.

## Các Tính Năng Chính

### 1. Tìm Kiếm Vé Theo Số Điện Thoại
- Nhập số điện thoại khách hàng để tìm tất cả vé của họ
- Hiển thị đầy đủ thông tin: Mã vé, Mã hóa đơn, Ga đi/đến, Ngày giờ, Toa, Ghế, Loại vé, Giá, Trạng thái

### 2. Điều Kiện Đổi Vé

Vé chỉ được phép đổi khi:
- Trạng thái vé là "Đã đặt" hoặc "Đã thanh toán"
- Đổi trước ít nhất 2 giờ so với giờ đi (có thể cấu hình)
- Ghế mới còn trống
- Không phải vé đã hoàn hoặc đã hủy

### 3. Quy Trình Đổi Vé

#### Bước 1: Tìm và Chọn Vé
1. Nhập số điện thoại khách hàng
2. Nhấn "Tìm kiếm"
3. Chọn vé cần đổi từ danh sách
4. Nhấn "Đổi vé"

#### Bước 2: Chọn Chuyến và Ghế Mới
1. Chọn ga đi, ga đến, ngày đi mới
2. Nhấn "Tìm chuyến" để xem danh sách chuyến tàu phù hợp
3. Chọn chuyến tàu từ danh sách
4. Chọn toa tàu từ dropdown
5. Chọn ghế mới từ sơ đồ ghế (ghế màu xanh = trống, xám = đã đặt)

#### Bước 3: Xem Thông Tin và Xác Nhận
- Hệ thống hiển thị:
  - Thông tin vé cũ (bên trái)
  - Thông tin vé mới (bên phải)
  - Giá vé mới
  - Chênh lệch giá (màu đỏ = phải trả thêm, màu xanh = được hoàn lại)
- Nhập lý do/ghi chú (không bắt buộc)
- Nhấn "Xác nhận đổi vé"

### 4. Xử Lý Chênh Lệch Giá

#### Trường hợp 1: Khách phải trả thêm (Giá mới > Giá cũ)
- Hệ thống thông báo số tiền phải trả thêm
- Nhân viên thu tiền từ khách hàng
- Cập nhật vào hóa đơn

#### Trường hợp 2: Khách được hoàn lại (Giá mới < Giá cũ)
- Hệ thống thông báo số tiền được hoàn
- Tạo yêu cầu hoàn tiền
- Có thể cần phê duyệt quản lý (tùy cấu hình)

#### Trường hợp 3: Không chênh lệch
- Đổi vé trực tiếp không cần xử lý thêm

### 5. Lịch Sử Đổi Vé

Mọi thao tác đổi vé được ghi lại vào bảng `LichSuDoiVe` bao gồm:
- Mã lịch sử
- Thông tin vé cũ
- Thông tin vé mới
- Người thực hiện
- Thời gian
- Lý do
- Chênh lệch giá
- Trạng thái (Đã duyệt / Chờ duyệt / Từ chối)

## Quyền Hạn

### Nhân Viên Bán Vé
- Đổi vé theo yêu cầu khách hàng
- Xem lịch sử đổi vé
- In vé mới sau khi đổi

### Quản Lý (LNV02, LNV03)
- Tất cả quyền của nhân viên
- Phê duyệt yêu cầu đổi vé đặc biệt
- Xem báo cáo đổi vé

## Giao Diện Sử Dụng

### Panel Đổi Vé (PnlDoiVe)
- Tìm kiếm theo số điện thoại
- Bảng hiển thị danh sách vé
- Nút: Đổi vé, In vé, Làm mới

### Dialog Đổi Vé (DlgDoiVe)
- Bên trái: Thông tin vé cũ (chỉ xem)
- Bên phải: 
  - Bộ lọc tìm chuyến (ga đi, ga đến, ngày)
  - Bảng chuyến tàu
  - Chọn toa
  - Sơ đồ ghế
  - Tóm tắt giá
  - Ghi chú
- Nút: Xác nhận, Hủy

### Panel Duyệt Đổi Vé (PnlDuyetDoiVe) - Dành cho Quản lý
- Bảng danh sách yêu cầu chờ duyệt
- Chi tiết yêu cầu
- Nút: Duyệt, Từ chối, Làm mới

## Xử Lý Kỹ Thuật

### Transaction và Concurrency
- Tất cả thao tác đổi vé được thực hiện trong một transaction
- Sử dụng database locking để tránh race condition khi nhiều người cùng đổi
- Rollback tự động nếu có lỗi

### Cập Nhật Dữ Liệu
1. Giải phóng ghế cũ (set về "Trống")
2. Cập nhật thông tin vé
3. Đặt ghế mới (set "Đã đặt")
4. Cập nhật ChiTietHoaDon
5. Ghi lịch sử đổi vé
6. Commit transaction

### Tính Giá
- Sử dụng `TinhGiaService` để tính giá vé mới
- Áp dụng bảng giá hiện hành
- Tính chênh lệch tự động

## Lưu Ý Quan Trọng

1. **Thời hạn đổi vé**: Mặc định là 2 giờ trước giờ đi, có thể thay đổi trong code (constant `HOURS_BEFORE_DEPARTURE_TO_EXCHANGE`)

2. **Không thể đổi vé**:
   - Vé đã hoàn
   - Vé đã hủy
   - Sau thời hạn cho phép

3. **Audit Trail**: Tất cả thao tác đều được ghi log, không thể xóa lịch sử

4. **Hóa đơn**: 
   - Nếu hóa đơn chưa hoàn tất: cập nhật trực tiếp
   - Nếu hóa đơn đã hoàn tất: tạo phiếu điều chỉnh (tùy chính sách)

## Xử Lý Lỗi

- **"Không tìm thấy khách hàng"**: Kiểm tra lại số điện thoại
- **"Đã quá thời hạn đổi vé"**: Vé không thể đổi do quá gần giờ đi
- **"Ghế đã được đặt"**: Chọn ghế khác
- **"Không thể cập nhật thông tin vé"**: Lỗi hệ thống, thử lại hoặc liên hệ IT

## Báo Cáo

Có thể truy vấn các báo cáo sau:
- Số lượng vé đổi theo ngày/tháng
- Chênh lệch giá thu/hoàn
- Nhân viên thực hiện nhiều nhất
- Lý do đổi vé phổ biến

## Khuyến Nghị UX

- **Không bắt buộc** quay về màn hình đặt vé
- Dialog đổi vé có đầy đủ chức năng chọn chuyến và ghế
- Giữ ngữ cảnh để người dùng không bị mất tập trung
- Hiển thị rõ ràng chênh lệch giá trước khi xác nhận

## Hỗ Trợ

Nếu gặp vấn đề, liên hệ:
- IT Support: [số điện thoại]
- Email: [địa chỉ email]
