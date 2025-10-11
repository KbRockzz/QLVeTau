# Application Flow - QLVeTau

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                      MainApplication                         │
│                    (Entry Point)                             │
└───────────────────────┬─────────────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────────────┐
│                      LoginFrame                              │
│               (Authentication Layer)                         │
│  - Username/Password validation                             │
│  - Account retrieval with maLoai                            │
└───────────────────────┬─────────────────────────────────────┘
                        │ (Successful Login)
                        ▼
┌─────────────────────────────────────────────────────────────┐
│                      MainFrame                               │
│                  (Main Container)                            │
│  ┌───────────────────────────────────────────────────────┐  │
│  │              NavigationBar                            │  │
│  │  - Home | Vé | Khách hàng | Chuyến tàu              │  │
│  │  - Nhân viên ▾ (if LNV03)                           │  │
│  │  - Tài khoản (if LNV03)                             │  │
│  │  - Đăng xuất | Tên nhân viên                        │  │
│  └───────────────────────────────────────────────────────┘  │
│  ┌───────────────────────────────────────────────────────┐  │
│  │              Content Panel (CardLayout)               │  │
│  │  ┌─────────────────────────────────────────────────┐ │  │
│  │  │         Current Page Panel                      │ │  │
│  │  │  - HomePanel                                    │ │  │
│  │  │  - TicketBookingPanel                           │ │  │
│  │  │  - CustomerPanel (with search)                  │ │  │
│  │  │  - TrainPanel                                   │ │  │
│  │  │  - EmployeePanel (LNV03 only)                   │ │  │
│  │  │  - AccountPanel (LNV03 only)                    │ │  │
│  │  │  - StatisticsPanel (LNV03 only)                 │ │  │
│  │  └─────────────────────────────────────────────────┘ │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

## User Journey

### 1. Login Phase
```
User enters credentials
      │
      ▼
AccountDAO.authenticate(username, password)
      │
      ├─── Valid? ──→ Load Account with maLoai
      │                      │
      │                      ▼
      │              Create MainFrame(account)
      │                      │
      │                      ▼
      │              Show HomePanel
      │
      └─── Invalid? ──→ Show error message
```

### 2. Navigation Flow
```
User clicks navigation button
      │
      ▼
MainFrame.navigateToPage(page)
      │
      ├─── Check access permissions
      │         │
      │         ├─── Manager-only page AND not LNV03?
      │         │         │
      │         │         └──→ Show "Access Denied" message
      │         │
      │         └─── Authorized?
      │                   │
      │                   ▼
      │         cardLayout.show(contentPanel, page)
      │
      ▼
Display requested page
```

### 3. Role-Based Access
```
User → Has maLoai from Account
         │
         ├─── LNV01 (Regular)
         │         │
         │         └──→ Access: Home, Vé, Khách hàng, Chuyến tàu
         │
         ├─── LNV02 (Senior)
         │         │
         │         └──→ Access: Home, Vé, Khách hàng, Chuyến tàu
         │
         └─── LNV03 (Manager)
                   │
                   └──→ Access: ALL pages including
                         - Quản lý nhân viên
                         - Quản lý tài khoản
                         - Thống kê
```

## Component Interactions

### Customer Search Flow
```
CustomerPanel
      │
      ├─── User enters phone number
      │         │
      │         ▼
      │    Click "Tìm kiếm"
      │         │
      │         ▼
      │    searchByPhone(phoneNumber)
      │         │
      │         ▼
      │    CustomerDAO.findAll()
      │         │
      │         ▼
      │    Filter by phone (contains)
      │         │
      │         ├─── Found? ──→ Display in table
      │         │
      │         └─── Not found? ──→ Show message
      │
      └─── Click "Làm mới"
                │
                ▼
           loadCustomers()
                │
                ▼
           Display all customers
```

### Delete Operation Flow
```
Any Panel with delete
      │
      ▼
User clicks "Xóa" button
      │
      ▼
Show confirmation dialog
"Bạn có chắc muốn xóa...?"
      │
      ├─── User selects "Yes"
      │         │
      │         ▼
      │    DAO.delete(id)
      │         │
      │         ▼
      │    Reload data
      │         │
      │         ▼
      │    Show success message
      │
      └─── User selects "No"
                │
                ▼
           Cancel operation
```

### Logout Flow
```
User clicks "Đăng xuất"
      │
      ▼
NavigationBar.handleLogout()
      │
      ▼
Show confirmation dialog
"Bạn có chắc muốn đăng xuất?"
      │
      ├─── User selects "Yes"
      │         │
      │         ▼
      │    Close MainFrame
      │         │
      │         ▼
      │    Create and show LoginFrame
      │
      └─── User selects "No"
                │
                ▼
           Stay logged in
```

## Data Flow

### Account Creation with maLoai
```
AccountPanel
      │
      ▼
User creates account with employeeId
      │
      ▼
AccountDAO.add(account)
      │
      ├─── maLoai not set?
      │         │
      │         ▼
      │    Find employee by employeeId
      │         │
      │         ▼
      │    Get employee.maLoai
      │         │
      │         ▼
      │    Set account.maLoai
      │
      ▼
Save account with maLoai
```

### Employee Management
```
EmployeePanel
      │
      ├─── Add Employee
      │         │
      │         ▼
      │    User selects maLoai (LNV01/LNV02/LNV03)
      │         │
      │         ▼
      │    Create Employee with maLoai
      │         │
      │         ▼
      │    EmployeeDAO.add(employee)
      │
      ├─── Update Employee
      │         │
      │         ▼
      │    User can change maLoai
      │         │
      │         ▼
      │    EmployeeDAO.update(employee)
      │
      └─── Delete Employee
                │
                ▼
           Confirm deletion
                │
                ▼
           EmployeeDAO.delete(id)
```

## Database Connection (Future)

```
Application
      │
      ▼
ConnectSql.getInstance()
      │
      ├─── First call?
      │         │
      │         ▼
      │    Create connection to SQL Server
      │         │
      │         └──→ Connection established
      │
      └─── Already exists?
                │
                └──→ Return existing connection

Usage in DAO:
      │
      ▼
Connection conn = ConnectSql.getInstance().getConnection()
      │
      ▼
Execute SQL queries
      │
      ▼
Process results
```

## Panel Responsibilities

| Panel | Responsibility | Access Level |
|-------|----------------|--------------|
| **HomePanel** | Welcome screen, quick access | All users |
| **TicketBookingPanel** | Book/refund/cancel tickets | All users |
| **CustomerPanel** | Manage customers, search by phone | All users |
| **TrainPanel** | Manage train schedules | All users |
| **EmployeePanel** | Manage employees with maLoai | LNV03 only |
| **AccountPanel** | Manage system accounts | LNV03 only |
| **StatisticsPanel** | View reports and statistics | LNV03 only |

## State Management

### Current Account State
- Stored in MainFrame and passed to NavigationBar
- Contains:
  - username
  - password
  - role (ADMIN/EMPLOYEE)
  - employeeId
  - **maLoai** (LNV01/LNV02/LNV03)
  - active status

### Navigation State
- Managed by CardLayout in MainFrame
- Current page tracked by card name
- Navigation changes trigger access control checks

## Error Handling

### Access Control Errors
```
User tries to access manager-only page
      │
      ▼
Check account.isManager()
      │
      └─── false? ──→ Show JOptionPane warning
                      "Bạn không có quyền truy cập..."
```

### Data Validation Errors
```
User submits incomplete form
      │
      ▼
validateForm()
      │
      └─── Invalid? ──→ Show JOptionPane error
                        "Vui lòng điền đầy đủ thông tin..."
```

## Performance Considerations

1. **In-Memory Storage**: Currently using HashMap for fast lookups
2. **Singleton DAOs**: One instance per DAO type
3. **Lazy Loading**: Panels created only when needed
4. **Event Listeners**: Efficient table selection listeners

## Security Features

1. ✅ Password field (not plain text display)
2. ✅ Role-based access control
3. ✅ Confirmation dialogs for destructive operations
4. ✅ Admin account protection (cannot be deleted)
5. ✅ Session management (one user at a time)
6. ✅ Access denial with clear messages

## Future Enhancements

1. **Database Integration**: Replace HashMap with SQL queries
2. **Session Timeout**: Auto-logout after inactivity
3. **Audit Logging**: Track all user actions
4. **Password Encryption**: Hash passwords before storage
5. **Multi-session Support**: Allow multiple concurrent users
6. **Data Export**: Export reports to PDF/Excel
7. **Advanced Search**: Multiple filter criteria
8. **User Preferences**: Customizable UI settings
