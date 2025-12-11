# Ticket Exchange Feature - Technical Implementation Guide

## Overview

This document describes the technical implementation of the comprehensive ticket exchange (Đổi Vé) feature for the QLVeTau train ticket management system.

## Architecture

### Components

1. **Model Layer**
   - `LichSuDoiVe`: Audit trail model for ticket exchanges
   - `Ve`: Enhanced with exchange support
   - `DoiVeRequest`: DTO for exchange requests
   - `DoiVeResult`: DTO for exchange results

2. **DAO Layer**
   - `LichSuDoiVeDAO`: Database operations for exchange history
   - `GheDAO`: Enhanced with locking support
   - `ChiTietHoaDonDAO`: Enhanced with price update methods
   - `VeDAO`: Existing DAO with ticket operations

3. **Service Layer**
   - `VeService`: Core business logic for ticket exchange
     - `validateDoiVe()`: Validation logic
     - `yeuCauDoiVe()`: Main exchange method
     - `thucHienDoiVe()`: Atomic transaction execution
     - `approveDoiVe()`: Approval workflow
     - `layDanhSachChoDuyet()`: Get pending approvals
     - `layLichSuDoiVe()`: Get exchange history

4. **UI Layer**
   - `PnlDoiVe`: Enhanced panel with phone search
   - `DlgDoiVe`: Comprehensive dialog for exchange
   - `PnlDuyetDoiVe`: Approval panel for managers

## Database Schema

### LichSuDoiVe Table

```sql
CREATE TABLE LichSuDoiVe (
    maLichSu NVARCHAR(50) PRIMARY KEY,
    maVe NVARCHAR(50) NOT NULL,
    maNV NVARCHAR(50),
    thoiGian DATETIME NOT NULL DEFAULT GETDATE(),
    chiTietCu NVARCHAR(MAX),
    chiTietMoi NVARCHAR(MAX),
    lyDo NVARCHAR(500),
    trangThai NVARCHAR(50) DEFAULT N'Đã duyệt',
    chenhLechGia FLOAT DEFAULT 0,
    CONSTRAINT FK_LichSuDoiVe_Ve FOREIGN KEY (maVe) REFERENCES Ve(maVe),
    CONSTRAINT FK_LichSuDoiVe_NhanVien FOREIGN KEY (maNV) REFERENCES NhanVien(maNhanVien)
);
```

### Indexes

```sql
CREATE INDEX IX_LichSuDoiVe_MaVe ON LichSuDoiVe(maVe);
CREATE INDEX IX_LichSuDoiVe_TrangThai ON LichSuDoiVe(trangThai);
CREATE INDEX IX_LichSuDoiVe_ThoiGian ON LichSuDoiVe(thoiGian DESC);
```

## Business Rules

### Exchange Eligibility

A ticket can be exchanged if:
1. Status is "Đã đặt" or "Đã thanh toán"
2. Exchange is requested at least 2 hours before departure (configurable)
3. New seat is available
4. Ticket is not "Đã hoàn" or "Đã hủy"

### Price Difference Handling

1. **Customer pays more** (New price > Old price):
   - Update ChiTietHoaDon with new price
   - Collect additional payment
   - Update invoice if not completed

2. **Customer gets refund** (New price < Old price):
   - Create refund request
   - May require manager approval (configurable)
   - Update ChiTietHoaDon

3. **No difference**:
   - Direct exchange
   - No payment processing needed

## Transaction Flow

### Main Exchange Transaction

```java
Connection conn = ConnectSql.getInstance().getConnection();
conn.setAutoCommit(false);
try {
    // 1. Lock new seat for verification
    Ghe gheMoiLocked = gheDAO.findByIdForUpdate(maGheMoi, conn);
    
    // 2. Release old seat
    gheDAO.setTrangThai(oldSeat, "Trống", conn);
    
    // 3. Update ticket information
    veDAO.update(newTicket);
    
    // 4. Reserve new seat
    gheDAO.setTrangThai(newSeat, "Đã đặt", conn);
    
    // 5. Update ChiTietHoaDon
    chiTietHoaDonDAO.updateDonGia(maHD, maVe, newPrice, conn);
    
    // 6. Save exchange history
    lichSuDoiVeDAO.insert(lichSu, conn);
    
    // 7. Commit
    conn.commit();
} catch (Exception e) {
    conn.rollback();
    throw e;
} finally {
    conn.setAutoCommit(true);
    conn.close();
}
```

## Concurrency Control

### Database Locking

Uses SQL Server row-level locking to prevent race conditions:

```sql
SELECT maGhe, maToa, trangThai, loaiGhe 
FROM Ghe WITH (UPDLOCK, ROWLOCK) 
WHERE maGhe = ?
```

This ensures:
- Only one transaction can reserve a seat at a time
- Prevents double-booking
- Maintains data consistency

### Validation Steps

1. Check seat availability before transaction
2. Lock seat during transaction
3. Re-verify seat is still available
4. Complete or rollback atomically

## Configuration

### Configurable Parameters

```java
// In VeService
private static final int HOURS_BEFORE_DEPARTURE_TO_EXCHANGE = 2;
```

To change:
1. Modify the constant in `VeService.java`
2. Recompile
3. Redeploy

Future enhancement: Move to configuration file or database

## Error Handling

### Common Errors

1. **Seat Already Taken**
   - Detect during lock acquisition
   - Rollback transaction
   - Return user-friendly message

2. **Time Restriction Violation**
   - Validate before transaction
   - Return clear error message
   - Suggest alternatives if available

3. **Database Connection Issues**
   - Automatic rollback
   - Log error details
   - Retry logic (future enhancement)

## Logging and Audit

### Exchange History

Every exchange creates a `LichSuDoiVe` record with:
- Unique ID (LSDV0001, LSDV0002, ...)
- Old ticket details (JSON/text format)
- New ticket details (JSON/text format)
- Employee who performed exchange
- Timestamp
- Reason/notes
- Price difference
- Status (Approved/Pending/Rejected)

### Query Examples

```sql
-- Get all exchanges for a ticket
SELECT * FROM LichSuDoiVe WHERE maVe = 'VE0001' ORDER BY thoiGian DESC;

-- Get pending approvals
SELECT * FROM LichSuDoiVe WHERE trangThai = N'Chờ duyệt';

-- Get exchanges by employee
SELECT * FROM LichSuDoiVe WHERE maNV = 'NV001' ORDER BY thoiGian DESC;
```

## API Methods

### VeService

```java
// Validate if ticket can be exchanged
DoiVeResult validateDoiVe(String maVe)

// Request ticket exchange
DoiVeResult yeuCauDoiVe(DoiVeRequest request)

// Approve exchange request
boolean approveDoiVe(String maLichSu, String maNV, boolean chapNhan)

// Get pending approvals
List<LichSuDoiVe> layDanhSachChoDuyet()

// Get exchange history for ticket
List<LichSuDoiVe> layLichSuDoiVe(String maVe)
```

### GheDAO

```java
// Update seat status
boolean setTrangThai(String maGhe, String trangThai)

// Update with existing connection (transactional)
boolean setTrangThai(String maGhe, String trangThai, Connection conn)

// Lock seat for update
Ghe findByIdForUpdate(String maGhe, Connection conn)
```

### ChiTietHoaDonDAO

```java
// Update ticket price in invoice detail
boolean updateDonGia(String maHD, String maVe, float giaGoc, float giaDaKM)

// Update with existing connection (transactional)
boolean updateDonGia(String maHD, String maVe, float giaGoc, float giaDaKM, Connection conn)

// Find by ticket
ChiTietHoaDon findByMaVe(String maVe)
```

## Testing Considerations

### Unit Tests

1. Test validation logic
2. Test price calculation
3. Test time restriction checking
4. Test status transitions

### Integration Tests

1. Test complete exchange flow
2. Test concurrent exchanges
3. Test rollback scenarios
4. Test approval workflow

### Manual Testing

1. Exchange to same train, different seat
2. Exchange to different train
3. Exchange with price increase
4. Exchange with price decrease
5. Exchange near time limit
6. Exchange after time limit (should fail)
7. Concurrent exchange attempts

## Performance Considerations

### Database Queries

- Indexes on frequently queried fields
- Efficient seat availability checks
- Batch operations where possible

### UI Responsiveness

- Load seat maps progressively
- Show loading indicators
- Cache static data (stations, trains)

## Security

### Authorization

- Check user role before allowing exchange
- Validate employee permissions
- Log all exchange attempts

### Data Validation

- Sanitize all user inputs
- Validate date ranges
- Verify seat existence
- Check ticket ownership

## Future Enhancements

1. **Bulk Exchange**: Exchange multiple tickets at once
2. **Exchange Fees**: Configurable exchange fees
3. **Email Notifications**: Auto-email new ticket to customer
4. **SMS Alerts**: SMS confirmation for exchanges
5. **Self-Service**: Allow customers to exchange online
6. **Exchange Limits**: Limit number of exchanges per ticket
7. **Blackout Periods**: Block exchanges during peak times
8. **Partial Refunds**: Calculate partial refunds for cancellations

## Deployment

### Database Migration

1. Run `database/create_lichsudoive_table.sql`
2. Verify table creation
3. Test with sample data

### Application Deployment

1. Build project: `mvn clean package`
2. Deploy JAR file
3. Restart application
4. Verify functionality

### Rollback Plan

1. Keep backup of old code
2. Keep database backup before migration
3. Test rollback procedure in staging
4. Document rollback steps

## Support and Maintenance

### Monitoring

- Monitor exchange success rate
- Track average exchange time
- Alert on unusual patterns
- Log errors for analysis

### Troubleshooting

1. Check database connectivity
2. Verify seat availability
3. Review transaction logs
4. Check user permissions

## Documentation

- User guide: `TICKET_EXCHANGE_GUIDE.md`
- Technical guide: This document
- API documentation: JavaDoc comments
- Database schema: `database/create_lichsudoive_table.sql`
