package com.demo.ticketing.service;

import com.demo.ticketing.dto.TicketDto;
import com.demo.ticketing.model.Ticket;
import com.demo.ticketing.model.User;

import java.util.List;
import java.util.Optional;

public interface TicketService extends MapperService<Ticket, TicketDto> {
    Optional<Ticket> getTicketById(String id);

    Optional<Ticket> getTicketByIdForPisteAudit(String id);

    List<Ticket> getAllTicketsByUser(User user);

    Optional<Ticket> getTicketByUserAndId(User user, String id);

    Ticket saveTicket(Ticket ticket);

    void deleteTicket(Ticket ticket);
}
