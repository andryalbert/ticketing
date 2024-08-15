package com.demo.ticketing.service.Impl;

import com.demo.ticketing.model.Ticket;
import com.demo.ticketing.model.User;
import com.demo.ticketing.repository.TicketRepository;
import com.demo.ticketing.service.TicketService;
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
        return ticketRepository.findById(id);
    }

    @Override
    public List<Ticket> getAllTicketsByUser(User user) {
        log.info("user {}",user);
        return ticketRepository.findByUser(user);
    }


}
