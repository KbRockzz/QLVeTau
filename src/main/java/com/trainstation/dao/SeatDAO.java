package com.trainstation.dao;

import com.trainstation.MySQL.ConnectSql;
import com.trainstation.model.Seat;
import com.trainstation.model.Seat.SeatStatus;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeatDAO implements GenericDAO<Seat> {
    private static SeatDAO instance;
    private Connection connection;

    private SeatDAO() {
        connection = ConnectSql.getInstance().getConnection();
    }

    public static synchronized SeatDAO getInstance() {
        if (instance == null) {
            instance = new SeatDAO();
        }
        return instance;
    }

    @Override
    public void add(Seat seat) {
        String sql = "INSERT INTO Seat (SeatID, CarriageID, SeatNumber, Status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, seat.getSeatId());
            pstmt.setString(2, seat.getCarriageId());
            pstmt.setString(3, seat.getSeatNumber());
            pstmt.setString(4, seat.getStatus().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Seat seat) {
        String sql = "UPDATE Seat SET CarriageID = ?, SeatNumber = ?, Status = ? WHERE SeatID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, seat.getCarriageId());
            pstmt.setString(2, seat.getSeatNumber());
            pstmt.setString(3, seat.getStatus().toString());
            pstmt.setString(4, seat.getSeatId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM Seat WHERE SeatID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Seat findById(String id) {
        String sql = "SELECT * FROM Seat WHERE SeatID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractSeatFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Seat> findAll() {
        List<Seat> seats = new ArrayList<>();
        String sql = "SELECT * FROM Seat";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                seats.add(extractSeatFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seats;
    }

    public List<Seat> findByCarriageId(String carriageId) {
        List<Seat> seats = new ArrayList<>();
        String sql = "SELECT * FROM Seat WHERE CarriageID = ? ORDER BY SeatNumber";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, carriageId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                seats.add(extractSeatFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seats;
    }

    public List<Seat> findAvailableByCarriageId(String carriageId) {
        List<Seat> seats = new ArrayList<>();
        String sql = "SELECT * FROM Seat WHERE CarriageID = ? AND Status = 'AVAILABLE' ORDER BY SeatNumber";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, carriageId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                seats.add(extractSeatFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seats;
    }

    private Seat extractSeatFromResultSet(ResultSet rs) throws SQLException {
        Seat seat = new Seat();
        seat.setSeatId(rs.getString("SeatID"));
        seat.setCarriageId(rs.getString("CarriageID"));
        seat.setSeatNumber(rs.getString("SeatNumber"));
        seat.setStatus(SeatStatus.valueOf(rs.getString("Status")));
        return seat;
    }
}
