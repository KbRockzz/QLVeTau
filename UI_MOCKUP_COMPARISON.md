# PnlDatVe UI Mockup - Before and After

## BEFORE - Old Design

```
╔═══════════════════════════════════════════════════════════════════╗
║                        ĐẶT VÉ TÀU                                 ║
╠═══════════════════════════════════════════════════════════════════╣
║ Khách hàng: [Nguyễn Văn A (0123456789)    ▼]                    ║
║             [+ Thêm khách hàng mới]                               ║
║                                                                   ║
║ Loại vé: [Vé thường ▼]                                           ║
╠═══════════════════════════════════════════════════════════════════╣
║                                                                   ║
║ ┌──────────────────────┬────────────────────────────────────────┐║
║ │ Chọn chuyến tàu:     │                                        │║
║ │ [CT001 - Sài Gòn → Hà Nội (15/10/2024 08:00) ▼]              │║
║ │                      │                                        │║
║ │ ╔══════════════════╗ │  ╔═══════════════════════════════════╗│║
║ │ ║ Danh sách toa    ║ │  ║ Sơ đồ ghế                         ║│║
║ │ ║──────────────────║ │  ║                                   ║│║
║ │ ║ Mã  │Tên│Loại│SC ║ │  ║  [A1][A2]  |  [A3][A4]           ║│║
║ │ ║─────┼───┼────┼──║ │  ║  [B1][B2]  |  [B3][B4]           ║│║
║ │ ║ T1  │T1 │VIP │40║ │  ║  [C1][C2]  |  [C3][C4]           ║│║
║ │ ║ T2  │T2 │VIP │40║ │  ║  [D1][D2]  |  [D3][D4]           ║│║
║ │ ║ T3  │T3 │Thg │44║ │  ║                                   ║│║
║ │ ╚══════════════════╝ │  ╚═══════════════════════════════════╝│║
║ └──────────────────────┴────────────────────────────────────────┘║
║                                                                   ║
║ ■ Trống         ■ Đã đặt                                         ║
╚═══════════════════════════════════════════════════════════════════╝
```

**Problems with OLD design:**
- Customer dropdown shows ALL customers (slow with many customers)
- Must scroll through long list to find customer
- Train dropdown shows ALL trains (inefficient)
- No filtering by route, date, or time
- Hard to compare trains side by side


## AFTER - New Modern Design

```
╔═══════════════════════════════════════════════════════════════════╗
║                        ĐẶT VÉ TÀU                                 ║
╠═══════════════════════════════════════════════════════════════════╣
║ ┌─ Thông tin khách hàng ─────────────────────────────────────┐   ║
║ │ Số điện thoại: [0123456789___] [Tìm khách hàng]            │   ║
║ │ ✓ Khách hàng: Nguyễn Văn A (Mã: KH001)                     │   ║
║ │                                    Loại vé: [Vé thường ▼]  │   ║
║ └─────────────────────────────────────────────────────────────┘   ║
║                                                                   ║
║ ┌─ Tìm chuyến tàu ───────────────────────────────────────────┐   ║
║ │ Ga đi: [Sài Gòn     ▼]  Ga đến: [Hà Nội      ▼]           │   ║
║ │ Ngày đi: [15/10/2024 📅]  Giờ đi (từ): [08:00 ⏰]         │   ║
║ │                                        [Tìm chuyến tàu]    │   ║
║ └─────────────────────────────────────────────────────────────┘   ║
╠═══════════════════════════════════════════════════════════════════╣
║ ┌─ Danh sách chuyến tàu ─────────────────────────────────────┐   ║
║ │╔════╦════════╦═══════╦════════╦══════════╦═══════╦═══════╗│   ║
║ │║ Mã ║Tên tàu ║ Ga đi ║ Ga đến ║ Ngày đi  ║Giờ đi ║Giờ đến║│   ║
║ │╠════╬════════╬═══════╬════════╬══════════╬═══════╬═══════╣│   ║
║ │║CT01║ TAU001 ║Sài Gòn║Hà Nội  ║15/10/2024║ 08:00 ║ 18:30 ║│   ║
║ │║CT02║ TAU002 ║Sài Gòn║Hà Nội  ║15/10/2024║ 14:00 ║ 00:30 ║│   ║
║ │║CT05║ TAU001 ║Sài Gòn║Hà Nội  ║15/10/2024║ 20:00 ║ 06:30 ║│   ║
║ │╚════╩════════╩═══════╩════════╩══════════╩═══════╩═══════╝│   ║
║ └─────────────────────────────────────────────────────────────┘   ║
╠═══════════════════════════════════════════════════════════════════╣
║ ┌─────────────────┬─────────────────────────────────────────────┐║
║ │ Danh sách toa   │  Sơ đồ ghế (Bố trí toa tàu)                │║
║ │─────────────────│─────────────────────────────────────────────│║
║ │╔═══╦═══╦════╦══╗│   [A1][A2]      |      [A3][A4]            │║
║ │║Mã ║Tên║Loại║SC║│   [B1][B2]      |      [B3][B4]            │║
║ │╠═══╬═══╬════╬══╣│   [C1][C2]      |      [C3][C4]            │║
║ │║T1 ║T1 │VIP │40║│   [D1][D2]      |      [D3][D4]            │║
║ │║T2 ║T2 │VIP │40║│   [E1][E2]      |      [E3][E4]            │║
║ │║T3 ║T3 │Thg │44║│   [F1][F2]      |      [F3][F4]            │║
║ │╚═══╩═══╩════╩══╝│                                             │║
║ └─────────────────┴─────────────────────────────────────────────┘║
║                                                                   ║
║ ■ Trống         ■ Đã đặt                                         ║
╚═══════════════════════════════════════════════════════════════════╝
```

**Benefits of NEW design:**
✓ Quick customer search by phone (no scrolling!)
✓ Filter trains by route and date/time
✓ See all matching trains in a table
✓ Compare multiple trains at once
✓ Inline customer creation if not found
✓ Scales better with large datasets


## User Flow Comparison

### OLD FLOW - Customer Selection
1. Click customer dropdown
2. Scroll through ENTIRE customer list (could be hundreds)
3. Find customer by name
4. Click to select

**Time: ~15-30 seconds for large customer base**

### NEW FLOW - Customer Search  
1. Type phone number (10 digits)
2. Click "Tìm khách hàng"
3. Customer info displayed immediately

**Time: ~3-5 seconds**

---

### OLD FLOW - Train Selection
1. Click train dropdown
2. Scroll through ALL trains
3. Can't easily compare trains
4. Can't filter by criteria
5. Select one train

**Time: ~20-40 seconds, prone to errors**

### NEW FLOW - Train Search
1. Select departure/arrival stations (optional)
2. Select date and time (optional)
3. Click "Tìm chuyến tàu"
4. See filtered results in table
5. Compare and select best train

**Time: ~10-15 seconds, more accurate**

---

## Interaction Examples

### Example 1: Booking for Existing Customer

**OLD WAY:**
```
User: Opens booking panel
      Clicks customer dropdown
      Scrolls... scrolls... scrolls...
      Finds "Nguyễn Văn A"
      Clicks train dropdown
      Scrolls through all trains
      Picks one randomly
      Continues with seat selection
```

**NEW WAY:**
```
User: Opens booking panel
      Types "0123456789" in phone field
      Clicks "Tìm khách hàng"
      ✓ Customer found and displayed
      Selects "Sài Gòn" → "Hà Nội"
      Picks date: 15/10/2024
      Clicks "Tìm chuyến tàu"
      Sees 3 matching trains
      Compares departure times
      Clicks on 08:00 train
      Continues with seat selection
```

### Example 2: Booking for New Customer

**OLD WAY:**
```
User: Clicks "+ Thêm khách hàng mới"
      Fills form
      Submits
      Goes back to booking panel
      Now customer is in dropdown
      Scrolls to find newly added customer
      Continues booking...
```

**NEW WAY:**
```
User: Types "9876543210" in phone field
      Clicks "Tìm khách hàng"
      System: "Customer not found. Create new?"
      User: Clicks "Yes"
      Form opens with phone pre-filled
      Fills remaining info
      Submits
      ✓ Customer automatically selected
      Continues directly with train search...
```

### Example 3: Finding Night Train

**OLD WAY:**
```
User: Clicks train dropdown
      Scrolls through ALL trains
      Manually reads each time
      Tries to remember which has evening departure
      Hard to compare options
      Picks one, hopes it's right
```

**NEW WAY:**
```
User: Selects route
      Picks date
      Sets time: 18:00 (looking for evening trains)
      Clicks search
      Table shows only trains after 18:00:
      - 20:00 departure
      - 22:00 departure
      Easy to compare and choose!
```

---

## Key UI Improvements

### 1. Customer Search Section
- **Phone number input** - Direct, fast entry
- **Search button** - Clear action
- **Visual feedback** - Green text when found
- **Inline creation** - No context switch needed

### 2. Train Search Section
- **Dropdown filters** - Easy station selection
- **Date picker** - Visual calendar
- **Time spinner** - Easy time selection
- **Clear criteria** - User knows what they're searching for

### 3. Results Table
- **Sortable columns** - Compare trains easily
- **Formatted dates/times** - dd/MM/yyyy, HH:mm
- **Multiple results** - See all options at once
- **Click to select** - Simple interaction

### 4. Preserved Elements
- **Carriage table** - Still shows toa tàu
- **Seat map** - Still shows visual layout
- **Color coding** - Green/red for availability
- **Booking flow** - Same confirmation and ticketing

---

## Mobile/Responsive Considerations

While this is a desktop application, the new design is more adaptable:

### OLD Design Issues:
- Long dropdowns difficult on small screens
- Scrolling performance poor with many items
- Hard to see all info at once

### NEW Design Benefits:
- Search fields are compact
- Table can be made scrollable
- Filter criteria stay visible
- Results are paginated naturally

---

## Accessibility Improvements

### Keyboard Navigation
- Tab order: Phone → Search → Stations → Date → Time → Search
- Enter key triggers search buttons
- Arrow keys navigate table rows

### Screen Reader Support
- Labels clearly describe purpose
- Table has proper headers
- Status messages announced
- Error messages clear and specific

### Visual Clarity
- Larger input fields
- Clear button labels
- Visual feedback on selection
- Consistent color scheme

---

## Summary of Changes

| Aspect | Before | After |
|--------|--------|-------|
| Customer selection | Dropdown (all) | Phone search |
| Train selection | Dropdown (all) | Filtered search |
| Results display | Single selected | Table of results |
| Data entry | Separate form | Inline creation |
| Performance | O(n) scroll | O(1) search |
| Usability | Click-heavy | Type-and-search |
| Comparison | Difficult | Easy (table) |
| Scalability | Poor (100s slow) | Good (search scales) |
| User satisfaction | Frustrating | Efficient |

The modernized interface provides a significantly better user experience while maintaining backward compatibility with all existing functionality.
