package com.trainstation.repository.impl;

import com.trainstation.model.Ghe;
import com.trainstation.persistence.JpaEntityManagerProvider;
import com.trainstation.repository.IGheRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GheRepositoryImpl implements IGheRepository {
    private static final Logger LOG = Logger.getLogger(GheRepositoryImpl.class.getName());
    private static GheRepositoryImpl instance;

    private GheRepositoryImpl() {
    }

    public static synchronized GheRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new GheRepositoryImpl();
        }
        return instance;
    }

    @Override
    public List<Ghe> getAll() {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            return em.createNativeQuery(
                    "SELECT maGhe, maToa, loaiGhe, trangThai FROM Ghe ORDER BY maGhe",
                    Ghe.class
            ).getResultList();
        }
    }

    @Override
    public List<Ghe> findByToa(String maToa) {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            return em.createNativeQuery(
                            "SELECT maGhe, maToa, loaiGhe, trangThai FROM Ghe WHERE maToa = ? ORDER BY maGhe",
                            Ghe.class
                    )
                    .setParameter(1, maToa)
                    .getResultList();
        }
    }

    @Override
    public Ghe findById(String id) {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            List<Ghe> result = em.createNativeQuery(
                            "SELECT maGhe, maToa, loaiGhe, trangThai FROM Ghe WHERE maGhe = ?",
                            Ghe.class
                    )
                    .setParameter(1, id)
                    .getResultList();
            return result.isEmpty() ? null : result.get(0);
        }
    }

    @Override
    public boolean insert(Ghe entity) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            em.persist(entity);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể thêm ghế", e);
            return false;
        }
    }

    @Override
    public boolean update(Ghe entity) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            em.merge(entity);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể cập nhật ghế", e);
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            int updated = em.createNativeQuery("DELETE FROM Ghe WHERE maGhe = ?")
                    .setParameter(1, id)
                    .executeUpdate();
            tx.commit();
            return updated > 0;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể xóa ghế", e);
            return false;
        }
    }

    @Override
    public String generateNextMaGhe() {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            Number max = (Number) em.createNativeQuery(
                            "SELECT MAX(CAST(SUBSTRING(maGhe, 3) AS UNSIGNED)) FROM Ghe WHERE maGhe REGEXP '^GH[0-9]+$'"
                    )
                    .getSingleResult();
            return String.format("GH%03d", max == null ? 1 : max.intValue() + 1);
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Không thể sinh mã ghế tiếp theo", e);
            return "GH" + (System.currentTimeMillis() % 1000);
        }
    }
}
