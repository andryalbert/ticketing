package com.demo.ticketing.service;

public interface MapperService <E,D>{
    E mapToEntity(D dto);
    D mapToDto(E entity);
}
