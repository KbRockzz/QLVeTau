package com.trainstation.repository.impl;

import com.trainstation.model.ChiTietHoaDon;
import com.trainstation.persistence.JpaEntityManagerProvider;
import com.trainstation.repository.IChiTietHoaDonRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChiTietHoaDonRepositoryImpl implements IChiTietHoaDonRepository {
    private static final Logger LOG = Logger.getLogger(ChiTietHoaDonRepositoryImpl.class.getName());
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
    public ChiTietHoaDon findById(String maHoaDon, String maVe) {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            List<ChiTietHoaDon> result = em.createNativeQuery(
                            "SELECT maHoaDon, maVe, maLoaiVe, giaGoc, giaDaKM, moTa " +
                                    "FROM ChiTietHoaDon WHERE maHoaDon = ? AND maVe = ? AND isActive = 1",
                            ChiTietHoaDon.class
                    ).setParameter(1, maHoaDon)
                    .setParameter(2, maVe)
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
            LOG.log(Level.SEVERE, "Không thể thêm chi tiết hóa đơn", e);
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
            LOG.log(Level.SEVERE, "Không thể cập nhật chi tiết hóa đơn", e);
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
            LOG.log(Level.SEVERE, "Không thể xóa mềm chi tiết hóa đơn theo mã vé", e);
            return false;
        }
    }

    @Override
    public boolean deleteByHoaDonAndVe(String maHoaDon, String maVe) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            int updated = em.createNativeQuery(
                            "UPDATE ChiTietHoaDon SET isActive = 0 WHERE maHoaDon = ? AND maVe = ?"
                    )
                    .setParameter(1, maHoaDon)
                    .setParameter(2, maVe)
                    .executeUpdate();
            tx.commit();
            return updated > 0;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể xóa mềm chi tiết hóa đơn theo khóa ghép", e);
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
