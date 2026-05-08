package com.trainstation.repository;

import com.trainstation.model.ChiTietChuyenTau;

import java.util.List;

public interface IChiTietChuyenTauRepository {
    List<ChiTietChuyenTau> getAll();
    List<ChiTietChuyenTau> findByChuyenTau(String maChuyenTau);
    ChiTietChuyenTau findById(String maChuyenTau, String maToaTau);
    boolean insert(ChiTietChuyenTau entity);
    boolean update(ChiTietChuyenTau entity);
    boolean delete(String maChuyenTau, String maToaTau);
}
