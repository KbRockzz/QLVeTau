package com.trainstation.repository.impl;

import com.trainstation.model.NhanVien;
import com.trainstation.persistence.JpaEntityManagerProvider;
import com.trainstation.repository.INhanVienRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NhanVienRepositoryImpl implements INhanVienRepository {
    private static final Logger LOG = Logger.getLogger(NhanVienRepositoryImpl.class.getName());
    private static NhanVienRepositoryImpl instance;

    private NhanVienRepositoryImpl() {
    }

    public static synchronized NhanVienRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new NhanVienRepositoryImpl();
        }
        return instance;
    }

    @Override
    public List<NhanVien> getAll() {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            return em.createNativeQuery(
                    "SELECT maNV, tenNV, soDienThoai, diaChi, ngaySinh, maLoaiNV, trangThai FROM NhanVien WHERE isActive = 1",
                    NhanVien.class
            ).getResultList();
        }
    }

    @Override
    public NhanVien findById(String id) {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            List<NhanVien> result = em.createNativeQuery(
                            "SELECT maNV, tenNV, soDienThoai, diaChi, ngaySinh, maLoaiNV, trangThai FROM NhanVien " +
                                    "WHERE maNV = ? AND isActive = 1",
                            NhanVien.class
                    )
                    .setParameter(1, id)
                    .getResultList();
            return result.isEmpty() ? null : result.get(0);
        }
    }

    @Override
    public boolean insert(NhanVien entity) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            em.persist(entity);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể thêm nhân viên", e);
            return false;
        }
    }

    @Override
    public boolean update(NhanVien entity) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            em.merge(entity);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể cập nhật nhân viên", e);
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        EntityTransaction tx = null;
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            tx = em.getTransaction();
            tx.begin();
            int updated = em.createNativeQuery("UPDATE NhanVien SET isActive = 0 WHERE maNV = ?")
                    .setParameter(1, id)
                    .executeUpdate();
            tx.commit();
            return updated > 0;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            LOG.log(Level.SEVERE, "Không thể xóa mềm nhân viên", e);
            return false;
        }
    }

    @Override
    public String generateNextMaNV() {
        try (EntityManager em = JpaEntityManagerProvider.createEntityManager()) {
            Number max = (Number) em.createNativeQuery(
                            "SELECT MAX(CAST(SUBSTRING(maNV, 3) AS UNSIGNED)) FROM NhanVien WHERE maNV REGEXP '^NV[0-9]+$'"
                    )
                    .getSingleResult();
            return String.format("NV%02d", max == null ? 1 : max.intValue() + 1);
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Không thể sinh mã nhân viên tiếp theo", e);
            return "NV" + (System.currentTimeMillis() % 100);
        }
    }
}
