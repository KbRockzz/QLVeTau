package com.trainstation.repository.impl;

import com.trainstation.dao.DauMayDAO;
import com.trainstation.model.DauMay;
import com.trainstation.repository.IDauMayRepository;

import java.util.List;

public class DauMayRepositoryImpl implements IDauMayRepository {
    private static DauMayRepositoryImpl instance;
    private final DauMayDAO dauMayDAO;

    private DauMayRepositoryImpl() {
        this.dauMayDAO = DauMayDAO.getInstance();
    }

    public static synchronized DauMayRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new DauMayRepositoryImpl();
        }
        return instance;
    }

    @Override
    public List<DauMay> getAll() {
        return dauMayDAO.getAll();
    }

    @Override
    public DauMay findById(String id) {
        return dauMayDAO.findById(id);
    }

    @Override
    public boolean insert(DauMay entity) {
        return dauMayDAO.insert(entity);
    }

    @Override
    public boolean update(DauMay entity) {
        return dauMayDAO.update(entity);
    }

    @Override
    public boolean delete(String id) {
        return dauMayDAO.delete(id);
    }

    @Override
    public boolean dungHoatDongDauMay(String maDauMay) {
        return dauMayDAO.dungHoatDongDauMay(maDauMay);
    }

    @Override
    public List<DauMay> layDauMayHoatDong() {
        return dauMayDAO.layDauMayHoatDong();
    }

    @Override
    public String generateNextMaDauMay() {
        return dauMayDAO.generateNextMaDauMay();
    }
}
