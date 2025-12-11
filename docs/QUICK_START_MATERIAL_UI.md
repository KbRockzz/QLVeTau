# Quick Start Guide - Material Professional Light UI

## Overview
The Train Ticket Management System now features a modern Material Professional Light theme powered by FlatLaf.

## What's New?

### 🎨 Modern Material Design
- Clean, flat design without shadows
- Professional color palette based on Material Design
- Consistent 6px rounded corners
- Roboto font throughout (with smart fallback)

### 📱 Improved Navigation
- Top horizontal navigation bar
- Text-only buttons with Material colors
- Smooth hover effects
- Same navigation order as before

### 🔐 Simplified Login
- Clean card-based layout
- Material styled input fields
- Professional appearance
- Same login functionality

## How to Use

### Running the Application

```bash
# Build the project
mvn clean package

# Run the application
java -jar target/QLVeTau-1.0.0.jar

# Or run directly
mvn exec:java -Dexec.mainClass="com.trainstation.MainApplication"
```

### Login Credentials
Use your existing credentials - nothing has changed in the authentication system.

### Navigation
Navigate using the top menu bar:
- **Trang chủ** - Home
- **Vé** - Tickets (dropdown)
  - Đặt vé (Book Ticket)
  - Hoàn vé (Refund Ticket)
  - Xuất hóa đơn (Invoice)
- **Khách hàng** - Customer
- **Tàu** - Train
- **Chuyến Tàu** - Train Schedule
- **Nhân viên** - Employee (Manager only, dropdown)
  - Quản lý nhân viên (Manage Employees)
  - Thống kê (Statistics)
  - Dữ liệu đã xóa (Deleted Data)
- **Tài khoản** - Account (Manager only)
- **Đăng xuất** - Logout

## Material Color Palette

### Primary Colors
- **Primary Blue**: #1976D2 - Navigation, buttons, links
- **Primary Dark**: #0D47A1 - Hover states, selected items
- **Light Blue**: #2196F3 - Table headers, accents
- **Very Light Blue**: #E3F2FD - Selection backgrounds

### Neutral Colors
- **Background**: #F5F5F5 - Main background
- **Surface**: #FFFFFF - Cards, panels, inputs
- **Text Primary**: #212121 - Main text
- **Text Secondary**: #757575 - Secondary text

### Semantic Colors
- **Success Green**: #4CAF50 - Available seats, success messages
- **Error Red**: #F44336 - Booked seats, error messages
- **Warning Orange**: #FF9800 - Held seats, warnings
- **Info Blue**: #2196F3 - Information messages

## Font System

### Font Priority
1. **Roboto** - Primary choice
2. **Segoe UI** - Fallback for Windows
3. **SansSerif** - System default fallback

### Font Sizes
- **Large Titles**: 36px Bold
- **Titles**: 24px Bold
- **Headings**: 20px Medium
- **Body Text**: 14px Regular
- **Small Text**: 12px Regular

## Component Styling

### Buttons
- Height: 36-40px
- Border Radius: 6px
- Background: Primary Blue (#1976D2)
- Hover: Primary Dark (#0D47A1)

### Text Fields
- Height: 36px
- Border Radius: 6px
- Border: 1px solid #BDBDBD
- Focus Border: 2px solid #1976D2

### Tables
- Header: Light Blue background (#2196F3), white text
- Row Height: 28px
- Selection: Very Light Blue background (#E3F2FD)
- Border: Light gray (#E0E0E0) between rows

### Navigation Bar
- Height: 56px
- Background: Primary Blue (#1976D2)
- Button Height: 40px
- Hover: Primary Dark (#0D47A1)

## For Developers

### Adding New Components

```java
// Import MaterialInitializer
import com.trainstation.config.MaterialInitializer;

// Create font with proper fallback
Font titleFont = MaterialInitializer.createFont(Font.BOLD, 24);
Font bodyFont = MaterialInitializer.createFont(Font.PLAIN, 14);

// Use Material colors
Color primaryBlue = new Color(25, 118, 210);    // #1976D2
Color background = new Color(245, 245, 245);     // #F5F5F5
Color textPrimary = new Color(33, 33, 33);       // #212121
```

### Best Practices
1. ✅ Use `MaterialInitializer.createFont()` for all font creation
2. ✅ Follow color palette defined in UI-Palette.md
3. ✅ Use 6px border radius for custom components
4. ✅ Avoid adding shadows or 3D effects
5. ✅ Test with different font availability scenarios

### Theme Customization
Edit `src/main/resources/theme/ProfessionalLight.json` to customize:
- Colors (primary, accent, background)
- Border radius (arc values)
- Component spacing
- Font sizes

## Troubleshooting

### Theme Not Loading
```
Warning: ProfessionalLight.json theme not found
```
**Solution**: Ensure `src/main/resources/theme/ProfessionalLight.json` exists

### Font Issues
```
Font displayed incorrectly
```
**Solution**: The fallback system will use Segoe UI or SansSerif if Roboto is unavailable

### Build Issues
```bash
# Clean rebuild
mvn clean install -DskipTests

# Clear Maven cache
rm -rf ~/.m2/repository/com/formdev/
mvn clean install -U
```

## Key Files

| File | Purpose |
|------|---------|
| `MaterialInitializer.java` | Theme and font initialization |
| `ProfessionalLight.json` | Theme configuration |
| `UI-Palette.md` | Complete style guide |
| `MATERIAL_UI_REDESIGN_SUMMARY.md` | Implementation details |

## What Hasn't Changed

✅ All existing functionality works exactly the same:
- Database operations
- Business logic
- Report generation
- Data validation
- User permissions
- Navigation flow
- Form submissions

Only the visual appearance has been improved!

## Need Help?

Refer to:
1. `docs/UI-Palette.md` - Complete style guide
2. `MATERIAL_UI_REDESIGN_SUMMARY.md` - Implementation details
3. FlatLaf Documentation - https://www.formdev.com/flatlaf/

## Screenshots

*Note: Run the application to see the new Material UI in action!*

Key screens to review:
- ✅ Login screen - Simplified card design
- ✅ Home screen - Material gradient
- ✅ Navigation bar - Top, flat design
- ✅ Tables - Material styled headers
- ✅ Forms - Material input fields
- ✅ Buttons - Material colors and hover effects

---
**Version**: 1.0.0  
**Last Updated**: December 2024  
**Status**: Production Ready ✅
