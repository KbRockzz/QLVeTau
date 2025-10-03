# Quick Start Guide - QLVeTau

## 🚀 Khởi động nhanh trong 3 bước

### Bước 1: Build dự án
```bash
mvn clean package
```

### Bước 2: Chạy ứng dụng
```bash
java -jar target/QLVeTau-1.0.0.jar
```

### Bước 3: Đăng nhập
```
Username: admin
Password: admin123
```

## 📋 Thử nghiệm nhanh các tính năng

### 1. Xem dữ liệu mẫu có sẵn

Hệ thống đã được nạp sẵn dữ liệu mẫu:
- ✅ 3 nhân viên
- ✅ 3 khách hàng  
- ✅ 5 chuyến tàu

### 2. Đặt vé (5 phút)
1. Chọn tab **"Đặt vé"**
2. Chọn chuyến tàu: **SE1 - Thống Nhất (Sài Gòn → Hà Nội)**
3. Chọn khách hàng: **KH001 - Phạm Minh Anh**
4. Nhập số ghế: **A1**
5. Click **"Đặt vé"**
6. ✅ Vé được đặt thành công!

### 3. Xem thống kê (2 phút)
1. Chọn tab **"Thống kê"**
2. Xem tổng quan:
   - 💰 Tổng doanh thu
   - 🎫 Số vé đã bán
   - ↩️ Vé hoàn trả
   - ❌ Vé hủy

### 4. Thêm khách hàng mới (3 phút)
1. Chọn tab **"Khách hàng"**
2. Nhập thông tin:
   ```
   Mã KH: KH004
   Họ tên: Nguyễn Văn Test
   SĐT: 0944444444
   CMND: 001234567893
   ```
3. Click **"Thêm"**
4. ✅ Khách hàng mới đã được thêm!

### 5. Thêm chuyến tàu mới (5 phút)
1. Chọn tab **"Chuyến tàu"**
2. Nhập thông tin:
   ```
   Mã tàu: TEST1
   Tên tàu: Chuyến Test
   Ga đi: Hà Nội
   Ga đến: Huế
   Giờ đi: 2024-12-25 08:00
   Giờ đến: 2024-12-25 20:00
   Tổng số chỗ: 100
   Giá vé: 400000
   ```
3. Click **"Thêm"**
4. ✅ Chuyến tàu mới đã được thêm!

## 🎯 Tính năng chính

| Tính năng | Mô tả | Quyền truy cập |
|-----------|-------|----------------|
| 🎫 Đặt vé | Đặt, hoàn, hủy vé | Tất cả |
| 👥 Khách hàng | Thêm, sửa, xóa | Tất cả |
| 🚆 Chuyến tàu | Quản lý lịch trình | Tất cả |
| 👤 Nhân viên | Quản lý nhân sự | Admin only |
| 🔐 Tài khoản | Quản lý đăng nhập | Admin only |
| 📊 Thống kê | Doanh thu, báo cáo | Admin only |

## 🔑 Tài khoản demo

```
Username: admin
Password: admin123
Role: ADMIN (Full access)
```

## 📖 Tài liệu chi tiết

- [README.md](README.md) - Tổng quan dự án
- [USAGE_GUIDE.md](USAGE_GUIDE.md) - Hướng dẫn chi tiết

## ⚡ Các thao tác thường dùng

### Đặt vé nhanh
```
Tab "Đặt vé" → Chọn tàu → Chọn khách → Nhập ghế → "Đặt vé"
```

### Hoàn vé
```
Tab "Đặt vé" → Click vào vé trong bảng → "Hoàn vé" → Xác nhận
```

### Xem doanh thu
```
Tab "Thống kê" → Xem tổng doanh thu → "Làm mới" để cập nhật
```

### Thêm tài khoản mới
```
Tab "Tài khoản" → Nhập thông tin → Chọn vai trò → "Thêm"
```

## ❓ Câu hỏi thường gặp

**Q: Dữ liệu có được lưu không?**  
A: Không, dữ liệu chỉ lưu trong RAM. Tắt app sẽ mất dữ liệu.

**Q: Làm sao tạo tài khoản cho nhân viên mới?**  
A: Đăng nhập admin → Tab "Nhân viên" → Thêm nhân viên → Tab "Tài khoản" → Tạo tài khoản với mã nhân viên vừa tạo

**Q: Định dạng ngày giờ là gì?**  
A: 
- Ngày: `yyyy-MM-dd` (VD: 2024-12-25)
- Ngày giờ: `yyyy-MM-dd HH:mm` (VD: 2024-12-25 08:00)

**Q: Làm sao biết chuyến tàu còn chỗ?**  
A: Xem cột "Chỗ trống" trong bảng chuyến tàu

**Q: Có thể hoàn vé đã hủy không?**  
A: Không, chỉ hoàn được vé có trạng thái BOOKED

## 🆘 Hỗ trợ

Gặp vấn đề? Tạo issue tại: https://github.com/KbRockzz/QLVeTau/issues

---
**Happy Coding! 🎉**
