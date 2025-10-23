package com.trainstation.dao;

import com.trainstation.model.ChuyenTau;
import com.trainstation.MySQL.ConnectSql;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChuyenTauDAO implements GenericDAO<ChuyenTau> {
    private static ChuyenTauDAO instance;
    private Connection connection;

    private ChuyenTauDAO() {
        connection = ConnectSql.getInstance().getConnection();
    }

    public static synchronized ChuyenTauDAO getInstance() {
        if (instance == null) {
            instance = new ChuyenTauDAO();
        }
        return instance;
    }

    @Override
    public List<ChuyenTau> getAll() {
        List<ChuyenTau> list = new ArrayList<>();
        String sql = "SELECT maChuyen, maTau, maNV, gaDi, gaDen, gioDi, gioDen, soKm, maChang FROM ChuyenTau";
        try (PreparedStatement pst = connection.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                LocalDateTime gioDi = null, gioDen = null;
                Timestamp ts1 = rs.getTimestamp("gioDi");
                if (ts1 != null) gioDi = ts1.toLocalDateTime();
                Timestamp ts2 = rs.getTimestamp("gioDen");
                if (ts2 != null) gioDen = ts2.toLocalDateTime();
                
                ChuyenTau ct = new ChuyenTau(
                    rs.getString("maChuyen"),
                    rs.getString("maTau"),
                    rs.getString("maNV"),
                    rs.getString("gaDi"),
                    rs.getString("gaDen"),
                    gioDi,
                    gioDen,
                    rs.getInt("soKm"),
                    rs.getString("maChang")
                );
                list.add(ct);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public ChuyenTau findById(String id) {
        String sql = "SELECT maChuyen, maTau, maNV, gaDi, gaDen, gioDi, gioDen, soKm, maChang FROM ChuyenTau WHERE maChuyen = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    LocalDateTime gioDi = null, gioDen = null;
                    Timestamp ts1 = rs.getTimestamp("gioDi");
                    if (ts1 != null) gioDi = ts1.toLocalDateTime();
                    Timestamp ts2 = rs.getTimestamp("gioDen");
                    if (ts2 != null) gioDen = ts2.toLocalDateTime();
                    
                    return new ChuyenTau(
                        rs.getString("maChuyen"),
                        rs.getString("maTau"),
                        rs.getString("maNV"),
                        rs.getString("gaDi"),
                        rs.getString("gaDen"),
                        gioDi,
                        gioDen,
                        rs.getInt("soKm"),
                        rs.getString("maChang")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean insert(ChuyenTau ct) {
        String sql = "INSERT INTO ChuyenTau (maChuyen, maTau, maNV, gaDi, gaDen, gioDi, gioDen, soKm, maChang) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, ct.getMaChuyen());
            pst.setString(2, ct.getMaTau());
            pst.setString(3, ct.getMaNV());
            pst.setString(4, ct.getGaDi());
            pst.setString(5, ct.getGaDen());
            if (ct.getGioDi() != null) {
                pst.setTimestamp(6, Timestamp.valueOf(ct.getGioDi()));
            } else {
                pst.setNull(6, Types.TIMESTAMP);
            }
            if (ct.getGioDen() != null) {
                pst.setTimestamp(7, Timestamp.valueOf(ct.getGioDen()));
            } else {
                pst.setNull(7, Types.TIMESTAMP);
            }
            pst.setInt(8, ct.getSoKm());
            pst.setString(9, ct.getMaChang());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(ChuyenTau ct) {
        String sql = "UPDATE ChuyenTau SET maTau = ?, maNV = ?, gaDi = ?, gaDen = ?, gioDi = ?, gioDen = ?, soKm = ?, maChang = ? WHERE maChuyen = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, ct.getMaTau());
            pst.setString(2, ct.getMaNV());
            pst.setString(3, ct.getGaDi());
            pst.setString(4, ct.getGaDen());
            if (ct.getGioDi() != null) {
                pst.setTimestamp(5, Timestamp.valueOf(ct.getGioDi()));
            } else {
                pst.setNull(5, Types.TIMESTAMP);
            }
            if (ct.getGioDen() != null) {
                pst.setTimestamp(6, Timestamp.valueOf(ct.getGioDen()));
            } else {
                pst.setNull(6, Types.TIMESTAMP);
            }
            pst.setInt(7, ct.getSoKm());
            pst.setString(8, ct.getMaChang());
            pst.setString(9, ct.getMaChuyen());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM ChuyenTau WHERE maChuyen = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Search for train trips by departure station, arrival station, date and time
     * @param gaDi Departure station
     * @param gaDen Arrival station
     * @param ngayDi Departure date (can be null)
     * @param gioDi Departure time (can be null)
     * @return List of matching train trips
     */
    public List<ChuyenTau> timKiemChuyenTau(String gaDi, String gaDen, LocalDate ngayDi, LocalTime gioDi) {
        List<ChuyenTau> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT maChuyen, maTau, maNV, gaDi, gaDen, gioDi, gioDen, soKm, maChang FROM ChuyenTau WHERE 1=1");
        
        if (gaDi != null && !gaDi.trim().isEmpty()) {
            sql.append(" AND gaDi = ?");
        }
        if (gaDen != null && !gaDen.trim().isEmpty()) {
            sql.append(" AND gaDen = ?");
        }
        if (ngayDi != null) {
            sql.append(" AND CAST(gioDi AS DATE) = ?");
        }
        if (gioDi != null) {
            // Compare using DATETIME instead of TIME to avoid type mismatch
            sql.append(" AND gioDi >= ?");
        }
        
        try (PreparedStatement pst = connection.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            
            if (gaDi != null && !gaDi.trim().isEmpty()) {
                pst.setString(paramIndex++, gaDi);
            }
            if (gaDen != null && !gaDen.trim().isEmpty()) {
                pst.setString(paramIndex++, gaDen);
            }
            if (ngayDi != null) {
                pst.setDate(paramIndex++, Date.valueOf(ngayDi));
            }
            if (gioDi != null) {
                // Combine date and time into a DATETIME for proper comparison
                LocalDateTime searchDateTime = LocalDateTime.of(ngayDi != null ? ngayDi : LocalDate.now(), gioDi);
                pst.setTimestamp(paramIndex++, Timestamp.valueOf(searchDateTime));
            }
            
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    LocalDateTime gioDiDT = null, gioDenDT = null;
                    Timestamp ts1 = rs.getTimestamp("gioDi");
                    if (ts1 != null) gioDiDT = ts1.toLocalDateTime();
                    Timestamp ts2 = rs.getTimestamp("gioDen");
                    if (ts2 != null) gioDenDT = ts2.toLocalDateTime();
                    
                    ChuyenTau ct = new ChuyenTau(
                        rs.getString("maChuyen"),
                        rs.getString("maTau"),
                        rs.getString("maNV"),
                        rs.getString("gaDi"),
                        rs.getString("gaDen"),
                        gioDiDT,
                        gioDenDT,
                        rs.getInt("soKm"),
                        rs.getString("maChang")
                    );
                    list.add(ct);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    /**
     * Get list of distinct stations (both departure and arrival)
     * @return List of unique station names
     */
    public List<String> getDistinctStations() {
        Set<String> stations = new HashSet<>();
        String sql = "SELECT DISTINCT gaDi FROM ChuyenTau UNION SELECT DISTINCT gaDen FROM ChuyenTau ORDER BY 1";
        try (PreparedStatement pst = connection.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                stations.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(stations);
    }
}
