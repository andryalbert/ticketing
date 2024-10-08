package com.demo.ticketing.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tickets")
@Data
public class Ticket extends AbstractEntity {

    @NotBlank(message = "le titre ne peut pas être vide")
    private String title;

    @NotBlank(message = "la description ne peut pas être vide")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Enumerated(EnumType.STRING)
    private TicketStatus ticketStatus;

}
