package com.trainstation.service;

import com.trainstation.dao.TicketDAO;
import com.trainstation.model.Ticket;
import com.trainstation.model.Ticket.TicketStatus;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsService {
    private static StatisticsService instance;
    private final TicketDAO ticketDAO;

    private StatisticsService() {
        this.ticketDAO = TicketDAO.getInstance();
    }

    public static synchronized StatisticsService getInstance() {
        if (instance == null) {
            instance = new StatisticsService();
        }
        return instance;
    }

    public double getTotalRevenue() {
        List<Ticket> tickets = ticketDAO.findByStatus(TicketStatus.BOOKED);
        return tickets.stream()
                .mapToDouble(Ticket::getPrice)
                .sum();
    }

    public int getTotalTicketsSold() {
        return ticketDAO.findByStatus(TicketStatus.BOOKED).size();
    }

    public int getTotalTicketsRefunded() {
        return ticketDAO.findByStatus(TicketStatus.REFUNDED).size();
    }

    public int getTotalTicketsCancelled() {
        return ticketDAO.findByStatus(TicketStatus.CANCELLED).size();
    }

    public Map<String, Integer> getTicketsByTrain() {
        Map<String, Integer> trainTickets = new HashMap<>();
        List<Ticket> allTickets = ticketDAO.findAll();
        
        for (Ticket ticket : allTickets) {
            if (ticket.getStatus() == TicketStatus.BOOKED) {
                String trainId = ticket.getTrainId();
                trainTickets.put(trainId, trainTickets.getOrDefault(trainId, 0) + 1);
            }
        }
        
        return trainTickets;
    }

    public Map<String, Object> getAllStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRevenue", getTotalRevenue());
        stats.put("ticketsSold", getTotalTicketsSold());
        stats.put("ticketsRefunded", getTotalTicketsRefunded());
        stats.put("ticketsCancelled", getTotalTicketsCancelled());
        stats.put("ticketsByTrain", getTicketsByTrain());
        return stats;
    }
}
