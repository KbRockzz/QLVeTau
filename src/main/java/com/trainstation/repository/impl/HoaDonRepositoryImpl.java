package com.trainstation.repository.impl;

import com.trainstation.model.HoaDon;
import com.trainstation.persistence.JpaEntityManagerProvider;
import com.trainstation.repository.IHoaDonRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HoaDonRepositoryImpl implements IHoaDonRepository {
    private static final Logger LOG = Logger.getLogger(HoaDonRepositoryImpl.class.getName());
    private static HoaDonRepositoryImpl instance;

    private HoaDonRepositoryImpl() {
    }

    public static synchronized HoaDonRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new HoaDonRepositoryImpl();
        }
        return instance;
    }

    @Override
    public List<HoaDon> getAll() {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            return em.createNativeQuery(
                    "SELECT maHoaDon, maNV, maKH, tenKH, soDienThoai, ngayLap, phuongThucThanhToan, trangThai " +
                            "FROM HoaDon WHERE isActive = 1",
                    HoaDon.class
            ).getResultList();
        }
    }

    @Override
    public HoaDon findById(String id) {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            List<HoaDon> result = em.createNativeQuery(
                            "SELECT maHoaDon, maNV, maKH, tenKH, soDienThoai, ngayLap, phuongThucThanhToan, trangThai " +
                                    "FROM HoaDon WHERE maHoaDon = ? AND isActive = 1",
                            HoaDon.class
                    ).setParameter(1, id)
                    .getResultList();
            return result.isEmpty() ? null : result.get(0);
        }
    }

    @Override
    public boolean insert(HoaDon entity) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            em.persist(entity);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể thêm hóa đơn", e);
            return false;
        }
    }

    @Override
    public boolean update(HoaDon entity) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            em.merge(entity);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể cập nhật hóa đơn", e);
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            int updated = em.createNativeQuery("UPDATE HoaDon SET isActive = 0 WHERE maHoaDon = ?")
                    .setParameter(1, id)
                    .executeUpdate();
            tx.commit();
            return updated > 0;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể xóa mềm hóa đơn", e);
            return false;
        }
    }
}
