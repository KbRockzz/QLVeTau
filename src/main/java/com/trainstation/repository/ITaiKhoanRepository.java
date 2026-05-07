package com.trainstation.repository;

import com.trainstation.model.TaiKhoan;

import java.util.List;

public interface ITaiKhoanRepository {
    List<TaiKhoan> getAll();
    TaiKhoan findById(String id);
    boolean insert(TaiKhoan entity);
    boolean update(TaiKhoan entity);
    boolean delete(String id);
}
