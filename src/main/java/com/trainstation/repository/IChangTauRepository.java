package com.trainstation.repository;

import com.trainstation.model.ChangTau;

import java.util.List;

public interface IChangTauRepository {
    List<ChangTau> getAll();
    ChangTau findById(String id);
    boolean insert(ChangTau entity);
    boolean update(ChangTau entity);
    boolean delete(String id);
}
