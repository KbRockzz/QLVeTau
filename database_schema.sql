-- QLVeTau Database Schema for SQL Server
-- Hệ thống quản lý vé tàu

USE master;
GO

-- Create database if not exists
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'QLVeTau')
BEGIN
    CREATE DATABASE QLVeTau;
END
GO

USE QLVeTau;
GO

-- Drop existing tables if they exist (for clean setup)
IF OBJECT_ID('Ticket', 'U') IS NOT NULL DROP TABLE Ticket;
IF OBJECT_ID('Seat', 'U') IS NOT NULL DROP TABLE Seat;
IF OBJECT_ID('Carriage', 'U') IS NOT NULL DROP TABLE Carriage;
IF OBJECT_ID('CarriageType', 'U') IS NOT NULL DROP TABLE CarriageType;
IF OBJECT_ID('Train', 'U') IS NOT NULL DROP TABLE Train;
IF OBJECT_ID('Account', 'U') IS NOT NULL DROP TABLE Account;
IF OBJECT_ID('Employee', 'U') IS NOT NULL DROP TABLE Employee;
IF OBJECT_ID('Customer', 'U') IS NOT NULL DROP TABLE Customer;
GO

-- Table: Customer (Khách hàng)
CREATE TABLE Customer (
    CustomerID NVARCHAR(50) PRIMARY KEY,
    FullName NVARCHAR(100) NOT NULL,
    PhoneNumber NVARCHAR(20),
    Email NVARCHAR(100),
    IDNumber NVARCHAR(20),
    Address NVARCHAR(255)
);
GO

-- Table: Employee (Nhân viên)
CREATE TABLE Employee (
    EmployeeID NVARCHAR(50) PRIMARY KEY,
    FullName NVARCHAR(100) NOT NULL,
    PhoneNumber NVARCHAR(20),
    Email NVARCHAR(100),
    Position NVARCHAR(100),
    Salary DECIMAL(18, 2),
    HireDate DATE,
    MaLoai NVARCHAR(10) -- LNV01, LNV02, LNV03 (Manager)
);
GO

-- Table: Account (Tài khoản)
CREATE TABLE Account (
    Username NVARCHAR(50) PRIMARY KEY,
    Password NVARCHAR(255) NOT NULL,
    EmployeeID NVARCHAR(50),
    Role NVARCHAR(20) NOT NULL, -- 'ADMIN' or 'EMPLOYEE'
    IsActive BIT DEFAULT 1,
    FOREIGN KEY (EmployeeID) REFERENCES Employee(EmployeeID)
);
GO

-- Table: Train (Chuyến tàu)
CREATE TABLE Train (
    TrainID NVARCHAR(50) PRIMARY KEY,
    TrainName NVARCHAR(100) NOT NULL,
    DepartureStation NVARCHAR(100) NOT NULL,
    ArrivalStation NVARCHAR(100) NOT NULL,
    DepartureTime DATETIME NOT NULL,
    ArrivalTime DATETIME NOT NULL,
    TotalSeats INT NOT NULL,
    AvailableSeats INT NOT NULL,
    TicketPrice DECIMAL(18, 2) NOT NULL
);
GO

-- Table: CarriageType (Loại toa)
CREATE TABLE CarriageType (
    CarriageTypeID NVARCHAR(50) PRIMARY KEY,
    TypeName NVARCHAR(100) NOT NULL,
    SeatCount INT NOT NULL,
    PriceMultiplier DECIMAL(5, 2) DEFAULT 1.0
);
GO

-- Table: Carriage (Toa tàu)
CREATE TABLE Carriage (
    CarriageID NVARCHAR(50) PRIMARY KEY,
    TrainID NVARCHAR(50) NOT NULL,
    CarriageTypeID NVARCHAR(50) NOT NULL,
    CarriageName NVARCHAR(100),
    CarriageNumber INT NOT NULL,
    FOREIGN KEY (TrainID) REFERENCES Train(TrainID),
    FOREIGN KEY (CarriageTypeID) REFERENCES CarriageType(CarriageTypeID)
);
GO

-- Table: Seat (Ghế)
CREATE TABLE Seat (
    SeatID NVARCHAR(50) PRIMARY KEY,
    CarriageID NVARCHAR(50) NOT NULL,
    SeatNumber NVARCHAR(10) NOT NULL,
    Status NVARCHAR(20) NOT NULL, -- 'AVAILABLE' or 'BOOKED'
    FOREIGN KEY (CarriageID) REFERENCES Carriage(CarriageID)
);
GO

-- Table: Ticket (Vé)
CREATE TABLE Ticket (
    TicketID NVARCHAR(50) PRIMARY KEY,
    TrainID NVARCHAR(50) NOT NULL,
    CustomerID NVARCHAR(50) NOT NULL,
    EmployeeID NVARCHAR(50),
    BookingDate DATETIME NOT NULL,
    SeatNumber NVARCHAR(10),
    SeatID NVARCHAR(50),
    CarriageID NVARCHAR(50),
    Price DECIMAL(18, 2) NOT NULL,
    Status NVARCHAR(20) NOT NULL, -- 'BOOKED', 'CANCELLED', 'REFUNDED'
    FOREIGN KEY (TrainID) REFERENCES Train(TrainID),
    FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID),
    FOREIGN KEY (EmployeeID) REFERENCES Employee(EmployeeID),
    FOREIGN KEY (SeatID) REFERENCES Seat(SeatID),
    FOREIGN KEY (CarriageID) REFERENCES Carriage(CarriageID)
);
GO

-- Insert sample data

-- Sample Customers
INSERT INTO Customer (CustomerID, FullName, PhoneNumber, Email, IDNumber, Address) VALUES
('KH001', N'Nguyễn Văn An', '0901234567', 'nguyenvanan@email.com', '001234567890', N'123 Lê Lợi, Q1, TP.HCM'),
('KH002', N'Trần Thị Bình', '0912345678', 'tranbinhth@email.com', '001234567891', N'456 Nguyễn Huệ, Q1, TP.HCM'),
('KH003', N'Lê Văn Cường', '0923456789', 'levancuong@email.com', '001234567892', N'789 Trần Hưng Đạo, Q5, TP.HCM');
GO

-- Sample Employees
INSERT INTO Employee (EmployeeID, FullName, PhoneNumber, Email, Position, Salary, HireDate, MaLoai) VALUES
('EMP001', N'Nguyễn Thị Mai', '0987654321', 'nguyenmai@trainstation.com', N'Quản lý', 15000000, '2020-01-15', 'LNV03'),
('EMP002', N'Phạm Văn Hùng', '0976543210', 'phamhung@trainstation.com', N'Nhân viên bán vé', 8000000, '2021-03-20', 'LNV01'),
('EMP003', N'Lê Thị Lan', '0965432109', 'lethilan@trainstation.com', N'Nhân viên bán vé', 8000000, '2021-06-10', 'LNV01');
GO

-- Sample Accounts
INSERT INTO Account (Username, Password, EmployeeID, Role, IsActive) VALUES
('admin', 'admin123', 'EMP001', 'ADMIN', 1),
('nvhung', 'hung123', 'EMP002', 'EMPLOYEE', 1),
('nvlan', 'lan123', 'EMP003', 'EMPLOYEE', 1);
GO

-- Sample Trains
INSERT INTO Train (TrainID, TrainName, DepartureStation, ArrivalStation, DepartureTime, ArrivalTime, TotalSeats, AvailableSeats, TicketPrice) VALUES
('SE1', N'SE1 - Thống Nhất', N'Sài Gòn', N'Hà Nội', '2024-01-15 06:00:00', '2024-01-16 08:00:00', 200, 200, 850000),
('SE2', N'SE2 - Thống Nhất', N'Hà Nội', N'Sài Gòn', '2024-01-15 19:00:00', '2024-01-16 21:00:00', 200, 200, 850000),
('SNT1', N'SNT1', N'Sài Gòn', N'Nha Trang', '2024-01-15 08:00:00', '2024-01-15 18:00:00', 150, 150, 350000),
('HP1', N'HP1', N'Hà Nội', N'Hải Phòng', '2024-01-15 07:00:00', '2024-01-15 09:30:00', 120, 120, 100000),
('DN1', N'DN1', N'Sài Gòn', N'Đà Nẵng', '2024-01-15 10:00:00', '2024-01-15 22:00:00', 180, 180, 550000);
GO

-- Sample Carriage Types
INSERT INTO CarriageType (CarriageTypeID, TypeName, SeatCount, PriceMultiplier) VALUES
('CT01', N'Ghế ngồi cứng', 64, 1.0),
('CT02', N'Ghế ngồi mềm', 48, 1.2),
('CT03', N'Giường nằm 6', 36, 1.5),
('CT04', N'Giường nằm 4', 24, 2.0);
GO

-- Sample Carriages for Train SE1
INSERT INTO Carriage (CarriageID, TrainID, CarriageTypeID, CarriageName, CarriageNumber) VALUES
('SE1-C01', 'SE1', 'CT01', N'Toa 1 - Ghế ngồi cứng', 1),
('SE1-C02', 'SE1', 'CT02', N'Toa 2 - Ghế ngồi mềm', 2),
('SE1-C03', 'SE1', 'CT03', N'Toa 3 - Giường nằm 6', 3);
GO

-- Sample Seats for SE1-C01 (Ghế ngồi cứng - 64 ghế)
DECLARE @i INT = 1;
WHILE @i <= 64
BEGIN
    INSERT INTO Seat (SeatID, CarriageID, SeatNumber, Status) 
    VALUES (
        'SE1-C01-S' + RIGHT('00' + CAST(@i AS VARCHAR(2)), 2),
        'SE1-C01',
        RIGHT('00' + CAST(@i AS VARCHAR(2)), 2),
        'AVAILABLE'
    );
    SET @i = @i + 1;
END
GO

-- Sample Seats for SE1-C02 (Ghế ngồi mềm - 48 ghế)
DECLARE @i INT = 1;
WHILE @i <= 48
BEGIN
    INSERT INTO Seat (SeatID, CarriageID, SeatNumber, Status) 
    VALUES (
        'SE1-C02-S' + RIGHT('00' + CAST(@i AS VARCHAR(2)), 2),
        'SE1-C02',
        RIGHT('00' + CAST(@i AS VARCHAR(2)), 2),
        'AVAILABLE'
    );
    SET @i = @i + 1;
END
GO

-- Sample Seats for SE1-C03 (Giường nằm 6 - 36 giường)
DECLARE @i INT = 1;
WHILE @i <= 36
BEGIN
    INSERT INTO Seat (SeatID, CarriageID, SeatNumber, Status) 
    VALUES (
        'SE1-C03-S' + RIGHT('00' + CAST(@i AS VARCHAR(2)), 2),
        'SE1-C03',
        RIGHT('00' + CAST(@i AS VARCHAR(2)), 2),
        'AVAILABLE'
    );
    SET @i = @i + 1;
END
GO

PRINT 'Database schema and sample data created successfully!';
GO
