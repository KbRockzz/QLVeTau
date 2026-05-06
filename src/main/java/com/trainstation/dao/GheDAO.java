package com.trainstation.dao;

import com.trainstation.MySQL.ConnectSql;
import com.trainstation.model.Ghe;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GheDAO {
    private static GheDAO instance;

    private GheDAO() {
    }

    public static synchronized GheDAO getInstance() {
        if (instance == null) {
            instance = new GheDAO();
        }
        return instance;
    }

    public List<Ghe> getByToa(String maToa) {
        List<Ghe> list = new ArrayList<>();
        // Đã thêm cột loaiGhe vào SELECT (tên cột có thể là 'loaiGhe' hoặc 'maLoaiGhe' tuỳ schema)
        String sql = "SELECT maGhe, maToa, trangThai, loaiGhe FROM Ghe WHERE maToa = ? ORDER BY maGhe";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maToa);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Ghe g = new Ghe();
                    g.setMaGhe(rs.getString("maGhe"));
                    g.setMaToa(rs.getString("maToa"));
                    g.setTrangThai(rs.getString("trangThai"));
                    // set loại ghế nếu có trong DB
                    try {
                        g.setLoaiGhe(rs.getString("loaiGhe"));
                    } catch (Throwable ignored) {
                        // nếu model không có setter/field, bỏ qua (mà nên bổ sung model)
                    }
                    list.add(g);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Ghe> getAll() {
        List<Ghe> list = new ArrayList<>();
        String sql = "SELECT maGhe, maToa, trangThai, loaiGhe FROM Ghe ORDER BY maGhe";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                Ghe g = new Ghe();
                g.setMaGhe(rs.getString("maGhe"));
                g.setMaToa(rs.getString("maToa"));
                g.setTrangThai(rs.getString("trangThai"));
                try { g.setLoaiGhe(rs.getString("loaiGhe")); } catch (Throwable ignored) {}
                list.add(g);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Ghe findById(String maGhe) {
        return getAll().stream()
            .filter(g -> maGhe != null && maGhe.equals(g.getMaGhe()))
            .findFirst()
            .orElse(null);
    }


    public boolean update(Ghe ghe) {
        String sql = "UPDATE Ghe SET trangThai = ? WHERE maGhe = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, ghe.getTrangThai());
            pst.setString(2, ghe.getMaGhe());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Count seats already existing for a given toa.
     */
    public int countByToa(String maToa) {
        String sql = "SELECT COUNT(*) FROM Ghe WHERE maToa = ?";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maToa);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Resolve a seat-type value to a LoaiGhe code. If the value already looks
     * like a code (e.g. "LG001"), return it unchanged. Otherwise look up the
     * matching maLoai from the LoaiGhe table by tenLoai.
     */
    private String resolveLoaiGheCode(String loaiGhe) {
        if (loaiGhe == null) return null;
        if (loaiGhe.matches("^LG\\d+$")) return loaiGhe; // already a code
        String sqlLookup = "SELECT maLoai FROM LoaiGhe WHERE tenLoai = ? LIMIT 1";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sqlLookup)) {
            pst.setString(1, loaiGhe);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) return rs.getString("maLoai");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // unknown type name, leave null
    }

    /**
     * Auto-generate {@code count} seats for the given toa, picking up from the
     * current global max seat number so IDs remain unique (format GH001, GH002, …).
     * Only generates seats up to the requested count if some already exist.
     * The {@code loaiGhe} parameter may be either a LoaiGhe code (e.g. "LG001")
     * or a type name (e.g. "Ghế ngồi cứng"); the latter is resolved to its code
     * automatically.
     */
    public boolean insertBatch(String maToa, String loaiGhe, int count) {
        if (count <= 0) return true;
        int existing = countByToa(maToa);
        int toAdd = count - existing;
        if (toAdd <= 0) return true; // already has enough seats

        // Resolve name → code before any INSERT
        String resolvedLoaiGhe = resolveLoaiGheCode(loaiGhe);

        // Find the current global max seat number
        int maxNum = 0;
        String sqlMax = "SELECT MAX(CAST(SUBSTRING(maGhe, 3) AS UNSIGNED)) FROM Ghe WHERE maGhe REGEXP '^GH[0-9]+$'";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sqlMax);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                int v = rs.getInt(1);
                if (!rs.wasNull()) maxNum = v;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        String sql = "INSERT IGNORE INTO Ghe (maGhe, maToa, loaiGhe, trangThai) VALUES (?, ?, ?, 'Rảnh')";
        try (Connection conn = ConnectSql.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            for (int i = 1; i <= toAdd; i++) {
                String maGhe = String.format("GH%03d", maxNum + i);
                pst.setString(1, maGhe);
                pst.setString(2, maToa);
                if (resolvedLoaiGhe != null) pst.setString(3, resolvedLoaiGhe);
                else pst.setNull(3, java.sql.Types.VARCHAR);
                pst.addBatch();
            }
            pst.executeBatch();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }}
