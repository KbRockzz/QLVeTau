# Seat Map Comparison - Before and After

## Before Update (Simple Grid)
```
┌────────────────────────────────┐
│  [G01] [G02] [G03] [G04]       │
│  [G05] [G06] [G07] [G08]       │
│  [G09] [G10] [G11] [G12]       │
│  [G13] [G14] [G15] [G16]       │
└────────────────────────────────┘
```
- 4 columns
- No aisle
- Simple GridLayout

## After Update (Matches PnlDatVe)
```
┌────────────────────────────────────────┐
│  [G01] [G02]  ║ AISLE ║  [G03] [G04]  │
│  [G05] [G06]  ║ AISLE ║  [G07] [G08]  │
│  [G09] [G10]  ║ AISLE ║  [G11] [G12]  │
│  [G13] [G14]  ║ AISLE ║  [G15] [G16]  │
└────────────────────────────────────────┘
```
- 5 columns (2 + aisle + 2)
- Gray aisle panel in middle
- Matches train car layout
- Same as booking interface (PnlDatVe)

## Visual Features

### Seat Button Styling
- **Border**: Black border (2px) around each seat
- **Font**: Arial, Bold, 12pt
- **Size**: 80x40 pixels

### Aisle Panel
- **Color**: Gray (200, 200, 200)
- **Size**: 30x40 pixels
- **Purpose**: Visual separator, matches train layout

### Color Legend
| Color | Status | Description |
|-------|--------|-------------|
| 🟢 Green (34, 139, 34) | Available | Can select |
| 🔴 Red | Occupied | Cannot select |
| 🟡 Orange | Current | User's current seat |
| 🔵 Blue | Selected | User's new choice |

## Code Structure

### Layout Calculation
```java
int soGhe = danhSachGhe.size();
int soHang = (int) Math.ceil(soGhe / 4.0);  // 4 seats per row
pnlSeatMap.setLayout(new GridLayout(soHang, 5, 5, 5));  // 5 columns
```

### Row Structure
```java
for (int i = 0; i < soHang; i++) {
    // Left 2 seats
    for (int j = 0; j < 2; j++) {
        int index = i * 4 + j;
        if (index < soGhe) {
            pnlSeatMap.add(taoNutGhe(danhSachGhe.get(index)));
        }
    }
    
    // Aisle
    JPanel pnlLoiDi = new JPanel();
    pnlLoiDi.setBackground(new Color(200, 200, 200));
    pnlSeatMap.add(pnlLoiDi);
    
    // Right 2 seats
    for (int j = 2; j < 4; j++) {
        int index = i * 4 + j;
        if (index < soGhe) {
            pnlSeatMap.add(taoNutGhe(danhSachGhe.get(index)));
        }
    }
}
```

## Benefits

✅ **Consistency**: Matches booking interface exactly
✅ **User Familiarity**: Users see same layout they used for booking
✅ **Visual Clarity**: Aisle helps users understand train car layout
✅ **Professional**: Looks more polished and realistic
✅ **Accessibility**: Better seat identification with clear separation
