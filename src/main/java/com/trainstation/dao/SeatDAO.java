package com.trainstation.dao;

import com.trainstation.MySQL.ConnectSql;
import com.trainstation.model.Seat;
import com.trainstation.model.Seat.SeatStatus;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeatDAO implements GenericDAO<Seat> {
    private static SeatDAO instance;

    private SeatDAO() {
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
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, seat.getSeatId());
            stmt.setString(2, seat.getCarriageId());
            stmt.setString(3, seat.getSeatNumber());
            stmt.setString(4, seat.getStatus().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Seat seat) {
        String sql = "UPDATE Seat SET CarriageID = ?, SeatNumber = ?, Status = ? WHERE SeatID = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, seat.getCarriageId());
            stmt.setString(2, seat.getSeatNumber());
            stmt.setString(3, seat.getStatus().toString());
            stmt.setString(4, seat.getSeatId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM Seat WHERE SeatID = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Seat findById(String id) {
        String sql = "SELECT * FROM Seat WHERE SeatID = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
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
        try (Connection conn = ConnectSql.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                seats.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seats;
    }

    public List<Seat> findByCarriageId(String carriageId) {
        List<Seat> seats = new ArrayList<>();
        String sql = "SELECT * FROM Seat WHERE CarriageID = ? ORDER BY SeatNumber";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, carriageId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                seats.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seats;
    }

    public List<Seat> findAvailableByCarriageId(String carriageId) {
        List<Seat> seats = new ArrayList<>();
        String sql = "SELECT * FROM Seat WHERE CarriageID = ? AND Status = 'AVAILABLE' ORDER BY SeatNumber";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, carriageId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                seats.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seats;
    }

    private Seat mapResultSet(ResultSet rs) throws SQLException {
        return new Seat(
            rs.getString("SeatID"),
            rs.getString("CarriageID"),
            rs.getString("SeatNumber"),
            SeatStatus.valueOf(rs.getString("Status"))
        );
    }
}
