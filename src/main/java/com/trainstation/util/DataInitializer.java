package com.trainstation.util;

import com.trainstation.dao.*;
import com.trainstation.model.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * Utility class to initialize sample data for the application.
 * This helps users get started quickly with demo data.
 */
public class DataInitializer {
    
    public static void initializeSampleData() {
        initializeEmployees();
        initializeCustomers();
        initializeTrains();
    }
    
    private static void initializeEmployees() {
        EmployeeDAO employeeDAO = EmployeeDAO.getInstance();
        
        // Admin employee with manager role (LNV03)
        Employee admin = new Employee(
            "EMP001",
            "Nguyễn Văn Admin",
            "0901234567",
            "admin@trainstation.com",
            "Quản lý",
            new BigDecimal("15000000"),
            LocalDate.of(2020, 1, 1),
            "LNV03"
        );
        employeeDAO.add(admin);
        
        // Sample employees with regular employee role (LNV01)
        Employee emp1 = new Employee(
            "EMP002",
            "Trần Thị Bình",
            "0902345678",
            "binh@trainstation.com",
            "Nhân viên bán vé",
            new BigDecimal("10000000"),
            LocalDate.of(2021, 3, 15),
            "LNV01"
        );
        employeeDAO.add(emp1);
        
        Employee emp2 = new Employee(
            "EMP003",
            "Lê Văn Cường",
            "0903456789",
            "cuong@trainstation.com",
            "Nhân viên bán vé",
            new BigDecimal("9500000"),
            LocalDate.of(2022, 6, 1),
            "LNV02"
        );
        employeeDAO.add(emp2);
    }
    
    private static void initializeCustomers() {
        CustomerDAO customerDAO = CustomerDAO.getInstance();
        
        Customer c1 = new Customer(
            "KH001",
            "Phạm Minh Anh",
            "0911111111",
            "anh@email.com",
            "001234567890",
            "123 Lê Lợi, Q1, TP.HCM"
        );
        customerDAO.add(c1);
        
        Customer c2 = new Customer(
            "KH002",
            "Võ Thị Mai",
            "0922222222",
            "mai@email.com",
            "001234567891",
            "456 Nguyễn Huệ, Q1, TP.HCM"
        );
        customerDAO.add(c2);
        
        Customer c3 = new Customer(
            "KH003",
            "Hoàng Văn Nam",
            "0933333333",
            "nam@email.com",
            "001234567892",
            "789 Trần Hưng Đạo, Hà Nội"
        );
        customerDAO.add(c3);
    }
    
    private static void initializeTrains() {
        TrainDAO trainDAO = TrainDAO.getInstance();
        
        // Sài Gòn - Hà Nội
        Train t1 = new Train(
            "SE1",
            "Thống Nhất",
            "Sài Gòn",
            "Hà Nội",
            LocalDateTime.of(2024, 12, 20, 19, 30),
            LocalDateTime.of(2024, 12, 21, 4, 0),
            200,
            200,
            new BigDecimal("850000")
        );
        trainDAO.add(t1);
        
        // Hà Nội - Sài Gòn
        Train t2 = new Train(
            "SE2",
            "Thống Nhất",
            "Hà Nội",
            "Sài Gòn",
            LocalDateTime.of(2024, 12, 20, 19, 0),
            LocalDateTime.of(2024, 12, 21, 3, 30),
            200,
            200,
            new BigDecimal("850000")
        );
        trainDAO.add(t2);
        
        // Sài Gòn - Nha Trang
        Train t3 = new Train(
            "SNT1",
            "Sài Gòn - Nha Trang",
            "Sài Gòn",
            "Nha Trang",
            LocalDateTime.of(2024, 12, 21, 6, 15),
            LocalDateTime.of(2024, 12, 21, 14, 0),
            150,
            150,
            new BigDecimal("350000")
        );
        trainDAO.add(t3);
        
        // Hà Nội - Hải Phòng
        Train t4 = new Train(
            "HP1",
            "Hà Nội - Hải Phòng",
            "Hà Nội",
            "Hải Phòng",
            LocalDateTime.of(2024, 12, 21, 8, 0),
            LocalDateTime.of(2024, 12, 21, 10, 30),
            120,
            120,
            new BigDecimal("100000")
        );
        trainDAO.add(t4);
        
        // Sài Gòn - Đà Nẵng
        Train t5 = new Train(
            "DN1",
            "Sài Gòn - Đà Nẵng",
            "Sài Gòn",
            "Đà Nẵng",
            LocalDateTime.of(2024, 12, 22, 7, 0),
            LocalDateTime.of(2024, 12, 22, 19, 30),
            180,
            180,
            new BigDecimal("550000")
        );
        trainDAO.add(t5);
    }
}
