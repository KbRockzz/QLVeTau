# DAO and Entity Implementation Guide

## Overview
This document describes the implementation of Data Access Objects (DAOs) and Entity classes for the QLVeTau (Train Ticket Management System).

## Implementation Summary

### Database Structure
The system uses SQL Server with the following main tables:
- **Customer** (Khách hàng) - Customer information
- **Employee** (Nhân viên) - Employee information
- **Account** (Tài khoản) - User accounts linked to employees
- **Train** (Chuyến tàu) - Train schedules
- **CarriageType** (Loại toa) - Types of train carriages
- **Carriage** (Toa tàu) - Individual carriages on trains
- **Seat** (Ghế) - Seats within carriages
- **Ticket** (Vé) - Ticket bookings

### Entity Classes

All entity classes are located in `src/main/java/com/trainstation/model/` and implement `Serializable`.

#### 1. Customer.java
**Properties:**
- `customerId` - Primary key
- `fullName` - Customer's full name
- `phoneNumber` - Contact phone number
- `email` - Email address
- `identityNumber` - ID card number
- `address` - Customer's address

**Constructors:**
- Default constructor
- Full constructor with all parameters

#### 2. Employee.java
**Properties:**
- `employeeId` - Primary key
- `fullName` - Employee's full name
- `phoneNumber` - Contact phone number
- `email` - Email address
- `position` - Job position
- `maLoai` - Employee type (LNV01, LNV02, LNV03 for Manager)
- `hireDate` - Date of hire (LocalDate)
- `salary` - Employee salary

**Constructors:**
- Default constructor
- Full constructor with all parameters

#### 3. Account.java
**Properties:**
- `username` - Primary key
- `password` - Account password
- `role` - User role (ADMIN or EMPLOYEE)
- `employeeId` - Foreign key to Employee
- `maLoai` - Employee type (cached from Employee)
- `active` - Account active status
- **`employee`** - Employee object reference (for object relationships)

**Constructors:**
- Default constructor
- Constructor with string employeeId
- Constructor with string employeeId and maLoai
- **Constructor with Employee object** (automatically extracts employeeId and maLoai)

**Special Methods:**
- `isManager()` - Returns true if maLoai is LNV03

**Object Relationship:**
- Setting `employee` object automatically updates `employeeId` and `maLoai` fields
- Maintains backward compatibility with string-based ID

#### 4. Train.java
**Properties:**
- `trainId` - Primary key
- `trainName` - Train name
- `departureStation` - Departure station name
- `arrivalStation` - Arrival station name
- `departureTime` - Departure date/time (LocalDateTime)
- `arrivalTime` - Arrival date/time (LocalDateTime)
- `totalSeats` - Total number of seats
- `availableSeats` - Number of available seats
- `ticketPrice` - Base ticket price

**Constructors:**
- Default constructor
- Full constructor with all parameters

#### 5. CarriageType.java
**Properties:**
- `carriageTypeId` - Primary key
- `typeName` - Type name (e.g., "Ghế ngồi cứng", "Giường nằm 6")
- `seatCount` - Number of seats in this carriage type
- `priceMultiplier` - Price multiplier for this type (1.0 = base price)

**Constructors:**
- Default constructor
- Full constructor with all parameters

#### 6. Carriage.java
**Properties:**
- `carriageId` - Primary key
- `trainId` - Foreign key to Train
- `carriageTypeId` - Foreign key to CarriageType
- `carriageName` - Carriage display name
- `carriageNumber` - Carriage sequence number
- **`train`** - Train object reference (for object relationships)
- **`carriageType`** - CarriageType object reference (for object relationships)

**Constructors:**
- Default constructor
- Constructor with string IDs
- **Constructor with Train and CarriageType objects** (automatically extracts IDs)

**Object Relationships:**
- Setting `train` object automatically updates `trainId` field
- Setting `carriageType` object automatically updates `carriageTypeId` field
- Maintains backward compatibility with string-based IDs

#### 7. Seat.java
**Properties:**
- `seatId` - Primary key
- `carriageId` - Foreign key to Carriage
- `seatNumber` - Seat number within carriage
- `status` - Seat status (AVAILABLE or BOOKED)
- **`carriage`** - Carriage object reference (for object relationships)

**Constructors:**
- Default constructor
- Constructor with string carriageId
- **Constructor with Carriage object** (automatically extracts carriageId)

**Object Relationship:**
- Setting `carriage` object automatically updates `carriageId` field
- Maintains backward compatibility with string-based ID

#### 8. Ticket.java
**Properties:**
- `ticketId` - Primary key
- `trainId` - Foreign key to Train
- `customerId` - Foreign key to Customer
- `employeeId` - Foreign key to Employee (who sold the ticket)
- `bookingDate` - Date/time of booking (LocalDateTime)
- `seatNumber` - Seat number
- `seatId` - Foreign key to Seat
- `carriageId` - Foreign key to Carriage
- `price` - Ticket price
- `status` - Ticket status (BOOKED, CANCELLED, REFUNDED)
- **`train`** - Train object reference (for object relationships)
- **`customer`** - Customer object reference (for object relationships)
- **`employee`** - Employee object reference (for object relationships)
- **`seat`** - Seat object reference (for object relationships)
- **`carriage`** - Carriage object reference (for object relationships)

**Constructors:**
- Default constructor
- Constructor with string IDs (basic)
- Constructor with string IDs (full)
- **Constructor with object references** (automatically extracts all IDs)

**Object Relationships:**
- Setting any object reference automatically updates corresponding ID field(s)
- Setting `seat` also updates `seatNumber`
- Maintains full backward compatibility with string-based IDs

### DAO Classes

All DAO classes are located in `src/main/java/com/trainstation/dao/` and implement the `GenericDAO<T>` interface.

#### GenericDAO Interface
```java
public interface GenericDAO<T> {
    void add(T entity);
    void update(T entity);
    void delete(String id);
    T findById(String id);
    List<T> findAll();
}
```

#### Common DAO Pattern

All DAOs follow this consistent pattern:

1. **Singleton Pattern**: Each DAO has a private constructor and `getInstance()` method
2. **Connection Management**: Uses `ConnectSql.getInstance().getConnection()` for database access
3. **PreparedStatement**: All SQL queries use PreparedStatement for security and performance
4. **CRUD Operations**: Implement add, update, delete, findById, findAll methods
5. **Helper Method**: `extract*FromResultSet(ResultSet rs)` for mapping ResultSet to entity objects

#### 1. CustomerDAO
**Standard Methods:**
- `add(Customer)` - Insert new customer
- `update(Customer)` - Update existing customer
- `delete(String id)` - Delete customer by ID
- `findById(String id)` - Find customer by ID
- `findAll()` - Get all customers

**Custom Methods:**
- `findByPhoneNumber(String phoneNumber)` - Find customer by phone number

#### 2. EmployeeDAO
**Standard Methods:**
- `add(Employee)` - Insert new employee
- `update(Employee)` - Update existing employee
- `delete(String id)` - Delete employee by ID
- `findById(String id)` - Find employee by ID
- `findAll()` - Get all employees

**Custom Methods:**
- `findByPosition(String position)` - Find employees by position

#### 3. AccountDAO
**Standard Methods:**
- `add(Account)` - Insert new account
- `update(Account)` - Update existing account
- `delete(String username)` - Delete account by username
- `findById(String username)` - Find account by username
- `findAll()` - Get all accounts

**Custom Methods:**
- `authenticate(String username, String password)` - Authenticate user login
- `findByRole(String role)` - Find accounts by role

**Special Features:**
- JOINs with Employee table to fetch maLoai automatically
- All methods that retrieve accounts include the employee's maLoai

#### 4. TrainDAO
**Standard Methods:**
- `add(Train)` - Insert new train
- `update(Train)` - Update existing train
- `delete(String id)` - Delete train by ID
- `findById(String id)` - Find train by ID
- `findAll()` - Get all trains

**Custom Methods:**
- `findByRoute(String departureStation, String arrivalStation)` - Find trains by route

#### 5. CarriageTypeDAO
**Standard Methods:**
- `add(CarriageType)` - Insert new carriage type
- `update(CarriageType)` - Update existing carriage type
- `delete(String id)` - Delete carriage type by ID
- `findById(String id)` - Find carriage type by ID
- `findAll()` - Get all carriage types

#### 6. CarriageDAO
**Standard Methods:**
- `add(Carriage)` - Insert new carriage
- `update(Carriage)` - Update existing carriage
- `delete(String id)` - Delete carriage by ID
- `findById(String id)` - Find carriage by ID
- `findAll()` - Get all carriages

**Custom Methods:**
- `findByTrainId(String trainId)` - Find all carriages for a specific train, ordered by carriage number

#### 7. SeatDAO
**Standard Methods:**
- `add(Seat)` - Insert new seat
- `update(Seat)` - Update existing seat
- `delete(String id)` - Delete seat by ID
- `findById(String id)` - Find seat by ID
- `findAll()` - Get all seats

**Custom Methods:**
- `findByCarriageId(String carriageId)` - Find all seats in a carriage, ordered by seat number
- `findAvailableByCarriageId(String carriageId)` - Find only available seats in a carriage

#### 8. TicketDAO
**Standard Methods:**
- `add(Ticket)` - Insert new ticket
- `update(Ticket)` - Update existing ticket
- `delete(String id)` - Delete ticket by ID
- `findById(String id)` - Find ticket by ID
- `findAll()` - Get all tickets

**Custom Methods:**
- `findByCustomerId(String customerId)` - Find all tickets for a customer
- `findByTrainId(String trainId)` - Find all tickets for a train
- `findByStatus(TicketStatus status)` - Find tickets by status

## Usage Examples

### Basic CRUD Operations

```java
// Create a new customer
Customer customer = new Customer("KH001", "Nguyen Van A", "0901234567", 
                                 "email@example.com", "001234567890", "123 Street");
CustomerDAO.getInstance().add(customer);

// Find a customer
Customer found = CustomerDAO.getInstance().findById("KH001");

// Update a customer
found.setPhoneNumber("0909999999");
CustomerDAO.getInstance().update(found);

// Delete a customer
CustomerDAO.getInstance().delete("KH001");

// Get all customers
List<Customer> allCustomers = CustomerDAO.getInstance().findAll();
```

### Using Object Relationships

```java
// Traditional approach (backward compatible)
Carriage carriage1 = new Carriage("C001", "SE1", "CT01", "Toa 1", 1);
CarriageDAO.getInstance().add(carriage1);

// Object-oriented approach (new feature)
Train train = TrainDAO.getInstance().findById("SE1");
CarriageType type = CarriageTypeDAO.getInstance().findById("CT01");
Carriage carriage2 = new Carriage("C002", train, type, "Toa 2", 2);
CarriageDAO.getInstance().add(carriage2);

// You can mix both approaches
Seat seat = new Seat();
seat.setSeatId("S001");
seat.setCarriage(carriage2);  // Sets both carriage object and carriageId
seat.setSeatNumber("01");
seat.setStatus(Seat.SeatStatus.AVAILABLE);
SeatDAO.getInstance().add(seat);
```

### Complex Ticket Booking Example

```java
// Find related entities
Train train = TrainDAO.getInstance().findById("SE1");
Customer customer = CustomerDAO.getInstance().findById("KH001");
Employee employee = EmployeeDAO.getInstance().findById("EMP001");
Seat seat = SeatDAO.getInstance().findById("SE1-C01-S01");
Carriage carriage = CarriageDAO.getInstance().findById("SE1-C01");

// Create ticket with object relationships
Ticket ticket = new Ticket(
    "VE001",
    train,
    customer,
    employee,
    LocalDateTime.now(),
    seat,
    carriage,
    850000.0,
    Ticket.TicketStatus.BOOKED
);
TicketDAO.getInstance().add(ticket);

// Update seat status
seat.setStatus(Seat.SeatStatus.BOOKED);
SeatDAO.getInstance().update(seat);

// Update train available seats
train.setAvailableSeats(train.getAvailableSeats() - 1);
TrainDAO.getInstance().update(train);
```

### Account Authentication

```java
Account account = AccountDAO.getInstance().authenticate("admin", "admin123");
if (account != null && account.isActive()) {
    if (account.isManager()) {
        // Admin/Manager access
    } else {
        // Regular employee access
    }
}
```

### Finding Tickets by Customer

```java
List<Ticket> customerTickets = TicketDAO.getInstance().findByCustomerId("KH001");
for (Ticket ticket : customerTickets) {
    if (ticket.getStatus() == Ticket.TicketStatus.BOOKED) {
        // Process booked ticket
    }
}
```

## Key Features

### 1. Backward Compatibility
- All existing code using string IDs continues to work
- No breaking changes to existing API
- Object references are optional enhancements

### 2. Object-Oriented Design
- Entities can reference related entities as objects
- Automatic ID synchronization when setting object references
- More intuitive and type-safe code

### 3. SQL Server Integration
- All DAOs use SQL Server database
- PreparedStatement for security (SQL injection prevention)
- Consistent error handling with printStackTrace()

### 4. Consistent Patterns
- All DAOs follow the same structure
- Standardized method names (add, update, delete, findById, findAll)
- Helper methods for ResultSet mapping

### 5. Data Integrity
- Foreign key relationships preserved
- Status enums for type safety (SeatStatus, TicketStatus)
- NULL handling for optional fields

## Database Configuration

Database connection settings are in `ConnectSql.java`:
```java
private static final String SERVER = "localhost";
private static final String PORT = "1433";
private static final String DATABASE = "QLVeTau";
private static final String USERNAME = "sa";
private static final String PASSWORD = "your_password";
```

Update these values according to your SQL Server configuration.

## Notes

1. **Thread Safety**: All DAOs use singleton pattern with synchronized getInstance() method
2. **Connection Management**: Connection is obtained once per DAO instance and reused
3. **Transaction Management**: Currently each operation is auto-committed; consider adding transaction support for complex operations
4. **Error Handling**: Currently prints stack trace; consider logging framework in production
5. **Object Relationships**: Setting object references automatically syncs ID fields, but IDs must be set manually if not using objects

## Migration from In-Memory to Database

The original implementation used in-memory HashMaps. The new implementation:
- Replaced HashMap storage with SQL Server database
- Maintains same API interface (GenericDAO)
- Existing GUI and service code requires no changes
- Data persists across application restarts

## Future Enhancements

Consider implementing:
1. Connection pooling for better performance
2. Transaction management for complex operations
3. Lazy loading for object relationships
4. Caching layer for frequently accessed data
5. Logging framework instead of printStackTrace()
6. Unit tests for all DAO operations
