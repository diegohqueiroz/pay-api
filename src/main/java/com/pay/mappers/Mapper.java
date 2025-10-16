package com.pay.mappers;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public interface Mapper<E, D> {
    
    D toDTO(E entity);
    
    E toEntity(D dto);
    
    default List<D> toDTOList(List<E> entities) {
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
                .map(this::toDTO)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    
    default List<E> toEntityList(List<D> dtos) {
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream()
                .map(this::toEntity)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}