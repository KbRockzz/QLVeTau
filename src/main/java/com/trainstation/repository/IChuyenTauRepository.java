package com.trainstation.repository;

import com.trainstation.model.ChuyenTau;
import java.util.List;

public interface IChuyenTauRepository {
    List<ChuyenTau> getAll();
    ChuyenTau findById(String id);
    List<ChuyenTau> search(String keyword);
    boolean insert(ChuyenTau entity);
    boolean update(ChuyenTau entity);
    boolean delete(String id);
}
