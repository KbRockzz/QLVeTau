# Developer Reference - QLVeTau

## Quick Start Commands

```bash
# Compile the project
mvn clean compile

# Package as JAR
mvn clean package

# Run the application
java -jar target/QLVeTau-1.0.0.jar
# OR
mvn exec:java -Dexec.mainClass="com.trainstation.MainApplication"
```

## Project Structure

```
src/main/java/com/trainstation/
├── MainApplication.java        # Entry point
├── MySQL/
│   └── ConnectSql.java        # SQL Server connection singleton
├── dao/                       # Data Access Objects
│   ├── GenericDAO.java        # Interface
│   ├── AccountDAO.java        # Account CRUD + authentication
│   ├── CustomerDAO.java       # Customer CRUD
│   ├── EmployeeDAO.java       # Employee CRUD
│   ├── TicketDAO.java         # Ticket CRUD
│   └── TrainDAO.java          # Train CRUD
├── gui/                       # User Interface
│   ├── LoginFrame.java        # Login screen
│   ├── MainFrame.java         # Main container with CardLayout
│   ├── NavigationBar.java     # Top navigation component
│   ├── HomePanel.java         # Dashboard/home page
│   ├── TicketBookingPanel.java
│   ├── CustomerPanel.java     # With phone search
│   ├── TrainPanel.java
│   ├── EmployeePanel.java     # Manager only
│   ├── AccountPanel.java      # Manager only
│   └── StatisticsPanel.java   # Manager only
├── model/                     # Entity classes
│   ├── Account.java           # User account with maLoai
│   ├── Customer.java
│   ├── Employee.java          # With maLoai field
│   ├── Ticket.java
│   └── Train.java
├── service/                   # Business logic
│   ├── TicketService.java
│   └── StatisticsService.java
└── util/
    └── DataInitializer.java   # Sample data loader
```

## Key Classes Reference

### Model Classes

#### Account
```java
public class Account {
    private String username;
    private String password;
    private String role;           // "ADMIN" or "EMPLOYEE"
    private String employeeId;
    private String maLoai;         // "LNV01", "LNV02", "LNV03"
    private boolean active;
    
    public boolean isManager();    // true if maLoai == "LNV03"
}
```

#### Employee
```java
public class Employee {
    private String employeeId;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String position;
    private String maLoai;         // Employee type
    private LocalDate hireDate;
    private double salary;
}
```

### DAO Pattern

All DAOs implement:
```java
public interface GenericDAO<T> {
    void add(T entity);
    void update(T entity);
    void delete(String id);
    T findById(String id);
    List<T> findAll();
}
```

Usage example:
```java
CustomerDAO customerDAO = CustomerDAO.getInstance();
Customer customer = customerDAO.findById("KH001");
customerDAO.update(customer);
```

### Navigation System

#### NavigationBar
```java
public class NavigationBar extends JPanel {
    public NavigationBar(Account account, JFrame parentFrame);
    // Creates navigation buttons based on user role
}
```

#### MainFrame
```java
public class MainFrame extends JFrame {
    public MainFrame(Account account);
    public void navigateToPage(String page);
    // Handles page switching with access control
}
```

## Access Control

### Check if user is manager:
```java
if (currentAccount.isManager()) {
    // Show manager-only features
}
```

### Navigate with access control:
```java
// In MainFrame
public void navigateToPage(String page) {
    if (!currentAccount.isManager() && 
        (page.equals("employee") || page.equals("account") || page.equals("statistics"))) {
        JOptionPane.showMessageDialog(this,
            "Bạn không có quyền truy cập trang này!",
            "Từ chối truy cập",
            JOptionPane.WARNING_MESSAGE);
        return;
    }
    cardLayout.show(contentPanel, page);
}
```

## Database Connection

### ConnectSql Usage
```java
// Get connection
ConnectSql db = ConnectSql.getInstance();
Connection conn = db.getConnection();

// Test connection
if (db.testConnection()) {
    // Connection is valid
}

// Execute query
try (PreparedStatement stmt = conn.prepareStatement(sql)) {
    ResultSet rs = stmt.executeQuery();
    // Process results
}

// Close connection when done
db.closeConnection();
```

### Configuration
Edit `ConnectSql.java`:
```java
private static final String SERVER = "your_server";
private static final String PORT = "1433";
private static final String DATABASE = "QLVeTau";
private static final String USERNAME = "your_username";
private static final String PASSWORD = "your_password";
```

## Common Patterns

### Form Validation
```java
private boolean validateForm() {
    if (field.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, 
            "Vui lòng điền đầy đủ thông tin!", 
            "Lỗi", 
            JOptionPane.ERROR_MESSAGE);
        return false;
    }
    return true;
}
```

### Confirmation Dialog
```java
int result = JOptionPane.showConfirmDialog(this, 
    "Bạn có chắc muốn xóa?", 
    "Xác nhận", 
    JOptionPane.YES_NO_OPTION);
    
if (result == JOptionPane.YES_OPTION) {
    // Proceed with deletion
}
```

### Table Update
```java
private void loadData() {
    tableModel.setRowCount(0);
    List<Entity> items = dao.findAll();
    for (Entity item : items) {
        tableModel.addRow(new Object[]{
            item.getId(),
            item.getName(),
            // ... more fields
        });
    }
}
```

### Search Implementation
```java
private void search(String criteria) {
    tableModel.setRowCount(0);
    List<Entity> items = dao.findAll();
    boolean found = false;
    
    for (Entity item : items) {
        if (item.getField().contains(criteria)) {
            tableModel.addRow(new Object[]{/* ... */});
            found = true;
        }
    }
    
    if (!found) {
        JOptionPane.showMessageDialog(this, 
            "Không tìm thấy kết quả");
    }
}
```

## Color Scheme

```java
// Navigation bar colors
Color navBackground = new Color(52, 73, 94);    // Dark blue-gray
Color navButton = new Color(41, 128, 185);      // Blue
Color navButtonHover = new Color(52, 152, 219); // Light blue

// Text colors
Color textWhite = Color.WHITE;
Color textGray = Color.GRAY;
Color titleBlue = new Color(41, 128, 185);
```

## Default Credentials

```java
// Admin account (LNV03)
Username: admin
Password: admin123
Employee ID: EMP001
Role: ADMIN
MaLoai: LNV03

// Sample employees
EMP001: LNV03 (Manager)
EMP002: LNV01 (Regular)
EMP003: LNV02 (Senior)
```

## Testing Checklist

### Login Testing
- [ ] Valid credentials
- [ ] Invalid credentials
- [ ] Empty fields
- [ ] Account with LNV03
- [ ] Account with LNV01/LNV02

### Navigation Testing
- [ ] All navigation buttons work
- [ ] Manager sees Employee dropdown
- [ ] Manager sees Account button
- [ ] Non-manager doesn't see restricted buttons
- [ ] Logout confirmation works

### CRUD Operations Testing
For each entity (Customer, Employee, Train, Account, Ticket):
- [ ] Create with valid data
- [ ] Create with invalid data
- [ ] Read/Display all
- [ ] Update existing
- [ ] Delete with confirmation
- [ ] Delete without selection

### Search Testing (Customer)
- [ ] Search with full phone number
- [ ] Search with partial phone number
- [ ] Search with no matches
- [ ] Refresh after search

### Access Control Testing
- [ ] LNV03 can access all pages
- [ ] LNV01/LNV02 cannot access Employee management
- [ ] LNV01/LNV02 cannot access Account management
- [ ] LNV01/LNV02 cannot access Statistics
- [ ] Access denial shows proper message

## Common Issues & Solutions

### Issue: Cannot connect to SQL Server
**Solution**: 
1. Check server name and port
2. Verify SQL Server is running
3. Check firewall settings
4. Update connection string in ConnectSql.java

### Issue: maLoai is null
**Solution**:
1. Ensure Employee has maLoai set when created
2. AccountDAO will auto-populate from Employee
3. Update existing employees with maLoai values

### Issue: Access denied for valid manager
**Solution**:
1. Check account.maLoai == "LNV03"
2. Verify employee record has LNV03
3. Re-authenticate to refresh maLoai

### Issue: Delete operation doesn't show confirmation
**Solution**:
1. Check delete method has showConfirmDialog
2. Verify YES_NO_OPTION is used
3. Delete only executed if result == YES_OPTION

## Performance Tips

1. **Singleton Pattern**: Use getInstance() for DAOs
2. **Table Updates**: Only reload when data changes
3. **Event Listeners**: Remove listeners when not needed
4. **Memory**: Clear large collections after use

## Code Style Guidelines

1. **Naming**:
   - Classes: PascalCase
   - Methods: camelCase
   - Constants: UPPER_SNAKE_CASE
   - Variables: camelCase

2. **Comments**:
   - JavaDoc for public methods
   - Inline comments for complex logic
   - Vietnamese for user messages

3. **Layout**:
   - Use BorderLayout for main panels
   - GridBagLayout for forms
   - FlowLayout for button groups

4. **Confirmation**:
   - All delete operations
   - Logout action
   - Refund/cancel operations

## Debugging

### Enable detailed logging:
```java
System.out.println("Current user: " + account.getUsername());
System.out.println("MaLoai: " + account.getMaLoai());
System.out.println("Is Manager: " + account.isManager());
```

### Check navigation:
```java
System.out.println("Navigating to: " + page);
System.out.println("Current panel: " + contentPanel.getName());
```

### Database connection:
```java
ConnectSql db = ConnectSql.getInstance();
if (db.testConnection()) {
    System.out.println("Database connected!");
} else {
    System.out.println("Database connection failed!");
}
```

## Build Configuration

### Maven Dependencies
- JUnit 4.13.2 (testing)
- Java 17 (compiler target)

### Maven Goals
```bash
mvn clean          # Clean build artifacts
mvn compile        # Compile source code
mvn test           # Run tests
mvn package        # Create JAR file
mvn install        # Install to local repository
```

## Version Control

### Important Files
- All .java files in src/
- pom.xml
- README.md
- Documentation files (.md)

### Ignore
- target/
- .idea/
- *.iml
- .DS_Store

## Support Resources

- Project Structure: See APPLICATION_FLOW.md
- User Guide: See FEATURES_GUIDE.md
- Implementation Details: See IMPLEMENTATION_NOTES.md
- This Reference: DEVELOPER_REFERENCE.md
