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
        // Không giữ Connection làm trường
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
        String sql = "SELECT maNV, tenNV, soDienThoai, diaChi, ngaySinh, maLoaiNV, trangThai FROM NhanVien";
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
        String sql = "SELECT maNV, tenNV, soDienThoai, diaChi, ngaySinh, maLoaiNV, trangThai FROM NhanVien WHERE maNV = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    LocalDate ngaySinh = null;
                    Date date = rs.getDate("ngaySinh");
                    if (date != null) {
                        ngaySinh = date.toLocalDate();
                    }
                    return new NhanVien(
                            rs.getString("maNV"),
                            rs.getString("tenNV"),
                            rs.getString("soDienThoai"),
                            rs.getString("diaChi"),
                            ngaySinh,
                            rs.getString("maLoaiNV")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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
        String sql = "UPDATE NhanVien SET tenNV = ?, soDienThoai = ?, diaChi = ?, ngaySinh = ?, maLoaiNV = ?, trangThai = ? WHERE maNV = ?";
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
        String sql = "UPDATE NhanVien SET trangThai = ? WHERE maNV = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, "hidden"); // Set "hidden"
            pst.setString(2, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getLoaiNV(String maNV) {
        String sql = "SELECT maLoaiNV FROM NhanVien WHERE maNV = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maNV);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("maLoaiNV");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}