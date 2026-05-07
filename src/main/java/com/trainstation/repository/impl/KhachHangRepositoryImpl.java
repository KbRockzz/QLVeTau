package com.trainstation.repository.impl;

import com.trainstation.dao.KhachHangDAO;
import com.trainstation.model.KhachHang;
import com.trainstation.repository.IKhachHangRepository;

import java.util.List;

public class KhachHangRepositoryImpl implements IKhachHangRepository {
    private static KhachHangRepositoryImpl instance;
    private final KhachHangDAO khachHangDAO;

    private KhachHangRepositoryImpl() {
        this.khachHangDAO = KhachHangDAO.getInstance();
    }

    public static synchronized KhachHangRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new KhachHangRepositoryImpl();
        }
        return instance;
    }

    @Override
    public List<KhachHang> getAll() {
        return khachHangDAO.getAll();
    }

    @Override
    public KhachHang findById(String id) {
        return khachHangDAO.findById(id);
    }

    @Override
    public KhachHang findBySoDienThoai(String soDienThoai) {
        return khachHangDAO.timTheoSoDienThoai(soDienThoai);
    }

    @Override
    public boolean insert(KhachHang entity) {
        return khachHangDAO.insert(entity);
    }

    @Override
    public boolean update(KhachHang entity) {
        return khachHangDAO.update(entity);
    }

    @Override
    public boolean delete(String id) {
        return khachHangDAO.delete(id);
    }
}
