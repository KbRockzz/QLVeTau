package com.trainstation.repository;

import com.trainstation.model.DauMay;

import java.util.List;

public interface IDauMayRepository {
    List<DauMay> getAll();
    DauMay findById(String id);
    boolean insert(DauMay entity);
    boolean update(DauMay entity);
    boolean delete(String id);
    boolean dungHoatDongDauMay(String maDauMay);
    List<DauMay> layDauMayHoatDong();
    String generateNextMaDauMay();
}
