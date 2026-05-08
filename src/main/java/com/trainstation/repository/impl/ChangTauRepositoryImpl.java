package com.trainstation.repository.impl;

import com.trainstation.model.ChangTau;
import com.trainstation.persistence.JpaEntityManagerProvider;
import com.trainstation.repository.IChangTauRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChangTauRepositoryImpl implements IChangTauRepository {
    private static final Logger LOG = Logger.getLogger(ChangTauRepositoryImpl.class.getName());
    private static ChangTauRepositoryImpl instance;

    private ChangTauRepositoryImpl() {
    }

    public static synchronized ChangTauRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new ChangTauRepositoryImpl();
        }
        return instance;
    }

    @Override
    public List<ChangTau> getAll() {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            return em.createNativeQuery(
                    "SELECT maChang, soKMToiThieu, soKMToiDa, moTa, giaTien FROM ChangTau WHERE isActive = 1",
                    ChangTau.class
            ).getResultList();
        }
    }

    @Override
    public ChangTau findById(String id) {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            List<ChangTau> result = em.createNativeQuery(
                            "SELECT maChang, soKMToiThieu, soKMToiDa, moTa, giaTien FROM ChangTau WHERE maChang = ? AND isActive = 1",
                            ChangTau.class
                    )
                    .setParameter(1, id)
                    .getResultList();
            return result.isEmpty() ? null : result.get(0);
        }
    }

    @Override
    public boolean insert(ChangTau entity) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            em.persist(entity);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể thêm chặng tàu", e);
            return false;
        }
    }

    @Override
    public boolean update(ChangTau entity) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            em.merge(entity);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể cập nhật chặng tàu", e);
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            int updated = em.createNativeQuery("UPDATE ChangTau SET isActive = 0 WHERE maChang = ?")
                    .setParameter(1, id)
                    .executeUpdate();
            tx.commit();
            return updated > 0;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể xóa chặng tàu", e);
            return false;
        }
    }
}
