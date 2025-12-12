package com.trainstation.dao;

import com.trainstation.model.DauMay;
import com.trainstation.MySQL.ConnectSql;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DauMayDAO implements GenericDAO<DauMay> {
    private static DauMayDAO instance;

    private DauMayDAO() {
    }

    public static synchronized DauMayDAO getInstance() {
        if (instance == null) {
            instance = new DauMayDAO();
        }
        return instance;
    }

    @Override
    public List<DauMay> getAll() {
        List<DauMay> list = new ArrayList<>();
        String sql = "SELECT maDauMay, loaiDauMay, tenDauMay, namSX, lanBaoTriGanNhat, trangThai, isActive FROM DauMay WHERE isActive = 1";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                DauMay dm = mapResultSetToEntity(rs);
                list.add(dm);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public List<DauMay> getUnActive(){
        List<DauMay> list = new ArrayList<>();
        String sql = "SELECT maDauMay, loaiDauMay, tenDauMay, namSX, lanBaoTriGanNhat, trangThai, isActive FROM DauMay WHERE isActive = 0";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                DauMay dm = mapResultSetToEntity(rs);
                list.add(dm);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    @Override
    public DauMay findById(String id) {
        List<DauMay> list = new ArrayList<>();
        list = getAll();
        return list.stream()
                .filter(dauMay -> dauMay.getMaDauMay().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean insert(DauMay entity) {
        String sql = "INSERT INTO DauMay (maDauMay, loaiDauMay, tenDauMay, namSX, lanBaoTriGanNhat, trangThai, isActive) VALUES (?, ?, ?, ?, ?, ?, 1)";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, entity.getMaDauMay());
            pst.setString(2, entity.getLoaiDauMay());
            pst.setString(3, entity.getTenDauMay());
            if (entity.getNamSX() != null) {
                pst.setInt(4, entity.getNamSX());
            } else {
                pst.setNull(4, Types.INTEGER);
            }
            if (entity.getLanBaoTriGanNhat() != null) {
                pst.setTimestamp(5, Timestamp.valueOf(entity.getLanBaoTriGanNhat()));
            } else {
                pst.setNull(5, Types.TIMESTAMP);
            }
            pst.setString(6, entity.getTrangThai());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(DauMay entity) {
        String sql = "UPDATE DauMay SET loaiDauMay = ?, tenDauMay = ?, namSX = ?, lanBaoTriGanNhat = ?, trangThai = ? WHERE maDauMay = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, entity.getLoaiDauMay());
            pst.setString(2, entity.getTenDauMay());
            if (entity.getNamSX() != null) {
                pst.setInt(3, entity.getNamSX());
            } else {
                pst.setNull(3, Types.INTEGER);
            }
            if (entity.getLanBaoTriGanNhat() != null) {
                pst.setTimestamp(4, Timestamp.valueOf(entity.getLanBaoTriGanNhat()));
            } else {
                pst.setNull(4, Types.TIMESTAMP);
            }
            pst.setString(5, entity.getTrangThai());
            pst.setString(6, entity.getMaDauMay());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(String id) {
        String sql = "UPDATE DauMay SET isActive = 0 WHERE maDauMay = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private DauMay mapResultSetToEntity(ResultSet rs) throws SQLException {
        LocalDateTime lanBaoTri = null;
        Timestamp ts = rs.getTimestamp("lanBaoTriGanNhat");
        if (ts != null) {
            lanBaoTri = ts.toLocalDateTime();
        }

        return new DauMay(
                rs.getString("maDauMay"),
                rs.getString("loaiDauMay"),
                rs.getString("tenDauMay"),
                rs.getObject("namSX", Integer.class),
                lanBaoTri,
                rs.getString("trangThai")
        );
    }

    /**
     * Dừng hoạt động đầu máy
     */
    public boolean dungHoatDongDauMay(String maDauMay) {
        String sql = "UPDATE DauMay SET trangThai = N'Dừng hoạt động' WHERE maDauMay = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maDauMay);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy danh sách đầu máy đang hoạt động
     */
    public List<DauMay> layDauMayHoatDong() {
        List<DauMay> list = new ArrayList<>();
        list = getAll();
        List<DauMay> activeList = new ArrayList<>();
        list.stream()
                .filter(dauMay -> "Hoạt động".equals(dauMay.getTrangThai()))
                .forEach(activeList::add);
        return activeList;
    }

    public boolean khoiPhucDauMay(String maDauMay) {
        String sql = "UPDATE DauMay SET isActive = 1 WHERE maDauMay = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maDauMay);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
