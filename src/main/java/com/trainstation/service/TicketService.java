package com.trainstation.service;

import com.trainstation.dao.VeDAO;
import com.trainstation.dao.TauDAO;
import com.trainstation.model.Ve;
import com.trainstation.model.Tau;
import java.time.LocalDateTime;
import java.util.List;

public class TicketService {
    private static TicketService instance;
    private final VeDAO ticketDAO;
    private final TauDAO trainDAO;

    private TicketService() {
        this.ticketDAO = VeDAO.getInstance();
        this.trainDAO = TauDAO.getInstance();
    }

    public static synchronized TicketService getInstance() {
        if (instance == null) {
            instance = new TicketService();
        }
        return instance;
    }

    public Ve bookTicket(String trainId, String customerId, String employeeId, String seatNumber) {
        Tau train = trainDAO.findById(trainId);
        if (train == null) {
            throw new IllegalArgumentException("Train not found");
        }
        
        if (train.getAvailableSeats() <= 0) {
            throw new IllegalStateException("No available seats");
        }

        String ticketId = "TKT" + System.currentTimeMillis();
        Ve ticket = new Ve(
            ticketId,
            trainId,
            customerId,
            employeeId,
            LocalDateTime.now(),
            seatNumber,
            null,
            null,
            train.getTicketPrice(),
            "BOOKED"
        );

        ticketDAO.add(ticket);
        train.setAvailableSeats(train.getAvailableSeats() - 1);
        trainDAO.update(train);

        return ticket;
    }

    public void refundTicket(String ticketId) {
        Ve ticket = ticketDAO.findById(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket not found");
        }

        if (!"BOOKED".equals(ticket.getStatus())) {
            throw new IllegalStateException("Ticket cannot be refunded");
        }

        ticket.setStatus("REFUNDED");
        ticketDAO.update(ticket);

        Tau train = trainDAO.findById(ticket.getTrainId());
        if (train != null) {
            train.setAvailableSeats(train.getAvailableSeats() + 1);
            trainDAO.update(train);
        }
    }

    public void cancelTicket(String ticketId) {
        Ve ticket = ticketDAO.findById(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket not found");
        }

        if (!"BOOKED".equals(ticket.getStatus())) {
            throw new IllegalStateException("Ticket cannot be cancelled");
        }

        ticket.setStatus("CANCELLED");
        ticketDAO.update(ticket);

        Tau train = trainDAO.findById(ticket.getTrainId());
        if (train != null) {
            train.setAvailableSeats(train.getAvailableSeats() + 1);
            trainDAO.update(train);
        }
    }

    public List<Ve> getAllTickets() {
        return ticketDAO.findAll();
    }

    public List<Ve> getTicketsByCustomer(String customerId) {
        return ticketDAO.findByCustomerId(customerId);
    }

    public List<Ve> getTicketsByTrain(String trainId) {
        return ticketDAO.findByTrainId(trainId);
    }

    public Ve getTicketById(String ticketId) {
        return ticketDAO.findById(ticketId);
    }

    public void changeTicket(String ticketId, String newTrainId, String newSeatNumber, String newSeatId, String newCarriageId) {
        Ve ticket = ticketDAO.findById(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket not found");
        }

        if (!"BOOKED".equals(ticket.getStatus())) {
            throw new IllegalStateException("Only booked tickets can be changed");
        }

        // Release old seat
        Tau oldTrain = trainDAO.findById(ticket.getTrainId());
        if (oldTrain != null) {
            oldTrain.setAvailableSeats(oldTrain.getAvailableSeats() + 1);
            trainDAO.update(oldTrain);
        }

        // Get new train and check availability
        Tau newTrain = trainDAO.findById(newTrainId);
        if (newTrain == null) {
            throw new IllegalArgumentException("New train not found");
        }

        if (newTrain.getAvailableSeats() <= 0) {
            throw new IllegalStateException("No available seats on new train");
        }

        // Update ticket with new train and seat
        ticket.setTrainId(newTrainId);
        ticket.setSeatNumber(newSeatNumber);
        ticket.setSeatId(newSeatId);
        ticket.setCarriageId(newCarriageId);
        ticket.setPrice(newTrain.getTicketPrice());
        ticketDAO.update(ticket);

        // Update new train's available seats
        newTrain.setAvailableSeats(newTrain.getAvailableSeats() - 1);
        trainDAO.update(newTrain);
    }
}
