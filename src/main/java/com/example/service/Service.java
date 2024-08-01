package com.example.service;

import com.example.exception.NotFoundException;

import java.util.List;

public interface Service<T, K> {
    T save(T t);

    void update(T t) throws NotFoundException;

    T findById(K k) throws NotFoundException;

    List<T> findAll();

    void delete(K k) throws NotFoundException;


}
