package com.trainstation.repository.impl;

import com.trainstation.model.DauMay;
import com.trainstation.persistence.JpaEntityManagerProvider;
import com.trainstation.repository.IDauMayRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DauMayRepositoryImpl implements IDauMayRepository {
    private static final Logger LOG = Logger.getLogger(DauMayRepositoryImpl.class.getName());
    private static DauMayRepositoryImpl instance;

    private DauMayRepositoryImpl() {
    }

    public static synchronized DauMayRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new DauMayRepositoryImpl();
        }
        return instance;
    }

    @Override
    public List<DauMay> getAll() {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            return em.createNativeQuery(
                    "SELECT maDauMay, loaiDauMay, tenDauMay, namSX, lanBaoTriGanNhat, trangThai FROM DauMay WHERE isActive = 1",
                    DauMay.class
            ).getResultList();
        }
    }

    @Override
    public DauMay findById(String id) {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            List<DauMay> result = em.createNativeQuery(
                            "SELECT maDauMay, loaiDauMay, tenDauMay, namSX, lanBaoTriGanNhat, trangThai " +
                                    "FROM DauMay WHERE maDauMay = ? AND isActive = 1",
                            DauMay.class
                    )
                    .setParameter(1, id)
                    .getResultList();
            return result.isEmpty() ? null : result.get(0);
        }
    }

    @Override
    public boolean insert(DauMay entity) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            em.persist(entity);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể thêm đầu máy", e);
            return false;
        }
    }

    @Override
    public boolean update(DauMay entity) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            em.merge(entity);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể cập nhật đầu máy", e);
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            int updated = em.createNativeQuery("UPDATE DauMay SET isActive = 0 WHERE maDauMay = ?")
                    .setParameter(1, id)
                    .executeUpdate();
            tx.commit();
            return updated > 0;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể xóa mềm đầu máy", e);
            return false;
        }
    }

    @Override
    public boolean dungHoatDongDauMay(String maDauMay) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            int updated = em.createNativeQuery(
                            "UPDATE DauMay SET trangThai = 'Dừng hoạt động' WHERE maDauMay = ? AND isActive = 1"
                    )
                    .setParameter(1, maDauMay)
                    .executeUpdate();
            tx.commit();
            return updated > 0;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể dừng hoạt động đầu máy", e);
            return false;
        }
    }

    @Override
    public List<DauMay> layDauMayHoatDong() {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            return em.createNativeQuery(
                            "SELECT maDauMay, loaiDauMay, tenDauMay, namSX, lanBaoTriGanNhat, trangThai " +
                                    "FROM DauMay WHERE isActive = 1 AND trangThai = 'Hoạt động'",
                            DauMay.class
                    )
                    .getResultList();
        }
    }

    @Override
    public String generateNextMaDauMay() {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            Number max = (Number) em.createNativeQuery(
                            "SELECT MAX(CAST(SUBSTRING(maDauMay, 3) AS UNSIGNED)) FROM DauMay WHERE maDauMay REGEXP '^DM[0-9]+$'"
                    )
                    .getSingleResult();
            return String.format("DM%03d", max == null ? 1 : max.intValue() + 1);
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Không thể sinh mã đầu máy tiếp theo", e);
            return "DM" + (System.currentTimeMillis() % 1000);
        }
    }
}
