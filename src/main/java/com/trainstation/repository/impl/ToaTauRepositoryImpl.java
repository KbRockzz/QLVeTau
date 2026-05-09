package com.trainstation.repository.impl;

import com.trainstation.model.ToaTau;
import com.trainstation.persistence.JpaEntityManagerProvider;
import com.trainstation.repository.IToaTauRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ToaTauRepositoryImpl implements IToaTauRepository {
    private static final Logger LOG = Logger.getLogger(ToaTauRepositoryImpl.class.getName());
    private static ToaTauRepositoryImpl instance;

    private ToaTauRepositoryImpl() {
    }

    public static synchronized ToaTauRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new ToaTauRepositoryImpl();
        }
        return instance;
    }

    @Override
    public List<ToaTau> getAll() {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            return em.createNativeQuery(
                    "SELECT maToa, loaiToa, samSX, trangThai, sucChua FROM ToaTau WHERE isActive = 1",
                    ToaTau.class
            ).getResultList();
        }
    }

    @Override
    public List<ToaTau> findByChuyenTau(String maChuyenTau) {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            return em.createNativeQuery(
                            "SELECT t.maToa, t.loaiToa, t.samSX, t.trangThai, t.sucChua " +
                                    "FROM ToaTau t " +
                                    "INNER JOIN ChiTietChuyenTau ct ON t.maToa = ct.maToaTau " +
                                    "WHERE ct.maChuyenTau = ? AND ct.isActive = 1 AND t.isActive = 1 " +
                                    "ORDER BY ct.soThuTuToa",
                            ToaTau.class
                    )
                    .setParameter(1, maChuyenTau)
                    .getResultList();
        }
    }

    @Override
    public ToaTau findById(String id) {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            List<ToaTau> result = em.createNativeQuery(
                            "SELECT maToa, loaiToa, samSX, trangThai, sucChua FROM ToaTau WHERE maToa = ? AND isActive = 1",
                            ToaTau.class
                    )
                    .setParameter(1, id)
                    .getResultList();
            return result.isEmpty() ? null : result.get(0);
        }
    }

    @Override
    public boolean insert(ToaTau entity) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            em.persist(entity);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể thêm toa tàu", e);
            return false;
        }
    }

    @Override
    public boolean update(ToaTau entity) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            em.merge(entity);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể cập nhật toa tàu", e);
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            int updated = em.createNativeQuery("UPDATE ToaTau SET isActive = 0 WHERE maToa = ?")
                    .setParameter(1, id)
                    .executeUpdate();
            tx.commit();
            return updated > 0;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể xóa toa tàu", e);
            return false;
        }
    }

    @Override
    public String generateNextMaToa() {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            Number max = (Number) em.createNativeQuery(
                            "SELECT MAX(CAST(SUBSTRING(maToa, 3) AS UNSIGNED)) FROM ToaTau WHERE maToa REGEXP '^TT[0-9]+$'"
                    )
                    .getSingleResult();
            return String.format("TT%03d", max == null ? 1 : max.intValue() + 1);
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Không thể sinh mã toa tàu tiếp theo", e);
            return "TT" + (System.currentTimeMillis() % 1000);
        }
    }
}
