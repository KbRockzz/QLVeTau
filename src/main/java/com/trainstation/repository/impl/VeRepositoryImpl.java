package com.trainstation.repository.impl;

import com.trainstation.MySQL.ConnectSql;
import com.trainstation.dao.VeDAO;
import com.trainstation.model.Ve;
import com.trainstation.repository.IVeRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class VeRepositoryImpl implements IVeRepository {
    private static final Logger LOG = Logger.getLogger(VeRepositoryImpl.class.getName());
    private static VeRepositoryImpl instance;
    private final VeDAO veDAO;

    private VeRepositoryImpl() {
        this.veDAO = VeDAO.getInstance();
    }

    public static synchronized VeRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new VeRepositoryImpl();
        }
        return instance;
    }

    @Override
    public List<Ve> getAll() {
        return veDAO.getAll();
    }

    @Override
    public Ve findById(String id) {
        return veDAO.findById(id);
    }

    @Override
    public List<Ve> findByKhachHang(String maKH) {
        return veDAO.getByKhachHang(maKH);
    }

    @Override
    public List<Ve> findByChuyen(String maChuyen) {
        try (Connection conn = ConnectSql.getInstance().getConnection()) {
            return veDAO.getByChuyen(conn, maChuyen);
        } catch (SQLException e) {
            LOG.severe("Không thể lấy danh sách vé cho chuyến " + maChuyen + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public boolean insert(Ve entity) {
        return veDAO.insert(entity);
    }

    @Override
    public boolean update(Ve entity) {
        return veDAO.update(entity);
    }

    @Override
    public boolean delete(String id) {
        return veDAO.delete(id);
    }
}
