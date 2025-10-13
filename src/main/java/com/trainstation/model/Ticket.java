package com.trainstation.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Ticket implements Serializable {
    private String ticketId;
    private String trainId;
    private String customerId;
    private String employeeId;
    private LocalDateTime bookingDate;
    private String seatNumber;
    private String seatId;
    private String carriageId;
    private BigDecimal price;
    private String status;
    
    private Train train;
    private Customer customer;
    private Employee employee;
    private Seat seat;
    private Carriage carriage;

    public Ticket() {
    }

    public Ticket(String ticketId, String trainId, String customerId, String employeeId, 
                  LocalDateTime bookingDate, String seatNumber, String seatId, String carriageId,
                  BigDecimal price, String status) {
        this.ticketId = ticketId;
        this.trainId = trainId;
        this.customerId = customerId;
        this.employeeId = employeeId;
        this.bookingDate = bookingDate;
        this.seatNumber = seatNumber;
        this.seatId = seatId;
        this.carriageId = carriageId;
        this.price = price;
        this.status = status;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getTrainId() {
        return trainId;
    }

    public void setTrainId(String trainId) {
        this.trainId = trainId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSeatId() {
        return seatId;
    }

    public void setSeatId(String seatId) {
        this.seatId = seatId;
    }

    public String getCarriageId() {
        return carriageId;
    }

    public void setCarriageId(String carriageId) {
        this.carriageId = carriageId;
    }

    public Train getTrain() {
        return train;
    }

    public void setTrain(Train train) {
        this.train = train;
        if (train != null) {
            this.trainId = train.getTrainId();
        }
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        if (customer != null) {
            this.customerId = customer.getCustomerId();
        }
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
        if (employee != null) {
            this.employeeId = employee.getEmployeeId();
        }
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
        if (seat != null) {
            this.seatId = seat.getSeatId();
            this.seatNumber = seat.getSeatNumber();
        }
    }

    public Carriage getCarriage() {
        return carriage;
    }

    public void setCarriage(Carriage carriage) {
        this.carriage = carriage;
        if (carriage != null) {
            this.carriageId = carriage.getCarriageId();
        }
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "ticketId='" + ticketId + '\'' +
                ", trainId='" + trainId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", employeeId='" + employeeId + '\'' +
                ", bookingDate=" + bookingDate +
                ", seatNumber='" + seatNumber + '\'' +
                ", seatId='" + seatId + '\'' +
                ", carriageId='" + carriageId + '\'' +
                ", price=" + price +
                ", status=" + status +
                '}';
    }
}
