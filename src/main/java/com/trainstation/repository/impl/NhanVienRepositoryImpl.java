package com.trainstation.repository.impl;

import com.trainstation.dao.NhanVienDAO;
import com.trainstation.model.NhanVien;
import com.trainstation.repository.INhanVienRepository;

import java.util.List;

public class NhanVienRepositoryImpl implements INhanVienRepository {
    private static NhanVienRepositoryImpl instance;
    private final NhanVienDAO nhanVienDAO;

    private NhanVienRepositoryImpl() {
        this.nhanVienDAO = NhanVienDAO.getInstance();
    }

    public static synchronized NhanVienRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new NhanVienRepositoryImpl();
        }
        return instance;
    }

    @Override
    public List<NhanVien> getAll() {
        return nhanVienDAO.getAll();
    }

    @Override
    public NhanVien findById(String id) {
        return nhanVienDAO.findById(id);
    }

    @Override
    public boolean insert(NhanVien entity) {
        return nhanVienDAO.insert(entity);
    }

    @Override
    public boolean update(NhanVien entity) {
        return nhanVienDAO.update(entity);
    }

    @Override
    public boolean delete(String id) {
        return nhanVienDAO.delete(id);
    }

    @Override
    public String generateNextMaNV() {
        return nhanVienDAO.generateNextMaNV();
    }
}
