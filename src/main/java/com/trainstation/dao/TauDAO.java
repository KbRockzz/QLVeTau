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
        // Updated to query DauMay table (replaces old Tau table)
        String sql = "SELECT maDauMay, tenDauMay, trangThai FROM DauMay WHERE isActive = 1";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                Tau t = new Tau(
                        rs.getString("maDauMay"),
                        0, // soToa not available in DauMay table
                        rs.getString("tenDauMay"),
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
        // Updated to query DauMay table (replaces old Tau table)
        String sql = "SELECT maDauMay, tenDauMay, trangThai FROM DauMay WHERE maDauMay = ? AND isActive = 1";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new Tau(
                            rs.getString("maDauMay"),
                            0, // soToa not available in DauMay table
                            rs.getString("tenDauMay"),
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
        // Updated to insert into DauMay table (replaces old Tau table)
        String sql = "INSERT INTO DauMay (maDauMay, tenDauMay, trangThai, isActive) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, t.getMaTau());
            pst.setString(2, t.getTenTau());
            pst.setString(3, t.getTrangThai());
            pst.setBoolean(4, true);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Tau t) {
        // Updated to update DauMay table (replaces old Tau table)
        String sql = "UPDATE DauMay SET tenDauMay = ?, trangThai = ? WHERE maDauMay = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, t.getTenTau());
            pst.setString(2, t.getTrangThai());
            pst.setString(3, t.getMaTau());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        // Updated to soft delete from DauMay table (replaces old Tau table)
        String sql = "UPDATE DauMay SET isActive = 0 WHERE maDauMay = ?";
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
        // Updated to update DauMay table (replaces old Tau table)
        String sql = "UPDATE DauMay SET trangThai = N'Dừng hoạt động' WHERE maDauMay = ?";
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
        // Updated to query DauMay table (replaces old Tau table)
        String sql = "SELECT maDauMay, tenDauMay, trangThai FROM DauMay WHERE trangThai != N'Dừng hoạt động' AND isActive = 1";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                Tau t = new Tau(
                        rs.getString("maDauMay"),
                        0, // soToa not available in DauMay table
                        rs.getString("tenDauMay"),
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
        // Updated to update DauMay table (replaces old Tau table)
        String sql = "UPDATE DauMay SET trangThai = ? WHERE maDauMay = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, sanSang);
            pst.setString(2, maTau);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}