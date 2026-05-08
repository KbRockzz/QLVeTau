package com.trainstation.repository.impl;

import com.trainstation.model.LoaiNV;
import com.trainstation.persistence.JpaEntityManagerProvider;
import com.trainstation.repository.ILoaiNVRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoaiNVRepositoryImpl implements ILoaiNVRepository {
    private static final Logger LOG = Logger.getLogger(LoaiNVRepositoryImpl.class.getName());
    private static LoaiNVRepositoryImpl instance;

    private LoaiNVRepositoryImpl() {
    }

    public static synchronized LoaiNVRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new LoaiNVRepositoryImpl();
        }
        return instance;
    }

    @Override
    public List<LoaiNV> getAll() {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            return em.createNativeQuery(
                    "SELECT maLoai, tenLoai, moTa FROM LoaiNV WHERE isActive = 1",
                    LoaiNV.class
            ).getResultList();
        }
    }

    @Override
    public LoaiNV findById(String id) {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            List<LoaiNV> result = em.createNativeQuery(
                            "SELECT maLoai, tenLoai, moTa FROM LoaiNV WHERE maLoai = ? AND isActive = 1",
                            LoaiNV.class
                    )
                    .setParameter(1, id)
                    .getResultList();
            return result.isEmpty() ? null : result.get(0);
        }
    }

    @Override
    public boolean insert(LoaiNV entity) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            em.persist(entity);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể thêm loại nhân viên", e);
            return false;
        }
    }

    @Override
    public boolean update(LoaiNV entity) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            em.merge(entity);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể cập nhật loại nhân viên", e);
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            int updated = em.createNativeQuery("UPDATE LoaiNV SET isActive = 0 WHERE maLoai = ?")
                    .setParameter(1, id)
                    .executeUpdate();
            tx.commit();
            return updated > 0;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể xóa loại nhân viên", e);
            return false;
        }
    }
}
