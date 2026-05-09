package com.trainstation.repository.impl;

import com.trainstation.model.BangGia;
import com.trainstation.model.ChuyenTau;
import com.trainstation.model.Ga;
import com.trainstation.model.Ve;
import com.trainstation.persistence.JpaEntityManagerProvider;
import com.trainstation.repository.IVeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.time.LocalDateTime;

public class VeRepositoryImpl implements IVeRepository {
    private static final Logger LOG = Logger.getLogger(VeRepositoryImpl.class.getName());
    private static VeRepositoryImpl instance;

    private VeRepositoryImpl() {
    }

    public static synchronized VeRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new VeRepositoryImpl();
        }
        return instance;
    }

    @Override
    public List<Ve> getAll() {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            List<Ve> result = em.createNativeQuery(
                    "SELECT maVe, maChuyen, maLoaiVe, maSoGhe, maGaDi, maGaDen, tenGaDi, tenGaDen, ngayIn, trangThai, " +
                            "gioDi, gioDenDuKien, soToa, loaiCho, loaiVe, maBangGia, giaThanhToan FROM Ve WHERE isActive = 1",
                    Ve.class
            ).getResultList();
            result.forEach(this::ensureStationNames);
            return result;
        }
    }

    @Override
    public Ve findById(String id) {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            List<Ve> result = em.createNativeQuery(
                            "SELECT maVe, maChuyen, maLoaiVe, maSoGhe, maGaDi, maGaDen, tenGaDi, tenGaDen, ngayIn, trangThai, " +
                                    "gioDi, gioDenDuKien, soToa, loaiCho, loaiVe, maBangGia, giaThanhToan " +
                                    "FROM Ve WHERE maVe = ? AND isActive = 1",
                            Ve.class
                    )
                    .setParameter(1, id)
                    .getResultList();
            Ve ve = result.isEmpty() ? null : result.get(0);
            ensureStationNames(ve);
            return ve;
        }
    }

    @Override
    public List<Ve> findByKhachHang(String maKH) {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            List<Ve> result = em.createNativeQuery(
                            "SELECT v.maVe, v.maChuyen, v.maLoaiVe, v.maSoGhe, v.maGaDi, v.maGaDen, v.tenGaDi, v.tenGaDen, " +
                                    "v.ngayIn, v.trangThai, v.gioDi, v.gioDenDuKien, v.soToa, v.loaiCho, v.loaiVe, v.maBangGia, v.giaThanhToan " +
                                    "FROM Ve v " +
                                    "JOIN ChiTietHoaDon ct ON ct.maVe = v.maVe AND ct.isActive = 1 " +
                                    "JOIN HoaDon h ON h.maHoaDon = ct.maHoaDon AND h.isActive = 1 " +
                                    "WHERE h.maKH = ? AND v.isActive = 1",
                            Ve.class
                    )
                    .setParameter(1, maKH)
                    .getResultList();
            result.forEach(this::ensureStationNames);
            return result;
        }
    }

    @Override
    public List<Ve> findByChuyen(String maChuyen) {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            List<Ve> result = em.createNativeQuery(
                            "SELECT maVe, maChuyen, maLoaiVe, maSoGhe, maGaDi, maGaDen, tenGaDi, tenGaDen, ngayIn, trangThai, " +
                                    "gioDi, gioDenDuKien, soToa, loaiCho, loaiVe, maBangGia, giaThanhToan " +
                                    "FROM Ve WHERE maChuyen = ? AND isActive = 1",
                            Ve.class
                    )
                    .setParameter(1, maChuyen)
                    .getResultList();
            result.forEach(this::ensureStationNames);
            return result;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Không thể lấy danh sách vé cho chuyến " + maChuyen, e);
            return List.of();
        }
    }

    @Override
    public List<Ve> findByTrangThai(String trangThai) {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            List<Ve> result = em.createNativeQuery(
                            "SELECT maVe, maChuyen, maLoaiVe, maSoGhe, maGaDi, maGaDen, tenGaDi, tenGaDen, ngayIn, trangThai, " +
                                    "gioDi, gioDenDuKien, soToa, loaiCho, loaiVe, maBangGia, giaThanhToan " +
                                    "FROM Ve WHERE trangThai = ? AND isActive = 1",
                            Ve.class
                    )
                    .setParameter(1, trangThai)
                    .getResultList();
            result.forEach(this::ensureStationNames);
            return result;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Không thể lấy danh sách vé theo trạng thái " + trangThai, e);
            return List.of();
        }
    }

    @Override
    public boolean insert(Ve entity) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            populateDerivedFields(em, entity);
            em.persist(entity);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể thêm vé", e);
            return false;
        }
    }

    @Override
    public boolean update(Ve entity) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            populateDerivedFields(em, entity);
            em.merge(entity);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể cập nhật vé", e);
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            int updated = em.createNativeQuery("UPDATE Ve SET isActive = 0 WHERE maVe = ?")
                    .setParameter(1, id)
                    .executeUpdate();
            tx.commit();
            return updated > 0;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể xóa mềm vé", e);
            return false;
        }
    }

    private void populateDerivedFields(EntityManager em, Ve ve) {
        if (ve.getNgayIn() == null) {
            ve.setNgayIn(LocalDateTime.now());
        }
        if (ve.getMaBangGia() == null || ve.getMaBangGia().isBlank()) {
            ChuyenTau chuyenTau = em.find(ChuyenTau.class, ve.getMaChuyen());
            if (chuyenTau != null && ve.getLoaiCho() != null && !ve.getLoaiCho().isBlank()) {
                List<BangGia> bangGiaList = em.createNativeQuery(
                                "SELECT maBangGia, maChang, loaiGhe, giaCoBan, ngayBatDau, ngayKetThuc " +
                                        "FROM BangGia WHERE maChang = ? AND loaiGhe = ? AND isActive = 1 " +
                                        "AND ngayBatDau <= ? AND (ngayKetThuc IS NULL OR ngayKetThuc >= ?) " +
                                        "ORDER BY ngayBatDau DESC LIMIT 1",
                                BangGia.class
                        )
                        .setParameter(1, chuyenTau.getMaChang())
                        .setParameter(2, ve.getLoaiCho())
                        .setParameter(3, LocalDateTime.now())
                        .setParameter(4, LocalDateTime.now())
                        .getResultList();
                BangGia bangGia = bangGiaList.isEmpty() ? null : bangGiaList.get(0);
                if (bangGia != null) {
                    ve.setMaBangGia(bangGia.getMaBangGia());
                }
            }
        }
        ensureStationNames(ve);
    }

    private void ensureStationNames(Ve ve) {
        if (ve == null) return;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            ve.setTenGaDi(resolveStationName(em, ve.getMaGaDi(), ve.getTenGaDi()));
            ve.setTenGaDen(resolveStationName(em, ve.getMaGaDen(), ve.getTenGaDen()));
        } catch (Exception e) {
            LOG.log(Level.FINE, "Không thể chuẩn hóa tên ga cho vé " + ve.getMaVe(), e);
        }
    }

    private String resolveStationName(EntityManager em, String maGa, String tenGa) {
        if (maGa == null || maGa.isBlank()) {
            return tenGa;
        }
        if (!needsStationLookup(maGa, tenGa)) {
            return tenGa;
        }
        Ga ga = em.find(Ga.class, maGa);
        return ga != null ? ga.getTenGa() : tenGa;
    }

    private boolean needsStationLookup(String maGa, String tenGa) {
        if (tenGa == null || tenGa.isBlank()) {
            return true;
        }
        String lower = tenGa.toLowerCase(Locale.ROOT);
        return lower.startsWith("ga_") || tenGa.equals(maGa) || tenGa.length() <= 3;
    }
}
