package com.trainstation.dao;

import com.trainstation.model.Ticket;
import com.trainstation.model.Ticket.TicketStatus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketDAO implements GenericDAO<Ticket> {
    private static TicketDAO instance;
    private Map<String, Ticket> tickets;

    private TicketDAO() {
        tickets = new HashMap<>();
    }

    public static synchronized TicketDAO getInstance() {
        if (instance == null) {
            instance = new TicketDAO();
        }
        return instance;
    }

    @Override
    public void add(Ticket ticket) {
        tickets.put(ticket.getTicketId(), ticket);
    }

    @Override
    public void update(Ticket ticket) {
        tickets.put(ticket.getTicketId(), ticket);
    }

    @Override
    public void delete(String id) {
        tickets.remove(id);
    }

    @Override
    public Ticket findById(String id) {
        return tickets.get(id);
    }

    @Override
    public List<Ticket> findAll() {
        return new ArrayList<>(tickets.values());
    }

    public List<Ticket> findByCustomerId(String customerId) {
        List<Ticket> result = new ArrayList<>();
        for (Ticket ticket : tickets.values()) {
            if (ticket.getCustomerId().equals(customerId)) {
                result.add(ticket);
            }
        }
        return result;
    }

    public List<Ticket> findByTrainId(String trainId) {
        List<Ticket> result = new ArrayList<>();
        for (Ticket ticket : tickets.values()) {
            if (ticket.getTrainId().equals(trainId)) {
                result.add(ticket);
            }
        }
        return result;
    }

    public List<Ticket> findByStatus(TicketStatus status) {
        List<Ticket> result = new ArrayList<>();
        for (Ticket ticket : tickets.values()) {
            if (ticket.getStatus() == status) {
                result.add(ticket);
            }
        }
        return result;
    }
}
