package com.trainstation.dao;

import com.trainstation.model.NhanVien;
import com.trainstation.MySQL.ConnectSql;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDAO implements GenericDAO<NhanVien> {
    private static NhanVienDAO instance;

    private NhanVienDAO() {
    }

    public static synchronized NhanVienDAO getInstance() {
        if (instance == null) {
            instance = new NhanVienDAO();
        }
        return instance;
    }

    @Override
    public List<NhanVien> getAll() {
        List<NhanVien> list = new ArrayList<>();
        // Chỉ lấy nhân viên đang hoạt động
        String sql = "SELECT maNV, tenNV, soDienThoai, diaChi, ngaySinh, maLoaiNV, trangThai FROM NhanVien WHERE isActive = 1";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                LocalDate ngaySinh = null;
                Date date = rs.getDate("ngaySinh");
                if (date != null) {
                    ngaySinh = date.toLocalDate();
                }
                NhanVien nv = new NhanVien(
                        rs.getString("maNV"),
                        rs.getString("tenNV"),
                        rs.getString("soDienThoai"),
                        rs.getString("diaChi"),
                        ngaySinh,
                        rs.getString("maLoaiNV"),
                        rs.getString("trangThai")
                );
                list.add(nv);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public NhanVien findById(String id) {
        List<NhanVien> list = new ArrayList<>();
        list = getAll();
        return list.stream()
                .filter(nv -> nv.getMaNV().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean insert(NhanVien nv) {
        String sql = "INSERT INTO NhanVien (maNV, tenNV, soDienThoai, diaChi, ngaySinh, maLoaiNV, trangThai) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, nv.getMaNV());
            pst.setString(2, nv.getTenNV());
            pst.setString(3, nv.getSoDienThoai());
            pst.setString(4, nv.getDiaChi());
            if (nv.getNgaySinh() != null) {
                pst.setDate(5, Date.valueOf(nv.getNgaySinh()));
            } else {
                pst.setNull(5, Types.DATE);
            }
            pst.setString(6, nv.getMaLoaiNV());
            pst.setString(7, nv.getTrangThai());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(NhanVien nv) {
        String sql = "UPDATE NhanVien SET tenNV = ?, soDienThoai = ?, diaChi = ?, ngaySinh = ?, maLoaiNV = ?, trangThai = ? WHERE maNV = ? AND isActive = 1";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, nv.getTenNV());
            pst.setString(2, nv.getSoDienThoai());
            pst.setString(3, nv.getDiaChi());
            if (nv.getNgaySinh() != null) {
                pst.setDate(4, Date.valueOf(nv.getNgaySinh()));
            } else {
                pst.setNull(4, Types.DATE);
            }
            pst.setString(5, nv.getMaLoaiNV());
            pst.setString(6, nv.getTrangThai());
            pst.setString(7, nv.getMaNV());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        // Xóa mềm: đặt isActive = 0
        String sql = "UPDATE NhanVien SET isActive = 0 WHERE maNV = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all deleted employees (isActive = 0)
     * @return List of soft-deleted employees
     */
    public List<NhanVien> getDeletedEmployees() {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT maNV, tenNV, soDienThoai, diaChi, ngaySinh, maLoaiNV, trangThai FROM NhanVien WHERE isActive = 0";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                LocalDate ngaySinh = null;
                Date date = rs.getDate("ngaySinh");
                if (date != null) {
                    ngaySinh = date.toLocalDate();
                }
                NhanVien nv = new NhanVien(
                        rs.getString("maNV"),
                        rs.getString("tenNV"),
                        rs.getString("soDienThoai"),
                        rs.getString("diaChi"),
                        ngaySinh,
                        rs.getString("maLoaiNV"),
                        rs.getString("trangThai")
                );
                list.add(nv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Restore a soft-deleted employee (set isActive = 1)
     * @param id Employee ID to restore
     * @return true if restore was successful
     */
    public boolean restoreEmployee(String id) {
        String sql = "UPDATE NhanVien SET isActive = 1 WHERE maNV = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getLoaiNV(String maNV) {
        List<NhanVien> list = new ArrayList<>();
        list = getAll();
        NhanVien nv = list.stream()
                .filter(n -> n.getMaNV().equals(maNV))
                .findFirst()
                .orElse(null);
        return nv.getMaLoaiNV();
    }
}