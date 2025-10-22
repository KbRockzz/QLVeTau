# PnlDatVe Modernization Guide

## Overview
This document describes the modernization of the ticket booking panel (PnlDatVe) to improve user experience and make the booking process more intuitive.

## Changes Implemented

### 1. Database Layer (DAO)

#### KhachHangDAO - New Method
Added `timTheoSoDienThoai(String soDienThoai)` method:
- Searches for a customer by phone number
- Returns `KhachHang` object if found, `null` otherwise
- Used to quickly find existing customers during booking

#### ChuyenTauDAO - New Methods
Added two new methods:

1. `timKiemChuyenTau(String gaDi, String gaDen, LocalDate ngayDi, LocalTime gioDi)`:
   - Searches for train trips based on route and time criteria
   - All parameters are optional (can be null)
   - Returns filtered list of `ChuyenTau` objects
   - Supports flexible searching (e.g., only by departure station, or by date and time)

2. `getDistinctStations()`:
   - Returns list of unique station names from all train trips
   - Used to populate departure and arrival station dropdowns
   - Combines both departure and arrival stations from ChuyenTau table

### 2. User Interface (PnlDatVe)

#### Previous Design
- Customer selection via combobox (showing all customers)
- Train selection via combobox (showing all trains)
- Direct selection without filtering

#### New Design

##### A. Customer Search Section
**Components:**
- `txtSoDienThoai`: Text field for entering phone number
- `btnTimKhachHang`: Button to search for customer
- `lblThongTinKhachHang`: Label showing selected customer info
- `khachHangDuocChon`: Stores the selected customer object

**Workflow:**
1. User enters phone number
2. Click "Tìm khách hàng" button
3. System searches database:
   - If found: Display customer name and ID in green
   - If not found: Show dialog asking to create new customer
4. If user chooses to create:
   - Opens form pre-filled with entered phone number
   - After creation, automatically selects the new customer

##### B. Train Search Section
**Components:**
- `cmbGaDi`: Dropdown for departure station
- `cmbGaDen`: Dropdown for arrival station
- `dateNgayDi`: Date picker (JDateChooser) for departure date
- `spnGioDi`: Time spinner for departure time
- `btnTimChuyenTau`: Button to search trains

**Workflow:**
1. User selects/enters search criteria (all optional):
   - Departure station
   - Arrival station
   - Departure date
   - Minimum departure time
2. Click "Tìm chuyến tàu" button
3. System searches and displays results in table

##### C. Train Results Table
**Components:**
- `tblChuyenTau`: JTable displaying search results
- `modelBangChuyenTau`: Table model for train results

**Columns:**
- Mã chuyến (Trip ID)
- Tên tàu (Train name/ID)
- Ga đi (Departure station)
- Ga đến (Arrival station)
- Ngày đi (Departure date - dd/MM/yyyy format)
- Giờ đi (Departure time - HH:mm format)
- Giờ đến (Arrival time - HH:mm format)

**Workflow:**
1. Click on a row to select a train
2. System loads carriages (toa tàu) for selected train
3. Carriage selection and seat selection work as before

##### D. Preserved Functionality
The following sections remain unchanged:
- Carriage (toa tàu) table display
- Seat map visualization with train layout
- Seat selection buttons (green for available, red for booked)
- Ticket type selection
- Booking confirmation dialog
- Invoice creation and management

### 3. Layout Structure

```
┌─────────────────────────────────────────────────────────────┐
│                      ĐẶT VÉ TÀU                             │
├─────────────────────────────────────────────────────────────┤
│ Thông tin khách hàng:                                       │
│ Số điện thoại: [___________] [Tìm khách hàng]              │
│ (Customer info displayed here)    Loại vé: [Dropdown]      │
├─────────────────────────────────────────────────────────────┤
│ Tìm chuyến tàu:                                             │
│ Ga đi: [Dropdown] Ga đến: [Dropdown] Ngày đi: [DatePicker] │
│ Giờ đi (từ): [Time Spinner] [Tìm chuyến tàu]              │
├─────────────────────────────────────────────────────────────┤
│ Danh sách chuyến tàu (Table - 150px height)                │
│ ┌─────────────────────────────────────────────────────┐   │
│ │ Mã | Tên tàu | Ga đi | Ga đến | Ngày | Giờ đi/đến  │   │
│ └─────────────────────────────────────────────────────┘   │
├─────────────────────────────────────────────────────────────┤
│ ┌──────────────┐ ┌───────────────────────────────────────┐ │
│ │ Danh sách    │ │ Sơ đồ ghế (Bố trí toa tàu)            │ │
│ │ toa tàu      │ │                                       │ │
│ │ (Table)      │ │ [Seat Layout Grid]                    │ │
│ │              │ │                                       │ │
│ └──────────────┘ └───────────────────────────────────────┘ │
├─────────────────────────────────────────────────────────────┤
│ ■ Trống        ■ Đã đặt                                    │
└─────────────────────────────────────────────────────────────┘
```

## Testing Guide

### Manual Testing Steps

#### Test 1: Customer Search - Existing Customer
1. Launch the application
2. Navigate to "Đặt vé" panel
3. Enter a valid phone number (e.g., "0123456789")
4. Click "Tìm khách hàng"
5. **Expected Result:** Customer info displayed in green label

#### Test 2: Customer Search - New Customer
1. Enter a non-existent phone number (e.g., "9999999999")
2. Click "Tìm khách hàng"
3. **Expected Result:** Dialog asking to create new customer
4. Click "Yes"
5. Fill in customer details (Mã KH, Họ tên required)
6. Click "Thêm"
7. **Expected Result:** 
   - Success message
   - Customer info displayed in green label
   - Phone number pre-filled in form

#### Test 3: Train Search - All Criteria
1. Select "Ga đi" (e.g., "Sài Gòn")
2. Select "Ga đến" (e.g., "Hà Nội")
3. Select departure date (future date)
4. Set departure time
5. Click "Tìm chuyến tàu"
6. **Expected Result:** Table populated with matching trains

#### Test 4: Train Search - Partial Criteria
1. Select only "Ga đi"
2. Leave other fields empty
3. Click "Tìm chuyến tàu"
4. **Expected Result:** All trains from that departure station

#### Test 5: Train Selection and Seat Booking
1. Search for trains (as in Test 3)
2. Click on a train row in the table
3. **Expected Result:** Carriage table populated
4. Click on a carriage
5. **Expected Result:** Seat map displayed
6. Select ticket type
7. Click on an available (green) seat
8. **Expected Result:** Confirmation dialog
9. Confirm booking
10. **Expected Result:** 
    - Success message with ticket and invoice details
    - Seat turns red (booked)

#### Test 6: Complete Booking Flow
1. Search customer (Test 1 or 2)
2. Search trains (Test 3)
3. Select train from table
4. Select carriage
5. Choose ticket type
6. Select seat
7. Confirm booking
8. **Expected Result:** Full booking completed successfully

### Edge Cases to Test

#### Edge Case 1: Empty Search
- Click "Tìm chuyến tàu" without any criteria
- **Expected:** All trains displayed

#### Edge Case 2: No Results
- Search with criteria that don't match any trains
- **Expected:** "Không tìm thấy chuyến tàu phù hợp!" message

#### Edge Case 3: Missing Customer
- Try to book without searching for customer first
- **Expected:** Error message "Vui lòng tìm và chọn khách hàng!"

#### Edge Case 4: Missing Ticket Type
- Search customer, select train, carriage, and seat
- Don't select ticket type
- **Expected:** Error message "Vui lòng chọn loại vé!"

## Benefits of New Design

### 1. Performance
- **Before:** Loads ALL customers and trains into memory/combobox
- **After:** Loads only search results, reduces memory usage

### 2. Usability
- **Before:** Scroll through long combobox list to find customer/train
- **After:** Direct search by phone number and route/time filters

### 3. Scalability
- **Before:** Performance degrades with more customers/trains
- **After:** Search-based approach scales better

### 4. User Experience
- **Before:** Multiple clicks to scroll and find
- **After:** Quick search with familiar phone number

### 5. Data Entry
- **Before:** Must create customer first, then go back to booking
- **After:** Inline customer creation during booking flow

## Code Quality

### Maintained Principles
- Single Responsibility: Each method has one clear purpose
- Open/Closed: New features added without modifying existing booking logic
- Minimal Changes: Preserved all working seat selection and booking logic
- Backward Compatibility: Database schema unchanged

### Key Design Decisions

1. **Optional Search Parameters:**
   - All search criteria are optional
   - Allows flexible querying (e.g., all trains from a station)
   - SQL query built dynamically based on provided parameters

2. **Pre-fill Phone Number:**
   - When creating new customer, phone number auto-filled
   - Reduces data entry errors
   - Improves user flow

3. **Customer Object Storage:**
   - `khachHangDuocChon` stores selected customer
   - Direct reference instead of string parsing
   - More reliable and type-safe

4. **Date/Time Handling:**
   - Uses JDateChooser for calendar picker
   - JSpinner for time selection
   - Converts between java.util.Date and java.time types safely

## Troubleshooting

### Issue: Stations not appearing in dropdowns
**Solution:** Check that ChuyenTau table has data with gaDi and gaDen values

### Issue: Date picker not showing
**Solution:** Verify JCalendar dependency (com.toedter:jcalendar:1.4) in pom.xml

### Issue: Search returns no results
**Solution:** 
1. Check database connection
2. Verify search criteria match data in ChuyenTau table
3. Check date/time format conversion

### Issue: Customer not found
**Solution:**
1. Verify phone number matches exactly (no spaces)
2. Check KhachHang table has soDienThoai column populated
3. Confirm database connection is active

## Future Enhancements

Potential improvements for future versions:

1. **Auto-complete for stations:** Type-ahead suggestions as user types
2. **Date range search:** Find trains within a date range
3. **Price filter:** Search by price range
4. **Seat availability indicator:** Show available seats count in train table
5. **Recent searches:** Remember and suggest recent search criteria
6. **Phone number validation:** Format checking for phone numbers
7. **Duplicate phone check:** Warn when creating customer with existing phone
8. **Export search results:** Save train search results to PDF/Excel
9. **Sort table columns:** Click column headers to sort results
10. **Double-click to select:** Double-click train row to select instead of single click

## Conclusion

The modernized PnlDatVe panel provides a more intuitive and efficient booking experience while maintaining all existing functionality. The search-based approach improves scalability and user satisfaction, making it easier for staff to quickly book tickets for customers.
