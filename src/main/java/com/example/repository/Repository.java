package com.example.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T, K> {
    T save(T t);

    void update(T t);

    void deleteById(K id);

    Optional<T> findById(K id);

    List<T> findAll();

}
