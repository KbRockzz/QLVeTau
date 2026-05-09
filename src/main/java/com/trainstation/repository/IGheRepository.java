package com.trainstation.repository;

import com.trainstation.model.Ghe;

import java.util.List;

public interface IGheRepository {
    List<Ghe> getAll();
    List<Ghe> findByToa(String maToa);
    Ghe findById(String id);
    boolean insert(Ghe entity);
    boolean update(Ghe entity);
    boolean delete(String id);
    String generateNextMaGhe();
}
