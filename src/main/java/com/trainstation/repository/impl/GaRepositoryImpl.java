package com.trainstation.repository.impl;

import com.trainstation.dao.GaDAO;
import com.trainstation.model.Ga;
import com.trainstation.repository.IGaRepository;

import java.util.List;

public class GaRepositoryImpl implements IGaRepository {
    private static GaRepositoryImpl instance;
    private final GaDAO gaDAO;

    private GaRepositoryImpl() {
        this.gaDAO = GaDAO.getInstance();
    }

    public static synchronized GaRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new GaRepositoryImpl();
        }
        return instance;
    }

    @Override
    public List<Ga> getAll() {
        return gaDAO.getAll();
    }

    @Override
    public Ga findById(String id) {
        return gaDAO.findById(id);
    }

    @Override
    public boolean insert(Ga entity) {
        return gaDAO.insert(entity);
    }

    @Override
    public boolean update(Ga entity) {
        return gaDAO.update(entity);
    }

    @Override
    public boolean delete(String id) {
        return gaDAO.delete(id);
    }

    @Override
    public List<Ga> getDeletedStations() {
        return gaDAO.getDeletedStations();
    }

    @Override
    public boolean restoreStation(String id) {
        return gaDAO.restoreStation(id);
    }

    @Override
    public String generateNextMaGa() {
        return gaDAO.generateNextMaGa();
    }
}
