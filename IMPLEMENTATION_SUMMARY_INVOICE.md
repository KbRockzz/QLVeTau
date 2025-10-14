# Invoice and Ticket PDF Generation - Implementation Summary

## Overview

This implementation adds complete invoice management and PDF generation capabilities to the train ticket booking system (QLVeTau).

## Features Implemented

### 1. Automatic Invoice Creation
- **Location**: `PnlDatVe.datVe()`
- **Behavior**: 
  - When a ticket is booked, system checks for existing open invoices
  - If found, adds ticket to existing invoice
  - If not found, creates new invoice with status "Chờ xác nhận" (Pending)
  - Each ticket is added to `ChiTietHoaDon` (Invoice Details)

### 2. Invoice Confirmation & PDF Export
- **Location**: `PnlQuanLyVe`
- **Features**:
  - List all invoices with details
  - Select invoice and click "Xuất hóa đơn" (Export Invoice)
  - Dialog prompts for payment method selection:
    - Tiền mặt (Cash)
    - Chuyển khoản (Bank Transfer)
  - On confirmation:
    - Updates invoice status to "Hoàn tất" (Completed)
    - Sets invoice date to current datetime
    - Sets payment method
    - Generates invoice PDF to `/invoices/` directory
    - Generates ticket PDFs for all tickets to `/tickets/` directory

### 3. Invoice PDF Format
- **File**: `invoices/HoaDon_[invoiceId].pdf`
- **Contents**:
  - Company header: "CONG TY CO PHAN VAN TAI DUONG SAT SAI GON"
  - Invoice ID, customer name, date, payment method
  - Table of tickets with: STT, Ticket ID, From Station, To Station, Date, Price
  - Total amount in VND format (###,### VND)
  - Footer: "Trang thai: Hoan tat - Cam on quy khach da su dung dich vu."

### 4. Ticket PDF Format (Boarding Pass)
- **File**: `tickets/Ve_[ticketId].pdf`
- **Contents**:
  - Title: "BOARDING PASS / VE TAU HOA"
  - Ticket ID (barcode placeholder)
  - Departure/Arrival stations (large font)
  - Train details: number, date, time
  - Seat details: carriage, seat number, seat type, ticket type
  - Footer: "Cam on quy khach da su dung dich vu!"

### 5. Enhanced Ticket History View
- **Location**: `PnlDoiVe`
- **Features**:
  - Search by customer ID
  - Shows ALL tickets for customer (via HoaDon → ChiTietHoaDon → Ve)
  - Includes completed/canceled tickets
  - Can select ticket for exchange

## Technical Architecture

### New Classes

#### HoaDonService
```java
public class HoaDonService {
    float capNhatTongTien(String maHoaDon)
    String xuatHoaDonPDF(String maHoaDon)
}
```

#### ChiTietHoaDonService
```java
public class ChiTietHoaDonService {
    List<ChiTietHoaDon> getByHoaDon(String maHoaDon)
}
```

### Extended Classes

#### VeService
- Added: `String inVePDF(Ve ve)`

#### HoaDonDAO
- Added: `List<HoaDon> findByKhachHang(String maKH)`

#### ChiTietHoaDonDAO
- Added: `List<ChiTietHoaDon> findByHoaDon(String maHoaDon)`

### Updated GUI Components

#### PnlDatVe
- Modified `datVe()` to auto-create invoices
- Imports: HoaDon, ChiTietHoaDon, BangGia DAOs

#### PnlQuanLyVe
- Complete rewrite with invoice management
- Invoice table display
- Export button with confirmation dialog

#### PnlDoiVe
- Updated `timKiemVe()` to use invoice chain
- Shows complete ticket history

## Database Schema

### HoaDon Table
```sql
maHoaDon VARCHAR(50) PRIMARY KEY
maNV VARCHAR(50)
maKH VARCHAR(50)
ngayLap DATETIME
phuongThucThanhToan VARCHAR
trangThai VARCHAR
```

### ChiTietHoaDon Table
```sql
maHoaDon VARCHAR(50) FK
maVe VARCHAR(50) FK
maLoaiVe VARCHAR(50)
giaGoc FLOAT
giaDaKM FLOAT
moTa VARCHAR
```

## Dependencies Added

### pom.xml
```xml
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itext7-core</artifactId>
    <version>7.2.5</version>
    <type>pom</type>
</dependency>
```

## Testing

### PDFGenerationTest
- Tests ticket PDF generation
- Tests directory creation
- Verifies PDF file validity
- All tests passing ✅

### Test Results
```
Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## User Workflow

```
1. Book Ticket (PnlDatVe)
   ↓
2. Invoice Auto-Created (status: "Chờ xác nhận")
   ↓
3. Open Invoice Management (PnlQuanLyVe)
   ↓
4. Select Invoice → Click "Xuất hóa đơn"
   ↓
5. Choose Payment Method (Tiền mặt / Chuyển khoản)
   ↓
6. Click "Xác nhận"
   ↓
7. System Generates:
   - Invoice PDF (invoices/HoaDon_*.pdf)
   - Ticket PDFs (tickets/Ve_*.pdf)
   ↓
8. Invoice Status → "Hoàn tất"
```

## File Structure

```
QLVeTau/
├── invoices/                          (auto-created, in .gitignore)
│   └── HoaDon_*.pdf
├── tickets/                           (auto-created, in .gitignore)
│   └── Ve_*.pdf
├── src/
│   ├── main/java/com/trainstation/
│   │   ├── service/
│   │   │   ├── HoaDonService.java              ✨ NEW
│   │   │   ├── ChiTietHoaDonService.java      ✨ NEW
│   │   │   └── VeService.java                  📝 UPDATED
│   │   ├── dao/
│   │   │   ├── HoaDonDAO.java                  📝 UPDATED
│   │   │   └── ChiTietHoaDonDAO.java          📝 UPDATED
│   │   └── gui/
│   │       ├── PnlDatVe.java                   📝 UPDATED
│   │       ├── PnlQuanLyVe.java               📝 UPDATED
│   │       └── PnlDoiVe.java                   📝 UPDATED
│   └── test/java/com/trainstation/
│       └── service/
│           └── PDFGenerationTest.java          ✨ NEW
├── .gitignore                                   📝 UPDATED
├── INVOICE_FEATURE_GUIDE.md                    ✨ NEW
└── IMPLEMENTATION_SUMMARY_INVOICE.md           ✨ NEW
```

## Key Design Decisions

1. **Auto-create invoices**: Reduces manual work, ensures all tickets are invoiced
2. **Open invoice concept**: Multiple tickets can be added before final confirmation
3. **Two-step process**: Book first, confirm/export later allows flexibility
4. **Batch PDF generation**: All tickets in invoice printed at once
5. **ASCII Vietnamese**: Simplified character handling (can be enhanced with fonts)

## Future Enhancements

- [ ] Add Vietnamese fonts with diacritics
- [ ] Generate QR codes/barcodes for tickets
- [ ] Email invoices and tickets to customers
- [ ] Direct printing from application
- [ ] Revenue reports by invoice
- [ ] Invoice cancellation/refund handling

## Performance Considerations

- PDF generation is synchronous (blocks UI)
- For large invoices with many tickets, consider async processing
- Directory creation is cached by filesystem
- Database queries optimized with proper indexing

## Security Considerations

- Invoice PDFs contain sensitive customer information
- Consider access control for `/invoices/` and `/tickets/` directories
- Validate user permissions before allowing invoice export
- Implement audit logging for invoice operations

## Build & Run

```bash
# Compile
mvn clean compile

# Run tests
mvn test

# Package
mvn package

# Run application
java -jar target/QLVeTau-1.0.0.jar
```

## Conclusion

All requirements from the problem statement have been successfully implemented:

✅ Auto-create invoices when booking tickets  
✅ Add tickets to invoice details automatically  
✅ Calculate total from invoice details  
✅ Confirmation dialog with payment method selection  
✅ Export invoice PDF with proper format  
✅ Print ticket PDF in boarding pass style  
✅ Extended ticket history view through invoice chain  
✅ All service and DAO methods implemented  
✅ Tests created and passing  
✅ Build successful  

The system is ready for deployment and use! 🚀

---

**Implementation Date**: October 14, 2025  
**Version**: 1.0.0  
**Status**: ✅ Complete
