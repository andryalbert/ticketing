package com.demo.ticketing.model;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@MappedSuperclass
public abstract class AbstractEntity<T> implements Serializable {

    @Id
    private T id;

    private LocalDateTime lastUpdate = LocalDateTime.now();

    private boolean deleted;

}
