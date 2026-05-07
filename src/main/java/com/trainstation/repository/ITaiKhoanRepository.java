package com.trainstation.repository;

import com.trainstation.model.TaiKhoan;

import java.util.List;

public interface ITaiKhoanRepository {
    List<TaiKhoan> getAll();
    TaiKhoan findById(String id);
    TaiKhoan findByTenTaiKhoan(String tenTaiKhoan);
    boolean insert(TaiKhoan entity);
    boolean update(TaiKhoan entity);
    boolean delete(String id);
    String generateNextMaTK();
}
