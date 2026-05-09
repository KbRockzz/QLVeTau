package com.trainstation.repository;

import com.trainstation.model.ToaTau;

import java.util.List;

public interface IToaTauRepository {
    List<ToaTau> getAll();
    List<ToaTau> findByChuyenTau(String maChuyenTau);
    ToaTau findById(String id);
    boolean insert(ToaTau entity);
    boolean update(ToaTau entity);
    boolean delete(String id);
    String generateNextMaToa();
}
