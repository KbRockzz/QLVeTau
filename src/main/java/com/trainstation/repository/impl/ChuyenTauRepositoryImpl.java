package com.trainstation.repository.impl;

import com.trainstation.model.ChuyenTau;
import com.trainstation.persistence.JpaEntityManagerProvider;
import com.trainstation.repository.IChuyenTauRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.stream.Collectors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChuyenTauRepositoryImpl implements IChuyenTauRepository {
    private static final Logger LOG = Logger.getLogger(ChuyenTauRepositoryImpl.class.getName());
    private static ChuyenTauRepositoryImpl instance;

    private ChuyenTauRepositoryImpl() {
    }

    public static synchronized ChuyenTauRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new ChuyenTauRepositoryImpl();
        }
        return instance;
    }

    @Override
    public List<ChuyenTau> getAll() {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            return em.createNativeQuery(
                    "SELECT maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang, trangThai FROM ChuyenTau WHERE isActive = 1",
                    ChuyenTau.class
            ).getResultList();
        }
    }

    @Override
    public ChuyenTau findById(String id) {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            List<ChuyenTau> result = em.createNativeQuery(
                            "SELECT maChuyen, maDauMay, maNV, maGaDi, maGaDen, gioDi, gioDen, soKm, maChang, trangThai " +
                                    "FROM ChuyenTau WHERE maChuyen = ? AND isActive = 1",
                            ChuyenTau.class
                    )
                    .setParameter(1, id)
                    .getResultList();
            return result.isEmpty() ? null : result.get(0);
        }
    }

    @Override
    public List<ChuyenTau> search(String keyword) {
        List<ChuyenTau> all = getAll();
        if (keyword == null || keyword.trim().isEmpty()) {
            return all;
        }
        String lower = keyword.toLowerCase().trim();
        return all.stream()
                .filter(ct -> (ct.getMaChuyen() != null && ct.getMaChuyen().toLowerCase().contains(lower))
                        || (ct.getMaGaDi() != null && ct.getMaGaDi().toLowerCase().contains(lower))
                        || (ct.getMaGaDen() != null && ct.getMaGaDen().toLowerCase().contains(lower)))
                .collect(Collectors.toList());
    }

    @Override
    public boolean insert(ChuyenTau entity) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            em.persist(entity);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể thêm chuyến tàu", e);
            return false;
        }
    }

    @Override
    public boolean update(ChuyenTau entity) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            em.merge(entity);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể cập nhật chuyến tàu", e);
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            int deleted = em.createNativeQuery("DELETE FROM ChuyenTau WHERE maChuyen = ?")
                    .setParameter(1, id)
                    .executeUpdate();
            tx.commit();
            return deleted > 0;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể xóa chuyến tàu", e);
            return false;
        }
    }
}
