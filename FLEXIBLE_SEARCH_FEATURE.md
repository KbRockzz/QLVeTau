# Flexible Train Search Feature

## Overview
The booking interface (PnlDatVe) now supports flexible train search. Users can search for trains using any combination of search criteria, or leave all fields empty to view all available trains.

## Changes Made

### UI Changes (PnlDatVe.java)
- Modified the `timChuyenTau()` method to treat empty combobox selections ("") as null (no filter)
- When user leaves a search field empty or unselected, it will not be used as a filter criterion

### Supported Search Criteria
Users can search by any combination of:
1. **Ga đi** (Departure Station) - Optional
2. **Ga đến** (Arrival Station) - Optional  
3. **Ngày đi** (Departure Date) - Optional
4. **Giờ đi** (Departure Time) - Optional

## Usage Examples

### Example 1: Search by Departure Station Only
- Select "Sài Gòn" in Ga đi
- Leave Ga đến, Ngày đi, and Giờ đi empty
- Result: All trains departing from Sài Gòn

### Example 2: Search by Date Only
- Leave Ga đi and Ga đến empty
- Select a date in Ngày đi
- Leave Giờ đi empty
- Result: All trains departing on the selected date

### Example 3: Search by Route
- Select "Sài Gòn" in Ga đi
- Select "Hà Nội" in Ga đến
- Leave Ngày đi and Giờ đi empty
- Result: All trains from Sài Gòn to Hà Nội

### Example 4: View All Trains
- Leave all fields empty
- Result: Complete list of all trains in the system

### Example 5: Specific Date and Time
- Select departure and arrival stations
- Select a date
- Select a time
- Result: Trains matching all criteria

## Technical Implementation

### Code Changes
The key change was in the `timChuyenTau()` method:
```java
// Get search criteria, treating empty strings as null (no filter)
String gaDi = (String) cmbGaDi.getSelectedItem();
if (gaDi != null && gaDi.trim().isEmpty()) {
    gaDi = null;
}

String gaDen = (String) cmbGaDen.getSelectedItem();
if (gaDen != null && gaDen.trim().isEmpty()) {
    gaDen = null;
}
```

### DAO Support
The underlying `ChuyenTauDAO.timKiemChuyenTau()` method already supported null parameters by dynamically building SQL queries:
```sql
SELECT ... FROM ChuyenTau WHERE 1=1
  AND (gaDi = ? OR gaDi IS NULL)  -- Only added if gaDi is not null
  AND (gaDen = ? OR gaDen IS NULL)  -- Only added if gaDen is not null
  ...
```

## Benefits
1. **User Flexibility**: Users don't need to fill all search fields
2. **Easier Browsing**: Can view all trains without specific criteria
3. **Better UX**: More intuitive search behavior
4. **Backward Compatible**: Existing functionality remains unchanged

## UI Layout
The search interface layout and all other functionality remain unchanged:
- Customer search panel
- Train search panel with 4 search fields
- Results table showing matching trains
- Carriage and seat selection
- Booking and invoice generation

## Notes
- Empty combobox selection (first option) is treated as "no filter"
- If no trains match the criteria, an information message is displayed
- The "Tìm chuyến tàu" button behavior is preserved
