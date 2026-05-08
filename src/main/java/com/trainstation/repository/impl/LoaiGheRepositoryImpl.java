package com.trainstation.repository.impl;

import com.trainstation.model.LoaiGhe;
import com.trainstation.persistence.JpaEntityManagerProvider;
import com.trainstation.repository.ILoaiGheRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoaiGheRepositoryImpl implements ILoaiGheRepository {
    private static final Logger LOG = Logger.getLogger(LoaiGheRepositoryImpl.class.getName());
    private static LoaiGheRepositoryImpl instance;

    private LoaiGheRepositoryImpl() {
    }

    public static synchronized LoaiGheRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new LoaiGheRepositoryImpl();
        }
        return instance;
    }

    @Override
    public List<LoaiGhe> getAll() {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            return em.createNativeQuery(
                    "SELECT maLoai, tenLoai, moTa FROM LoaiGhe WHERE isActive = 1",
                    LoaiGhe.class
            ).getResultList();
        }
    }

    @Override
    public LoaiGhe findById(String id) {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            List<LoaiGhe> result = em.createNativeQuery(
                            "SELECT maLoai, tenLoai, moTa FROM LoaiGhe WHERE maLoai = ? AND isActive = 1",
                            LoaiGhe.class
                    )
                    .setParameter(1, id)
                    .getResultList();
            return result.isEmpty() ? null : result.get(0);
        }
    }

    @Override
    public boolean insert(LoaiGhe entity) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            em.persist(entity);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể thêm loại ghế", e);
            return false;
        }
    }

    @Override
    public boolean update(LoaiGhe entity) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            em.merge(entity);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể cập nhật loại ghế", e);
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            int updated = em.createNativeQuery("UPDATE LoaiGhe SET isActive = 0 WHERE maLoai = ?")
                    .setParameter(1, id)
                    .executeUpdate();
            tx.commit();
            return updated > 0;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể xóa loại ghế", e);
            return false;
        }
    }
}
