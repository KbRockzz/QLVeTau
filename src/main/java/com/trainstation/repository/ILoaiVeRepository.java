package com.trainstation.repository;

import com.trainstation.model.LoaiVe;

import java.util.List;

public interface ILoaiVeRepository {
    List<LoaiVe> getAll();
    LoaiVe findById(String id);
    boolean insert(LoaiVe entity);
    boolean update(LoaiVe entity);
    boolean delete(String id);
}
