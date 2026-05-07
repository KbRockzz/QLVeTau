package com.trainstation.repository;

import com.trainstation.model.Ga;

import java.util.List;

public interface IGaRepository {
    List<Ga> getAll();
    Ga findById(String id);
    boolean insert(Ga entity);
    boolean update(Ga entity);
    boolean delete(String id);
    List<Ga> getDeletedStations();
    boolean restoreStation(String id);
}
