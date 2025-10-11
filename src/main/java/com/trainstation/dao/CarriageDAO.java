package com.trainstation.dao;

import com.trainstation.MySQL.ConnectSql;
import com.trainstation.model.Carriage;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarriageDAO implements GenericDAO<Carriage> {
    private static CarriageDAO instance;

    private CarriageDAO() {
    }

    public static synchronized CarriageDAO getInstance() {
        if (instance == null) {
            instance = new CarriageDAO();
        }
        return instance;
    }

    @Override
    public void add(Carriage carriage) {
        String sql = "INSERT INTO Carriage (CarriageID, TrainID, CarriageTypeID, CarriageName, CarriageNumber) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, carriage.getCarriageId());
            stmt.setString(2, carriage.getTrainId());
            stmt.setString(3, carriage.getCarriageTypeId());
            stmt.setString(4, carriage.getCarriageName());
            stmt.setInt(5, carriage.getCarriageNumber());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Carriage carriage) {
        String sql = "UPDATE Carriage SET TrainID = ?, CarriageTypeID = ?, CarriageName = ?, CarriageNumber = ? WHERE CarriageID = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, carriage.getTrainId());
            stmt.setString(2, carriage.getCarriageTypeId());
            stmt.setString(3, carriage.getCarriageName());
            stmt.setInt(4, carriage.getCarriageNumber());
            stmt.setString(5, carriage.getCarriageId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM Carriage WHERE CarriageID = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Carriage findById(String id) {
        String sql = "SELECT * FROM Carriage WHERE CarriageID = ?";
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
    public List<Carriage> findAll() {
        List<Carriage> carriages = new ArrayList<>();
        String sql = "SELECT * FROM Carriage";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                carriages.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return carriages;
    }

    public List<Carriage> findByTrainId(String trainId) {
        List<Carriage> carriages = new ArrayList<>();
        String sql = "SELECT * FROM Carriage WHERE TrainID = ? ORDER BY CarriageNumber";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, trainId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                carriages.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return carriages;
    }

    private Carriage mapResultSet(ResultSet rs) throws SQLException {
        return new Carriage(
            rs.getString("CarriageID"),
            rs.getString("TrainID"),
            rs.getString("CarriageTypeID"),
            rs.getString("CarriageName"),
            rs.getInt("CarriageNumber")
        );
    }
}
