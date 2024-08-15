package com.demo.ticketing.service.Impl;

import com.demo.ticketing.model.Ticket;
import com.demo.ticketing.model.User;
import com.demo.ticketing.repository.TicketRepository;
import com.demo.ticketing.service.TicketService;
import com.demo.ticketing.utils.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

    @Override
    public Optional<Ticket> getTicketById(String id) {
        log.info("ticket id {}",id);
        return ticketRepository.findByIdAndDeleted(id,false);
    }

    @Override
    public List<Ticket> getAllTicketsByUser(User user) {
        log.info("user {}",user);
        return ticketRepository.findByUserAndDeleted(user,false);
    }

    @Override
    public Optional<Ticket> getTicketByUserById(User user, String id) {
        log.info("user {}",user);
        log.info("ticket id {}",id);
        return ticketRepository.findByUserAndIdAndDeleted(user,id,false);
    }

    @Override
    public Ticket saveTicket(Ticket ticket) {
        ticket.setId(IdGenerator.uuid());
        log.info("ticket {}",ticket);
        return ticketRepository.save(ticket);
    }

    @Override
    public void deleteTicket(Ticket ticket) {
        log.info("ticket {}",ticket);
        // do a soft delete
        ticket.setDeleted(true);
        ticketRepository.save(ticket);
    }


}
