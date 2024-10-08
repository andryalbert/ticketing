package com.demo.ticketing.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "pisteAudits")
@Data
public class PisteAudit extends AbstractEntity{

    @Enumerated(EnumType.STRING)
    private Action action;

    @Column(nullable = false)
    private String userConcerned;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Ticket ticket;

}
