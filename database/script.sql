
-- Tạo database nếu chưa có
IF DB_ID(N'QLTauHoa') IS NULL
BEGIN
    PRINT N'Creating database QLTauHoa...';
    EXEC('CREATE DATABASE QLTauHoa');
END
GO

USE QLTauHoa;
GO

SET XACT_ABORT ON;
BEGIN TRY
    BEGIN TRAN;

    ----------------------------------------------------------------
    -- TẠO BẢNG (IF NOT EXISTS)
    ----------------------------------------------------------------
    IF OBJECT_ID(N'dbo.LoaiVe', N'U') IS NULL
    BEGIN
        CREATE TABLE LoaiVe (
            maLoaiVe VARCHAR(50) PRIMARY KEY,
            tenLoai NVARCHAR(100),
            heSoGia DECIMAL(5,3),
            moTa NVARCHAR(100)
        );
    END

    IF OBJECT_ID(N'dbo.Tau', N'U') IS NULL
    BEGIN
        CREATE TABLE Tau (
            maTau VARCHAR(50) PRIMARY KEY,
            soToa INT,
            tenTau NVARCHAR(100),
            trangThai NVARCHAR(50)
        );
    END

    IF OBJECT_ID(N'dbo.ToaTau', N'U') IS NULL
    BEGIN
        CREATE TABLE ToaTau (
            maToa VARCHAR(50) PRIMARY KEY,
            tenToa NVARCHAR(100),
            loaiToa VARCHAR(20),
            maTau VARCHAR(50),
            sucChua INT,
            CONSTRAINT CHK_LoaiToa CHECK (loaiToa IN ('TOANGOI', 'TOANAM'))
        );
        ALTER TABLE ToaTau ADD CONSTRAINT FK_ToaTau_Tau FOREIGN KEY (maTau) REFERENCES Tau(maTau);
    END

    IF OBJECT_ID(N'dbo.LoaiGhe', N'U') IS NULL
    BEGIN
        CREATE TABLE LoaiGhe (
            maLoai VARCHAR(50) PRIMARY KEY,
            tenLoai NVARCHAR(50),
            moTa NVARCHAR(100)
        );
    END

    IF OBJECT_ID(N'dbo.Ghe', N'U') IS NULL
    BEGIN
        CREATE TABLE Ghe (
            maGhe VARCHAR(50) PRIMARY KEY,
            maToa VARCHAR(50),
            loaiGhe VARCHAR(50),
            trangThai NVARCHAR(50)
        );
        ALTER TABLE Ghe ADD CONSTRAINT FK_Ghe_ToaTau FOREIGN KEY (maToa) REFERENCES ToaTau(maToa);
        ALTER TABLE Ghe ADD CONSTRAINT FK_Ghe_LoaiGhe FOREIGN KEY (loaiGhe) REFERENCES LoaiGhe(maLoai);
    END

    IF OBJECT_ID(N'dbo.KhachHang', N'U') IS NULL
    BEGIN
        CREATE TABLE KhachHang (
            maKhachHang VARCHAR(50) PRIMARY KEY,
            tenKhachHang NVARCHAR(100),
            email VARCHAR(100),
            soDienThoai VARCHAR(20)
        );
    END

    IF OBJECT_ID(N'dbo.LoaiNV', N'U') IS NULL
    BEGIN
        CREATE TABLE LoaiNV (
            maLoai VARCHAR(50) PRIMARY KEY,
            tenLoai NVARCHAR(100),
            moTa NVARCHAR(100)
        );
    END

    IF OBJECT_ID(N'dbo.NhanVien', N'U') IS NULL
    BEGIN
        CREATE TABLE NhanVien (
            maNV VARCHAR(50) PRIMARY KEY,
            tenNV NVARCHAR(100),
            soDienThoai VARCHAR(10),
            diaChi NVARCHAR(100),
            ngaySinh DATE,
            maLoaiNV VARCHAR(50)
        );
        ALTER TABLE NhanVien ADD CONSTRAINT FK_NhanVien_LoaiNV FOREIGN KEY (maLoaiNV) REFERENCES LoaiNV(maLoai);
        ALTER TABLE NhanVien ADD trangThai VARCHAR(50) CONSTRAINT DF_NhanVien_trangThai DEFAULT 'active';
    END
    ELSE
    BEGIN
        -- Nếu bảng tồn tại nhưng cột trangThai chưa có thì thêm
        IF COL_LENGTH('dbo.NhanVien', 'trangThai') IS NULL
            ALTER TABLE NhanVien ADD trangThai VARCHAR(50) CONSTRAINT DF_NhanVien_trangThai DEFAULT 'active';
        -- Nếu FK chưa có, đảm bảo tồn tại
        IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE parent_object_id = OBJECT_ID('dbo.NhanVien') AND referenced_object_id = OBJECT_ID('dbo.LoaiNV'))
        BEGIN
            ALTER TABLE NhanVien ADD CONSTRAINT FK_NhanVien_LoaiNV FOREIGN KEY (maLoaiNV) REFERENCES LoaiNV(maLoai);
        END
    END

    IF OBJECT_ID(N'dbo.TaiKhoan', N'U') IS NULL
    BEGIN
        CREATE TABLE TaiKhoan (
            maTK VARCHAR(50) PRIMARY KEY,
            maNV VARCHAR(50),
            tenTaiKhoan VARCHAR(100),
            matKhau VARCHAR(100),
            trangThai NVARCHAR(50)
        );
        ALTER TABLE TaiKhoan ADD CONSTRAINT FK_TaiKhoan_NhanVien FOREIGN KEY (maNV) REFERENCES NhanVien(maNV);
    END

    IF OBJECT_ID(N'dbo.ChangTau', N'U') IS NULL
    BEGIN
        CREATE TABLE ChangTau (
            maChang VARCHAR(50) PRIMARY KEY,
            soKMToiThieu INT,
            soKMToiDa INT,
            moTa NVARCHAR(100),
            giaTien FLOAT
        );
    END

    IF OBJECT_ID(N'dbo.ChuyenTau', N'U') IS NULL
    BEGIN
        CREATE TABLE ChuyenTau (
            maChuyen VARCHAR(50) PRIMARY KEY,
            maTau VARCHAR(50),
            maNV VARCHAR(50),
            gaDi NVARCHAR(100),
            gaDen NVARCHAR(100),
            gioDi DATETIME,
            gioDen DATETIME,
            soKm INT,
            maChang VARCHAR(50),
        );
        ALTER TABLE ChuyenTau ADD CONSTRAINT FK_ChuyenTau_Tau FOREIGN KEY (maTau) REFERENCES Tau(maTau);
        ALTER TABLE ChuyenTau ADD CONSTRAINT FK_ChuyenTau_NhanVien FOREIGN KEY (maNV) REFERENCES NhanVien(maNV);
        ALTER TABLE ChuyenTau ADD CONSTRAINT FK_ChuyenTau_ChangTau FOREIGN KEY (maChang) REFERENCES ChangTau(maChang);
		ALTER TABLE ChuyenTau ADD trangThai NVARCHAR(50) DEFAULT N'Sẵn sàng'
    END
	

    IF OBJECT_ID(N'dbo.BangGia', N'U') IS NULL
    BEGIN
        CREATE TABLE BangGia (
            maBangGia VARCHAR(50) PRIMARY KEY,
            maChang VARCHAR(50),
            loaiGhe VARCHAR(50),
            giaCoBan FLOAT,
            ngayBatDau DATETIME,
            ngayKetThuc DATETIME
        );
        ALTER TABLE BangGia ADD CONSTRAINT FK_BangGia_ChangTau FOREIGN KEY (maChang) REFERENCES ChangTau(maChang);
        ALTER TABLE BangGia ADD CONSTRAINT FK_BangGia_LoaiGhe FOREIGN KEY (loaiGhe) REFERENCES LoaiGhe(maLoai);
    END

    IF OBJECT_ID(N'dbo.Ve', N'U') IS NULL
    BEGIN
        CREATE TABLE Ve (
            maVe VARCHAR(50) PRIMARY KEY,
            maChuyen VARCHAR(50),
            maLoaiVe VARCHAR(50),
            maSoGhe VARCHAR(50),
            ngayIn DATETIME,
            trangThai NVARCHAR(50),
            gaDi NVARCHAR(100),
            gaDen NVARCHAR(100),
            gioDi DATETIME,
            soToa VARCHAR(50),
            loaiCho NVARCHAR(100),
            loaiVe NVARCHAR(100),
            maBangGia VARCHAR(50)
        );
        ALTER TABLE Ve ADD CONSTRAINT FK_Ve_ChuyenTau FOREIGN KEY (maChuyen) REFERENCES ChuyenTau(maChuyen);
        ALTER TABLE Ve ADD CONSTRAINT FK_Ve_LoaiVe FOREIGN KEY (maLoaiVe) REFERENCES LoaiVe(maLoaiVe);
        ALTER TABLE Ve ADD CONSTRAINT FK_Ve_Ghe FOREIGN KEY (maSoGhe) REFERENCES Ghe(maGhe);
        ALTER TABLE Ve ADD CONSTRAINT FK_Ve_BangGia FOREIGN KEY (maBangGia) REFERENCES BangGia(maBangGia);
    END

    IF OBJECT_ID(N'dbo.HoaDon', N'U') IS NULL
    BEGIN
        CREATE TABLE HoaDon (
            maHoaDon VARCHAR(50) PRIMARY KEY,
            maNV VARCHAR(50),
            maKH VARCHAR(50),
            ngayLap DATETIME,
            phuongThucThanhToan NVARCHAR(100),
            trangThai NVARCHAR(50)
        );
        ALTER TABLE HoaDon ADD CONSTRAINT FK_HoaDon_NhanVien FOREIGN KEY (maNV) REFERENCES NhanVien(maNV);
        ALTER TABLE HoaDon ADD CONSTRAINT FK_HoaDon_KhachHang FOREIGN KEY (maKH) REFERENCES KhachHang(maKhachHang);
    END

    IF OBJECT_ID(N'dbo.ChiTietHoaDon', N'U') IS NULL
    BEGIN
        CREATE TABLE ChiTietHoaDon (
            maHoaDon VARCHAR(50),
            maVe VARCHAR(50),
            maLoaiVe VARCHAR(50),
            giaGoc FLOAT,
            giaDaKM FLOAT,
            moTa NVARCHAR(100),
            PRIMARY KEY (maHoaDon, maVe)
        );
        ALTER TABLE ChiTietHoaDon ADD CONSTRAINT FK_CTHD_HoaDon FOREIGN KEY (maHoaDon) REFERENCES HoaDon(maHoaDon);
        ALTER TABLE ChiTietHoaDon ADD CONSTRAINT FK_CTHD_Ve FOREIGN KEY (maVe) REFERENCES Ve(maVe);
    END

    ----------------------------------------------------------------
    -- CHÈN DỮ LIỆU MẪU (IF NOT EXISTS)
    ----------------------------------------------------------------

    -- ChangTau
    IF NOT EXISTS (SELECT 1 FROM ChangTau WHERE maChang='CH_NGAN')
    INSERT INTO ChangTau (maChang, soKMToiThieu, soKMToiDa, moTa, giaTien)
    VALUES ('CH_NGAN', 0, 199, N'Chặng ngắn dưới 200 km', 100000);

    IF NOT EXISTS (SELECT 1 FROM ChangTau WHERE maChang='CH_TRUNG')
    INSERT INTO ChangTau (maChang, soKMToiThieu, soKMToiDa, moTa, giaTien)
    VALUES ('CH_TRUNG', 200, 600, N'Chặng trung 200-600 km', 300000);

    IF NOT EXISTS (SELECT 1 FROM ChangTau WHERE maChang='CH_DAI')
    INSERT INTO ChangTau (maChang, soKMToiThieu, soKMToiDa, moTa, giaTien)
    VALUES ('CH_DAI', 601, 1500, N'Chặng dài trên 600 km', 600000);

    -- LoaiGhe
    IF NOT EXISTS (SELECT 1 FROM LoaiGhe WHERE maLoai='LG01')
    INSERT INTO LoaiGhe (maLoai, tenLoai, moTa) VALUES ('LG01', N'Ghế ngồi', N'Ghế ngồi, chỗ ngồi thông thường');

    IF NOT EXISTS (SELECT 1 FROM LoaiGhe WHERE maLoai='LG02')
    INSERT INTO LoaiGhe (maLoai, tenLoai, moTa) VALUES ('LG02', N'Giường nằm', N'Giường nằm khoang 2 giường cho chặng dài');

    -- BangGia
    IF NOT EXISTS (SELECT 1 FROM BangGia WHERE maBangGia='BG01')
    INSERT INTO BangGia (maBangGia, maChang, loaiGhe, giaCoBan, ngayBatDau, ngayKetThuc)
    VALUES ('BG01', 'CH_NGAN', 'LG01', 100000, '2025-01-01', '2025-12-31');

    IF NOT EXISTS (SELECT 1 FROM BangGia WHERE maBangGia='BG02')
    INSERT INTO BangGia (maBangGia, maChang, loaiGhe, giaCoBan, ngayBatDau, ngayKetThuc)
    VALUES ('BG02', 'CH_TRUNG', 'LG01', 300000, '2025-01-01', '2025-12-31');

    IF NOT EXISTS (SELECT 1 FROM BangGia WHERE maBangGia='BG03')
    INSERT INTO BangGia (maBangGia, maChang, loaiGhe, giaCoBan, ngayBatDau, ngayKetThuc)
    VALUES ('BG03', 'CH_DAI', 'LG01', 600000, '2025-01-01', '2025-12-31');

    IF NOT EXISTS (SELECT 1 FROM BangGia WHERE maBangGia='BG04')
    INSERT INTO BangGia (maBangGia, maChang, loaiGhe, giaCoBan, ngayBatDau, ngayKetThuc)
    VALUES ('BG04', 'CH_DAI', 'LG02', 1200000, '2025-01-01', '2025-12-31');

    -- LoaiVe
    IF NOT EXISTS (SELECT 1 FROM LoaiVe WHERE maLoaiVe='LV01')
    INSERT INTO LoaiVe (maLoaiVe, tenLoai, heSoGia, moTa) VALUES ('LV01', N'Người lớn', 1.000, N'Giá gốc, không giảm');

    IF NOT EXISTS (SELECT 1 FROM LoaiVe WHERE maLoaiVe='LV02')
    INSERT INTO LoaiVe (maLoaiVe, tenLoai, heSoGia, moTa) VALUES ('LV02', N'Trẻ em', 0.500, N'Giảm 50% cho 2–12 tuổi');

    IF NOT EXISTS (SELECT 1 FROM LoaiVe WHERE maLoaiVe='LV03')
    INSERT INTO LoaiVe (maLoaiVe, tenLoai, heSoGia, moTa) VALUES ('LV03', N'Cao tuổi', 0.700, N'Giảm 30% cho ≥60 tuổi');

    IF NOT EXISTS (SELECT 1 FROM LoaiVe WHERE maLoaiVe='LV04')
    INSERT INTO LoaiVe (maLoaiVe, tenLoai, heSoGia, moTa) VALUES ('LV04', N'Khuyết tật', 0.500, N'Giảm 50%');

    -- LoaiNV
    IF NOT EXISTS (SELECT 1 FROM LoaiNV WHERE maLoai='LNV01')
    INSERT INTO LoaiNV (maLoai, tenLoai, moTa) VALUES ('LNV01', N'Nhân viên quầy', N'Bán vé, đổi hoàn vé');

    IF NOT EXISTS (SELECT 1 FROM LoaiNV WHERE maLoai='LNV02')
    INSERT INTO LoaiNV (maLoai, tenLoai, moTa) VALUES ('LNV02', N'Quản lý ca', N'Duyệt hoàn, xem báo cáo');

    IF NOT EXISTS (SELECT 1 FROM LoaiNV WHERE maLoai='LNV03')
    INSERT INTO LoaiNV (maLoai, tenLoai, moTa) VALUES ('LNV03', N'Admin hệ thống', N'Quản trị cấu hình');

    -- KhachHang
    IF NOT EXISTS (SELECT 1 FROM KhachHang WHERE maKhachHang='KH001') INSERT INTO KhachHang (maKhachHang, tenKhachHang, email, soDienThoai) VALUES ('KH001', N'Nguyễn Văn An', 'an.nguyen@example.com', '0901234567');
    IF NOT EXISTS (SELECT 1 FROM KhachHang WHERE maKhachHang='KH002') INSERT INTO KhachHang (maKhachHang, tenKhachHang, email, soDienThoai) VALUES ('KH002', N'Trần Thị Bích', 'bich.tran@example.com', '0912345678');
    IF NOT EXISTS (SELECT 1 FROM KhachHang WHERE maKhachHang='KH003') INSERT INTO KhachHang (maKhachHang, tenKhachHang, email, soDienThoai) VALUES ('KH003', N'Lê Hoàng Minh', 'minh.le@example.com', '0923456789');
    IF NOT EXISTS (SELECT 1 FROM KhachHang WHERE maKhachHang='KH004') INSERT INTO KhachHang (maKhachHang, tenKhachHang, email, soDienThoai) VALUES ('KH004', N'Phạm Thị Hương', 'huong.pham@example.com', '0934567890');
    IF NOT EXISTS (SELECT 1 FROM KhachHang WHERE maKhachHang='KH005') INSERT INTO KhachHang (maKhachHang, tenKhachHang, email, soDienThoai) VALUES ('KH005', N'Ngô Văn Phúc', 'phuc.ngo@example.com', '0945678901');
    IF NOT EXISTS (SELECT 1 FROM KhachHang WHERE maKhachHang='KH006') INSERT INTO KhachHang (maKhachHang, tenKhachHang, email, soDienThoai) VALUES ('KH006', N'Vũ Thị Lan', 'lan.vu@example.com', '0956789012');
    IF NOT EXISTS (SELECT 1 FROM KhachHang WHERE maKhachHang='KH007') INSERT INTO KhachHang (maKhachHang, tenKhachHang, email, soDienThoai) VALUES ('KH007', N'Hoàng Văn Tú', 'tu.hoang@example.com', '0967890123');
    IF NOT EXISTS (SELECT 1 FROM KhachHang WHERE maKhachHang='KH008') INSERT INTO KhachHang (maKhachHang, tenKhachHang, email, soDienThoai) VALUES ('KH008', N'Đặng Thị Mai', 'mai.dang@example.com', '0978901234');
    IF NOT EXISTS (SELECT 1 FROM KhachHang WHERE maKhachHang='KH009') INSERT INTO KhachHang (maKhachHang, tenKhachHang, email, soDienThoai) VALUES ('KH009', N'Phùng Văn Long', 'long.phung@example.com', '0989012345');
    IF NOT EXISTS (SELECT 1 FROM KhachHang WHERE maKhachHang='KH010') INSERT INTO KhachHang (maKhachHang, tenKhachHang, email, soDienThoai) VALUES ('KH010', N'Bùi Thị Ngọc', 'ngoc.bui@example.com', '0990123456');

    -- NhanVien (ĐÃ SỬA: liệt kê cột, bỏ trangThai vì có DEFAULT)
    IF NOT EXISTS (SELECT 1 FROM NhanVien WHERE maNV='NV01') INSERT INTO NhanVien (maNV, tenNV, soDienThoai, diaChi, ngaySinh, maLoaiNV) VALUES ('NV01', N'Thùy Lý', '0905123456', N'1 Nguyễn Thông, Q3', '2005-05-12', 'LNV03');
    IF NOT EXISTS (SELECT 1 FROM NhanVien WHERE maNV='NV02') INSERT INTO NhanVien (maNV, tenNV, soDienThoai, diaChi, ngaySinh, maLoaiNV) VALUES ('NV02', N'Thuận', '0918234567', N'12 Võ Thị Sáu, Q3', '2005-09-14', 'LNV03');
    IF NOT EXISTS (SELECT 1 FROM NhanVien WHERE maNV='NV03') INSERT INTO NhanVien (maNV, tenNV, soDienThoai, diaChi, ngaySinh, maLoaiNV) VALUES ('NV03', N'Bảo', '0906789123', N'15 CMT8, Q10', '2005-11-08', 'LNV03');
    IF NOT EXISTS (SELECT 1 FROM NhanVien WHERE maNV='NV04') INSERT INTO NhanVien (maNV, tenNV, soDienThoai, diaChi, ngaySinh, maLoaiNV) VALUES ('NV04', N'Trí', '0909555123', N'5 Lý Tự Trọng, Q1', '2005-03-11', 'LNV03');
    IF NOT EXISTS (SELECT 1 FROM NhanVien WHERE maNV='NV05') INSERT INTO NhanVien (maNV, tenNV, soDienThoai, diaChi, ngaySinh, maLoaiNV) VALUES ('NV05', N'Trần Minh Quân', '0905123456', N'1 Nguyễn Thông, Q3', '1990-05-12', 'LNV01');
    IF NOT EXISTS (SELECT 1 FROM NhanVien WHERE maNV='NV06') INSERT INTO NhanVien (maNV, tenNV, soDienThoai, diaChi, ngaySinh, maLoaiNV) VALUES ('NV06', N'Lê Thị Hạnh', '0918234567', N'12 Võ Thị Sáu, Q3', '1986-09-14', 'LNV01');
    IF NOT EXISTS (SELECT 1 FROM NhanVien WHERE maNV='NV07') INSERT INTO NhanVien (maNV, tenNV, soDienThoai, diaChi, ngaySinh, maLoaiNV) VALUES ('NV07', N'Phan Văn Khải', '0906789123', N'15 CMT8, Q10', '1985-11-08', 'LNV02');
    IF NOT EXISTS (SELECT 1 FROM NhanVien WHERE maNV='NV08') INSERT INTO NhanVien (maNV, tenNV, soDienThoai, diaChi, ngaySinh, maLoaiNV) VALUES ('NV08', N'Trương Thị Hoa', '0909555123', N'5 Lý Tự Trọng, Q1', '1982-03-11', 'LNV01');

    -- TaiKhoan (liệt kê cột)
    IF NOT EXISTS (SELECT 1 FROM TaiKhoan WHERE maTK='TK01') INSERT INTO TaiKhoan (maTK, maNV, tenTaiKhoan, matKhau, trangThai) VALUES ('TK01', 'NV01', 'thuyly', '123456', N'Hoạt động');
    IF NOT EXISTS (SELECT 1 FROM TaiKhoan WHERE maTK='TK02') INSERT INTO TaiKhoan (maTK, maNV, tenTaiKhoan, matKhau, trangThai) VALUES ('TK02', 'NV02', 'thuan', '123456', N'Hoạt động');
    IF NOT EXISTS (SELECT 1 FROM TaiKhoan WHERE maTK='TK03') INSERT INTO TaiKhoan (maTK, maNV, tenTaiKhoan, matKhau, trangThai) VALUES ('TK03', 'NV03', 'bao', '123456', N'Hoạt động');
    IF NOT EXISTS (SELECT 1 FROM TaiKhoan WHERE maTK='TK04') INSERT INTO TaiKhoan (maTK, maNV, tenTaiKhoan, matKhau, trangThai) VALUES ('TK04', 'NV04', 'tri', '123456', N'Hoạt động');
    IF NOT EXISTS (SELECT 1 FROM TaiKhoan WHERE maTK='TK05') INSERT INTO TaiKhoan (maTK, maNV, tenTaiKhoan, matKhau, trangThai) VALUES ('TK05', 'NV05', 'quantran', '123456', N'Hoạt động');
    IF NOT EXISTS (SELECT 1 FROM TaiKhoan WHERE maTK='TK06') INSERT INTO TaiKhoan (maTK, maNV, tenTaiKhoan, matKhau, trangThai) VALUES ('TK06', 'NV06', 'hanhle', '123456', N'Hoạt động');
    IF NOT EXISTS (SELECT 1 FROM TaiKhoan WHERE maTK='TK07') INSERT INTO TaiKhoan (maTK, maNV, tenTaiKhoan, matKhau, trangThai) VALUES ('TK07', 'NV07', 'khaiphan', '123456', N'Hoạt động');
    IF NOT EXISTS (SELECT 1 FROM TaiKhoan WHERE maTK='TK08') INSERT INTO TaiKhoan (maTK, maNV, tenTaiKhoan, matKhau, trangThai) VALUES ('TK08', 'NV08', 'hoa', '123456', N'Hoạt động');

    -- Tau
    IF NOT EXISTS (SELECT 1 FROM Tau WHERE maTau='T001') INSERT INTO Tau (maTau, soToa, tenTau, trangThai) VALUES ('T001', 6, N'Tàu SE1 - Bắc Nam Express', N'Đang hoạt động');
    IF NOT EXISTS (SELECT 1 FROM Tau WHERE maTau='T002') INSERT INTO Tau (maTau, soToa, tenTau, trangThai) VALUES ('T002', 5, N'Tàu SE2 - Hành trình phương Nam', N'Đang hoạt động');
    IF NOT EXISTS (SELECT 1 FROM Tau WHERE maTau='T003') INSERT INTO Tau (maTau, soToa, tenTau, trangThai) VALUES ('T003', 7, N'Tàu TN1 - Tốc hành Sài Gòn - Nha Trang', N'Đang hoạt động');
    IF NOT EXISTS (SELECT 1 FROM Tau WHERE maTau='T004') INSERT INTO Tau (maTau, soToa, tenTau, trangThai) VALUES ('T004', 8, N'Tàu SE3 - Bắc Nam Premium', N'Bảo trì');
    IF NOT EXISTS (SELECT 1 FROM Tau WHERE maTau='T005') INSERT INTO Tau (maTau, soToa, tenTau, trangThai) VALUES ('T005', 5, N'Tàu TN2 - Hành trình miền Trung', N'Tạm dừng');

    -- ToaTau (giữ dữ liệu như file gốc)
    IF NOT EXISTS (SELECT 1 FROM ToaTau WHERE maToa='TOA001') INSERT INTO ToaTau (maToa, tenToa, loaiToa, maTau, sucChua) VALUES ('TOA001', N'Toa 1 - Ghế ngồi', 'TOANGOI', 'T001', 24);
    IF NOT EXISTS (SELECT 1 FROM ToaTau WHERE maToa='TOA002') INSERT INTO ToaTau (maToa, tenToa, loaiToa, maTau, sucChua) VALUES ('TOA002', N'Toa 2 - Ghế ngồi', 'TOANGOI', 'T001', 24);
    IF NOT EXISTS (SELECT 1 FROM ToaTau WHERE maToa='TOA003') INSERT INTO ToaTau (maToa, tenToa, loaiToa, maTau, sucChua) VALUES ('TOA003', N'Toa 3 - Giường nằm', 'TOANAM', 'T001', 20);
    IF NOT EXISTS (SELECT 1 FROM ToaTau WHERE maToa='TOA004') INSERT INTO ToaTau (maToa, tenToa, loaiToa, maTau, sucChua) VALUES ('TOA004', N'Toa 4 - Giường nằm', 'TOANAM', 'T001', 20);
    IF NOT EXISTS (SELECT 1 FROM ToaTau WHERE maToa='TOA005') INSERT INTO ToaTau (maToa, tenToa, loaiToa, maTau, sucChua) VALUES ('TOA005', N'Toa 5 - Ghế ngồi', 'TOANGOI', 'T001', 24);
    IF NOT EXISTS (SELECT 1 FROM ToaTau WHERE maToa='TOA006') INSERT INTO ToaTau (maToa, tenToa, loaiToa, maTau, sucChua) VALUES ('TOA006', N'Toa 6 - Giường nằm', 'TOANAM', 'T001', 20);

    IF NOT EXISTS (SELECT 1 FROM ToaTau WHERE maToa='TOA007') INSERT INTO ToaTau (maToa, tenToa, loaiToa, maTau, sucChua) VALUES ('TOA007', N'Toa 1 - Ghế ngồi', 'TOANGOI', 'T002', 24);
    IF NOT EXISTS (SELECT 1 FROM ToaTau WHERE maToa='TOA008') INSERT INTO ToaTau (maToa, tenToa, loaiToa, maTau, sucChua) VALUES ('TOA008', N'Toa 2 - Ghế ngồi', 'TOANGOI', 'T002', 24);
    IF NOT EXISTS (SELECT 1 FROM ToaTau WHERE maToa='TOA009') INSERT INTO ToaTau (maToa, tenToa, loaiToa, maTau, sucChua) VALUES ('TOA009', N'Toa 3 - Giường nằm', 'TOANAM', 'T002', 20);
    IF NOT EXISTS (SELECT 1 FROM ToaTau WHERE maToa='TOA010') INSERT INTO ToaTau (maToa, tenToa, loaiToa, maTau, sucChua) VALUES ('TOA010', N'Toa 4 - Giường nằm', 'TOANAM', 'T002', 20);
    IF NOT EXISTS (SELECT 1 FROM ToaTau WHERE maToa='TOA011') INSERT INTO ToaTau (maToa, tenToa, loaiToa, maTau, sucChua) VALUES ('TOA011', N'Toa 5 - Ghế ngồi', 'TOANGOI', 'T002', 24);

    IF NOT EXISTS (SELECT 1 FROM ToaTau WHERE maToa='TOA012') INSERT INTO ToaTau (maToa, tenToa, loaiToa, maTau, sucChua) VALUES ('TOA012', N'Toa 1 - Ghế ngồi', 'TOANGOI', 'T003', 24);
    IF NOT EXISTS (SELECT 1 FROM ToaTau WHERE maToa='TOA013') INSERT INTO ToaTau (maToa, tenToa, loaiToa, maTau, sucChua) VALUES ('TOA013', N'Toa 2 - Ghế ngồi', 'TOANGOI', 'T003', 24);
    IF NOT EXISTS (SELECT 1 FROM ToaTau WHERE maToa='TOA014') INSERT INTO ToaTau (maToa, tenToa, loaiToa, maTau, sucChua) VALUES ('TOA014', N'Toa 3 - Giường nằm', 'TOANAM', 'T003', 20);
    IF NOT EXISTS (SELECT 1 FROM ToaTau WHERE maToa='TOA015') INSERT INTO ToaTau (maToa, tenToa, loaiToa, maTau, sucChua) VALUES ('TOA015', N'Toa 4 - Ghế ngồi', 'TOANGOI', 'T003', 24);
    IF NOT EXISTS (SELECT 1 FROM ToaTau WHERE maToa='TOA016') INSERT INTO ToaTau (maToa, tenToa, loaiToa, maTau, sucChua) VALUES ('TOA016', N'Toa 5 - Giường nằm', 'TOANAM', 'T003', 20);
    IF NOT EXISTS (SELECT 1 FROM ToaTau WHERE maToa='TOA017') INSERT INTO ToaTau (maToa, tenToa, loaiToa, maTau, sucChua) VALUES ('TOA017', N'Toa 6 - Giường nằm', 'TOANAM', 'T003', 20);
    IF NOT EXISTS (SELECT 1 FROM ToaTau WHERE maToa='TOA018') INSERT INTO ToaTau (maToa, tenToa, loaiToa, maTau, sucChua) VALUES ('TOA018', N'Toa 7 - Ghế ngồi', 'TOANGOI', 'T003', 24);

    IF NOT EXISTS (SELECT 1 FROM ToaTau WHERE maToa='TOA019') INSERT INTO ToaTau (maToa, tenToa, loaiToa, maTau, sucChua) VALUES ('TOA019', N'Toa 1 - Giường nằm', 'TOANAM', 'T004', 20);
    IF NOT EXISTS (SELECT 1 FROM ToaTau WHERE maToa='TOA020') INSERT INTO ToaTau (maToa, tenToa, loaiToa, maTau, sucChua) VALUES ('TOA020', N'Toa 2 - Giường nằm', 'TOANAM', 'T004', 20);
    IF NOT EXISTS (SELECT 1 FROM ToaTau WHERE maToa='TOA021') INSERT INTO ToaTau (maToa, tenToa, loaiToa, maTau, sucChua) VALUES ('TOA021', N'Toa 3 - Giường nằm', 'TOANAM', 'T004', 20);
    IF NOT EXISTS (SELECT 1 FROM ToaTau WHERE maToa='TOA022') INSERT INTO ToaTau (maToa, tenToa, loaiToa, maTau, sucChua) VALUES ('TOA022', N'Toa 4 - Ghế ngồi', 'TOANGOI', 'T004', 24);
    IF NOT EXISTS (SELECT 1 FROM ToaTau WHERE maToa='TOA023') INSERT INTO ToaTau (maToa, tenToa, loaiToa, maTau, sucChua) VALUES ('TOA023', N'Toa 5 - Ghế ngồi', 'TOANGOI', 'T004', 24);
    IF NOT EXISTS (SELECT 1 FROM ToaTau WHERE maToa='TOA024') INSERT INTO ToaTau (maToa, tenToa, loaiToa, maTau, sucChua) VALUES ('TOA024', N'Toa 6 - Giường nằm', 'TOANAM', 'T004', 20);
    IF NOT EXISTS (SELECT 1 FROM ToaTau WHERE maToa='TOA025') INSERT INTO ToaTau (maToa, tenToa, loaiToa, maTau, sucChua) VALUES ('TOA025', N'Toa 7 - Giường nằm', 'TOANAM', 'T004', 20);
    IF NOT EXISTS (SELECT 1 FROM ToaTau WHERE maToa='TOA026') INSERT INTO ToaTau (maToa, tenToa, loaiToa, maTau, sucChua) VALUES ('TOA026', N'Toa 8 - Ghế ngồi', 'TOANGOI', 'T004', 24);

    IF NOT EXISTS (SELECT 1 FROM ToaTau WHERE maToa='TOA027') INSERT INTO ToaTau (maToa, tenToa, loaiToa, maTau, sucChua) VALUES ('TOA027', N'Toa 1 - Ghế ngồi', 'TOANGOI', 'T005', 24);
    IF NOT EXISTS (SELECT 1 FROM ToaTau WHERE maToa='TOA028') INSERT INTO ToaTau (maToa, tenToa, loaiToa, maTau, sucChua) VALUES ('TOA028', N'Toa 2 - Ghế ngồi', 'TOANGOI', 'T005', 24);
    IF NOT EXISTS (SELECT 1 FROM ToaTau WHERE maToa='TOA029') INSERT INTO ToaTau (maToa, tenToa, loaiToa, maTau, sucChua) VALUES ('TOA029', N'Toa 3 - Giường nằm', 'TOANAM', 'T005', 20);
    IF NOT EXISTS (SELECT 1 FROM ToaTau WHERE maToa='TOA030') INSERT INTO ToaTau (maToa, tenToa, loaiToa, maTau, sucChua) VALUES ('TOA030', N'Toa 4 - Giường nằm', 'TOANAM', 'T005', 20);
    IF NOT EXISTS (SELECT 1 FROM ToaTau WHERE maToa='TOA031') INSERT INTO ToaTau (maToa, tenToa, loaiToa, maTau, sucChua) VALUES ('TOA031', N'Toa 5 - Ghế ngồi', 'TOANGOI', 'T005', 24);

    -- Sinh ghế động (gốc insert_ghe.sql chuyển sang CTE)
    ;WITH Numbers AS (
        SELECT 1 AS n
        UNION ALL
        SELECT n+1 FROM Numbers WHERE n < 50
    ),
    SeatsToInsert AS (
        SELECT
            'G_' + t.maTau + '_' + t.maToa + '_' + RIGHT('00' + CAST(n AS VARCHAR(2)), 2) AS maGhe,
            t.maToa,
            CASE WHEN t.loaiToa = 'TOANGOI' THEN 'LG01' ELSE 'LG02' END AS loaiGhe,
            CASE WHEN t.maToa = 'TOA001' AND n <= 0 THEN N'Bận' ELSE N'Rảnh' END AS trangThai
        FROM ToaTau t
        CROSS JOIN Numbers
        WHERE n <= t.sucChua
    )
    INSERT INTO Ghe (maGhe, maToa, loaiGhe, trangThai)
    SELECT s.maGhe, s.maToa, s.loaiGhe, s.trangThai
    FROM SeatsToInsert s
    LEFT JOIN Ghe g ON g.maGhe = s.maGhe
    WHERE g.maGhe IS NULL
    OPTION (MAXRECURSION 0);

    -- ChuyenTau
    IF NOT EXISTS (SELECT 1 FROM ChuyenTau WHERE maChuyen='CT001')
    INSERT INTO ChuyenTau (maChuyen, maTau, maNV, gaDi, gaDen, gioDi, gioDen, soKm, maChang)
    VALUES ('CT001', 'T001', 'NV05', N'Ga Sài Gòn', N'Ga Hà Nội', '2025-01-10 06:00', '2025-01-11 20:00', 1500, 'CH_DAI');

    IF NOT EXISTS (SELECT 1 FROM ChuyenTau WHERE maChuyen='CT002')
    INSERT INTO ChuyenTau (maChuyen, maTau, maNV, gaDi, gaDen, gioDi, gioDen, soKm, maChang)
    VALUES ('CT002', 'T001', 'NV06', N'Ga Hà Nội', N'Ga Sài Gòn', '2025-01-12 08:00', '2025-01-13 22:00', 1500, 'CH_DAI');

    IF NOT EXISTS (SELECT 1 FROM ChuyenTau WHERE maChuyen='CT003')
    INSERT INTO ChuyenTau (maChuyen, maTau, maNV, gaDi, gaDen, gioDi, gioDen, soKm, maChang)
    VALUES ('CT003', 'T002', 'NV05', N'Ga Sài Gòn', N'Ga Nha Trang', '2025-01-10 07:00', '2025-01-10 19:00', 450, 'CH_TRUNG');

    IF NOT EXISTS (SELECT 1 FROM ChuyenTau WHERE maChuyen='CT004')
    INSERT INTO ChuyenTau (maChuyen, maTau, maNV, gaDi, gaDen, gioDi, gioDen, soKm, maChang)
    VALUES ('CT004', 'T002', 'NV06', N'Ga Nha Trang', N'Ga Sài Gòn', '2025-01-11 07:00', '2025-01-11 19:00', 450, 'CH_TRUNG');

    IF NOT EXISTS (SELECT 1 FROM ChuyenTau WHERE maChuyen='CT005')
    INSERT INTO ChuyenTau (maChuyen, maTau, maNV, gaDi, gaDen, gioDi, gioDen, soKm, maChang)
    VALUES ('CT005', 'T003', 'NV07', N'Ga Sài Gòn', N'Ga Nha Trang', '2025-01-10 09:00', '2025-01-10 18:00', 450, 'CH_TRUNG');

    IF NOT EXISTS (SELECT 1 FROM ChuyenTau WHERE maChuyen='CT006')
    INSERT INTO ChuyenTau (maChuyen, maTau, maNV, gaDi, gaDen, gioDi, gioDen, soKm, maChang)
    VALUES ('CT006', 'T001', 'NV05', N'Ga Sài Gòn', N'Ga Biên Hòa', '2025-01-10 08:00', '2025-01-10 10:00', 150, 'CH_NGAN');

    IF NOT EXISTS (SELECT 1 FROM ChuyenTau WHERE maChuyen='CT007')
    INSERT INTO ChuyenTau (maChuyen, maTau, maNV, gaDi, gaDen, gioDi, gioDen, soKm, maChang)
    VALUES ('CT007', 'T002', 'NV06', N'Ga Biên Hòa', N'Ga Sài Gòn', '2025-01-10 11:00', '2025-01-10 13:00', 150, 'CH_NGAN');

	INSERT INTO ChuyenTau (maChuyen, maTau, maNV, gaDi, gaDen, gioDi, gioDen, soKm, maChang)
	VALUES ('CT101','T001','NV05',N'Ga Sài Gòn',N'Ga Hà Nội','2025-11-08 06:00:00.000','2025-11-09 20:00:00.000',1500,'CH_DAI');

	INSERT INTO ChuyenTau (maChuyen, maTau, maNV, gaDi, gaDen, gioDi, gioDen, soKm, maChang)
	VALUES ('CT102','T002','NV06',N'Ga Sài Gòn',N'Ga Nha Trang','2025-11-08 07:00:00.000','2025-11-08 19:00:00.000',450,'CH_TRUNG');

	INSERT INTO ChuyenTau (maChuyen, maTau, maNV, gaDi, gaDen, gioDi, gioDen, soKm, maChang)
	VALUES ('CT103','T003','NV07',N'Ga Sài Gòn',N'Ga Biên Hòa','2025-11-09 08:00:00.000','2025-11-09 10:00:00.000',150,'CH_NGAN');

	INSERT INTO ChuyenTau (maChuyen, maTau, maNV, gaDi, gaDen, gioDi, gioDen, soKm, maChang)
	VALUES ('CT104','T001','NV05',N'Ga Hà Nội',N'Ga Sài Gòn','2025-11-09 08:00:00.000','2025-11-10 22:00:00.000',1500,'CH_DAI');

	INSERT INTO ChuyenTau (maChuyen, maTau, maNV, gaDi, gaDen, gioDi, gioDen, soKm, maChang)
	VALUES ('CT105','T002','NV06',N'Ga Biên Hòa',N'Ga Sài Gòn','2025-11-10 11:00:00.000','2025-11-10 13:00:00.000',150,'CH_NGAN');

	IF NOT EXISTS (SELECT 1 FROM ChuyenTau WHERE maChuyen='CT201')
    INSERT INTO ChuyenTau (maChuyen, maTau, maNV, gaDi, gaDen, gioDi, gioDen, soKm, maChang)
    VALUES ('CT201', 'T001', 'NV01', N'Ga Hà Nội', N'Ga Nha Trang', '2025-10-30 06:00:00.000', '2025-10-30 18:30:00.000', 900, 'CH_TRUNG');

	IF NOT EXISTS (SELECT 1 FROM ChuyenTau WHERE maChuyen='CT202')
	INSERT INTO ChuyenTau (maChuyen, maTau, maNV, gaDi, gaDen, gioDi, gioDen, soKm, maChang)
	VALUES ('CT202', 'T002', 'NV02', N'Ga Sài Gòn', N'Ga Nha Trang', '2025-10-30 07:30:00.000', '2025-10-30 19:30:00.000', 450, 'CH_TRUNG');

	IF NOT EXISTS (SELECT 1 FROM ChuyenTau WHERE maChuyen='CT203')
	INSERT INTO ChuyenTau (maChuyen, maTau, maNV, gaDi, gaDen, gioDi, gioDen, soKm, maChang)
	VALUES ('CT203', 'T003', 'NV03', N'Ga Biên Hòa', N'Ga Sài Gòn', '2025-10-30 08:00:00.000', '2025-10-30 09:30:00.000', 150, 'CH_NGAN');

	IF NOT EXISTS (SELECT 1 FROM ChuyenTau WHERE maChuyen='CT204')
	INSERT INTO ChuyenTau (maChuyen, maTau, maNV, gaDi, gaDen, gioDi, gioDen, soKm, maChang)
	VALUES ('CT204', 'T001', 'NV04', N'Ga Hà Nội', N'Ga Sài Gòn', '2025-10-30 05:00:00.000', '2025-10-30 22:00:00.000', 1500, 'CH_DAI');

	IF NOT EXISTS (SELECT 1 FROM ChuyenTau WHERE maChuyen='CT205')
    INSERT INTO ChuyenTau (maChuyen, maTau, maNV, gaDi, gaDen, gioDi, gioDen, soKm, maChang)
    VALUES ('CT205', 'T002', 'NV05', N'Ga Sài Gòn', N'Ga Biên Hòa', '2025-10-30 10:00:00.000', '2025-10-30 11:30:00.000', 150, 'CH_NGAN');

	IF NOT EXISTS (SELECT 1 FROM ChuyenTau WHERE maChuyen='CT201')
    INSERT INTO ChuyenTau (maChuyen, maTau, maNV, gaDi, gaDen, gioDi, gioDen, soKm, maChang)
    VALUES ('CT301', 'T001', 'NV01', N'Ga Hà Nội', N'Ga Nha Trang', '2025-10-30 06:00:00.000', '2025-10-29 18:30:00.000', 900, 'CH_TRUNG');

	IF NOT EXISTS (SELECT 1 FROM ChuyenTau WHERE maChuyen='CT302')
	INSERT INTO ChuyenTau (maChuyen, maTau, maNV, gaDi, gaDen, gioDi, gioDen, soKm, maChang)
	VALUES ('CT302', 'T002', 'NV02', N'Ga Sài Gòn', N'Ga Nha Trang', '2025-10-30 07:30:00.000', '2025-10-29 19:30:00.000', 450, 'CH_TRUNG');

	IF NOT EXISTS (SELECT 1 FROM ChuyenTau WHERE maChuyen='CT303')
	INSERT INTO ChuyenTau (maChuyen, maTau, maNV, gaDi, gaDen, gioDi, gioDen, soKm, maChang)
	VALUES ('CT303', 'T003', 'NV03', N'Ga Biên Hòa', N'Ga Sài Gòn', '2025-10-30 08:00:00.000', '2025-10-29 09:30:00.000', 150, 'CH_NGAN');

	IF NOT EXISTS (SELECT 1 FROM ChuyenTau WHERE maChuyen='CT304')
	INSERT INTO ChuyenTau (maChuyen, maTau, maNV, gaDi, gaDen, gioDi, gioDen, soKm, maChang)
	VALUES ('CT304', 'T001', 'NV04', N'Ga Hà Nội', N'Ga Sài Gòn', '2025-10-30 05:00:00.000', '2025-10-29 22:00:00.000', 1500, 'CH_DAI');

	IF NOT EXISTS (SELECT 1 FROM ChuyenTau WHERE maChuyen='CT305')
    INSERT INTO ChuyenTau (maChuyen, maTau, maNV, gaDi, gaDen, gioDi, gioDen, soKm, maChang)
    VALUES ('CT305', 'T002', 'NV05', N'Ga Sài Gòn', N'Ga Biên Hòa', '2025-10-30 10:00:00.000', '2025-10-29 11:30:00.000', 150, 'CH_NGAN');

    -- Xoá vé demo cũ (nếu muốn) - giữ an toàn: chỉ xóa các mã VE cụ thể
    IF EXISTS (SELECT 1 FROM Ve WHERE maVe IN ('VE001','VE002','VE003','VE004','VE005','VE006','VE007','VE008','VE009','VE010'))
    BEGIN
        DELETE FROM Ve WHERE maVe IN ('VE001','VE002','VE003','VE004','VE005','VE006','VE007','VE008','VE009','VE010');
    END

    -- Vé mẫu cho CT006 (theo ví dụ) - dùng IF NOT EXISTS để idempotent
    IF NOT EXISTS (SELECT 1 FROM Ve WHERE maVe='VE001')
    INSERT INTO Ve (maVe, maChuyen, maLoaiVe, maSoGhe, ngayIn, trangThai, gaDi, gaDen, gioDi, soToa, loaiCho, loaiVe, maBangGia)
    VALUES ('VE001', 'CT006', 'LV01', 'G_T001_TOA001_01', '2025-01-05 08:00', N'Đã thanh toán', N'Ga Sài Gòn', N'Ga Biên Hòa', '2025-01-10 08:00', 'TOA001', N'Ghế ngồi', N'Người lớn', 'BG01');

    IF NOT EXISTS (SELECT 1 FROM Ve WHERE maVe='VE002')
    INSERT INTO Ve (maVe, maChuyen, maLoaiVe, maSoGhe, ngayIn, trangThai, gaDi, gaDen, gioDi, soToa, loaiCho, loaiVe, maBangGia)
    VALUES ('VE002', 'CT006', 'LV02', 'G_T001_TOA001_02', '2025-01-05 08:05', N'Đã thanh toán', N'Ga Sài Gòn', N'Ga Biên Hòa', '2025-01-10 08:00', 'TOA001', N'Ghế ngồi', N'Trẻ em', 'BG01');

    IF NOT EXISTS (SELECT 1 FROM Ve WHERE maVe='VE003')
    INSERT INTO Ve (maVe, maChuyen, maLoaiVe, maSoGhe, ngayIn, trangThai, gaDi, gaDen, gioDi, soToa, loaiCho, loaiVe, maBangGia)
    VALUES ('VE003', 'CT006', 'LV03', 'G_T001_TOA001_03', '2025-01-05 08:10', N'Đã thanh toán', N'Ga Sài Gòn', N'Ga Biên Hòa', '2025-01-10 08:00', 'TOA001', N'Ghế ngồi', N'Cao tuổi', 'BG01');

    IF NOT EXISTS (SELECT 1 FROM Ve WHERE maVe='VE004')
    INSERT INTO Ve (maVe, maChuyen, maLoaiVe, maSoGhe, ngayIn, trangThai, gaDi, gaDen, gioDi, soToa, loaiCho, loaiVe, maBangGia)
    VALUES ('VE004', 'CT006', 'LV04', 'G_T001_TOA001_04', '2025-01-05 08:15', N'Đã thanh toán', N'Ga Sài Gòn', N'Ga Biên Hòa', '2025-01-10 08:00', 'TOA001', N'Ghế ngồi', N'Khuyết tật', 'BG01');

    IF NOT EXISTS (SELECT 1 FROM Ve WHERE maVe='VE005')
    INSERT INTO Ve (maVe, maChuyen, maLoaiVe, maSoGhe, ngayIn, trangThai, gaDi, gaDen, gioDi, soToa, loaiCho, loaiVe, maBangGia)
    VALUES ('VE005', 'CT006', 'LV01', 'G_T001_TOA001_05', '2025-01-05 08:20', N'Đã thanh toán', N'Ga Sài Gòn', N'Ga Biên Hòa', '2025-01-10 08:00', 'TOA001', N'Ghế ngồi', N'Người lớn', 'BG01');

    IF NOT EXISTS (SELECT 1 FROM Ve WHERE maVe='VE006')
    INSERT INTO Ve (maVe, maChuyen, maLoaiVe, maSoGhe, ngayIn, trangThai, gaDi, gaDen, gioDi, soToa, loaiCho, loaiVe, maBangGia)
    VALUES ('VE006', 'CT006', 'LV02', 'G_T001_TOA001_06', '2025-01-05 08:25', N'Đã thanh toán', N'Ga Sài Gòn', N'Ga Biên Hòa', '2025-01-10 08:00', 'TOA001', N'Ghế ngồi', N'Trẻ em', 'BG01');

    IF NOT EXISTS (SELECT 1 FROM Ve WHERE maVe='VE007')
    INSERT INTO Ve (maVe, maChuyen, maLoaiVe, maSoGhe, ngayIn, trangThai, gaDi, gaDen, gioDi, soToa, loaiCho, loaiVe, maBangGia)
    VALUES ('VE007', 'CT006', 'LV03', 'G_T001_TOA001_07', '2025-01-05 08:30', N'Đã thanh toán', N'Ga Sài Gòn', N'Ga Biên Hòa', '2025-01-10 08:00', 'TOA001', N'Ghế ngồi', N'Cao tuổi', 'BG01');

    IF NOT EXISTS (SELECT 1 FROM Ve WHERE maVe='VE008')
    INSERT INTO Ve (maVe, maChuyen, maLoaiVe, maSoGhe, ngayIn, trangThai, gaDi, gaDen, gioDi, soToa, loaiCho, loaiVe, maBangGia)
    VALUES ('VE008', 'CT006', 'LV02', 'G_T001_TOA001_08', '2025-01-05 08:35', N'Đã thanh toán', N'Ga Sài Gòn', N'Ga Biên Hòa', '2025-01-10 08:00', 'TOA001', N'Ghế ngồi', N'Trẻ em', 'BG01');

    IF NOT EXISTS (SELECT 1 FROM Ve WHERE maVe='VE009')
    INSERT INTO Ve (maVe, maChuyen, maLoaiVe, maSoGhe, ngayIn, trangThai, gaDi, gaDen, gioDi, soToa, loaiCho, loaiVe, maBangGia)
    VALUES ('VE009', 'CT006', 'LV01', 'G_T001_TOA001_09', '2025-01-05 08:40', N'Đã thanh toán', N'Ga Sài Gòn', N'Ga Biên Hòa', '2025-01-10 08:00', 'TOA001', N'Ghế ngồi', N'Người lớn', 'BG01');

    IF NOT EXISTS (SELECT 1 FROM Ve WHERE maVe='VE010')
    INSERT INTO Ve (maVe, maChuyen, maLoaiVe, maSoGhe, ngayIn, trangThai, gaDi, gaDen, gioDi, soToa, loaiCho, loaiVe, maBangGia)
    VALUES ('VE010', 'CT006', 'LV02', 'G_T001_TOA001_10', '2025-01-05 08:45', N'Đã thanh toán', N'Ga Sài Gòn', N'Ga Biên Hòa', '2025-01-10 08:00', 'TOA001', N'Ghế ngồi', N'Trẻ em', 'BG01');


    -- HoaDon
    IF NOT EXISTS (SELECT 1 FROM HoaDon WHERE maHoaDon='HD001') INSERT INTO HoaDon (maHoaDon, maNV, maKH, ngayLap, phuongThucThanhToan, trangThai) VALUES ('HD001','NV05','KH001','2025-01-05 08:10',N'Tiền mặt',N'Hoàn tất');
    IF NOT EXISTS (SELECT 1 FROM HoaDon WHERE maHoaDon='HD002') INSERT INTO HoaDon (maHoaDon, maNV, maKH, ngayLap, phuongThucThanhToan, trangThai) VALUES ('HD002','NV06','KH002','2025-01-05 08:15',N'Tiền mặt',N'Hoàn tất');
    IF NOT EXISTS (SELECT 1 FROM HoaDon WHERE maHoaDon='HD003') INSERT INTO HoaDon (maHoaDon, maNV, maKH, ngayLap, phuongThucThanhToan, trangThai) VALUES ('HD003','NV08','KH003','2025-01-06 09:10',N'Tiền mặt',N'Hoàn tất');
    IF NOT EXISTS (SELECT 1 FROM HoaDon WHERE maHoaDon='HD004') INSERT INTO HoaDon (maHoaDon, maNV, maKH, ngayLap, phuongThucThanhToan, trangThai) VALUES ('HD004','NV05','KH004','2025-01-06 09:15',N'Tiền mặt',N'Hoàn tất');
    IF NOT EXISTS (SELECT 1 FROM HoaDon WHERE maHoaDon='HD005') INSERT INTO HoaDon (maHoaDon, maNV, maKH, ngayLap, phuongThucThanhToan, trangThai) VALUES ('HD005','NV06','KH005','2025-01-07 10:10',N'Tiền mặt',N'Hoàn tất');
    IF NOT EXISTS (SELECT 1 FROM HoaDon WHERE maHoaDon='HD006') INSERT INTO HoaDon (maHoaDon, maNV, maKH, ngayLap, phuongThucThanhToan, trangThai) VALUES ('HD006','NV08','KH006','2025-01-07 10:15',N'Tiền mặt',N'Hoàn tất');
    IF NOT EXISTS (SELECT 1 FROM HoaDon WHERE maHoaDon='HD007') INSERT INTO HoaDon (maHoaDon, maNV, maKH, ngayLap, phuongThucThanhToan, trangThai) VALUES ('HD007','NV05','KH007','2025-01-07 11:10',N'Tiền mặt',N'Hoàn tất');
    IF NOT EXISTS (SELECT 1 FROM HoaDon WHERE maHoaDon='HD008') INSERT INTO HoaDon (maHoaDon, maNV, maKH, ngayLap, phuongThucThanhToan, trangThai) VALUES ('HD008','NV06','KH008','2025-01-07 11:15',N'Tiền mặt',N'Hoàn tất');
    IF NOT EXISTS (SELECT 1 FROM HoaDon WHERE maHoaDon='HD009') INSERT INTO HoaDon (maHoaDon, maNV, maKH, ngayLap, phuongThucThanhToan, trangThai) VALUES ('HD009','NV05','KH009','2025-01-08 08:10',N'Tiền mặt',N'Hoàn tất');
    IF NOT EXISTS (SELECT 1 FROM HoaDon WHERE maHoaDon='HD010') INSERT INTO HoaDon (maHoaDon, maNV, maKH, ngayLap, phuongThucThanhToan, trangThai) VALUES ('HD010','NV06','KH010','2025-01-08 08:15',N'Tiền mặt',N'Hoàn tất');

    -- ChiTietHoaDon
    IF NOT EXISTS (SELECT 1 FROM ChiTietHoaDon WHERE maHoaDon='HD001' AND maVe='VE001') INSERT INTO ChiTietHoaDon (maHoaDon, maVe, maLoaiVe, giaGoc, giaDaKM, moTa) VALUES ('HD001','VE001','LV01',100000,100000,N'Người lớn');
    IF NOT EXISTS (SELECT 1 FROM ChiTietHoaDon WHERE maHoaDon='HD002' AND maVe='VE002') INSERT INTO ChiTietHoaDon (maHoaDon, maVe, maLoaiVe, giaGoc, giaDaKM, moTa) VALUES ('HD002','VE002','LV02',100000,50000,N'Trẻ em');
    IF NOT EXISTS (SELECT 1 FROM ChiTietHoaDon WHERE maHoaDon='HD003' AND maVe='VE003') INSERT INTO ChiTietHoaDon (maHoaDon, maVe, maLoaiVe, giaGoc, giaDaKM, moTa) VALUES ('HD003','VE003','LV01',300000,300000,N'Người lớn');
    IF NOT EXISTS (SELECT 1 FROM ChiTietHoaDon WHERE maHoaDon='HD004' AND maVe='VE004') INSERT INTO ChiTietHoaDon (maHoaDon, maVe, maLoaiVe, giaGoc, giaDaKM, moTa) VALUES ('HD004','VE004','LV03',300000,210000,N'Cao tuổi');
    IF NOT EXISTS (SELECT 1 FROM ChiTietHoaDon WHERE maHoaDon='HD005' AND maVe='VE005') INSERT INTO ChiTietHoaDon (maHoaDon, maVe, maLoaiVe, giaGoc, giaDaKM, moTa) VALUES ('HD005','VE005','LV01',600000,600000,N'Người lớn');
    IF NOT EXISTS (SELECT 1 FROM ChiTietHoaDon WHERE maHoaDon='HD006' AND maVe='VE006') INSERT INTO ChiTietHoaDon (maHoaDon, maVe, maLoaiVe, giaGoc, giaDaKM, moTa) VALUES ('HD006','VE006','LV04',600000,300000,N'Khuyết tật');
    IF NOT EXISTS (SELECT 1 FROM ChiTietHoaDon WHERE maHoaDon='HD007' AND maVe='VE007') INSERT INTO ChiTietHoaDon (maHoaDon, maVe, maLoaiVe, giaGoc, giaDaKM, moTa) VALUES ('HD007','VE007','LV01',1200000,1200000,N'Người lớn');
    IF NOT EXISTS (SELECT 1 FROM ChiTietHoaDon WHERE maHoaDon='HD008' AND maVe='VE008') INSERT INTO ChiTietHoaDon (maHoaDon, maVe, maLoaiVe, giaGoc, giaDaKM, moTa) VALUES ('HD008','VE008','LV02',1200000,600000,N'Trẻ em');
    IF NOT EXISTS (SELECT 1 FROM ChiTietHoaDon WHERE maHoaDon='HD009' AND maVe='VE009') INSERT INTO ChiTietHoaDon (maHoaDon, maVe, maLoaiVe, giaGoc, giaDaKM, moTa) VALUES ('HD009','VE009','LV01',100000,100000,N'Người lớn');
    IF NOT EXISTS (SELECT 1 FROM ChiTietHoaDon WHERE maHoaDon='HD010' AND maVe='VE010') INSERT INTO ChiTietHoaDon (maHoaDon, maVe, maLoaiVe, giaGoc, giaDaKM, moTa) VALUES ('HD010','VE010','LV02',100000,50000,N'Trẻ em');

    COMMIT;
    PRINT N'Seed script chạy xong.';
END TRY
BEGIN CATCH
    DECLARE @errMsg NVARCHAR(4000) = ERROR_MESSAGE(), @errNum INT = ERROR_NUMBER();
    PRINT N'Error occurred: ' + COALESCE(@errMsg, N'');
    ROLLBACK;
    THROW;
END CATCH;
GO