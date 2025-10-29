-- QLVeTau Database Seed Script
-- Script khởi tạo database và dữ liệu mẫu cho hệ thống quản lý vé tàu hỏa
-- Idempotent: có thể chạy nhiều lần mà không gây lỗi

USE master;
GO

-- Tạo database nếu chưa tồn tại
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'QLVeTau')
BEGIN
    CREATE DATABASE QLVeTau;
    PRINT N'Database QLVeTau đã được tạo';
END
ELSE
BEGIN
    PRINT N'Database QLVeTau đã tồn tại';
END
GO

USE QLVeTau;
GO

-- =============================================
-- Tạo các bảng (Tables)
-- =============================================

-- Bảng LoaiNV (Loại Nhân Viên)
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[LoaiNV]') AND type in (N'U'))
BEGIN
    CREATE TABLE LoaiNV (
        maLoai NVARCHAR(10) PRIMARY KEY,
        tenLoai NVARCHAR(100) NOT NULL,
        moTa NVARCHAR(255)
    );
    PRINT N'Bảng LoaiNV đã được tạo';
END
ELSE
BEGIN
    PRINT N'Bảng LoaiNV đã tồn tại';
END
GO

-- Bảng NhanVien (Nhân Viên) với cột trangThai có DEFAULT
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[NhanVien]') AND type in (N'U'))
BEGIN
    CREATE TABLE NhanVien (
        maNV NVARCHAR(10) PRIMARY KEY,
        tenNV NVARCHAR(100) NOT NULL,
        soDienThoai NVARCHAR(20),
        diaChi NVARCHAR(255),
        ngaySinh DATE,
        maLoaiNV NVARCHAR(10),
        trangThai NVARCHAR(20) DEFAULT 'active',
        FOREIGN KEY (maLoaiNV) REFERENCES LoaiNV(maLoai)
    );
    PRINT N'Bảng NhanVien đã được tạo';
END
ELSE
BEGIN
    PRINT N'Bảng NhanVien đã tồn tại';
END
GO

-- Bảng TaiKhoan (Tài Khoản)
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[TaiKhoan]') AND type in (N'U'))
BEGIN
    CREATE TABLE TaiKhoan (
        maTK NVARCHAR(10) PRIMARY KEY,
        maNV NVARCHAR(10),
        tenTaiKhoan NVARCHAR(50) NOT NULL UNIQUE,
        matKhau NVARCHAR(255) NOT NULL,
        trangThai NVARCHAR(20) DEFAULT 'active',
        FOREIGN KEY (maNV) REFERENCES NhanVien(maNV)
    );
    PRINT N'Bảng TaiKhoan đã được tạo';
END
ELSE
BEGIN
    PRINT N'Bảng TaiKhoan đã tồn tại';
END
GO

-- Bảng KhachHang (Khách Hàng)
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[KhachHang]') AND type in (N'U'))
BEGIN
    CREATE TABLE KhachHang (
        maKH NVARCHAR(10) PRIMARY KEY,
        tenKH NVARCHAR(100) NOT NULL,
        soDienThoai NVARCHAR(20),
        email NVARCHAR(100),
        CCCD NVARCHAR(20),
        diaChi NVARCHAR(255)
    );
    PRINT N'Bảng KhachHang đã được tạo';
END
ELSE
BEGIN
    PRINT N'Bảng KhachHang đã tồn tại';
END
GO

-- Bảng ChuyenTau (Chuyến Tàu)
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[ChuyenTau]') AND type in (N'U'))
BEGIN
    CREATE TABLE ChuyenTau (
        maCT NVARCHAR(10) PRIMARY KEY,
        tenCT NVARCHAR(100) NOT NULL,
        gaDi NVARCHAR(100) NOT NULL,
        gaDen NVARCHAR(100) NOT NULL,
        thoiGianDi DATETIME NOT NULL,
        thoiGianDen DATETIME NOT NULL
    );
    PRINT N'Bảng ChuyenTau đã được tạo';
END
ELSE
BEGIN
    PRINT N'Bảng ChuyenTau đã tồn tại';
END
GO

-- =============================================
-- Chèn dữ liệu mẫu (Sample Data)
-- =============================================

-- Dữ liệu cho bảng LoaiNV
IF NOT EXISTS (SELECT * FROM LoaiNV WHERE maLoai = 'LNV01')
BEGIN
    INSERT INTO LoaiNV(maLoai, tenLoai, moTa) VALUES 
    ('LNV01', N'Nhân viên bán vé', N'Nhân viên phụ trách bán vé tàu'),
    ('LNV02', N'Quản lý', N'Quản lý nhân viên và giám sát hoạt động'),
    ('LNV03', N'Admin', N'Quản trị hệ thống');
    PRINT N'Đã thêm dữ liệu vào bảng LoaiNV';
END
ELSE
BEGIN
    PRINT N'Dữ liệu bảng LoaiNV đã tồn tại';
END
GO

-- Dữ liệu cho bảng NhanVien
-- Sử dụng INSERT với danh sách cột rõ ràng để bỏ qua cột trangThai (có DEFAULT)
IF NOT EXISTS (SELECT * FROM NhanVien WHERE maNV = 'NV001')
BEGIN
    INSERT INTO NhanVien(maNV, tenNV, soDienThoai, diaChi, ngaySinh, maLoaiNV) VALUES
    ('NV001', N'Nguyễn Văn An', '0901234567', N'123 Lê Lợi, Q1, TP.HCM', '1990-01-15', 'LNV03'),
    ('NV002', N'Trần Thị Bình', '0912345678', N'456 Nguyễn Huệ, Q1, TP.HCM', '1992-05-20', 'LNV02'),
    ('NV003', N'Lê Văn Cường', '0923456789', N'789 Trần Hưng Đạo, Q5, TP.HCM', '1995-08-10', 'LNV01'),
    ('NV004', N'Phạm Thị Dung', '0934567890', N'321 Hai Bà Trưng, Q3, TP.HCM', '1993-03-25', 'LNV01'),
    ('NV005', N'Hoàng Văn Em', '0945678901', N'654 Lý Thường Kiệt, Q10, TP.HCM', '1994-11-30', 'LNV01');
    PRINT N'Đã thêm dữ liệu vào bảng NhanVien';
END
ELSE
BEGIN
    PRINT N'Dữ liệu bảng NhanVien đã tồn tại';
END
GO

-- Dữ liệu cho bảng TaiKhoan
IF NOT EXISTS (SELECT * FROM TaiKhoan WHERE maTK = 'TK001')
BEGIN
    INSERT INTO TaiKhoan(maTK, maNV, tenTaiKhoan, matKhau, trangThai) VALUES
    ('TK001', 'NV001', 'admin', 'admin123', 'active'),
    ('TK002', 'NV002', 'quanly01', 'ql123456', 'active'),
    ('TK003', 'NV003', 'nvbv01', 'nv123456', 'active'),
    ('TK004', 'NV004', 'nvbv02', 'nv123456', 'active'),
    ('TK005', 'NV005', 'nvbv03', 'nv123456', 'active');
    PRINT N'Đã thêm dữ liệu vào bảng TaiKhoan';
END
ELSE
BEGIN
    PRINT N'Dữ liệu bảng TaiKhoan đã tồn tại';
END
GO

-- Dữ liệu cho bảng KhachHang
IF NOT EXISTS (SELECT * FROM KhachHang WHERE maKH = 'KH001')
BEGIN
    INSERT INTO KhachHang(maKH, tenKH, soDienThoai, email, CCCD, diaChi) VALUES
    ('KH001', N'Nguyễn Minh Anh', '0987654321', 'minhanh@email.com', '001234567890', N'12 Võ Văn Tần, Q3, TP.HCM'),
    ('KH002', N'Trần Văn Bảo', '0976543210', 'vanbao@email.com', '001234567891', N'34 Điện Biên Phủ, Q10, TP.HCM'),
    ('KH003', N'Lê Thị Cẩm', '0965432109', 'thicam@email.com', '001234567892', N'56 Cách Mạng Tháng 8, Q3, TP.HCM'),
    ('KH004', N'Phạm Văn Đạt', '0954321098', 'vandat@email.com', '001234567893', N'78 Nguyễn Thị Minh Khai, Q1, TP.HCM'),
    ('KH005', N'Hoàng Thị Em', '0943210987', 'thiem@email.com', '001234567894', N'90 Lê Văn Sỹ, Q3, TP.HCM');
    PRINT N'Đã thêm dữ liệu vào bảng KhachHang';
END
ELSE
BEGIN
    PRINT N'Dữ liệu bảng KhachHang đã tồn tại';
END
GO

-- Dữ liệu cho bảng ChuyenTau
IF NOT EXISTS (SELECT * FROM ChuyenTau WHERE maCT = 'CT001')
BEGIN
    INSERT INTO ChuyenTau(maCT, tenCT, gaDi, gaDen, thoiGianDi, thoiGianDen) VALUES
    ('CT001', N'SE1 - Thống Nhất', N'Sài Gòn', N'Hà Nội', '2024-01-15 06:00:00', '2024-01-16 08:00:00'),
    ('CT002', N'SE2 - Thống Nhất', N'Hà Nội', N'Sài Gòn', '2024-01-15 19:00:00', '2024-01-16 21:00:00'),
    ('CT003', N'SNT1', N'Sài Gòn', N'Nha Trang', '2024-01-15 08:00:00', '2024-01-15 18:00:00'),
    ('CT004', N'HP1', N'Hà Nội', N'Hải Phòng', '2024-01-15 07:00:00', '2024-01-15 09:30:00'),
    ('CT005', N'DN1', N'Sài Gòn', N'Đà Nẵng', '2024-01-15 10:00:00', '2024-01-15 22:00:00');
    PRINT N'Đã thêm dữ liệu vào bảng ChuyenTau';
END
ELSE
BEGIN
    PRINT N'Dữ liệu bảng ChuyenTau đã tồn tại';
END
GO

PRINT N'===========================================';
PRINT N'Hoàn tất khởi tạo database và dữ liệu mẫu';
PRINT N'===========================================';
GO
