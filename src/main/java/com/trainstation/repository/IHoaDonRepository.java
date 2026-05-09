package com.trainstation.repository;

import com.trainstation.model.HoaDon;

import java.util.List;

public interface IHoaDonRepository {
    List<HoaDon> getAll();
    HoaDon findById(String id);
    boolean insert(HoaDon entity);
    boolean update(HoaDon entity);
    boolean delete(String id);
}
