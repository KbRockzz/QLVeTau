package com.trainstation.repository;

import com.trainstation.model.ChiTietHoaDon;

import java.util.List;

public interface IChiTietHoaDonRepository {
    List<ChiTietHoaDon> getAll();
    ChiTietHoaDon findById(String id);
    boolean insert(ChiTietHoaDon entity);
    boolean update(ChiTietHoaDon entity);
    boolean delete(String id);
    List<ChiTietHoaDon> findByHoaDon(String maHoaDon);
}
