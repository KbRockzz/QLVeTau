package com.trainstation.dao;

import com.trainstation.model.CauHinhDoiVe;
import com.trainstation.MySQL.ConnectSql;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho bảng CauHinhDoiVe
 */
public class CauHinhDoiVeDAO implements GenericDAO<CauHinhDoiVe> {
    private static CauHinhDoiVeDAO instance;

    private CauHinhDoiVeDAO() {
    }

    public static synchronized CauHinhDoiVeDAO getInstance() {
        if (instance == null) {
            instance = new CauHinhDoiVeDAO();
        }
        return instance;
    }

    @Override
    public List<CauHinhDoiVe> getAll() {
        List<CauHinhDoiVe> list = new ArrayList<>();
        String sql = "SELECT * FROM CauHinhDoiVe";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public CauHinhDoiVe findById(String id) {
        String sql = "SELECT * FROM CauHinhDoiVe WHERE maCauHinh = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean insert(CauHinhDoiVe cauHinh) {
        String sql = "INSERT INTO CauHinhDoiVe (maCauHinh, tenCauHinh, giaTriSo, giaTriChuoi, moTa, ngayCapNhat) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, cauHinh.getMaCauHinh());
            pst.setString(2, cauHinh.getTenCauHinh());
            
            if (cauHinh.getGiaTriSo() != null) {
                pst.setFloat(3, cauHinh.getGiaTriSo());
            } else {
                pst.setNull(3, Types.FLOAT);
            }
            
            pst.setString(4, cauHinh.getGiaTriChuoi());
            pst.setString(5, cauHinh.getMoTa());
            
            if (cauHinh.getNgayCapNhat() != null) {
                pst.setTimestamp(6, Timestamp.valueOf(cauHinh.getNgayCapNhat()));
            } else {
                pst.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            }
            
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(CauHinhDoiVe cauHinh) {
        String sql = "UPDATE CauHinhDoiVe SET tenCauHinh = ?, giaTriSo = ?, giaTriChuoi = ?, " +
                    "moTa = ?, ngayCapNhat = ? WHERE maCauHinh = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, cauHinh.getTenCauHinh());
            
            if (cauHinh.getGiaTriSo() != null) {
                pst.setFloat(2, cauHinh.getGiaTriSo());
            } else {
                pst.setNull(2, Types.FLOAT);
            }
            
            pst.setString(3, cauHinh.getGiaTriChuoi());
            pst.setString(4, cauHinh.getMoTa());
            pst.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            pst.setString(6, cauHinh.getMaCauHinh());
            
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM CauHinhDoiVe WHERE maCauHinh = ?";
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
     * Lấy giá trị số của cấu hình, trả về giá trị mặc định nếu không tìm thấy
     */
    public float getGiaTriSo(String maCauHinh, float defaultValue) {
        CauHinhDoiVe cauHinh = findById(maCauHinh);
        if (cauHinh != null && cauHinh.getGiaTriSo() != null) {
            return cauHinh.getGiaTriSo();
        }
        return defaultValue;
    }

    /**
     * Lấy giá trị chuỗi của cấu hình, trả về giá trị mặc định nếu không tìm thấy
     */
    public String getGiaTriChuoi(String maCauHinh, String defaultValue) {
        CauHinhDoiVe cauHinh = findById(maCauHinh);
        if (cauHinh != null && cauHinh.getGiaTriChuoi() != null) {
            return cauHinh.getGiaTriChuoi();
        }
        return defaultValue;
    }

    /**
     * Map ResultSet to CauHinhDoiVe
     */
    private CauHinhDoiVe mapResultSet(ResultSet rs) throws SQLException {
        CauHinhDoiVe cauHinh = new CauHinhDoiVe();
        cauHinh.setMaCauHinh(rs.getString("maCauHinh"));
        cauHinh.setTenCauHinh(rs.getString("tenCauHinh"));
        
        float giaTriSo = rs.getFloat("giaTriSo");
        if (!rs.wasNull()) {
            cauHinh.setGiaTriSo(giaTriSo);
        }
        
        cauHinh.setGiaTriChuoi(rs.getString("giaTriChuoi"));
        cauHinh.setMoTa(rs.getString("moTa"));
        
        Timestamp ngayCapNhat = rs.getTimestamp("ngayCapNhat");
        if (ngayCapNhat != null) {
            cauHinh.setNgayCapNhat(ngayCapNhat.toLocalDateTime());
        }
        
        return cauHinh;
    }
}
