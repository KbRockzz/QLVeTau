package com.trainstation.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Train implements Serializable {
    private String trainId;
    private String trainName;
    private String departureStation;
    private String arrivalStation;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private int totalSeats;
    private int availableSeats;
    private BigDecimal ticketPrice;

    public Train() {
    }

    public Train(String trainId, String trainName, String departureStation, String arrivalStation, 
                 LocalDateTime departureTime, LocalDateTime arrivalTime, int totalSeats, int availableSeats, BigDecimal ticketPrice) {
        this.trainId = trainId;
        this.trainName = trainName;
        this.departureStation = departureStation;
        this.arrivalStation = arrivalStation;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
        this.ticketPrice = ticketPrice;
    }

    public String getTrainId() {
        return trainId;
    }

    public void setTrainId(String trainId) {
        this.trainId = trainId;
    }

    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public String getDepartureStation() {
        return departureStation;
    }

    public void setDepartureStation(String departureStation) {
        this.departureStation = departureStation;
    }

    public String getArrivalStation() {
        return arrivalStation;
    }

    public void setArrivalStation(String arrivalStation) {
        this.arrivalStation = arrivalStation;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public BigDecimal getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(BigDecimal ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    @Override
    public String toString() {
        return "Train{" +
                "trainId='" + trainId + '\'' +
                ", trainName='" + trainName + '\'' +
                ", departureStation='" + departureStation + '\'' +
                ", arrivalStation='" + arrivalStation + '\'' +
                ", departureTime=" + departureTime +
                ", arrivalTime=" + arrivalTime +
                ", totalSeats=" + totalSeats +
                ", availableSeats=" + availableSeats +
                ", ticketPrice=" + ticketPrice +
                '}';
    }
}
