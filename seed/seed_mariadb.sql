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
('CT001', 'DM001', 'NV001', 'GA001', 'GA002', '2024-06-01 06:00:00', '2024-06-02 06:00:00', 1726, 'CH003', 'active'),
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
('BG006', 'CH003', 'LG004', 500000, '2024-01-01 00:00:00', '2024-12-31 23:59:59'),
-- Bảng giá năm 2025-2026
('BG007', 'CH003', 'LG001', 350000, '2025-01-01 00:00:00', '2026-12-31 23:59:59'),
('BG008', 'CH003', 'LG004', 600000, '2025-01-01 00:00:00', '2026-12-31 23:59:59'),
('BG009', 'CH002', 'LG001', 200000, '2025-01-01 00:00:00', '2026-12-31 23:59:59'),
('BG010', 'CH002', 'LG003', 290000, '2025-01-01 00:00:00', '2026-12-31 23:59:59'),
('BG011', 'CH003', 'LG003', 520000, '2025-01-01 00:00:00', '2026-12-31 23:59:59')
ON DUPLICATE KEY UPDATE giaCoBan = VALUES(giaCoBan);

-- Chuyến Tàu ngày 12/5/2026 (tương lai để kiểm thử)
INSERT INTO ChuyenTau (maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang, trangThai) VALUES
('CT004', 'DM001', 'NV001', 'GA001', 'GA002', '2026-05-12 06:00:00', '2026-05-13 06:00:00', 1726, 'CH003', 'active'),
('CT005', 'DM002', 'NV003', 'GA001', 'GA003', '2026-05-12 08:00:00', '2026-05-12 22:00:00', 791,  'CH002', 'active')
ON DUPLICATE KEY UPDATE trangThai = VALUES(trangThai);

-- Chi Tiết Chuyến Tàu (gán toa vào từng chuyến)
INSERT INTO ChiTietChuyenTau (maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES
-- CT001: Hà Nội → Sài Gòn (2024-06-01)
('CT001', 'TT001', 1, 100),
('CT001', 'TT003', 2, 48),
('CT001', 'TT004', 3, 32),
-- CT002: Hà Nội → Đà Nẵng (2024-06-02)
('CT002', 'TT002', 1, 64),
('CT002', 'TT003', 2, 48),
-- CT003: Sài Gòn → Đà Nẵng (2024-06-03)
('CT003', 'TT001', 1, 100),
('CT003', 'TT002', 2, 64),
-- CT004: Hà Nội → Sài Gòn (2026-05-12)
('CT004', 'TT001', 1, 100),
('CT004', 'TT002', 2, 64),
('CT004', 'TT003', 3, 48),
('CT004', 'TT004', 4, 32),
-- CT005: Hà Nội → Đà Nẵng (2026-05-12)
('CT005', 'TT001', 1, 100),
('CT005', 'TT003', 2, 48)
ON DUPLICATE KEY UPDATE sucChua = VALUES(sucChua);

-- Vé quá khứ (đã bán)
-- CT001: Hà Nội → Sài Gòn, 2024-06-01
INSERT INTO Ve (maVe, maChuyen, maLoaiVe, maSoGhe, maGaDi, maGaDen, tenGaDi, tenGaDen,
                ngayIn, trangThai, gioDi, gioDenDuKien, soToa, loaiCho, loaiVe, maBangGia, giaThanhToan, isActive) VALUES
('VE001', 'CT001', 'LV001', 'GH001', 'GA001', 'GA002', 'Ga Hà Nội', 'Ga Sài Gòn',
 '2024-05-28 09:00:00', 'sold', '2024-06-01 06:00:00', '2024-06-02 06:00:00', 1, 'LG001', 'LV001', 'BG005', 300000, 1),
('VE002', 'CT001', 'LV002', 'GH002', 'GA001', 'GA002', 'Ga Hà Nội', 'Ga Sài Gòn',
 '2024-05-28 09:05:00', 'sold', '2024-06-01 06:00:00', '2024-06-02 06:00:00', 1, 'LG001', 'LV002', 'BG005', 225000, 1),
('VE003', 'CT001', 'LV001', 'GH005', 'GA001', 'GA002', 'Ga Hà Nội', 'Ga Sài Gòn',
 '2024-05-29 10:00:00', 'sold', '2024-06-01 06:00:00', '2024-06-02 06:00:00', 3, 'LG004', 'LV001', 'BG006', 500000, 1),
-- CT002: Hà Nội → Đà Nẵng, 2024-06-02
('VE004', 'CT002', 'LV001', 'GH001', 'GA001', 'GA003', 'Ga Hà Nội', 'Ga Đà Nẵng',
 '2024-05-30 08:00:00', 'sold', '2024-06-02 08:00:00', '2024-06-02 22:00:00', 1, 'LG001', 'LV001', 'BG003', 180000, 1),
('VE005', 'CT002', 'LV002', 'GH004', 'GA001', 'GA003', 'Ga Hà Nội', 'Ga Đà Nẵng',
 '2024-05-30 08:10:00', 'sold', '2024-06-02 08:00:00', '2024-06-02 22:00:00', 2, 'LG003', 'LV002', 'BG004', 187500, 1),
-- CT003: Sài Gòn → Đà Nẵng, 2024-06-03
('VE006', 'CT003', 'LV001', 'GH002', 'GA002', 'GA003', 'Ga Sài Gòn', 'Ga Đà Nẵng',
 '2024-06-01 07:00:00', 'sold', '2024-06-03 07:00:00', '2024-06-03 18:00:00', 2, 'LG001', 'LV001', 'BG003', 180000, 1)
ON DUPLICATE KEY UPDATE trangThai = VALUES(trangThai);

-- Vé chuyến 12/5/2026 (đã đặt trước + còn trống để kiểm thử)
-- CT004: Hà Nội → Sài Gòn, 2026-05-12
INSERT INTO Ve (maVe, maChuyen, maLoaiVe, maSoGhe, maGaDi, maGaDen, tenGaDi, tenGaDen,
                ngayIn, trangThai, gioDi, gioDenDuKien, soToa, loaiCho, loaiVe, maBangGia, giaThanhToan, isActive) VALUES
('VE007', 'CT004', 'LV001', 'GH001', 'GA001', 'GA002', 'Ga Hà Nội', 'Ga Sài Gòn',
 '2026-04-15 10:00:00', 'sold', '2026-05-12 06:00:00', '2026-05-13 06:00:00', 1, 'LG001', 'LV001', 'BG007', 350000, 1),
('VE008', 'CT004', 'LV002', 'GH002', 'GA001', 'GA002', 'Ga Hà Nội', 'Ga Sài Gòn',
 '2026-04-20 11:00:00', 'sold', '2026-05-12 06:00:00', '2026-05-13 06:00:00', 1, 'LG001', 'LV002', 'BG007', 262500, 1),
('VE009', 'CT004', 'LV001', 'GH005', 'GA001', 'GA002', 'Ga Hà Nội', 'Ga Sài Gòn',
 '2026-04-20 11:10:00', 'sold', '2026-05-12 06:00:00', '2026-05-13 06:00:00', 4, 'LG004', 'LV001', 'BG008', 600000, 1),
('VE010', 'CT004', 'LV001', 'GH004', 'GA001', 'GA002', 'Ga Hà Nội', 'Ga Sài Gòn',
 NULL,                 'available', '2026-05-12 06:00:00', '2026-05-13 06:00:00', 3, 'LG003', 'LV001', 'BG011', 520000, 1),
-- CT005: Hà Nội → Đà Nẵng, 2026-05-12
('VE011', 'CT005', 'LV001', 'GH001', 'GA001', 'GA003', 'Ga Hà Nội', 'Ga Đà Nẵng',
 '2026-04-20 14:00:00', 'sold', '2026-05-12 08:00:00', '2026-05-12 22:00:00', 1, 'LG001', 'LV001', 'BG009', 200000, 1),
('VE012', 'CT005', 'LV003', 'GH004', 'GA001', 'GA003', 'Ga Hà Nội', 'Ga Đà Nẵng',
 NULL,                 'available', '2026-05-12 08:00:00', '2026-05-12 22:00:00', 2, 'LG003', 'LV003', 'BG010', 232000, 1)
ON DUPLICATE KEY UPDATE trangThai = VALUES(trangThai);

-- Hóa Đơn
INSERT INTO HoaDon (maHoaDon, maNV, maKH, tenKH, soDienThoai, ngayLap, phuongThucThanhToan, trangThai) VALUES
-- Hóa đơn quá khứ
('HD001', 'NV001', 'KH01', 'Phạm Văn Dũng',   '0934567890', '2024-05-28 09:10:00', 'Tiền mặt',    'completed'),
('HD002', 'NV001', 'KH03', 'Hoàng Văn Phúc',   '0956789012', '2024-05-29 10:05:00', 'Tiền mặt',    'completed'),
('HD003', 'NV003', 'KH02', 'Nguyễn Thị Em',    '0945678901', '2024-05-30 08:15:00', 'Chuyển khoản','completed'),
('HD004', 'NV001', 'KH03', 'Hoàng Văn Phúc',   '0956789012', '2024-06-01 07:05:00', 'Tiền mặt',    'completed'),
-- Hóa đơn đặt trước chuyến 12/5/2026
('HD005', 'NV001', 'KH01', 'Phạm Văn Dũng',   '0934567890', '2026-04-15 10:10:00', 'Chuyển khoản','completed'),
('HD006', 'NV002', 'KH02', 'Nguyễn Thị Em',    '0945678901', '2026-04-20 11:15:00', 'Tiền mặt',    'completed')
ON DUPLICATE KEY UPDATE trangThai = VALUES(trangThai);

-- Chi Tiết Hóa Đơn
INSERT INTO ChiTietHoaDon (maHoaDon, maVe, maLoaiVe, giaGoc, giaDaKM, moTa) VALUES
-- HD001: KH01 mua VE001 (thường) + VE002 (học sinh) – chuyến HN→SG 01/06/2024
('HD001', 'VE001', 'LV001', 300000, 300000, 'Vé HN-SG ghế ngồi cứng (thường)'),
('HD001', 'VE002', 'LV002', 300000, 225000, 'Vé HN-SG ghế ngồi cứng (học sinh -25%)'),
-- HD002: KH03 mua VE003 (nằm mềm VIP) – chuyến HN→SG 01/06/2024
('HD002', 'VE003', 'LV001', 500000, 500000, 'Vé HN-SG giường nằm mềm (thường)'),
-- HD003: KH02 mua VE004 (thường) + VE005 (học sinh nằm cứng) – chuyến HN→ĐN 02/06/2024
('HD003', 'VE004', 'LV001', 180000, 180000, 'Vé HN-ĐN ghế ngồi cứng (thường)'),
('HD003', 'VE005', 'LV002', 250000, 187500, 'Vé HN-ĐN giường nằm cứng (học sinh -25%)'),
-- HD004: KH03 mua VE006 – chuyến SG→ĐN 03/06/2024
('HD004', 'VE006', 'LV001', 180000, 180000, 'Vé SG-ĐN ghế ngồi cứng (thường)'),
-- HD005: KH01 đặt trước VE007 (thường) + VE008 (học sinh) – chuyến HN→SG 12/05/2026
('HD005', 'VE007', 'LV001', 350000, 350000, 'Vé HN-SG ghế ngồi cứng, chuyến 12/5/2026 (thường)'),
('HD005', 'VE008', 'LV002', 350000, 262500, 'Vé HN-SG ghế ngồi cứng, chuyến 12/5/2026 (học sinh -25%)'),
-- HD006: KH02 đặt trước VE009 (nằm mềm) + VE011 (thường HN→ĐN) – 12/05/2026
('HD006', 'VE009', 'LV001', 600000, 600000, 'Vé HN-SG giường nằm mềm, chuyến 12/5/2026 (thường)'),
('HD006', 'VE011', 'LV001', 200000, 200000, 'Vé HN-ĐN ghế ngồi cứng, chuyến 12/5/2026 (thường)')
ON DUPLICATE KEY UPDATE moTa = VALUES(moTa);
