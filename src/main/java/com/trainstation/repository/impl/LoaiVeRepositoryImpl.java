package com.trainstation.repository.impl;

import com.trainstation.model.LoaiVe;
import com.trainstation.persistence.JpaEntityManagerProvider;
import com.trainstation.repository.ILoaiVeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoaiVeRepositoryImpl implements ILoaiVeRepository {
    private static final Logger LOG = Logger.getLogger(LoaiVeRepositoryImpl.class.getName());
    private static LoaiVeRepositoryImpl instance;

    private LoaiVeRepositoryImpl() {
    }

    public static synchronized LoaiVeRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new LoaiVeRepositoryImpl();
        }
        return instance;
    }

    @Override
    public List<LoaiVe> getAll() {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            return em.createNativeQuery(
                    "SELECT maLoaiVe, tenLoai, heSoGia, moTa FROM LoaiVe WHERE isActive = 1",
                    LoaiVe.class
            ).getResultList();
        }
    }

    @Override
    public LoaiVe findById(String id) {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            List<LoaiVe> result = em.createNativeQuery(
                            "SELECT maLoaiVe, tenLoai, heSoGia, moTa FROM LoaiVe WHERE maLoaiVe = ? AND isActive = 1",
                            LoaiVe.class
                    )
                    .setParameter(1, id)
                    .getResultList();
            return result.isEmpty() ? null : result.get(0);
        }
    }

    @Override
    public boolean insert(LoaiVe entity) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            em.persist(entity);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể thêm loại vé", e);
            return false;
        }
    }

    @Override
    public boolean update(LoaiVe entity) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            em.merge(entity);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể cập nhật loại vé", e);
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            int updated = em.createNativeQuery("UPDATE LoaiVe SET isActive = 0 WHERE maLoaiVe = ?")
                    .setParameter(1, id)
                    .executeUpdate();
            tx.commit();
            return updated > 0;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể xóa loại vé", e);
            return false;
        }
    }
}
