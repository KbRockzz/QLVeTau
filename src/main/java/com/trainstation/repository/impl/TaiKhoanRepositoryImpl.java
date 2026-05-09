package com.trainstation.repository.impl;

import com.trainstation.model.TaiKhoan;
import com.trainstation.persistence.JpaEntityManagerProvider;
import com.trainstation.repository.ITaiKhoanRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaiKhoanRepositoryImpl implements ITaiKhoanRepository {
    private static final Logger LOG = Logger.getLogger(TaiKhoanRepositoryImpl.class.getName());
    private static TaiKhoanRepositoryImpl instance;

    private TaiKhoanRepositoryImpl() {
    }

    public static synchronized TaiKhoanRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new TaiKhoanRepositoryImpl();
        }
        return instance;
    }

    @Override
    public List<TaiKhoan> getAll() {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            return em.createNativeQuery(
                    "SELECT maTK, maNV, tenTaiKhoan, matKhau, trangThai FROM TaiKhoan WHERE isActive = 1",
                    TaiKhoan.class
            ).getResultList();
        }
    }

    @Override
    public TaiKhoan findById(String id) {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            List<TaiKhoan> result = em.createNativeQuery(
                            "SELECT maTK, maNV, tenTaiKhoan, matKhau, trangThai FROM TaiKhoan WHERE maTK = ? AND isActive = 1",
                            TaiKhoan.class
                    )
                    .setParameter(1, id)
                    .getResultList();
            return result.isEmpty() ? null : result.get(0);
        }
    }

    @Override
    public TaiKhoan findByTenTaiKhoan(String tenTaiKhoan) {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            List<TaiKhoan> result = em.createNativeQuery(
                            "SELECT maTK, maNV, tenTaiKhoan, matKhau, trangThai FROM TaiKhoan " +
                                    "WHERE tenTaiKhoan = ? AND isActive = 1",
                            TaiKhoan.class
                    )
                    .setParameter(1, tenTaiKhoan)
                    .getResultList();
            return result.isEmpty() ? null : result.get(0);
        }
    }

    @Override
    public boolean insert(TaiKhoan entity) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            em.persist(entity);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể thêm tài khoản", e);
            return false;
        }
    }

    @Override
    public boolean update(TaiKhoan entity) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            em.merge(entity);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể cập nhật tài khoản", e);
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            int updated = em.createNativeQuery("UPDATE TaiKhoan SET isActive = 0 WHERE maTK = ?")
                    .setParameter(1, id)
                    .executeUpdate();
            tx.commit();
            return updated > 0;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể xóa mềm tài khoản", e);
            return false;
        }
    }

    @Override
    public String generateNextMaTK() {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            Number max = (Number) em.createNativeQuery(
                            "SELECT MAX(CAST(SUBSTRING(maTK, 3) AS UNSIGNED)) FROM TaiKhoan WHERE maTK REGEXP '^TK[0-9]+$'"
                    )
                    .getSingleResult();
            return String.format("TK%02d", max == null ? 1 : max.intValue() + 1);
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Không thể sinh mã tài khoản tiếp theo", e);
            return "TK" + (System.currentTimeMillis() % 100);
        }
    }
}
