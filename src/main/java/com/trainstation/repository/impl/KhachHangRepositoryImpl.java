package com.trainstation.repository.impl;

import com.trainstation.model.KhachHang;
import com.trainstation.persistence.JpaEntityManagerProvider;
import com.trainstation.repository.IKhachHangRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KhachHangRepositoryImpl implements IKhachHangRepository {
    private static final Logger LOG = Logger.getLogger(KhachHangRepositoryImpl.class.getName());
    private static KhachHangRepositoryImpl instance;

    private KhachHangRepositoryImpl() {
    }

    public static synchronized KhachHangRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new KhachHangRepositoryImpl();
        }
        return instance;
    }

    @Override
    public List<KhachHang> getAll() {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            return em.createNativeQuery(
                    "SELECT maKhachHang, tenKhachHang, email, soDienThoai FROM KhachHang WHERE isActive = 1",
                    KhachHang.class
            ).getResultList();
        }
    }

    @Override
    public KhachHang findById(String id) {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            List<KhachHang> result = em.createNativeQuery(
                            "SELECT maKhachHang, tenKhachHang, email, soDienThoai FROM KhachHang " +
                                    "WHERE maKhachHang = ? AND isActive = 1",
                            KhachHang.class
                    )
                    .setParameter(1, id)
                    .getResultList();
            return result.isEmpty() ? null : result.get(0);
        }
    }

    @Override
    public KhachHang findBySoDienThoai(String soDienThoai) {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            List<KhachHang> result = em.createNativeQuery(
                            "SELECT maKhachHang, tenKhachHang, email, soDienThoai FROM KhachHang " +
                                    "WHERE soDienThoai = ? AND isActive = 1",
                            KhachHang.class
                    )
                    .setParameter(1, soDienThoai)
                    .getResultList();
            return result.isEmpty() ? null : result.get(0);
        }
    }

    @Override
    public String generateNextMaKhachHang() {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            Number max = (Number) em.createNativeQuery(
                            "SELECT MAX(CAST(SUBSTRING(maKhachHang, 3) AS UNSIGNED)) FROM KhachHang " +
                                    "WHERE maKhachHang REGEXP '^KH[0-9]+$'"
                    )
                    .getSingleResult();
            return String.format("KH%02d", max == null ? 1 : max.intValue() + 1);
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Không thể sinh mã khách hàng tiếp theo", e);
            return "KH" + System.currentTimeMillis();
        }
    }

    @Override
    public boolean insert(KhachHang entity) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            em.persist(entity);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể thêm khách hàng", e);
            return false;
        }
    }

    @Override
    public boolean update(KhachHang entity) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            em.merge(entity);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể cập nhật khách hàng", e);
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            int updated = em.createNativeQuery("UPDATE KhachHang SET isActive = 0 WHERE maKhachHang = ?")
                    .setParameter(1, id)
                    .executeUpdate();
            tx.commit();
            return updated > 0;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể xóa mềm khách hàng", e);
            return false;
        }
    }
}
