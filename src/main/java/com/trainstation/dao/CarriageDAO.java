package com.trainstation.dao;

import com.trainstation.MySQL.ConnectSql;
import com.trainstation.model.Carriage;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarriageDAO implements GenericDAO<Carriage> {
    private static CarriageDAO instance;
    private Connection connection;

    private CarriageDAO() {
        connection = ConnectSql.getInstance().getConnection();
    }

    public static synchronized CarriageDAO getInstance() {
        if (instance == null) {
            instance = new CarriageDAO();
        }
        return instance;
    }

    @Override
    public void add(Carriage carriage) {
        String sql = "INSERT INTO ToaTau (CarriageID, TrainID, CarriageTypeID, CarriageName, CarriageNumber) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, carriage.getCarriageId());
            pstmt.setString(2, carriage.getTrainId());
            pstmt.setString(3, carriage.getCarriageTypeId());
            pstmt.setString(4, carriage.getCarriageName());
            pstmt.setInt(5, carriage.getCarriageNumber());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Carriage carriage) {
        String sql = "UPDATE ToaTau SET TrainID = ?, CarriageTypeID = ?, CarriageName = ?, CarriageNumber = ? WHERE CarriageID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, carriage.getTrainId());
            pstmt.setString(2, carriage.getCarriageTypeId());
            pstmt.setString(3, carriage.getCarriageName());
            pstmt.setInt(4, carriage.getCarriageNumber());
            pstmt.setString(5, carriage.getCarriageId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM ToaTau WHERE CarriageID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Carriage findById(String id) {
        String sql = "SELECT * FROM ToaTau WHERE CarriageID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractCarriageFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Carriage> findAll() {
        List<Carriage> carriages = new ArrayList<>();
        String sql = "SELECT * FROM ToaTau";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                carriages.add(extractCarriageFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return carriages;
    }

    public List<Carriage> findByTrainId(String trainId) {
        List<Carriage> carriages = new ArrayList<>();
        String sql = "SELECT * FROM ToaTau WHERE TrainID = ? ORDER BY CarriageNumber";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, trainId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                carriages.add(extractCarriageFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return carriages;
    }

    private Carriage extractCarriageFromResultSet(ResultSet rs) throws SQLException {
        Carriage carriage = new Carriage();
        carriage.setCarriageId(rs.getString("CarriageID"));
        carriage.setTrainId(rs.getString("TrainID"));
        carriage.setCarriageTypeId(rs.getString("CarriageTypeID"));
        carriage.setCarriageName(rs.getString("CarriageName"));
        carriage.setCarriageNumber(rs.getInt("CarriageNumber"));
        return carriage;
    }
}
