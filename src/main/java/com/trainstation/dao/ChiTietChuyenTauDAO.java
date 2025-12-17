package com.trainstation.dao;

import com.trainstation.MySQL.ConnectSql;
import com.trainstation.model.ChiTietChuyenTau;
import com.trainstation.model.ToaTau;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for ChiTietChuyenTau.
 * Provides methods to manage the relationship between train trips and their assigned coaches.
 */
public class ChiTietChuyenTauDAO implements GenericDAO<ChiTietChuyenTau> {
    private static ChiTietChuyenTauDAO instance;
    private final ToaTauDAO toaTauDAO;

    private ChiTietChuyenTauDAO() {
        this.toaTauDAO = ToaTauDAO.getInstance();
    }

    public static synchronized ChiTietChuyenTauDAO getInstance() {
        if (instance == null) {
            instance = new ChiTietChuyenTauDAO();
        }
        return instance;
    }

    @Override
    public List<ChiTietChuyenTau> getAll() {
        List<ChiTietChuyenTau> list = new ArrayList<>();
        String sql = "SELECT maChuyenTau, maToaTau, soThuTuToa, sucChua, isActive " +
                     "FROM ChiTietChuyenTau WHERE isActive = 1";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                ChiTietChuyenTau ct = new ChiTietChuyenTau();
                ct.setMaChuyenTau(rs.getString("maChuyenTau"));
                ct.setMaToaTau(rs.getString("maToaTau"));
                ct.setSoThuTuToa(rs.getObject("soThuTuToa", Integer.class));
                ct.setSucChua(rs.getObject("sucChua", Integer.class));
                ct.setActive(rs.getBoolean("isActive"));
                list.add(ct);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public ChiTietChuyenTau findById(String id) {
        // This table has a composite primary key (maChuyenTau, maToaTau)
        // This method is not directly applicable; use findByCompositeKey instead
        return null;
    }

    /**
     * Find a specific ChiTietChuyenTau by its composite primary key.
     *
     * @param maChuyenTau Train trip ID
     * @param maToaTau Coach ID
     * @return ChiTietChuyenTau object or null if not found
     */
    public ChiTietChuyenTau findByCompositeKey(String maChuyenTau, String maToaTau) {
        String sql = "SELECT maChuyenTau, maToaTau, soThuTuToa, sucChua, isActive " +
                     "FROM ChiTietChuyenTau WHERE maChuyenTau = ? AND maToaTau = ? AND isActive = 1";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maChuyenTau);
            pst.setString(2, maToaTau);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    ChiTietChuyenTau ct = new ChiTietChuyenTau();
                    ct.setMaChuyenTau(rs.getString("maChuyenTau"));
                    ct.setMaToaTau(rs.getString("maToaTau"));
                    ct.setSoThuTuToa(rs.getObject("soThuTuToa", Integer.class));
                    ct.setSucChua(rs.getObject("sucChua", Integer.class));
                    ct.setActive(rs.getBoolean("isActive"));
                    return ct;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all coach details (ToaTau) assigned to a specific train trip.
     * This is the key method for dynamic coach loading.
     *
     * @param maChuyenTau Train trip ID
     * @return List of ToaTau objects assigned to this trip, ordered by soThuTuToa
     */
    public List<ToaTau> getCoachesByTrip(String maChuyenTau) {
        List<ToaTau> coaches = new ArrayList<>();
        
        // Query to get coach details using JOIN to avoid N+1 query problem
        String sql = "SELECT t.maToa, t.tenToa, t.loaiToa, t.maTau, t.sucChua " +
                     "FROM ChiTietChuyenTau ct " +
                     "INNER JOIN ToaTau t ON ct.maToaTau = t.maToa " +
                     "WHERE ct.maChuyenTau = ? AND ct.isActive = 1 " +
                     "ORDER BY ct.soThuTuToa";
        
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maChuyenTau);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    ToaTau coach = new ToaTau(
                        rs.getString("maToa"),
                        rs.getString("tenToa"),
                        rs.getString("loaiToa"),
                        rs.getString("maTau"),
                        rs.getInt("sucChua")
                    );
                    coaches.add(coach);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading coaches for trip " + maChuyenTau + ": " + e.getMessage());
            e.printStackTrace();
        }
        
        return coaches;
    }

    /**
     * Get all ChiTietChuyenTau records for a specific train trip.
     *
     * @param maChuyenTau Train trip ID
     * @return List of ChiTietChuyenTau objects
     */
    public List<ChiTietChuyenTau> findByChuyenTau(String maChuyenTau) {
        List<ChiTietChuyenTau> list = new ArrayList<>();
        String sql = "SELECT maChuyenTau, maToaTau, soThuTuToa, sucChua, isActive " +
                     "FROM ChiTietChuyenTau WHERE maChuyenTau = ? AND isActive = 1 " +
                     "ORDER BY soThuTuToa";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maChuyenTau);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    ChiTietChuyenTau ct = new ChiTietChuyenTau();
                    ct.setMaChuyenTau(rs.getString("maChuyenTau"));
                    ct.setMaToaTau(rs.getString("maToaTau"));
                    ct.setSoThuTuToa(rs.getObject("soThuTuToa", Integer.class));
                    ct.setSucChua(rs.getObject("sucChua", Integer.class));
                    ct.setActive(rs.getBoolean("isActive"));
                    list.add(ct);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean insert(ChiTietChuyenTau ct) {
        String sql = "INSERT INTO ChiTietChuyenTau (maChuyenTau, maToaTau, soThuTuToa, sucChua, isActive) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, ct.getMaChuyenTau());
            pst.setString(2, ct.getMaToaTau());
            if (ct.getSoThuTuToa() != null) {
                pst.setInt(3, ct.getSoThuTuToa());
            } else {
                pst.setNull(3, Types.INTEGER);
            }
            if (ct.getSucChua() != null) {
                pst.setInt(4, ct.getSucChua());
            } else {
                pst.setNull(4, Types.INTEGER);
            }
            pst.setBoolean(5, ct.isActive());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(ChiTietChuyenTau ct) {
        String sql = "UPDATE ChiTietChuyenTau SET soThuTuToa = ?, sucChua = ?, isActive = ? " +
                     "WHERE maChuyenTau = ? AND maToaTau = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            if (ct.getSoThuTuToa() != null) {
                pst.setInt(1, ct.getSoThuTuToa());
            } else {
                pst.setNull(1, Types.INTEGER);
            }
            if (ct.getSucChua() != null) {
                pst.setInt(2, ct.getSucChua());
            } else {
                pst.setNull(2, Types.INTEGER);
            }
            pst.setBoolean(3, ct.isActive());
            pst.setString(4, ct.getMaChuyenTau());
            pst.setString(5, ct.getMaToaTau());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        // This table has a composite primary key
        // This method is not directly applicable
        return false;
    }

    /**
     * Soft delete a ChiTietChuyenTau record by setting isActive to false.
     *
     * @param maChuyenTau Train trip ID
     * @param maToaTau Coach ID
     * @return true if successful, false otherwise
     */
    public boolean softDelete(String maChuyenTau, String maToaTau) {
        String sql = "UPDATE ChiTietChuyenTau SET isActive = 0 WHERE maChuyenTau = ? AND maToaTau = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maChuyenTau);
            pst.setString(2, maToaTau);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Hard delete a ChiTietChuyenTau record.
     *
     * @param maChuyenTau Train trip ID
     * @param maToaTau Coach ID
     * @return true if successful, false otherwise
     */
    public boolean hardDelete(String maChuyenTau, String maToaTau) {
        String sql = "DELETE FROM ChiTietChuyenTau WHERE maChuyenTau = ? AND maToaTau = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maChuyenTau);
            pst.setString(2, maToaTau);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
