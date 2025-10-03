package com.trainstation.dao;

import java.util.List;

public interface GenericDAO<T> {
    void add(T entity);
    void update(T entity);
    void delete(String id);
    T findById(String id);
    List<T> findAll();
}
