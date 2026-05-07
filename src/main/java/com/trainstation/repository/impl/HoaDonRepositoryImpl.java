package com.trainstation.repository.impl;

import com.trainstation.dao.HoaDonDAO;
import com.trainstation.model.HoaDon;
import com.trainstation.repository.IHoaDonRepository;

import java.util.List;

public class HoaDonRepositoryImpl implements IHoaDonRepository {
    private static HoaDonRepositoryImpl instance;
    private final HoaDonDAO hoaDonDAO;

    private HoaDonRepositoryImpl() {
        this.hoaDonDAO = HoaDonDAO.getInstance();
    }

    public static synchronized HoaDonRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new HoaDonRepositoryImpl();
        }
        return instance;
    }

    @Override
    public List<HoaDon> getAll() {
        return hoaDonDAO.getAll();
    }

    @Override
    public HoaDon findById(String id) {
        return hoaDonDAO.findById(id);
    }

    @Override
    public boolean insert(HoaDon entity) {
        return hoaDonDAO.insert(entity);
    }

    @Override
    public boolean update(HoaDon entity) {
        return hoaDonDAO.update(entity);
    }

    @Override
    public boolean delete(String id) {
        return hoaDonDAO.delete(id);
    }
}
