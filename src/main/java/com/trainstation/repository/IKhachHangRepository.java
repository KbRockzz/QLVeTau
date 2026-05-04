package com.trainstation.repository;

import com.trainstation.model.KhachHang;
import java.util.List;

public interface IKhachHangRepository {
    List<KhachHang> getAll();
    KhachHang findById(String id);
    KhachHang findBySoDienThoai(String soDienThoai);
    boolean insert(KhachHang entity);
    boolean update(KhachHang entity);
    boolean delete(String id);
}
