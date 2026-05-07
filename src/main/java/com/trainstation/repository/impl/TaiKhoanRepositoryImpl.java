package com.trainstation.repository.impl;

import com.trainstation.dao.TaiKhoanDAO;
import com.trainstation.model.TaiKhoan;
import com.trainstation.repository.ITaiKhoanRepository;

import java.util.List;

public class TaiKhoanRepositoryImpl implements ITaiKhoanRepository {
    private static TaiKhoanRepositoryImpl instance;
    private final TaiKhoanDAO taiKhoanDAO;

    private TaiKhoanRepositoryImpl() {
        this.taiKhoanDAO = TaiKhoanDAO.getInstance();
    }

    public static synchronized TaiKhoanRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new TaiKhoanRepositoryImpl();
        }
        return instance;
    }

    @Override
    public List<TaiKhoan> getAll() {
        return taiKhoanDAO.getAll();
    }

    @Override
    public TaiKhoan findById(String id) {
        return taiKhoanDAO.findById(id);
    }

    @Override
    public boolean insert(TaiKhoan entity) {
        return taiKhoanDAO.insert(entity);
    }

    @Override
    public boolean update(TaiKhoan entity) {
        return taiKhoanDAO.update(entity);
    }

    @Override
    public boolean delete(String id) {
        return taiKhoanDAO.delete(id);
    }
}
