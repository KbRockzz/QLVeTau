package com.trainstation.dao;

import com.trainstation.model.Ga;
import com.trainstation.MySQL.ConnectSql;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GaDAO implements GenericDAO<Ga> {
    private static GaDAO instance;

    private GaDAO() {
    }

    public static synchronized GaDAO getInstance() {
        if (instance == null) {
            instance = new GaDAO();
        }
        return instance;
    }

    @Override
    public List<Ga> getAll() {
        List<Ga> list = new ArrayList<>();
        // Only get active stations (isActive = 1)
        String sql = "SELECT maGa, tenGa, moTa, tinhTrang, diaChi FROM Ga WHERE isActive = 1";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                Ga ga = mapResultSetToEntity(rs);
                list.add(ga);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Ga findById(String id) {
        String sql = "SELECT maGa, tenGa, moTa, tinhTrang, diaChi FROM Ga WHERE maGa = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean insert(Ga entity) {
        // Set isActive = 1 by default for new stations
        String sql = "INSERT INTO Ga (maGa, tenGa, moTa, tinhTrang, diaChi, isActive) VALUES (?, ?, ?, ?, ?, 1)";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, entity.getMaGa());
            pst.setString(2, entity.getTenGa());
            pst.setString(3, entity.getMoTa());
            pst.setString(4, entity.getTinhTrang());
            pst.setString(5, entity.getDiaChi());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(Ga entity) {
        String sql = "UPDATE Ga SET tenGa = ?, moTa = ?, tinhTrang = ?, diaChi = ? WHERE maGa = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, entity.getTenGa());
            pst.setString(2, entity.getMoTa());
            pst.setString(3, entity.getTinhTrang());
            pst.setString(4, entity.getDiaChi());
            pst.setString(5, entity.getMaGa());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(String id) {
        // Soft delete: set isActive = 0
        String sql = "UPDATE Ga SET isActive = 0 WHERE maGa = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get all deleted stations (isActive = 0)
     * @return List of soft-deleted stations
     */
    public List<Ga> getDeletedStations() {
        List<Ga> list = new ArrayList<>();
        String sql = "SELECT maGa, tenGa, moTa, tinhTrang, diaChi FROM Ga WHERE isActive = 0";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                Ga ga = mapResultSetToEntity(rs);
                list.add(ga);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Restore a soft-deleted station (set isActive = 1)
     * @param id Station ID to restore
     * @return true if restore was successful
     */
    public boolean restoreStation(String id) {
        String sql = "UPDATE Ga SET isActive = 1 WHERE maGa = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Ga mapResultSetToEntity(ResultSet rs) throws SQLException {
        return new Ga(
                rs.getString("maGa"),
                rs.getString("tenGa"),
                rs.getString("moTa"),
                rs.getString("tinhTrang"),
                rs.getString("diaChi")
        );
    }
}
