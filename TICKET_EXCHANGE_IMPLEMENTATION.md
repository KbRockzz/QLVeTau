# Ticket Exchange Feature Implementation Summary

## Overview
This document describes the implementation of the ticket exchange (đổi vé) feature according to the new business requirements.

## Key Business Rules

### 1. Same-Toa Constraint (Most Important)
- **Only allows seat exchange within the same toa (carriage)**
- **Cannot exchange to a different toa or different train**
- This is the primary constraint that differentiates this feature from other ticket modifications

### 2. Eligible Ticket Status
- Can exchange tickets with status: `Đã đặt` or `Đã thanh toán`
- Cannot exchange tickets with status: `Đã hoàn`, `Đã hủy`, or `Đã đổi`

### 3. Time Deadline
- Must exchange at least **2 hours before departure** (`gioDi`)
- Prevents last-minute exchanges that could cause operational issues

### 4. New Ticket Creation (Not Modification)
- Creates a new ticket with new ID
- Marks old ticket as `Đã đổi` (exchanged)
- Preserves audit trail in `ChiTietHoaDon.moTa`

### 5. What Changes
- **ONLY CHANGES**: `maSoGhe` (seat number)
- **DOES NOT CHANGE**: `maChuyen`, `soToa`, `maGaDi`, `maGaDen`, `gioDi`, `maLoaiVe`, `giaThanhToan`

## Implementation Details

### Database Changes
**No new tables created** - uses existing schema:
- `Ve` table: stores ticket information
- `Ghe` table: stores seat information including `maToa` foreign key
- `ChiTietHoaDon` table: stores invoice details with `moTa` field for audit trail

### Code Changes

#### 1. DAO Layer (`ChiTietHoaDonDAO.java`)
Added methods for updating audit trail:
```java
public boolean updateMoTa(String maHoaDon, String maVe, String moTa)
public boolean updateMoTa(String maHoaDon, String maVe, String moTa, Connection conn)
```

#### 2. Service Layer (`VeService.java`)
Added new ticket exchange method:
```java
public Ve thucHienDoiVe(String maVeCu, String maGheMoi, String lyDo)
```

**Validation logic**:
1. ✅ Check ticket exists
2. ✅ Check ticket status (only `Đã đặt` or `Đã thanh toán`)
3. ✅ Check time deadline (2 hours before departure)
4. ✅ Check old and new seats exist
5. ✅ **Check same-toa constraint** (most important)
6. ✅ Check new seat is available (`Trống`)

**Transaction steps**:
1. Update old seat status → `Trống`
2. Update new seat status → `Đã đặt`
3. Create new ticket (copy all fields except `maSoGhe` and `maVe`)
4. Update old ticket status → `Đã đổi`
5. Update audit trail in `ChiTietHoaDon`:
   - Old ticket: `"Đã đổi sang <maVeMoi>"`
   - New ticket: `"Đổi từ <maVeCu>; lý do: <lyDo>"`

#### 3. GUI Layer

**DlgDoiVe.java** (new dialog):
- Left panel: Read-only display of current ticket information
- Right panel: Seat map showing **ONLY seats in the same toa**
- Color coding:
  - 🟢 Green: Available seats (`Trống`)
  - 🔴 Red: Occupied seats (`Đã đặt`)
  - 🟡 Orange: Current seat
  - 🔵 Blue: Selected seat
- Reason input field (optional)
- Validates all business rules before confirming exchange

**PnlDoiVe.java** (updated):
- Search options:
  - By phone number (`soDienThoai`)
  - By ticket code (`maVe`)
- Opens `DlgDoiVe` dialog when "Đổi vé" button clicked
- Pre-validates ticket status before opening dialog
- Refreshes ticket list after successful exchange

## Test Cases

### Test Case 1: Successful Exchange (Same Toa)
- **Input**: Valid ticket, seat in same toa, more than 2 hours before departure
- **Expected**: New ticket created, old ticket marked as "Đã đổi", seats updated
- **Status**: ✅ Implemented

### Test Case 2: Reject Different Toa
- **Input**: Valid ticket, seat in different toa
- **Expected**: Error message "Chỉ được đổi ghế trong cùng một toa"
- **Status**: ✅ Implemented

### Test Case 3: Reject Occupied Seat
- **Input**: Valid ticket, occupied seat in same toa
- **Expected**: Error message "Ghế đã bị đặt"
- **Status**: ✅ Implemented

### Test Case 4: Reject Invalid Status
- **Input**: Ticket with status "Đã hoàn"
- **Expected**: Error message "Không thể đổi vé này"
- **Status**: ✅ Implemented

### Test Case 5: Reject Past Deadline
- **Input**: Valid ticket, less than 2 hours before departure
- **Expected**: Error message "Đã quá thời hạn đổi vé"
- **Status**: ✅ Implemented

## Code Quality Improvements

Based on code review feedback:
1. ✅ Changed ticket ID generation from `System.currentTimeMillis()` to UUID-based to avoid collisions
2. ✅ Removed redundant validation logic
3. ✅ Added named constant `TICKET_ACTIVE` for better readability
4. ✅ Optimized seat map update to avoid full reconstruction on each selection
5. ✅ All changes compile successfully
6. ✅ No security vulnerabilities detected by CodeQL

## Usage Example

```java
VeService veService = VeService.getInstance();

// Exchange ticket from seat GHE_001 to GHE_002 in same toa
try {
    Ve newTicket = veService.thucHienDoiVe(
        "VE_123456",           // Old ticket ID
        "GHE_002",             // New seat ID (must be in same toa)
        "Khách yêu cầu đổi"    // Reason
    );
    System.out.println("Exchange successful! New ticket: " + newTicket.getMaVe());
} catch (IllegalStateException e) {
    System.out.println("Cannot exchange: " + e.getMessage());
}
```

## Future Enhancements

1. **Email notifications**: Send confirmation email after successful exchange
2. **SMS notifications**: Send SMS to customer about exchange
3. **Exchange history**: Add separate table to track all exchanges
4. **Refund handling**: If new seat is cheaper, handle price difference
5. **Admin override**: Allow admins to override time deadline in special cases
6. **Bulk exchange**: Support exchanging multiple tickets at once

## Files Modified

1. `src/main/java/com/trainstation/dao/ChiTietHoaDonDAO.java`
2. `src/main/java/com/trainstation/service/VeService.java`
3. `src/main/java/com/trainstation/gui/PnlDoiVe.java`
4. `src/main/java/com/trainstation/gui/DlgDoiVe.java` (new)
5. `src/test/java/com/trainstation/service/VeServiceExchangeTest.java` (new)

## Backward Compatibility

The old `doiVe()` method is marked as `@Deprecated` but still functional for backward compatibility. New code should use `thucHienDoiVe()` instead.
