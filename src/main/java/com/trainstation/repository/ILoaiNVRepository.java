package com.trainstation.repository;

import com.trainstation.model.LoaiNV;

import java.util.List;

public interface ILoaiNVRepository {
    List<LoaiNV> getAll();
    LoaiNV findById(String id);
    boolean insert(LoaiNV entity);
    boolean update(LoaiNV entity);
    boolean delete(String id);
}
