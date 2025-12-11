
IF DB_ID(N'QLTauHoa') IS NULL
BEGIN
    PRINT N'Creating database QLTauHoa...';
    EXEC('CREATE DATABASE QLTauHoa');
END
GO

USE QLTauHoa;
GO

----------------------------------------------------------------
-- Lookup and basic tables (idempotent)
----------------------------------------------------------------

-- LoaiVe
IF OBJECT_ID(N'dbo.LoaiVe', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.LoaiVe (
        maLoaiVe VARCHAR(50) PRIMARY KEY,
        tenLoai NVARCHAR(100) NULL,
        heSoGia DECIMAL(5,3) NULL,
        moTa NVARCHAR(200) NULL,
        isActive BIT NOT NULL CONSTRAINT DF_LoaiVe_isActive DEFAULT (1)
    );
    PRINT 'Created table dbo.LoaiVe';
END
ELSE
    PRINT 'Table dbo.LoaiVe already exists';
GO

-- DauMay
IF OBJECT_ID(N'dbo.DauMay', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.DauMay (
        maDauMay VARCHAR(50) PRIMARY KEY,
        loaiDauMay NVARCHAR(50) NULL,
        tenDauMay NVARCHAR(100) NULL,
		namSX int NULL,
		lanBaoTriGanNhat DATETIME null,
        trangThai NVARCHAR(100) NULL,
        isActive BIT NOT NULL CONSTRAINT DF_Tau_isActive DEFAULT (1)
    );
    PRINT 'Created table dbo.DauMay';
END
ELSE
    PRINT 'Table dbo.DauMay already exists';
GO

-- ToaTau
IF OBJECT_ID(N'dbo.ToaTau', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.ToaTau (
        maToa VARCHAR(50) PRIMARY KEY,
        loaiToa VARCHAR(20) NULL,
        samSX int NULL,
		trangThai NVARCHAR(100),
        sucChua INT NULL,
        CONSTRAINT CHK_LoaiToa CHECK (loaiToa IN ('TOANGOI', 'TOANAM')),
        isActive BIT NOT NULL CONSTRAINT DF_ToaTau_isActive DEFAULT (1)
    );
    PRINT 'Created table dbo.ToaTau';
END
ELSE
    PRINT 'Table dbo.ToaTau already exists';
GO

-- LoaiGhe
IF OBJECT_ID(N'dbo.LoaiGhe', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.LoaiGhe (
        maLoai VARCHAR(50) PRIMARY KEY,
        tenLoai NVARCHAR(100) NULL,
        moTa NVARCHAR(200) NULL,
        isActive BIT NOT NULL CONSTRAINT DF_LoaiGhe_isActive DEFAULT (1)
    );
    PRINT 'Created table dbo.LoaiGhe';
END
ELSE
    PRINT 'Table dbo.LoaiGhe already exists';
GO

-- Ga
IF OBJECT_ID(N'dbo.Ga', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Ga (
        maGa VARCHAR(50) PRIMARY KEY,
        tenGa NVARCHAR(100) NULL,
        moTa NVARCHAR(200) NULL,
        tinhTrang NVARCHAR(100) NULL,
        diaChi NVARCHAR(200) NULL,
        isActive BIT NOT NULL CONSTRAINT DF_Ga_isActive DEFAULT (1)
    );
    PRINT 'Created table dbo.Ga';
END
ELSE
    PRINT 'Table dbo.Ga already exists';
GO

-- Ghe
IF OBJECT_ID(N'dbo.Ghe', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Ghe (
        maGhe VARCHAR(50) PRIMARY KEY,
        maToa VARCHAR(50) NULL,
        loaiGhe VARCHAR(50) NULL,
        trangThai NVARCHAR(50) NULL,
        isActive BIT NOT NULL CONSTRAINT DF_Ghe_isActive DEFAULT (1)
    );
    PRINT 'Created table dbo.Ghe';
END
ELSE
    PRINT 'Table dbo.Ghe already exists';
GO

-- KhachHang
IF OBJECT_ID(N'dbo.KhachHang', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.KhachHang (
        maKhachHang VARCHAR(50) PRIMARY KEY,
        tenKhachHang NVARCHAR(200) NULL,
        email VARCHAR(200) NULL,
        soDienThoai VARCHAR(50) NULL,
        isActive BIT NOT NULL CONSTRAINT DF_KhachHang_isActive DEFAULT (1)
    );
    PRINT 'Created table dbo.KhachHang';
END
ELSE
    PRINT 'Table dbo.KhachHang already exists';
GO

-- LoaiNV
IF OBJECT_ID(N'dbo.LoaiNV', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.LoaiNV (
        maLoai VARCHAR(50) PRIMARY KEY,
        tenLoai NVARCHAR(200) NULL,
        moTa NVARCHAR(200) NULL,
        isActive BIT NOT NULL CONSTRAINT DF_LoaiNV_isActive DEFAULT (1)
    );
    PRINT 'Created table dbo.LoaiNV';
END
ELSE
    PRINT 'Table dbo.LoaiNV already exists';
GO

-- NhanVien
IF OBJECT_ID(N'dbo.NhanVien', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.NhanVien (
        maNV VARCHAR(50) PRIMARY KEY,
        tenNV NVARCHAR(200) NULL,
        soDienThoai VARCHAR(50) NULL,
        diaChi NVARCHAR(300) NULL,
        ngaySinh DATE NULL,
        maLoaiNV VARCHAR(50) NULL,
        trangThai NVARCHAR(50) NULL,
        isActive BIT NOT NULL CONSTRAINT DF_NhanVien_isActive DEFAULT (1)
    );
    PRINT 'Created table dbo.NhanVien';
END
ELSE
    PRINT 'Table dbo.NhanVien already exists';
GO

-- TaiKhoan
IF OBJECT_ID(N'dbo.TaiKhoan', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.TaiKhoan (
        maTK VARCHAR(50) PRIMARY KEY,
        maNV VARCHAR(50) NULL,
        tenTaiKhoan NVARCHAR(200) NULL,
        matKhau VARCHAR(50) NULL,
        trangThai NVARCHAR(50) NULL,
        isActive BIT NOT NULL CONSTRAINT DF_TaiKhoan_isActive DEFAULT (1)
    );
    PRINT 'Created table dbo.TaiKhoan';
END
ELSE
    PRINT 'Table dbo.TaiKhoan already exists';
GO

-- ChangTau
IF OBJECT_ID(N'dbo.ChangTau', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.ChangTau (
        maChang VARCHAR(50) PRIMARY KEY,
        soKMToiThieu INT NULL,
        soKMToiDa INT NULL,
        moTa NVARCHAR(200) NULL,
        giaTien FLOAT NULL,
        isActive BIT NOT NULL CONSTRAINT DF_ChangTau_isActive DEFAULT (1)
    );
    PRINT 'Created table dbo.ChangTau';
END
ELSE
    PRINT 'Table dbo.ChangTau already exists';
GO

-- BangGia
IF OBJECT_ID(N'dbo.BangGia', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.BangGia (
        maBangGia VARCHAR(50) PRIMARY KEY,
        maChang VARCHAR(50) NULL,
        loaiGhe VARCHAR(50) NULL,
        giaCoBan FLOAT NULL,
        ngayBatDau DATETIME NULL,
        ngayKetThuc DATETIME NULL,
        isActive BIT NOT NULL CONSTRAINT DF_BangGia_isActive DEFAULT (1)
    );
    PRINT 'Created table dbo.BangGia';
END
ELSE
    PRINT 'Table dbo.BangGia already exists';
GO

-- ChuyenTau
IF OBJECT_ID(N'dbo.ChuyenTau', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.ChuyenTau (
        maChuyen VARCHAR(50) PRIMARY KEY,
        maDauMay VARCHAR(50) NULL,
        maNV VARCHAR(50) NULL,
        maGaDi VARCHAR(50) NULL,
        maGaDen VARCHAR(50) NULL,
        gioDi DATETIME NULL,
        gioDen DATETIME NULL,
        soKm INT NULL,
        maChang VARCHAR(50) NULL,
        trangThai NVARCHAR(50) NULL,
        isActive BIT NOT NULL CONSTRAINT DF_ChuyenTau_isActive DEFAULT (1)
    );
    PRINT 'Created table dbo.ChuyenTau';
END
ELSE
    PRINT 'Table dbo.ChuyenTau already exists';
GO

-- Ve
IF OBJECT_ID(N'dbo.Ve', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Ve (
        maVe VARCHAR(50) PRIMARY KEY,
        maChuyen VARCHAR(50) NULL,
        maLoaiVe VARCHAR(50) NULL,
        maSoGhe VARCHAR(50) NULL,
        maGaDi VARCHAR(50) NULL,
        maGaDen VARCHAR(50) NULL,
        tenGaDi NVARCHAR(200) NULL,
        tenGaDen NVARCHAR(200) NULL,
        ngayIn DATETIME NULL,
        trangThai NVARCHAR(50) NULL,
        gioDi DATETIME NULL,
		gioDenDuKien DATETIME NULL,
        soToa int NULL,
        loaiCho NVARCHAR(100) NULL,
        loaiVe NVARCHAR(100) NULL,
        maBangGia VARCHAR(50) NULL,
        giaThanhToan FLOAT NULL,
        isActive BIT NOT NULL CONSTRAINT DF_Ve_isActive DEFAULT (1)
    );
    PRINT 'Created table dbo.Ve';
END
ELSE
    PRINT 'Table dbo.Ve already exists';
GO

-- HoaDon
IF OBJECT_ID(N'dbo.HoaDon', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.HoaDon (
        maHoaDon VARCHAR(50) PRIMARY KEY,
        maNV VARCHAR(50) NULL,
        maKH VARCHAR(50) NULL,
		tenKH NVARCHAR(50) NULL,
		soDienThoai VARCHAR(10) NULL,
        ngayLap DATETIME NULL,
        phuongThucThanhToan NVARCHAR(100) NULL,
        trangThai NVARCHAR(50) NULL,
        isActive BIT NOT NULL CONSTRAINT DF_HoaDon_isActive DEFAULT (1)
    );
    PRINT 'Created table dbo.HoaDon';
END
ELSE
    PRINT 'Table dbo.HoaDon already exists';
GO

-- ChiTietHoaDon
IF OBJECT_ID(N'dbo.ChiTietHoaDon', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.ChiTietHoaDon (
        maHoaDon VARCHAR(50) NOT NULL,
        maVe VARCHAR(50) NOT NULL,
        maLoaiVe VARCHAR(50) NULL,
        giaGoc FLOAT NULL,
        giaDaKM FLOAT NULL,
        moTa NVARCHAR(200) NULL,
        isActive BIT NOT NULL CONSTRAINT DF_ChiTietHoaDon_isActive DEFAULT (1),
        PRIMARY KEY (maHoaDon, maVe)
    );
    PRINT 'Created table dbo.ChiTietHoaDon';
END
ELSE
    PRINT 'Table dbo.ChiTietHoaDon already exists';
GO

--ChiTietChuyenTau
IF OBJECT_ID(N'dbo.ChiTietChuyenTau', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.ChiTietChuyenTau (
        maChuyenTau VARCHAR(50) NOT NULL,
        maToaTau VARCHAR(50) NOT NULL,
		soThuTuToa int NULL,
		sucChua int NULL,
        isActive BIT NOT NULL CONSTRAINT DF_ChiTietChuyenTauu_isActive DEFAULT (1),
        PRIMARY KEY (maChuyenTau, maToaTau)
    );
    PRINT 'Created table dbo.ChiTietChuyenTau';
END
ELSE
    PRINT 'Table dbo.ChiTietChuyenTau already exists';
GO

----------------------------------------------------------------
-- Add foreign key constraints where referenced objects already exist.
-- These ALTERs are conditional and will not error if the FK already exists.
----------------------------------------------------------------


-- Ghe -> ToaTau, Ghe -> LoaiGhe
IF OBJECT_ID(N'dbo.Ghe', N'U') IS NOT NULL
BEGIN
    IF OBJECT_ID(N'dbo.ToaTau', N'U') IS NOT NULL
        IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys fk WHERE fk.parent_object_id = OBJECT_ID(N'dbo.Ghe') AND fk.referenced_object_id = OBJECT_ID(N'dbo.ToaTau'))
            ALTER TABLE dbo.Ghe ADD CONSTRAINT FK_Ghe_ToaTau FOREIGN KEY (maToa) REFERENCES dbo.ToaTau(maToa);

    IF OBJECT_ID(N'dbo.LoaiGhe', N'U') IS NOT NULL
        IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys fk WHERE fk.parent_object_id = OBJECT_ID(N'dbo.Ghe') AND fk.referenced_object_id = OBJECT_ID(N'dbo.LoaiGhe'))
            ALTER TABLE dbo.Ghe ADD CONSTRAINT FK_Ghe_LoaiGhe FOREIGN KEY (loaiGhe) REFERENCES dbo.LoaiGhe(maLoai);
END
GO
-- ChiTietChuyenTau -> Toa, ChuyenTau

IF OBJECT_ID(N'dbo.ChiTietChuyenTau', N'U') IS NOT NULL
BEGIN
    IF OBJECT_ID(N'dbo.ToaTau', N'U') IS NOT NULL
        IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys fk WHERE fk.parent_object_id = OBJECT_ID(N'dbo.ChiTietChuyenTau') AND fk.referenced_object_id = OBJECT_ID(N'dbo.ToaTau'))
            ALTER TABLE dbo.ChiTietChuyenTau ADD CONSTRAINT FK_ChiTietChuyenTau_ToaTau FOREIGN KEY (maToaTau) REFERENCES dbo.ToaTau(maToa);

	IF OBJECT_ID(N'dbo.ChuyenTau', N'U') IS NOT NULL
        IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys fk WHERE fk.parent_object_id = OBJECT_ID(N'dbo.ChiTietChuyenTau') AND fk.referenced_object_id = OBJECT_ID(N'dbo.ChuyenTau'))
            ALTER TABLE dbo.ChiTietChuyenTau ADD CONSTRAINT FK_ChiTietChuyenTau_ChuyenTau FOREIGN KEY (maChuyenTau) REFERENCES dbo.ChuyenTau(maChuyen);

END
GO


-- NhanVien -> LoaiNV
IF OBJECT_ID(N'dbo.NhanVien', N'U') IS NOT NULL AND OBJECT_ID(N'dbo.LoaiNV', N'U') IS NOT NULL
BEGIN
    IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys fk WHERE fk.parent_object_id = OBJECT_ID(N'dbo.NhanVien') AND fk.referenced_object_id = OBJECT_ID(N'dbo.LoaiNV'))
    BEGIN
        ALTER TABLE dbo.NhanVien ADD CONSTRAINT FK_NhanVien_LoaiNV FOREIGN KEY (maLoaiNV) REFERENCES dbo.LoaiNV(maLoai);
        PRINT 'Added FK_NhanVien_LoaiNV';
    END
END
GO

-- TaiKhoan -> NhanVien
IF OBJECT_ID(N'dbo.TaiKhoan', N'U') IS NOT NULL AND OBJECT_ID(N'dbo.NhanVien', N'U') IS NOT NULL
BEGIN
    IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys fk WHERE fk.parent_object_id = OBJECT_ID(N'dbo.TaiKhoan') AND fk.referenced_object_id = OBJECT_ID(N'dbo.NhanVien'))
    BEGIN
        ALTER TABLE dbo.TaiKhoan ADD CONSTRAINT FK_TaiKhoan_NhanVien FOREIGN KEY (maNV) REFERENCES dbo.NhanVien(maNV);
        PRINT 'Added FK_TaiKhoan_NhanVien';
    END
END
GO

-- BangGia -> ChangTau, BangGia -> LoaiGhe
IF OBJECT_ID(N'dbo.BangGia', N'U') IS NOT NULL
BEGIN
    IF OBJECT_ID(N'dbo.ChangTau', N'U') IS NOT NULL
        IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys fk WHERE fk.parent_object_id = OBJECT_ID(N'dbo.BangGia') AND fk.referenced_object_id = OBJECT_ID(N'dbo.ChangTau'))
            ALTER TABLE dbo.BangGia ADD CONSTRAINT FK_BangGia_ChangTau FOREIGN KEY (maChang) REFERENCES dbo.ChangTau(maChang);

    IF OBJECT_ID(N'dbo.LoaiGhe', N'U') IS NOT NULL
        IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys fk WHERE fk.parent_object_id = OBJECT_ID(N'dbo.BangGia') AND fk.referenced_object_id = OBJECT_ID(N'dbo.LoaiGhe'))
            ALTER TABLE dbo.BangGia ADD CONSTRAINT FK_BangGia_LoaiGhe FOREIGN KEY (loaiGhe) REFERENCES dbo.LoaiGhe(maLoai);
END
GO

-- ChuyenTau -> DauMay, ChiTietChuyenTau, NhanVien, ChangTau, Ga
IF OBJECT_ID(N'dbo.ChuyenTau', N'U') IS NOT NULL
BEGIN
    IF OBJECT_ID(N'dbo.DauMay', N'U') IS NOT NULL
        IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys fk WHERE fk.parent_object_id = OBJECT_ID(N'dbo.ChuyenTau') AND fk.referenced_object_id = OBJECT_ID(N'dbo.DauMay'))
            ALTER TABLE dbo.ChuyenTau ADD CONSTRAINT FK_ChuyenTau_DauMay FOREIGN KEY (maDauMay) REFERENCES dbo.DauMay(maDauMay);

    IF OBJECT_ID(N'dbo.NhanVien', N'U') IS NOT NULL
        IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys fk WHERE fk.parent_object_id = OBJECT_ID(N'dbo.ChuyenTau') AND fk.referenced_object_id = OBJECT_ID(N'dbo.NhanVien'))
            ALTER TABLE dbo.ChuyenTau ADD CONSTRAINT FK_ChuyenTau_NhanVien FOREIGN KEY (maNV) REFERENCES dbo.NhanVien(maNV);

    IF OBJECT_ID(N'dbo.ChangTau', N'U') IS NOT NULL
        IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys fk WHERE fk.parent_object_id = OBJECT_ID(N'dbo.ChuyenTau') AND fk.referenced_object_id = OBJECT_ID(N'dbo.ChangTau'))
            ALTER TABLE dbo.ChuyenTau ADD CONSTRAINT FK_ChuyenTau_ChangTau FOREIGN KEY (maChang) REFERENCES dbo.ChangTau(maChang);

    IF OBJECT_ID(N'dbo.Ga', N'U') IS NOT NULL
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys fk WHERE fk.parent_object_id = OBJECT_ID(N'dbo.ChuyenTau') AND fk.referenced_object_id = OBJECT_ID(N'dbo.Ga'))
            ALTER TABLE dbo.ChuyenTau ADD CONSTRAINT FK_ChuyenTau_GaDi FOREIGN KEY (maGaDi) REFERENCES dbo.Ga(maGa);

        IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys fk WHERE fk.parent_object_id = OBJECT_ID(N'dbo.ChuyenTau') AND fk.referenced_object_id = OBJECT_ID(N'dbo.Ga'))
            ALTER TABLE dbo.ChuyenTau ADD CONSTRAINT FK_ChuyenTau_GaDen FOREIGN KEY (maGaDen) REFERENCES dbo.Ga(maGa);
    END
END
GO

-- Ve foreign keys: to ChuyenTau, LoaiVe, Ghe, BangGia, Ga (only if targets exist)
IF OBJECT_ID(N'dbo.Ve', N'U') IS NOT NULL
BEGIN
    IF OBJECT_ID(N'dbo.ChuyenTau', N'U') IS NOT NULL
        IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys fk WHERE fk.parent_object_id = OBJECT_ID(N'dbo.Ve') AND fk.referenced_object_id = OBJECT_ID(N'dbo.ChuyenTau'))
            ALTER TABLE dbo.Ve ADD CONSTRAINT FK_Ve_ChuyenTau FOREIGN KEY (maChuyen) REFERENCES dbo.ChuyenTau(maChuyen);

    IF OBJECT_ID(N'dbo.LoaiVe', N'U') IS NOT NULL
        IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys fk WHERE fk.parent_object_id = OBJECT_ID(N'dbo.Ve') AND fk.referenced_object_id = OBJECT_ID(N'dbo.LoaiVe'))
            ALTER TABLE dbo.Ve ADD CONSTRAINT FK_Ve_LoaiVe FOREIGN KEY (maLoaiVe) REFERENCES dbo.LoaiVe(maLoaiVe);

    IF OBJECT_ID(N'dbo.Ghe', N'U') IS NOT NULL
        IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys fk WHERE fk.parent_object_id = OBJECT_ID(N'dbo.Ve') AND fk.referenced_object_id = OBJECT_ID(N'dbo.Ghe'))
            ALTER TABLE dbo.Ve ADD CONSTRAINT FK_Ve_Ghe FOREIGN KEY (maSoGhe) REFERENCES dbo.Ghe(maGhe);

    IF OBJECT_ID(N'dbo.Ga', N'U') IS NOT NULL
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys fk WHERE fk.parent_object_id = OBJECT_ID(N'dbo.Ve') AND fk.referenced_object_id = OBJECT_ID(N'dbo.Ga'))
            ALTER TABLE dbo.Ve ADD CONSTRAINT FK_Ve_GaDi FOREIGN KEY (maGaDi) REFERENCES dbo.Ga(maGa);
        IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys fk WHERE fk.parent_object_id = OBJECT_ID(N'dbo.Ve') AND fk.referenced_object_id = OBJECT_ID(N'dbo.Ga'))
            ALTER TABLE dbo.Ve ADD CONSTRAINT FK_Ve_GaDen FOREIGN KEY (maGaDen) REFERENCES dbo.Ga(maGa);
    END

    IF OBJECT_ID(N'dbo.BangGia', N'U') IS NOT NULL
        IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys fk WHERE fk.parent_object_id = OBJECT_ID(N'dbo.Ve') AND fk.referenced_object_id = OBJECT_ID(N'dbo.BangGia'))
            ALTER TABLE dbo.Ve ADD CONSTRAINT FK_Ve_BangGia FOREIGN KEY (maBangGia) REFERENCES dbo.BangGia(maBangGia);
END
GO

-- HoaDon -> NhanVien, KhachHang
IF OBJECT_ID(N'dbo.HoaDon', N'U') IS NOT NULL
BEGIN
    IF OBJECT_ID(N'dbo.NhanVien', N'U') IS NOT NULL
        IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys fk WHERE fk.parent_object_id = OBJECT_ID(N'dbo.HoaDon') AND fk.referenced_object_id = OBJECT_ID(N'dbo.NhanVien'))
            ALTER TABLE dbo.HoaDon ADD CONSTRAINT FK_HoaDon_NhanVien FOREIGN KEY (maNV) REFERENCES dbo.NhanVien(maNV);

    IF OBJECT_ID(N'dbo.KhachHang', N'U') IS NOT NULL
        IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys fk WHERE fk.parent_object_id = OBJECT_ID(N'dbo.HoaDon') AND fk.referenced_object_id = OBJECT_ID(N'dbo.KhachHang'))
            ALTER TABLE dbo.HoaDon ADD CONSTRAINT FK_HoaDon_KhachHang FOREIGN KEY (maKH) REFERENCES dbo.KhachHang(maKhachHang);
END
GO

-- ChiTietHoaDon -> HoaDon, Ve
IF OBJECT_ID(N'dbo.ChiTietHoaDon', N'U') IS NOT NULL
BEGIN
    IF OBJECT_ID(N'dbo.HoaDon', N'U') IS NOT NULL
        IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys fk WHERE fk.parent_object_id = OBJECT_ID(N'dbo.ChiTietHoaDon') AND fk.referenced_object_id = OBJECT_ID(N'dbo.HoaDon'))
            ALTER TABLE dbo.ChiTietHoaDon ADD CONSTRAINT FK_CTHD_HoaDon FOREIGN KEY (maHoaDon) REFERENCES dbo.HoaDon(maHoaDon);

    IF OBJECT_ID(N'dbo.Ve', N'U') IS NOT NULL
        IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys fk WHERE fk.parent_object_id = OBJECT_ID(N'dbo.ChiTietHoaDon') AND fk.referenced_object_id = OBJECT_ID(N'dbo.Ve'))
            ALTER TABLE dbo.ChiTietHoaDon ADD CONSTRAINT FK_CTHD_Ve FOREIGN KEY (maVe) REFERENCES dbo.Ve(maVe);
END
GO


--DULIEUMAU

USE QLTauHoa;
GO

SET XACT_ABORT ON;
BEGIN TRY
    BEGIN TRAN;

    ----------------------------------------------------------------
    -- 1) Basic lookups: Ga, ChangTau, LoaiGhe, BangGia, LoaiVe, LoaiNV
    ----------------------------------------------------------------
    IF NOT EXISTS (SELECT 1 FROM dbo.Ga WHERE maGa = 'GA_SG')
        INSERT INTO dbo.Ga (maGa, tenGa, moTa, tinhTrang, diaChi)
        VALUES ('GA_SG', N'Ga Sài Gòn', N'Ga chính tại Thành phố Hồ Chí Minh', N'Hoạt động', N'1 Nguyễn Thông, Q3, TP.HCM');

    IF NOT EXISTS (SELECT 1 FROM dbo.Ga WHERE maGa = 'GA_HN')
        INSERT INTO dbo.Ga (maGa, tenGa, moTa, tinhTrang, diaChi)
        VALUES ('GA_HN', N'Ga Hà Nội', N'Ga chính tại Hà Nội', N'Hoạt động', N'100 Lê Duẩn, Hoàn Kiếm, Hà Nội');

    IF NOT EXISTS (SELECT 1 FROM dbo.Ga WHERE maGa = 'GA_NT')
        INSERT INTO dbo.Ga (maGa, tenGa, moTa, tinhTrang, diaChi)
        VALUES ('GA_NT', N'Ga Nha Trang', N'Ga Nha Trang - Khánh Hòa', N'Hoạt động', N'10 Trần Phú, Nha Trang');

    IF NOT EXISTS (SELECT 1 FROM dbo.Ga WHERE maGa = 'GA_BH')
        INSERT INTO dbo.Ga (maGa, tenGa, moTa, tinhTrang, diaChi)
        VALUES ('GA_BH', N'Ga Biên Hòa', N'Ga Biên Hòa - Đồng Nai', N'Hoạt động', N'50 Đồng Khởi, Biên Hòa, Đồng Nai');

	IF NOT EXISTS (SELECT 1 FROM dbo.Ga WHERE maGa = 'GA_HP')
    INSERT INTO dbo.Ga (maGa, tenGa, moTa, tinhTrang, diaChi)
    VALUES ('GA_HP', N'Ga Hải Phòng', N'Ga Hải Phòng', N'Hoạt động', N'75 Lương Khánh Thiện, Gia Viên, Hải Phòng');

    IF NOT EXISTS (SELECT 1 FROM dbo.ChangTau WHERE maChang = 'CH_NGAN')
        INSERT INTO dbo.ChangTau (maChang, soKMToiThieu, soKMToiDa, moTa, giaTien)
        VALUES ('CH_NGAN', 0, 199, N'Chặng ngắn dưới 200 km', 100000);

    IF NOT EXISTS (SELECT 1 FROM dbo.ChangTau WHERE maChang = 'CH_TRUNG')
        INSERT INTO dbo.ChangTau (maChang, soKMToiThieu, soKMToiDa, moTa, giaTien)
        VALUES ('CH_TRUNG', 200, 600, N'Chặng trung 200-600 km', 300000);

    IF NOT EXISTS (SELECT 1 FROM dbo.ChangTau WHERE maChang = 'CH_DAI')
        INSERT INTO dbo.ChangTau (maChang, soKMToiThieu, soKMToiDa, moTa, giaTien)
        VALUES ('CH_DAI', 601, 1500, N'Chặng dài trên 600 km', 600000);

    IF NOT EXISTS (SELECT 1 FROM dbo.LoaiGhe WHERE maLoai = 'LG01')
        INSERT INTO dbo.LoaiGhe (maLoai, tenLoai, moTa) VALUES ('LG01', N'Ghế ngồi', N'Ghế ngồi, chỗ ngồi thông thường');

    IF NOT EXISTS (SELECT 1 FROM dbo.LoaiGhe WHERE maLoai = 'LG02')
        INSERT INTO dbo.LoaiGhe (maLoai, tenLoai, moTa) VALUES ('LG02', N'Giường nằm', N'Giường nằm khoang 2 giường cho chặng dài');

    IF NOT EXISTS (SELECT 1 FROM dbo.BangGia WHERE maBangGia = 'BG01')
        INSERT INTO dbo.BangGia (maBangGia, maChang, loaiGhe, giaCoBan, ngayBatDau, ngayKetThuc)
        VALUES ('BG01', 'CH_NGAN', 'LG01', 100000, '2025-01-01', '2025-12-31');

    IF NOT EXISTS (SELECT 1 FROM dbo.BangGia WHERE maBangGia = 'BG02')
        INSERT INTO dbo.BangGia (maBangGia, maChang, loaiGhe, giaCoBan, ngayBatDau, ngayKetThuc)
        VALUES ('BG02', 'CH_TRUNG', 'LG01', 300000, '2025-01-01', '2025-12-31');

    IF NOT EXISTS (SELECT 1 FROM dbo.BangGia WHERE maBangGia = 'BG03')
        INSERT INTO dbo.BangGia (maBangGia, maChang, loaiGhe, giaCoBan, ngayBatDau, ngayKetThuc)
        VALUES ('BG03', 'CH_DAI', 'LG01', 600000, '2025-01-01', '2025-12-31');

    IF NOT EXISTS (SELECT 1 FROM dbo.BangGia WHERE maBangGia = 'BG04')
        INSERT INTO dbo.BangGia (maBangGia, maChang, loaiGhe, giaCoBan, ngayBatDau, ngayKetThuc)
        VALUES ('BG04', 'CH_DAI', 'LG02', 1200000, '2025-01-01', '2025-12-31');

    IF NOT EXISTS (SELECT 1 FROM dbo.LoaiVe WHERE maLoaiVe = 'LV01')
        INSERT INTO dbo.LoaiVe (maLoaiVe, tenLoai, heSoGia, moTa) VALUES ('LV01', N'Người lớn', 1.000, N'Giá gốc, không giảm');

    IF NOT EXISTS (SELECT 1 FROM dbo.LoaiVe WHERE maLoaiVe = 'LV02')
        INSERT INTO dbo.LoaiVe (maLoaiVe, tenLoai, heSoGia, moTa) VALUES ('LV02', N'Trẻ em', 0.500, N'Giảm 50% cho 2–12 tuổi');

    IF NOT EXISTS (SELECT 1 FROM dbo.LoaiVe WHERE maLoaiVe = 'LV03')
        INSERT INTO dbo.LoaiVe (maLoaiVe, tenLoai, heSoGia, moTa) VALUES ('LV03', N'Cao tuổi', 0.700, N'Giảm 30% cho ≥60 tuổi');

    IF NOT EXISTS (SELECT 1 FROM dbo.LoaiVe WHERE maLoaiVe = 'LV04')
        INSERT INTO dbo.LoaiVe (maLoaiVe, tenLoai, heSoGia, moTa) VALUES ('LV04', N'Khuyết tật', 0.500, N'Giảm 50%');

    IF NOT EXISTS (SELECT 1 FROM dbo.LoaiNV WHERE maLoai = 'LNV01')
        INSERT INTO dbo.LoaiNV (maLoai, tenLoai, moTa) VALUES ('LNV01', N'Nhân viên quầy', N'Bán vé, đổi hoàn vé');

    IF NOT EXISTS (SELECT 1 FROM dbo.LoaiNV WHERE maLoai = 'LNV02')
        INSERT INTO dbo.LoaiNV (maLoai, tenLoai, moTa) VALUES ('LNV02', N'Quản lý ca', N'Duyệt hoàn, xem báo cáo');

    IF NOT EXISTS (SELECT 1 FROM dbo.LoaiNV WHERE maLoai = 'LNV03')
        INSERT INTO dbo.LoaiNV (maLoai, tenLoai, moTa) VALUES ('LNV03', N'Admin hệ thống', N'Quản trị cấu hình');

    ----------------------------------------------------------------
    -- 2) People and accounts: KhachHang, NhanVien, TaiKhoan
    ----------------------------------------------------------------
    IF NOT EXISTS (SELECT 1 FROM dbo.KhachHang WHERE maKhachHang = 'KH001') INSERT INTO dbo.KhachHang (maKhachHang, tenKhachHang, email, soDienThoai) VALUES ('KH001', N'Nguyễn Văn An', 'an.nguyen@example.com', '0901234567');
    IF NOT EXISTS (SELECT 1 FROM dbo.KhachHang WHERE maKhachHang = 'KH002') INSERT INTO dbo.KhachHang (maKhachHang, tenKhachHang, email, soDienThoai) VALUES ('KH002', N'Trần Thị Bích', 'bich.tran@example.com', '0912345678');
    IF NOT EXISTS (SELECT 1 FROM dbo.KhachHang WHERE maKhachHang = 'KH003') INSERT INTO dbo.KhachHang (maKhachHang, tenKhachHang, email, soDienThoai) VALUES ('KH003', N'Lê Hoàng Minh', 'minh.le@example.com', '0923456789');

    IF NOT EXISTS (SELECT 1 FROM dbo.NhanVien WHERE maNV = 'NV01') INSERT INTO dbo.NhanVien (maNV, tenNV, soDienThoai, diaChi, ngaySinh, maLoaiNV) VALUES ('NV01', N'Thùy Lý', '0905123456', N'1 Nguyễn Thông, Q3', '1985-05-12', 'LNV03');
    IF NOT EXISTS (SELECT 1 FROM dbo.NhanVien WHERE maNV = 'NV05') INSERT INTO dbo.NhanVien (maNV, tenNV, soDienThoai, diaChi, ngaySinh, maLoaiNV) VALUES ('NV05', N'Trần Minh Quân', '0905123456', N'1 Nguyễn Thông, Q3', '1990-05-12', 'LNV01');
    IF NOT EXISTS (SELECT 1 FROM dbo.NhanVien WHERE maNV = 'NV06') INSERT INTO dbo.NhanVien (maNV, tenNV, soDienThoai, diaChi, ngaySinh, maLoaiNV) VALUES ('NV06', N'Lê Thị Hạnh', '0918234567', N'12 Võ Thị Sáu, Q3', '1986-09-14', 'LNV01');

	IF NOT EXISTS (SELECT 1 FROM dbo.NhanVien WHERE maNV = 'NV07')
	INSERT INTO dbo.NhanVien (maNV, tenNV, soDienThoai, diaChi, ngaySinh, maLoaiNV, trangThai)
	VALUES ('NV07', N'Nguyễn Văn Hải', '0908111222', N'25 Lê Lợi, Q1', '1988-03-21', 'LNV01', 'Đang hoạt động');

	IF NOT EXISTS (SELECT 1 FROM dbo.NhanVien WHERE maNV = 'NV08')
	INSERT INTO dbo.NhanVien (maNV, tenNV, soDienThoai, diaChi, ngaySinh, maLoaiNV, trangThai)
	VALUES ('NV08', N'Phạm Thị Thu', '0912333444', N'78 Hai Bà Trưng, Q3', '1992-11-02', 'LNV01', 'Đang hoạt động');

	IF NOT EXISTS (SELECT 1 FROM dbo.NhanVien WHERE maNV = 'NV09')
	INSERT INTO dbo.NhanVien (maNV, tenNV, soDienThoai, diaChi, ngaySinh, maLoaiNV, trangThai)
	VALUES ('NV09', N'Hoàng Gia Huy', '0935445566', N'10 Nguyễn Huệ, Q1', '1987-07-18', 'LNV02', 'Đang hoạt động');

	IF NOT EXISTS (SELECT 1 FROM dbo.NhanVien WHERE maNV = 'NV10')
	INSERT INTO dbo.NhanVien (maNV, tenNV, soDienThoai, diaChi, ngaySinh, maLoaiNV, trangThai)
	VALUES ('NV10', N'Đặng Thị Kim Ngân', '0946556677', N'5 Cách Mạng Tháng 8, Q10', '1991-02-27', 'LNV02', 'Đang hoạt động');

	IF NOT EXISTS (SELECT 1 FROM dbo.NhanVien WHERE maNV = 'NV11')
	INSERT INTO dbo.NhanVien (maNV, tenNV, soDienThoai, diaChi, ngaySinh, maLoaiNV, trangThai)
	VALUES ('NV11', N'Võ Thành Tâm', '0909778899', N'22 Điện Biên Phủ, Q3', '1984-12-09', 'LNV03', 'Đã khóa');

	IF NOT EXISTS (SELECT 1 FROM dbo.NhanVien WHERE maNV = 'NV12')
	INSERT INTO dbo.NhanVien (maNV, tenNV, soDienThoai, diaChi, ngaySinh, maLoaiNV, trangThai)
	VALUES ('NV12', N'Trương Mỹ Duyên', '0917888999', N'90 Lý Tự Trọng, Q1', '1993-08-15', 'LNV01', 'Đã khóa');

	IF NOT EXISTS (SELECT 1 FROM dbo.NhanVien WHERE maNV = 'NV13')
	INSERT INTO dbo.NhanVien (maNV, tenNV, soDienThoai, diaChi, ngaySinh, maLoaiNV, trangThai)
	VALUES ('NV13', N'Lý Minh Nhật', '0905667788', N'40 Trần Hưng Đạo, Q5', '1989-06-04', 'LNV02', 'Đã khóa');

	IF NOT EXISTS (SELECT 1 FROM dbo.NhanVien WHERE maNV = 'NV14')
	INSERT INTO dbo.NhanVien (maNV, tenNV, soDienThoai, diaChi, ngaySinh, maLoaiNV, trangThai)
	VALUES ('NV14', N'Huỳnh Thanh Vy', '0935667788', N'18 Võ Văn Tần, Q3', '1994-10-22', 'LNV02', 'Đang hoạt động');

	IF NOT EXISTS (SELECT 1 FROM dbo.NhanVien WHERE maNV = 'NV15')
	INSERT INTO dbo.NhanVien (maNV, tenNV, soDienThoai, diaChi, ngaySinh, maLoaiNV, trangThai)
	VALUES ('NV15', N'Bùi Ngọc Khánh', '0977333555', N'77 Nguyễn Trãi, Q5', '1985-04-30', 'LNV03', 'Đang hoạt động');

	IF NOT EXISTS (SELECT 1 FROM dbo.NhanVien WHERE maNV = 'NV16')
	INSERT INTO dbo.NhanVien (maNV, tenNV, soDienThoai, diaChi, ngaySinh, maLoaiNV, trangThai)
	VALUES ('NV16', N'Tô Anh Phú', '0905443322', N'12 Nguyễn Thiện Thuật, Q3', '1990-01-19', 'LNV01', 'Đang hoạt động');

	IF NOT EXISTS (SELECT 1 FROM dbo.NhanVien WHERE maNV = 'NV17')
	INSERT INTO dbo.NhanVien (maNV, tenNV, soDienThoai, diaChi, ngaySinh, maLoaiNV, trangThai)
	VALUES ('NV17', N'Nguyễn Trà My', '0913555666', N'66 Phạm Ngũ Lão, Q1', '1996-09-10', 'LNV01', 'Đang hoạt động');

	IF NOT EXISTS (SELECT 1 FROM dbo.NhanVien WHERE maNV = 'NV18')
	INSERT INTO dbo.NhanVien (maNV, tenNV, soDienThoai, diaChi, ngaySinh, maLoaiNV, trangThai)
	VALUES ('NV18', N'Đỗ Minh Khang', '0923667788', N'9 Hoàng Văn Thụ, Q10', '1983-03-05', 'LNV03', 'Đang hoạt động');

	IF NOT EXISTS (SELECT 1 FROM dbo.NhanVien WHERE maNV = 'NV19')
	INSERT INTO dbo.NhanVien (maNV, tenNV, soDienThoai, diaChi, ngaySinh, maLoaiNV, trangThai)
	VALUES ('NV19', N'Đinh Tấn Lộc', '0988111222', N'55 Nguyễn Kiệm, Phú Nhuận', '1992-12-11', 'LNV02', 'Đang hoạt động');

	IF NOT EXISTS (SELECT 1 FROM dbo.NhanVien WHERE maNV = 'NV20')
	INSERT INTO dbo.NhanVien (maNV, tenNV, soDienThoai, diaChi, ngaySinh, maLoaiNV, trangThai)
	VALUES ('NV20', N'Lê Hải Vy', '0966778899', N'33 Quang Trung, Gò Vấp', '1995-07-08', 'LNV01', 'Đang hoạt động');


    IF NOT EXISTS (SELECT 1 FROM dbo.TaiKhoan WHERE maTK = 'TK01') INSERT INTO dbo.TaiKhoan (maTK, maNV, tenTaiKhoan, matKhau, trangThai) VALUES ('TK01', 'NV01', 'thuyly', '123456', N'Hoạt động');
    IF NOT EXISTS (SELECT 1 FROM dbo.TaiKhoan WHERE maTK = 'TK05') INSERT INTO dbo.TaiKhoan (maTK, maNV, tenTaiKhoan, matKhau, trangThai) VALUES ('TK05', 'NV05', 'quantran', '123456', N'Hoạt động');
    IF NOT EXISTS (SELECT 1 FROM dbo.TaiKhoan WHERE maTK = 'TK06') INSERT INTO dbo.TaiKhoan (maTK, maNV, tenTaiKhoan, matKhau, trangThai) VALUES ('TK06', 'NV06', 'hanhle', '123456', N'Hoạt động');
	IF NOT EXISTS (SELECT 1 FROM dbo.TaiKhoan WHERE maTK = 'TK07')
	INSERT INTO dbo.TaiKhoan (maTK, maNV, tenTaiKhoan, matKhau, trangThai)
	VALUES ('TK07', 'NV07', 'hainguyen', '123456', N'Hoạt động');

	IF NOT EXISTS (SELECT 1 FROM dbo.TaiKhoan WHERE maTK = 'TK08')
	INSERT INTO dbo.TaiKhoan (maTK, maNV, tenTaiKhoan, matKhau, trangThai)
	VALUES ('TK08', 'NV08', 'thupham', '123456', N'Hoạt động');

	IF NOT EXISTS (SELECT 1 FROM dbo.TaiKhoan WHERE maTK = 'TK09')
	INSERT INTO dbo.TaiKhoan (maTK, maNV, tenTaiKhoan, matKhau, trangThai)
	VALUES ('TK09', 'NV09', 'huyhoang', '123456', N'Hoạt động');

	IF NOT EXISTS (SELECT 1 FROM dbo.TaiKhoan WHERE maTK = 'TK10')
	INSERT INTO dbo.TaiKhoan (maTK, maNV, tenTaiKhoan, matKhau, trangThai)
	VALUES ('TK10', 'NV10', 'ngankim', '123456', N'Hoạt động');

	IF NOT EXISTS (SELECT 1 FROM dbo.TaiKhoan WHERE maTK = 'TK11')
	INSERT INTO dbo.TaiKhoan (maTK, maNV, tenTaiKhoan, matKhau, trangThai)
	VALUES ('TK11', 'NV11', 'tamvo', '123456', N'Hoạt động');

	IF NOT EXISTS (SELECT 1 FROM dbo.TaiKhoan WHERE maTK = 'TK12')
	INSERT INTO dbo.TaiKhoan (maTK, maNV, tenTaiKhoan, matKhau, trangThai)
	VALUES ('TK12', 'NV12', 'duyentruong', '123456', N'Hoạt động');

	IF NOT EXISTS (SELECT 1 FROM dbo.TaiKhoan WHERE maTK = 'TK13')
	INSERT INTO dbo.TaiKhoan (maTK, maNV, tenTaiKhoan, matKhau, trangThai)
	VALUES ('TK13', 'NV13', 'nhatly', '123456', N'Hoạt động');

	IF NOT EXISTS (SELECT 1 FROM dbo.TaiKhoan WHERE maTK = 'TK14')
	INSERT INTO dbo.TaiKhoan (maTK, maNV, tenTaiKhoan, matKhau, trangThai)
	VALUES ('TK14', 'NV14', 'vyhuynh', '123456', N'Hoạt động');

	IF NOT EXISTS (SELECT 1 FROM dbo.TaiKhoan WHERE maTK = 'TK15')
	INSERT INTO dbo.TaiKhoan (maTK, maNV, tenTaiKhoan, matKhau, trangThai)
	VALUES ('TK15', 'NV15', 'khanhbui', '123456', N'Hoạt động');

	IF NOT EXISTS (SELECT 1 FROM dbo.TaiKhoan WHERE maTK = 'TK16')
	INSERT INTO dbo.TaiKhoan (maTK, maNV, tenTaiKhoan, matKhau, trangThai)
	VALUES ('TK16', 'NV16', 'phuto', '123456', N'Hoạt động');

	IF NOT EXISTS (SELECT 1 FROM dbo.TaiKhoan WHERE maTK = 'TK17')
	INSERT INTO dbo.TaiKhoan (maTK, maNV, tenTaiKhoan, matKhau, trangThai)
	VALUES ('TK17', 'NV17', 'mynguyen', '123456', N'Hoạt động');

	IF NOT EXISTS (SELECT 1 FROM dbo.TaiKhoan WHERE maTK = 'TK18')
	INSERT INTO dbo.TaiKhoan (maTK, maNV, tenTaiKhoan, matKhau, trangThai)
	VALUES ('TK18', 'NV18', 'khangdo', '123456', N'Hoạt động');

	IF NOT EXISTS (SELECT 1 FROM dbo.TaiKhoan WHERE maTK = 'TK19')
	INSERT INTO dbo.TaiKhoan (maTK, maNV, tenTaiKhoan, matKhau, trangThai)
	VALUES ('TK19', 'NV19', 'locdinh', '123456', N'Hoạt động');

	IF NOT EXISTS (SELECT 1 FROM dbo.TaiKhoan WHERE maTK = 'TK20')
	INSERT INTO dbo.TaiKhoan (maTK, maNV, tenTaiKhoan, matKhau, trangThai)
	VALUES ('TK20', 'NV20', 'vyLe', '123456', N'Hoạt động');


    ----------------------------------------------------------------
    -- 3) DauMay (locomotives)
    ----------------------------------------------------------------
    IF NOT EXISTS (SELECT 1 FROM dbo.DauMay WHERE maDauMay = 'DM001')
        INSERT INTO dbo.DauMay (maDauMay, loaiDauMay, tenDauMay, namSX, trangThai)
        VALUES ('DM001', N'Diesel', N'DauMay-A', 2010, N'Sẵn sàng');

    IF NOT EXISTS (SELECT 1 FROM dbo.DauMay WHERE maDauMay = 'DM002')
        INSERT INTO dbo.DauMay (maDauMay, loaiDauMay, tenDauMay, namSX, trangThai)
        VALUES ('DM002', N'Diesel', N'DauMay-B', 2012, N'Sẵn sàng');

	IF NOT EXISTS (SELECT 1 FROM dbo.DauMay WHERE maDauMay = 'DM003')
		INSERT INTO dbo.DauMay (maDauMay, loaiDauMay, tenDauMay, namSX, trangThai)
		VALUES ('DM003', N'Diesel', N'DauMay-C', 2013, N'Sẵn sàng');

	IF NOT EXISTS (SELECT 1 FROM dbo.DauMay WHERE maDauMay = 'DM004')
		INSERT INTO dbo.DauMay (maDauMay, loaiDauMay, tenDauMay, namSX, trangThai)
		VALUES ('DM004', N'Điện', N'DauMay-D', 2014, N'Sẵn sàng');

	IF NOT EXISTS (SELECT 1 FROM dbo.DauMay WHERE maDauMay = 'DM005')
		INSERT INTO dbo.DauMay (maDauMay, loaiDauMay, tenDauMay, namSX, trangThai)
		VALUES ('DM005', N'Điện', N'DauMay-E', 2015, N'Sẵn sàng');

	IF NOT EXISTS (SELECT 1 FROM dbo.DauMay WHERE maDauMay = 'DM006')
		INSERT INTO dbo.DauMay (maDauMay, loaiDauMay, tenDauMay, namSX, trangThai)
		VALUES ('DM006', N'Diesel', N'DauMay-F', 2016, N'Bảo trì');

	IF NOT EXISTS (SELECT 1 FROM dbo.DauMay WHERE maDauMay = 'DM007')
		INSERT INTO dbo.DauMay (maDauMay, loaiDauMay, tenDauMay, namSX, trangThai)
		VALUES ('DM007', N'Diesel', N'DauMay-G', 2017, N'Sẵn sàng');


    ----------------------------------------------------------------
    -- 4) ToaTau templates (independent). These are the seat templates you will map into trips via ChiTietChuyenTau.
    ----------------------------------------------------------------
    IF NOT EXISTS (SELECT 1 FROM dbo.ToaTau WHERE maToa = 'TOA001')
        INSERT INTO dbo.ToaTau (maToa, loaiToa, samSX, trangThai, sucChua) VALUES ('TOA001', 'TOANGOI', 2015, N'Hoạt động', 24);
    IF NOT EXISTS (SELECT 1 FROM dbo.ToaTau WHERE maToa = 'TOA002')
        INSERT INTO dbo.ToaTau (maToa, loaiToa, samSX, trangThai, sucChua) VALUES ('TOA002', 'TOANGOI', 2016, N'Hoạt động', 24);
    IF NOT EXISTS (SELECT 1 FROM dbo.ToaTau WHERE maToa = 'TOA003')
        INSERT INTO dbo.ToaTau (maToa, loaiToa, samSX, trangThai, sucChua) VALUES ('TOA003', 'TOANAM', 2015, N'Hoạt động', 20);
    IF NOT EXISTS (SELECT 1 FROM dbo.ToaTau WHERE maToa = 'TOA004')
        INSERT INTO dbo.ToaTau (maToa, loaiToa, samSX, trangThai, sucChua) VALUES ('TOA004', 'TOANAM', 2016, N'Hoạt động', 20);
	
	IF NOT EXISTS (SELECT 1 FROM dbo.ToaTau WHERE maToa = 'TOA005')
		INSERT INTO dbo.ToaTau (maToa, loaiToa, samSX, trangThai, sucChua)
		VALUES ('TOA005', 'TOANGOI', 2017, N'Hoạt động', 24);

	IF NOT EXISTS (SELECT 1 FROM dbo.ToaTau WHERE maToa = 'TOA006')
		INSERT INTO dbo.ToaTau (maToa, loaiToa, samSX, trangThai, sucChua)
		VALUES ('TOA006', 'TOANGOI', 2018, N'Hoạt động', 24);

	IF NOT EXISTS (SELECT 1 FROM dbo.ToaTau WHERE maToa = 'TOA007')
		INSERT INTO dbo.ToaTau (maToa, loaiToa, samSX, trangThai, sucChua)
		VALUES ('TOA007', 'TOANGOI', 2019, N'Hoạt động', 24);

	IF NOT EXISTS (SELECT 1 FROM dbo.ToaTau WHERE maToa = 'TOA008')
		INSERT INTO dbo.ToaTau (maToa, loaiToa, samSX, trangThai, sucChua)
		VALUES ('TOA008', 'TOANAM', 2017, N'Hoạt động', 20);

	IF NOT EXISTS (SELECT 1 FROM dbo.ToaTau WHERE maToa = 'TOA009')
		INSERT INTO dbo.ToaTau (maToa, loaiToa, samSX, trangThai, sucChua)
		VALUES ('TOA009', 'TOANAM', 2018, N'Hoạt động', 20);

	IF NOT EXISTS (SELECT 1 FROM dbo.ToaTau WHERE maToa = 'TOA010')
		INSERT INTO dbo.ToaTau (maToa, loaiToa, samSX, trangThai, sucChua)
		VALUES ('TOA010', 'TOANAM', 2019, N'Hoạt động', 20);



    ----------------------------------------------------------------
    -- 5) Create ChuyenTau (trips) BEFORE ChiTietChuyenTau
    ----------------------------------------------------------------
    IF NOT EXISTS (SELECT 1 FROM dbo.ChuyenTau WHERE maChuyen = 'CT001')
        INSERT INTO dbo.ChuyenTau (maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang)
        VALUES ('CT001', 'DM001', 'NV05', 'GA_SG', 'GA_HN', '2025-01-10 06:00', '2025-01-11 20:00', 1500, 'CH_DAI');

    IF NOT EXISTS (SELECT 1 FROM dbo.ChuyenTau WHERE maChuyen = 'CT002')
        INSERT INTO dbo.ChuyenTau (maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang)
        VALUES ('CT002', 'DM001', 'NV06', 'GA_HN', 'GA_SG', '2025-01-12 08:00', '2025-01-13 22:00', 1500, 'CH_DAI');

    IF NOT EXISTS (SELECT 1 FROM dbo.ChuyenTau WHERE maChuyen = 'CT006')
        INSERT INTO dbo.ChuyenTau (maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang)
        VALUES ('CT006', 'DM002', 'NV05', 'GA_SG', 'GA_BH', '2025-01-10 08:00', '2025-01-10 10:00', 150, 'CH_NGAN');

	-- =======================
	-- 23/12/2025
	-- =======================
	IF NOT EXISTS (SELECT 1 FROM dbo.ChuyenTau WHERE maChuyen = 'CT20251223A')
		INSERT INTO dbo.ChuyenTau(maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang)
		VALUES ('CT20251223A', 'DM001', 'NV05', 'GA_SG', 'GA_NT',
				'2025-12-23 07:00', '2025-12-23 13:00', 400, 'CH_TRUNG');

	IF NOT EXISTS (SELECT 1 FROM dbo.ChuyenTau WHERE maChuyen = 'CT20251223B')
		INSERT INTO dbo.ChuyenTau(maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang)
		VALUES ('CT20251223B', 'DM002', 'NV06', 'GA_NT', 'GA_SG',
				'2025-12-23 15:00', '2025-12-23 21:00', 400, 'CH_TRUNG');
	IF NOT EXISTS (SELECT 1 FROM dbo.ChuyenTau WHERE maChuyen = 'CT3_20251223A')
		INSERT INTO dbo.ChuyenTau(maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang)
		VALUES ('CT3_20251223A', 'DM003', 'NV07', 'GA_SG', 'GA_NT',
				'2025-12-23 08:00', '2025-12-23 14:00', 400, 'CH_TRUNG');

	IF NOT EXISTS (SELECT 1 FROM dbo.ChuyenTau WHERE maChuyen = 'CT3_20251223B')
		INSERT INTO dbo.ChuyenTau(maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang)
		VALUES ('CT3_20251223B', 'DM004', 'NV08', 'GA_NT', 'GA_SG',
            '2025-12-23 16:00', '2025-12-23 22:00', 400, 'CH_TRUNG');

	INSERT INTO dbo.ChuyenTau(maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang) VALUES
	('CT5_20251223', 'DM005', 'NV09', 'GA_SG', 'GA_BH',
	 '2025-12-23 07:00', '2025-12-23 11:00', 30, 'CH_NGAN');

	INSERT INTO dbo.ChuyenTau(maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang) VALUES
	('CT6_20251223', 'DM006', 'NV10', 'GA_HN', 'GA_SG',
	 '2025-12-23 12:00', '2025-12-23 18:00', 1500, 'CH_DAI');

	INSERT INTO dbo.ChuyenTau(maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang) VALUES
	('CT7_20251223', 'DM007', 'NV11', 'GA_NT', 'GA_HP',
	 '2025-12-23 19:00', '2025-12-24 01:00', 1300, 'CH_DAI');


	-- =======================
	-- 24/12/2025
	-- =======================
	IF NOT EXISTS (SELECT 1 FROM dbo.ChuyenTau WHERE maChuyen = 'CT20251224A')
		INSERT INTO dbo.ChuyenTau(maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang)
		VALUES ('CT20251224A', 'DM001', 'NV05', 'GA_SG', 'GA_NT',
				'2025-12-24 07:00', '2025-12-24 13:00', 400, 'CH_TRUNG');

	IF NOT EXISTS (SELECT 1 FROM dbo.ChuyenTau WHERE maChuyen = 'CT20251224B')
		INSERT INTO dbo.ChuyenTau(maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang)
		VALUES ('CT20251224B', 'DM002', 'NV06', 'GA_NT', 'GA_SG',
				'2025-12-24 15:00', '2025-12-24 21:00', 400, 'CH_TRUNG');

	IF NOT EXISTS (SELECT 1 FROM dbo.ChuyenTau WHERE maChuyen = 'CT3_20251224A')
		INSERT INTO dbo.ChuyenTau(maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang)
		VALUES ('CT3_20251224A', 'DM003', 'NV07', 'GA_SG', 'GA_NT',
				'2025-12-24 08:00', '2025-12-24 14:00', 400, 'CH_TRUNG');

	IF NOT EXISTS (SELECT 1 FROM dbo.ChuyenTau WHERE maChuyen = 'CT3_20251224B')
		INSERT INTO dbo.ChuyenTau(maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang)
		VALUES ('CT3_20251224B', 'DM004', 'NV08', 'GA_NT', 'GA_SG',
            '2025-12-24 16:00', '2025-12-24 22:00', 400, 'CH_TRUNG');

	INSERT INTO dbo.ChuyenTau(maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang) VALUES
	('CT5_20251224', 'DM005', 'NV09', 'GA_BH', 'GA_NT',
	 '2025-12-24 07:00', '2025-12-24 11:00', 350, 'CH_TRUNG');

	INSERT INTO dbo.ChuyenTau(maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang) VALUES
	('CT6_20251224', 'DM006', 'NV10', 'GA_SG', 'GA_HP',
	 '2025-12-24 12:00', '2025-12-24 18:00', 1700, 'CH_DAI');

	INSERT INTO dbo.ChuyenTau(maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang) VALUES
	('CT7_20251224', 'DM007', 'NV11', 'GA_HP', 'GA_SG',
	 '2025-12-24 19:00', '2025-12-25 01:00', 1700, 'CH_DAI');


	-- =======================
	-- 25/12/2025
	-- =======================
	IF NOT EXISTS (SELECT 1 FROM dbo.ChuyenTau WHERE maChuyen = 'CT20251225A')
		INSERT INTO dbo.ChuyenTau(maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang)
		VALUES ('CT20251225A', 'DM001', 'NV05', 'GA_SG', 'GA_NT',
				'2025-12-25 07:00', '2025-12-25 13:00', 400, 'CH_TRUNG');

	IF NOT EXISTS (SELECT 1 FROM dbo.ChuyenTau WHERE maChuyen = 'CT20251225B')
		INSERT INTO dbo.ChuyenTau(maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang)
		VALUES ('CT20251225B', 'DM002', 'NV06', 'GA_NT', 'GA_SG',
				'2025-12-25 15:00', '2025-12-25 21:00', 400, 'CH_TRUNG');
	IF NOT EXISTS (SELECT 1 FROM dbo.ChuyenTau WHERE maChuyen = 'CT3_20251225A')
		INSERT INTO dbo.ChuyenTau(maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang)
		VALUES ('CT3_20251225A', 'DM003', 'NV07', 'GA_SG', 'GA_NT',
				'2025-12-25 08:00', '2025-12-25 14:00', 400, 'CH_TRUNG');

	IF NOT EXISTS (SELECT 1 FROM dbo.ChuyenTau WHERE maChuyen = 'CT3_20251225B')
		INSERT INTO dbo.ChuyenTau(maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang)
		VALUES ('CT3_20251225B', 'DM004', 'NV08', 'GA_NT', 'GA_SG',
				'2025-12-25 16:00', '2025-12-25 22:00', 400, 'CH_TRUNG');

	INSERT INTO dbo.ChuyenTau(maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang) VALUES
	('CT5_20251225', 'DM005', 'NV09', 'GA_NT', 'GA_HN',
	 '2025-12-25 07:00', '2025-12-25 11:00', 1300, 'CH_DAI');

	INSERT INTO dbo.ChuyenTau(maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang) VALUES
	('CT6_20251225', 'DM006', 'NV10', 'GA_HP', 'GA_NT',
	 '2025-12-25 12:00', '2025-12-25 18:00', 1300, 'CH_DAI');

	INSERT INTO dbo.ChuyenTau(maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang) VALUES
	('CT7_20251225', 'DM007', 'NV11', 'GA_SG', 'GA_BH',
	 '2025-12-25 19:00', '2025-12-26 01:00', 30, 'CH_NGAN');


	-- =======================
	-- 26/12/2025
	-- =======================
	IF NOT EXISTS (SELECT 1 FROM dbo.ChuyenTau WHERE maChuyen = 'CT20251226A')
		INSERT INTO dbo.ChuyenTau(maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang)
		VALUES ('CT20251226A', 'DM001', 'NV05', 'GA_SG', 'GA_NT',
				'2025-12-26 07:00', '2025-12-26 13:00', 400, 'CH_TRUNG');

	IF NOT EXISTS (SELECT 1 FROM dbo.ChuyenTau WHERE maChuyen = 'CT20251226B')
		INSERT INTO dbo.ChuyenTau(maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang)
		VALUES ('CT20251226B', 'DM002', 'NV06', 'GA_NT', 'GA_SG',
				'2025-12-26 15:00', '2025-12-26 21:00', 400, 'CH_TRUNG');

	IF NOT EXISTS (SELECT 1 FROM dbo.ChuyenTau WHERE maChuyen = 'CT3_20251226A')
		INSERT INTO dbo.ChuyenTau(maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang)
		VALUES ('CT3_20251226A', 'DM003', 'NV07', 'GA_SG', 'GA_NT',
				'2025-12-26 08:00', '2025-12-26 14:00', 400, 'CH_TRUNG');

	IF NOT EXISTS (SELECT 1 FROM dbo.ChuyenTau WHERE maChuyen = 'CT3_20251226B')
		INSERT INTO dbo.ChuyenTau(maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang)
		VALUES ('CT3_20251226B', 'DM004', 'NV08', 'GA_NT', 'GA_SG',
				'2025-12-26 16:00', '2025-12-26 22:00', 400, 'CH_TRUNG');

	INSERT INTO dbo.ChuyenTau(maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang) VALUES
	('CT5_20251226', 'DM005', 'NV09', 'GA_HN', 'GA_HP',
	 '2025-12-26 07:00', '2025-12-26 11:00', 120, 'CH_NGAN');

	INSERT INTO dbo.ChuyenTau(maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang) VALUES
	('CT6_20251226', 'DM006', 'NV10', 'GA_NT', 'GA_BH',
	 '2025-12-26 12:00', '2025-12-26 18:00', 350, 'CH_TRUNG');

	INSERT INTO dbo.ChuyenTau(maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang) VALUES
	('CT7_20251226', 'DM007', 'NV11', 'GA_BH', 'GA_HN',
	 '2025-12-26 19:00', '2025-12-27 01:00', 1500, 'CH_DAI');


	-- =======================
	-- 27/12/2025
	-- =======================
	IF NOT EXISTS (SELECT 1 FROM dbo.ChuyenTau WHERE maChuyen = 'CT20251227A')
		INSERT INTO dbo.ChuyenTau(maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang)
		VALUES ('CT20251227A', 'DM001', 'NV05', 'GA_SG', 'GA_NT',
				'2025-12-27 07:00', '2025-12-27 13:00', 400, 'CH_TRUNG');

	IF NOT EXISTS (SELECT 1 FROM dbo.ChuyenTau WHERE maChuyen = 'CT20251227B')
		INSERT INTO dbo.ChuyenTau(maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang)
		VALUES ('CT20251227B', 'DM002', 'NV06', 'GA_NT', 'GA_SG',
				'2025-12-27 15:00', '2025-12-27 21:00', 400, 'CH_TRUNG');

	IF NOT EXISTS (SELECT 1 FROM dbo.ChuyenTau WHERE maChuyen = 'CT3_20251227A')
		INSERT INTO dbo.ChuyenTau(maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang)
		VALUES ('CT3_20251227A', 'DM003', 'NV07', 'GA_SG', 'GA_NT',
				'2025-12-27 08:00', '2025-12-27 14:00', 400, 'CH_TRUNG');

	IF NOT EXISTS (SELECT 1 FROM dbo.ChuyenTau WHERE maChuyen = 'CT3_20251227B')
		INSERT INTO dbo.ChuyenTau(maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang)
		VALUES ('CT3_20251227B', 'DM004', 'NV08', 'GA_NT', 'GA_SG',
            '2025-12-27 16:00', '2025-12-27 22:00', 400, 'CH_TRUNG');

	INSERT INTO dbo.ChuyenTau(maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang) VALUES
	('CT5_20251227', 'DM005', 'NV09', 'GA_HP', 'GA_SG',
	 '2025-12-27 07:00', '2025-12-27 11:00', 1700, 'CH_DAI');

	INSERT INTO dbo.ChuyenTau(maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang) VALUES
	('CT6_20251227', 'DM006', 'NV10', 'GA_BH', 'GA_HN',
	 '2025-12-27 12:00', '2025-12-27 18:00', 1500, 'CH_DAI');

	INSERT INTO dbo.ChuyenTau(maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang) VALUES
	('CT7_20251227', 'DM007', 'NV11', 'GA_HN', 'GA_NT',
	 '2025-12-27 19:00', '2025-12-28 01:00', 1300, 'CH_DAI');
    ----------------------------------------------------------------
    -- 6) Now map ToaTau -> ChuyenTau (ChiTietChuyenTau). These rows must match existing ToaTau and ChuyenTau.
    ----------------------------------------------------------------
    IF NOT EXISTS (SELECT 1 FROM dbo.ChiTietChuyenTau WHERE maChuyenTau = 'CT001' AND maToaTau = 'TOA001')
        INSERT INTO dbo.ChiTietChuyenTau (maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT001', 'TOA001', 1, 24);

    IF NOT EXISTS (SELECT 1 FROM dbo.ChiTietChuyenTau WHERE maChuyenTau = 'CT001' AND maToaTau = 'TOA002')
        INSERT INTO dbo.ChiTietChuyenTau (maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT001', 'TOA002', 2, 24);

    IF NOT EXISTS (SELECT 1 FROM dbo.ChiTietChuyenTau WHERE maChuyenTau = 'CT006' AND maToaTau = 'TOA001')
        INSERT INTO dbo.ChiTietChuyenTau (maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT006', 'TOA003', 1, 24);


	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT20251223A','TOA001',1,24);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT20251223A','TOA005',2,24);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT20251223A','TOA003',3,20);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT20251223A','TOA007',4,24);

	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT20251223B','TOA002',1,24);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT20251223B','TOA006',2,24);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT20251223B','TOA004',3,20);

	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT3_20251223A','TOA007',1,24);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT3_20251223A','TOA009',2,20);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT3_20251223A','TOA005',3,24);

	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT3_20251223B','TOA008',1,20);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT3_20251223B','TOA003',2,20);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT3_20251223B','TOA001',3,24);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT3_20251223B','TOA006',4,24);

	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT5_20251223','TOA002',1,24);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT5_20251223','TOA004',2,20);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT5_20251223','TOA005',3,24);

	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT6_20251223','TOA007',1,24);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT6_20251223','TOA008',2,20);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT6_20251223','TOA009',3,20);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT6_20251223','TOA001',4,24);

	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT7_20251223','TOA003',1,20);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT7_20251223','TOA006',2,24);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT7_20251223','TOA010',3,20);

	-- =========================
	-- 24/12/2025
	-- =========================
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT20251224A','TOA001',1,24);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT20251224A','TOA003',2,20);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT20251224A','TOA005',3,24);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT20251224A','TOA007',4,24);

	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT20251224B','TOA002',1,24);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT20251224B','TOA004',2,20);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT20251224B','TOA006',3,24);

	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT3_20251224A','TOA007',1,24);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT3_20251224A','TOA009',2,20);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT3_20251224A','TOA001',3,24);

	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT3_20251224B','TOA008',1,20);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT3_20251224B','TOA003',2,20);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT3_20251224B','TOA006',3,24);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT3_20251224B','TOA010',4,20);

	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT5_20251224','TOA005',1,24);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT5_20251224','TOA004',2,20);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT5_20251224','TOA002',3,24);

	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT6_20251224','TOA007',1,24);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT6_20251224','TOA008',2,20);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT6_20251224','TOA009',3,20);

	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT7_20251224','TOA010',1,20);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT7_20251224','TOA003',2,20);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT7_20251224','TOA006',3,24);

	-- =========================
	-- 25/12/2025
	-- =========================
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT20251225A','TOA001',1,24);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT20251225A','TOA005',2,24);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT20251225A','TOA003',3,20);

	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT20251225B','TOA002',1,24);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT20251225B','TOA004',2,20);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT20251225B','TOA006',3,24);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT20251225B','TOA008',4,20);

	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT3_20251225A','TOA007',1,24);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT3_20251225A','TOA009',2,20);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT3_20251225A','TOA002',3,24);

	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT3_20251225B','TOA010',1,20);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT3_20251225B','TOA003',2,20);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT3_20251225B','TOA005',3,24);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT3_20251225B','TOA006',4,24);

	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT5_20251225','TOA001',1,24);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT5_20251225','TOA004',2,20);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT5_20251225','TOA007',3,24);

	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT6_20251225','TOA002',1,24);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT6_20251225','TOA008',2,20);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT6_20251225','TOA009',3,20);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT6_20251225','TOA005',4,24);

	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT7_20251225','TOA003',1,20);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT7_20251225','TOA006',2,24);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT7_20251225','TOA010',3,20);

	-- =========================
	-- 26/12/2025
	-- =========================
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT20251226A','TOA001',1,24);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT20251226A','TOA003',2,20);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT20251226A','TOA005',3,24);

	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT20251226B','TOA002',1,24);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT20251226B','TOA004',2,20);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT20251226B','TOA006',3,24);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT20251226B','TOA009',4,20);

	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT3_20251226A','TOA007',1,24);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT3_20251226A','TOA008',2,20);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT3_20251226A','TOA001',3,24);

	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT3_20251226B','TOA010',1,20);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT3_20251226B','TOA003',2,20);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT3_20251226B','TOA005',3,24);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT3_20251226B','TOA006',4,24);

	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT5_20251226','TOA002',1,24);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT5_20251226','TOA004',2,20);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT5_20251226','TOA007',3,24);

	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT6_20251226','TOA008',1,20);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT6_20251226','TOA009',2,20);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT6_20251226','TOA001',3,24);

	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT7_20251226','TOA003',1,20);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT7_20251226','TOA006',2,24);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT7_20251226','TOA010',3,20);

	-- =========================
	-- 27/12/2025
	-- =========================
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT20251227A','TOA001',1,24);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT20251227A','TOA005',2,24);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT20251227A','TOA003',3,20);

	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT20251227B','TOA002',1,24);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT20251227B','TOA004',2,20);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT20251227B','TOA006',3,24);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT20251227B','TOA008',4,20);

	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT3_20251227A','TOA007',1,24);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT3_20251227A','TOA009',2,20);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT3_20251227A','TOA002',3,24);

	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT3_20251227B','TOA010',1,20);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT3_20251227B','TOA003',2,20);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT3_20251227B','TOA005',3,24);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT3_20251227B','TOA006',4,24);

	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT5_20251227','TOA001',1,24);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT5_20251227','TOA004',2,20);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT5_20251227','TOA007',3,24);

	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT6_20251227','TOA008',1,20);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT6_20251227','TOA009',2,20);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT6_20251227','TOA001',3,24);

	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT7_20251227','TOA003',1,20);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT7_20251227','TOA006',2,24);
	INSERT INTO ChiTietChuyenTau(maChuyenTau, maToaTau, soThuTuToa, sucChua) VALUES ('CT7_20251227','TOA010',3,20);


    ----------------------------------------------------------------
    -- 7) Generate seats (Ghe) for ToaTau that were mapped above.
    --    We create maGhe values as 'G_' + maToa + '_' + two-digit seat number (01..)
    ----------------------------------------------------------------
    ;WITH Numbers AS (
		SELECT 1 AS n
		UNION ALL
		SELECT n + 1 FROM Numbers WHERE n < 100
	),
	SeatsToInsert AS (
		SELECT
			'G_' + t.maToa + '_' + RIGHT('00' + CAST(n AS VARCHAR(3)), 2) AS maGhe,
			t.maToa,
			CASE WHEN t.loaiToa = 'TOANGOI' THEN 'LG01' ELSE 'LG02' END AS loaiGhe,
			N'Rảnh' AS trangThai
		FROM dbo.ToaTau t
		CROSS JOIN Numbers
		WHERE n <= ISNULL(t.sucChua, 0)
		  AND EXISTS (
				SELECT 1
				FROM dbo.ChiTietChuyenTau c
				WHERE c.maToaTau = t.maToa   -- dùng đúng cột maToaTau
		  )
	)
	INSERT INTO dbo.Ghe (maGhe, maToa, loaiGhe, trangThai)
	SELECT s.maGhe, s.maToa, s.loaiGhe, s.trangThai
	FROM SeatsToInsert s
	LEFT JOIN dbo.Ghe g ON g.maGhe = s.maGhe
	WHERE g.maGhe IS NULL
	OPTION (MAXRECURSION 0);


    ----------------------------------------------------------------
    -- 8) Insert sample Ve rows (tickets). Ensure referenced Ghe exist.
    ----------------------------------------------------------------
    -- Remove specific demo ve IDs if present (safe for reruns)
    IF EXISTS (SELECT 1 FROM dbo.Ve WHERE maVe IN ('VE001','VE002','VE003','VE004','VE005','VE006'))
    BEGIN
        DELETE FROM dbo.Ve WHERE maVe IN ('VE001','VE002','VE003','VE004','VE005','VE006');
    END

    -- Insert tickets for CT006 (uses TOA001 seats generated above)
    IF NOT EXISTS (SELECT 1 FROM dbo.Ve WHERE maVe = 'VE001')
        INSERT INTO dbo.Ve (maVe, maChuyen, maLoaiVe, maSoGhe, ngayIn, trangThai, gioDi, soToa, loaiCho, loaiVe, maBangGia, giaThanhToan)
        VALUES ('VE001', 'CT006', 'LV01', 'G_TOA001_01', '2025-01-05 08:00', N'Đã thanh toán', '2025-01-10 08:00', 1, N'Ghế ngồi', N'Người lớn', 'BG01', 100000);

    IF NOT EXISTS (SELECT 1 FROM dbo.Ve WHERE maVe = 'VE002')
        INSERT INTO dbo.Ve (maVe, maChuyen, maLoaiVe, maSoGhe, ngayIn, trangThai, gioDi, soToa, loaiCho, loaiVe, maBangGia, giaThanhToan)
        VALUES ('VE002', 'CT006', 'LV02', 'G_TOA001_02', '2025-01-05 08:05', N'Đã thanh toán', '2025-01-10 08:00', 1, N'Ghế ngồi', N'Trẻ em', 'BG01', 50000);

    IF NOT EXISTS (SELECT 1 FROM dbo.Ve WHERE maVe = 'VE003')
        INSERT INTO dbo.Ve (maVe, maChuyen, maLoaiVe, maSoGhe, ngayIn, trangThai, gioDi, soToa, loaiCho, loaiVe, maBangGia, giaThanhToan)
        VALUES ('VE003', 'CT006', 'LV03', 'G_TOA001_03', '2025-01-05 08:10', N'Đã thanh toán', '2025-01-10 08:00', 1, N'Ghế ngồi', N'Cao tuổi', 'BG01', 300000);

    IF NOT EXISTS (SELECT 1 FROM dbo.Ve WHERE maVe = 'VE004')
        INSERT INTO dbo.Ve (maVe, maChuyen, maLoaiVe, maSoGhe, ngayIn, trangThai, gioDi, soToa, loaiCho, loaiVe, maBangGia, giaThanhToan)
        VALUES ('VE004', 'CT006', 'LV04', 'G_TOA001_04', '2025-01-05 08:15', N'Đã thanh toán', '2025-01-10 08:00', 1, N'Ghế ngồi', N'Khuyết tật', 'BG01', 300000);

    IF NOT EXISTS (SELECT 1 FROM dbo.Ve WHERE maVe = 'VE005')
        INSERT INTO dbo.Ve (maVe, maChuyen, maLoaiVe, maSoGhe, ngayIn, trangThai, gioDi, soToa, loaiCho, loaiVe, maBangGia, giaThanhToan)
        VALUES ('VE005', 'CT006', 'LV01', 'G_TOA001_05', '2025-01-05 08:20', N'Đã thanh toán', '2025-01-10 08:00', 1, N'Ghế ngồi', N'Người lớn', 'BG01', 100000);

    IF NOT EXISTS (SELECT 1 FROM dbo.Ve WHERE maVe = 'VE006')
        INSERT INTO dbo.Ve (maVe, maChuyen, maLoaiVe, maSoGhe, ngayIn, trangThai, gioDi, soToa, loaiCho, loaiVe, maBangGia, giaThanhToan)
        VALUES ('VE006', 'CT006', 'LV02', 'G_TOA001_06', '2025-01-05 08:25', N'Đã thanh toán', '2025-01-10 08:00', 1, N'Ghế ngồi', N'Trẻ em', 'BG01', 50000);

    ----------------------------------------------------------------
    -- 9) HoaDon and ChiTietHoaDon (invoices linked to tickets)
    ----------------------------------------------------------------
    IF NOT EXISTS (SELECT 1 FROM dbo.HoaDon WHERE maHoaDon = 'HD001')
        INSERT INTO dbo.HoaDon (maHoaDon, maNV, maKH, tenKH, soDienThoai, ngayLap, phuongThucThanhToan, trangThai)
        VALUES ('HD001','NV05','KH001', N'Nguyễn Văn An', '0901234567', '2025-01-05 08:10', N'Tiền mặt', N'Hoàn tất');

    IF NOT EXISTS (SELECT 1 FROM dbo.HoaDon WHERE maHoaDon = 'HD002')
        INSERT INTO dbo.HoaDon (maHoaDon, maNV, maKH, tenKH, soDienThoai, ngayLap, phuongThucThanhToan, trangThai)
        VALUES ('HD002','NV06','KH002', N'Trần Thị Bích', '0912345678', '2025-01-05 08:15', N'Tiền mặt', N'Hoàn tất');

    IF NOT EXISTS (SELECT 1 FROM dbo.ChiTietHoaDon WHERE maHoaDon='HD001' AND maVe='VE001')
        INSERT INTO dbo.ChiTietHoaDon (maHoaDon, maVe, maLoaiVe, giaGoc, giaDaKM, moTa) VALUES ('HD001','VE001','LV01',100000,100000,N'Người lớn');

    IF NOT EXISTS (SELECT 1 FROM dbo.ChiTietHoaDon WHERE maHoaDon='HD002' AND maVe='VE002')
        INSERT INTO dbo.ChiTietHoaDon (maHoaDon, maVe, maLoaiVe, giaGoc, giaDaKM, moTa) VALUES ('HD002','VE002','LV02',100000,50000,N'Trẻ em');

    ----------------------------------------------------------------
    -- Finalize
    ----------------------------------------------------------------
    COMMIT;
    PRINT N'new_seed.sql applied successfully.';
END TRY
BEGIN CATCH
    DECLARE @errMsg NVARCHAR(4000) = ERROR_MESSAGE();
    PRINT N'Error occurred while applying new_seed.sql: ' + COALESCE(@errMsg, N'');
    IF XACT_STATE() <> 0 ROLLBACK;
    THROW;
END CATCH;
GO