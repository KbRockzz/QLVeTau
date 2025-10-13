package com.trainstation.dao;

import java.util.List;

public interface GenericDAO<T> {
    List<T> getAll();
    T findById(String id);
    boolean insert(T entity);
    boolean update(T entity);
    boolean delete(String id);
}
