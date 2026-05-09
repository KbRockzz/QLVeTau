package com.trainstation.repository;

import com.trainstation.model.LoaiGhe;

import java.util.List;

public interface ILoaiGheRepository {
    List<LoaiGhe> getAll();
    LoaiGhe findById(String id);
    boolean insert(LoaiGhe entity);
    boolean update(LoaiGhe entity);
    boolean delete(String id);
}
