package com.trainstation.repository.impl;

import com.trainstation.model.BangGia;
import com.trainstation.persistence.JpaEntityManagerProvider;
import com.trainstation.repository.IBangGiaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BangGiaRepositoryImpl implements IBangGiaRepository {
    private static final Logger LOG = Logger.getLogger(BangGiaRepositoryImpl.class.getName());
    private static BangGiaRepositoryImpl instance;

    private BangGiaRepositoryImpl() {
    }

    public static synchronized BangGiaRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new BangGiaRepositoryImpl();
        }
        return instance;
    }

    @Override
    public List<BangGia> getAll() {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            return em.createNativeQuery(
                    "SELECT maBangGia, maChang, loaiGhe, giaCoBan, ngayBatDau, ngayKetThuc FROM BangGia WHERE isActive = 1",
                    BangGia.class
            ).getResultList();
        }
    }

    @Override
    public BangGia findById(String id) {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            List<BangGia> result = em.createNativeQuery(
                            "SELECT maBangGia, maChang, loaiGhe, giaCoBan, ngayBatDau, ngayKetThuc " +
                                    "FROM BangGia WHERE maBangGia = ? AND isActive = 1",
                            BangGia.class
                    ).setParameter(1, id)
                    .getResultList();
            return result.isEmpty() ? null : result.get(0);
        }
    }

    @Override
    public String generateNextMaBangGia() {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            Number max = (Number) em.createNativeQuery(
                            "SELECT MAX(CAST(SUBSTRING(maBangGia, 3) AS UNSIGNED)) FROM BangGia " +
                                    "WHERE maBangGia REGEXP '^BG[0-9]+$'"
                    )
                    .getSingleResult();
            return String.format("BG%03d", max == null ? 1 : max.intValue() + 1);
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Không thể sinh mã bảng giá tiếp theo", e);
            throw new IllegalStateException("Không thể sinh mã bảng giá tiếp theo", e);
        }
    }

    @Override
    public boolean insert(BangGia entity) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            em.persist(entity);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể thêm bảng giá", e);
            return false;
        }
    }

    @Override
    public boolean update(BangGia entity) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            em.merge(entity);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể cập nhật bảng giá", e);
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            int updated = em.createNativeQuery("UPDATE BangGia SET isActive = 0 WHERE maBangGia = ?")
                    .setParameter(1, id)
                    .executeUpdate();
            tx.commit();
            return updated > 0;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể xóa mềm bảng giá", e);
            return false;
        }
    }
}
