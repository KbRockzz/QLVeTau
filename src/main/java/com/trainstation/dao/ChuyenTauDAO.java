package com.trainstation.dao;

import com.trainstation.model.ChuyenTau;
import com.trainstation.MySQL.ConnectSql;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
}
