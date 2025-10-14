# Seat Map UI Upgrade Documentation

## Overview
This document describes the upgraded UI for the PnlDatVe (Ticket Booking Panel) component.

## Visual Layout Changes

### Before:
```
Simple grid layout with 5 columns:
[Seat1] [Seat2] [Seat3] [Seat4] [Seat5]
[Seat6] [Seat7] [Seat8] [Seat9] [Seat10]
...
```

### After:
```
Realistic train carriage layout (2-2 configuration):
[Seat A1] [Seat A2]  ||  [Seat B1] [Seat B2]
[Seat A3] [Seat A4]  ||  [Seat B3] [Seat B4]
[Seat A5] [Seat A6]  ||  [Seat B5] [Seat B6]
...

Where || represents the aisle/corridor (gray panel)
```

## New Features

### 1. Customer Selection
- **Component**: JComboBox<String> cboKhachHang
- **Data Source**: KhachHangDAO.getAll()
- **Display Format**: "Customer Name (Phone Number)"
- **Location**: Top panel, left side

### 2. Add New Customer
- **Component**: JButton "+" + "Thêm khách hàng mới"
- **Functionality**: Opens modal dialog for adding new customers
- **Dialog Fields**:
  - Mã khách hàng (Customer ID) - Required
  - Họ tên (Full Name) - Required
  - Số điện thoại (Phone) - Required
  - CCCD (ID Card Number) - Optional
  - Địa chỉ (Address) - Optional
- **Validation**: Checks for duplicate customer IDs

### 3. Ticket Type Selection
- **Component**: JComboBox<String> cboLoaiVe
- **Data Source**: LoaiVeDAO.getAll()
- **Display Format**: Ticket type name (e.g., "Thường", "VIP", "Giường nằm")
- **Location**: Top panel, right side

### 4. Enhanced Seat Buttons
- **Size**: 80x40 pixels
- **Font**: Arial, Bold, 12pt
- **Border**: Black, 2px solid
- **Colors**:
  - Available seats: Green (#228B22) with white text
  - Booked seats: Red with white text
- **Tooltips**:
  - Available: "Ghế [ID] - Trống"
  - Booked: "Ghế [ID] - Đã đặt"

### 5. Booking Confirmation
- **Dialog**: Shows complete booking details before confirmation
- **Information Displayed**:
  - Customer name
  - Train route (departure → arrival)
  - Carriage name
  - Seat number
  - Ticket type
- **Options**: Yes/No confirmation

## UI Layout Structure

```
┌─────────────────────────────────────────────────────┐
│              ĐẶT VÉ TÀU (Title)                     │
├─────────────────────────────────────────────────────┤
│ Khách hàng: [ComboBox▼] [+ Thêm khách hàng mới]   │
│ Loại vé: [ComboBox▼]                               │
├──────────────────┬──────────────────────────────────┤
│ Train Selection  │                                  │
│ ┌──────────────┐ │      SEAT MAP                   │
│ │Chọn chuyến:  │ │                                  │
│ │[ComboBox ▼]  │ │  [A1] [A2]  ||  [B1] [B2]      │
│ └──────────────┘ │  [A3] [A4]  ||  [B3] [B4]      │
│                  │  [A5] [A6]  ||  [B5] [B6]      │
│ Carriage List    │  ...                            │
│ ┌──────────────┐ │                                  │
│ │Mã│Tên│Loại  │ │  Legend:                        │
│ │T1│A  │VIP   │ │  ■ Trống    ■ Đã đặt           │
│ │T2│B  │Thường│ │                                  │
│ └──────────────┘ │                                  │
└──────────────────┴──────────────────────────────────┘
```

## Code Changes Summary

### Modified File
- **File**: `src/main/java/com/trainstation/gui/PnlDatVe.java`
- **Lines Changed**: ~243 insertions, ~46 deletions

### New Fields
```java
private JComboBox<String> cboKhachHang;
private JComboBox<String> cboLoaiVe;
private LoaiVeDAO loaiVeDAO;
```

### New Methods
```java
private void taiDanhSachKhachHang()
private void taiDanhSachLoaiVe()
private void xacNhanDatVe()
private void hienThiFormThemKhachHang()
private JButton taoNutGhe(Ghe ghe)
```

### Modified Methods
```java
private void initComponents() // Added customer and ticket type selection
private void hienThiSoDoGhe(String maToa) // Changed to 2-2 layout
private void chonGhe(Ghe ghe) // Added validation and confirmation
private void datVe(KhachHang, LoaiVe) // Added loaiVe parameter
```

## Technical Implementation

### Seat Layout Algorithm
```java
// For N seats, calculate rows: ceil(N / 4)
int soHang = (int) Math.ceil(soGhe / 4.0);

// GridLayout with 5 columns:
// - Column 0-1: Left seats
// - Column 2: Aisle (gray panel)
// - Column 3-4: Right seats
pnlSoDoGhe.setLayout(new GridLayout(soHang, 5, 5, 5));

// Add seats in order:
for (int i = 0; i < soHang; i++) {
    addSeat(i*4 + 0); // Left A
    addSeat(i*4 + 1); // Left B
    addAisle();       // Middle corridor
    addSeat(i*4 + 2); // Right A
    addSeat(i*4 + 3); // Right B
}
```

### Customer Selection Logic
```java
// Format: "Name (Phone)"
String khachHangStr = cboKhachHang.getSelectedItem();
String sdt = khachHangStr.substring(
    khachHangStr.lastIndexOf("(") + 1, 
    khachHangStr.lastIndexOf(")")
);

// Find customer by phone number
for (KhachHang kh : khachHangDAO.getAll()) {
    if (sdt.equals(kh.getSoDienThoai())) {
        return kh;
    }
}
```

## Testing Checklist

- [ ] Customer ComboBox loads all customers from database
- [ ] Add Customer button opens dialog with all fields
- [ ] New customer is added to database and refreshes ComboBox
- [ ] Ticket Type ComboBox loads all ticket types
- [ ] Seat map displays in 2-2 layout with aisle
- [ ] Seat colors match status (green=available, red=booked)
- [ ] Seat tooltips show correct information
- [ ] Clicking seat validates customer and ticket type selection
- [ ] Confirmation dialog shows all booking details
- [ ] Booking creates ticket with correct customer and ticket type
- [ ] Seat status updates after booking
- [ ] Success message displays complete information

## Future Enhancements (Out of Scope)

1. Add customer search/filter in ComboBox
2. Add customer photo/avatar display
3. Support multi-seat selection for group bookings
4. Add seat preference selection (window/aisle)
5. Show real-time seat availability updates
6. Add price calculation based on ticket type and route
7. Support for different carriage layouts (sleeper cars, etc.)
