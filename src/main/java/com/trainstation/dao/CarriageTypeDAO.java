package com.trainstation.dao;

import com.trainstation.MySQL.ConnectSql;
import com.trainstation.model.CarriageType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarriageTypeDAO implements GenericDAO<CarriageType> {
    private static CarriageTypeDAO instance;

    private CarriageTypeDAO() {
    }

    public static synchronized CarriageTypeDAO getInstance() {
        if (instance == null) {
            instance = new CarriageTypeDAO();
        }
        return instance;
    }

    @Override
    public void add(CarriageType carriageType) {
        String sql = "INSERT INTO CarriageType (CarriageTypeID, TypeName, SeatCount, PriceMultiplier) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, carriageType.getCarriageTypeId());
            stmt.setString(2, carriageType.getTypeName());
            stmt.setInt(3, carriageType.getSeatCount());
            stmt.setDouble(4, carriageType.getPriceMultiplier());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(CarriageType carriageType) {
        String sql = "UPDATE CarriageType SET TypeName = ?, SeatCount = ?, PriceMultiplier = ? WHERE CarriageTypeID = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, carriageType.getTypeName());
            stmt.setInt(2, carriageType.getSeatCount());
            stmt.setDouble(3, carriageType.getPriceMultiplier());
            stmt.setString(4, carriageType.getCarriageTypeId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM CarriageType WHERE CarriageTypeID = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CarriageType findById(String id) {
        String sql = "SELECT * FROM CarriageType WHERE CarriageTypeID = ?";
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
    public List<CarriageType> findAll() {
        List<CarriageType> carriageTypes = new ArrayList<>();
        String sql = "SELECT * FROM CarriageType";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                carriageTypes.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return carriageTypes;
    }

    private CarriageType mapResultSet(ResultSet rs) throws SQLException {
        return new CarriageType(
            rs.getString("CarriageTypeID"),
            rs.getString("TypeName"),
            rs.getInt("SeatCount"),
            rs.getDouble("PriceMultiplier")
        );
    }
}
