package com.trainstation.dao;

import com.trainstation.MySQL.ConnectSql;
import com.trainstation.model.CarriageType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarriageTypeDAO implements GenericDAO<CarriageType> {
    private static CarriageTypeDAO instance;
    private Connection connection;

    private CarriageTypeDAO() {
        connection = ConnectSql.getInstance().getConnection();
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
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, carriageType.getCarriageTypeId());
            pstmt.setString(2, carriageType.getTypeName());
            pstmt.setInt(3, carriageType.getSeatCount());
            pstmt.setBigDecimal(4, carriageType.getPriceMultiplier());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(CarriageType carriageType) {
        String sql = "UPDATE CarriageType SET TypeName = ?, SeatCount = ?, PriceMultiplier = ? WHERE CarriageTypeID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, carriageType.getTypeName());
            pstmt.setInt(2, carriageType.getSeatCount());
            pstmt.setBigDecimal(3, carriageType.getPriceMultiplier());
            pstmt.setString(4, carriageType.getCarriageTypeId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM CarriageType WHERE CarriageTypeID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CarriageType findById(String id) {
        String sql = "SELECT * FROM CarriageType WHERE CarriageTypeID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractCarriageTypeFromResultSet(rs);
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
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                carriageTypes.add(extractCarriageTypeFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return carriageTypes;
    }

    private CarriageType extractCarriageTypeFromResultSet(ResultSet rs) throws SQLException {
        CarriageType carriageType = new CarriageType();
        carriageType.setCarriageTypeId(rs.getString("CarriageTypeID"));
        carriageType.setTypeName(rs.getString("TypeName"));
        carriageType.setSeatCount(rs.getInt("SeatCount"));
        carriageType.setPriceMultiplier(rs.getBigDecimal("PriceMultiplier"));
        return carriageType;
    }
}
