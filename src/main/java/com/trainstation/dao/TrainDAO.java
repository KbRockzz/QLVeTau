package com.trainstation.dao;

import com.trainstation.model.Train;
import com.trainstation.MySQL.ConnectSql;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrainDAO implements GenericDAO<Train> {
    private static TrainDAO instance;
    private Connection connection;

    private TrainDAO() {
        connection = ConnectSql.getInstance().getConnection();
    }

    public static synchronized TrainDAO getInstance() {
        if (instance == null) {
            instance = new TrainDAO();
        }
        return instance;
    }

    @Override
    public void add(Train train) {
        String sql = "INSERT INTO Train (TrainID, TrainName, DepartureStation, ArrivalStation, DepartureTime, ArrivalTime, TotalSeats, AvailableSeats, TicketPrice) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, train.getTrainId());
            pstmt.setString(2, train.getTrainName());
            pstmt.setString(3, train.getDepartureStation());
            pstmt.setString(4, train.getArrivalStation());
            pstmt.setTimestamp(5, train.getDepartureTime() != null ? Timestamp.valueOf(train.getDepartureTime()) : null);
            pstmt.setTimestamp(6, train.getArrivalTime() != null ? Timestamp.valueOf(train.getArrivalTime()) : null);
            pstmt.setInt(7, train.getTotalSeats());
            pstmt.setInt(8, train.getAvailableSeats());
            pstmt.setBigDecimal(9, train.getTicketPrice());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Train train) {
        String sql = "UPDATE Train SET TrainName = ?, DepartureStation = ?, ArrivalStation = ?, DepartureTime = ?, ArrivalTime = ?, TotalSeats = ?, AvailableSeats = ?, TicketPrice = ? WHERE TrainID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, train.getTrainName());
            pstmt.setString(2, train.getDepartureStation());
            pstmt.setString(3, train.getArrivalStation());
            pstmt.setTimestamp(4, train.getDepartureTime() != null ? Timestamp.valueOf(train.getDepartureTime()) : null);
            pstmt.setTimestamp(5, train.getArrivalTime() != null ? Timestamp.valueOf(train.getArrivalTime()) : null);
            pstmt.setInt(6, train.getTotalSeats());
            pstmt.setInt(7, train.getAvailableSeats());
            pstmt.setBigDecimal(8, train.getTicketPrice());
            pstmt.setString(9, train.getTrainId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM Train WHERE TrainID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Train findById(String id) {
        String sql = "SELECT * FROM Train WHERE TrainID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractTrainFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Train> findAll() {
        List<Train> trains = new ArrayList<>();
        String sql = "SELECT * FROM Train";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                trains.add(extractTrainFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trains;
    }

    public List<Train> findByRoute(String departureStation, String arrivalStation) {
        List<Train> trains = new ArrayList<>();
        String sql = "SELECT * FROM Train WHERE DepartureStation = ? AND ArrivalStation = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, departureStation);
            pstmt.setString(2, arrivalStation);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                trains.add(extractTrainFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trains;
    }

    private Train extractTrainFromResultSet(ResultSet rs) throws SQLException {
        Train train = new Train();
        train.setTrainId(rs.getString("TrainID"));
        train.setTrainName(rs.getString("TrainName"));
        train.setDepartureStation(rs.getString("DepartureStation"));
        train.setArrivalStation(rs.getString("ArrivalStation"));
        Timestamp departureTime = rs.getTimestamp("DepartureTime");
        if (departureTime != null) {
            train.setDepartureTime(departureTime.toLocalDateTime());
        }
        Timestamp arrivalTime = rs.getTimestamp("ArrivalTime");
        if (arrivalTime != null) {
            train.setArrivalTime(arrivalTime.toLocalDateTime());
        }
        train.setTotalSeats(rs.getInt("TotalSeats"));
        train.setAvailableSeats(rs.getInt("AvailableSeats"));
        train.setTicketPrice(rs.getBigDecimal("TicketPrice"));
        return train;
    }
}
