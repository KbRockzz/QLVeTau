package com.trainstation.dao;

import com.trainstation.model.ChangTau;
import com.trainstation.MySQL.ConnectSql;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChangTauDAO implements GenericDAO<ChangTau> {
    private static ChangTauDAO instance;

    private ChangTauDAO() {
        // Không giữ Connection làm trường
    }

    public static synchronized ChangTauDAO getInstance() {
        if (instance == null) {
            instance = new ChangTauDAO();
        }
        return instance;
    }

    @Override
    public List<ChangTau> getAll() {
        List<ChangTau> list = new ArrayList<>();
        String sql = "SELECT maChang, soKMToiThieu, soKMToiDa, moTa, giaTien FROM ChangTau";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                ChangTau ct = new ChangTau(
                        rs.getString("maChang"),
                        rs.getInt("soKMToiThieu"),
                        rs.getInt("soKMToiDa"),
                        rs.getString("moTa"),
                        rs.getFloat("giaTien")
                );
                list.add(ct);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public ChangTau findById(String id) {
        String sql = "SELECT maChang, soKMToiThieu, soKMToiDa, moTa, giaTien FROM ChangTau WHERE maChang = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new ChangTau(
                            rs.getString("maChang"),
                            rs.getInt("soKMToiThieu"),
                            rs.getInt("soKMToiDa"),
                            rs.getString("moTa"),
                            rs.getFloat("giaTien")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean insert(ChangTau ct) {
        String sql = "INSERT INTO ChangTau (maChang, soKMToiThieu, soKMToiDa, moTa, giaTien) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, ct.getMaChang());
            pst.setInt(2, ct.getSoKMToiThieu());
            pst.setInt(3, ct.getSoKMToiDa());
            pst.setString(4, ct.getMoTa());
            pst.setFloat(5, ct.getGiaTien());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(ChangTau ct) {
        String sql = "UPDATE ChangTau SET soKMToiThieu = ?, soKMToiDa = ?, moTa = ?, giaTien = ? WHERE maChang = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, ct.getSoKMToiThieu());
            pst.setInt(2, ct.getSoKMToiDa());
            pst.setString(3, ct.getMoTa());
            pst.setFloat(4, ct.getGiaTien());
            pst.setString(5, ct.getMaChang());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM ChangTau WHERE maChang = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}