package com.trainstation.model;

import java.io.Serializable;

public class Seat implements Serializable {
    public enum SeatStatus {
        AVAILABLE, BOOKED
    }

    private String seatId;
    private String carriageId;
    private String seatNumber;
    private SeatStatus status;
    
    private Carriage carriage;

    public Seat() {
    }

    public Seat(String seatId, String carriageId, String seatNumber, SeatStatus status) {
        this.seatId = seatId;
        this.carriageId = carriageId;
        this.seatNumber = seatNumber;
        this.status = status;
    }

    public Seat(String seatId, Carriage carriage, String seatNumber, SeatStatus status) {
        this.seatId = seatId;
        this.carriage = carriage;
        if (carriage != null) {
            this.carriageId = carriage.getCarriageId();
        }
        this.seatNumber = seatNumber;
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

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public void setStatus(SeatStatus status) {
        this.status = status;
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
        return "Seat{" +
                "seatId='" + seatId + '\'' +
                ", carriageId='" + carriageId + '\'' +
                ", seatNumber='" + seatNumber + '\'' +
                ", status=" + status +
                '}';
    }
}
