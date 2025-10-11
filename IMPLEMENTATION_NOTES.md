# Implementation Notes - Navigation Bar and Role-Based Access Control

## Overview
This document describes the changes made to implement a navigation bar system, role-based access control, and enhanced search functionality.

## Key Changes

### 1. Database Connection (MySQL/ConnectSql.java)
- Created `MySQL/ConnectSql.java` for SQL Server connectivity
- Singleton pattern implementation for connection management
- Connection string configured for SQL Server with encryption
- Methods: `getInstance()`, `getConnection()`, `closeConnection()`, `testConnection()`

### 2. Employee Type System (maLoai)
- Added `maLoai` field to `Employee` model
- Three employee types:
  - **LNV01**: Regular employee
  - **LNV02**: Senior employee  
  - **LNV03**: Manager (has full access to all features)

### 3. Account Model Updates
- Added `maLoai` field to link employee type
- Added `isManager()` method to check if account is LNV03
- Updated AccountDAO to automatically populate maLoai from employee data

### 4. Navigation Bar System
- Created `NavigationBar.java` - reusable navigation component
- Features:
  - Home, Tickets, Customers, Trains buttons
  - Employee dropdown menu (only for managers):
    - Manage Employees
    - Statistics
  - Account button (only for managers)
  - Logout button with confirmation
  - Employee name display on the right

### 5. Home Dashboard
- Created `HomePanel.java` with welcome screen
- Quick access buttons to all features
- Role-based button visibility

### 6. MainFrame Redesign
- Changed from JTabbedPane to CardLayout
- Navigation bar at the top of all pages
- Content area switches between different panels
- Role-based access control in `navigateToPage()` method

### 7. Enhanced Customer Management
- Added phone number search functionality
- Search panel at the top with:
  - Phone number input field
  - Search button - filters table to show matching customers
  - Refresh button - returns to showing all customers
- Search supports partial phone number matching

### 8. Employee Management Updates
- Added maLoai field to employee form
- ComboBox with LNV01, LNV02, LNV03 options
- Updated table to display employee type
- All CRUD operations updated to handle maLoai

### 9. Sample Data Updates
- Admin employee set as LNV03 (Manager)
- Sample employees assigned LNV01 and LNV02 types
- Sample data includes maLoai for proper testing

## Access Control Rules

### All Users Can Access:
- Home page
- Ticket booking
- Customer management
- Train schedule management

### Managers Only (LNV03):
- Employee management
- Account management
- Statistics and reports

### Confirmation Dialogs
All destructive operations require user confirmation:
- ✅ Logout
- ✅ Delete customer
- ✅ Delete employee
- ✅ Delete train
- ✅ Delete account
- ✅ Refund ticket
- ✅ Cancel ticket

## User Interface Flow

1. **Login** → User credentials validated
2. **Home Dashboard** → Welcome screen with quick access
3. **Navigation Bar** → Available on all pages for easy navigation
4. **Role Check** → Manager-only pages protected by access control
5. **Confirmation** → All delete/logout operations require confirmation

## Technical Implementation

### Design Patterns Used:
- **Singleton**: DAO classes, ConnectSql
- **Factory**: Panel creation in MainFrame
- **Observer**: Event listeners for navigation
- **MVC**: Clear separation of concerns

### Key Classes:
- `NavigationBar`: Top navigation component
- `HomePanel`: Dashboard/home page
- `MainFrame`: Main window with CardLayout
- `ConnectSql`: SQL Server connection manager

## Testing

The application compiles successfully with Maven:
```bash
mvn clean compile
mvn clean package -DskipTests
```

All 26 Java source files compile without errors.

## Future Enhancements

Potential improvements for future releases:
1. Connect to actual SQL Server database
2. Add more search filters for other entities
3. Implement password change functionality
4. Add user activity logging
5. Export reports to PDF/Excel
6. Add data validation rules from database
7. Implement backup/restore functionality

## Migration Notes

For existing deployments:
1. All employees must be assigned a maLoai value
2. Default admin account is set to LNV03
3. Existing accounts will have maLoai populated on first authentication
4. No data migration scripts needed for in-memory implementation

## Conclusion

The implementation successfully adds:
- ✅ SQL Server connection infrastructure
- ✅ Navigation bar on all pages
- ✅ Role-based access control (LNV03 = Manager)
- ✅ Enhanced customer search by phone
- ✅ Confirmation dialogs for all destructive operations
- ✅ Modern, user-friendly interface
- ✅ Employee type management system
