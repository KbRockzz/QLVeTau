-- Migration: Add default admin account
-- Run this against an existing QLTauHoa database that was created without seed data
-- Safe to run multiple times (uses INSERT IGNORE)

USE QLTauHoa;

-- Ensure required LoaiNV entries exist
INSERT IGNORE INTO LoaiNV (maLoai, tenLoai, moTa) VALUES
    ('LNV01', 'Nhân viên quầy', 'Bán vé, đổi hoàn vé'),
    ('LNV02', 'Quản lý ca', 'Duyệt hoàn, xem báo cáo'),
    ('LNV03', 'Admin hệ thống', 'Quản trị cấu hình');

-- Ensure admin employee exists
INSERT IGNORE INTO NhanVien (maNV, tenNV, soDienThoai, diaChi, ngaySinh, maLoaiNV, trangThai) VALUES
    ('NV00', 'Quản trị viên', '0900000000', 'Hệ thống', '2000-01-01', 'LNV03', 'active');

-- Add admin account (username: admin, password: admin123)
INSERT IGNORE INTO TaiKhoan (maTK, maNV, tenTaiKhoan, matKhau, trangThai) VALUES
    ('TK00', 'NV00', 'admin', 'admin123', 'Hoạt động');

SELECT 'Migration completed: admin account added (username: admin, password: admin123).' AS status;
