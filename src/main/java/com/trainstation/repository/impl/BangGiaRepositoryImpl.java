package com.trainstation.repository.impl;

import com.trainstation.dao.BangGiaDAO;
import com.trainstation.model.BangGia;
import com.trainstation.repository.IBangGiaRepository;

import java.util.List;

public class BangGiaRepositoryImpl implements IBangGiaRepository {
    private static BangGiaRepositoryImpl instance;
    private final BangGiaDAO bangGiaDAO;

    private BangGiaRepositoryImpl() {
        this.bangGiaDAO = BangGiaDAO.getInstance();
    }

    public static synchronized BangGiaRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new BangGiaRepositoryImpl();
        }
        return instance;
    }

    @Override
    public List<BangGia> getAll() {
        return bangGiaDAO.getAll();
    }

    @Override
    public BangGia findById(String id) {
        return bangGiaDAO.findById(id);
    }

    @Override
    public boolean insert(BangGia entity) {
        return bangGiaDAO.insert(entity);
    }

    @Override
    public boolean update(BangGia entity) {
        return bangGiaDAO.update(entity);
    }

    @Override
    public boolean delete(String id) {
        return bangGiaDAO.delete(id);
    }
}
