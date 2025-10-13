package com.trainstation.dao;

import com.trainstation.model.ChangTau;
import com.trainstation.MySQL.ConnectSql;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChangTauDAO implements GenericDAO<ChangTau> {
    private static ChangTauDAO instance;
    private Connection connection;

    private ChangTauDAO() {
        connection = ConnectSql.getInstance().getConnection();
    }

    public static synchronized ChangTauDAO getInstance() {
        if (instance == null) {
            instance = new ChangTauDAO();
        }
        return instance;
    }

    @Override
    public void add(ChangTau changTau) {
        String sql = "INSERT INTO ChangTau (MaChang, TenChang, DiaChi, ThanhPho) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, changTau.getMaChang());
            pstmt.setString(2, changTau.getTenChang());
            pstmt.setString(3, changTau.getDiaChi());
            pstmt.setString(4, changTau.getThanhPho());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(ChangTau changTau) {
        String sql = "UPDATE ChangTau SET TenChang = ?, DiaChi = ?, ThanhPho = ? WHERE MaChang = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, changTau.getTenChang());
            pstmt.setString(2, changTau.getDiaChi());
            pstmt.setString(3, changTau.getThanhPho());
            pstmt.setString(4, changTau.getMaChang());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM ChangTau WHERE MaChang = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ChangTau findById(String id) {
        String sql = "SELECT * FROM ChangTau WHERE MaChang = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractChangTauFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<ChangTau> findAll() {
        List<ChangTau> changTauList = new ArrayList<>();
        String sql = "SELECT * FROM ChangTau";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                changTauList.add(extractChangTauFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return changTauList;
    }

    public List<ChangTau> findByCity(String thanhPho) {
        List<ChangTau> changTauList = new ArrayList<>();
        String sql = "SELECT * FROM ChangTau WHERE ThanhPho = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, thanhPho);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                changTauList.add(extractChangTauFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return changTauList;
    }

    private ChangTau extractChangTauFromResultSet(ResultSet rs) throws SQLException {
        ChangTau changTau = new ChangTau();
        changTau.setMaChang(rs.getString("MaChang"));
        changTau.setTenChang(rs.getString("TenChang"));
        changTau.setDiaChi(rs.getString("DiaChi"));
        changTau.setThanhPho(rs.getString("ThanhPho"));
        return changTau;
    }
}
