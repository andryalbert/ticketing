package com.demo.ticketing.service;

import com.demo.ticketing.model.Ticket;

import java.util.Optional;

public interface TicketService {
    Optional<Ticket> getTicketById(String id);
}
