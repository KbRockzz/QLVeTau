package com.trainstation.repository;

import com.trainstation.model.ChiTietHoaDon;

import java.util.List;

public interface IChiTietHoaDonRepository {
    List<ChiTietHoaDon> getAll();
    ChiTietHoaDon findById(String id);
    ChiTietHoaDon findById(String maHoaDon, String maVe);
    boolean insert(ChiTietHoaDon entity);
    boolean update(ChiTietHoaDon entity);
    boolean delete(String id);
    boolean deleteByHoaDonAndVe(String maHoaDon, String maVe);
    List<ChiTietHoaDon> findByHoaDon(String maHoaDon);
}
