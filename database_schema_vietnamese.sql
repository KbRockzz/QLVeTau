-- QLVeTau Database Schema for SQL Server (Vietnamese Table Names)
-- Hệ thống quản lý vé tàu

USE master;
GO

IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'QLVeTau')
BEGIN
    CREATE DATABASE QLVeTau;
END
GO

USE QLVeTau;
GO

-- Drop existing tables if they exist
IF OBJECT_ID('Ve', 'U') IS NOT NULL DROP TABLE Ve;
IF OBJECT_ID('Ghe', 'U') IS NOT NULL DROP TABLE Ghe;
IF OBJECT_ID('ToaTau', 'U') IS NOT NULL DROP TABLE ToaTau;
IF OBJECT_ID('LoaiGhe', 'U') IS NOT NULL DROP TABLE LoaiGhe;
IF OBJECT_ID('Tau', 'U') IS NOT NULL DROP TABLE Tau;
IF OBJECT_ID('TaiKhoan', 'U') IS NOT NULL DROP TABLE TaiKhoan;
IF OBJECT_ID('NhanVien', 'U') IS NOT NULL DROP TABLE NhanVien;
IF OBJECT_ID('KhachHang', 'U') IS NOT NULL DROP TABLE KhachHang;
IF OBJECT_ID('ChiTietHoaDon', 'U') IS NOT NULL DROP TABLE ChiTietHoaDon;
IF OBJECT_ID('HoaDon', 'U') IS NOT NULL DROP TABLE HoaDon;
IF OBJECT_ID('ChuyenTau', 'U') IS NOT NULL DROP TABLE ChuyenTau;
IF OBJECT_ID('ChangTau', 'U') IS NOT NULL DROP TABLE ChangTau;
IF OBJECT_ID('BangGia', 'U') IS NOT NULL DROP TABLE BangGia;
IF OBJECT_ID('LoaiVe', 'U') IS NOT NULL DROP TABLE LoaiVe;
IF OBJECT_ID('LoaiNV', 'U') IS NOT NULL DROP TABLE LoaiNV;
GO

-- Table: LoaiNV (Loại nhân viên)
CREATE TABLE LoaiNV (
    MaLoai NVARCHAR(10) PRIMARY KEY,
    TenLoai NVARCHAR(100) NOT NULL,
    MoTa NVARCHAR(255)
);
GO

-- Table: NhanVien (Nhân viên)
CREATE TABLE NhanVien (
    EmployeeID NVARCHAR(50) PRIMARY KEY,
    FullName NVARCHAR(100) NOT NULL,
    PhoneNumber NVARCHAR(20),
    Email NVARCHAR(100),
    Position NVARCHAR(100),
    Salary DECIMAL(18, 2),
    HireDate DATE,
    MaLoai NVARCHAR(10),
    FOREIGN KEY (MaLoai) REFERENCES LoaiNV(MaLoai)
);
GO

-- Table: TaiKhoan (Tài khoản)
CREATE TABLE TaiKhoan (
    Username NVARCHAR(50) PRIMARY KEY,
    Password NVARCHAR(255) NOT NULL,
    EmployeeID NVARCHAR(50),
    Role NVARCHAR(20) NOT NULL,
    IsActive BIT DEFAULT 1,
    FOREIGN KEY (EmployeeID) REFERENCES NhanVien(EmployeeID)
);
GO

-- Table: KhachHang (Khách hàng)
CREATE TABLE KhachHang (
    CustomerID NVARCHAR(50) PRIMARY KEY,
    FullName NVARCHAR(100) NOT NULL,
    PhoneNumber NVARCHAR(20),
    Email NVARCHAR(100),
    IDNumber NVARCHAR(20),
    Address NVARCHAR(255)
);
GO

-- Table: ChangTau (Chặng tàu/Ga tàu)
CREATE TABLE ChangTau (
    MaChang NVARCHAR(50) PRIMARY KEY,
    TenChang NVARCHAR(100) NOT NULL,
    DiaChi NVARCHAR(255),
    ThanhPho NVARCHAR(100)
);
GO

-- Table: ChuyenTau (Chuyến tàu)
CREATE TABLE ChuyenTau (
    MaChuyenTau NVARCHAR(50) PRIMARY KEY,
    TenChuyenTau NVARCHAR(100) NOT NULL,
    MaGaDi NVARCHAR(50),
    MaGaDen NVARCHAR(50),
    ThoiGianKhoiHanh DATETIME NOT NULL,
    ThoiGianDen DATETIME NOT NULL,
    FOREIGN KEY (MaGaDi) REFERENCES ChangTau(MaChang),
    FOREIGN KEY (MaGaDen) REFERENCES ChangTau(MaChang)
);
GO

-- Table: Tau (Tàu hỏa)
CREATE TABLE Tau (
    TrainID NVARCHAR(50) PRIMARY KEY,
    TrainName NVARCHAR(100) NOT NULL,
    DepartureStation NVARCHAR(100) NOT NULL,
    ArrivalStation NVARCHAR(100) NOT NULL,
    DepartureTime DATETIME NOT NULL,
    ArrivalTime DATETIME NOT NULL,
    TotalSeats INT NOT NULL,
    AvailableSeats INT NOT NULL,
    TicketPrice DECIMAL(18, 2) NOT NULL,
    MaChuyenTau NVARCHAR(50),
    FOREIGN KEY (MaChuyenTau) REFERENCES ChuyenTau(MaChuyenTau)
);
GO

-- Table: LoaiGhe (Loại ghế)
CREATE TABLE LoaiGhe (
    CarriageTypeID NVARCHAR(50) PRIMARY KEY,
    TypeName NVARCHAR(100) NOT NULL,
    SeatCount INT NOT NULL,
    PriceMultiplier DECIMAL(5, 2) DEFAULT 1.0
);
GO

-- Table: ToaTau (Toa tàu)
CREATE TABLE ToaTau (
    CarriageID NVARCHAR(50) PRIMARY KEY,
    TrainID NVARCHAR(50) NOT NULL,
    CarriageTypeID NVARCHAR(50) NOT NULL,
    CarriageName NVARCHAR(100),
    CarriageNumber INT NOT NULL,
    FOREIGN KEY (TrainID) REFERENCES Tau(TrainID),
    FOREIGN KEY (CarriageTypeID) REFERENCES LoaiGhe(CarriageTypeID)
);
GO

-- Table: Ghe (Ghế)
CREATE TABLE Ghe (
    SeatID NVARCHAR(50) PRIMARY KEY,
    CarriageID NVARCHAR(50) NOT NULL,
    SeatNumber NVARCHAR(10) NOT NULL,
    Status NVARCHAR(20) NOT NULL,
    FOREIGN KEY (CarriageID) REFERENCES ToaTau(CarriageID)
);
GO

-- Table: LoaiVe (Loại vé)
CREATE TABLE LoaiVe (
    MaLoaiVe NVARCHAR(10) PRIMARY KEY,
    TenLoaiVe NVARCHAR(100) NOT NULL,
    PhanTramGiam DECIMAL(5, 2) DEFAULT 0,
    MoTa NVARCHAR(255)
);
GO

-- Table: BangGia (Bảng giá)
CREATE TABLE BangGia (
    MaBangGia NVARCHAR(50) PRIMARY KEY,
    MaLoaiGhe NVARCHAR(50),
    MaLoaiVe NVARCHAR(10),
    GiaTien DECIMAL(18, 2) NOT NULL,
    NgayApDung DATE NOT NULL,
    NgayKetThuc DATE,
    FOREIGN KEY (MaLoaiGhe) REFERENCES LoaiGhe(CarriageTypeID),
    FOREIGN KEY (MaLoaiVe) REFERENCES LoaiVe(MaLoaiVe)
);
GO

-- Table: HoaDon (Hóa đơn)
CREATE TABLE HoaDon (
    MaHoaDon NVARCHAR(50) PRIMARY KEY,
    CustomerID NVARCHAR(50),
    EmployeeID NVARCHAR(50),
    NgayLap DATETIME NOT NULL,
    TongTien DECIMAL(18, 2) NOT NULL,
    TrangThai NVARCHAR(20) NOT NULL,
    FOREIGN KEY (CustomerID) REFERENCES KhachHang(CustomerID),
    FOREIGN KEY (EmployeeID) REFERENCES NhanVien(EmployeeID)
);
GO

-- Table: Ve (Vé)
CREATE TABLE Ve (
    TicketID NVARCHAR(50) PRIMARY KEY,
    TrainID NVARCHAR(50) NOT NULL,
    CustomerID NVARCHAR(50) NOT NULL,
    EmployeeID NVARCHAR(50),
    BookingDate DATETIME NOT NULL,
    SeatNumber NVARCHAR(10),
    SeatID NVARCHAR(50),
    CarriageID NVARCHAR(50),
    Price DECIMAL(18, 2) NOT NULL,
    Status NVARCHAR(20) NOT NULL,
    MaHoaDon NVARCHAR(50),
    MaLoaiVe NVARCHAR(10),
    FOREIGN KEY (TrainID) REFERENCES Tau(TrainID),
    FOREIGN KEY (CustomerID) REFERENCES KhachHang(CustomerID),
    FOREIGN KEY (EmployeeID) REFERENCES NhanVien(EmployeeID),
    FOREIGN KEY (SeatID) REFERENCES Ghe(SeatID),
    FOREIGN KEY (CarriageID) REFERENCES ToaTau(CarriageID),
    FOREIGN KEY (MaHoaDon) REFERENCES HoaDon(MaHoaDon),
    FOREIGN KEY (MaLoaiVe) REFERENCES LoaiVe(MaLoaiVe)
);
GO

-- Table: ChiTietHoaDon (Chi tiết hóa đơn)
CREATE TABLE ChiTietHoaDon (
    MaChiTiet NVARCHAR(50) PRIMARY KEY,
    MaHoaDon NVARCHAR(50) NOT NULL,
    TicketID NVARCHAR(50) NOT NULL,
    DonGia DECIMAL(18, 2) NOT NULL,
    SoLuong INT DEFAULT 1,
    ThanhTien DECIMAL(18, 2) NOT NULL,
    FOREIGN KEY (MaHoaDon) REFERENCES HoaDon(MaHoaDon),
    FOREIGN KEY (TicketID) REFERENCES Ve(TicketID)
);
GO

-- Insert sample data

-- Sample LoaiNV
INSERT INTO LoaiNV (MaLoai, TenLoai, MoTa) VALUES
('LNV01', N'Nhân viên bán vé', N'Nhân viên thường'),
('LNV02', N'Nhân viên kiểm soát', N'Nhân viên trung cấp'),
('LNV03', N'Quản lý', N'Quản lý cấp cao');
GO

-- Sample NhanVien
INSERT INTO NhanVien (EmployeeID, FullName, PhoneNumber, Email, Position, Salary, HireDate, MaLoai) VALUES
('EMP001', N'Nguyễn Thị Mai', '0987654321', 'nguyenmai@trainstation.com', N'Quản lý', 15000000, '2020-01-15', 'LNV03'),
('EMP002', N'Phạm Văn Hùng', '0976543210', 'phamhung@trainstation.com', N'Nhân viên bán vé', 8000000, '2021-03-20', 'LNV01'),
('EMP003', N'Lê Thị Lan', '0965432109', 'lethilan@trainstation.com', N'Nhân viên bán vé', 8000000, '2021-06-10', 'LNV01');
GO

-- Sample TaiKhoan
INSERT INTO TaiKhoan (Username, Password, EmployeeID, Role, IsActive) VALUES
('admin', 'admin123', 'EMP001', 'ADMIN', 1),
('nvhung', 'hung123', 'EMP002', 'EMPLOYEE', 1),
('nvlan', 'lan123', 'EMP003', 'EMPLOYEE', 1);
GO

-- Sample KhachHang
INSERT INTO KhachHang (CustomerID, FullName, PhoneNumber, Email, IDNumber, Address) VALUES
('KH001', N'Nguyễn Văn An', '0901234567', 'nguyenvanan@email.com', '001234567890', N'123 Lê Lợi, Q1, TP.HCM'),
('KH002', N'Trần Thị Bình', '0912345678', 'tranbinhth@email.com', '001234567891', N'456 Nguyễn Huệ, Q1, TP.HCM'),
('KH003', N'Lê Văn Cường', '0923456789', 'levancuong@email.com', '001234567892', N'789 Trần Hưng Đạo, Q5, TP.HCM');
GO

-- Sample ChangTau
INSERT INTO ChangTau (MaChang, TenChang, DiaChi, ThanhPho) VALUES
('GA01', N'Ga Sài Gòn', N'1 Nguyễn Thông, Q3', N'TP.HCM'),
('GA02', N'Ga Hà Nội', N'120 Lê Duẩn', N'Hà Nội'),
('GA03', N'Ga Nha Trang', N'26 Thái Nguyên', N'Nha Trang'),
('GA04', N'Ga Hải Phòng', N'75 Lương Khánh Thiện', N'Hải Phòng'),
('GA05', N'Ga Đà Nẵng', N'791 Hai Phòng', N'Đà Nẵng');
GO

-- Sample ChuyenTau
INSERT INTO ChuyenTau (MaChuyenTau, TenChuyenTau, MaGaDi, MaGaDen, ThoiGianKhoiHanh, ThoiGianDen) VALUES
('CT001', N'SE1 - Thống Nhất', 'GA01', 'GA02', '2024-01-15 06:00:00', '2024-01-16 08:00:00'),
('CT002', N'SE2 - Thống Nhất', 'GA02', 'GA01', '2024-01-15 19:00:00', '2024-01-16 21:00:00'),
('CT003', N'SNT1', 'GA01', 'GA03', '2024-01-15 08:00:00', '2024-01-15 18:00:00');
GO

-- Sample Tau
INSERT INTO Tau (TrainID, TrainName, DepartureStation, ArrivalStation, DepartureTime, ArrivalTime, TotalSeats, AvailableSeats, TicketPrice, MaChuyenTau) VALUES
('SE1', N'SE1 - Thống Nhất', N'Sài Gòn', N'Hà Nội', '2024-01-15 06:00:00', '2024-01-16 08:00:00', 200, 200, 850000, 'CT001'),
('SE2', N'SE2 - Thống Nhất', N'Hà Nội', N'Sài Gòn', '2024-01-15 19:00:00', '2024-01-16 21:00:00', 200, 200, 850000, 'CT002'),
('SNT1', N'SNT1', N'Sài Gòn', N'Nha Trang', '2024-01-15 08:00:00', '2024-01-15 18:00:00', 150, 150, 350000, 'CT003'),
('HP1', N'HP1', N'Hà Nội', N'Hải Phòng', '2024-01-15 07:00:00', '2024-01-15 09:30:00', 120, 120, 100000, NULL),
('DN1', N'DN1', N'Sài Gòn', N'Đà Nẵng', '2024-01-15 10:00:00', '2024-01-15 22:00:00', 180, 180, 550000, NULL);
GO

-- Sample LoaiGhe
INSERT INTO LoaiGhe (CarriageTypeID, TypeName, SeatCount, PriceMultiplier) VALUES
('CT01', N'Ghế ngồi cứng', 64, 1.0),
('CT02', N'Ghế ngồi mềm', 48, 1.2),
('CT03', N'Giường nằm 6', 36, 1.5),
('CT04', N'Giường nằm 4', 24, 2.0);
GO

-- Sample ToaTau
INSERT INTO ToaTau (CarriageID, TrainID, CarriageTypeID, CarriageName, CarriageNumber) VALUES
('SE1-C01', 'SE1', 'CT01', N'Toa 1 - Ghế ngồi cứng', 1),
('SE1-C02', 'SE1', 'CT02', N'Toa 2 - Ghế ngồi mềm', 2),
('SE1-C03', 'SE1', 'CT03', N'Toa 3 - Giường nằm 6', 3);
GO

-- Sample Ghe for SE1-C01
DECLARE @i INT = 1;
WHILE @i <= 64
BEGIN
    INSERT INTO Ghe (SeatID, CarriageID, SeatNumber, Status) 
    VALUES (
        'SE1-C01-S' + RIGHT('00' + CAST(@i AS VARCHAR(2)), 2),
        'SE1-C01',
        RIGHT('00' + CAST(@i AS VARCHAR(2)), 2),
        'AVAILABLE'
    );
    SET @i = @i + 1;
END
GO

-- Sample LoaiVe
INSERT INTO LoaiVe (MaLoaiVe, TenLoaiVe, PhanTramGiam, MoTa) VALUES
('LV01', N'Vé thường', 0, N'Vé giá chuẩn'),
('LV02', N'Vé sinh viên', 10, N'Giảm 10% cho sinh viên'),
('LV03', N'Vé trẻ em', 25, N'Giảm 25% cho trẻ em'),
('LV04', N'Vé người cao tuổi', 15, N'Giảm 15% cho người cao tuổi');
GO

-- Sample BangGia
INSERT INTO BangGia (MaBangGia, MaLoaiGhe, MaLoaiVe, GiaTien, NgayApDung, NgayKetThuc) VALUES
('BG001', 'CT01', 'LV01', 850000, '2024-01-01', NULL),
('BG002', 'CT02', 'LV01', 1020000, '2024-01-01', NULL),
('BG003', 'CT03', 'LV01', 1275000, '2024-01-01', NULL),
('BG004', 'CT04', 'LV01', 1700000, '2024-01-01', NULL);
GO

PRINT 'Database schema with Vietnamese table names created successfully!';
GO
