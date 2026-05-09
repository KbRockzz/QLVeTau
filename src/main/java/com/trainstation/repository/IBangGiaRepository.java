package com.trainstation.repository;

import com.trainstation.model.BangGia;

import java.util.List;

public interface IBangGiaRepository {
    List<BangGia> getAll();
    BangGia findById(String id);
    boolean insert(BangGia entity);
    boolean update(BangGia entity);
    boolean delete(String id);
}
