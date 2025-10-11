# Sơ đồ kiến trúc hệ thống QLVeTau

## Tổng quan kiến trúc

```
┌─────────────────────────────────────────────────────────────────┐
│                        PRESENTATION LAYER                        │
│                         (Swing GUI)                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────────┐  ┌──────────────────────────────────────┐    │
│  │ LoginFrame   │  │        MainFrame                     │    │
│  └──────────────┘  │  ┌────────────────────────────────┐ │    │
│                    │  │    NavigationBar               │ │    │
│                    │  └────────────────────────────────┘ │    │
│                    │  ┌────────────────────────────────┐ │    │
│                    │  │  CardLayout Content Panel      │ │    │
│                    │  │                                │ │    │
│                    │  │  ┌──────────────────────────┐ │ │    │
│                    │  │  │   HomePanel              │ │ │    │
│                    │  │  │   (Train Station BG)     │ │ │    │
│                    │  │  └──────────────────────────┘ │ │    │
│                    │  │                                │ │    │
│                    │  │  ┌──────────────────────────┐ │ │    │
│                    │  │  │   BookTicketPanel        │ │ │    │
│                    │  │  │   - Train Selection      │ │ │    │
│                    │  │  │   - Carriage List        │ │ │    │
│                    │  │  │   - Seat Grid           │ │ │    │
│                    │  │  └──────────────────────────┘ │ │    │
│                    │  │                                │ │    │
│                    │  │  ┌──────────────────────────┐ │ │    │
│                    │  │  │   RefundTicketPanel      │ │ │    │
│                    │  │  │   - Ticket List          │ │ │    │
│                    │  │  │   - Ticket Details       │ │ │    │
│                    │  │  └──────────────────────────┘ │ │    │
│                    │  │                                │ │    │
│                    │  │  ┌──────────────────────────┐ │ │    │
│                    │  │  │   ChangeTicketPanel      │ │ │    │
│                    │  │  │   - Ticket Selection     │ │ │    │
│                    │  │  │   - New Train Selection  │ │ │    │
│                    │  │  │   - New Seat Selection   │ │ │    │
│                    │  │  └──────────────────────────┘ │ │    │
│                    │  │                                │ │    │
│                    │  │  [Other Panels...]            │ │    │
│                    │  └────────────────────────────────┘ │    │
│                    └──────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────┐
│                      BUSINESS LOGIC LAYER                        │
│                         (Services)                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌─────────────────────┐         ┌─────────────────────┐       │
│  │  TicketService      │         │ StatisticsService   │       │
│  │  - bookTicket()     │         │ - getRevenue()      │       │
│  │  - refundTicket()   │         │ - getTicketStats()  │       │
│  │  - changeTicket() ✨│         └─────────────────────┘       │
│  │  - getAllTickets()  │                                        │
│  └─────────────────────┘                                        │
└─────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────┐
│                      DATA ACCESS LAYER                           │
│                          (DAOs)                                  │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐         │
│  │ AccountDAO   │  │ CustomerDAO  │  │ EmployeeDAO  │         │
│  └──────────────┘  └──────────────┘  └──────────────┘         │
│                                                                  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐         │
│  │  TrainDAO    │  │  TicketDAO   │  │CarriageType  │         │
│  │              │  │              │  │    DAO    ✨  │         │
│  └──────────────┘  └──────────────┘  └──────────────┘         │
│                                                                  │
│  ┌──────────────┐  ┌──────────────┐                            │
│  │ CarriageDAO  │  │   SeatDAO    │                            │
│  │         ✨   │  │         ✨   │                            │
│  └──────────────┘  └──────────────┘                            │
│                                                                  │
│  Implements: GenericDAO<T>                                      │
│  - add(T entity)                                                │
│  - update(T entity)                                             │
│  - delete(String id)                                            │
│  - findById(String id)                                          │
│  - findAll()                                                    │
└─────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────┐
│                      DATABASE CONNECTION                         │
│                        (ConnectSql)                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Singleton Pattern                                              │
│  - getInstance()                                                │
│  - getConnection()                                              │
│  - testConnection()                                             │
│                                                                  │
│  JDBC Driver: mssql-jdbc 12.4.1                                │
└─────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────┐
│                       SQL SERVER DATABASE                        │
│                          (QLVeTau)                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Tables:                                                        │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐           │
│  │  Customer   │  │  Employee   │  │   Account   │           │
│  └─────────────┘  └─────────────┘  └─────────────┘           │
│                                                                  │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐           │
│  │    Train    │  │   Ticket    │  │ Carriage    │           │
│  │             │  │             │  │   Type   ✨  │           │
│  └─────────────┘  └─────────────┘  └─────────────┘           │
│                                                                  │
│  ┌─────────────┐  ┌─────────────┐                              │
│  │  Carriage   │  │    Seat     │                              │
│  │         ✨  │  │         ✨  │                              │
│  └─────────────┘  └─────────────┘                              │
└─────────────────────────────────────────────────────────────────┘
```

## Luồng dữ liệu chính

### 1. Đặt vé (Book Ticket)

```
User Input → BookTicketPanel
                │
                ▼
        Select Train & Customer
                │
                ▼
        Load Carriages (CarriageDAO)
                │
                ▼
        Display Carriage List
                │
                ▼
        User Selects Carriage
                │
                ▼
        Load Seats (SeatDAO)
                │
                ▼
        Display Seat Grid (Color-coded)
                │
                ▼
        User Selects Seat (Green only)
                │
                ▼
        TicketService.bookTicket()
                │
                ├─→ TicketDAO.add()
                ├─→ SeatDAO.update() (→ BOOKED)
                └─→ TrainDAO.update() (availableSeats--)
                │
                ▼
        Success Message (Ticket ID)
```

### 2. Hoàn vé (Refund Ticket)

```
User Input → RefundTicketPanel
                │
                ▼
        Display Ticket List (BOOKED only)
                │
                ▼
        User Selects Ticket
                │
                ▼
        Display Ticket Details
                │
                ▼
        User Confirms Refund
                │
                ▼
        TicketService.refundTicket()
                │
                ├─→ TicketDAO.update() (→ REFUNDED)
                ├─→ SeatDAO.update() (→ AVAILABLE)
                └─→ TrainDAO.update() (availableSeats++)
                │
                ▼
        Success Message
```

### 3. Đổi vé (Change Ticket) ✨

```
User Input → ChangeTicketPanel
                │
                ▼
        Display Ticket List (BOOKED only)
                │
                ▼
        User Selects Old Ticket
                │
                ▼
        Select New Train
                │
                ▼
        Load New Carriages
                │
                ▼
        User Selects New Carriage
                │
                ▼
        Load New Seats
                │
                ▼
        User Selects New Seat
                │
                ▼
        User Confirms Change
                │
                ▼
        TicketService.changeTicket()
                │
                ├─→ Get Old Ticket (TicketDAO)
                ├─→ Release Old Seat (SeatDAO → AVAILABLE)
                ├─→ Update Old Train (availableSeats++)
                ├─→ Book New Seat (SeatDAO → BOOKED)
                ├─→ Update New Train (availableSeats--)
                └─→ Update Ticket (new train, seat, price)
                │
                ▼
        Success Message
```

## Quan hệ giữa các Entity

```
┌──────────────┐
│   Account    │
│              │
│ username PK  │
│ employeeId FK├──────┐
└──────────────┘      │
                      ▼
                ┌──────────────┐
                │   Employee   │
                │              │
                │ employeeId PK│
                │ maLoai       │
                └──────────────┘
                      │
                      │ (creates)
                      ▼
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│   Customer   │    │    Ticket    │    │    Train     │
│              │◄───┤              ├───►│              │
│ customerId PK│    │ ticketId PK  │    │ trainId PK   │
└──────────────┘    │ customerId FK│    └──────────────┘
                    │ trainId FK   │           │
                    │ seatId FK    │           │ (has)
                    │ carriageId FK│           ▼
                    └──────────────┘    ┌──────────────┐
                           │            │CarriageType ✨│
                           │            │              │
                           │            │carriageTypeId│
                           │            └──────────────┘
                           │                   │
                           │                   │ (defines)
                           ▼                   ▼
                    ┌──────────────┐    ┌──────────────┐
                    │    Seat   ✨  │    │ Carriage  ✨  │
                    │              │◄───┤              │
                    │ seatId PK    │    │ carriageId PK│
                    │ carriageId FK│    │ trainId FK   │
                    │ status       │    │carriageTypeFK│
                    └──────────────┘    └──────────────┘
                       (1..n)                 (1..n)
```

## Design Patterns sử dụng

### 1. Singleton Pattern
- **ConnectSql**: Database connection
- **All DAOs**: AccountDAO, CustomerDAO, EmployeeDAO, etc.
- **Services**: TicketService, StatisticsService

### 2. DAO Pattern
- Generic interface: `GenericDAO<T>`
- Specific implementations for each entity
- Separation of data access logic

### 3. MVC Pattern
- **Model**: Entity classes (Customer, Ticket, Train, etc.)
- **View**: Swing GUI panels (BookTicketPanel, RefundTicketPanel, etc.)
- **Controller**: Service layer (TicketService, StatisticsService)

### 4. Factory Pattern
- CardLayout for switching panels dynamically

### 5. Observer Pattern
- Table selection listeners
- Action listeners for buttons

## Cải tiến so với phiên bản trước

### Thêm mới ✨
1. **Models**: Carriage, CarriageType, Seat
2. **DAOs**: CarriageDAO, CarriageTypeDAO, SeatDAO
3. **Panels**: BookTicketPanel, RefundTicketPanel, ChangeTicketPanel
4. **Service Method**: changeTicket()
5. **UI Feature**: Visual seat selection with color coding

### Cập nhật
1. **HomePanel**: New train station background design
2. **NavigationBar**: Dropdown menu for ticket management
3. **Ticket Model**: Added seatId and carriageId fields
4. **MainFrame**: Updated to use new panels

### Loại bỏ
1. **Quick Access Panel**: Removed from HomePanel
2. **Cancel Ticket**: Replaced by Change Ticket functionality

---

**Ghi chú**: 
- ✨ = Tính năng/thành phần mới
- FK = Foreign Key
- PK = Primary Key
