package com.trainstation.repository;

import com.trainstation.model.Ve;
import java.util.List;

public interface IVeRepository {
    List<Ve> getAll();
    Ve findById(String id);
    List<Ve> findByKhachHang(String maKH);
    List<Ve> findByChuyen(String maChuyen);
    boolean insert(Ve entity);
    boolean update(Ve entity);
    boolean delete(String id);
}
