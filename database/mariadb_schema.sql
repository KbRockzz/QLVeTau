-- QLVeTau Database Schema for MariaDB
-- Hệ thống quản lý vé tàu

CREATE DATABASE IF NOT EXISTS QLTauHoa
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE QLTauHoa;

-- Bảng LoaiNV (Loại Nhân Viên)
CREATE TABLE IF NOT EXISTS LoaiNV (
    maLoai VARCHAR(10) PRIMARY KEY,
    tenLoai VARCHAR(100) NOT NULL,
    moTa VARCHAR(255),
    isActive TINYINT(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Bảng NhanVien (Nhân Viên)
CREATE TABLE IF NOT EXISTS NhanVien (
    maNV VARCHAR(10) PRIMARY KEY,
    tenNV VARCHAR(100) NOT NULL,
    soDienThoai VARCHAR(20),
    diaChi VARCHAR(255),
    ngaySinh DATE,
    maLoaiNV VARCHAR(10),
    trangThai VARCHAR(20) DEFAULT 'Đang hoạt động',
    isActive TINYINT(1) DEFAULT 1,
    FOREIGN KEY (maLoaiNV) REFERENCES LoaiNV(maLoai)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Bảng TaiKhoan (Tài Khoản)
CREATE TABLE IF NOT EXISTS TaiKhoan (
    maTK VARCHAR(10) PRIMARY KEY,
    maNV VARCHAR(10),
    tenTaiKhoan VARCHAR(50) NOT NULL UNIQUE,
    matKhau VARCHAR(255) NOT NULL,
    trangThai VARCHAR(20) DEFAULT 'Hoạt động',
    isActive TINYINT(1) DEFAULT 1,
    FOREIGN KEY (maNV) REFERENCES NhanVien(maNV)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Bảng KhachHang (Khách Hàng)
CREATE TABLE IF NOT EXISTS KhachHang (
    maKhachHang VARCHAR(20) PRIMARY KEY,
    tenKhachHang VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    soDienThoai VARCHAR(20),
    isActive TINYINT(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Bảng Ga (Ga Tàu)
CREATE TABLE IF NOT EXISTS Ga (
    maGa VARCHAR(20) PRIMARY KEY,
    tenGa VARCHAR(100) NOT NULL,
    moTa VARCHAR(255),
    tinhTrang VARCHAR(20),
    diaChi VARCHAR(255),
    isActive TINYINT(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Bảng DauMay (Đầu Máy)
CREATE TABLE IF NOT EXISTS DauMay (
    maDauMay VARCHAR(20) PRIMARY KEY,
    loaiDauMay VARCHAR(50),
    tenDauMay VARCHAR(100),
    namSX INT,
    lanBaoTriGanNhat DATETIME,
    trangThai VARCHAR(20),
    isActive TINYINT(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Bảng ToaTau (Toa Tàu)
CREATE TABLE IF NOT EXISTS ToaTau (
    maToa VARCHAR(20) PRIMARY KEY,
    loaiToa VARCHAR(50),
    samSX INT,
    trangThai VARCHAR(20),
    sucChua INT,
    isActive TINYINT(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Bảng LoaiGhe (Loại Ghế)
CREATE TABLE IF NOT EXISTS LoaiGhe (
    maLoai VARCHAR(20) PRIMARY KEY,
    tenLoai VARCHAR(100) NOT NULL,
    moTa VARCHAR(255),
    isActive TINYINT(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Bảng Ghe (Ghế)
CREATE TABLE IF NOT EXISTS Ghe (
    maGhe VARCHAR(20) PRIMARY KEY,
    maToa VARCHAR(20),
    loaiGhe VARCHAR(20),
    trangThai VARCHAR(20),
    FOREIGN KEY (maToa) REFERENCES ToaTau(maToa)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Bảng ChangTau (Chặng Tàu)
CREATE TABLE IF NOT EXISTS ChangTau (
    maChang VARCHAR(20) PRIMARY KEY,
    soKMToiThieu INT,
    soKMToiDa INT,
    moTa VARCHAR(255),
    giaTien FLOAT,
    isActive TINYINT(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Bảng ChuyenTau (Chuyến Tàu)
CREATE TABLE IF NOT EXISTS ChuyenTau (
    maChuyen VARCHAR(20) PRIMARY KEY,
    maDauMay VARCHAR(20),
    maNV VARCHAR(10),
    maGaDi VARCHAR(20),
    maGaDen VARCHAR(20),
    gioDi DATETIME,
    gioDen DATETIME,
    soKm INT,
    maChang VARCHAR(20),
    trangThai VARCHAR(20),
    isActive TINYINT(1) DEFAULT 1,
    FOREIGN KEY (maDauMay) REFERENCES DauMay(maDauMay),
    FOREIGN KEY (maNV) REFERENCES NhanVien(maNV),
    FOREIGN KEY (maGaDi) REFERENCES Ga(maGa),
    FOREIGN KEY (maGaDen) REFERENCES Ga(maGa),
    FOREIGN KEY (maChang) REFERENCES ChangTau(maChang)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Bảng ChiTietChuyenTau
CREATE TABLE IF NOT EXISTS ChiTietChuyenTau (
    maChuyenTau VARCHAR(20),
    maToaTau VARCHAR(20),
    soThuTuToa INT,
    sucChua INT,
    isActive TINYINT(1) DEFAULT 1,
    PRIMARY KEY (maChuyenTau, maToaTau),
    FOREIGN KEY (maChuyenTau) REFERENCES ChuyenTau(maChuyen),
    FOREIGN KEY (maToaTau) REFERENCES ToaTau(maToa)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Bảng LoaiVe (Loại Vé)
CREATE TABLE IF NOT EXISTS LoaiVe (
    maLoaiVe VARCHAR(20) PRIMARY KEY,
    tenLoai VARCHAR(100) NOT NULL,
    heSoGia DECIMAL(5,2),
    moTa VARCHAR(255),
    isActive TINYINT(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Bảng BangGia (Bảng Giá)
CREATE TABLE IF NOT EXISTS BangGia (
    maBangGia VARCHAR(20) PRIMARY KEY,
    maChang VARCHAR(20),
    loaiGhe VARCHAR(20),
    giaCoBan FLOAT,
    ngayBatDau DATETIME,
    ngayKetThuc DATETIME,
    isActive TINYINT(1) DEFAULT 1,
    FOREIGN KEY (maChang) REFERENCES ChangTau(maChang)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Bảng HoaDon (Hóa Đơn)
CREATE TABLE IF NOT EXISTS HoaDon (
    maHoaDon VARCHAR(20) PRIMARY KEY,
    maNV VARCHAR(10),
    maKH VARCHAR(20),
    tenKH VARCHAR(100),
    soDienThoai VARCHAR(20),
    ngayLap DATETIME,
    phuongThucThanhToan VARCHAR(50),
    trangThai VARCHAR(20),
    isActive TINYINT(1) DEFAULT 1,
    FOREIGN KEY (maNV) REFERENCES NhanVien(maNV),
    FOREIGN KEY (maKH) REFERENCES KhachHang(maKhachHang)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Bảng Ve (Vé)
CREATE TABLE IF NOT EXISTS Ve (
    maVe VARCHAR(20) PRIMARY KEY,
    maChuyen VARCHAR(20),
    maLoaiVe VARCHAR(20),
    maSoGhe VARCHAR(20),
    maGaDi VARCHAR(20),
    maGaDen VARCHAR(20),
    tenGaDi VARCHAR(100),
    tenGaDen VARCHAR(100),
    ngayIn DATETIME,
    trangThai VARCHAR(20),
    gioDi DATETIME,
    gioDenDuKien DATETIME,
    soToa INT,
    loaiCho VARCHAR(20),
    loaiVe VARCHAR(100),
    maBangGia VARCHAR(20),
    giaThanhToan FLOAT,
    isActive TINYINT(1) DEFAULT 1,
    FOREIGN KEY (maChuyen) REFERENCES ChuyenTau(maChuyen),
    FOREIGN KEY (maGaDi) REFERENCES Ga(maGa),
    FOREIGN KEY (maGaDen) REFERENCES Ga(maGa)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Bảng ChiTietHoaDon
CREATE TABLE IF NOT EXISTS ChiTietHoaDon (
    maHoaDon VARCHAR(20),
    maVe VARCHAR(20),
    maLoaiVe VARCHAR(20),
    giaGoc FLOAT,
    giaDaKM FLOAT,
    moTa VARCHAR(255),
    isActive TINYINT(1) DEFAULT 1,
    PRIMARY KEY (maHoaDon, maVe),
    FOREIGN KEY (maHoaDon) REFERENCES HoaDon(maHoaDon),
    FOREIGN KEY (maVe) REFERENCES Ve(maVe)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- Seed data mặc định
-- ============================================================

-- Loại nhân viên
INSERT IGNORE INTO LoaiNV (maLoai, tenLoai, moTa) VALUES
    ('LNV01', 'Nhân viên quầy', 'Bán vé, đổi hoàn vé'),
    ('LNV02', 'Quản lý ca', 'Duyệt hoàn, xem báo cáo'),
    ('LNV03', 'Admin hệ thống', 'Quản trị cấu hình');

-- Nhân viên admin mặc định
INSERT IGNORE INTO NhanVien (maNV, tenNV, soDienThoai, diaChi, ngaySinh, maLoaiNV, trangThai) VALUES
    ('NV00', 'Quản trị viên', '0900000000', 'Hệ thống', '2000-01-01', 'LNV03', 'Đang hoạt động');

-- Tài khoản admin mặc định (username: admin, password: admin123)
INSERT IGNORE INTO TaiKhoan (maTK, maNV, tenTaiKhoan, matKhau, trangThai) VALUES
    ('TK00', 'NV00', 'admin', 'admin123', 'Hoạt động');
