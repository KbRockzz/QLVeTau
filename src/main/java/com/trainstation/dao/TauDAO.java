package com.trainstation.dao;

import com.trainstation.model.Tau;
import com.trainstation.MySQL.ConnectSql;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TauDAO implements GenericDAO<Tau> {
    private static TauDAO instance;
    private Connection connection;

    private TauDAO() {
        connection = ConnectSql.getInstance().getConnection();
    }

    public static synchronized TauDAO getInstance() {
        if (instance == null) {
            instance = new TauDAO();
        }
        return instance;
    }

    @Override
    public List<Tau> getAll() {
        List<Tau> list = new ArrayList<>();
        String sql = "SELECT maTau, soToa, tenTau, trangThai FROM Tau";
        try (PreparedStatement pst = connection.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                Tau t = new Tau(
                    rs.getString("maTau"),
                    rs.getInt("soToa"),
                    rs.getString("tenTau"),
                    rs.getString("trangThai")
                );
                list.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Tau findById(String id) {
        String sql = "SELECT maTau, soToa, tenTau, trangThai FROM Tau WHERE maTau = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new Tau(
                        rs.getString("maTau"),
                        rs.getInt("soToa"),
                        rs.getString("tenTau"),
                        rs.getString("trangThai")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean insert(Tau t) {
        String sql = "INSERT INTO Tau (maTau, soToa, tenTau, trangThai) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, t.getMaTau());
            pst.setInt(2, t.getSoToa());
            pst.setString(3, t.getTenTau());
            pst.setString(4, t.getTrangThai());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Tau t) {
        String sql = "UPDATE Tau SET soToa = ?, tenTau = ?, trangThai = ? WHERE maTau = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, t.getSoToa());
            pst.setString(2, t.getTenTau());
            pst.setString(3, t.getTrangThai());
            pst.setString(4, t.getMaTau());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM Tau WHERE maTau = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
