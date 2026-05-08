package com.trainstation.repository.impl;

import com.trainstation.model.Ga;
import com.trainstation.persistence.JpaEntityManagerProvider;
import com.trainstation.repository.IGaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GaRepositoryImpl implements IGaRepository {
    private static final Logger LOG = Logger.getLogger(GaRepositoryImpl.class.getName());
    private static GaRepositoryImpl instance;

    private GaRepositoryImpl() {
    }

    public static synchronized GaRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new GaRepositoryImpl();
        }
        return instance;
    }

    @Override
    public List<Ga> getAll() {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            return em.createNativeQuery(
                    "SELECT maGa, tenGa, moTa, tinhTrang, diaChi FROM Ga WHERE isActive = 1",
                    Ga.class
            ).getResultList();
        }
    }

    @Override
    public Ga findById(String id) {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            List<Ga> result = em.createNativeQuery(
                            "SELECT maGa, tenGa, moTa, tinhTrang, diaChi FROM Ga WHERE maGa = ? AND isActive = 1",
                            Ga.class
                    )
                    .setParameter(1, id)
                    .getResultList();
            return result.isEmpty() ? null : result.get(0);
        }
    }

    @Override
    public boolean insert(Ga entity) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            em.persist(entity);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể thêm ga", e);
            return false;
        }
    }

    @Override
    public boolean update(Ga entity) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            em.merge(entity);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể cập nhật ga", e);
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            int updated = em.createNativeQuery("UPDATE Ga SET isActive = 0 WHERE maGa = ?")
                    .setParameter(1, id)
                    .executeUpdate();
            tx.commit();
            return updated > 0;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể xóa mềm ga", e);
            return false;
        }
    }

    @Override
    public List<Ga> getDeletedStations() {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            return em.createNativeQuery(
                    "SELECT maGa, tenGa, moTa, tinhTrang, diaChi FROM Ga WHERE isActive = 0",
                    Ga.class
            ).getResultList();
        }
    }

    @Override
    public boolean restoreStation(String id) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            int updated = em.createNativeQuery("UPDATE Ga SET isActive = 1 WHERE maGa = ?")
                    .setParameter(1, id)
                    .executeUpdate();
            tx.commit();
            return updated > 0;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể khôi phục ga", e);
            return false;
        }
    }

    @Override
    public String generateNextMaGa() {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            Number max = (Number) em.createNativeQuery(
                            "SELECT MAX(CAST(SUBSTRING(maGa, 3) AS UNSIGNED)) FROM Ga WHERE maGa REGEXP '^GA[0-9]+$'"
                    )
                    .getSingleResult();
            return String.format("GA%03d", max == null ? 1 : max.intValue() + 1);
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Không thể sinh mã ga tiếp theo", e);
            return "GA" + (System.currentTimeMillis() % 1000);
        }
    }
}
