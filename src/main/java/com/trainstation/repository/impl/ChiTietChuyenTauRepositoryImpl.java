package com.trainstation.repository.impl;

import com.trainstation.model.ChiTietChuyenTau;
import com.trainstation.persistence.JpaEntityManagerProvider;
import com.trainstation.repository.IChiTietChuyenTauRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChiTietChuyenTauRepositoryImpl implements IChiTietChuyenTauRepository {
    private static final Logger LOG = Logger.getLogger(ChiTietChuyenTauRepositoryImpl.class.getName());
    private static ChiTietChuyenTauRepositoryImpl instance;

    private ChiTietChuyenTauRepositoryImpl() {
    }

    public static synchronized ChiTietChuyenTauRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new ChiTietChuyenTauRepositoryImpl();
        }
        return instance;
    }

    @Override
    public List<ChiTietChuyenTau> getAll() {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            return em.createNativeQuery(
                    "SELECT maChuyenTau, maToaTau, soThuTuToa, sucChua FROM ChiTietChuyenTau WHERE isActive = 1",
                    ChiTietChuyenTau.class
            ).getResultList();
        }
    }

    @Override
    public List<ChiTietChuyenTau> findByChuyenTau(String maChuyenTau) {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            return em.createNativeQuery(
                            "SELECT maChuyenTau, maToaTau, soThuTuToa, sucChua FROM ChiTietChuyenTau " +
                                    "WHERE maChuyenTau = ? AND isActive = 1 ORDER BY soThuTuToa",
                            ChiTietChuyenTau.class
                    )
                    .setParameter(1, maChuyenTau)
                    .getResultList();
        }
    }

    @Override
    public ChiTietChuyenTau findById(String maChuyenTau, String maToaTau) {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            List<ChiTietChuyenTau> result = em.createNativeQuery(
                            "SELECT maChuyenTau, maToaTau, soThuTuToa, sucChua FROM ChiTietChuyenTau " +
                                    "WHERE maChuyenTau = ? AND maToaTau = ? AND isActive = 1",
                            ChiTietChuyenTau.class
                    )
                    .setParameter(1, maChuyenTau)
                    .setParameter(2, maToaTau)
                    .getResultList();
            return result.isEmpty() ? null : result.get(0);
        }
    }

    @Override
    public boolean insert(ChiTietChuyenTau entity) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            em.persist(entity);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể thêm chi tiết chuyến tàu", e);
            return false;
        }
    }

    @Override
    public boolean update(ChiTietChuyenTau entity) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            em.merge(entity);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể cập nhật chi tiết chuyến tàu", e);
            return false;
        }
    }

    @Override
    public boolean delete(String maChuyenTau, String maToaTau) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            int updated = em.createNativeQuery(
                            "UPDATE ChiTietChuyenTau SET isActive = 0 WHERE maChuyenTau = ? AND maToaTau = ?"
                    )
                    .setParameter(1, maChuyenTau)
                    .setParameter(2, maToaTau)
                    .executeUpdate();
            tx.commit();
            return updated > 0;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể xóa chi tiết chuyến tàu", e);
            return false;
        }
    }
}
