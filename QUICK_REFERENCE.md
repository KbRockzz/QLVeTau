# Quick Reference - Database & Code Synchronization

## 🎯 What Was Done

This project synchronizes Java models/DAOs with a Vietnamese-named database schema.

## 📊 Complete Mapping

| Database Table (Vietnamese) | Java Model | Java DAO | Status |
|------------------------------|------------|----------|---------|
| TaiKhoan | Account | AccountDAO | ✅ Updated |
| NhanVien | Employee | EmployeeDAO | ✅ Updated |
| KhachHang | Customer | CustomerDAO | ✅ Updated |
| Tau | Train | TrainDAO | ✅ Updated |
| Ghe | Seat | SeatDAO | ✅ Updated |
| ToaTau | Carriage | CarriageDAO | ✅ Updated |
| LoaiGhe | CarriageType | CarriageTypeDAO | ✅ Updated |
| Ve | Ticket | TicketDAO | ✅ Updated |
| **LoaiNV** | **LoaiNV** | **LoaiNVDAO** | ✨ **New** |
| **ChangTau** | **ChangTau** | **ChangTauDAO** | ✨ **New** |
| **ChuyenTau** | **ChuyenTau** | **ChuyenTauDAO** | ✨ **New** |
| **LoaiVe** | **LoaiVe** | **LoaiVeDAO** | ✨ **New** |
| **BangGia** | **BangGia** | **BangGiaDAO** | ✨ **New** |
| **HoaDon** | **HoaDon** | **HoaDonDAO** | ✨ **New** |
| **ChiTietHoaDon** | **ChiTietHoaDon** | **ChiTietHoaDonDAO** | ✨ **New** |

## 🚀 Quick Start

### 1. Setup Database

```bash
# Run the Vietnamese database schema
sqlcmd -S localhost -U sa -P your_password -i database_schema_vietnamese.sql
```

### 2. Build Project

```bash
mvn clean compile
```

### 3. Run Application

```bash
mvn exec:java -Dexec.mainClass="com.trainstation.MainApplication"
```

## 💡 Usage Examples

### Example 1: Working with LoaiNV (Employee Types)

```java
// Get DAO instance
LoaiNVDAO dao = LoaiNVDAO.getInstance();

// Get all employee types
List<LoaiNV> types = dao.findAll();

// Find specific type
LoaiNV manager = dao.findById("LNV03");
System.out.println(manager.getTenLoai()); // Prints: "Quản lý"
```

### Example 2: Working with ChangTau (Train Stations)

```java
// Get DAO instance
ChangTauDAO dao = ChangTauDAO.getInstance();

// Find all stations
List<ChangTau> stations = dao.findAll();

// Find stations in a specific city
List<ChangTau> hcmStations = dao.findByCity("TP.HCM");

// Add new station
ChangTau newStation = new ChangTau(
    "GA06", 
    "Ga Biên Hòa", 
    "123 Phạm Văn Thuận",
    "Đồng Nai"
);
dao.add(newStation);
```

### Example 3: Working with BangGia (Price Table)

```java
// Get DAO instance
BangGiaDAO dao = BangGiaDAO.getInstance();

// Find price for specific seat type and ticket type
BangGia price = dao.findByLoaiGheAndLoaiVe("CT01", "LV01");
System.out.println("Price: " + price.getGiaTien());
```

### Example 4: Working with HoaDon (Invoices)

```java
// Get DAO instance
HoaDonDAO dao = HoaDonDAO.getInstance();

// Create new invoice
HoaDon invoice = new HoaDon(
    "HD001",
    "KH001",
    "EMP001",
    LocalDateTime.now(),
    850000.0,
    "COMPLETED"
);
dao.add(invoice);

// Find all invoices for a customer
List<HoaDon> customerInvoices = dao.findByCustomerId("KH001");

// Find invoices by status
List<HoaDon> completed = dao.findByStatus("COMPLETED");
```

### Example 5: Existing Models Still Work

```java
// All existing code continues to work
AccountDAO accountDAO = AccountDAO.getInstance();
Account account = accountDAO.findById("admin");

CustomerDAO customerDAO = CustomerDAO.getInstance();
Customer customer = customerDAO.findById("KH001");

// Now queries Vietnamese table names internally
// but the Java API remains the same!
```

## 📁 File Locations

```
QLVeTau/
├── database_schema_vietnamese.sql          # New Vietnamese DB schema
├── SYNCHRONIZATION_SUMMARY.md              # Detailed documentation
├── QUICK_REFERENCE.md                      # This file
└── src/main/java/com/trainstation/
    ├── model/
    │   ├── Account.java                    # Existing
    │   ├── Employee.java                   # Existing
    │   ├── Customer.java                   # Existing
    │   ├── Train.java                      # Existing
    │   ├── Seat.java                       # Existing
    │   ├── Carriage.java                   # Existing
    │   ├── CarriageType.java               # Existing
    │   ├── Ticket.java                     # Existing
    │   ├── LoaiNV.java                     # NEW
    │   ├── ChangTau.java                   # NEW
    │   ├── ChuyenTau.java                  # NEW
    │   ├── LoaiVe.java                     # NEW
    │   ├── BangGia.java                    # NEW
    │   ├── HoaDon.java                     # NEW
    │   └── ChiTietHoaDon.java              # NEW
    └── dao/
        ├── GenericDAO.java                 # Interface
        ├── AccountDAO.java                 # Updated
        ├── EmployeeDAO.java                # Updated
        ├── CustomerDAO.java                # Updated
        ├── TrainDAO.java                   # Updated
        ├── SeatDAO.java                    # Updated
        ├── CarriageDAO.java                # Updated
        ├── CarriageTypeDAO.java            # Updated
        ├── TicketDAO.java                  # Updated
        ├── LoaiNVDAO.java                  # NEW
        ├── ChangTauDAO.java                # NEW
        ├── ChuyenTauDAO.java               # NEW
        ├── LoaiVeDAO.java                  # NEW
        ├── BangGiaDAO.java                 # NEW
        ├── HoaDonDAO.java                  # NEW
        └── ChiTietHoaDonDAO.java           # NEW
```

## ✅ Verification Checklist

- [x] Database schema with Vietnamese table names created
- [x] All 8 existing DAOs updated to query Vietnamese tables
- [x] 7 new model classes created
- [x] 7 new DAO classes created with full CRUD
- [x] All models have fields matching database columns
- [x] No virtual or redundant properties
- [x] Project compiles successfully
- [x] No breaking changes to existing code
- [x] Singleton pattern maintained
- [x] GenericDAO interface implemented
- [x] Documentation provided

## 🎓 Key Points

1. **Model class names** remain in English (Account, Employee, etc.) for code readability
2. **Database table names** are in Vietnamese (TaiKhoan, NhanVien, etc.) as required
3. **DAO SQL queries** use Vietnamese table names internally
4. **Zero breaking changes** - all existing code continues to work
5. **Full coverage** - every database table has a corresponding model and DAO

## 📞 Support

For detailed information, see `SYNCHRONIZATION_SUMMARY.md`

## 🔄 Migration Path

If you have an existing database with English table names:

1. Backup your current database
2. Run `database_schema_vietnamese.sql` to create new schema
3. Migrate data from old tables to new Vietnamese-named tables
4. Update your application to use the new schema

OR simply rename your existing tables to Vietnamese names:

```sql
EXEC sp_rename 'Account', 'TaiKhoan';
EXEC sp_rename 'Employee', 'NhanVien';
EXEC sp_rename 'Customer', 'KhachHang';
-- etc...
```
