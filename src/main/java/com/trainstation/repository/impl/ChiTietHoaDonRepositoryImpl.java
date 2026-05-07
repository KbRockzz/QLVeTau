package com.trainstation.repository.impl;

import com.trainstation.dao.ChiTietHoaDonDAO;
import com.trainstation.model.ChiTietHoaDon;
import com.trainstation.repository.IChiTietHoaDonRepository;

import java.util.List;

public class ChiTietHoaDonRepositoryImpl implements IChiTietHoaDonRepository {
    private static ChiTietHoaDonRepositoryImpl instance;
    private final ChiTietHoaDonDAO chiTietHoaDonDAO;

    private ChiTietHoaDonRepositoryImpl() {
        this.chiTietHoaDonDAO = ChiTietHoaDonDAO.getInstance();
    }

    public static synchronized ChiTietHoaDonRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new ChiTietHoaDonRepositoryImpl();
        }
        return instance;
    }

    @Override
    public List<ChiTietHoaDon> getAll() {
        return chiTietHoaDonDAO.getAll();
    }

    @Override
    public ChiTietHoaDon findById(String id) {
        return chiTietHoaDonDAO.findById(id);
    }

    @Override
    public boolean insert(ChiTietHoaDon entity) {
        return chiTietHoaDonDAO.insert(entity);
    }

    @Override
    public boolean update(ChiTietHoaDon entity) {
        return chiTietHoaDonDAO.update(entity);
    }

    @Override
    public boolean delete(String id) {
        return chiTietHoaDonDAO.delete(id);
    }

    @Override
    public List<ChiTietHoaDon> findByHoaDon(String maHoaDon) {
        return chiTietHoaDonDAO.findByHoaDon(maHoaDon);
    }
}
