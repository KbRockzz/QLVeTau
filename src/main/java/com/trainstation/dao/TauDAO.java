package com.trainstation.dao;

import com.trainstation.model.Tau;
import com.trainstation.MySQL.ConnectSql;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TauDAO implements GenericDAO<Tau> {
    private static TauDAO instance;

    private TauDAO() {
        // Không giữ Connection làm trường
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
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
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
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
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
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
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
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
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
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean dungHoatDongTau(String maTau) {
        String sql = "UPDATE Tau SET trangThai = N'Dừng hoạt động' WHERE maTau = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maTau);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Tau> layTauHoatDong() {
        List<Tau> list = new ArrayList<>();
        String sql = "SELECT maTau, soToa, tenTau, trangThai FROM Tau WHERE trangThai != N'Dừng hoạt động'";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
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

    public void capNhatTrangThai(Connection conn, String maTau, String sanSang) {
        String sql = "UPDATE Tau SET trangThai = ? WHERE maTau = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, sanSang);
            pst.setString(2, maTau);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}