-- SQL Script to create LichSuDoiVe table for ticket exchange history
-- Bảng lịch sử đổi vé

USE QLVeTau;
GO

-- Drop existing table if exists
IF OBJECT_ID('LichSuDoiVe', 'U') IS NOT NULL
    DROP TABLE LichSuDoiVe;
GO

-- Create LichSuDoiVe table
CREATE TABLE LichSuDoiVe (
    maLichSu NVARCHAR(50) PRIMARY KEY,
    maVe NVARCHAR(50) NOT NULL,
    maNV NVARCHAR(50),
    thoiGian DATETIME NOT NULL DEFAULT GETDATE(),
    chiTietCu NVARCHAR(MAX),
    chiTietMoi NVARCHAR(MAX),
    lyDo NVARCHAR(500),
    trangThai NVARCHAR(50) DEFAULT N'Đã duyệt', -- 'Chờ duyệt', 'Đã duyệt', 'Từ chối'
    chenhLechGia FLOAT DEFAULT 0,
    
    CONSTRAINT FK_LichSuDoiVe_Ve FOREIGN KEY (maVe) REFERENCES Ve(maVe),
    CONSTRAINT FK_LichSuDoiVe_NhanVien FOREIGN KEY (maNV) REFERENCES NhanVien(maNhanVien)
);
GO

-- Create index for faster queries
CREATE INDEX IX_LichSuDoiVe_MaVe ON LichSuDoiVe(maVe);
CREATE INDEX IX_LichSuDoiVe_TrangThai ON LichSuDoiVe(trangThai);
CREATE INDEX IX_LichSuDoiVe_ThoiGian ON LichSuDoiVe(thoiGian DESC);
GO

-- Add some sample comments
EXEC sp_addextendedproperty 
    @name = N'MS_Description', 
    @value = N'Lịch sử đổi vé - Audit trail cho các giao dịch đổi vé',
    @level0type = N'Schema', @level0name = 'dbo',
    @level1type = N'Table',  @level1name = 'LichSuDoiVe';
GO

EXEC sp_addextendedproperty 
    @name = N'MS_Description', 
    @value = N'Mã lịch sử đổi vé (LSDV0001, LSDV0002, ...)',
    @level0type = N'Schema', @level0name = 'dbo',
    @level1type = N'Table',  @level1name = 'LichSuDoiVe',
    @level2type = N'Column', @level2name = 'maLichSu';
GO

EXEC sp_addextendedproperty 
    @name = N'MS_Description', 
    @value = N'Chênh lệch giá vé (dương = khách trả thêm, âm = khách được hoàn)',
    @level0type = N'Schema', @level0name = 'dbo',
    @level1type = N'Table',  @level1name = 'LichSuDoiVe',
    @level2type = N'Column', @level2name = 'chenhLechGia';
GO

PRINT 'Table LichSuDoiVe created successfully!';
GO
