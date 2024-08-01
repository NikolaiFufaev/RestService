package com.example.servlet.mapper;

import java.util.List;

public interface Mapper<T, K> {
    T mapFromDto(K dto);

    K mapToDto(T entity);

    List<K> mapToListDto(List<T> listEntity);

}
