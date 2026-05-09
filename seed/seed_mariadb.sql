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
('NV001', 'Nguyễn Văn An', '0901234567', 'Hà Nội', '1990-01-15', 'LNV01', 'Đang hoạt động'),
('NV002', 'Trần Thị Bình', '0912345678', 'TP. Hồ Chí Minh', '1985-03-22', 'LNV02', 'Đang hoạt động'),
('NV003', 'Lê Văn Cường', '0923456789', 'Đà Nẵng', '1992-07-10', 'LNV01', 'Đang hoạt động'),
('NV004', 'admin', '0900000001', 'Hà Nội', '1980-01-01', 'LNV03', 'Đang hoạt động')
ON DUPLICATE KEY UPDATE tenNV = VALUES(tenNV);

-- Tài Khoản
INSERT INTO TaiKhoan (maTK, maNV, tenTaiKhoan, matKhau, trangThai) VALUES
('TK001', 'NV001', 'nhanvien1', '123456', 'Hoạt động'),
('TK002', 'NV002', 'quanly1', '123456', 'Hoạt động'),
('TK003', 'NV003', 'nhanvien2', '123456', 'Hoạt động'),
('TK004', 'NV004', 'admin', 'admin123', 'Hoạt động')
ON DUPLICATE KEY UPDATE tenTaiKhoan = VALUES(tenTaiKhoan);

-- Khách Hàng
INSERT INTO KhachHang (maKhachHang, tenKhachHang, email, soDienThoai) VALUES
('KH01', 'Phạm Văn Dũng', 'dungpv@email.com', '0934567890'),
('KH02', 'Nguyễn Thị Em', 'emnt@email.com', '0945678901'),
('KH03', 'Hoàng Văn Phúc', 'phuchv@email.com', '0956789012'),
('KH04', 'Trần Minh Hiếu', 'hieu.tran@email.com', '0967890123'),
('KH05', 'Lê Thị Lan', 'lan.le@email.com', '0978901234'),
('KH06', 'Võ Thanh Tùng', 'tung.vo@email.com', '0989012345'),
('KH07', 'Nguyễn Thành Đạt', 'dat.nguyen@email.com', '0990123456'),
('KH08', 'Phạm Thị Hoa', 'hoa.pham@email.com', '0901234560')
ON DUPLICATE KEY UPDATE tenKhachHang = VALUES(tenKhachHang);

-- Ga Tàu
INSERT INTO Ga (maGa, tenGa, moTa, tinhTrang, diaChi) VALUES
('GA001', 'Ga Hà Nội', 'Ga trung tâm Hà Nội', 'Hoạt động', '120 Lê Duẩn, Hà Nội'),
('GA002', 'Ga Sài Gòn', 'Ga trung tâm TP.HCM', 'Hoạt động', '1 Nguyễn Thông, TP.HCM'),
('GA003', 'Ga Đà Nẵng', 'Ga trung tâm Đà Nẵng', 'Hoạt động', '202 Hải Phòng, Đà Nẵng'),
('GA004', 'Ga Huế', 'Ga trung tâm Huế', 'Hoạt động', '02 Bùi Thị Xuân, Huế'),
('GA005', 'Ga Nha Trang', 'Ga trung tâm Nha Trang', 'Hoạt động', '17 Thái Nguyên, Nha Trang')
ON DUPLICATE KEY UPDATE tenGa = VALUES(tenGa);

-- Đầu Máy
INSERT INTO DauMay (maDauMay, loaiDauMay, tenDauMay, namSX, lanBaoTriGanNhat, trangThai) VALUES
('DM001', 'Diesel', 'D19E-001', 2010, '2024-01-15 08:00:00', 'Hoạt động'),
('DM002', 'Diesel', 'D19E-002', 2012, '2024-02-20 09:00:00', 'Hoạt động'),
('DM003', 'Điện', 'SE3-001', 2018, '2024-03-10 10:00:00', 'Hoạt động')
ON DUPLICATE KEY UPDATE tenDauMay = VALUES(tenDauMay);

-- Toa Tàu
INSERT INTO ToaTau (maToa, loaiToa, samSX, trangThai, sucChua) VALUES
('TT001', 'Ghế ngồi cứng', 2010, 'Hoạt động', 100),
('TT002', 'Ghế ngồi mềm', 2012, 'Hoạt động', 64),
('TT003', 'Giường nằm cứng', 2015, 'Hoạt động', 48),
('TT004', 'Giường nằm mềm', 2018, 'Hoạt động', 32),
('TT005', 'VIP', 2020, 'Hoạt động', 16)
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
('GH001', 'TT001', 'LG001', 'Rảnh'),
('GH002', 'TT001', 'LG001', 'Rảnh'),
('GH003', 'TT001', 'LG001', 'Rảnh'),
('GH004', 'TT001', 'LG001', 'Rảnh'),
('GH005', 'TT001', 'LG001', 'Rảnh'),
('GH006', 'TT001', 'LG001', 'Rảnh'),
('GH007', 'TT001', 'LG001', 'Rảnh'),
('GH008', 'TT001', 'LG001', 'Rảnh'),
('GH009', 'TT001', 'LG001', 'Rảnh'),
('GH010', 'TT001', 'LG001', 'Rảnh'),
('GH011', 'TT001', 'LG001', 'Rảnh'),
('GH012', 'TT001', 'LG001', 'Rảnh'),
('GH013', 'TT001', 'LG001', 'Rảnh'),
('GH014', 'TT001', 'LG001', 'Rảnh'),
('GH015', 'TT001', 'LG001', 'Rảnh'),
('GH016', 'TT001', 'LG001', 'Rảnh'),
('GH017', 'TT001', 'LG001', 'Rảnh'),
('GH018', 'TT001', 'LG001', 'Rảnh'),
('GH019', 'TT001', 'LG001', 'Rảnh'),
('GH020', 'TT001', 'LG001', 'Rảnh'),
('GH021', 'TT001', 'LG001', 'Rảnh'),
('GH022', 'TT001', 'LG001', 'Rảnh'),
('GH023', 'TT001', 'LG001', 'Rảnh'),
('GH024', 'TT001', 'LG001', 'Rảnh'),
('GH025', 'TT001', 'LG001', 'Rảnh'),
('GH026', 'TT001', 'LG001', 'Rảnh'),
('GH027', 'TT001', 'LG001', 'Rảnh'),
('GH028', 'TT001', 'LG001', 'Rảnh'),
('GH029', 'TT001', 'LG001', 'Rảnh'),
('GH030', 'TT001', 'LG001', 'Rảnh'),
('GH031', 'TT001', 'LG001', 'Rảnh'),
('GH032', 'TT001', 'LG001', 'Rảnh'),
('GH033', 'TT001', 'LG001', 'Rảnh'),
('GH034', 'TT001', 'LG001', 'Rảnh'),
('GH035', 'TT001', 'LG001', 'Rảnh'),
('GH036', 'TT001', 'LG001', 'Rảnh'),
('GH037', 'TT001', 'LG001', 'Rảnh'),
('GH038', 'TT001', 'LG001', 'Rảnh'),
('GH039', 'TT001', 'LG001', 'Rảnh'),
('GH040', 'TT001', 'LG001', 'Rảnh'),
('GH041', 'TT001', 'LG001', 'Rảnh'),
('GH042', 'TT001', 'LG001', 'Rảnh'),
('GH043', 'TT001', 'LG001', 'Rảnh'),
('GH044', 'TT001', 'LG001', 'Rảnh'),
('GH045', 'TT001', 'LG001', 'Rảnh'),
('GH046', 'TT001', 'LG001', 'Rảnh'),
('GH047', 'TT001', 'LG001', 'Rảnh'),
('GH048', 'TT001', 'LG001', 'Rảnh'),
('GH049', 'TT001', 'LG001', 'Rảnh'),
('GH050', 'TT001', 'LG001', 'Rảnh'),
('GH051', 'TT001', 'LG001', 'Rảnh'),
('GH052', 'TT001', 'LG001', 'Rảnh'),
('GH053', 'TT001', 'LG001', 'Rảnh'),
('GH054', 'TT001', 'LG001', 'Rảnh'),
('GH055', 'TT001', 'LG001', 'Rảnh'),
('GH056', 'TT001', 'LG001', 'Rảnh'),
('GH057', 'TT001', 'LG001', 'Rảnh'),
('GH058', 'TT001', 'LG001', 'Rảnh'),
('GH059', 'TT001', 'LG001', 'Rảnh'),
('GH060', 'TT001', 'LG001', 'Rảnh'),
('GH061', 'TT001', 'LG001', 'Rảnh'),
('GH062', 'TT001', 'LG001', 'Rảnh'),
('GH063', 'TT001', 'LG001', 'Rảnh'),
('GH064', 'TT001', 'LG001', 'Rảnh'),
('GH065', 'TT001', 'LG001', 'Rảnh'),
('GH066', 'TT001', 'LG001', 'Rảnh'),
('GH067', 'TT001', 'LG001', 'Rảnh'),
('GH068', 'TT001', 'LG001', 'Rảnh'),
('GH069', 'TT001', 'LG001', 'Rảnh'),
('GH070', 'TT001', 'LG001', 'Rảnh'),
('GH071', 'TT001', 'LG001', 'Rảnh'),
('GH072', 'TT001', 'LG001', 'Rảnh'),
('GH073', 'TT001', 'LG001', 'Rảnh'),
('GH074', 'TT001', 'LG001', 'Rảnh'),
('GH075', 'TT001', 'LG001', 'Rảnh'),
('GH076', 'TT001', 'LG001', 'Rảnh'),
('GH077', 'TT001', 'LG001', 'Rảnh'),
('GH078', 'TT001', 'LG001', 'Rảnh'),
('GH079', 'TT001', 'LG001', 'Rảnh'),
('GH080', 'TT001', 'LG001', 'Rảnh'),
('GH081', 'TT001', 'LG001', 'Rảnh'),
('GH082', 'TT001', 'LG001', 'Rảnh'),
('GH083', 'TT001', 'LG001', 'Rảnh'),
('GH084', 'TT001', 'LG001', 'Rảnh'),
('GH085', 'TT001', 'LG001', 'Rảnh'),
('GH086', 'TT001', 'LG001', 'Rảnh'),
('GH087', 'TT001', 'LG001', 'Rảnh'),
('GH088', 'TT001', 'LG001', 'Rảnh'),
('GH089', 'TT001', 'LG001', 'Rảnh'),
('GH090', 'TT001', 'LG001', 'Rảnh'),
('GH091', 'TT001', 'LG001', 'Rảnh'),
('GH092', 'TT001', 'LG001', 'Rảnh'),
('GH093', 'TT001', 'LG001', 'Rảnh'),
('GH094', 'TT001', 'LG001', 'Rảnh'),
('GH095', 'TT001', 'LG001', 'Rảnh'),
('GH096', 'TT001', 'LG001', 'Rảnh'),
('GH097', 'TT001', 'LG001', 'Rảnh'),
('GH098', 'TT001', 'LG001', 'Rảnh'),
('GH099', 'TT001', 'LG001', 'Rảnh'),
('GH100', 'TT001', 'LG001', 'Rảnh'),
('GH101', 'TT002', 'LG002', 'Rảnh'),
('GH102', 'TT002', 'LG002', 'Rảnh'),
('GH103', 'TT002', 'LG002', 'Rảnh'),
('GH104', 'TT002', 'LG002', 'Rảnh'),
('GH105', 'TT002', 'LG002', 'Rảnh'),
('GH106', 'TT002', 'LG002', 'Rảnh'),
('GH107', 'TT002', 'LG002', 'Rảnh'),
('GH108', 'TT002', 'LG002', 'Rảnh'),
('GH109', 'TT002', 'LG002', 'Rảnh'),
('GH110', 'TT002', 'LG002', 'Rảnh'),
('GH111', 'TT002', 'LG002', 'Rảnh'),
('GH112', 'TT002', 'LG002', 'Rảnh'),
('GH113', 'TT002', 'LG002', 'Rảnh'),
('GH114', 'TT002', 'LG002', 'Rảnh'),
('GH115', 'TT002', 'LG002', 'Rảnh'),
('GH116', 'TT002', 'LG002', 'Rảnh'),
('GH117', 'TT002', 'LG002', 'Rảnh'),
('GH118', 'TT002', 'LG002', 'Rảnh'),
('GH119', 'TT002', 'LG002', 'Rảnh'),
('GH120', 'TT002', 'LG002', 'Rảnh'),
('GH121', 'TT002', 'LG002', 'Rảnh'),
('GH122', 'TT002', 'LG002', 'Rảnh'),
('GH123', 'TT002', 'LG002', 'Rảnh'),
('GH124', 'TT002', 'LG002', 'Rảnh'),
('GH125', 'TT002', 'LG002', 'Rảnh'),
('GH126', 'TT002', 'LG002', 'Rảnh'),
('GH127', 'TT002', 'LG002', 'Rảnh'),
('GH128', 'TT002', 'LG002', 'Rảnh'),
('GH129', 'TT002', 'LG002', 'Rảnh'),
('GH130', 'TT002', 'LG002', 'Rảnh'),
('GH131', 'TT002', 'LG002', 'Rảnh'),
('GH132', 'TT002', 'LG002', 'Rảnh'),
('GH133', 'TT002', 'LG002', 'Rảnh'),
('GH134', 'TT002', 'LG002', 'Rảnh'),
('GH135', 'TT002', 'LG002', 'Rảnh'),
('GH136', 'TT002', 'LG002', 'Rảnh'),
('GH137', 'TT002', 'LG002', 'Rảnh'),
('GH138', 'TT002', 'LG002', 'Rảnh'),
('GH139', 'TT002', 'LG002', 'Rảnh'),
('GH140', 'TT002', 'LG002', 'Rảnh'),
('GH141', 'TT002', 'LG002', 'Rảnh'),
('GH142', 'TT002', 'LG002', 'Rảnh'),
('GH143', 'TT002', 'LG002', 'Rảnh'),
('GH144', 'TT002', 'LG002', 'Rảnh'),
('GH145', 'TT002', 'LG002', 'Rảnh'),
('GH146', 'TT002', 'LG002', 'Rảnh'),
('GH147', 'TT002', 'LG002', 'Rảnh'),
('GH148', 'TT002', 'LG002', 'Rảnh'),
('GH149', 'TT002', 'LG002', 'Rảnh'),
('GH150', 'TT002', 'LG002', 'Rảnh'),
('GH151', 'TT002', 'LG002', 'Rảnh'),
('GH152', 'TT002', 'LG002', 'Rảnh'),
('GH153', 'TT002', 'LG002', 'Rảnh'),
('GH154', 'TT002', 'LG002', 'Rảnh'),
('GH155', 'TT002', 'LG002', 'Rảnh'),
('GH156', 'TT002', 'LG002', 'Rảnh'),
('GH157', 'TT002', 'LG002', 'Rảnh'),
('GH158', 'TT002', 'LG002', 'Rảnh'),
('GH159', 'TT002', 'LG002', 'Rảnh'),
('GH160', 'TT002', 'LG002', 'Rảnh'),
('GH161', 'TT002', 'LG002', 'Rảnh'),
('GH162', 'TT002', 'LG002', 'Rảnh'),
('GH163', 'TT002', 'LG002', 'Rảnh'),
('GH164', 'TT002', 'LG002', 'Rảnh'),
('GH165', 'TT003', 'LG003', 'Rảnh'),
('GH166', 'TT003', 'LG003', 'Rảnh'),
('GH167', 'TT003', 'LG003', 'Rảnh'),
('GH168', 'TT003', 'LG003', 'Rảnh'),
('GH169', 'TT003', 'LG003', 'Rảnh'),
('GH170', 'TT003', 'LG003', 'Rảnh'),
('GH171', 'TT003', 'LG003', 'Rảnh'),
('GH172', 'TT003', 'LG003', 'Rảnh'),
('GH173', 'TT003', 'LG003', 'Rảnh'),
('GH174', 'TT003', 'LG003', 'Rảnh'),
('GH175', 'TT003', 'LG003', 'Rảnh'),
('GH176', 'TT003', 'LG003', 'Rảnh'),
('GH177', 'TT003', 'LG003', 'Rảnh'),
('GH178', 'TT003', 'LG003', 'Rảnh'),
('GH179', 'TT003', 'LG003', 'Rảnh'),
('GH180', 'TT003', 'LG003', 'Rảnh'),
('GH181', 'TT003', 'LG003', 'Rảnh'),
('GH182', 'TT003', 'LG003', 'Rảnh'),
('GH183', 'TT003', 'LG003', 'Rảnh'),
('GH184', 'TT003', 'LG003', 'Rảnh'),
('GH185', 'TT003', 'LG003', 'Rảnh'),
('GH186', 'TT003', 'LG003', 'Rảnh'),
('GH187', 'TT003', 'LG003', 'Rảnh'),
('GH188', 'TT003', 'LG003', 'Rảnh'),
('GH189', 'TT003', 'LG003', 'Rảnh'),
('GH190', 'TT003', 'LG003', 'Rảnh'),
('GH191', 'TT003', 'LG003', 'Rảnh'),
('GH192', 'TT003', 'LG003', 'Rảnh'),
('GH193', 'TT003', 'LG003', 'Rảnh'),
('GH194', 'TT003', 'LG003', 'Rảnh'),
('GH195', 'TT003', 'LG003', 'Rảnh'),
('GH196', 'TT003', 'LG003', 'Rảnh'),
('GH197', 'TT003', 'LG003', 'Rảnh'),
('GH198', 'TT003', 'LG003', 'Rảnh'),
('GH199', 'TT003', 'LG003', 'Rảnh'),
('GH200', 'TT003', 'LG003', 'Rảnh'),
('GH201', 'TT003', 'LG003', 'Rảnh'),
('GH202', 'TT003', 'LG003', 'Rảnh'),
('GH203', 'TT003', 'LG003', 'Rảnh'),
('GH204', 'TT003', 'LG003', 'Rảnh'),
('GH205', 'TT003', 'LG003', 'Rảnh'),
('GH206', 'TT003', 'LG003', 'Rảnh'),
('GH207', 'TT003', 'LG003', 'Rảnh'),
('GH208', 'TT003', 'LG003', 'Rảnh'),
('GH209', 'TT003', 'LG003', 'Rảnh'),
('GH210', 'TT003', 'LG003', 'Rảnh'),
('GH211', 'TT003', 'LG003', 'Rảnh'),
('GH212', 'TT003', 'LG003', 'Rảnh'),
('GH213', 'TT004', 'LG004', 'Rảnh'),
('GH214', 'TT004', 'LG004', 'Rảnh'),
('GH215', 'TT004', 'LG004', 'Rảnh'),
('GH216', 'TT004', 'LG004', 'Rảnh'),
('GH217', 'TT004', 'LG004', 'Rảnh'),
('GH218', 'TT004', 'LG004', 'Rảnh'),
('GH219', 'TT004', 'LG004', 'Rảnh'),
('GH220', 'TT004', 'LG004', 'Rảnh'),
('GH221', 'TT004', 'LG004', 'Rảnh'),
('GH222', 'TT004', 'LG004', 'Rảnh'),
('GH223', 'TT004', 'LG004', 'Rảnh'),
('GH224', 'TT004', 'LG004', 'Rảnh'),
('GH225', 'TT004', 'LG004', 'Rảnh'),
('GH226', 'TT004', 'LG004', 'Rảnh'),
('GH227', 'TT004', 'LG004', 'Rảnh'),
('GH228', 'TT004', 'LG004', 'Rảnh'),
('GH229', 'TT004', 'LG004', 'Rảnh'),
('GH230', 'TT004', 'LG004', 'Rảnh'),
('GH231', 'TT004', 'LG004', 'Rảnh'),
('GH232', 'TT004', 'LG004', 'Rảnh'),
('GH233', 'TT004', 'LG004', 'Rảnh'),
('GH234', 'TT004', 'LG004', 'Rảnh'),
('GH235', 'TT004', 'LG004', 'Rảnh'),
('GH236', 'TT004', 'LG004', 'Rảnh'),
('GH237', 'TT004', 'LG004', 'Rảnh'),
('GH238', 'TT004', 'LG004', 'Rảnh'),
('GH239', 'TT004', 'LG004', 'Rảnh'),
('GH240', 'TT004', 'LG004', 'Rảnh'),
('GH241', 'TT004', 'LG004', 'Rảnh'),
('GH242', 'TT004', 'LG004', 'Rảnh'),
('GH243', 'TT004', 'LG004', 'Rảnh'),
('GH244', 'TT004', 'LG004', 'Rảnh')
ON DUPLICATE KEY UPDATE trangThai = VALUES(trangThai);

-- Chặng Tàu
INSERT INTO ChangTau (maChang, soKMToiThieu, soKMToiDa, moTa, giaTien) VALUES
('CH001', 0, 500, 'Chặng ngắn (dưới 500km)', 50000),
('CH002', 500, 1000, 'Chặng trung bình (500-1000km)', 100000),
('CH003', 1000, 2000, 'Chặng dài (1000-2000km)', 200000)
ON DUPLICATE KEY UPDATE moTa = VALUES(moTa);

-- Chuyến Tàu
INSERT INTO ChuyenTau (maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang, trangThai) VALUES
('CT001', 'DM001', 'NV001', 'GA001', 'GA002', '2024-06-01 06:00:00', '2024-06-02 06:00:00', 1726, 'CH003', 'Đã đến'),
('CT002', 'DM002', 'NV002', 'GA001', 'GA003', '2024-06-02 08:00:00', '2024-06-02 22:00:00', 791, 'CH002', 'Đã đến'),
('CT003', 'DM003', 'NV003', 'GA002', 'GA003', '2024-06-03 07:00:00', '2024-06-03 18:00:00', 935, 'CH002', 'Đã đến')
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
('CT004', 'DM001', 'NV001', 'GA001', 'GA002', '2026-05-12 06:00:00', '2026-05-13 06:00:00', 1726, 'CH003', 'Chưa khởi hành'),
('CT005', 'DM002', 'NV003', 'GA001', 'GA003', '2026-05-12 08:00:00', '2026-05-12 22:00:00', 791,  'CH002', 'Chưa khởi hành')
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
 '2024-05-28 09:00:00', 'Đã thanh toán', '2024-06-01 06:00:00', '2024-06-02 06:00:00', 1, 'LG001', 'LV001', 'BG005', 300000, 1),
('VE002', 'CT001', 'LV002', 'GH002', 'GA001', 'GA002', 'Ga Hà Nội', 'Ga Sài Gòn',
 '2024-05-28 09:05:00', 'Đã thanh toán', '2024-06-01 06:00:00', '2024-06-02 06:00:00', 1, 'LG001', 'LV002', 'BG005', 225000, 1),
('VE003', 'CT001', 'LV001', 'GH005', 'GA001', 'GA002', 'Ga Hà Nội', 'Ga Sài Gòn',
 '2024-05-29 10:00:00', 'Đã thanh toán', '2024-06-01 06:00:00', '2024-06-02 06:00:00', 3, 'LG004', 'LV001', 'BG006', 500000, 1),
-- CT002: Hà Nội → Đà Nẵng, 2024-06-02
('VE004', 'CT002', 'LV001', 'GH001', 'GA001', 'GA003', 'Ga Hà Nội', 'Ga Đà Nẵng',
 '2024-05-30 08:00:00', 'Đã thanh toán', '2024-06-02 08:00:00', '2024-06-02 22:00:00', 1, 'LG001', 'LV001', 'BG003', 180000, 1),
('VE005', 'CT002', 'LV002', 'GH004', 'GA001', 'GA003', 'Ga Hà Nội', 'Ga Đà Nẵng',
 '2024-05-30 08:10:00', 'Đã thanh toán', '2024-06-02 08:00:00', '2024-06-02 22:00:00', 2, 'LG003', 'LV002', 'BG004', 187500, 1),
-- CT003: Sài Gòn → Đà Nẵng, 2024-06-03
('VE006', 'CT003', 'LV001', 'GH002', 'GA002', 'GA003', 'Ga Sài Gòn', 'Ga Đà Nẵng',
 '2024-06-01 07:00:00', 'Đã thanh toán', '2024-06-03 07:00:00', '2024-06-03 18:00:00', 2, 'LG001', 'LV001', 'BG003', 180000, 1)
ON DUPLICATE KEY UPDATE trangThai = VALUES(trangThai);

-- Vé chuyến 12/5/2026 (đã đặt trước + còn trống để kiểm thử)
-- CT004: Hà Nội → Sài Gòn, 2026-05-12
INSERT INTO Ve (maVe, maChuyen, maLoaiVe, maSoGhe, maGaDi, maGaDen, tenGaDi, tenGaDen,
                ngayIn, trangThai, gioDi, gioDenDuKien, soToa, loaiCho, loaiVe, maBangGia, giaThanhToan, isActive) VALUES
('VE007', 'CT004', 'LV001', 'GH001', 'GA001', 'GA002', 'Ga Hà Nội', 'Ga Sài Gòn',
 '2026-04-15 10:00:00', 'Đã thanh toán', '2026-05-12 06:00:00', '2026-05-13 06:00:00', 1, 'LG001', 'LV001', 'BG007', 350000, 1),
('VE008', 'CT004', 'LV002', 'GH002', 'GA001', 'GA002', 'Ga Hà Nội', 'Ga Sài Gòn',
 '2026-04-20 11:00:00', 'Đã thanh toán', '2026-05-12 06:00:00', '2026-05-13 06:00:00', 1, 'LG001', 'LV002', 'BG007', 262500, 1),
('VE009', 'CT004', 'LV001', 'GH005', 'GA001', 'GA002', 'Ga Hà Nội', 'Ga Sài Gòn',
 '2026-04-20 11:10:00', 'Đã thanh toán', '2026-05-12 06:00:00', '2026-05-13 06:00:00', 4, 'LG004', 'LV001', 'BG008', 600000, 1),
('VE010', 'CT004', 'LV001', 'GH004', 'GA001', 'GA002', 'Ga Hà Nội', 'Ga Sài Gòn',
 NULL,                 'Rảnh', '2026-05-12 06:00:00', '2026-05-13 06:00:00', 3, 'LG003', 'LV001', 'BG011', 520000, 1),
-- CT005: Hà Nội → Đà Nẵng, 2026-05-12
('VE011', 'CT005', 'LV001', 'GH001', 'GA001', 'GA003', 'Ga Hà Nội', 'Ga Đà Nẵng',
 '2026-04-20 14:00:00', 'Đã thanh toán', '2026-05-12 08:00:00', '2026-05-12 22:00:00', 1, 'LG001', 'LV001', 'BG009', 200000, 1),
('VE012', 'CT005', 'LV003', 'GH004', 'GA001', 'GA003', 'Ga Hà Nội', 'Ga Đà Nẵng',
 NULL,                 'Rảnh', '2026-05-12 08:00:00', '2026-05-12 22:00:00', 2, 'LG003', 'LV003', 'BG010', 232000, 1)
ON DUPLICATE KEY UPDATE trangThai = VALUES(trangThai);

-- Hóa Đơn
INSERT INTO HoaDon (maHoaDon, maNV, maKH, tenKH, soDienThoai, ngayLap, phuongThucThanhToan, trangThai) VALUES
-- Hóa đơn quá khứ
('HD001', 'NV001', 'KH01', 'Phạm Văn Dũng',   '0934567890', '2024-05-28 09:10:00', 'Tiền mặt',    'Hoàn tất'),
('HD002', 'NV001', 'KH03', 'Hoàng Văn Phúc',   '0956789012', '2024-05-29 10:05:00', 'Tiền mặt',    'Hoàn tất'),
('HD003', 'NV003', 'KH02', 'Nguyễn Thị Em',    '0945678901', '2024-05-30 08:15:00', 'Chuyển khoản','Hoàn tất'),
('HD004', 'NV001', 'KH03', 'Hoàng Văn Phúc',   '0956789012', '2024-06-01 07:05:00', 'Tiền mặt',    'Hoàn tất'),
-- Hóa đơn đặt trước chuyến 12/5/2026
('HD005', 'NV001', 'KH01', 'Phạm Văn Dũng',   '0934567890', '2026-04-15 10:10:00', 'Chuyển khoản','Hoàn tất'),
('HD006', 'NV002', 'KH02', 'Nguyễn Thị Em',    '0945678901', '2026-04-20 11:15:00', 'Tiền mặt',    'Hoàn tất')
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

-- ====================================================================
-- Dữ liệu bổ sung: chuyến tàu quá khứ 2025 + vé hoàn/đổi cho thống kê
-- ====================================================================

-- Chuyến tàu quá khứ năm 2025 (Đã đến)
INSERT INTO ChuyenTau (maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang, trangThai) VALUES
('CT006', 'DM001', 'NV001', 'GA001', 'GA002', '2025-01-15 06:00:00', '2025-01-16 06:00:00', 1726, 'CH003', 'Đã đến'),
('CT007', 'DM002', 'NV002', 'GA001', 'GA003', '2025-02-20 07:00:00', '2025-02-20 21:00:00', 791,  'CH002', 'Đã đến'),
('CT008', 'DM003', 'NV003', 'GA002', 'GA001', '2025-03-10 08:00:00', '2025-03-11 08:00:00', 1726, 'CH003', 'Đã đến'),
('CT009', 'DM001', 'NV001', 'GA001', 'GA004', '2025-04-05 06:30:00', '2025-04-05 18:00:00', 688,  'CH002', 'Đã đến'),
('CT010', 'DM002', 'NV003', 'GA003', 'GA005', '2025-05-20 09:00:00', '2025-05-20 15:00:00', 524,  'CH002', 'Đã đến')
ON DUPLICATE KEY UPDATE trangThai = VALUES(trangThai);

-- Chi tiết chuyến tàu mới
INSERT INTO ChiTietChuyenTau (maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES
('CT006', 'TT001', 1, 100), ('CT006', 'TT003', 2, 48), ('CT006', 'TT004', 3, 32),
('CT007', 'TT002', 1, 64),  ('CT007', 'TT003', 2, 48),
('CT008', 'TT001', 1, 100), ('CT008', 'TT002', 2, 64),
('CT009', 'TT001', 1, 100), ('CT009', 'TT003', 2, 48),
('CT010', 'TT002', 1, 64),  ('CT010', 'TT004', 2, 32)
ON DUPLICATE KEY UPDATE sucChua = VALUES(sucChua);

-- Bảng giá 2025 bổ sung cho chặng CH002 và loại ghế LG002
INSERT INTO BangGia (maBangGia, maChang, loaiGhe, giaCoBan, ngayBatDau, ngayKetThuc) VALUES
('BG012', 'CH002', 'LG002', 240000, '2025-01-01 00:00:00', '2026-12-31 23:59:59'),
('BG013', 'CH003', 'LG002', 420000, '2025-01-01 00:00:00', '2026-12-31 23:59:59')
ON DUPLICATE KEY UPDATE giaCoBan = VALUES(giaCoBan);

-- ====================================================================
-- Vé cho các chuyến 2025
-- ====================================================================
INSERT INTO Ve (maVe, maChuyen, maLoaiVe, maSoGhe, maGaDi, maGaDen, tenGaDi, tenGaDen,
                ngayIn, trangThai, gioDi, gioDenDuKien, soToa, loaiCho, loaiVe, maBangGia, giaThanhToan, isActive) VALUES
-- CT006: HN→SG 15/01/2025 – VE013..VE018
('VE013','CT006','LV001','GH010','GA001','GA002','Ga Hà Nội','Ga Sài Gòn',
 '2025-01-10 09:00:00','Đã thanh toán','2025-01-15 06:00:00','2025-01-16 06:00:00',1,'LG001','LV001','BG007',350000,1),
('VE014','CT006','LV002','GH011','GA001','GA002','Ga Hà Nội','Ga Sài Gòn',
 '2025-01-10 09:10:00','Đã thanh toán','2025-01-15 06:00:00','2025-01-16 06:00:00',1,'LG001','LV002','BG007',262500,1),
('VE015','CT006','LV001','GH170','GA001','GA002','Ga Hà Nội','Ga Sài Gòn',
 '2025-01-11 10:00:00','Đã hoàn','2025-01-15 06:00:00','2025-01-16 06:00:00',2,'LG003','LV001','BG011',520000,1),
('VE016','CT006','LV001','GH213','GA001','GA002','Ga Hà Nội','Ga Sài Gòn',
 '2025-01-11 10:05:00','Đã đổi','2025-01-15 06:00:00','2025-01-16 06:00:00',3,'LG004','LV001','BG008',600000,1),
('VE017','CT006','LV003','GH012','GA001','GA002','Ga Hà Nội','Ga Sài Gòn',
 '2025-01-12 08:00:00','Đã thanh toán','2025-01-15 06:00:00','2025-01-16 06:00:00',1,'LG001','LV003','BG007',280000,1),
('VE018','CT006','LV001','GH013','GA001','GA002','Ga Hà Nội','Ga Sài Gòn',
 '2025-01-12 08:05:00','Đã hoàn','2025-01-15 06:00:00','2025-01-16 06:00:00',1,'LG001','LV001','BG007',350000,1),

-- CT007: HN→ĐN 20/02/2025 – VE019..VE023
('VE019','CT007','LV001','GH101','GA001','GA003','Ga Hà Nội','Ga Đà Nẵng',
 '2025-02-15 09:00:00','Đã thanh toán','2025-02-20 07:00:00','2025-02-20 21:00:00',1,'LG002','LV001','BG012',240000,1),
('VE020','CT007','LV002','GH102','GA001','GA003','Ga Hà Nội','Ga Đà Nẵng',
 '2025-02-15 09:10:00','Đã thanh toán','2025-02-20 07:00:00','2025-02-20 21:00:00',1,'LG002','LV002','BG012',180000,1),
('VE021','CT007','LV001','GH165','GA001','GA003','Ga Hà Nội','Ga Đà Nẵng',
 '2025-02-16 10:00:00','Đã đổi','2025-02-20 07:00:00','2025-02-20 21:00:00',2,'LG003','LV001','BG010',290000,1),
('VE022','CT007','LV001','GH103','GA001','GA003','Ga Hà Nội','Ga Đà Nẵng',
 '2025-02-17 08:00:00','Đã hoàn','2025-02-20 07:00:00','2025-02-20 21:00:00',1,'LG002','LV001','BG012',240000,1),
('VE023','CT007','LV004','GH104','GA001','GA003','Ga Hà Nội','Ga Đà Nẵng',
 '2025-02-17 08:05:00','Đã thanh toán','2025-02-20 07:00:00','2025-02-20 21:00:00',1,'LG002','LV004','BG012',120000,1),

-- CT008: SG→HN 10/03/2025 – VE024..VE027
('VE024','CT008','LV001','GH020','GA002','GA001','Ga Sài Gòn','Ga Hà Nội',
 '2025-03-05 10:00:00','Đã thanh toán','2025-03-10 08:00:00','2025-03-11 08:00:00',1,'LG001','LV001','BG007',350000,1),
('VE025','CT008','LV002','GH021','GA002','GA001','Ga Sài Gòn','Ga Hà Nội',
 '2025-03-05 10:10:00','Đã thanh toán','2025-03-10 08:00:00','2025-03-11 08:00:00',1,'LG001','LV002','BG007',262500,1),
('VE026','CT008','LV001','GH214','GA002','GA001','Ga Sài Gòn','Ga Hà Nội',
 '2025-03-06 09:00:00','Đã hoàn','2025-03-10 08:00:00','2025-03-11 08:00:00',2,'LG004','LV001','BG008',600000,1),
('VE027','CT008','LV001','GH022','GA002','GA001','Ga Sài Gòn','Ga Hà Nội',
 '2025-03-07 11:00:00','Đã đổi','2025-03-10 08:00:00','2025-03-11 08:00:00',1,'LG001','LV001','BG007',350000,1),

-- CT009: HN→Huế 05/04/2025 – VE028..VE030
('VE028','CT009','LV001','GH030','GA001','GA004','Ga Hà Nội','Ga Huế',
 '2025-04-01 08:00:00','Đã thanh toán','2025-04-05 06:30:00','2025-04-05 18:00:00',1,'LG001','LV001','BG009',200000,1),
('VE029','CT009','LV002','GH031','GA001','GA004','Ga Hà Nội','Ga Huế',
 '2025-04-01 08:05:00','Đã thanh toán','2025-04-05 06:30:00','2025-04-05 18:00:00',1,'LG001','LV002','BG009',150000,1),
('VE030','CT009','LV001','GH171','GA001','GA004','Ga Hà Nội','Ga Huế',
 '2025-04-02 09:00:00','Đã hoàn','2025-04-05 06:30:00','2025-04-05 18:00:00',2,'LG003','LV001','BG010',290000,1)
ON DUPLICATE KEY UPDATE trangThai = VALUES(trangThai);

-- ====================================================================
-- Hóa Đơn bổ sung (chuyến 2025)
-- ====================================================================
INSERT INTO HoaDon (maHoaDon, maNV, maKH, tenKH, soDienThoai, ngayLap, phuongThucThanhToan, trangThai) VALUES
('HD007','NV001','KH04','Trần Minh Hiếu',  '0967890123','2025-01-10 09:15:00','Tiền mặt',    'Hoàn tất'),
('HD008','NV003','KH05','Lê Thị Lan',       '0978901234','2025-01-11 10:10:00','Chuyển khoản','Hoàn tất'),
('HD009','NV001','KH06','Võ Thanh Tùng',   '0989012345','2025-01-12 08:10:00','Tiền mặt',    'Hoàn tất'),
('HD010','NV002','KH04','Trần Minh Hiếu',  '0967890123','2025-02-15 09:15:00','Chuyển khoản','Hoàn tất'),
('HD011','NV001','KH07','Nguyễn Thành Đạt','0990123456','2025-02-17 08:10:00','Tiền mặt',    'Hoàn tất'),
('HD012','NV003','KH05','Lê Thị Lan',       '0978901234','2025-03-05 10:15:00','Chuyển khoản','Hoàn tất'),
('HD013','NV001','KH08','Phạm Thị Hoa',    '0901234560','2025-03-06 09:05:00','Tiền mặt',    'Hoàn tất'),
('HD014','NV002','KH06','Võ Thanh Tùng',   '0989012345','2025-04-01 08:10:00','Chuyển khoản','Hoàn tất'),
('HD015','NV001','KH07','Nguyễn Thành Đạt','0990123456','2025-04-02 09:05:00','Tiền mặt',    'Hoàn tất')
ON DUPLICATE KEY UPDATE trangThai = VALUES(trangThai);

-- ====================================================================
-- Chi Tiết Hóa Đơn bổ sung
-- ====================================================================
INSERT INTO ChiTietHoaDon (maHoaDon, maVe, maLoaiVe, giaGoc, giaDaKM, moTa) VALUES
-- HD007: KH04 mua VE013+VE014 CT006 HN→SG
('HD007','VE013','LV001',350000,350000,'Vé HN-SG ghế ngồi cứng (thường)'),
('HD007','VE014','LV002',350000,262500,'Vé HN-SG ghế ngồi cứng (học sinh -25%)'),
-- HD008: KH05 mua VE015 (hoàn) + VE016 (đổi) CT006 HN→SG
('HD008','VE015','LV001',520000,520000,'Vé HN-SG giường nằm cứng (thường) – đã hoàn'),
('HD008','VE016','LV001',600000,600000,'Vé HN-SG giường nằm mềm (thường) – đã đổi'),
-- HD009: KH06 mua VE017+VE018 CT006 HN→SG
('HD009','VE017','LV003',350000,280000,'Vé HN-SG ghế ngồi cứng (cao tuổi -20%)'),
('HD009','VE018','LV001',350000,350000,'Vé HN-SG ghế ngồi cứng (thường) – đã hoàn'),
-- HD010: KH04 mua VE019+VE020 CT007 HN→ĐN
('HD010','VE019','LV001',240000,240000,'Vé HN-ĐN ghế ngồi mềm (thường)'),
('HD010','VE020','LV002',240000,180000,'Vé HN-ĐN ghế ngồi mềm (học sinh -25%)'),
-- HD011: KH07 mua VE021+VE022+VE023 CT007 HN→ĐN
('HD011','VE021','LV001',290000,290000,'Vé HN-ĐN giường nằm cứng (thường) – đã đổi'),
('HD011','VE022','LV001',240000,240000,'Vé HN-ĐN ghế ngồi mềm (thường) – đã hoàn'),
('HD011','VE023','LV004',240000,120000,'Vé HN-ĐN ghế ngồi mềm (trẻ em -50%)'),
-- HD012: KH05 mua VE024+VE025 CT008 SG→HN
('HD012','VE024','LV001',350000,350000,'Vé SG-HN ghế ngồi cứng (thường)'),
('HD012','VE025','LV002',350000,262500,'Vé SG-HN ghế ngồi cứng (học sinh -25%)'),
-- HD013: KH08 mua VE026 (hoàn) + VE027 (đổi) CT008 SG→HN
('HD013','VE026','LV001',600000,600000,'Vé SG-HN giường nằm mềm (thường) – đã hoàn'),
('HD013','VE027','LV001',350000,350000,'Vé SG-HN ghế ngồi cứng (thường) – đã đổi'),
-- HD014: KH06 mua VE028+VE029 CT009 HN→Huế
('HD014','VE028','LV001',200000,200000,'Vé HN-Huế ghế ngồi cứng (thường)'),
('HD014','VE029','LV002',200000,150000,'Vé HN-Huế ghế ngồi cứng (học sinh -25%)'),
-- HD015: KH07 mua VE030 CT009 HN→Huế (hoàn)
('HD015','VE030','LV001',290000,290000,'Vé HN-Huế giường nằm cứng (thường) – đã hoàn')
ON DUPLICATE KEY UPDATE moTa = VALUES(moTa);

-- ====================================================================
-- Sửa loaiGhe sai (tên → mã) trong bảng Ghe (nếu tồn tại từ lần chạy cũ)
-- ====================================================================
UPDATE Ghe SET loaiGhe = 'LG001' WHERE loaiGhe = 'Ghế ngồi cứng';
UPDATE Ghe SET loaiGhe = 'LG002' WHERE loaiGhe = 'Ghế ngồi mềm';
UPDATE Ghe SET loaiGhe = 'LG003' WHERE loaiGhe = 'Giường nằm cứng';
UPDATE Ghe SET loaiGhe = 'LG004' WHERE loaiGhe = 'Giường nằm mềm';
UPDATE Ghe SET loaiGhe = 'LG005' WHERE loaiGhe = 'VIP';
