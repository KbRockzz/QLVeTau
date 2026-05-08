package com.trainstation.repository.impl;

import com.trainstation.model.ChiTietHoaDon;
import com.trainstation.persistence.JpaEntityManagerProvider;
import com.trainstation.repository.IChiTietHoaDonRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class ChiTietHoaDonRepositoryImpl implements IChiTietHoaDonRepository {
    private static ChiTietHoaDonRepositoryImpl instance;

    private ChiTietHoaDonRepositoryImpl() {
    }

    public static synchronized ChiTietHoaDonRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new ChiTietHoaDonRepositoryImpl();
        }
        return instance;
    }

    @Override
    public List<ChiTietHoaDon> getAll() {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            return em.createNativeQuery(
                    "SELECT maHoaDon, maVe, maLoaiVe, giaGoc, giaDaKM, moTa " +
                            "FROM ChiTietHoaDon WHERE isActive = 1",
                    ChiTietHoaDon.class
            ).getResultList();
        }
    }

    @Override
    public ChiTietHoaDon findById(String id) {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            List<ChiTietHoaDon> result = em.createNativeQuery(
                            "SELECT maHoaDon, maVe, maLoaiVe, giaGoc, giaDaKM, moTa " +
                                    "FROM ChiTietHoaDon WHERE maVe = ? AND isActive = 1 LIMIT 1",
                            ChiTietHoaDon.class
                    ).setParameter(1, id)
                    .getResultList();
            return result.isEmpty() ? null : result.get(0);
        }
    }

    @Override
    public boolean insert(ChiTietHoaDon entity) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            em.persist(entity);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            return false;
        }
    }

    @Override
    public boolean update(ChiTietHoaDon entity) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            em.merge(entity);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            int updated = em.createNativeQuery("UPDATE ChiTietHoaDon SET isActive = 0 WHERE maVe = ?")
                    .setParameter(1, id)
                    .executeUpdate();
            tx.commit();
            return updated > 0;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            return false;
        }
    }

    @Override
    public List<ChiTietHoaDon> findByHoaDon(String maHoaDon) {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            return em.createNativeQuery(
                            "SELECT maHoaDon, maVe, maLoaiVe, giaGoc, giaDaKM, moTa " +
                                    "FROM ChiTietHoaDon WHERE maHoaDon = ? AND isActive = 1",
                            ChiTietHoaDon.class
                    ).setParameter(1, maHoaDon)
                    .getResultList();
        }
    }
}
