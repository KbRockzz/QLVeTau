package com.trainstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Ticket implements Serializable {
    public enum TicketStatus {
        BOOKED, CANCELLED, REFUNDED
    }

    private String ticketId;
    private String trainId;
    private String customerId;
    private String employeeId;
    private LocalDateTime bookingDate;
    private String seatNumber;
    private double price;
    private TicketStatus status;

    public Ticket() {
    }

    public Ticket(String ticketId, String trainId, String customerId, String employeeId, 
                  LocalDateTime bookingDate, String seatNumber, double price, TicketStatus status) {
        this.ticketId = ticketId;
        this.trainId = trainId;
        this.customerId = customerId;
        this.employeeId = employeeId;
        this.bookingDate = bookingDate;
        this.seatNumber = seatNumber;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
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
                ", price=" + price +
                ", status=" + status +
                '}';
    }
}
