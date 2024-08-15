package com.demo.ticketing.service;

import com.demo.ticketing.model.Ticket;
import com.demo.ticketing.model.User;

import java.util.List;
import java.util.Optional;

public interface TicketService {
    Optional<Ticket> getTicketById(String id);

    List<Ticket> getAllTicketsByUser(User user);

    Optional<Ticket> getTicketByUserById(User user, String id);

    Ticket saveTicket(Ticket ticket);

    void deleteTicket(Ticket ticket);
}
