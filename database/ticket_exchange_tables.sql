-- Script tạo các bảng cho chức năng đổi vé
-- Thực thi sau khi đã có database QLTauHoa

USE QLTauHoa;
GO

-- Bảng lịch sử vé (lưu snapshot trước và sau khi đổi)
IF OBJECT_ID(N'dbo.VeHistory', N'U') IS NULL
BEGIN
    CREATE TABLE VeHistory (
        maLichSu VARCHAR(50) PRIMARY KEY,
        maVe VARCHAR(50) NOT NULL,
        maChuyen VARCHAR(50),
        maLoaiVe VARCHAR(50),
        maSoGhe VARCHAR(50),
        trangThai NVARCHAR(50),
        gaDi NVARCHAR(100),
        gaDen NVARCHAR(100),
        gioDi DATETIME,
        soToa VARCHAR(50),
        loaiCho NVARCHAR(100),
        loaiVe NVARCHAR(100),
        maBangGia VARCHAR(50),
        ngayThayDoi DATETIME NOT NULL,
        loaiThayDoi NVARCHAR(50), -- 'DOI_VE', 'HOAN_VE', 'HUY_VE'
        nguoiThucHien VARCHAR(50),
        ghiChu NVARCHAR(500)
    );
END

-- Bảng giao dịch đổi vé
IF OBJECT_ID(N'dbo.GiaoDichDoiVe', N'U') IS NULL
BEGIN
    CREATE TABLE GiaoDichDoiVe (
        maGiaoDich VARCHAR(50) PRIMARY KEY,
        maVeCu VARCHAR(50) NOT NULL,
        maVeMoi VARCHAR(50) NOT NULL,
        ngayDoi DATETIME NOT NULL,
        maNV VARCHAR(50),
        maKH VARCHAR(50),
        -- Thông tin tài chính
        giaVeCu FLOAT,
        giaVeMoi FLOAT,
        phiDoiVe FLOAT,
        chenhLechGia FLOAT,
        soTienThu FLOAT, -- Số tiền phải thu thêm (nếu > 0)
        soTienHoan FLOAT, -- Số tiền hoàn lại (nếu > 0)
        -- Thông tin phê duyệt
        trangThai NVARCHAR(50), -- 'CHO_DUYET', 'DA_DUYET', 'TU_CHOI', 'HOAN_THANH'
        nguoiDuyet VARCHAR(50),
        ngayDuyet DATETIME,
        lyDoTuChoi NVARCHAR(500),
        ghiChu NVARCHAR(500),
        CONSTRAINT FK_GiaoDichDoiVe_Ve_Cu FOREIGN KEY (maVeCu) REFERENCES Ve(maVe),
        CONSTRAINT FK_GiaoDichDoiVe_Ve_Moi FOREIGN KEY (maVeMoi) REFERENCES Ve(maVe),
        CONSTRAINT FK_GiaoDichDoiVe_NhanVien FOREIGN KEY (maNV) REFERENCES NhanVien(maNV),
        CONSTRAINT FK_GiaoDichDoiVe_KhachHang FOREIGN KEY (maKH) REFERENCES KhachHang(maKhachHang),
        CONSTRAINT FK_GiaoDichDoiVe_NguoiDuyet FOREIGN KEY (nguoiDuyet) REFERENCES NhanVien(maNV)
    );
END

-- Bảng audit log (ghi lại tất cả thao tác quan trọng)
IF OBJECT_ID(N'dbo.AuditLog', N'U') IS NULL
BEGIN
    CREATE TABLE AuditLog (
        maLog VARCHAR(50) PRIMARY KEY,
        loaiThaoTac NVARCHAR(100), -- 'DOI_VE', 'HOAN_VE', 'HUY_VE', 'DUYET_DOI_VE', etc.
        maThamChieu VARCHAR(50), -- Mã vé, mã giao dịch, etc.
        maNV VARCHAR(50),
        thoiGian DATETIME NOT NULL,
        noiDung NVARCHAR(1000),
        duLieuTruoc NVARCHAR(MAX), -- JSON snapshot trước khi thay đổi
        duLieuSau NVARCHAR(MAX), -- JSON snapshot sau khi thay đổi
        diaChiIP VARCHAR(50),
        CONSTRAINT FK_AuditLog_NhanVien FOREIGN KEY (maNV) REFERENCES NhanVien(maNV)
    );
END

-- Bảng cấu hình tham số hệ thống cho đổi vé
IF OBJECT_ID(N'dbo.CauHinhDoiVe', N'U') IS NULL
BEGIN
    CREATE TABLE CauHinhDoiVe (
        maCauHinh VARCHAR(50) PRIMARY KEY,
        tenCauHinh NVARCHAR(100),
        giaTriSo FLOAT,
        giaTriChuoi NVARCHAR(500),
        moTa NVARCHAR(500),
        ngayCapNhat DATETIME
    );
    
    -- Thêm các tham số mặc định
    INSERT INTO CauHinhDoiVe (maCauHinh, tenCauHinh, giaTriSo, moTa, ngayCapNhat)
    VALUES 
        ('PHI_DOI_72H', N'Phí đổi vé trước 72 giờ (%)', 10, N'Phí đổi vé trước 72 giờ khởi hành', GETDATE()),
        ('PHI_DOI_24_72H', N'Phí đổi vé 24-72 giờ (%)', 20, N'Phí đổi vé trong khoảng 24-72 giờ khởi hành', GETDATE()),
        ('PHI_DOI_MIN_72H', N'Phí đổi vé tối thiểu trước 72h', 20000, N'Phí đổi vé tối thiểu trước 72 giờ', GETDATE()),
        ('PHI_DOI_MIN_24_72H', N'Phí đổi vé tối thiểu 24-72h', 50000, N'Phí đổi vé tối thiểu trong khoảng 24-72 giờ', GETDATE()),
        ('THOI_HAN_DOI_MIN', N'Thời hạn đổi vé tối thiểu (giờ)', 24, N'Số giờ tối thiểu trước giờ khởi hành để được đổi vé', GETDATE()),
        ('NGUONG_DUYET_DOI_VE', N'Ngưỡng phê duyệt giao dịch đổi vé', 5000000, N'Giao dịch đổi vé có giá trị trên ngưỡng này cần phê duyệt', GETDATE());
END

GO

PRINT N'Đã tạo các bảng cho chức năng đổi vé thành công!';
