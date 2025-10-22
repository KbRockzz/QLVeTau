# PnlDatVe Modernization - Architecture Diagram

## System Architecture

```
┌──────────────────────────────────────────────────────────────────┐
│                     PnlDatVe (Presentation Layer)                │
│                                                                  │
│  ┌────────────────────────┐  ┌────────────────────────────┐    │
│  │  Customer Search UI    │  │  Train Search UI           │    │
│  │  - txtSoDienThoai      │  │  - cmbGaDi                 │    │
│  │  - btnTimKhachHang     │  │  - cmbGaDen                │    │
│  │  - lblThongTinKH       │  │  - dateNgayDi              │    │
│  └────────┬───────────────┘  │  - spnGioDi                │    │
│           │                   │  - btnTimChuyenTau         │    │
│           │                   └────────┬───────────────────┘    │
│           │                            │                        │
│           ▼                            ▼                        │
│  ┌─────────────────────────────────────────────────────┐       │
│  │         Results & Selection                         │       │
│  │  - tblChuyenTau (JTable)                           │       │
│  │  - bangToaTau (JTable)                             │       │
│  │  - pnlSoDoGhe (Seat Map)                           │       │
│  └─────────────────────────────────────────────────────┘       │
└──────────────────────┬───────────────────┬────────────────────┘
                       │                   │
                       ▼                   ▼
         ┌──────────────────────┐  ┌──────────────────────┐
         │   KhachHangDAO       │  │   ChuyenTauDAO       │
         │  (Data Access)       │  │  (Data Access)       │
         │                      │  │                      │
         │  NEW METHODS:        │  │  NEW METHODS:        │
         │  + timTheoSDT()      │  │  + timKiemChuyenTau()│
         │                      │  │  + getDistinctGa()   │
         └──────────┬───────────┘  └──────────┬───────────┘
                    │                         │
                    └───────────┬─────────────┘
                                ▼
                    ┌───────────────────────┐
                    │   ConnectSql          │
                    │  (Connection Pool)    │
                    └───────────┬───────────┘
                                ▼
                    ┌───────────────────────┐
                    │   SQL Server DB       │
                    │                       │
                    │  Tables:              │
                    │  - KhachHang          │
                    │  - ChuyenTau          │
                    │  - ToaTau             │
                    │  - Ghe                │
                    │  - Ve                 │
                    │  - HoaDon             │
                    └───────────────────────┘
```

## Data Flow Diagrams

### 1. Customer Search Flow

```
User Input                    UI Layer              DAO Layer           Database
─────────                     ────────              ─────────           ────────

  Type phone                                                        
  number "0123..."     
       │                                                            
       ▼                                                            
  Click "Tìm KH"      ──►  timKhachHang()                          
                               │                                   
                               ▼                                   
                          Validate input                           
                               │                                   
                               ▼                                   
                          Call DAO method  ──►  timTheoSDT()       
                                                     │             
                                                     ▼             
                                              Execute SQL   ──►  SELECT * 
                                              PreparedStmt       FROM KhachHang
                                                     │             WHERE sdt=?
                                                     ▼                   │
                                              ResultSet        ◄──  Result
                                                     │             
                                                     ▼             
                                              Create object    
                               ◄──          Return KhachHang
                               │
                               ▼
                          Update label
                          (green text)
       ◄──              Display info
       
  See customer info
```

### 2. Train Search Flow

```
User Input                    UI Layer              DAO Layer           Database
─────────                     ────────              ─────────           ────────

  Select criteria:
  - Ga di: Sài Gòn
  - Ga den: Hà Nội
  - Date: 15/10/2024
       │
       ▼
  Click "Tìm CT"      ──►  timChuyenTau()
                               │
                               ▼
                          Collect criteria
                          Convert dates
                               │
                               ▼
                          Call DAO method  ──►  timKiemChuyenTau()
                                                     │
                                                     ▼
                                              Build dynamic SQL
                                              Add WHERE clauses
                                                     │
                                                     ▼
                                              Execute query  ──►  SELECT *
                                              PreparedStmt       FROM ChuyenTau
                                                     │             WHERE gaDi=?
                                                     ▼             AND gaDen=?
                                              ResultSet       ◄──  AND date=?
                                                     │
                                                     ▼
                                              Map to objects
                               ◄──          Return List<CT>
                               │
                               ▼
                          Clear table
                          Format dates
                               │
                               ▼
                          Populate table
       ◄──              Display results
       
  See train list
  Compare options
       │
       ▼
  Click train row     ──►  chonChuyenTauTuBang()
                               │
                               ▼
                          Load carriages
                          (existing logic)
```

## Component Interaction Map

```
┌───────────────────────────────────────────────────────────────┐
│                          User Interface                       │
└───────────────────────────────────────────────────────────────┘
                              │
                ┌─────────────┼─────────────┐
                │             │             │
                ▼             ▼             ▼
        ┌──────────┐  ┌──────────┐  ┌──────────┐
        │ Customer │  │  Train   │  │  Seat    │
        │  Search  │  │  Search  │  │ Selection│
        │ Component│  │ Component│  │ Component│
        └─────┬────┘  └────┬─────┘  └────┬─────┘
              │            │             │
              ▼            ▼             ▼
        ┌──────────┐  ┌──────────┐  ┌──────────┐
        │ Khach    │  │ Chuyen   │  │  Ghe     │
        │ HangDAO  │  │ TauDAO   │  │  DAO     │
        └─────┬────┘  └────┬─────┘  └────┬─────┘
              │            │             │
              └────────────┼─────────────┘
                           │
                           ▼
                  ┌────────────────┐
                  │   SQL Server   │
                  │   Connection   │
                  └────────────────┘
```

## Class Diagram - Key Components

```
┌─────────────────────────────┐
│       PnlDatVe              │
├─────────────────────────────┤
│ - khachHangDuocChon: KH     │
│ - chuyenDuocChon: CT        │
│ - txtSoDienThoai: JTextField│
│ - cmbGaDi: JComboBox        │
│ - cmbGaDen: JComboBox       │
│ - dateNgayDi: JDateChooser  │
│ - tblChuyenTau: JTable      │
├─────────────────────────────┤
│ + timKhachHang(): void      │
│ + timChuyenTau(): void      │
│ + chonChuyenTauTuBang():void│
│ + chonGhe(Ghe): void        │
│ + datVe(KH, LoaiVe): void   │
└───────┬──────────┬──────────┘
        │          │
        │          │ uses
        ▼          ▼
┌──────────────┐  ┌──────────────┐
│ KhachHangDAO │  │ ChuyenTauDAO │
├──────────────┤  ├──────────────┤
│ + getAll()   │  │ + getAll()   │
│ + findById() │  │ + findById() │
│ + insert()   │  │ + insert()   │
│ + update()   │  │ + update()   │
│ + delete()   │  │ + delete()   │
│              │  │              │
│ NEW:         │  │ NEW:         │
│ + timTheoSDT()│ │ + timKiemCT()│
│              │  │ + getDistinctGa()│
└──────┬───────┘  └──────┬───────┘
       │                 │
       └────────┬────────┘
                │ uses
                ▼
        ┌───────────────┐
        │  ConnectSql   │
        ├───────────────┤
        │ - connection  │
        ├───────────────┤
        │ + getInstance()│
        │ + getConnection()│
        └───────────────┘
```

## Sequence Diagram - Complete Booking

```
User      PnlDatVe    KHDAO     CTDAO     GheDAO    VeService   DB
 │           │          │         │         │          │        │
 │ Type phone│          │         │         │          │        │
 │──────────►│          │         │         │          │        │
 │           │ timTheoSDT()       │         │          │        │
 │           │─────────►│         │         │          │        │
 │           │          │ SELECT  │         │          │        │
 │           │          │────────────────────────────────────►  │
 │           │          │◄────────────────────────────────────  │
 │           │◄─────────│         │         │          │        │
 │ Customer  │          │         │         │          │        │
 │◄──────────│          │         │         │          │        │
 │           │          │         │         │          │        │
 │ Select criteria      │         │         │          │        │
 │──────────►│          │         │         │          │        │
 │           │ timKiemChuyenTau() │         │          │        │
 │           │────────────────────►│         │          │        │
 │           │                     │ SELECT  │          │        │
 │           │                     │───────────────────────────► │
 │           │                     │◄─────────────────────────── │
 │           │◄────────────────────│         │          │        │
 │ Train list│          │         │         │          │        │
 │◄──────────│          │         │         │          │        │
 │           │          │         │         │          │        │
 │ Click train│         │         │         │          │        │
 │──────────►│          │         │         │          │        │
 │           │ Load carriages/seats│         │          │        │
 │           │────────────────────────────► │          │        │
 │           │                     │        │ SELECT   │        │
 │           │                     │        │─────────────────► │
 │           │                     │        │◄───────────────── │
 │           │◄────────────────────────────│          │        │
 │ Seat map  │          │         │         │          │        │
 │◄──────────│          │         │         │          │        │
 │           │          │         │         │          │        │
 │ Click seat│          │         │         │          │        │
 │──────────►│          │         │         │          │        │
 │           │ Confirm booking     │         │          │        │
 │           │ datVe()             │         │          │        │
 │           │─────────────────────────────────────────►│        │
 │           │                     │         │ taoVe()  │        │
 │           │                     │         │          │ INSERT │
 │           │                     │         │          │───────►│
 │           │                     │         │◄─────────│◄───────│
 │           │◄─────────────────────────────────────────│        │
 │ Success!  │          │         │         │          │        │
 │◄──────────│          │         │         │          │        │
```

## Technology Stack

```
┌───────────────────────────────────────┐
│         Presentation Layer            │
│  ┌─────────────────────────────────┐  │
│  │    Java Swing Components        │  │
│  │  - JPanel, JTable, JButton      │  │
│  │  - JComboBox, JTextField        │  │
│  │  - JDateChooser (JCalendar)     │  │
│  │  - JSpinner                     │  │
│  └─────────────────────────────────┘  │
└───────────────────────────────────────┘
                  │
┌───────────────────────────────────────┐
│         Business Layer                │
│  ┌─────────────────────────────────┐  │
│  │     Service Classes             │  │
│  │  - VeService                    │  │
│  │  - HoaDonService                │  │
│  └─────────────────────────────────┘  │
└───────────────────────────────────────┘
                  │
┌───────────────────────────────────────┐
│         Data Access Layer             │
│  ┌─────────────────────────────────┐  │
│  │     DAO Pattern                 │  │
│  │  - KhachHangDAO                 │  │
│  │  - ChuyenTauDAO                 │  │
│  │  - GheDAO, VeDAO                │  │
│  │  - GenericDAO interface         │  │
│  └─────────────────────────────────┘  │
└───────────────────────────────────────┘
                  │
┌───────────────────────────────────────┐
│         Persistence Layer             │
│  ┌─────────────────────────────────┐  │
│  │    JDBC Connection              │  │
│  │  - SQL Server Driver            │  │
│  │  - Connection Pool              │  │
│  │  - PreparedStatement            │  │
│  └─────────────────────────────────┘  │
└───────────────────────────────────────┘
                  │
┌───────────────────────────────────────┐
│         Database Layer                │
│  ┌─────────────────────────────────┐  │
│  │    Microsoft SQL Server         │  │
│  │  - QLTauHoa Database            │  │
│  │  - Tables: KhachHang, ChuyenTau │  │
│  │            ToaTau, Ghe, Ve      │  │
│  └─────────────────────────────────┘  │
└───────────────────────────────────────┘
```

## Key Patterns Used

### 1. Singleton Pattern
```java
// Used in DAO classes
public class KhachHangDAO {
    private static KhachHangDAO instance;
    
    public static synchronized KhachHangDAO getInstance() {
        if (instance == null) {
            instance = new KhachHangDAO();
        }
        return instance;
    }
}
```

### 2. DAO Pattern
```java
// GenericDAO interface
public interface GenericDAO<T> {
    List<T> getAll();
    T findById(String id);
    boolean insert(T entity);
    boolean update(T entity);
    boolean delete(String id);
}
```

### 3. MVC Pattern
```
Model:      KhachHang, ChuyenTau, Ve (entity classes)
View:       PnlDatVe (Swing components)
Controller: PnlDatVe (event handlers)
```

### 4. Table Model Pattern
```java
// Swing table model
DefaultTableModel model = new DefaultTableModel(columns, 0) {
    @Override
    public boolean isCellEditable(int row, int column) {
        return false; // Read-only
    }
};
```

## Summary

This architecture provides:
- ✅ Clear separation of concerns (UI, Business, Data layers)
- ✅ Reusable DAO components
- ✅ Maintainable code structure
- ✅ Scalable search functionality
- ✅ Type-safe data handling
- ✅ Efficient database queries

The modernization enhances the presentation layer with search capabilities while maintaining the solid foundation of the existing architecture.
