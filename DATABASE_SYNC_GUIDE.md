# DATABASE SYNCHRONIZATION IMPLEMENTATION GUIDE

## Summary
This document provides a complete guide for synchronizing the codebase with the new database schema defined in `database/script.sql`.

## ✅ COMPLETED: Phase 1 - Entity Models (100%)

All entity models have been updated to match the new database schema:

### New Entities Created:
1. **DauMay.java** - Locomotive entity with fields: maDauMay, loaiDauMay, tenDauMay, namSX, lanBaoTriGanNhat, trangThai, isActive
2. **Ga.java** - Station entity with fields: maGa, tenGa, moTa, tinhTrang, diaChi, isActive
3. **ChiTietChuyenTau.java** - Train composition entity with fields: maChuyenTau, maToaTau, soThuTuToa, sucChua, isActive

### Updated Entities:
1. **Ve.java** - Added: maGaDi, maGaDen, tenGaDi, tenGaDen, gioDenDuKien, giaThanhToan, isActive; Changed soToa from String to Integer
2. **ChuyenTau.java** - Changed: maTau→maDauMay, gaDi/gaDen→maGaDi/maGaDen; Added: isActive; Changed soKm from int to Integer
3. **ToaTau.java** - Removed: tenToa, maTau; Added: samSX, trangThai, isActive; Changed sucChua from int to Integer
4. **HoaDon.java** - Added: tenKH, soDienThoai, isActive
5. **ChiTietHoaDon.java** - Added: isActive; Changed float→Float
6. **Ghe.java** - Added: isActive
7. **NhanVien.java** - Added: isActive
8. **TaiKhoan.java** - Added: isActive
9. **BangGia.java** - Added: isActive; Changed float→Float
10. **LoaiVe.java** - Added: isActive (already had heSoGia)
11. **KhachHang.java** - Added: isActive
12. **LoaiGhe.java** - Added: isActive
13. **LoaiNV.java** - Added: isActive
14. **ChangTau.java** - Added: isActive; Changed int→Integer, float→Float

## ✅ COMPLETED: Phase 2A - New DAOs (100%)

Created 3 new DAO classes:

1. **DauMayDAO.java** - Complete CRUD operations for DauMay
2. **GaDAO.java** - Complete CRUD operations for Ga
3. **ChiTietChuyenTauDAO.java** - CRUD operations for train composition with composite key support

## 🔄 TODO: Phase 2B - Update Existing DAOs

The following DAOs need SQL query updates to match the new schema:

### Critical Priority (Core Functionality):

#### 1. **VeDAO.java**
Current SQL columns to update:
```sql
OLD: gaDi, gaDen, soToa (VARCHAR)
NEW: maGaDi, maGaDen, tenGaDi, tenGaDen, gioDenDuKien, giaThanhToan, soToa (INT), isActive
```

Example query update:
```sql
-- OLD
SELECT maVe, maChuyen, maLoaiVe, maSoGhe, ngayIn, trangThai, gaDi, gaDen, gioDi, soToa, loaiCho, loaiVe, maBangGia FROM Ve

-- NEW
SELECT maVe, maChuyen, maLoaiVe, maSoGhe, maGaDi, maGaDen, tenGaDi, tenGaDen, ngayIn, trangThai, gioDi, gioDenDuKien, soToa, loaiCho, loaiVe, maBangGia, giaThanhToan, isActive FROM Ve WHERE isActive = 1
```

#### 2. **ChuyenTauDAO.java**
Current columns to update:
```sql
OLD: maTau, gaDi, gaDen
NEW: maDauMay, maGaDi, maGaDen, isActive
```

Example query update:
```sql
-- OLD  
SELECT maChuyen, maTau, maNV, gaDi, gaDen, gioDi, gioDen, soKm, maChang, trangThai FROM ChuyenTau

-- NEW
SELECT maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang, trangThai, isActive FROM ChuyenTau WHERE isActive = 1
```

#### 3. **ToaTauDAO.java**
Current columns to update:
```sql
OLD: maToa, tenToa, loaiToa, maTau, sucChua
NEW: maToa, loaiToa, samSX, trangThai, sucChua, isActive
```

Example query update:
```sql
-- OLD
SELECT maToa, tenToa, loaiToa, maTau, sucChua FROM ToaTau

-- NEW
SELECT maToa, loaiToa, samSX, trangThai, sucChua, isActive FROM ToaTau WHERE isActive = 1
```

#### 4. **HoaDonDAO.java**
Current columns to update:
```sql
OLD: maHoaDon, maNV, maKH, ngayLap, phuongThucThanhToan, trangThai
NEW: maHoaDon, maNV, maKH, tenKH, soDienThoai, ngayLap, phuongThucThanhToan, trangThai, isActive
```

### Medium Priority:

#### 5. **GheDAO.java**
Add: isActive filtering
```sql
WHERE isActive = 1
```

#### 6. **ChiTietHoaDonDAO.java**
Add: isActive field to all queries

#### 7. **NhanVienDAO.java**
Add: isActive field to all queries

#### 8. **TaiKhoanDAO.java**
Add: isActive field to all queries

#### 9. **BangGiaDAO.java**
Add: isActive field to all queries

#### 10. **LoaiVeDAO.java**
Add: isActive field to all queries

#### 11. **KhachHangDAO.java**
Add: isActive field to all queries

#### 12. **LoaiGheDAO.java**
Add: isActive field to all queries

#### 13. **LoaiNVDAO.java**
Add: isActive field to all queries

#### 14. **ChangTauDAO.java**
Add: isActive field to all queries

### DAO Update Pattern:

For each DAO, update the following methods:

1. **getAll()** - Add `WHERE isActive = 1`
2. **findById()** - Add isActive column to SELECT
3. **add()** - Add isActive to INSERT (default to 1/true)
4. **update()** - Add isActive to UPDATE SET clause
5. **delete()** - Change from DELETE to UPDATE SET isActive = 0
6. **mapResultSetToEntity()** - Add isActive field mapping

## 🔄 TODO: Phase 3 - Service Layer Updates

### Services to Update:

#### 1. **ChuyenTauService.java**
- Update to use DauMayDAO instead of TauDAO
- Update to use GaDAO for station lookups
- Update ChiTietChuyenTauDAO usage for train composition
- Handle new maGaDi/maGaDen references

#### 2. **VeService.java**
- Update for new Ve fields (maGaDi, maGaDen, tenGaDi, tenGaDen, gioDenDuKien, giaThanhToan)
- Ensure soToa is handled as Integer
- Update pricing calculations if needed

#### 3. **HoaDonService.java**
- Update for new HoaDon fields (tenKH, soDienThoai)
- Ensure customer name/phone are populated correctly

#### 4. **TauService.java**
- Consider deprecating or refactoring to DauMayService
- Or update to work with new schema

#### 5. **TinhGiaService.java**
- Verify compatibility with new pricing structure
- Update if BangGia changes affect pricing logic

#### 6. **ThongKeService.java**
- Update statistics queries for new schema
- Update join logic for DauMay and Ga tables

## 🔄 TODO: Phase 4 - GUI Panel Updates

**Important**: Only update data binding, NOT layouts or UI structure.

### Panels to Update:

#### 1. **PnlChuyenTau** (Train Management)
- Update combo boxes to use GaDAO for stations (maGaDi/maGaDen)
- Update combo boxes to use DauMayDAO for locomotives (maDauMay)
- Update table model for new ChuyenTau fields
- Remove maTau references, use maDauMay instead

#### 2. **PnlDatVe** (Ticket Booking)
- Update Ve form fields for new columns
- Add/update fields: maGaDi, maGaDen, tenGaDi, tenGaDen, gioDenDuKien, giaThanhToan
- Update soToa handling (now Integer)
- Update search by station logic

#### 3. **PnlHoaDon** (Invoice Management)
- Update HoaDon form for tenKH and soDienThoai fields
- Update table model for new columns
- Ensure customer name/phone display correctly

#### 4. **PnlToaTau** (Carriage Management)
- Remove tenToa and maTau fields
- Add samSX and trangThai fields
- Update table model accordingly

#### 5. **PnlThongKe** (Statistics)
- Update queries to join with Ga and DauMay tables
- Update report generation for new schema
- Fix any broken statistics due to schema changes

#### 6. **PnlTimKiem** (Search Panels)
- Update search by phone to use new HoaDon.soDienThoai
- Update search by station to use Ga table
- Update train search to use DauMay

### GUI Update Pattern:

For each panel:
1. Update form bindings (TextField, ComboBox, DatePicker)
2. Update TableModel column definitions
3. Update data loading from entities to GUI
4. Update data saving from GUI to entities
5. Remove obsolete fields
6. Add new fields

## 🔄 TODO: Phase 5 - Verification & Testing

### Test Checklist:

1. **Database Connection**
   - [ ] Verify connection to QLTauHoa database
   - [ ] Verify all tables exist with correct schema
   - [ ] Run script.sql if needed to create/update schema

2. **CRUD Operations**
   - [ ] Test DauMay CRUD
   - [ ] Test Ga CRUD
   - [ ] Test ChiTietChuyenTau CRUD
   - [ ] Test Ve CRUD with new fields
   - [ ] Test ChuyenTau CRUD with new fields
   - [ ] Test all other entity CRUD operations

3. **Business Logic**
   - [ ] Test ticket booking flow
   - [ ] Test invoice generation
   - [ ] Test price calculation
   - [ ] Test search functionality
   - [ ] Test statistics generation

4. **GUI Functionality**
   - [ ] All panels load without errors
   - [ ] ComboBoxes populate correctly
   - [ ] Tables display data correctly
   - [ ] Forms save/update correctly
   - [ ] Search functions work

## Implementation Steps

### Recommended Order:

1. ✅ Update all entity models (COMPLETED)
2. ✅ Create new DAOs (DauMay, Ga, ChiTietChuyenTau) (COMPLETED)
3. Update critical DAOs (Ve, ChuyenTau, ToaTau, HoaDon)
4. Update remaining DAOs with isActive support
5. Update Service layer
6. Update GUI panels for data binding
7. Run comprehensive tests
8. Fix any issues found
9. Document changes

## SQL Query Examples

### Common Patterns:

#### Adding isActive Filter:
```sql
-- Before
SELECT * FROM TableName

-- After
SELECT * FROM TableName WHERE isActive = 1
```

#### Soft Delete:
```sql
-- Before
DELETE FROM TableName WHERE id = ?

-- After
UPDATE TableName SET isActive = 0 WHERE id = ?
```

#### Join with New Tables:
```sql
-- ChuyenTau with Ga stations
SELECT c.maChuyen, c.maDauMay, gd.tenGa as tenGaDi, gde.tenGa as tenGaDen
FROM ChuyenTau c
LEFT JOIN Ga gd ON c.maGaDi = gd.maGa
LEFT JOIN Ga gde ON c.maGaDen = gde.maGa
WHERE c.isActive = 1
```

## Notes

- All changes maintain backward compatibility where possible
- The isActive pattern enables soft delete functionality
- Foreign key changes (maTau→maDauMay, gaDi→maGaDi) require careful migration
- Test thoroughly before deploying to production
- Consider data migration scripts if existing data needs to be preserved

## Files Modified

### Entities (14 files):
- DauMay.java (NEW)
- Ga.java (NEW)
- ChiTietChuyenTau.java (NEW)
- Ve.java
- ChuyenTau.java
- ToaTau.java
- HoaDon.java
- ChiTietHoaDon.java
- Ghe.java
- NhanVien.java
- TaiKhoan.java
- BangGia.java
- LoaiVe.java
- KhachHang.java
- LoaiGhe.java
- LoaiNV.java
- ChangTau.java

### DAOs (3 new files):
- DauMayDAO.java (NEW)
- GaDAO.java (NEW)
- ChiTietChuyenTauDAO.java (NEW)

### Remaining Work:
- 14 existing DAOs to update
- 6+ Service classes to update
- 5+ GUI panels to update
- Comprehensive testing required
