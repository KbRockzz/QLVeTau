-- Seed data for QLTauHoa MariaDB database
-- Dữ liệu mẫu cho hệ thống quản lý vé tàu

USE QLTauHoa;

-- Loại Nhân Viên
INSERT INTO LoaiNV (maLoai, tenLoai, moTa) VALUES
('LNV01', 'Nhân viên bán vé', 'Nhân viên phụ trách bán vé tại quầy'),
('LNV02', 'Quản lý', 'Quản lý ca và nhân viên'),
('LNV03', 'Admin', 'Quản trị hệ thống')
ON DUPLICATE KEY UPDATE tenLoai = VALUES(tenLoai);

-- Nhân Viên
INSERT INTO NhanVien (maNV, tenNV, soDienThoai, diaChi, ngaySinh, maLoaiNV, trangThai) VALUES
('NV001', 'Nguyễn Văn An', '0901234567', 'Hà Nội', '1990-01-15', 'LNV01', 'active'),
('NV002', 'Trần Thị Bình', '0912345678', 'TP. Hồ Chí Minh', '1985-03-22', 'LNV02', 'active'),
('NV003', 'Lê Văn Cường', '0923456789', 'Đà Nẵng', '1992-07-10', 'LNV01', 'active'),
('NV004', 'admin', '0900000001', 'Hà Nội', '1980-01-01', 'LNV03', 'active')
ON DUPLICATE KEY UPDATE tenNV = VALUES(tenNV);

-- Tài Khoản
INSERT INTO TaiKhoan (maTK, maNV, tenTaiKhoan, matKhau, trangThai) VALUES
('TK001', 'NV001', 'nhanvien1', '123456', 'active'),
('TK002', 'NV002', 'quanly1', '123456', 'active'),
('TK003', 'NV003', 'nhanvien2', '123456', 'active'),
('TK004', 'NV004', 'admin', 'admin123', 'active')
ON DUPLICATE KEY UPDATE tenTaiKhoan = VALUES(tenTaiKhoan);

-- Khách Hàng
INSERT INTO KhachHang (maKhachHang, tenKhachHang, email, soDienThoai) VALUES
('KH01', 'Phạm Văn Dũng', 'dungpv@email.com', '0934567890'),
('KH02', 'Nguyễn Thị Em', 'emnt@email.com', '0945678901'),
('KH03', 'Hoàng Văn Phúc', 'phuchv@email.com', '0956789012')
ON DUPLICATE KEY UPDATE tenKhachHang = VALUES(tenKhachHang);

-- Ga Tàu
INSERT INTO Ga (maGa, tenGa, moTa, tinhTrang, diaChi) VALUES
('GA001', 'Ga Hà Nội', 'Ga trung tâm Hà Nội', 'active', '120 Lê Duẩn, Hà Nội'),
('GA002', 'Ga Sài Gòn', 'Ga trung tâm TP.HCM', 'active', '1 Nguyễn Thông, TP.HCM'),
('GA003', 'Ga Đà Nẵng', 'Ga trung tâm Đà Nẵng', 'active', '202 Hải Phòng, Đà Nẵng'),
('GA004', 'Ga Huế', 'Ga trung tâm Huế', 'active', '02 Bùi Thị Xuân, Huế'),
('GA005', 'Ga Nha Trang', 'Ga trung tâm Nha Trang', 'active', '17 Thái Nguyên, Nha Trang')
ON DUPLICATE KEY UPDATE tenGa = VALUES(tenGa);

-- Đầu Máy
INSERT INTO DauMay (maDauMay, loaiDauMay, tenDauMay, namSX, lanBaoTriGanNhat, trangThai) VALUES
('DM001', 'Diesel', 'D19E-001', 2010, '2024-01-15 08:00:00', 'active'),
('DM002', 'Diesel', 'D19E-002', 2012, '2024-02-20 09:00:00', 'active'),
('DM003', 'Điện', 'SE3-001', 2018, '2024-03-10 10:00:00', 'active')
ON DUPLICATE KEY UPDATE tenDauMay = VALUES(tenDauMay);

-- Toa Tàu
INSERT INTO ToaTau (maToa, loaiToa, samSX, trangThai, sucChua) VALUES
('TT001', 'Ghế ngồi cứng', 2010, 'active', 100),
('TT002', 'Ghế ngồi mềm', 2012, 'active', 64),
('TT003', 'Giường nằm cứng', 2015, 'active', 48),
('TT004', 'Giường nằm mềm', 2018, 'active', 32),
('TT005', 'VIP', 2020, 'active', 16)
ON DUPLICATE KEY UPDATE loaiToa = VALUES(loaiToa);

-- Loại Ghế
INSERT INTO LoaiGhe (maLoai, tenLoai, moTa) VALUES
('LG001', 'Ghế ngồi cứng', 'Ghế ngồi thông thường'),
('LG002', 'Ghế ngồi mềm', 'Ghế ngồi có đệm'),
('LG003', 'Giường nằm cứng', 'Giường nằm 6 người/khoang'),
('LG004', 'Giường nằm mềm', 'Giường nằm 4 người/khoang'),
('LG005', 'VIP', 'Khoang VIP cao cấp')
ON DUPLICATE KEY UPDATE tenLoai = VALUES(tenLoai);

-- Ghế
INSERT INTO Ghe (maGhe, maToa, loaiGhe, trangThai) VALUES
('GH001', 'TT001', 'LG001', 'available'),
('GH002', 'TT001', 'LG001', 'available'),
('GH003', 'TT002', 'LG002', 'available'),
('GH004', 'TT003', 'LG003', 'available'),
('GH005', 'TT004', 'LG004', 'available')
ON DUPLICATE KEY UPDATE trangThai = VALUES(trangThai);

-- Chặng Tàu
INSERT INTO ChangTau (maChang, soKMToiThieu, soKMToiDa, moTa, giaTien) VALUES
('CH001', 0, 500, 'Chặng ngắn (dưới 500km)', 50000),
('CH002', 500, 1000, 'Chặng trung bình (500-1000km)', 100000),
('CH003', 1000, 2000, 'Chặng dài (1000-2000km)', 200000)
ON DUPLICATE KEY UPDATE moTa = VALUES(moTa);

-- Chuyến Tàu
INSERT INTO ChuyenTau (maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang, trangThai) VALUES
('CT001', 'DM001', 'NV001', 'GA001', 'GA002', '2024-06-01 06:00:00', '2024-06-01 30:00:00', 1726, 'CH003', 'active'),
('CT002', 'DM002', 'NV002', 'GA001', 'GA003', '2024-06-02 08:00:00', '2024-06-02 22:00:00', 791, 'CH002', 'active'),
('CT003', 'DM003', 'NV003', 'GA002', 'GA003', '2024-06-03 07:00:00', '2024-06-03 18:00:00', 935, 'CH002', 'active')
ON DUPLICATE KEY UPDATE trangThai = VALUES(trangThai);

-- Loại Vé
INSERT INTO LoaiVe (maLoaiVe, tenLoai, heSoGia, moTa) VALUES
('LV001', 'Vé thường', 1.00, 'Vé tiêu chuẩn'),
('LV002', 'Vé học sinh/sinh viên', 0.75, 'Giảm 25% cho học sinh, sinh viên'),
('LV003', 'Vé người cao tuổi', 0.80, 'Giảm 20% cho người cao tuổi'),
('LV004', 'Vé trẻ em', 0.50, 'Giảm 50% cho trẻ em dưới 12 tuổi')
ON DUPLICATE KEY UPDATE tenLoai = VALUES(tenLoai);

-- Bảng Giá
INSERT INTO BangGia (maBangGia, maChang, loaiGhe, giaCoBan, ngayBatDau, ngayKetThuc) VALUES
('BG001', 'CH001', 'LG001', 100000, '2024-01-01 00:00:00', '2024-12-31 23:59:59'),
('BG002', 'CH001', 'LG002', 150000, '2024-01-01 00:00:00', '2024-12-31 23:59:59'),
('BG003', 'CH002', 'LG001', 180000, '2024-01-01 00:00:00', '2024-12-31 23:59:59'),
('BG004', 'CH002', 'LG003', 250000, '2024-01-01 00:00:00', '2024-12-31 23:59:59'),
('BG005', 'CH003', 'LG001', 300000, '2024-01-01 00:00:00', '2024-12-31 23:59:59'),
('BG006', 'CH003', 'LG004', 500000, '2024-01-01 00:00:00', '2024-12-31 23:59:59')
ON DUPLICATE KEY UPDATE giaCoBan = VALUES(giaCoBan);
