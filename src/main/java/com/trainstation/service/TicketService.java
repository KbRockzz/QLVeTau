package com.trainstation.service;

import com.trainstation.dao.TicketDAO;
import com.trainstation.dao.TrainDAO;
import com.trainstation.model.Ticket;
import com.trainstation.model.Ticket.TicketStatus;
import com.trainstation.model.Train;
import java.time.LocalDateTime;
import java.util.List;

public class TicketService {
    private static TicketService instance;
    private final TicketDAO ticketDAO;
    private final TrainDAO trainDAO;

    private TicketService() {
        this.ticketDAO = TicketDAO.getInstance();
        this.trainDAO = TrainDAO.getInstance();
    }

    public static synchronized TicketService getInstance() {
        if (instance == null) {
            instance = new TicketService();
        }
        return instance;
    }

    public Ticket bookTicket(String trainId, String customerId, String employeeId, String seatNumber) {
        Train train = trainDAO.findById(trainId);
        if (train == null) {
            throw new IllegalArgumentException("Train not found");
        }
        
        if (train.getAvailableSeats() <= 0) {
            throw new IllegalStateException("No available seats");
        }

        String ticketId = "TKT" + System.currentTimeMillis();
        Ticket ticket = new Ticket(
            ticketId,
            trainId,
            customerId,
            employeeId,
            LocalDateTime.now(),
            seatNumber,
            train.getTicketPrice(),
            TicketStatus.BOOKED
        );

        ticketDAO.add(ticket);
        train.setAvailableSeats(train.getAvailableSeats() - 1);
        trainDAO.update(train);

        return ticket;
    }

    public void refundTicket(String ticketId) {
        Ticket ticket = ticketDAO.findById(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket not found");
        }

        if (ticket.getStatus() != TicketStatus.BOOKED) {
            throw new IllegalStateException("Ticket cannot be refunded");
        }

        ticket.setStatus(TicketStatus.REFUNDED);
        ticketDAO.update(ticket);

        Train train = trainDAO.findById(ticket.getTrainId());
        if (train != null) {
            train.setAvailableSeats(train.getAvailableSeats() + 1);
            trainDAO.update(train);
        }
    }

    public void cancelTicket(String ticketId) {
        Ticket ticket = ticketDAO.findById(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket not found");
        }

        if (ticket.getStatus() != TicketStatus.BOOKED) {
            throw new IllegalStateException("Ticket cannot be cancelled");
        }

        ticket.setStatus(TicketStatus.CANCELLED);
        ticketDAO.update(ticket);

        Train train = trainDAO.findById(ticket.getTrainId());
        if (train != null) {
            train.setAvailableSeats(train.getAvailableSeats() + 1);
            trainDAO.update(train);
        }
    }

    public List<Ticket> getAllTickets() {
        return ticketDAO.findAll();
    }

    public List<Ticket> getTicketsByCustomer(String customerId) {
        return ticketDAO.findByCustomerId(customerId);
    }

    public List<Ticket> getTicketsByTrain(String trainId) {
        return ticketDAO.findByTrainId(trainId);
    }

    public Ticket getTicketById(String ticketId) {
        return ticketDAO.findById(ticketId);
    }

    public void changeTicket(String ticketId, String newTrainId, String newSeatNumber, String newSeatId, String newCarriageId) {
        Ticket ticket = ticketDAO.findById(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket not found");
        }

        if (ticket.getStatus() != TicketStatus.BOOKED) {
            throw new IllegalStateException("Only booked tickets can be changed");
        }

        // Release old seat
        Train oldTrain = trainDAO.findById(ticket.getTrainId());
        if (oldTrain != null) {
            oldTrain.setAvailableSeats(oldTrain.getAvailableSeats() + 1);
            trainDAO.update(oldTrain);
        }

        // Get new train and check availability
        Train newTrain = trainDAO.findById(newTrainId);
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
