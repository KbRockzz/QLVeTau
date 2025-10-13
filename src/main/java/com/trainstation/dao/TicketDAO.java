package com.trainstation.dao;

import com.trainstation.model.Ticket;
import com.trainstation.model.Ticket.TicketStatus;
import com.trainstation.MySQL.ConnectSql;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO implements GenericDAO<Ticket> {
    private static TicketDAO instance;
    private Connection connection;

    private TicketDAO() {
        connection = ConnectSql.getInstance().getConnection();
    }

    public static synchronized TicketDAO getInstance() {
        if (instance == null) {
            instance = new TicketDAO();
        }
        return instance;
    }

    @Override
    public void add(Ticket ticket) {
        String sql = "INSERT INTO Ve (TicketID, TrainID, CustomerID, EmployeeID, BookingDate, SeatNumber, SeatID, CarriageID, Price, Status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, ticket.getTicketId());
            pstmt.setString(2, ticket.getTrainId());
            pstmt.setString(3, ticket.getCustomerId());
            pstmt.setString(4, ticket.getEmployeeId());
            pstmt.setTimestamp(5, ticket.getBookingDate() != null ? Timestamp.valueOf(ticket.getBookingDate()) : null);
            pstmt.setString(6, ticket.getSeatNumber());
            pstmt.setString(7, ticket.getSeatId());
            pstmt.setString(8, ticket.getCarriageId());
            pstmt.setDouble(9, ticket.getPrice());
            pstmt.setString(10, ticket.getStatus().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Ticket ticket) {
        String sql = "UPDATE Ve SET TrainID = ?, CustomerID = ?, EmployeeID = ?, BookingDate = ?, SeatNumber = ?, SeatID = ?, CarriageID = ?, Price = ?, Status = ? WHERE TicketID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, ticket.getTrainId());
            pstmt.setString(2, ticket.getCustomerId());
            pstmt.setString(3, ticket.getEmployeeId());
            pstmt.setTimestamp(4, ticket.getBookingDate() != null ? Timestamp.valueOf(ticket.getBookingDate()) : null);
            pstmt.setString(5, ticket.getSeatNumber());
            pstmt.setString(6, ticket.getSeatId());
            pstmt.setString(7, ticket.getCarriageId());
            pstmt.setDouble(8, ticket.getPrice());
            pstmt.setString(9, ticket.getStatus().toString());
            pstmt.setString(10, ticket.getTicketId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM Ve WHERE TicketID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Ticket findById(String id) {
        String sql = "SELECT * FROM Ve WHERE TicketID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractTicketFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Ticket> findAll() {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM Ve";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tickets.add(extractTicketFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tickets;
    }

    public List<Ticket> findByCustomerId(String customerId) {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM Ve WHERE CustomerID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                tickets.add(extractTicketFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tickets;
    }

    public List<Ticket> findByTrainId(String trainId) {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM Ve WHERE TrainID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, trainId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                tickets.add(extractTicketFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tickets;
    }

    public List<Ticket> findByStatus(TicketStatus status) {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM Ve WHERE Status = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status.toString());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                tickets.add(extractTicketFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tickets;
    }

    private Ticket extractTicketFromResultSet(ResultSet rs) throws SQLException {
        Ticket ticket = new Ticket();
        ticket.setTicketId(rs.getString("TicketID"));
        ticket.setTrainId(rs.getString("TrainID"));
        ticket.setCustomerId(rs.getString("CustomerID"));
        ticket.setEmployeeId(rs.getString("EmployeeID"));
        Timestamp bookingDate = rs.getTimestamp("BookingDate");
        if (bookingDate != null) {
            ticket.setBookingDate(bookingDate.toLocalDateTime());
        }
        ticket.setSeatNumber(rs.getString("SeatNumber"));
        ticket.setSeatId(rs.getString("SeatID"));
        ticket.setCarriageId(rs.getString("CarriageID"));
        ticket.setPrice(rs.getDouble("Price"));
        ticket.setStatus(TicketStatus.valueOf(rs.getString("Status")));
        return ticket;
    }
}
