# Dialog Size and Current Seat Color Update

## Changes Made

### 1. Increased Dialog Size
**Before**: 900x600 pixels
**After**: 1100x700 pixels

This provides more room for the seat map and ticket information to display comfortably.

### 2. Changed Current Seat Color
**Before**: Orange/yellow color (Color.ORANGE)
**After**: Red color (Color.RED) - same as occupied seats

The current seat now displays in red (like occupied seats) to make it clear it cannot be selected. The user will choose a different seat from the available green seats.

### 3. Updated Legend
**Before**: 
- 🟢 Trống (Available)
- 🔴 Đã đặt (Occupied)
- 🟡 Hiện tại (Current) ← Removed
- 🔵 Đang chọn (Selected)

**After**:
- 🟢 Trống (Available)
- 🔴 Đã đặt (Occupied)
- 🔵 Đang chọn (Selected)

The "Hiện tại" legend item was removed since the current seat now uses the same color as occupied seats.

## Visual Changes

### Dialog Size
The dialog is now 200 pixels wider (900→1100) and 100 pixels taller (600→700), providing better spacing for:
- Left panel: Ticket information
- Right panel: Seat map with 2-2 layout and aisle
- Bottom: Reason input field and buttons

### Current Seat Appearance
```
Before:
[🟢][🟢] ║Aisle║ [🟡][🟢]  ← Current seat in orange
[🟢][🟢] ║Aisle║ [🟢][🔴]

After:
[🟢][🟢] ║Aisle║ [🔴][🟢]  ← Current seat in red (same as occupied)
[🟢][🟢] ║Aisle║ [🟢][🔴]
```

The current seat is disabled (cannot be clicked) and has a tooltip "Ghế XXX - Ghế hiện tại" to inform the user.

## Code Changes

### DlgDoiVe.java

**Line 48**: Dialog size
```java
setSize(1100, 700);  // Was: setSize(900, 600);
```

**Line 122-125**: Legend (removed orange current seat)
```java
legendPanel.add(createLegendItem("Trống", new Color(34, 139, 34)));
legendPanel.add(createLegendItem("Đã đặt", Color.RED));
legendPanel.add(createLegendItem("Đang chọn", Color.BLUE));
// Removed: legendPanel.add(createLegendItem("Hiện tại", Color.ORANGE));
```

**Line 278-283**: Current seat color
```java
if (ghe.getMaGhe().equals(veGoc.getMaSoGhe())) {
    // Ghế hiện tại - màu đỏ giống ghế đã đặt
    btnGhe.setBackground(Color.RED);  // Was: Color.ORANGE
    btnGhe.setForeground(Color.BLACK);
    btnGhe.setEnabled(false);
    btnGhe.setToolTipText("Ghế " + ghe.getMaGhe() + " - Ghế hiện tại");
}
```

## Benefits

✅ **Better spacing**: Larger dialog provides more comfortable viewing
✅ **Clearer UI**: Current seat in red makes it obvious it's not selectable
✅ **Simpler legend**: Removed unnecessary "Hiện tại" color since red serves dual purpose
✅ **Consistent**: Disabled seats (occupied + current) all use same red color
