package com.trainstation.repository;

import com.trainstation.model.NhanVien;

import java.util.List;

public interface INhanVienRepository {
    List<NhanVien> getAll();
    NhanVien findById(String id);
    boolean insert(NhanVien entity);
    boolean update(NhanVien entity);
    boolean delete(String id);
    String generateNextMaNV();
}
